package br.com.orionsoft.financeiro.documento.cobranca.processes;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;

import br.com.orionsoft.basic.entities.Contrato;
import br.com.orionsoft.basic.entities.endereco.Municipio;
import br.com.orionsoft.basic.entities.pessoa.EscritorioContabil;
import br.com.orionsoft.basic.entities.pessoa.Pessoa;
import br.com.orionsoft.basic.etiquetas.InserirEtiquetaEnderecoService;
import br.com.orionsoft.financeiro.documento.cobranca.ConvenioCobranca;
import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobranca;
import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobrancaBean;
import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobrancaCategoria;
import br.com.orionsoft.financeiro.documento.cobranca.services.CalcularVencimentoService;
import br.com.orionsoft.financeiro.documento.cobranca.services.ImprimirDocumentosCobrancaService;
import br.com.orionsoft.financeiro.documento.cobranca.services.ListarDocumentosCobrancaService;
import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
import br.com.orionsoft.monstrengo.auditorship.services.UtilsAuditorship;
import br.com.orionsoft.monstrengo.core.annotations.ProcessMetadata;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.process.IRunnableEntityCollectionProcess;
import br.com.orionsoft.monstrengo.core.process.IRunnableEntityProcess;
import br.com.orionsoft.monstrengo.core.process.ProcessBasic;
import br.com.orionsoft.monstrengo.core.process.ProcessParamEntity;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.core.util.PrintUtils;
import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityCollection;
import br.com.orionsoft.monstrengo.crud.entity.PropertyValueException;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

/**
 * Este processo imprime Documentos de cobrança gerados.
 * 
 * <p>
 * <b>Procedimentos:</b><br>
 * zerarValor: Define para que o processo zere o valor do documento antes de
 * enviar para impressão. O Financeiro é preparado para que títulos com valor
 * Zero sejam impressos com valor em branco. E também, no recebimento destes
 * títulos, é forçada a alteração do valor inicial lançado.<br>
 * Obter a lista de documentos. <br>
 * Chamar o servico de impressão de documentos para cada guia da lista.
 * 
 * @author Lucio
 * @since 03/03/2007
 * @version 20120821
 */
@ProcessMetadata(label="Imprimir documentos de cobrança", hint="Permite a impressão de vários documentos de cobrança", description="Permite a impressão de vários documentos de cobrança utilizando filtros de pesquisa para seleção dos documentos.")
public class ImprimirDocumentosCobrancaProcess extends ProcessBasic implements IRunnableEntityProcess, IRunnableEntityCollectionProcess{
	public static final String PROCESS_NAME = "ImprimirDocumentosCobrancaProcess";

	private String cpfCnpj = "";

	private String instrucoesAdicionais = "";

	private Calendar dataDocumentoAte = CalendarUtils.getCalendar();
	private Calendar dataDocumentoDe = CalendarUtils.getCalendarBaseDate();

	private Calendar dataVencimentoAte = CalendarUtils.getCalendar();
	private Calendar dataVencimentoDe = CalendarUtils.getCalendarFirstMonthDay();

	private Long documentoCobrancaCategoriaId = IDAO.ENTITY_UNSAVED;
	private String nomeGerenciadorDocumento = "";

	private ProcessParamEntity<EscritorioContabil> paramEscritorioContabil = new ProcessParamEntity<EscritorioContabil>(EscritorioContabil.class, false, this);
	private ProcessParamEntity<Municipio> paramMunicipio = new ProcessParamEntity<Municipio>(Municipio.class, false, this);

	private Boolean excluirMunicipio = false;
	private Boolean zerarValor = false;
	private Boolean downloadPdfZip = false;
	

	private Boolean recalcularDataVencimento = false;
	private Calendar dataPagamento = CalendarUtils.getCalendar();

	private Integer tipoOrdem = ListarDocumentosCobrancaService.ORDEM_ENDERECO;

	private OutputStream outputStream;
	private int printerIndex = PrintUtils.PRINTER_INDEX_NO_PRINT;
	private Boolean enviarEMail = false;

    private InputStream inputStreamImagem;
	
	public int getPrinterIndex() {return printerIndex;}
	public void setPrinterIndex(int printerIndex) {this.printerIndex = printerIndex;}

	public Boolean getEnviarEMail() {return enviarEMail;}
	public void setEnviarEMail(Boolean enviarEMail) {this.enviarEMail = enviarEMail;}

	public Boolean getDownloadPdfZip() {return downloadPdfZip;}
	public void setDownloadPdfZip(Boolean downloadPdfZip) {this.downloadPdfZip = downloadPdfZip;}

