package br.com.orionsoft.monstrengo.crud.processes;

import br.com.orionsoft.monstrengo.core.process.IProcess;
import br.com.orionsoft.monstrengo.core.process.IRunnableEntityProcessController;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.core.process.RunnableProcessControllerBasic;
import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;

/**
 * Este controlador define as entidades que são compatíveis com o processo 
 * de impressão de GRCS.
 * 
 * @author Lucio
 * @version 20121015
 * 
 * @spring.bean id="SqlQueryProcessController" init-method="registerController"
 * @spring.property name="processManager" ref="ProcessManager"
 */
public class SqlQueryProcessController extends RunnableProcessControllerBasic implements IRunnableEntityProcessController
{
    /** Informa para o gerenciador quais as entidades que são compatíveis com este controlador */
	public static final Class<?>[] RUNNABLE_ENTITIES = {};
	public Class<?>[] getRunnableEntities() {return RUNNABLE_ENTITIES;}

	public Class<? extends IProcess> getProcessClass() {return SqlQueryProcess.class;}
	
	/**
	 * @throws EntityException 
	 * @see Consulte {@link IRunnableEntityProcessController.canRunWithEntity(IEntity)}
	 */
	public boolean canRunWithEntity(IEntity<?> entity) throws ProcessException {
		/* Limpa a atual mensagem do controlador */
		this.setMessage(null);

		return true;	
	}
}
