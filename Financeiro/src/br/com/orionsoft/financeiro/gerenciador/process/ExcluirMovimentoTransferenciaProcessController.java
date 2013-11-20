package br.com.orionsoft.financeiro.gerenciador.process;

import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimento;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimentoCategoria;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.process.IProcess;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.core.process.RunnableEntityProcessControllerBasic;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;

/**
 * 
 * @author Lucio
 * @since 20090525
 * @version 20090525 LUcio 
 *
 * @spring.bean id="ExcluirMovimentoTransferenciaProcessController" init-method="registerController"
 * @spring.property name="processManager" ref="ProcessManager"
 */
public class ExcluirMovimentoTransferenciaProcessController extends RunnableEntityProcessControllerBasic {
	public static final Class<?>[] RUNNABLE_ENTITIES = {LancamentoMovimento.class};

	public Class<? extends IProcess> getProcessClass() {return ExcluirMovimentoTransferenciaProcess.class;}
	
	public boolean canRunWithEntity(IEntity<?> entity) throws ProcessException {
		if (entity.getInfo().getType() == LancamentoMovimento.class) {
			LancamentoMovimento oLancamentoMovimento = (LancamentoMovimento) entity.getObject();

			/* Verifica se é uma transferência */
			if (!oLancamentoMovimento.getLancamentoMovimentoCategoria().equals(LancamentoMovimentoCategoria.TRANSFERIDO)){
				this.setMessage(new BusinessMessage(ExcluirMovimentoTransferenciaProcessController.class, "MOVIMENTO_NAO_TRANSFERENCIA"));

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
