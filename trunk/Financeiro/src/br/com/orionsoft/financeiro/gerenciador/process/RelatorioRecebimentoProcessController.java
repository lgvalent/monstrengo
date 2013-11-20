package br.com.orionsoft.financeiro.gerenciador.process;

import br.com.orionsoft.basic.entities.Contrato;
import br.com.orionsoft.basic.entities.pessoa.Pessoa;
import br.com.orionsoft.monstrengo.core.process.IProcess;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.core.process.RunnableEntityProcessControllerBasic;
import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;

/**
 * Este controlador define as entidades que s�o compat�veis com o processo 
 * e valida se o atual estado de uma entidade possibilita a execu��o do processo aqui controlado.
 * 
 * @author Lucio 31/10/2008
 * @version 20081031
 * 
 * @spring.bean id="RelatorioRecebimentoProcessController" init-method="registerController"
 * @spring.property name="processManager" ref="ProcessManager"
 */
public class RelatorioRecebimentoProcessController extends RunnableEntityProcessControllerBasic
{
    /** Informa para o gerenciador quais as entidades que s�o compat�veis com este controlador */
	public static final Class<?>[] RUNNABLE_ENTITIES = {Contrato.class, Pessoa.class};
	public Class<?>[] getRunnableEntities() {return RUNNABLE_ENTITIES;}

	public Class<? extends IProcess> getProcessClass() {return RelatorioRecebimentoProcess.class;}
	
	/**
	 * @throws EntityException 
	 * @see Consulte {@link IRunnableEntityProcessController.canRunWithEntity(IEntity)}
	 */
	public boolean canRunWithEntity(IEntity entity) throws ProcessException {
		/* Limpa a atual mensagem do controlador */
		this.setMessage(null);

		/* Podem ser adicionados aqui c�digos de valida��o 
		 * para verificar se os atuais valores das propriedades
		 * da entidade permitem a execu��o deste processo sobre
		 * a entidade. */
//		try{
//			if(entity.getInfo().getType() == Contrato.class){
//				/* Verifica se o saldo em aberto permite quitar */
//				if(false){
//					this.setMessage(new BusinessMessage(ListarPosicaoContratoProcessController.class, "DESCRICAO_DA_VALIDA��O"));
//					return false;
//				}
//			}	
//		}catch(BusinessException e){
//			throw new ProcessException(e.getErrorList());
//		}
		
		return true;	
	}


}
