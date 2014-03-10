package br.com.orionsoft.financeiro.gerenciador.process;

import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
import br.com.orionsoft.monstrengo.core.process.IProcess;
import br.com.orionsoft.monstrengo.core.process.IRunnableEntityProcessController;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.core.process.RunnableProcessControllerBasic;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;

/**
 * @author Lucio 20120517
 */
public class InserirLancamentoProcessController extends RunnableProcessControllerBasic implements IRunnableEntityProcessController {
	
	public static final Class<?>[] RUNNABLE_ENTITIES = {Lancamento.class};

	public Class<? extends IProcess> getProcessClass() {return InserirLancamentoProcess.class;}

	public boolean canRunWithEntity(IEntity<?> entity) throws ProcessException {
		if (entity.getInfo().getType() == Lancamento.class) {
			/* Nenhuma restri��o, simplemente cria um novo lan�amento tendo como base o lan�amento passado */
			return true;
		}
		
		return false;
	}

	public Class<?>[] getRunnableEntities() {
		return RUNNABLE_ENTITIES;
	}

}
