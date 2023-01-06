package br.com.orionsoft.monstrengo.view.jsf;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.processes.UpdateProcess;
import br.com.orionsoft.monstrengo.view.jsf.bean.BeanSessionBasic;
import br.com.orionsoft.monstrengo.view.jsf.crud.RetrieveBean;
import br.com.orionsoft.monstrengo.view.jsf.crud.UpdateBean;
import br.com.orionsoft.monstrengo.view.jsf.util.FacesUtils;


/**
 * Bean que controla a página de edição de uma entidade Crud.
 * 
 * @author Lucio
 * @version 20060115
 *
 */
public class EditBasicBean extends BeanSessionBasic
{
	private static final long serialVersionUID = 1L;

	/* Dados internos */
    /** Armazena os processos das entidades que estão atualmente em edição */
    @SuppressWarnings("rawtypes")
	private UpdateProcess process = null;
    private IEntity<?> entity=null;
    
    // Outros Beans que são utilizados
    private EntityBean entityBean;
    
    /**
     * Prepara os parâmetros que foram fornecidos e estão disponiveis no Faces.
     *  
     * @throws Exception
     */
    public void loadEntityParams() throws Exception
    {
        log.debug("Lendo parâmetros para o editBean");
        // Não possui parâmetros próprios.
        
        // Solicita que seu filho prepare os parâmetros
        this.entityBean.loadEntityParams();
    }
  
    /**
     * Action que prepara a visualização
     * e controla o fluxo de tela. 
     * @return
     */
    public String actionEdit() throws Exception
    {
        log.debug("::Iniciando actionEdit");
    	// Prepara os parâmetros fornecidos
        this.loadEntityParams();
        
        try{
            /* prepara entidade corrente */ 
            log.debug("::Preparando a entidade corrente");
            prepareCurrentEntity();
        }catch(ProcessException e){
        	FacesUtils.addErrorMsgs(e.getErrorList());
        	/* Edição REJEITADA */
            return FacesUtils.FACES_VIEW_FAILURE;
        }
        // Redireciona a Edit
        log.debug("::Fim actionEdit");
        return UpdateBean.FACES_VIEW_UPDATE;
    }
    
    /**
     * 
     * @return
     */
    public String actionUpdate() throws Exception{
    
        log.debug("::Iniciando actionUpdate");
    	if(process.runUpdate()){ 
    		/* Exibe a mensagem de sucesso */
    		FacesUtils.addInfoMsgs(process.getMessageList());

    		log.debug("Recarregando a nova entidade para ser mostrada na VIEW");
    		entityBean.setTypeName(process.getEntityType().getName());
    		entityBean.setId(process.getEntityId());
    		entityBean.doReload();
    		
    		/* Finaliza o processo e remove ele da lista de processo ativos */
    		process.finish();
    		process = null;
    		entity = null;
    		
    		log.debug("::Fim actionSave(). Redirecionando para a visão VIEW.");
        	return RetrieveBean.FACES_VIEW_RETRIEVE;
    	}else{
    		log.debug("::Fim actionSave(). Hoveram erros, voltando para a visão EDIT.");
    		FacesUtils.addErrorMsgs(process.getMessageList());
    		return "";
    	}
    }
    
    public EntityBean getEntityBean(){return entityBean;}
    public void setEntityBean(EntityBean entityBean){this.entityBean = entityBean;}
 
    
    /**
     * Obtem a entidade atualmente em edicao
     * @return
     * @throws Exception 
     * @throws  
     */
    public IEntity<?> getEntity() throws Exception {
    	/* Se a visao for chamada por URL
    	 * o metodo actionEdit nao eh executado. Assim, 
    	 * Quando tentar pegar a primeira vez a entity e ela
    	 * nao estiver pronta. o Bean irah preparar 
    	 */
    	if(entity==null){
    		actionEdit();
    	}
		
    	return entity;
	}
    /**
     * Define a chave da entidade que está correntemente sendo editada pelo bean.<br>
     * A cada definição desta chave. O processo responsável pela entidade é
     * localizado e a entidade corrente é obtida. 
     * @return
     */
	public void setEntity(IEntity<?> currentEntity) {
		this.entity = currentEntity;
	}

	/**
     * Este método prepara a entidade correntemente em edição, 
     * baseando-se nos parametros capturados pelo EntityBean.<br>
	 * @throws BusinessException 
     */
    @SuppressWarnings("unchecked")
	private void prepareCurrentEntity() throws BusinessException, Exception{
    	
        if(log.isDebugEnabled())
        	log.debug("prepareCurrentEntity:");
    
        if (process != null){
        	log.debug("Utilizando o processo já ativo");
        	entity = process.retrieveEntity();
    	}else{
        	log.debug("Iniciando um novo processo de Edição");
        	process = (UpdateProcess<?>)this.getApplicationBean().getProcessManager().createProcessByName(UpdateProcess.PROCESS_NAME, this.getUserSessionBean().getUserSession());
            
        	/* Preenche os parâmetros */
        	process.setEntityType(entityBean.getEntity().getInfo().getType());
            process.setEntityId(entityBean.getEntity().getId());
            
            try{
                /* Obtem a entidade de edição , a tentativa causará um throw
                 * e o processo responderá com a mensage de UPDATE_DENIED
                 */
            	entity = process.retrieveEntity();
            	/* Coloca o processo de edição como processo ativo */
	        	log.debug("Novo processo criado com sucesso");
            }catch(BusinessException e){
	        	log.debug("Finalizando o processo pela ocorrência de algum erro");
            	process.finish();
            	process = null;
            	/* Passa o erro pra frente */
            	throw e;
            }
            
    	}
    }

	public void doReset() throws BusinessException, Exception {
		// TODO Auto-generated method stub
		
	}

	public void doReload() throws BusinessException, Exception {
		// TODO Auto-generated method stub
		
	}
    
}