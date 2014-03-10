/*
 * Created on 31/03/2006 by antonio
 */
package br.com.orionsoft.financeiro.view.jsf;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimento;
import br.com.orionsoft.financeiro.gerenciador.process.QuitarLancamentoProcess;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.core.util.DecimalUtils;
import br.com.orionsoft.monstrengo.crud.documents.services.ListModelDocumentEntityService;
import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityCollection;
import br.com.orionsoft.monstrengo.crud.entity.IProperty;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.view.jsf.bean.BeanSessionBasic;
import br.com.orionsoft.monstrengo.view.jsf.bean.IRunnableProcessView;
import br.com.orionsoft.monstrengo.view.jsf.util.FacesUtils;

/**
 * Bean que controla a view de quitação de um grupo com
 * saldo em aberto.
 */
@ManagedBean
@SessionScoped
public class QuitarLancamentoBean extends BeanSessionBasic implements IRunnableProcessView{
	private static final long serialVersionUID = 1L;

	/** Define a view JSF que é ativada para cada view */
	public static final String VIEW_NAME = "quitarLancamentoBean";
	public static final String FACES_VIEW_PASSO_1 = "/pages/financeiro/gerenciadorQuitarLancamento1?faces-redirect=true";
	public static final String FACES_VIEW_PASSO_2 = "/pages/financeiro/gerenciadorQuitarLancamento2?faces-redirect=true";
	public static final String FACES_VIEW_PASSO_3 = "/pages/financeiro/gerenciadorQuitarLancamento3?faces-redirect=true";
	public static final String FACES_VIEW_PASSO_4 = "/pages/financeiro/gerenciadorQuitarLancamento4?faces-redirect=true";
	public static final String FACES_VIEW_PASSO_5 = "/pages/financeiro/gerenciadorQuitarLancamento5?faces-redirect=true";

	public static final String URL_PARAM_LANCAMENTO_ID = "lancamentoId";
    public static final String URL_PARAM_LANCAMENTO_SALDO = "lancamentoSaldo";

    private QuitarLancamentoProcess process = null;
    
    /*
	 * Andre 12/06/2008 - Caso o serviço de criação de documento de cobrança lançasse exception, o processo não era interrompido
	 * pois o método doCriarDocumentocobranca é usado pelo Ajax e o tipo de retorno é void, dessa forma não é possível mostrar
	 * o erro na hora da criação do documento, apenas no passo seguinte, que é a visualização dos dados. 
	 */	
	private boolean erroAoCriarDocumentoPagamento = false;

    /**
     * Este método tenta ler os parâmetros da atual requisição.
     * Se ele conseguir ele pega os valores, aplica no processo
     * e retorna true informando que os parâmetros foram encontrados.
     * Assim, os actions poderão decidir se passa para um ou para outro 
     * passo já que os parâmetros já foram fornecidos.
     * @throws BusinessException
     */
    public boolean loadParams() throws BusinessException {
    	boolean result = false;
        if (FacesUtils.isNotNull(super.getRequestParams().get(URL_PARAM_LANCAMENTO_ID))){
        	IEntity<Lancamento> lancamento = UtilsCrud.retrieve(getProcess().getProcessManager().getServiceManager(), Lancamento.class, Long.parseLong(super.getRequestParams().get(URL_PARAM_LANCAMENTO_ID).toString()), null);
        	
        	/* Solicita ao processo que prepare a sua execução com base na entidade lancamento fornecida
        	 * Haverá um cálculode juros automático */
        	result = this.getProcess().runWithEntity(lancamento);

        	/* Permite um fornecimento do valor a ser pago personalizado */
        	if (FacesUtils.isNotNull(super.getRequestParams().get(URL_PARAM_LANCAMENTO_SALDO))){
        		double valor = Double.parseDouble(super.getRequestParams().get(URL_PARAM_LANCAMENTO_SALDO).toString());
        		/*
        		 * Transforma valor em absoluto. E o define diretamente no movimento do processo
        		 */
        		if (valor < 0) {
        			valor = valor * -1;
        		}
    			this.getProcess().getContratos().get(0).getMovimentos().getFirst().getObject().setValor(DecimalUtils.getBigDecimal(valor));
        		result = this.getProcess().runCalcularMultaJuros();
        	}
        }

        if(!result)
        	throw new BusinessException(this.getProcess().getMessageList());
        	
        return result;
    }

