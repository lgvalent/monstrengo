package br.com.orionsoft.financeiro.gerenciador.process;

import java.math.BigDecimal;

import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.process.IProcess;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.core.process.RunnableEntityProcessControllerBasic;
import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;

/**
 * Este controlador define as entidades que são compatíveis com o processo 
 * de quitação de laçamentos.
 * 
 * @author Lucio 23/06/2008
 * @version 20080623
 * 
 * @spring.bean id="QuitarLancamentoProcessController" init-method="registerController"
 * @spring.property name="processManager" ref="ProcessManager"
 */
public class QuitarLancamentoProcessController extends RunnableEntityProcessControllerBasic
{
    /** Informa para o gerenciador quais as entidades que são compatíveis com este controlador */
	public static final Class<?>[] RUNNABLE_ENTITIES = {Lancamento.class};
	public Class<?>[] getRunnableEntities() {return RUNNABLE_ENTITIES;}

	public Class<? extends IProcess> getProcessClass() {return QuitarLancamentoProcess.class;}
	
	/**
	 * @throws EntityException 
	 * @see Consulte {@link IRunnableEntityProcessController.canRunWithEntity(IEntity)}
	 */
	public boolean canRunWithEntity(IEntity<?> entity) throws ProcessException {
		/* Limpa a atual mensagem do controlador */
		this.setMessage(null);

		try{
			/* Verifica se a entidade é compatível */
			if(entity.getInfo().getType() == Lancamento.class){
				/* Verifica se o saldo em aberto permite quitar */
				if(entity.getProperty(Lancamento.SALDO).getValue().getAsBigDecimal().compareTo(BigDecimal.ZERO) == 0){
					this.setMessage(new BusinessMessage(QuitarLancamentoProcessController.class, "SALDO_ZERO"));
					
					return false;
				}
			}	
		}catch(BusinessException e){
			throw new ProcessException(e.getErrorList());
		}
		
		return true;	
	}


}
