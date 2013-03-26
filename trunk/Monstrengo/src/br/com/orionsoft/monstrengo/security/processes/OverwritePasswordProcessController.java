package br.com.orionsoft.monstrengo.security.processes;

import br.com.orionsoft.monstrengo.security.processes.OverwritePasswordProcess;
import br.com.orionsoft.monstrengo.security.processes.OverwritePasswordProcessController;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.process.IProcess;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.core.process.RunnableEntityProcessControllerBasic;
import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;

/**
 * Este controlador define as entidades que são compatíveis com o processo 
 * de alteração de senha de um operador.
 * 
 * @author Lucio 
 * @version 20070917
 * 
 * @spring.bean id="OverwritePasswordProcessController" init-method="registerController"
 * @spring.property name="processManager" ref="ProcessManager"
 */
public class OverwritePasswordProcessController extends RunnableEntityProcessControllerBasic
{
    /** Informa para o gerenciador quais as entidades que são compatíveis com este controlador */
	public static final Class<?>[] RUNNABLE_ENTITIES = {ApplicationUser.class};
	public Class<?>[] getRunnableEntities() {return RUNNABLE_ENTITIES;}

	public Class<? extends IProcess> getProcessClass() {return OverwritePasswordProcess.class;}
	
	/**
	 * @throws EntityException 
	 * @see Consulte {@link IRunnableEntityProcessController.canRunWithEntity(IEntity)}
	 */
	public boolean canRunWithEntity(IEntity entity) throws ProcessException {
		/* Limpa a atual mensagem do controlador */
		this.setMessage(null);

		try{
			/* Verifica se a entidade é compatível */
			if(entity.getInfo().getType() == ApplicationUser.class){
				/* Verifica se o operador está ATIVO */
				if(entity.getProperty(ApplicationUser.INACTIVE).getValue().getAsBoolean()){
					
					this.setMessage(new BusinessMessage(OverwritePasswordProcessController.class, "INACTIVE_USER"));
					
					return false;
				}
				
		
			}	
		}catch(BusinessException e){
			throw new ProcessException(e.getErrorList());
		}
		
		return true;	
	}


}
