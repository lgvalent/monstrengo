package br.com.orionsoft.financeiro.documento.cobranca.processes;

import java.math.BigDecimal;

import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.process.IProcess;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.core.process.RunnableEntityProcessControllerBasic;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;

/**
 * 
 * @author Lucio
 * @since 07/10/2008
 *
 * @spring.bean id="AlterarDocumentoCobrancaProcessController" init-method="registerController"
 * @spring.property name="processManager" ref="ProcessManager"
 */
public class AlterarDocumentoCobrancaProcessController extends RunnableEntityProcessControllerBasic {
	public static final Class<?>[] RUNNABLE_ENTITIES = {Lancamento.class};

	public Class<? extends IProcess> getProcessClass() {return AlterarDocumentoCobrancaProcess.class;}

	public boolean canRunWithEntity(IEntity entity) throws ProcessException {
		try {
			if (entity.getInfo().getType() == Lancamento.class) {
				/* Verifica se o saldo em aberto permite quitar */
				if(entity.getProperty(Lancamento.SALDO).getValue().getAsBigDecimal().compareTo(BigDecimal.ZERO) == 0){
					this.setMessage(new BusinessMessage(AlterarDocumentoCobrancaProcessController.class, "LANCAMENTO_SEM_SALDO"));
					
					return false;
				}
				
				return true;
			}
		} catch (BusinessException e) {
			throw new ProcessException(e.getErrorList());
		}
		return false;
	}

	public Class<?>[] getRunnableEntities() {
		return RUNNABLE_ENTITIES;
	}

}