	public List<SelectItem> getPrinterIndexList() {
		return PrintUtils.retrievePrinters();
	}

	public String getNomeGerenciadorDocumento() {return nomeGerenciadorDocumento;}
	public void setNomeGerenciadorDocumento(String nomeGerenciadorDocumento) {this.nomeGerenciadorDocumento = nomeGerenciadorDocumento;}
	
	public boolean isVisualizando() throws BusinessException {
		return (this.beanList != null);
	}

	public int getSize() {
		if (this.beanList == null)
			return 0;
		return beanList.size();
	}

	private List<DocumentoCobrancaBean> beanList = null;

	public List<DocumentoCobrancaBean> getBeanList() {
		return beanList;
	}

	/**
	 * Este método permite que o beanList receba uma lista de
	 * DocumentoCobrancaBean, vindo de outros Processos que queiram a Impressão
	 * das guias.
	 * 
	 * @param documentoCobrancaBeans
	 */
	public void setBeanList(List<DocumentoCobrancaBean> documentoCobrancaBeans) {
		this.beanList = documentoCobrancaBeans;
	}

	public boolean runVisualizar() {
		this.getMessageList().clear();

		try {
			/* Busca as guias Grcs geradas */
			ServiceData sdGuias = new ServiceData(ListarDocumentosCobrancaService.SERVICE_NAME, null);

			sdGuias.getArgumentList().setProperty(ListarDocumentosCobrancaService.IN_CPF_CNPJ_OPT, cpfCnpj);

			if (dataDocumentoDe != null)
				sdGuias.getArgumentList().setProperty(ListarDocumentosCobrancaService.IN_DATA_DOCUMENTO_DE_OPT, dataDocumentoDe);
			if (dataDocumentoAte != null)
				sdGuias.getArgumentList().setProperty(ListarDocumentosCobrancaService.IN_DATA_DOCUMENTO_ATE_OPT, dataDocumentoAte);

			if (dataVencimentoDe != null)
				sdGuias.getArgumentList().setProperty(ListarDocumentosCobrancaService.IN_DATA_VENCIMENTO_DE_OPT, dataVencimentoDe);
			
			if (dataVencimentoAte != null)
				sdGuias.getArgumentList().setProperty(ListarDocumentosCobrancaService.IN_DATA_VENCIMENTO_ATE_OPT, dataVencimentoAte);

			if (!paramEscritorioContabil.isNull())
				sdGuias.getArgumentList().setProperty(ListarDocumentosCobrancaService.IN_ESCRITORIO_CONTABIL_ID_OPT, paramEscritorioContabil.getValue().getId());

			if (documentoCobrancaCategoriaId != null && documentoCobrancaCategoriaId.compareTo(IDAO.ENTITY_UNSAVED) == 1)
				sdGuias.getArgumentList().setProperty(ListarDocumentosCobrancaService.IN_DOCUMENTO_COBRANCA_CATEGORIA_ID_OPT, documentoCobrancaCategoriaId);

			if (!paramMunicipio.isNull())
				sdGuias.getArgumentList().setProperty(ListarDocumentosCobrancaService.IN_MUNICIPIO_ID_OPT, paramMunicipio.getValue().getId());
			
			sdGuias.getArgumentList().setProperty(ListarDocumentosCobrancaService.IN_EXCLUIR_MUNICIPIO_OPT, excluirMunicipio);

			sdGuias.getArgumentList().setProperty(ListarDocumentosCobrancaService.IN_TIPO_ORDEM_OPT, tipoOrdem);

			this.getProcessManager().getServiceManager().execute(sdGuias);

			beanList = sdGuias.getFirstOutput();


		} catch (BusinessException e) {
			this.getMessageList().addAll(e.getErrorList());

			return false;
		}
		/* Se no tiver item na lista, o filtro nao serviu e indica isto */
		if (getSize() == 0) {
			this.getMessageList().add(ImprimirDocumentosCobrancaProcess.class, "LISTA_VAZIA");
			return false;
		}

		return true;
	}

	public String getProcessName() {
		return PROCESS_NAME;
	}

