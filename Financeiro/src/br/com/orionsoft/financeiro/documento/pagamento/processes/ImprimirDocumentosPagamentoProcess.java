package br.com.orionsoft.financeiro.documento.pagamento.processes;

import java.io.OutputStream;
import java.util.Calendar;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import br.com.orionsoft.basic.etiquetas.InserirEtiquetaEnderecoService;
import br.com.orionsoft.financeiro.documento.pagamento.DocumentoPagamento;
import br.com.orionsoft.financeiro.documento.pagamento.DocumentoPagamentoBean;
import br.com.orionsoft.financeiro.documento.pagamento.DocumentoPagamentoCategoria;
import br.com.orionsoft.financeiro.documento.pagamento.services.ImprimirDocumentosPagamentoService;
import br.com.orionsoft.financeiro.documento.pagamento.services.ListarDocumentosPagamentoService;
import br.com.orionsoft.monstrengo.auditorship.services.UtilsAuditorship;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.process.ProcessBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.core.util.PrintUtils;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

/**
 * Este processo imprime Documentos de pagamento gerados.
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
 * @version 20070303
 * 
 * @spring.bean id="ImprimirDocumentosPagamentoProcess" init-method="start"
 *              destroy-method="finish" singleton="false"
 * @spring.property name="processManager" ref="ProcessManager"
 */
public class ImprimirDocumentosPagamentoProcess extends ProcessBasic {
	public static final String PROCESS_NAME = "ImprimirDocumentosPagamentoProcess";

	private String cpfCnpj = "";

	private String instrucoesAdicionais = "";

	private Calendar dataDocumentoAte = CalendarUtils.getCalendar();
	private Calendar dataDocumentoDe = CalendarUtils.getCalendarBaseDate();

	private Calendar dataVencimentoAte = CalendarUtils.getCalendar();
	private Calendar dataVencimentoDe = CalendarUtils.getCalendar(dataVencimentoAte.get(Calendar.YEAR),
			dataVencimentoAte.get(Calendar.MONTH), dataVencimentoAte.getActualMinimum(Calendar.DATE));

	private Long documentoPagamentoCategoriaId = IDAO.ENTITY_UNSAVED;
	private Long escritorioContabilId = IDAO.ENTITY_UNSAVED;
	private Long municipioId = IDAO.ENTITY_UNSAVED;
	private Boolean excluirMunicipio = false;
	private Integer tipoOrdem = ListarDocumentosPagamentoService.ORDEM_ENDERECO;

	private OutputStream outputStream;
	private int printerIndex = PrintUtils.PRINTER_INDEX_NO_PRINT;

	public int getPrinterIndex() {return printerIndex;}
	public void setPrinterIndex(int printerIndex) {this.printerIndex = printerIndex;}
	public List<SelectItem> getPrinterIndexList() {
		return PrintUtils.retrievePrinters();
	}

	public boolean isVisualizando() throws BusinessException {
		return (this.beanList != null);
	}

	public int getSize() {
		if (this.beanList == null)
			return 0;
		return beanList.size();
	}

	private List<DocumentoPagamentoBean> beanList = null;

	public List<DocumentoPagamentoBean> getBeanList() {
		return beanList;
	}

	/**
	 * Este método permite que o beanList receba uma lista de
	 * DocumentoPagamentoBean, vindo de outros Processos que queiram a Impressão
	 * das guias.
	 * 
	 * @param documentoPagamentoBeans
	 */
	public void setBeanList(List<DocumentoPagamentoBean> documentoPagamentoBeans) {
		this.beanList = documentoPagamentoBeans;
	}

