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
 * Bean que controla a página de exclusão de uma entidade Crud.
 * Parâmetro de URL necessário: <br>
 * entityType: nome completo da classe da entidade que será deletada.
 * A tela pode ser chamada pelo actionDelete(), ou ainda, diretamente
 * acessando a página delete1.jsp.
 * No primeiro caso, o action irá disparar runCheckDependences();
 * No segundo caso, o método isHasDependences() irá verificar se a tela foi 
 * chamada de um link (checkLinkRequest()) para então acionar o runVerifyDependence()
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
	
	/** Define a view JSF que é ativada para a visão DELETE */
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
	
	/*String justificando o porquê da exclusão da entidade*/
    private String justification;
    
    /**
     * Action que prepara a visualização
     * e controla o fluxo de tela. 
     * @return
     */
    public String actionDelete() throws Exception
    {
        log.debug("::Iniciando actionDelete");
    	// Prepara os parâmetros fornecidos
        this.loadEntityParams();

        /*Limpa o cache do processo ativo, para trabalhar com as entidades que foram atualizadas por outros processos*/
    	this.removeFromCache(prepareCurrentEntityKey());
        
        try{
        	/* prepara entidade corrente */ 
            log.debug("::Preparando a entidade corrente");
            prepareCurrentEntity(prepareCurrentEntityKey());
            
            /* Executa a verificação se tem dependências */
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
        	/* Edição REJEITADA */
            return FacesUtils.FACES_VIEW_FAILURE;
        }
    }

    /**
     * Action que prepara a visualização baseada
	 * nos parâmetros passados.
	 * Este método é usado pelos outros beans quando desejam 
	 * exibir uma entidade no browser
     * e controla o fluxo de tela. 
     * @return
     * @throws Exception 
     */
	public String actionDelete(String typeName, long id) throws Exception
	{
        log.debug("::Iniciando actionDelete");
        
  		/* Passa os parâmetros para o controlador de entidade da visao */
   		this.getEntityParam().getParentParam().setTypeName(null);
   		this.getEntityParam().setTypeName(typeName);
   		this.getEntityParam().setId(id);

        /*Limpa o cache do processo ativo, para trabalhar com as entidades que foram atualizadas por outros processos*/
    	this.removeFromCache(prepareCurrentEntityKey());
        
        try{
        	/* prepara entidade corrente */ 
            log.debug("::Preparando a entidade corrente");
            prepareCurrentEntity(prepareCurrentEntityKey());
            
            /* Executa a verificação se tem dependências */
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
        	/* Edição REJEITADA */
            return FacesUtils.FACES_VIEW_FAILURE;
        }
	}

   
    public String actionConfirm() throws Exception{
    	log.debug("::Iniciando actionUpdate");
    	log.debug("Gravando alterações pelo processo:" + currentEntityKey);
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
    		 * senão, fecha o processo.*/
    		if(dependencesMap.containsKey(currentEntityKey)){
    			String parentKey = dependencesMap.get(currentEntityKey);
    			/* Troca o contexto da visão, ativando o processo que foi
    			 * armazenado sob a chave 'parentKey' que foi guardada 
    			 * no início da exclusão pela actionDeleteChild() */
    			this.setCurrentEntityKey(parentKey);
    			this.prepareCurrentEntity(parentKey);
    			
    			/* Verifica a dependência novamente para forçar que
    			 * o processo atualize a entidade e suas dependências
    			 * e logo depois, atualiza a referência da entidade da
    			 * atual visão */
				getProcess().runCheckDependences();
				currentEntity = getProcess().retrieveEntity();
    			
    			return FACES_VIEW_DELETE_1;
    		}
    		
    		log.debug("::Fim actionConfirm(). Redirecionando para a visão SUCCESS.");
    		return FacesUtils.FACES_VIEW_SUCCESS;
    			

    	}else{
    		log.debug("::Fim actionConfirm(). Houveram erros, voltando para a visão DELETE.");
    		FacesUtils.addErrorMsgs(proc.getMessageList());
    		return "";
    	}
    }
    
    /**
     * Obtem a chave da entidade que está currentemente sendo criada pelo bean
     * @return
     */
    public IEntity<?> getCurrentEntity() {
		return currentEntity;
	}

	/**
     * Este método prepara a entidade correntemente deletada, 
     * baseando-se na chave passada.<br>
     * O mapa de processos ativos é consultado e se não for encontrado o processo,
     * um novo processo é criado.  
	 * @throws BusinessException 
     */
    @SuppressWarnings("unchecked")
	public void prepareCurrentEntity(String currentEntityKey) throws BusinessException, Exception{
    	
        super.prepareCurrentEntity(currentEntityKey);
        
        if(processes.containsKey(currentEntityKey)){
        	log.debug("Utilizando o processo já ativo");
        	process = (DeleteProcess<?>) processes.get(currentEntityKey); 
        	currentEntity = getProcess().retrieveEntity();
    	}else{
        	log.debug("Iniciando um novo processo Delete");
        	process = (DeleteProcess<?>) this.getApplicationBean().getProcessManager().createProcessByName(DeleteProcess.PROCESS_NAME, this.getUserSessionBean().getUserSession());
            try{
                /* Preenche os parâmetros */
            	process.setEntityType(Class.forName(this.getEntityParam().getTypeName()));
            	process.setEntityId(this.getEntityParam().getId());
                
            	/* Obtem a entidade Delete , a tentativa causará um throw
                 * e o processo responderá com a mensage de DELETE_DENIED
                 */
            	currentEntity = process.retrieveEntity();
            	/* Coloca o processo Delete na lista de processos ativos */
            	processes.put(this.currentEntityKey, process);
	        	
            }catch(BusinessException e){
	        	log.debug("Finalizando o processo pela ocorrência de erro:" + e.getMessage());
            	process.finish();
            	process = null;
            	/* Passa o erro pra frente */
            	throw e;
            }
    	}
    }
    
	/**
     * Este método é responsável por compor a chave Delete da entidade usando
     * o tipo da entidade e o id da entidade. 
     * @return Retorna uma chave com entityType+entityId.
     */
    public String prepareCurrentEntityKey(){
    	return this.getEntityParam().getTypeName() + this.getEntityParam().getId();
    }

	public void setJustification(String justification){this.justification = justification;}
	public String getJustification(){return this.justification;}
	
	/**
	 * Este método verifica apenas se existem entidades relacionadas com a entidade
	 * corrente (entidade a ser deletada), obtidas pelo metodo runVerifyDependences
	 */
	public boolean isHasDependences(){
		/* Verifica se a chamada é por meio de um Link, pois será necessário executar
		 * o runCheckDependences(), pois o actionDelete() não foi acionado. 
		 * Para evitar dupla execução em uma mesma página é verificado se a lista
		 * já possui itens */
		try {
			if(checkLinkRequest() && getProcess().getDependencesBean().isEmpty()){
				getProcess().runCheckDependences();
			}

			/*Variavel deleteProcess recebe o processo já ativo*/
			if(getProcess().getDependencesBean().size()>0){
				return true;
			}
		} catch (ProcessException e) {
			FacesUtils.addErrorMsgs(e.getErrorList());
		}
		return false;
	}

	/**
	 * Verifica se a ação de delete é sobre uma entidade filha que depende de um pai.
	 * Se for uma entidade filha, a armazena numa pilha a entidade que possui dependentes para 
	 * que o processo possa voltar para este e atualizar a lista de dependentes
	 */
	public String actionDeleteChild(){
		String result = "";
		try {
			/*armazena a entidade dependente e o seu pai na pilha, e depois chama o actionDelete, para decidir a visão*/
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
	 * Verifica se a ação de delete é sobre uma entidade filha que depende de um pai.
	 * Se for uma entidade filha, a armazena numa pilha a entidade que possui dependentes para 
	 * que o processo possa voltar para este e atualizar a lista de dependentes
	 */
	public String actionDeleteChilds(){
		String result = "";
		try {
	        log.debug("::Iniciando actionDeleteChilds");

	        // Prepara os parâmetros fornecidos
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