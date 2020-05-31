/*
 * Created on 31/03/2006 by antonio
 */
package br.com.orionsoft.financeiro.gerenciador.process;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.ClassUtils;

import br.com.orionsoft.basic.entities.Contrato;
import br.com.orionsoft.basic.entities.ContratoCategoria;
import br.com.orionsoft.basic.entities.endereco.Municipio;
import br.com.orionsoft.basic.entities.pessoa.CNAE;
import br.com.orionsoft.basic.entities.pessoa.EscritorioContabil;
import br.com.orionsoft.basic.entities.pessoa.Pessoa;
import br.com.orionsoft.basic.entities.pessoa.Representante;
import br.com.orionsoft.basic.etiquetas.InserirEtiquetaEnderecoService;
import br.com.orionsoft.financeiro.gerenciador.entities.ItemCusto;
import br.com.orionsoft.financeiro.gerenciador.services.ImprimirCartaCobrancaService;
import br.com.orionsoft.financeiro.gerenciador.services.ImprimirCartaCobrancaService.CartaCobrancaModelo;
import br.com.orionsoft.financeiro.gerenciador.services.ImprimirRelatorioCobrancaService;
import br.com.orionsoft.financeiro.gerenciador.services.InativarContratosService;
import br.com.orionsoft.financeiro.gerenciador.services.RelatorioCobrancaService;
import br.com.orionsoft.financeiro.gerenciador.services.RelatorioCobrancaService.QueryRelatorioCobranca;
import br.com.orionsoft.financeiro.gerenciador.services.RelatorioCobrancaService.RelatorioCobrancaModelo;
import br.com.orionsoft.monstrengo.core.annotations.ProcessMetadata;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.process.IRunnableEntityProcess;
import br.com.orionsoft.monstrengo.core.process.ProcessBasic;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.core.process.ProcessParamEntity;
import br.com.orionsoft.monstrengo.core.process.ProcessParamEntityList;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.core.util.PrintUtils;
import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.mail.entities.EmailAccount;

/**
 * Este processo lista as pendï¿½ncias dos contratos para cobranï¿½a.
 * 
 * <p><b>Procedimentos:</b>
 * 
 * @author Antï¿½nio 20070424
 * @version 20070424
 *
 * @spring.bean id="RelatorioCobrancaProcess" singleton="false"
 * @spring.property name="processManager" ref="ProcessManager"
 *
 */
@ProcessMetadata(label="Relatório de cobrança", hint="Gera um relatório com os lançamentos ainda não quitados, seu vencimento e seu valor", description="Gera um relatório com os lançamentos ainda não quitados, seu vencimento e seu valor.")
public class RelatorioCobrancaProcess extends ProcessBasic implements IRunnableEntityProcess{
    public static final String PROCESS_NAME = "RelatorioCobrancaProcess";

	private ProcessParamEntity<Pessoa> paramPessoa = new ProcessParamEntity<Pessoa>(Pessoa.class, false, this);
	private Boolean incluirFiliais = false;
	
	private ProcessParamEntityList<EscritorioContabil> paramEscritorioContabil= new ProcessParamEntityList<EscritorioContabil>(EscritorioContabil.class, false, this);
	
	private ProcessParamEntityList<ItemCusto> paramItemCusto = new ProcessParamEntityList<ItemCusto>(ItemCusto.class, false, this);
	private Boolean omitirItemCustoSelecionado = false;

	private ProcessParamEntity<CNAE> paramCnae = new ProcessParamEntity<CNAE>(CNAE.class, false, this);
	private String cnaeDescricao = "";

	private ProcessParamEntity<Municipio> paramMunicipio = new ProcessParamEntity<Municipio>(Municipio.class, false, this);
	private Boolean notMunicipio = false;

	private Long categoriaContratoId = IDAO.ENTITY_UNSAVED;
	private Long contratoRepresentanteId = IDAO.ENTITY_UNSAVED;
	private Boolean omitirValores = false;
	private int tipoContrato = 0;
	private Calendar dataLancamentoInicial = CalendarUtils.getCalendar();
	private Calendar dataLancamentoFinal = CalendarUtils.getCalendar();
	private Calendar dataVencimentoInicial = CalendarUtils.getCalendar();
	private Calendar dataVencimentoFinal = CalendarUtils.getCalendar();
	private Calendar dataPagamento = CalendarUtils.getCalendar();
	private Integer quantidadeItensInicial = null;
	private Integer quantidadeItensFinal = null;
	private Integer quantidadeItensPagosInicial = null;
	private Integer quantidadeItensPagosFinal = null;
	private Boolean possuiItensPagos = false;
	private OutputStream outputStream = null;
	private int printerIndex = 0;
	private int cartaCobrancaModelo = CartaCobrancaModelo.PADRAO.ordinal();
	private int relatorioCobrancaModelo = RelatorioCobrancaModelo.RETRATO.ordinal();

