package br.com.orionsoft.monstrengo.crud.processes;

import java.util.ArrayList;
import java.util.List;

import br.com.orionsoft.monstrengo.crud.processes.DeleteProcess;
import br.com.orionsoft.monstrengo.auditorship.services.UtilsAuditorship;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.process.ProcessBasic;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.services.CheckDependencesEntityService;
import br.com.orionsoft.monstrengo.crud.services.DeleteService;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.crud.services.CheckDependencesEntityService.DependencesBean;
import br.com.orionsoft.monstrengo.security.services.UtilsSecurity;

/**
 * Este processo controla a remo��o de uma entidade do sistema.
 * 
 * <p><b>Procedimentos:</b>
 * <br>Definir o tipo da entidade: <i>setEntityType(Class)</i>
 * <br>Definir o id da entidade: <i>setEntityId(long)</i>
 * <br>Verificar se a entidade pode ser deletada: <i>boolean mayDelete()</i>
 * <br>Obter a entidade por <i>(IEntity) retrieveEntity()</i>.
 * <li>Realizar edi��es pela interface com o usu�rio.
 * <br>Gravar as altera��es por <i>runDelete()</i>.
 * 
 * @author Andre 2006/02/03
 * @version 20070810 Juliana
 *
 * @spring.bean id="DeleteProcess" init-method="start" destroy-method="finish" singleton="false"
 * @spring.property name="processManager" ref="ProcessManager"
 *
 */
public class DeleteProcess <T> extends ProcessBasic
{
    public static final String PROCESS_NAME = "DeleteProcess";

    private Class<T> entityType;
    private long entityId = IDAO.ENTITY_UNSAVED;
    private IEntity<T> entity=null;
    private String justification = "";
    private List<DependencesBean> dependencesBean = new ArrayList<DependencesBean>();
    
    // Auditoria
    //private EntityAuditValue entityAuditValue;
    
    public String getProcessName(){return PROCESS_NAME;}
    
    public boolean runDelete(){
    	super.beforeRun();
        try{
            if((this.justification.equals("")) || (this.justification.length() < 5))
                throw new ProcessException(MessageList.create(DeleteProcess.class, "JUSTIFICATION_FAILURE", this.justification));
            
            String justification = this.justification + ":'"+ this.retrieveEntity().toString() + "'";  
            
            /*
             *  Executa a valida��o ap�s a confirma��o da exclus�o da entidade.
             */
            if(this.getProcessManager().getServiceManager().getEntityManager().getDvoManager().contains(entity))
            	   this.getProcessManager().getServiceManager().getEntityManager().getDvoManager().getDvoByEntity(entity).afterDelete(entity, this.getUserSession(), null); 

            //Deleta a entidade
            UtilsCrud.delete(this.getProcessManager().getServiceManager(),
                    this.retrieveEntity(), 
                    null);
            
            // Registra a auditoria da atualiza��o
            UtilsAuditorship.auditDelete(this.getProcessManager().getServiceManager(), 
                    this.getUserSession(),
                    entityType,
                    entityId,
                    justification, null);
            
            // Grava a mensagem de sucesso
            this.getMessageList().add(new BusinessMessage(BusinessMessage.TYPE_INFO, DeleteProcess.class, "DELETE_SUCCESS", this.retrieveEntity().getInfo().getLabel() + ":" + this.retrieveEntity().toString()));
            
            // Libera a entidade atual
            entity = null;
            return true;
        }
        catch(BusinessException e)
        {
            // Gravando a mensagem de falha
            e.getErrorList().add(new BusinessMessage(DeleteProcess.class, "DELETE_FAILURE", this.entity.getInfo().getLabel() + ":" + this.entity.toString()));
            // Armazenando a lista de erros;
            this.getMessageList().addAll(e.getErrorList());
            return false;
        }
    }

    // Verifica se o user tem permiss�o de edi��o.
    public boolean mayDelete() throws BusinessException
    {
        return UtilsSecurity.checkRightDelete(this.getProcessManager().getServiceManager(), this.entityType, this.getUserSession(), null);
    }
    