	public boolean runVisualizar() {
		this.getMessageList().clear();

		try {
			/* Busca as guias Grcs geradas */
			ServiceData sdGuias = new ServiceData(ListarDocumentosPagamentoService.SERVICE_NAME, null);

			sdGuias.getArgumentList().setProperty(ListarDocumentosPagamentoService.IN_CPF_CNPJ_OPT, cpfCnpj);

			if (dataDocumentoDe != null)
				sdGuias.getArgumentList().setProperty(ListarDocumentosPagamentoService.IN_DATA_DOCUMENTO_DE_OPT,
						dataDocumentoDe);
			if (dataDocumentoAte != null)
				sdGuias.getArgumentList().setProperty(ListarDocumentosPagamentoService.IN_DATA_DOCUMENTO_ATE_OPT,
						dataDocumentoAte);

			if (dataVencimentoDe != null)
				sdGuias.getArgumentList().setProperty(ListarDocumentosPagamentoService.IN_DATA_VENCIMENTO_DE_OPT,
						dataVencimentoDe);
			if (dataVencimentoAte != null)
				sdGuias.getArgumentList().setProperty(ListarDocumentosPagamentoService.IN_DATA_VENCIMENTO_ATE_OPT,
						dataVencimentoAte);

			if (escritorioContabilId != null && escritorioContabilId.compareTo(IDAO.ENTITY_UNSAVED) == 1)
				sdGuias.getArgumentList().setProperty(ListarDocumentosPagamentoService.IN_ESCRITORIO_CONTABIL_ID_OPT,
						escritorioContabilId);

			if (documentoPagamentoCategoriaId != null && documentoPagamentoCategoriaId.compareTo(IDAO.ENTITY_UNSAVED) == 1)
				sdGuias.getArgumentList().setProperty(ListarDocumentosPagamentoService.IN_DOCUMENTO_PAGAMENTO_CATEGORIA_ID_OPT,
						documentoPagamentoCategoriaId);

			if (municipioId != null && municipioId.compareTo(IDAO.ENTITY_UNSAVED) == 1)
				sdGuias.getArgumentList().setProperty(ListarDocumentosPagamentoService.IN_MUNICIPIO_ID_OPT, municipioId);
			sdGuias.getArgumentList().setProperty(ListarDocumentosPagamentoService.IN_EXCLUIR_MUNICIPIO_OPT,
					excluirMunicipio);

			sdGuias.getArgumentList().setProperty(ListarDocumentosPagamentoService.IN_TIPO_ORDEM_OPT, tipoOrdem);

			this.getProcessManager().getServiceManager().execute(sdGuias);

			beanList = sdGuias.getFirstOutput();
		} catch (BusinessException e) {
			this.getMessageList().addAll(e.getErrorList());

			return false;
		}
		/* Se no tiver item na lista, o filtro nao serviu e indica isto */
		if (getSize() == 0) {
			this.getMessageList().add(ImprimirDocumentosPagamentoProcess.class, "LISTA_VAZIA");
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

			log.debug("Definindo as intruções adicionais dos documentos");
			if (StringUtils.isNotEmpty(instrucoesAdicionais)) {
				for (DocumentoPagamentoBean bean : getBeanList())
					if (bean.isChecked()) {
						log.debug("Definindo as instruções adicionais");
						bean.setInstrucoesAdicionais(instrucoesAdicionais);
					}
			}

			/* Chama o serviço ImprimirDocumento com a lista de Guias */
			ServiceData sdImprimir = new ServiceData(ImprimirDocumentosPagamentoService.SERVICE_NAME, null);
			sdImprimir.getArgumentList().setProperty(ImprimirDocumentosPagamentoService.IN_DOCUMENTO_BEAN_LIST,
					getBeanList());
			sdImprimir.getArgumentList().setProperty(ImprimirDocumentosPagamentoService.IN_OUTPUT_STREAM_OPT, this.outputStream);
			sdImprimir.getArgumentList().setProperty(ImprimirDocumentosPagamentoService.IN_PRINTER_INDEX_OPT, this.printerIndex);
			this.getProcessManager().getServiceManager().execute(sdImprimir);

			this.getMessageList().addAll(sdImprimir.getMessageList());

			if (!sdImprimir.getMessageList().isTransactionSuccess())
				return false;

			/* Grava a auditoria do processo */
			UtilsAuditorship.auditProcess(this, "cpfCnpj='" + cpfCnpj + "', escritorioContabilId='"
					+ escritorioContabilId + "', municipioId='" + municipioId + "',municipioId='" + municipioId + "'",
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
			for (DocumentoPagamentoBean bean : beanList) {
				/* verifica se o bean foi selecionado */
				if (bean.isChecked()) {
					IEntity documento = UtilsCrud.retrieve(this.getProcessManager().getServiceManager(),
							DocumentoPagamento.class, bean.getId(), null);
					IEntity contrato = documento.getProperty(DocumentoPagamento.CONTRATO).getValue()
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
		return ListarDocumentosPagamentoService.getTiposOrdem();
	}

	public Integer getTipoOrdem() {
		return tipoOrdem;
	}

	public void setTipoOrdem(Integer tipoOrdem) {
		this.tipoOrdem = tipoOrdem;
	}

	public Long getMunicipioId() {
		return municipioId;
	}

	public void setMunicipioId(Long cidade) {
		this.municipioId = cidade;
	}

	public Long getEscritorioContabilId() {
		return escritorioContabilId;
	}

	public void setEscritorioContabilId(Long escritorio) {
		this.escritorioContabilId = escritorio;
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

	public Long getDocumentoPagamentoCategoriaId() {
		return documentoPagamentoCategoriaId;
	}

	public void setDocumentoPagamentoCategoriaId(Long documentoPagamentoCategoriaId) {
		this.documentoPagamentoCategoriaId = documentoPagamentoCategoriaId;
	}

	public List<SelectItem> getDocumentoPagamentoCategoriaList() throws BusinessException {
		List<SelectItem> result = this.getProcessManager().getServiceManager().getEntityManager().getEntitySelectItems(
				DocumentoPagamentoCategoria.class, "");
		/* Adiciona a primeira opção para mostar todas as formas pagamento */
		result.add(0, new SelectItem(IDAO.ENTITY_UNSAVED, "(Todas)"));

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

}