	private Boolean enviarEMail = false;
	private ProcessParamEntity<EmailAccount> paramContaEMail = new ProcessParamEntity<EmailAccount>(EmailAccount.class, false, this);
	private String mensagemEMail = "";

	private String observacao;
	
	private List<QueryRelatorioCobranca> lista = null;
	
	@Override
	public void start() throws ProcessException {
		super.start();
		dataLancamentoInicial.add(Calendar.YEAR, -1);
		dataLancamentoInicial.set(Calendar.DATE, dataLancamentoInicial.getActualMinimum(Calendar.MONTH));
		dataVencimentoInicial.add(Calendar.YEAR, -1);
		dataVencimentoInicial.set(Calendar.DATE, dataVencimentoInicial.getActualMinimum(Calendar.MONTH));
	}
    
	public boolean runGerarPdf(){
		super.beforeRun();
		log.debug("Iniciando RelatorioCobrancaProcess (ImprimirCartaCobranca)");
		try {
	        List<QueryRelatorioCobranca> list = this.lista==null?this.execute(null):this.lista;
	        if(list == null)
	        	return false;

    		/*
	         * Imprime a carta de cobrança
	         */
    		ServiceData sd = new ServiceData(ImprimirRelatorioCobrancaService.SERVICE_NAME, null);
    		sd.getArgumentList().setProperty(ImprimirRelatorioCobrancaService.IN_DATA_PAGAMENTO_OPT, this.dataPagamento);
    		sd.getArgumentList().setProperty(ImprimirRelatorioCobrancaService.IN_OUTPUT_STREAM, this.outputStream);
    		sd.getArgumentList().setProperty(ImprimirRelatorioCobrancaService.IN_QUERY_RELATORIO_COBRANCA, list);
			sd.getArgumentList().setProperty(ImprimirRelatorioCobrancaService.IN_RELATORIO_COBRANCA_MODELO, RelatorioCobrancaModelo.values()[this.relatorioCobrancaModelo]);
    		this.getProcessManager().getServiceManager().execute(sd);
    		
	        return true;
		} catch (BusinessException e) {
			/* Armazenando a lista de erros */
			this.getMessageList().addAll(e.getErrorList());
			return false;
		}

	}
	
	public boolean runListar(){
		super.beforeRun();

		this.lista = this.execute(null);
		
		return this.lista != null;
	}
	