    /**
     * Obtem a entidade persistida baseado no Tipo e Id fornecidos.
     * Verifica se a edi��o ser� poss�vel, sen�o lan�a uma exce��o.  
     * Se a entidade ainda n�o foi obtidade pelo processo, o servi�o
     * � executado e os valores da auditoria s�o preparados.
     * Caso a entidade j� esteja preparada, ela � retornada. 
     * @return Uma entidade pronta para a edi��o.
     * @throws BusinessException
     */
    public IEntity<T> retrieveEntity() throws BusinessException
    {
        // Verificar se pode editar a entidade
        if (this.mayDelete())
        {    
            // Se ainda n�o est� pronta a entidade, prepara-a
            if (entity == null)
            {
            	// Cria uma entidade tempor�ria para ser validade pelo dvo
            	IEntity<T> entityTemp = UtilsCrud.retrieve(this.getProcessManager().getServiceManager(),
                                            entityType,
                                            entityId, 
                                            null);
            	
            	// Se houver algum problema, uma exce��o ser� levantada aqui
            	if(this.getProcessManager().getServiceManager().getEntityManager().getDvoManager().contains(entityTemp))
            		this.getProcessManager().getServiceManager().getEntityManager().getDvoManager().getDvoByEntity(entityTemp).beforeDelete(entityTemp, this.getUserSession(), null);
            	
            	// Se nada aconteceu, � porque est� tudo OK e a entidade poder� prosseguir para sua exclus�o.
            	entity = entityTemp;
            }
            
            return entity;
        }

        // N�o possui direitos de editar este tipo de entidade
        throw new ProcessException(MessageList.create(DeleteProcess.class, "DELETE_DENIED", getUserSession().getUserLogin(), this.getProcessManager().getServiceManager().getEntityManager().getEntityMetadata(this.entityType).getLabel() + ":" + this.entity.toString()));
    }
    
    /**
     * Verifica se a entidade a ser deletada afeta outras entidades que 
     * a possuem como uma propriedade. Isto �, se existem outras entidades dependentes
     * (restri��o de chave estrangeira) que devem ser excluidas primeiro
     * 
     * @return true, se existe a estrutura (DependecesBean)que cont�m a entidade afetada, 
     * a propriedade pela qual esta entidade se relaciona, e a lista de entidades que 
     * est�o ligadas a essa propriedade . 
     * 
     * @version 20070207
     */
    public boolean runCheckDependences(){
    	super.beforeRun();
    	
    	try {
    		/*For�a a recarga da entidade para limpar as refer�ncias de seus dependentes
    		 * que foram exclu�dos */
    		this.entity = null;

    		/*limpa a lista de dependentes*/
    		dependencesBean.clear();
    		ServiceData sdCheckDependences = new ServiceData(CheckDependencesEntityService.SERVICE_NAME, null);
    		sdCheckDependences.getArgumentList().setProperty(CheckDependencesEntityService.IN_ENTITY, this.retrieveEntity());
    		
    		super.getProcessManager().getServiceManager().execute(sdCheckDependences);
    		
    		/* Popula a lista de entidades dependentes (dependencesBean) se o servi�o retornar 
    		 * um resultado diferente de vazio*/
    		if(!sdCheckDependences.getOutputData().isEmpty()){
    			for(Object obj:sdCheckDependences.getOutputData()){
    				DependencesBean bean = (DependencesBean) obj;
    				dependencesBean.add(bean);
    			}
    			log.debug("&&& runCheckDependences => encontrados " + dependencesBean.size() + " entidades");
    		}
    		/*Adiciona mensagem avisando que existem outras entidades dependentes que 
    		 * devem ser excluidas primeiro, para depois deletar a entidade selecionada*/
    		this.getMessageList().add(new BusinessMessage(BusinessMessage.TYPE_INFO, DeleteService.class, "ERROR_DELETE_FOREING", this.entity.getInfo().getLabel() + ":" + this.entity.toString()));
    		return true;
    	} catch (BusinessException e) {
    		this.getMessageList().add(e.getErrorList());
    		return false;
    	}
    }

