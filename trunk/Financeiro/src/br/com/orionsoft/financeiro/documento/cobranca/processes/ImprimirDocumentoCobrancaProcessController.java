package br.com.orionsoft.financeiro.documento.cobranca.processes;

import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobranca;
import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
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
 * de impressão de documentos de cobrança.
 * 
 * @author Lucio 25/06/2008
 * @version 20080625
 * 
 * @spring.bean id="ImprimirDocumentoCobrancaProcessController" init-method="registerController"
 * @spring.property name="processManager" ref="ProcessManager"
 */
public class ImprimirDocumentoCobrancaProcessController extends RunnableProcessControllerBasic implements IRunnableEntityProcessController {
	
    /** Informa para o gerenciador quais as entidades que são compatíveis com este controlador */
	public static final Class<?>[] RUNNABLE_ENTITIES = {DocumentoCobranca.class, Lancamento.class};
	public Class<?>[] getRunnableEntities() {return RUNNABLE_ENTITIES;}

	public Class<? extends IProcess> getProcessClass() {return ImprimirDocumentoCobrancaProcess.class;}
	
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
				/* Verifica se o lançamento tem algum documento de cobrança para imprimir */
				if(entity.getProperty(Lancamento.DOCUMENTO_COBRANCA).getValue().isValueNull()){
					this.setMessage(new BusinessMessage(ImprimirDocumentoCobrancaProcessController.class, "LANCAMENTO_SEM_DOCUMENTO"));
					return false;
				}
			}	
		}catch(BusinessException e){
			throw new ProcessException(e.getErrorList());
		}
		
		return true;	
	}


}