	public boolean runInativarContratos(){
		super.beforeRun();
		
		Set<Long> contratoIdSet = new HashSet<Long>(this.lista.size());
		for(QueryRelatorioCobranca bean: this.lista)
			if(bean.isChecked())
				contratoIdSet.add(bean.getContratoId());

		ServiceData sd = new ServiceData(InativarContratosService.SERVICE_NAME, null);
		sd.getArgumentList().setProperty(InativarContratosService.IN_DATA_RESCISAO, CalendarUtils.getCalendar());
		sd.getArgumentList().setProperty(InativarContratosService.IN_CONTRATO_ID_SET, contratoIdSet);
		sd.getArgumentList().setProperty(InativarContratosService.IN_OBSERVACAO, this.getObservacao());
		sd.getArgumentList().setProperty(InativarContratosService.IN_USER_SESSION, this.getUserSession());
		try {
			this.getProcessManager().getServiceManager().execute(sd);
			this.getMessageList().add(sd.getMessageList());
		} catch (ServiceException e) {
			this.getMessageList().addAll(e.getErrorList());
			
			return false;
		}
		
		return true;
	}
	
	
	private List<QueryRelatorioCobranca> execute(OutputStream outputStreamLocal) {
		log.debug("Iniciando RelatorioCobrancaProcess...");
		try {
			ServiceData sd = new ServiceData(RelatorioCobrancaService.SERVICE_NAME, null);
			sd.getArgumentList().setProperty(RelatorioCobrancaService.IN_ITEM_CUSTO_ID_LIST, this.paramItemCusto.getValue().getIds());
			sd.getArgumentList().setProperty(RelatorioCobrancaService.IN_ITEM_CUSTO_NOT, this.omitirItemCustoSelecionado);
			
			sd.getArgumentList().setProperty(RelatorioCobrancaService.IN_ESCRITORIO_CONTABIL_ID_LIST, this.paramEscritorioContabil.getValue().getIds());
			
			sd.getArgumentList().setProperty(RelatorioCobrancaService.IN_CATEGORIA_CONTRATO_ID, this.categoriaContratoId);
			sd.getArgumentList().setProperty(RelatorioCobrancaService.IN_TIPO_CONTRATO, this.tipoContrato);
			sd.getArgumentList().setProperty(RelatorioCobrancaService.IN_DATA_LANCAMENTO_INICIAL, this.dataLancamentoInicial);
			sd.getArgumentList().setProperty(RelatorioCobrancaService.IN_DATA_LANCAMENTO_FINAL, this.dataLancamentoFinal);
			sd.getArgumentList().setProperty(RelatorioCobrancaService.IN_DATA_VENCIMENTO_INICIAL, this.dataVencimentoInicial);
			sd.getArgumentList().setProperty(RelatorioCobrancaService.IN_DATA_VENCIMENTO_FINAL, this.dataVencimentoFinal);
			sd.getArgumentList().setProperty(RelatorioCobrancaService.IN_DATA_PAGAMENTO_OPT, this.dataPagamento);
			sd.getArgumentList().setProperty(RelatorioCobrancaService.IN_QUANTIDADE_ITENS_INICIAL, this.quantidadeItensInicial);
			sd.getArgumentList().setProperty(RelatorioCobrancaService.IN_QUANTIDADE_ITENS_FINAL, this.quantidadeItensFinal);
//			sd.getArgumentList().setProperty(RelatorioCobrancaService.IN_QUANTIDADE_ITENS_PAGOS_INICIAL, this.quantidadeItensPagosInicial);
//			sd.getArgumentList().setProperty(RelatorioCobrancaService.IN_QUANTIDADE_ITENS_PAGOS_FINAL, this.quantidadeItensPagosFinal);
			sd.getArgumentList().setProperty(RelatorioCobrancaService.IN_POSSUI_ITENS_PAGO, this.possuiItensPagos);
			
			if(!this.paramPessoa.isNull())
				if(this.incluirFiliais)
					sd.getArgumentList().setProperty(RelatorioCobrancaService.IN_CPF_CNPJ_OPT, this.paramPessoa.getValue().getObject().getDocumento().substring(0, 9));
				else
					sd.getArgumentList().setProperty(RelatorioCobrancaService.IN_CPF_CNPJ_OPT, this.paramPessoa.getValue().getObject().getDocumento());
			
			if(!this.paramCnae.isNull())
				sd.getArgumentList().setProperty(RelatorioCobrancaService.IN_CNAE_ID_OPT, this.paramCnae.getValue().getId());
			
			sd.getArgumentList().setProperty(RelatorioCobrancaService.IN_CNAE_DESCRICAO_OPT, this.cnaeDescricao);
			
			if(!this.paramMunicipio.isNull()){
				sd.getArgumentList().setProperty(RelatorioCobrancaService.IN_MUNICIPIO_ID_OPT, this.paramMunicipio.getValue().getId());
				sd.getArgumentList().setProperty(RelatorioCobrancaService.IN_NOT_MUNICIPIO_OPT, this.notMunicipio);
			}
			
			sd.getArgumentList().setProperty(RelatorioCobrancaService.IN_OMITIR_VALORES, this.omitirValores);
			sd.getArgumentList().setProperty(RelatorioCobrancaService.IN_CONTRATO_REPRESENTANTE_ID_OPT, this.contratoRepresentanteId);
			this.getProcessManager().getServiceManager().execute(sd);
			
			List<QueryRelatorioCobranca> result = sd.getFirstOutput();
			
            if(result.isEmpty())
            	 throw new BusinessException(MessageList.create(RelatorioCobrancaProcess.class, "LISTA_VAZIA", ""));

			return result;

		} catch (BusinessException e) {
			/* Armazenando a lista de erros */
			this.getMessageList().addAll(e.getErrorList());
		}
		
		return null;
	}
	
