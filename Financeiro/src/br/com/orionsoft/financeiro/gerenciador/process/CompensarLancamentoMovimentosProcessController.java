package br.com.orionsoft.financeiro.gerenciador.process;

import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimento;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.process.IProcess;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.core.process.RunnableEntityProcessControllerBasic;
import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;

/**
 * @author Lucio 20131008
 */
public class CompensarLancamentoMovimentosProcessController extends RunnableEntityProcessControllerBasic
{
    /** Informa para o gerenciador quais as entidades que são compatíveis com este controlador */
	public static final Class<?>[] RUNNABLE_ENTITIES = {LancamentoMovimento.class};
	public Class<?>[] getRunnableEntities() {return RUNNABLE_ENTITIES;}

	public Class<? extends IProcess> getProcessClass() {return CompensarLancamentoMovimentosProcess.class;}
	
	/**
	 * @throws EntityException 
	 * @see Consulte {@link IRunnableEntityProcessController.canRunWithEntity(IEntity)}
	 */
	public boolean canRunWithEntity(IEntity<?> entity) throws ProcessException {
		/* Limpa a atual mensagem do controlador */
		this.setMessage(null);

		try{
			/* Verifica se a entidade é compatível */
			if(entity.getInfo().getType() == LancamentoMovimento.class){
				/* Verifica se o saldo em aberto permite quitar */
				if(entity.getProperty(LancamentoMovimento.COMPENSADO).getValue().getAsBoolean()){
					this.setMessage(new BusinessMessage(CompensarLancamentoMovimentosProcessController.class, "JA_COMPENSADO", entity.getProperty(LancamentoMovimento.DATA_COMPENSACAO).getValue().getAsString()));
					
					return false;
				}
			}	
		}catch(BusinessException e){
			throw new ProcessException(e.getErrorList());
		}
		
		return true;	
	}


}