    public void doReset() throws BusinessException, Exception {
    }

    public void doReload() throws BusinessException, Exception {
    }
    
    public QuitarLancamentoProcess getProcess() throws ProcessException {
        if (process == null)
            process = (QuitarLancamentoProcess)this.getApplicationBean().getProcessManager().createProcessByName(QuitarLancamentoProcess.PROCESS_NAME, this.getUserSessionBean().getUserSession());
        return process;
    }
    

    /**
     * Este método é util quando uma tela já chama diretamente
     * o quitar grupo e fornece por parâmetros de URL o grupo 
     * e o valor, assim, o passo 1, onde seria coletado o grupo
     * nao precisa ser executado 
     * @return
     * @throws BusinessException
     */
    public String actionQuitarLancamento(long lancamentoId, BigDecimal valorAQuitar){
    	log.debug("QuitarLancamentoBean.actionQuitarLancamento");

    	try {
    		IEntity<Lancamento> lancamento = UtilsCrud.retrieve(getProcess().getProcessManager().getServiceManager(), Lancamento.class, lancamentoId, null);

    		/* Solicita ao processo que prepare a sua execução com base na entidade lancamento fornecida
    		 * Haverá um cálculode juros automático */
    		if(this.getProcess().runWithEntity(lancamento)){
    			/* Permite um fornecimento do valor a ser pago personalizado */
    			/*
    			 * Transforma valor em absoluto. E o define diretamente no movimento do processo
    			 */
    			if (valorAQuitar.signum() < 0) {
    				valorAQuitar = valorAQuitar.negate();
    			}
    			
    			this.getProcess().getContratos().get(0).getMovimentos().getFirst().getObject().setValor(valorAQuitar);

    			if(!this.getProcess().runCalcularMultaJuros()){
        			FacesUtils.addErrorMsgs(this.getProcess().getMessageList());
        	    	return FACES_VIEW_PASSO_1;
    			}


    		}else{
    			FacesUtils.addErrorMsgs(this.getProcess().getMessageList());
    	    	return FACES_VIEW_PASSO_1;
    		}
    	} catch (BusinessException e) {
			FacesUtils.addErrorMsgs(e.getErrorList());
	    	return FACES_VIEW_PASSO_1;
    	}

    	/* Manda para o passo 2 para pegar a data de quitação e o valor*/
    	return FACES_VIEW_PASSO_2;
    }

    
    /**
     * Este método é util quando uma tela já chama diretamente
     * o quitar grupo e fornece por parâmetros de URL o grupo 
     * e o valor, assim, o passo 1, onde seria coletado o grupo
     * nao precisa ser executado 
     * @return
     * @throws BusinessException
     */
    public String actionInicio() throws BusinessException{
        log.debug("QuitarLancamentoBean.actionInicio");
        
        /* Pega por parâmetro de URL o grupo e o valor sugerido */
       	this.loadParams();
       	
       	/* Manda para o passo 2 para pegar a data de quitação e o valor*/
        return FACES_VIEW_PASSO_2;
    }
    
    public void doCalcularMultaJuros(){
    	log.debug("QuitarLancamentoBean.doCalcularMultaJuros");

    	try {
    		if(!getProcess().runCalcularMultaJuros())
    			FacesUtils.addErrorMsgs(getProcess().getMessageList());
    	} catch (ProcessException e) {
			FacesUtils.addErrorMsgs(e.getErrorList());
    	}
    }
    
    public void doReiniciarValores() throws ProcessException, BusinessException {
    	log.debug("QuitarLancamentoBean.doReiniciarValores");
    
    	getProcess().runPrepararContratos();
    }
    
