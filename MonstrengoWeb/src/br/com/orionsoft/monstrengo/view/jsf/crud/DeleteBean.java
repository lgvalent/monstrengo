package br.com.orionsoft.monstrengo.view.jsf.crud;

import java.util.HashMap;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.processes.DeleteProcess;
import br.com.orionsoft.monstrengo.view.jsf.util.FacesUtils;

/**
 * Bean que controla a p�gina de exclus�o de uma entidade Crud.
 * Par�metro de URL necess�rio: <br>
 * entityType: nome completo da classe da entidade que ser� deletada.
 * A tela pode ser chamada pelo actionDelete(), ou ainda, diretamente
 * acessando a p�gina delete1.jsp.
 * No primeiro caso, o action ir� disparar runCheckDependences();
 * No segundo caso, o m�todo isHasDependences() ir� verificar se a tela foi 
 * chamada de um link (checkLinkRequest()) para ent�o acionar o runVerifyDependence()
 * 
 * @author Andre
 * @version 20060206
 *
 * @jsf.bean name="deleteBean" scope="session"
 * @jsf.navigation from="*" result="delete1" to="/pages/basic/delete1.jsp" 
 * @jsf.navigation from="*" result="delete2" to="/pages/basic/delete2.jsp" 
 */
@ManagedBean
@SessionScoped
public class DeleteBean extends CrudBasicBean
{
	private static final long serialVersionUID = 1L;
	
	/** Define a view JSF que � ativada para a vis�o DELETE */
	public static final String FACES_VIEW_DELETE_1 = "/pages/basic/delete1?faces-redirect=true";
	public static final String FACES_VIEW_DELETE_2 = "/pages/basic/delete2?faces-redirect=true";
	
	private Map<String, String> dependencesMap = new HashMap<String, String>(0);

	@SuppressWarnings("rawtypes")
	private DeleteProcess process = null;
	public DeleteProcess<?> getProcess() throws ProcessException {
		if (process == null)
			process = (DeleteProcess<?>)this.getApplicationBean().getProcessManager().createProcessByName(DeleteProcess.PROCESS_NAME, this.getUserSessionBean().getUserSession());
		return process;
	}
	
	/*String justificando o porqu� da exclus�o da entidade*/
    private String justification;
    
    /**
     * Action que prepara a visualiza��o
     * e controla o fluxo de tela. 
     * @return
     */
    public String actionDelete() throws Exception
    {
        log.debug("::Iniciando actionDelete");
    	// Prepara os par�metros fornecidos
        this.loadEntityParams();

        /*Limpa o cache do processo ativo, para trabalhar com as entidades que foram atualizadas por outros processos*/
    	this.removeFromCache(prepareCurrentEntityKey());
        
        try{
        	/* prepara entidade corrente */ 
            log.debug("::Preparando a entidade corrente");
            prepareCurrentEntity(prepareCurrentEntityKey());
            
            /* Executa a verifica��o se tem depend�ncias */
            if(getProcess().runCheckDependences()){
            	if(getProcess().getDependencesBean().isEmpty())
            		return FACES_VIEW_DELETE_2;
            	
        		return FACES_VIEW_DELETE_1;
            }else{
            	/* Houve erro no processo */
            	FacesUtils.addErrorMsgs(getProcess().getMessageList());
                return FacesUtils.FACES_VIEW_FAILURE;
            }
            
        }catch(BusinessException e){
        	FacesUtils.addErrorMsgs(e.getErrorList());
        	/* Edi��o REJEITADA */
            return FacesUtils.FACES_VIEW_FAILURE;
        }
    }

