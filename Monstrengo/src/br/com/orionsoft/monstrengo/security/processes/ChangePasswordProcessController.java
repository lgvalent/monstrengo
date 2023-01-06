package br.com.orionsoft.monstrengo.security.processes;

import br.com.orionsoft.monstrengo.core.process.IProcess;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.core.process.RunnableProcessControllerBasic;
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
public class ChangePasswordProcessController extends RunnableProcessControllerBasic
{
    /** Informa para o gerenciador quais as entidades que são compatíveis com este controlador */
	public static final Class<?>[] RUNNABLE_ENTITIES = {ApplicationUser.class};
	public Class<?>[] getRunnableEntities() {return RUNNABLE_ENTITIES;}

	public Class<? extends IProcess> getProcessClass() {return ChangePasswordProcess.class;}

	@Override
	public boolean canRunWithEntity(IEntity<?> entity) throws ProcessException {
		// TODO Auto-generated method stub
		return false;
	}
}