	/**
     * Justificativa para a Auditoria, do motivo da exclus�o da entidade
     */
    public String getJustification(){return justification;}
    public void setJustification(String justification){this.justification = justification;}
    
    public long getEntityId(){return entityId;}
    public void setEntityId(long entityId){this.entityId = entityId;}

    public Class<T> getEntityType(){return entityType;}
    public void setEntityType(Class<T> entityType){this.entityType = entityType;}

    /**
     * Estrutura do CheckDependencesEntityService que contem as entidades e 
     * suas respectivas propriedades que referenciam a entidade a ser excluida
     * @since 20070207
     */
	public List<DependencesBean> getDependencesBean() {return dependencesBean;}
	
    /**
     * Este m�todo percorre a lista de depend�ncias e TENTA exluir 
     * as dependencias marcadas.
     * @return true, se deu tudo certo e excluiu tudo, false se alguma dependencia n�o foi excluida.  
     * @version 20071407
     */
    @SuppressWarnings("unchecked")
	public boolean runDeleteDependencesChecked(){
    	super.beforeRun();

		List<DeleteProcess<T>> processes = new ArrayList<DeleteProcess<T>>();
    	try {
        	/* Valida a justificativa */
    		if((this.justification.equals("")) || (this.justification.length() < 5))
                throw new ProcessException(MessageList.create(DeleteProcess.class, "JUSTIFICATION_FAILURE", this.justification));
    		
    		/* Primeiro percorre todas as dependencias marcadas 
    		 * criando seus processos de remo��o e verificando
    		 * se elas n�o possuem dependencias */
    		for(DependencesBean dependence: this.dependencesBean){
    			for(IEntity<?> entity: dependence.getEntityList()){
    				if(entity.isSelected()){
    					DeleteProcess<T> process = (DeleteProcess<T>) this.getProcessManager().createProcessByName(PROCESS_NAME, this.getUserSession());
    					process.setEntityType((Class<T>) entity.getInfo().getType());
    					process.setEntityId(entity.getId());
    					process.setJustification(this.justification);
    					
    					/* Verifica se algum erro ocorreu e passa pra frente */
    					if(!process.runCheckDependences()){
    						this.getMessageList().addAll(process.getMessageList());
    						return false;
    					}
    					
    					/* Verifica se h� dependencias que n�o poder�o ser excluidas por ter dependencia */
    					if(process.getDependencesBean().size()>0){
    						this.getMessageList().add(new BusinessMessage(DeleteProcess.class, "DEPENDENCE_HAS_DEPENDENCE", process.retrieveEntity().getInfo().getLabel(), process.retrieveEntity()));
    					}
    					
    					/* Coloca o processo na lista de processos finais que ser�o 
    					 * executados se tudo der certo ou finalizados */
   						processes.add(process);
    				}
    			}
    		}
    		
    		/* Verifica se alguma dependencia possui outras dependencias e
    		 * ocorreu alguns erros na prepara��o dos processos de remo��o */
    		if(!this.getMessageList().isTransactionSuccess())
    			return false;
    		
    		/* Se deu tudo certo. Ent�o executa as exclusoes */
    		for(DeleteProcess<T> process: processes)
    			if(!process.runDelete()){
    				this.getMessageList().addAll(process.getMessageList());
    				return false;
    			}else{
    				this.getMessageList().addAll(process.getMessageList());
    			}
    		
    		return true;
    	} catch (BusinessException e) {
    		this.getMessageList().add(e.getErrorList());
    		return false;
    	}finally{
    		/* Ao final de tudo, Finaliza os processos */
    		for(DeleteProcess<T> process: processes)
				try {
					process.finish();
				} catch (ProcessException e) {
					throw new RuntimeException(e.getMessage());
				}
    	}
    }
}