    /**
     * Action que prepara a visualiza��o baseada
	 * nos par�metros passados.
	 * Este m�todo � usado pelos outros beans quando desejam 
	 * exibir uma entidade no browser
     * e controla o fluxo de tela. 
     * @return
     * @throws Exception 
     */
	public String actionDelete(String typeName, long id) throws Exception
	{
        log.debug("::Iniciando actionDelete");
        
  		/* Passa os par�metros para o controlador de entidade da visao */
   		this.getEntityParam().getParentParam().setTypeName(null);
   		this.getEntityParam().setTypeName(typeName);
   		this.getEntityParam().setId(id);

        /*Limpa o cache do processo ativo, para trabalhar com as entidades que foram atualizadas por outros processos*/
    	this.removeFromCache(prepareCurrentEntityKey());
        
        try{
        	/* prepara entidade corrente */ 
            log.debug("::Preparando a entidade corrente");
            prepareCurrentEntity(prepareCurrentEntityKey());
            
            /* Executa a verifica��o se tem depend�ncias */
            if(getProcess().runCheckDependences()){
            	if(getProcess().getDependencesBean().isEmpty())
            		return FACES_VIEW_DELETE_2;
            	
        		return FACES_VIEW_DELETE_1;
            }else{
            	/* Houve erro no processo */
            	FacesUtils.addErrorMsgs(getProcess().getMessageList());
                return FacesUtils.FACES_VIEW_FAILURE;
            }
            
        }catch(BusinessException e){
        	FacesUtils.addErrorMsgs(e.getErrorList());
        	/* Edi��o REJEITADA */
            return FacesUtils.FACES_VIEW_FAILURE;
        }
	}

   
    public String actionConfirm() throws Exception{
    	log.debug("::Iniciando actionUpdate");
    	log.debug("Gravando altera��es pelo processo:" + currentEntityKey);
    	DeleteProcess<?> proc = (DeleteProcess<?>) processes.get(currentEntityKey); 
    	//justification

    	proc.setJustification(this.justification);

    	if(proc.runDelete()){ 
    		/* Exibe a mensagem de sucesso */
    		FacesUtils.addInfoMsgs(proc.getMessageList());
    		
    		/* Finaliza o processo e remove ele da lista de processo ativos */
    		processes.remove(currentEntityKey);
    		proc.finish();
    		proc=null;
    		currentEntity = null;
    		
    		/* Se a entidade a ser excluida estiver na pilha de dependencias, 
    		 * prepara a entidade corrente e renderiza a tela delete1.jsp
    		 * sen�o, fecha o processo.*/
    		if(dependencesMap.containsKey(currentEntityKey)){
    			String parentKey = dependencesMap.get(currentEntityKey);
    			/* Troca o contexto da vis�o, ativando o processo que foi
    			 * armazenado sob a chave 'parentKey' que foi guardada 
    			 * no in�cio da exclus�o pela actionDeleteChild() */
    			this.setCurrentEntityKey(parentKey);
    			this.prepareCurrentEntity(parentKey);
    			
    			/* Verifica a depend�ncia novamente para for�ar que
    			 * o processo atualize a entidade e suas depend�ncias
    			 * e logo depois, atualiza a refer�ncia da entidade da
    			 * atual vis�o */
				getProcess().runCheckDependences();
				currentEntity = getProcess().retrieveEntity();
    			
    			return FACES_VIEW_DELETE_1;
    		}
    		
    		log.debug("::Fim actionConfirm(). Redirecionando para a vis�o SUCCESS.");
    		return FacesUtils.FACES_VIEW_SUCCESS;
    			

    	}else{
    		log.debug("::Fim actionConfirm(). Houveram erros, voltando para a vis�o DELETE.");
    		FacesUtils.addErrorMsgs(proc.getMessageList());
    		return "";
    	}
    }
    
    /**
     * Obtem a chave da entidade que est� currentemente sendo criada pelo bean
     * @return
     */
    public IEntity<?> getCurrentEntity() {
		return currentEntity;
	}

	/**
     * Este m�todo prepara a entidade correntemente deletada, 
     * baseando-se na chave passada.<br>
     * O mapa de processos ativos � consultado e se n�o for encontrado o processo,
     * um novo processo � criado.  
	 * @throws BusinessException 
     */
    @SuppressWarnings("unchecked")
	public void prepareCurrentEntity(String currentEntityKey) throws BusinessException, Exception{
    	
        super.prepareCurrentEntity(currentEntityKey);
        
        if(processes.containsKey(currentEntityKey)){
        	log.debug("Utilizando o processo j� ativo");
        	process = (DeleteProcess<?>) processes.get(currentEntityKey); 
        	currentEntity = getProcess().retrieveEntity();
    	}else{
        	log.debug("Iniciando um novo processo Delete");
        	process = (DeleteProcess<?>) this.getApplicationBean().getProcessManager().createProcessByName(DeleteProcess.PROCESS_NAME, this.getUserSessionBean().getUserSession());
            try{
                /* Preenche os par�metros */
            	process.setEntityType(Class.forName(this.getEntityParam().getTypeName()));
            	process.setEntityId(this.getEntityParam().getId());
                
            	/* Obtem a entidade Delete , a tentativa causar� um throw
                 * e o processo responder� com a mensage de DELETE_DENIED
                 */
            	currentEntity = process.retrieveEntity();
            	/* Coloca o processo Delete na lista de processos ativos */
            	processes.put(this.currentEntityKey, process);
	        	
            }catch(BusinessException e){
	        	log.debug("Finalizando o processo pela ocorr�ncia de erro:" + e.getMessage());
            	process.finish();
            	process = null;
            	/* Passa o erro pra frente */
            	throw e;
            }
    	}
    }
    
