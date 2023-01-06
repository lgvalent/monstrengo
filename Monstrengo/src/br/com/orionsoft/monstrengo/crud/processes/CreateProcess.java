package br.com.orionsoft.monstrengo.crud.processes;

import br.com.orionsoft.monstrengo.crud.processes.CreateProcess;
import br.com.orionsoft.monstrengo.auditorship.services.UtilsAuditorship;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.process.ProcessBasic;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.services.CreateService;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.security.services.UtilsSecurity;

/**
 * Este processo controla a criação de uma entidade do sistema.
 * 
 * <p><b>Procedimentos:</b>
 * <br>Definir o tipo da entidade: <i>setEntityType(Class)</i>
 * <br>Verificar se a entidade pode ser criada: <i>boolean mayCreate()</i>
 * <br>Obter a entidade por <i>(IEntity) retrieveEntity()</i>.
 * <li>Realizar edições pela interface com o usuário.
 * <br>Gravar as alterações por <i>runUpdate()</i>.
 * 
 * @author Juliana e Tatiana 20060203
 * @version 20060203
 *
 * @spring.bean id="CreateProcess" init-method="start" destroy-method="finish" singleton="false"
 * @spring.property name="processManager" ref="ProcessManager"
 */
public class CreateProcess <T> extends ProcessBasic {
    
	public static final String PROCESS_NAME = "CreateProcess";

    private Class<T> entityType;
    
    private long entityCopyId = IDAO.ENTITY_UNSAVED;
    
    private IEntity<T> entity = null;
    
    public String getProcessName(){return PROCESS_NAME;}
    
    public boolean runUpdate(){
    	super.beforeRun();

    	try{
    		if(this.runValidate()){
    			// Grava a entidade
    			UtilsCrud.update(this.getProcessManager().getServiceManager(),
    					entity, 
    					null);

    			// Registra a auditoria da criação
    			UtilsAuditorship.auditCreate(this.getProcessManager().getServiceManager(), 
    					this.getUserSession(),
    					entity,
    					null);

    			// Grava a mensagem de sucesso
    			this.getMessageList().add(new BusinessMessage(BusinessMessage.TYPE_INFO, CreateProcess.class, "CREATE_SUCCESS", this.entity.getInfo().getLabel() + ":" + this.entity.toString()));

    			/* Não Libera a entidade atual pois precisa pegar qual
    			 * foi o id atribuído */
    			//            entity = null;
    			return true;
    		}

    		return false;
    	}
    	catch(BusinessException e){
    		// Gravando a mensagem de falha
    		e.getErrorList().add(new BusinessMessage(CreateProcess.class, "CREATE_FAILURE", this.entity.getInfo().getLabel() + ": " + this.entity.toString()));
    		// Armazenando a lista de erros;
    		this.getMessageList().addAll(e.getErrorList());
    		return false;
    	}
    }

    // Verifica se o user tem permissão de criação.
    public boolean mayCreate() throws BusinessException
    {
        return UtilsSecurity.checkRightCreate(this.getProcessManager().getServiceManager(), this.entityType, this.getUserSession(), null);
    }
    
    /**
     * Obtem a entidade persistida baseado no Tipo e Id fornecidos.
     * Verifica se a criação será possível, senão lança uma exceção.  
     * Se a entidade ainda não foi obtidade pelo processo, o serviço
     * é excutado.
     * Caso a entidade já esteja preparada, ela é retornada. 
     * @return Uma entidade pronta para a edição.
     * @throws BusinessException
     */
    public IEntity<T> retrieveEntity() throws BusinessException{
        // Verificar se pode editar a entidade
        if (this.mayCreate()){    
            // Se ainda não está pronta a entidade, prepara-a
            if (entity == null){
            	ServiceData sdC = new ServiceData(CreateService.SERVICE_NAME, null);
                sdC.getArgumentList().setProperty(CreateService.IN_ENTITY_TYPE, entityType);
                
                if(entityCopyId != IDAO.ENTITY_UNSAVED)
                	sdC.getArgumentList().setProperty(CreateService.IN_ENTITY_COPY_ID_OPT, entityCopyId);

                this.getProcessManager().getServiceManager().execute(sdC);

            	// Cria uma entidade temporária para ser validade pelo dvo
            	IEntity<T> entityTemp = sdC.getFirstOutput();
            	
            	// Se houver algum problema, uma exceção será levantada aqui
            	if(this.getProcessManager().getServiceManager().getEntityManager().getDvoManager().contains(entityTemp))
                    	this.getProcessManager().getServiceManager().getEntityManager().getDvoManager().getDvoByEntity(entityTemp).beforeCreate(entityTemp, this.getUserSession(), null);

            	// Se nada aconteceu, é porque está tudo OK e a entidade poderá prosseguir para sua exclusão.
            	entity = entityTemp;
                
            }
            
            return entity;
        }

        // Não possui direitos de editar este tipo de entidade
        throw new ProcessException(MessageList.create(CreateProcess.class, "CREATE_DENIED", getUserSession().getUserLogin(), this.getProcessManager().getServiceManager().getEntityManager().getEntityMetadata(this.entityType).getLabel()));
        
    }
    
    public Class<?> getEntityType(){
        return entityType;
    }
    
    public void setEntityType(Class<T> entityType){
        this.entityType = entityType;
    }

	/**
	 * Este método anula a atual instância da entidade do processo e recarrega novamente
	 * sem as alterações realizadas até aqui.
	 * @return
	 * @throws BusinessException 
	 * @since 20060322
	 */
	public void runReload() throws BusinessException{
        super.beforeRun();

        /* Anula a atual instância */
		entity = null;
		/* Recarrega */
		retrieveEntity();
	}

    /**
     * Este método permite executar uma validação no DVO antes de 
     * confirmar propriamente o update. Assim, o operador poderá
     * verificar possíveis erros de validação sem confirmar um Update 
     * @return
     * @throws BusinessException 
     * @since 20080113
     */
    public boolean runValidate(){
        super.beforeRun();
        
        // solicitando a validação da entidade pelo DvoManager
        try {
			if(this.getProcessManager().getServiceManager().getEntityManager().getDvoManager().contains(entity)){
				this.getProcessManager().getServiceManager().getEntityManager().getDvoManager().getDvoByEntity(entity).afterCreate(entity, this.getUserSession(), null);

				this.getProcessManager().getServiceManager().getEntityManager().getDvoManager().getDvoByEntity(entity).afterUpdate(entity, this.getUserSession(), null);
			}
		} catch (BusinessException e) {
			super.getMessageList().addAll(e.getErrorList());
	    	return false;
		}

    	return true;
    }
	
	public long getEntityCopyId(){
			return entityCopyId;
	}
	public void setEntityCopyId(long entityCopyId){
			this.entityCopyId = entityCopyId;
	}
    
}