    /**
     * Confirma os dados do passo 1 que identifica o grupo.
     */
    public String actionPasso1() throws ParseException {
        log.debug("QuitarLancamentoBean.actionInicio");
        /* Data não preenchida, vai direto para confirmação */
        return FACES_VIEW_PASSO_2;
    }
    
    /**
     * Confirma os dados do passo 2 que identifica o valor e data de quitação.
     */
    public String actionPasso2() throws BusinessException{
        log.debug("QuitarLancamentoBean.actionDocumentoPagamentoCategoria");
        
        /* Força um recálculo dos valores */
        this.doCalcularMultaJuros();
        
        
        /* Lucio 20120623: Verifica os documentos de pagamentos e se há lançamentos com 
         * diferentes CNPJ */
        //se já existir documento de pagamento, dá uma alerta
//        if (this.process.runVerificarDocumentoPagamentoExistente())
//        	FacesUtils.addInfoMsgs(this.process.getMessageList());
//        else
//        	this.process.runCriarDocumentoPagamento();
        	
        //senão manda para a tela de criação de documento de pagamento	
       	return FACES_VIEW_PASSO_3;

        //        IEntity lancamento = this.getProcess().getLancamento();
        
//        /* Define a conta padrão baseando na conta do grupo */
//        this.getCurrentProcess().setContaId(grupo.getConta().getId());
        
        /* 
         * TODO Verificar se é necessário a troca do documento. 
         */
//        /* Verifica se o lancamento já possui um documento vinculado para pular a tela de fornecimento de
//         * forma de documento e utilizar a definida pelo documento */
//        if (this.getCurrentProcess().getLancamento().getProperty(Lancamento.DOCUMENTO_COBRANCA).getValue().isValueNull() || this.getCurrentProcess().isNovoDocumento())
//        	/* Manda para a tela de solicitação de forma de pagamento */
//        	return FACES_VIEW_PASSO_3;
        /* Define a forma de pagamento*/ 
//        this.getProcess().setDocumentoPagamentoCategoria(lancamento.getProperty(Lancamento.DOCUMENTO_PAGAMENTO).getValue().getAsEntity().getProperty(DocumentoPagamento.DOCUMENTO_PAGAMENTO_CATEGORIA).getValue().getAsEntity());
        /* Manda direto para a tela de confirmação */
//        return FACES_VIEW_PASSO_4;
        
//        //se já existir documento de pagamento, manda direto para a tela de confirmação
//        if (this.process.runPossuiDocumentoPagamento()){
//        	return FACES_VIEW_PASSO_4;
//        	
//        //senão manda para a tela de criação de documento de pagamento	
//        }else{
//        	return FACES_VIEW_PASSO_3;
//        }

    }
    
    /**
     * Confirma os dados do passo 3 que identifica a forma de pagamento (Documento de cobrança).
     */
    public String actionPasso3(){
        log.debug("QuitarLancamentoBean.actionConfirmação");
        if (this.erroAoCriarDocumentoPagamento){ //Andre, 30/06/2008
			/* Adiciona as mensagens de erro no Faces */
			FacesUtils.addErrorMsgs(process.getMessageList());
			return FacesUtils.FACES_VIEW_FAILURE;
		}else{
			/* Manda direto para a tela de confirmação */
			return FACES_VIEW_PASSO_4;
		}
    }
    
    /**
     * Confirma todos os dados e realiza a quitação.
     * @throws EntityException 
     */
    public String actionPasso4() throws ParseException, ProcessException, EntityException {
        log.debug("QuitarLancamentoBean.actionQuitar");
        if (this.getProcess().runQuitar()){
            /* Adiciona as mensagens de info no Faces */
            FacesUtils.addInfoMsgs(this.getProcess().getMessageList());

            /* Definir o fluxo de tela de SUCESSO */
            return FACES_VIEW_PASSO_5;
        }else{
            /* Adiciona as mensagens de erro no Faces */
            FacesUtils.addErrorMsgs(this.getProcess().getMessageList());
            /* Definir o fluxo de tela de SUCESSO */
            return FacesUtils.FACES_VIEW_FAILURE;
        }
    }
    