	/**
     * Este m�todo � respons�vel por compor a chave Delete da entidade usando
     * o tipo da entidade e o id da entidade. 
     * @return Retorna uma chave com entityType+entityId.
     */
    public String prepareCurrentEntityKey(){
    	return this.getEntityParam().getTypeName() + this.getEntityParam().getId();
    }

	public void setJustification(String justification){this.justification = justification;}
	public String getJustification(){return this.justification;}
	
	/**
	 * Este m�todo verifica apenas se existem entidades relacionadas com a entidade
	 * corrente (entidade a ser deletada), obtidas pelo metodo runVerifyDependences
	 */
	public boolean isHasDependences(){
		/* Verifica se a chamada � por meio de um Link, pois ser� necess�rio executar
		 * o runCheckDependences(), pois o actionDelete() n�o foi acionado. 
		 * Para evitar dupla execu��o em uma mesma p�gina � verificado se a lista
		 * j� possui itens */
		try {
			if(checkLinkRequest() && getProcess().getDependencesBean().isEmpty()){
				getProcess().runCheckDependences();
			}

			/*Variavel deleteProcess recebe o processo j� ativo*/
			if(getProcess().getDependencesBean().size()>0){
				return true;
			}
		} catch (ProcessException e) {
			FacesUtils.addErrorMsgs(e.getErrorList());
		}
		return false;
	}

	/**
	 * Verifica se a a��o de delete � sobre uma entidade filha que depende de um pai.
	 * Se for uma entidade filha, a armazena numa pilha a entidade que possui dependentes para 
	 * que o processo possa voltar para este e atualizar a lista de dependentes
	 */
	public String actionDeleteChild(){
		String result = "";
		try {
			/*armazena a entidade dependente e o seu pai na pilha, e depois chama o actionDelete, para decidir a vis�o*/
			String parentKey = currentEntityKey;
			result = actionDelete();
			dependencesMap.put(currentEntityKey, parentKey);
			
		} catch (Exception e) {
			FacesUtils.addErrorMsgs(MessageList.createSingleInternalError(e));
			result = FacesUtils.FACES_VIEW_FAILURE;
		}
		return result;
	}
	
	/**
	 * Verifica se a a��o de delete � sobre uma entidade filha que depende de um pai.
	 * Se for uma entidade filha, a armazena numa pilha a entidade que possui dependentes para 
	 * que o processo possa voltar para este e atualizar a lista de dependentes
	 */
	public String actionDeleteChilds(){
		String result = "";
		try {
	        log.debug("::Iniciando actionDeleteChilds");

	        // Prepara os par�metros fornecidos
	        this.loadEntityParams();

	    	process.setJustification(this.justification);
	    	
	    	if(!process.runDeleteDependencesChecked()){
				FacesUtils.addErrorMsgs(process.getMessageList());
				result = FacesUtils.FACES_VIEW_FAILURE;
	    	}else{
	    		/* Re-processa o checkDependences() e desvia para a tela inicial */
	    		process.runCheckDependences();
	    		result = "";
	    	}
		} catch (Exception e) {
			FacesUtils.addErrorMsgs(MessageList.createSingleInternalError(e));
			result = FacesUtils.FACES_VIEW_FAILURE;
		}
		return result;
	}
	
	/**
	 * Se o processo for cancelado, limpa o mapa de dependencias
	 * @throws Exception 
	 */
	public String actionCancel() throws Exception{
		dependencesMap.remove(currentEntityKey);
		return super.actionCancel();
	}
}