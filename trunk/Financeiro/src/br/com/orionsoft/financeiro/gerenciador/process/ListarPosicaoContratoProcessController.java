package br.com.orionsoft.financeiro.gerenciador.process;

import br.com.orionsoft.basic.entities.Contrato;
import br.com.orionsoft.basic.entities.pessoa.Pessoa;
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
 * @author Lucio 17/08/2008
 * @version 20080817
 * 
 * @spring.bean id="ListarPosicaoContratoProcessController" init-method="registerController"
 * @spring.property name="processManager" ref="ProcessManager"
 */
public class ListarPosicaoContratoProcessController extends RunnableProcessControllerBasic implements IRunnableEntityProcessController {
	
    /** Informa para o gerenciador quais as entidades que são compatíveis com este controlador */
	public static final Class<?>[] RUNNABLE_ENTITIES = {Contrato.class, Pessoa.class};
	public Class<?>[] getRunnableEntities() {return RUNNABLE_ENTITIES;}

	public Class<? extends IProcess> getProcessClass() {return ListarPosicaoContratoProcess.class;}
	
	/**
	 * @throws EntityException 
	 * @see Consulte {@link IRunnableEntityProcessController.canRunWithEntity(IEntity)}
	 */
	public boolean canRunWithEntity(IEntity entity) throws ProcessException {
		/* Limpa a atual mensagem do controlador */
		this.setMessage(null);

		/* Podem ser adicionados aqui códigos de validação 
		 * para verificar se os atuais valores das propriedades
		 * da entidade permitem a execução deste processo sobre
		 * a entidade. */
//		try{
//			if(entity.getInfo().getType() == Contrato.class){
//				/* Verifica se o saldo em aberto permite quitar */
//				if(false){
//					this.setMessage(new BusinessMessage(ListarPosicaoContratoProcessController.class, "DESCRICAO_DA_VALIDAÇÂO"));
//					return false;
//				}
//			}	
//		}catch(BusinessException e){
//			throw new ProcessException(e.getErrorList());
//		}
		
		return true;	
	}


}