    public List<IProperty> getPropriedadesEditaveis(){
    	try {
			return this.getProcess().getPropriedadesEditaveis();
		} catch (BusinessException e) {
			FacesUtils.addErrorMsgs(e.getErrorList());
			return null;
		}
    }

    public boolean isTemPropriedadesEditaveis(){
    	try {
			return this.getProcess().getPropriedadesEditaveis().size()>0;
		} catch (BusinessException e) {
			FacesUtils.addErrorMsgs(e.getErrorList());
			return false;
		}
    }
    
	public void doPrepararDocumentoPagamento() {
		log.debug("InserirLancamentoBean.doCriarDocumentoPagamento");
		if (!process.runPrepararDocumentoPagamento()) {
			this.erroAoCriarDocumentoPagamento = true; //Andre, 12/06/2008
			FacesUtils.addErrorMsgs(process.getMessageList());
		}else{
			FacesUtils.addInfoMsgs(process.getMessageList());
		}
	}

	/**
	 *  Cria uma lista com os modelos de etiquetas de entidades disponivel
	 *  para a entidade atualmente manipulada.
	 *  O tipo da entidade já deve estar definido para executar este método, para que ele mostra somente os modelos 
	 *  da entidade selecionada
	 */
	private List<SelectItem> modelsDocumentEntityBuffer = null;
	public List<SelectItem> getModelsDocumentEntity(){
		try{
			if(modelsDocumentEntityBuffer==null){
				ServiceData sd = new ServiceData(ListModelDocumentEntityService.SERVICE_NAME, null);
				sd.getArgumentList().setProperty(ListModelDocumentEntityService.IN_ENTITY_TYPE_NAME, LancamentoMovimento.class.getName());
				sd.getArgumentList().setProperty(ListModelDocumentEntityService.IN_APPLICATION_USER_OPT, this.getUserSessionBean().getUserSession().getUser());
				this.getApplicationBean().getProcessManager().getServiceManager().execute(sd);
				modelsDocumentEntityBuffer = sd.getFirstOutput();
			}
			
			return modelsDocumentEntityBuffer;
		}catch (ServiceException e){
			FacesUtils.addErrorMsgs(e.getErrorList());
			return null;
		}
    }
	public boolean isHasModelsDocumentEntity(){
		return this.getModelsDocumentEntity().size()>0;
	}

	/* IRunnableProcessView */
	public String getViewName() {
		return VIEW_NAME;
	}

	public String getRunnableEntityProcessName() {
		return QuitarLancamentoProcess.PROCESS_NAME;
	}

	public String runWithEntity(IEntity<?> entity) {
		try {
			this.getProcess().runWithEntity(entity);
		} catch (ProcessException e) {
			FacesUtils.addErrorMsgs(e.getErrorList());
			return FacesUtils.FACES_VIEW_FAILURE;
		}
		return FACES_VIEW_PASSO_2; // O lançamento já está preenchido, vai par ao segujndo passo!
	}
	
	@SuppressWarnings("unchecked")
	public String runWithEntities(IEntityCollection<?> entities) {
		try {
			this.getProcess().getLancamentos().clear();
			for(IEntity<?> entity: entities)
				this.getProcess().getLancamentos().add((IEntity<Lancamento>) entity);
			if(!this.getProcess().runPrepararContratos()){
				FacesUtils.addErrorMsgs(this.getProcess().getMessageList());
				return FacesUtils.FACES_VIEW_FAILURE;
			}
		} catch (ProcessException e) {
			FacesUtils.addErrorMsgs(e.getErrorList());
			return FacesUtils.FACES_VIEW_FAILURE;
		}
		return FACES_VIEW_PASSO_2; // O lançamento já está preenchido, vai par ao segujndo passo!
	}

}
