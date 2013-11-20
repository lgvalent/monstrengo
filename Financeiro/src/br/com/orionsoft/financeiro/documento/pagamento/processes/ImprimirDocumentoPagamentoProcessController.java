package br.com.orionsoft.financeiro.documento.pagamento.processes;

import br.com.orionsoft.financeiro.documento.pagamento.DocumentoPagamento;
import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimento;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.process.IProcess;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.core.process.RunnableEntityProcessControllerBasic;
import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;

/**
 * Este controlador define as entidades que são compatíveis com o processo 
 * de impressão de documentos de cobrança.
 * 
 * @author Lucio
 * @version 20081201
 * 
 * @spring.bean id="ImprimirDocumentoPagamentoProcessController" init-method="registerController"
 * @spring.property name="processManager" ref="ProcessManager"
 */
public class ImprimirDocumentoPagamentoProcessController extends RunnableEntityProcessControllerBasic
{
    /** Informa para o gerenciador quais as entidades que são compatíveis com este controlador */
	public static final Class<?>[] RUNNABLE_ENTITIES = {DocumentoPagamento.class, Lancamento.class, LancamentoMovimento.class};
	public Class<?>[] getRunnableEntities() {return RUNNABLE_ENTITIES;}

	public Class<? extends IProcess> getProcessClass() {return ImprimirDocumentoPagamentoProcess.class;}
	
	/**
	 * @throws EntityException 
	 * @see Consulte {@link IRunnableEntityProcessController.canRunWithEntity(IEntity)}
	 */
	public boolean canRunWithEntity(IEntity entity) throws ProcessException {
		/* Limpa a atual mensagem do controlador */
		this.setMessage(null);

		try{
			/* Verifica se a entidade é compatível */
			if(entity.getInfo().getType() == Lancamento.class){
				/* Verifica se o lançamento tem algum documento de cobrança para imprimir */
				if(entity.getProperty(Lancamento.DOCUMENTO_PAGAMENTO).getValue().isValueNull()){
					this.setMessage(new BusinessMessage(ImprimirDocumentoPagamentoProcessController.class, "LANCAMENTO_SEM_DOCUMENTO"));
					return false;
				}
			}	
			/* Verifica se a entidade é compatível */
			if(entity.getInfo().getType() == LancamentoMovimento.class){
				/* Verifica se o lançamento tem algum documento de cobrança para imprimir */
				if(entity.getProperty(LancamentoMovimento.DOCUMENTO_PAGAMENTO).getValue().isValueNull()){
					this.setMessage(new BusinessMessage(ImprimirDocumentoPagamentoProcessController.class, "MOVIMENTO_SEM_DOCUMENTO"));
					return false;
				}
			}	
		}catch(BusinessException e){
			throw new ProcessException(e.getErrorList());
		}
		
		return true;	
	}


}