	public boolean runImprimir() {
		this.getMessageList().clear();

		try {
			if (!isVisualizando())
				runVisualizar();

			log.debug("Verificando se foi solicitado para zerar o valor");
			if (this.zerarValor)
				for (DocumentoCobrancaBean bean : getBeanList()) {
					log.debug("Zerando o valor do documento");
					if(bean.isTemDocumentoOriginal())
						bean.getDocumentoOriginal().getObject().setValor(BigDecimal.ZERO);
					else
						bean.setValorDocumento(BigDecimal.ZERO);
				}
			
			String descricaoAdicionalDataPagamento = "";
			if(this.recalcularDataVencimento){
				descricaoAdicionalDataPagamento = "NÃO RECEBER ESTE DOCUMENTO APÓS " + CalendarUtils.formatDate(this.dataPagamento) + ".";
				for (DocumentoCobrancaBean bean: getBeanList()) {
					/* Verifica se o documento está pendente e vencido para então
					 * gerar um documento com data de vencimento, multa e juros recalculados */
					if(bean.isChecked()&&(CalendarUtils.diffDay(bean.getDataVencimento(), this.dataPagamento) < 0)){

						/* Busca o documento original para o cálculo de juros e vencimento */
						if(!bean.isTemDocumentoOriginal()){
							IEntity<DocumentoCobranca> documento = UtilsCrud.retrieve(this.getProcessManager().getServiceManager(), DocumentoCobranca.class, bean.getId(), null);
							bean.setDocumentoOriginal(documento);
						}

						ServiceData sd = new ServiceData(CalcularVencimentoService.SERVICE_NAME, null);
						sd.getArgumentList().setProperty(CalcularVencimentoService.IN_DOCUMENTO, bean.getDocumentoOriginal());
						sd.getArgumentList().setProperty(CalcularVencimentoService.IN_DATA_PAGAMENTO, this.dataPagamento);

						this.getProcessManager().getServiceManager().execute(sd);
					}
				}
			}
			
			log.debug("Definindo as intruções adicionais e de data de pagamento dos documentos");
			for (DocumentoCobrancaBean bean : getBeanList())
				if (bean.isChecked()) {
					log.debug("Definindo as instruções adicionais");
					bean.setInstrucoesAdicionais(instrucoesAdicionais + " " + descricaoAdicionalDataPagamento);
				}
	
			/* Chama o serviço ImprimirDocumento com a lista de Guias */
			ServiceData sdImprimir = new ServiceData(ImprimirDocumentosCobrancaService.SERVICE_NAME, null);
			sdImprimir.getArgumentList().setProperty(ImprimirDocumentosCobrancaService.IN_DOCUMENTO_BEAN_LIST,
					getBeanList());
			sdImprimir.getArgumentList().setProperty(ImprimirDocumentosCobrancaService.IN_OUTPUT_STREAM_OPT, this.outputStream);
			sdImprimir.getArgumentList().setProperty(ImprimirDocumentosCobrancaService.IN_DOWNLOAD_PDF_ZIP_OPT, this.downloadPdfZip);
			sdImprimir.getArgumentList().setProperty(ImprimirDocumentosCobrancaService.IN_PRINTER_INDEX_OPT, this.printerIndex);
			sdImprimir.getArgumentList().setProperty(ImprimirDocumentosCobrancaService.IN_ENVIAR_EMAIL_OPT, this.enviarEMail);
			sdImprimir.getArgumentList().setProperty(ImprimirDocumentosCobrancaService.IN_INPUT_STREAM_IMAGEM_OPT, this.inputStreamImagem);
			this.getProcessManager().getServiceManager().execute(sdImprimir);

			this.getMessageList().addAll(sdImprimir.getMessageList());

			if (!sdImprimir.getMessageList().isTransactionSuccess()){
				// Armazenando a lista de erros;
				this.getMessageList().addAll(sdImprimir.getMessageList());
				return false;
			}

			/* Grava a auditoria do processo */
			UtilsAuditorship.auditProcess(this, "cpfCnpj='" + cpfCnpj + "', escritório contábil='"
					+ paramEscritorioContabil.getValue() + "', município='" + paramMunicipio.getValue() + "''",
					null);

			return true;
		} catch (BusinessException e) {
			// Armazenando a lista de erros;
			this.getMessageList().addAll(e.getErrorList());

			return false;
		}
	}

