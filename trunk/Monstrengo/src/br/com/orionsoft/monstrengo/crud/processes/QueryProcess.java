package br.com.orionsoft.monstrengo.crud.processes;

import br.com.orionsoft.monstrengo.crud.processes.SelectParam;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.process.ProcessBasic;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.crud.report.entities.UserReport;

/**
 * Este processo controla a visualização de uma entidade do sistema.
 * Controlando também as permissões.
 * 
 * <p><b>Procedimentos:</b>
 * <br>Definir o tipo da entidade: <i>setEntityType(Class)</i>
 * <br>Definir o id da entidade: <i>setEntityId(long)</i>
 * <br>Verificar se a entidade pode ser visualizada: <i>boolean mayView()</i>
 * <br>Obter a entidade por <i>(IEntity) retrieveEntity()</i>.
 * 
 * @author 
 * @version 20060308
 *
 * @spring.bean id="QueryProcess" init-method="start" destroy-method="finish" singleton="false"
 * @spring.property name="processManager" ref="ProcessManager"
 *
 */
public class QueryProcess extends ProcessBasic
{
    public static final String PROCESS_NAME = "QueryProcess";
    
    private UserReport userReport;
    
    private Class<?> entityType;

	private SelectParam selectParam = new SelectParam();

    public String getProcessName(){return PROCESS_NAME;}

    public SelectParam getSelectParam() {return selectParam;}

	public UserReport getUserReport() throws ProcessException{
		if(userReport==null)
			try {
				/* Verifica se existe uma entityType definida */
				if (entityType == null)
			        throw new ProcessException(MessageList.create(UserReport.class, "ENTITY_NOT_DEFINED", this.getProcessManager().getServiceManager().getEntityManager().getEntityMetadata(this.entityType).getLabel()));
				
				userReport = new UserReport(this.getProcessManager().getServiceManager().getEntityManager(), this.entityType, this.getUserSession().getUser());
			} catch (BusinessException e) {
				throw new ProcessException(e.getErrorList());
			}
		
		return userReport;
	}

	public Class<?> getEntityType(){return entityType;}
	public void setEntityType(Class<?> entityType) throws ProcessException{
		this.entityType = entityType;
		/* Se trocar a entidade, o relatório anterior será descartado */
		this.userReport = null;
	}
	
	public boolean runQuery(){
		super.beforeRun();

		try
		{
			this.getUserReport().runQuery();

			return true;
		} catch (ProcessException e)
		{
			this.getMessageList().addAll(e.getErrorList());
			return false;
		}
	}

}