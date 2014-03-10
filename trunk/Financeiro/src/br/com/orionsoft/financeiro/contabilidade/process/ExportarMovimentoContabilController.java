package br.com.orionsoft.financeiro.contabilidade.process;

import br.com.orionsoft.monstrengo.core.process.IProcess;
import br.com.orionsoft.monstrengo.core.process.IRunnableEntityProcessController;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.core.process.RunnableProcessControllerBasic;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;

/**
 * 
 * @author Lucio 20120822
 */
public class ExportarMovimentoContabilController extends RunnableProcessControllerBasic implements IRunnableEntityProcessController{
	public static final Class<?>[] RUNNABLE_ENTITIES = {};

	public Class<? extends IProcess> getProcessClass() {return ExportarMovimentoContabilProcess.class;}

	public boolean canRunWithEntity(IEntity<?> entity) throws ProcessException {
		return false;
	}

	public Class<?>[] getRunnableEntities() {
		return RUNNABLE_ENTITIES;
	}

}