	public boolean runImprimirCartaCobranca() {
		super.beforeRun();
		log.debug("Iniciando RelatorioCobrancaProcess (ImprimirCartaCobranca)");
		try {
	        List<QueryRelatorioCobranca> list = this.lista==null?this.execute(null):this.lista;
	        if(list == null)
	        	return false;

    		/*
	         * Imprime a carta de cobrança
	         */
    		ServiceData sd = new ServiceData(ImprimirCartaCobrancaService.SERVICE_NAME, null);
    		sd.getArgumentList().setProperty(ImprimirCartaCobrancaService.IN_QUERY_RELATORIO_COBRANCA, list);
    		sd.getArgumentList().setProperty(ImprimirCartaCobrancaService.IN_MODELO_CARTA_COBRANCA, CartaCobrancaModelo.values()[this.cartaCobrancaModelo]);
    		sd.getArgumentList().setProperty(ImprimirCartaCobrancaService.IN_OUTPUT_STREAM, this.outputStream);
			sd.getArgumentList().setProperty(ImprimirCartaCobrancaService.IN_ENVIAR_EMAIL_OPT, this.enviarEMail);
			sd.getArgumentList().setProperty(ImprimirCartaCobrancaService.IN_CONTA_EMAIL_OPT, this.paramContaEMail.getValue().getObject());
			sd.getArgumentList().setProperty(ImprimirCartaCobrancaService.IN_MENSAGAEM_EMAIL_OPT, this.mensagemEMail);
    		this.getProcessManager().getServiceManager().execute(sd);
    		
            /* Mensagem de sucesso */
            this.getMessageList().add(new BusinessMessage(RelatorioCobrancaProcess.class, "SUCESSO_CARTA_COBRANCA", ""));
	        return true;
		} catch (BusinessException e) {
			/* Armazenando a lista de erros */
			this.getMessageList().addAll(e.getErrorList());
			return false;
		}
	}

	public boolean gerarEtiquetas() {
		log.debug("Iniciando RelatorioCobrancaProcess (ImprimirCartaCobranca)");
		try {
	        List<QueryRelatorioCobranca> list = this.execute(null);
	        if(list == null)
	        	return false;

	        /*
	         * Imprime as etiquetas
	         */
	        List<Long> pessoaIdList = new ArrayList<Long>();
	        for (QueryRelatorioCobranca query : list) {
	        	Long pessoaId = query.getId();
	        	if (!pessoaIdList.contains(pessoaId)) {
	        		pessoaIdList.add(pessoaId);

	        		ServiceData sdEtiquetas = new ServiceData(InserirEtiquetaEnderecoService.SERVICE_NAME, null);
	        		sdEtiquetas.getArgumentList().setProperty(InserirEtiquetaEnderecoService.IN_OPERADOR, this.getUserSession().getUser());
	        		sdEtiquetas.getArgumentList().setProperty(InserirEtiquetaEnderecoService.IN_PESSOA_ID_OPT, pessoaId);
	        		this.getProcessManager().getServiceManager().execute(sdEtiquetas);
	        	}
	        }
	        /* Mensagem de sucesso */
	        this.getMessageList().add(new BusinessMessage(BusinessMessage.TYPE_INFO, RelatorioCobrancaProcess.class, "SUCESSO_ETIQUETA", ""));
	        return true;
		} catch (BusinessException e) {
			/* Armazenando a lista de erros */
			this.getMessageList().addAll(e.getErrorList());
			return false;
		}
	}

	public Boolean getOmitirItemCustoSelecionado() {
		return omitirItemCustoSelecionado;
	}

	public void setOmitirItemCustoSelecionado(Boolean omitirItemCustoSelecionado) {
		this.omitirItemCustoSelecionado = omitirItemCustoSelecionado;
	}

	public ProcessParamEntity<Pessoa> getParamPessoa() {
		return paramPessoa;
	}

	public ProcessParamEntityList<EscritorioContabil> getParamEscritorioContabil() {
		return paramEscritorioContabil;
	}

	public ProcessParamEntityList<ItemCusto> getParamItemCusto() {
		return paramItemCusto;
	}

	public ProcessParamEntity<CNAE> getParamCnae() {
		return paramCnae;
	}

	public ProcessParamEntity<Municipio> getParamMunicipio() {
		return paramMunicipio;
	}