	/**
	 * Este método gera as etiquetas para cada documento grcs percorrendo a
	 * lista de beans e inserindo cada elemento da lista em uma etiqueta
	 */
	public boolean runGerarEtiquetas() {
		boolean result = false;
		this.getMessageList().clear();
		try {

			/* recupera os contratos relacionados ao documento grcs */
			for (DocumentoCobrancaBean bean : beanList) {
				/* verifica se o bean foi selecionado */
				if (bean.isChecked()) {
					IEntity<DocumentoCobranca> documento = UtilsCrud.retrieve(this.getProcessManager().getServiceManager(),
							DocumentoCobranca.class, bean.getId(), null);
					IEntity<Contrato> contrato = documento.getProperty(DocumentoCobranca.CONTRATO).getValue()
							.getAsEntity();

					/* Chamando o serviço de Inserir etiquetas */
					ServiceData sd = new ServiceData(InserirEtiquetaEnderecoService.SERVICE_NAME, null);
					sd.getArgumentList().setProperty(InserirEtiquetaEnderecoService.IN_OPERADOR,
							this.getUserSession().getUser());
					sd.getArgumentList().setProperty(InserirEtiquetaEnderecoService.IN_CONTRATO_OPT, contrato);
					this.getProcessManager().getServiceManager().execute(sd);

					/*
					 * Pegando a mensagem do serviço de inserir etiqueta,
					 * independente de sucesso ou falha
					 */
					this.getMessageList().addAll(sd.getMessageList());

					/*
					 * Verifica se algum erro ocorreu durante a geração das
					 * etiquetas
					 */
					if (!sd.getMessageList().isTransactionSuccess()) {
						return false;
					}
				}
			}

			result = true;

		} catch (BusinessException e) {
			/* Armazenando a lista de erros */
			this.getMessageList().addAll(e.getErrorList());
		}
		return result;
	}

	/**
	 * Constrói uma lista de opções para ordenar as guias que podem ser geradas
	 * pelo processo.
	 * 
	 * @return
	 */
	public List<SelectItem> getTiposOrdem() {
		return ListarDocumentosCobrancaService.getTiposOrdem();
	}

	public Integer getTipoOrdem() {
		return tipoOrdem;
	}

	public void setTipoOrdem(Integer tipoOrdem) {
		this.tipoOrdem = tipoOrdem;
	}

	public Boolean getExcluirMunicipio() {
		return excluirMunicipio;
	}

	public void setExcluirMunicipio(Boolean excluirMunicipio) {
		this.excluirMunicipio = excluirMunicipio;
	}

	public String getCpfCnpj() {
		return cpfCnpj;
	}

	public void setCpfCnpj(String cpf) {
		this.cpfCnpj = cpf;
	}

	// public Boolean getRecalcular() {return recalcular;}
	// public void setRecalcular(Boolean recalcular) {this.recalcular =
	// recalcular;}

	public Long getDocumentoCobrancaCategoriaId() {
		return documentoCobrancaCategoriaId;
	}

	public void setDocumentoCobrancaCategoriaId(Long documentoCobrancaCategoriaId) {
		this.documentoCobrancaCategoriaId = documentoCobrancaCategoriaId;
	}

	public List<SelectItem> getDocumentoCobrancaCategoriaList() throws BusinessException {
		String filtro = "";
		if(!StringUtils.isEmpty(this.nomeGerenciadorDocumento))
			filtro = IDAO.ENTITY_ALIAS_HQL + "." + DocumentoCobrancaCategoria.CONVENIO_COBRANCA + "." + ConvenioCobranca.NOME_GERENCIADOR_DOCUMENTO + "='" + this.nomeGerenciadorDocumento + "'";
		
		List<SelectItem> result = this.getProcessManager().getServiceManager().getEntityManager().getEntitySelectItems(
				DocumentoCobrancaCategoria.class, filtro);

		/* Adiciona a primeira opção para mostar todas as formas pagamento */
		/* Lucio 20131022: Não é possível gerar um mesmo PDF com diversas categorias de DocCob,
		 * pois eles possuem layouts diferentes */
		// result.add(0, new SelectItem(IDAO.ENTITY_UNSAVED, "(Todas)"));

		return result;
	}

	public Calendar getDataVencimentoDe() {
		return dataVencimentoDe;
	}

	public void setDataVencimentoDe(Calendar vencimento) {
		this.dataVencimentoDe = vencimento;
	}

	public Calendar getDataDocumentoAte() {
		return dataDocumentoAte;
	}

	public void setDataDocumentoAte(Calendar dataLancamentoAte) {
		this.dataDocumentoAte = dataLancamentoAte;
	}

	public Calendar getDataDocumentoDe() {
		return dataDocumentoDe;
	}

	public void setDataDocumentoDe(Calendar dataLancamentoDe) {
		this.dataDocumentoDe = dataLancamentoDe;
	}

	public Calendar getDataVencimentoAte() {
		return dataVencimentoAte;
	}

	public void setDataVencimentoAte(Calendar dataVencimentoAte) {
		this.dataVencimentoAte = dataVencimentoAte;
	}

