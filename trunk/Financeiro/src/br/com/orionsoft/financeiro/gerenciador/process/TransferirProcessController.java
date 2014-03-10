package br.com.orionsoft.financeiro.gerenciador.process;

import br.com.orionsoft.financeiro.gerenciador.entities.Conta;
import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimento;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoSituacao;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.process.IProcess;
import br.com.orionsoft.monstrengo.core.process.IRunnableEntityProcessController;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.core.process.RunnableProcessControllerBasic;
import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;

/**
 * Este controlador define as entidades que são compatíveis com o processo 
 * de quitação de laçamentos.
 * 
 * @author Lucio 20120918
 * 
 */
public class TransferirProcessController extends RunnableProcessControllerBasic implements IRunnableEntityProcessController {

    /** Informa para o gerenciador quais as entidades que são compatíveis com este controlador */
	public static final Class<?>[] RUNNABLE_ENTITIES = {Conta.class, Lancamento.class, LancamentoMovimento.class};
	public Class<?>[] getRunnableEntities() {return RUNNABLE_ENTITIES;}

	public Class<? extends IProcess> getProcessClass() {return TransferirProcess.class;}
	
	/**
	 * @throws EntityException 
	 * @see Consulte {@link IRunnableEntityProcessController.canRunWithEntity(IEntity)}
	 */
	public boolean canRunWithEntity(IEntity<?> entity) throws ProcessException {
		/* Limpa a atual mensagem do controlador */
		this.setMessage(null);

		try{
			/* Verifica se a entidade é compatível */
			if(entity.getInfo().getType() == Conta.class){
				/* Verifica se a conta está ativa */
				if(entity.getProperty(Conta.INATIVO).getValue().getAsBoolean()){
					this.setMessage(new BusinessMessage(TransferirProcessController.class, "CONTA_INATIVA"));
					return false;
				}
			}	
			if(entity.getInfo().getType() == Lancamento.class){
				/* Verifica se o lançamento está quitado */
				Lancamento oLancamento = (Lancamento) entity.getObject();
				if(oLancamento.getLancamentoSituacao() != LancamentoSituacao.QUITADO){
					this.setMessage(new BusinessMessage(TransferirProcessController.class, "LANCAMENTO_NAO_QUITADO"));
					return false;
				}
			}	
		}catch(BusinessException e){
			throw new ProcessException(e.getErrorList());
		}
		
		return true;	
	}


}