	public void setPrinterIndex(int printerIndex) {
		this.printerIndex = printerIndex;
	}

	public List<SelectItem> getListPrinterIndex() {
		return PrintUtils.retrievePrinters();
	}
	
	public List<SelectItem> getListCartaCobrancaModelo() {
		List<SelectItem> result = new ArrayList<SelectItem>();
		for(CartaCobrancaModelo modelo : CartaCobrancaModelo.values())
		    result.add(new SelectItem(modelo.ordinal(), modelo.getNome()));
		return result;
	}
	
	public List<SelectItem> getListRelatorioCobrancaModelo() {
		List<SelectItem> result = new ArrayList<SelectItem>();
		for(RelatorioCobrancaModelo modelo : RelatorioCobrancaModelo.values())
		    result.add(new SelectItem(modelo.ordinal(), modelo.getNome()));
		return result;
	}
	
	/**
     * Constrï¿½i uma lista de Categorias de Contratos para ser usada
     * na exibiï¿½ï¿½o da pï¿½gina JSF.
     * @return List<SelectItem>
     * @throws BusinessException 
     */
    public List<SelectItem> getListCategoriaContrato() throws BusinessException {
        List<SelectItem> result = this.getProcessManager().getServiceManager().getEntityManager().getEntitySelectItems(ContratoCategoria.class, "");
        result.add(0, new SelectItem(IDAO.ENTITY_UNSAVED, "(Todos)"));
        return result;
    }

    public List<SelectItem> getListMunicipio() throws BusinessException {
        List<SelectItem> result = this.getProcessManager().getServiceManager().getEntityManager().getEntitySelectItems(Municipio.class, "");
        result.add(0, new SelectItem(IDAO.ENTITY_UNSAVED, "(Todos)"));
        return result;
    }
    
    public List<SelectItem> getListContratoRepresentante() throws BusinessException {
        List<SelectItem> result = this.getProcessManager().getServiceManager().getEntityManager().getEntitySelectItems(Representante.class, "");
        result.add(0, new SelectItem(IDAO.ENTITY_UNSAVED, "(Todos)"));
        return result;
    }
    
    public List<SelectItem> getListTipoContrato() {
        List<SelectItem> result = new ArrayList<SelectItem>(3);
        result.add(new SelectItem(RelatorioCobrancaService.TIPO_CONTRATO_TODOS, "Todos"));
        result.add(new SelectItem(RelatorioCobrancaService.TIPO_CONTRATO_ATIVOS, "Ativos"));
        result.add(new SelectItem(RelatorioCobrancaService.TIPO_CONTRATO_INATIVOS, "Inativos"));
        return result;
    }

	public String getProcessName() {
		return PROCESS_NAME;
	}

	public Long getCategoriaContratoId() {
		return categoriaContratoId;
	}

	public void setCategoriaContratoId(Long categoriaContratoId) {
		this.categoriaContratoId = categoriaContratoId;
	}

	public int getTipoContrato() {
		return tipoContrato;
	}

	public void setTipoContrato(int tipoContrato) {
		this.tipoContrato = tipoContrato;
	}

	public Integer getQuantidadeItensInicial() {
		return quantidadeItensInicial;
	}

	public void setQuantidadeItensInicial(Integer quantidadeItensInicial) {
		this.quantidadeItensInicial = quantidadeItensInicial;
	}

	public Integer getQuantidadeItensFinal() {
		return quantidadeItensFinal;
	}

	public void setQuantidadeItensFinal(Integer quantidadeItensFinal) {
		this.quantidadeItensFinal = quantidadeItensFinal;
	}
	
	public Integer getQuantidadeItensPagosInicial() {
		return quantidadeItensPagosInicial;
	}

	public void setQuantidadeItensPagosInicial(Integer quantidadeItensPagoInicial) {
		this.quantidadeItensPagosInicial = quantidadeItensPagoInicial;
	}
	
	public Boolean getPossuiItensPagos() {
		return possuiItensPagos;
	}

	public void setPossuiItensPagos(Boolean itensPagos) {
		this.possuiItensPagos = itensPagos;
	}

	public Integer getQuantidadeItensPagosFinal() {
		return quantidadeItensPagosFinal;
	}

	public void setQuantidadeItensPagosFinal(Integer quantidadeItensPagosFinal) {
		this.quantidadeItensPagosFinal = quantidadeItensPagosFinal;
	}