	public Boolean getZerarValor() {
		return zerarValor;
	}

	public void setZerarValor(Boolean zerarValor) {
		this.zerarValor = zerarValor;
	}

	public String getInstrucoesAdicionais() {
		return instrucoesAdicionais;
	}

	public void setInstrucoesAdicionais(String instrucoesAdicionais) {
		this.instrucoesAdicionais = instrucoesAdicionais;
	}

	public OutputStream getOutputStream() {
		return outputStream;
	}

	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}
	public Boolean getRecalcularDataVencimento() {
		return recalcularDataVencimento;
	}
	public void setRecalcularDataVencimento(Boolean recalcularDataVencimento) {
		this.recalcularDataVencimento = recalcularDataVencimento;
	}
	public Calendar getDataPagamento() {
		return dataPagamento;
	}
	public void setDataPagamento(Calendar dataPagamento) {
		this.dataPagamento = dataPagamento;
	}
	
	public ProcessParamEntity<EscritorioContabil> getParamEscritorioContabil() {
		return paramEscritorioContabil;
	}
	public ProcessParamEntity<Municipio> getParamMunicipio() {
		return paramMunicipio;
	}
	
	public InputStream getInputStreamImagem() {
		return inputStreamImagem;
	}
	public void setInputStreamImagem(InputStream inputStreamImagem) {
		this.inputStreamImagem = inputStreamImagem;
	}

	/* IRunnableEntityProcess */
	public boolean runWithEntity(IEntity<?> entity) {
		super.beforeRun();
		boolean result = false;
		/* Verifica se a entidade é compatível */
		/* Verifica se a entidade passada eh um Contrato ou eh descendente */
		if (ClassUtils.isAssignable(entity.getInfo().getType(), Contrato.class)) {
			Contrato oContrato = (Contrato) entity.getObject();
			this.cpfCnpj = oContrato.getPessoa().getDocumento();

			/* Alguns dados poderao ser inicializados aqui */
			this.dataVencimentoDe = CalendarUtils.getCalendar();
			this.dataVencimentoDe.add(Calendar.YEAR, -2);

			/* Executa a listagem com os parâmetros definidos acima */
			result = this.runVisualizar();
		}else
		if (ClassUtils.isAssignable(entity.getInfo().getType(), Pessoa.class)) {
			Pessoa oPessoa = (Pessoa) entity.getObject();
			this.cpfCnpj = oPessoa.getDocumento();

			/* Alguns dados poderao ser inicializados aqui */

			/* Executa a listagem com os parâmetros definidos acima */
			result = this.runVisualizar();
		}else
		{
			this.getMessageList().add(new BusinessMessage(IRunnableEntityProcess.class, "ENTITY_NOT_COMPATIBLE", PROCESS_NAME, entity.getInfo().getType().getName()));
		}

		return result;
	}
	
	@SuppressWarnings("unchecked")
	public boolean runWithEntities(IEntityCollection<?> entities) {
		super.beforeRun();
		boolean result = false;
		/* Verifica se a entidade é compatível */
		/* Verifica se a entidade passada eh um DocumentoCobranca ou eh descendente */
		if (ClassUtils.isAssignable(entities.getInfo().getType(), DocumentoCobranca.class)) {
			this.beanList = new ArrayList<DocumentoCobrancaBean>(entities.size());
			
			for(IEntity<DocumentoCobranca> entity: (IEntityCollection<DocumentoCobranca>) entities){
				this.beanList.add(new DocumentoCobrancaBean(entity, ""));
			}
			
			result = true;
		}else
			if (ClassUtils.isAssignable(entities.getInfo().getType(), Lancamento.class)) {
				this.beanList = new ArrayList<DocumentoCobrancaBean>(entities.size());
				
				for(IEntity<DocumentoCobranca> entity: (IEntityCollection<DocumentoCobranca>) entities){
					IEntity<DocumentoCobranca> doc = null;
					try {
						doc = entity.getProperty(Lancamento.DOCUMENTO_COBRANCA).getValue().getAsEntity();
						if(doc != null){
							this.beanList.add(new DocumentoCobrancaBean(doc, ""));
						}
					} catch (BusinessException e) {
						this.getMessageList().add(e.getErrorList());
					}
				}
				
				result = true;
			}else
		{
			this.getMessageList().add(new BusinessMessage(IRunnableEntityProcess.class, "ENTITY_NOT_COMPATIBLE", PROCESS_NAME, entities.getInfo().getType().getName()));
		}

		return result;
	}
}
