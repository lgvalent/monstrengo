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
 * @author Antonio Alves
 * @since 08/10/2007
 * @version 24/06/2008 Lucio 
 *
 * @spring.bean id="EstornarLancamentoMovimentoProcessController" init-method="registerController"
 * @spring.property name="processManager" ref="ProcessManager"
 */
public class EstornarLancamentoMovimentoProcessController extends RunnableEntityProcessControllerBasic {
	public static final Class<?>[] RUNNABLE_ENTITIES = {LancamentoMovimento.class};

	public Class<? extends IProcess> getProcessClass() {return EstornarLancamentoMovimentoProcess.class;}

	public boolean canRunWithEntity(IEntity<?> entity) throws ProcessException {
		if (entity.getInfo().getType() == LancamentoMovimento.class) {
			LancamentoMovimento oLancamentoMovimento = (LancamentoMovimento) entity.getObject();

			/* Verifica se o movimento j� � um estorno */
			if (oLancamentoMovimento.getEstornado()){
				this.setMessage(new BusinessMessage(EstornarLancamentoMovimentoProcessController.class, "MOVIMENTO_JAH_ESTORNADO"));

				return false;
			}else
				/* Verifica se � uma transfer�ncia */
				if (oLancamentoMovimento.getLancamentoMovimentoCategoria().equals(LancamentoMovimentoCategoria.TRANSFERIDO)){
					/* Verifica se o movimento transferido possui o registro de liga��o entre as transfer�ncias.
					 * Os dados importados n�o possuiam esta referencia m�tua entre os movimentos transferidos */
					if(oLancamentoMovimento.getTransferencia() ==  null)
					   this.setMessage(new BusinessMessage(EstornarLancamentoMovimentoProcessController.class, "MOVIMENTO_TRANSFERIDO", "", ""));
					else
						this.setMessage(new BusinessMessage(EstornarLancamentoMovimentoProcessController.class, "MOVIMENTO_TRANSFERIDO", oLancamentoMovimento.getConta().toString(), oLancamentoMovimento.getTransferencia().getConta().toString()));

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