	public OutputStream getOutputStream() {
		return outputStream;
	}

	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	public Long getContratoRepresentanteId() {
		return contratoRepresentanteId;
	}

	public void setContratoRepresentanteId(Long contratoRepresentanteId) {
		this.contratoRepresentanteId = contratoRepresentanteId;
	}

	public Integer getPrinterIndex() {
		return printerIndex;
	}

	public void setPrinterIndex(Integer printerIndex) {
		this.printerIndex = printerIndex;
	}

	public int getCartaCobrancaModelo() {
		return cartaCobrancaModelo;
	}

	public void setCartaCobrancaModelo(int cartaCobrancaModelo) {
		this.cartaCobrancaModelo = cartaCobrancaModelo;
	}

	public Calendar getDataLancamentoInicial() {
		return dataLancamentoInicial;
	}

	public void setDataLancamentoInicial(Calendar dataLancamentoInicial) {
		this.dataLancamentoInicial = dataLancamentoInicial;
	}

	public Calendar getDataVencimentoInicial() {
		return dataVencimentoInicial;
	}

	public void setDataVencimentoInicial(Calendar dataVencimentoInicial) {
		this.dataVencimentoInicial = dataVencimentoInicial;
	}

	public Calendar getDataVencimentoFinal() {
		return dataVencimentoFinal;
	}

	public void setDataVencimentoFinal(Calendar dataVencimentoFinal) {
		this.dataVencimentoFinal = dataVencimentoFinal;
	}

	public void setDataLancamentoFinal(Calendar dataLancamentoFinal) {
		this.dataLancamentoFinal = dataLancamentoFinal;
	}

	public Calendar getDataLancamentoFinal() {
		return dataLancamentoFinal;
	}

	public Boolean getIncluirFiliais() {return incluirFiliais;}
	public void setIncluirFiliais(Boolean incluirFiliais) {this.incluirFiliais = incluirFiliais;}
	
	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public Boolean getEnviarEMail() {
		return enviarEMail;
	}

	public void setEnviarEMail(Boolean enviarEMail) {
		this.enviarEMail = enviarEMail;
	}
	
	public String getMensagemEMail() {
		return mensagemEMail;
	}

	public void setMensagemEMail(String mensagemEMail) {
		this.mensagemEMail = mensagemEMail;
	}

	public ProcessParamEntity<EmailAccount> getParamContaEMail() {
		return paramContaEMail;
	}

	/*==============================================================================
	 * IRunnableEntityProcess	
	 *==============================================================================*/
	public boolean runWithEntity(IEntity<?> entity) {
        super.beforeRun();

        boolean result = false;
		
		/* Verifica se a entidade é compatível */
		/* Verifica se a entidade é compatível */
		/* Verifica se a entidade passada eh um DocumentoCobranca ou pertence eh descendente */
		if (ClassUtils.isAssignable(entity.getInfo().getType(), Contrato.class)) {
			try {
				this.paramPessoa.setValue((IEntity<Pessoa>) entity.getPropertyValue(Contrato.PESSOA));
			} catch (EntityException e) {
				// TODO Auto-generated catch block
				this.getMessageList().addAll(e.getErrorList());
			}

			/* Alguns dados poderao ser inicializados aqui */
			this.categoriaContratoId = IDAO.ENTITY_UNSAVED;

			/* Define as datas de vencimento e recebimento amplas */
			Calendar dataInicial = CalendarUtils.getCalendar(1900,Calendar.JANUARY,1);
			Calendar dataAtual = CalendarUtils.getCalendar();
			
			this.dataLancamentoInicial = (Calendar) dataInicial.clone(); 
			this.dataLancamentoFinal = (Calendar) dataAtual.clone();

			this.dataVencimentoInicial = (Calendar) dataInicial.clone(); 
			this.dataVencimentoFinal = (Calendar) dataAtual.clone();
				
			/* Não executa nada, pois o processo gera um PDF e é importante
			 * que o operador defina as propriedades do relatório antes de gerar o PDF */
			result = true;
		}else
		if (ClassUtils.isAssignable(entity.getInfo().getType(), Pessoa.class)) {
			Pessoa oPessoa = (Pessoa) entity.getObject();
			this.paramPessoa.setValue((IEntity<Pessoa>) entity);

			/* Alguns dados poderao ser inicializados aqui */
			this.categoriaContratoId = IDAO.ENTITY_UNSAVED;

			/* Define as datas de vencimento e recebimento amplas */
			Calendar dataInicial = CalendarUtils.getCalendar(1900,Calendar.JANUARY,1);
			Calendar dataAtual = CalendarUtils.getCalendar();
			
			this.dataLancamentoInicial = (Calendar) dataInicial.clone(); 
			this.dataLancamentoFinal = (Calendar) dataAtual.clone();

			this.dataVencimentoInicial = (Calendar) dataInicial.clone(); 
			this.dataVencimentoFinal = (Calendar) dataAtual.clone();
				
			/* Não executa nada, pois o processo gera um PDF e é importante
			 * que o operador defina as propriedades do relatório antes de gerar o PDF */
			result = true;
		}else
		{
			this.getMessageList().add(new BusinessMessage(IRunnableEntityProcess.class, "ENTITY_NOT_COMPATIBLE", PROCESS_NAME, entity.getInfo().getType().getName()));
		}
		
		return result;
	}

