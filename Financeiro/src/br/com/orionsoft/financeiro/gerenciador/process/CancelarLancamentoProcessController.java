package br.com.orionsoft.financeiro.gerenciador.process;

import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoSituacao;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.process.IProcess;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.core.process.RunnableEntityProcessControllerBasic;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;

/**
 * @author Lucio 20120515
 */
public class CancelarLancamentoProcessController extends RunnableEntityProcessControllerBasic {
	public static final Class<?>[] RUNNABLE_ENTITIES = {Lancamento.class};

	public Class<? extends IProcess> getProcessClass() {return CancelarLancamentoProcess.class;}

	public boolean canRunWithEntity(IEntity<?> entity) throws ProcessException {
		if (entity.getInfo().getType() == Lancamento.class) {
			Lancamento oLancamento= (Lancamento) entity.getObject();

			/* Verifica se o movimento já é um estorno */
			if (oLancamento.getLancamentoSituacao()!=LancamentoSituacao.PENDENTE){
				this.setMessage(new BusinessMessage(CancelarLancamentoProcessController.class, "LANCAMENTO_NAO_PENDENTE"));

				return false;
			}
			return true;
		}
		
		return false;
	}

	public Class<?>[] getRunnableEntities() {
		return RUNNABLE_ENTITIES;
	}

}