	public Boolean getNotMunicipio() {
		return notMunicipio;
	}

	public void setNotMunicipio(Boolean notMunicipio) {
		this.notMunicipio = notMunicipio;
	}

	public Boolean getOmitirValores() {
		return omitirValores;
	}

	public void setOmitirValores(Boolean omitirValores) {
		this.omitirValores = omitirValores;
	}

	public Calendar getDataPagamento() {
		return dataPagamento;
	}

	public void setDataPagamento(Calendar dataPagamento) {
		this.dataPagamento = dataPagamento;
	}

	public String getCnaeDescricao() {
		return cnaeDescricao;
	}

	public void setCnaeDescricao(String cnaeDescricao) {
		this.cnaeDescricao = cnaeDescricao;
	}

	public String getFileName() {
		StringBuffer result = new StringBuffer();
		result.append("CartasCobranca-");
		result.append(CartaCobrancaModelo.values()[this.cartaCobrancaModelo].getNome());
		result.append(CartaCobrancaModelo.values()[this.cartaCobrancaModelo].getExtensaoSaida());
		return result.toString();
	}

	public int getRelatorioCobrancaModelo() {
		return relatorioCobrancaModelo;
	}

	public void setRelatorioCobrancaModelo(int relatorioCobrancaModelo) {
		this.relatorioCobrancaModelo = relatorioCobrancaModelo;
	}

	public List<QueryRelatorioCobranca> getLista() {
		return lista;
	}
	
	

}
/*
select 
  pessoa.nome,
  pessoa.documento,
  grupo.dataVencimento,
  descricao.nome,
  grupo.saldo
from 
  financeiro_grupo grupo
inner join basic_contrato contrato on (
  contrato.id = grupo.contrato
)
inner join basic_pessoa pessoa on (
  pessoa.id = contrato.pessoa
)
left outer join financeiro_descricao descricao on (
  descricao.id = grupo.id
)
left outer join financeiro_lancamento lancamento on (
  lancamento.grupo = grupo.id
)
left outer join financeiro_item_custo itemCusto on (
  itemCusto.id = lancamento.itemCusto
)
where (
  (grupo.saldo > 0) and
  (contrato.categoria is null) and
  (contrato.inativo = false) and
  (lancamento.itemCusto in (select id from financeiro_item_custo)) and
  (grupo.dataLancamento between '2007-04-01' and '2007-04-30') and
  (grupo.dataVencimento between '2007-04-01' and '2007-04-30')
)
order by
  pessoa.documento,
  pessoa.nome,
  grupo.dataVencimento
 */

/*
select 
  grupo.contrato
from 
  financeiro_grupo grupo 
inner join basic_contrato contrato on (
  contrato.id = grupo.contrato
)
left outer join financeiro_lancamento lancamento on (
  lancamento.grupo = grupo.id
)
left outer join financeiro_item_custo itemCusto on (
  itemCusto.id = lancamento.itemCusto
)
where
  (grupo.saldo > 0) and
  (contrato.categoria is null) and
  (contrato.inativo = false) and
  (lancamento.itemCusto in (select id from financeiro_item_custo)) and
  (grupo.dataLancamento between '2007-01-01' and '2007-04-30') and
  (grupo.dataVencimento between '2007-04-01' and '2007-04-30')
group by
  grupo.contrato
having 
  count(grupo.contrato) = 1
 */
