package br.com.orionsoft.financeiro.documento.cobranca.processes;

import br.com.orionsoft.basic.entities.Contrato;
import br.com.orionsoft.basic.entities.pessoa.Pessoa;
import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobranca;
import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.process.IProcess;
import br.com.orionsoft.monstrengo.core.process.IRunnableEntityCollectionProcessController;
import br.com.orionsoft.monstrengo.core.process.IRunnableEntityProcessController;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.core.process.RunnableProcessControllerBasic;
import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;

/**
 * Este controlador define as entidades que são compatíveis com o processo 
 * de impressão de documentos de cobrança.
 * 
 * @author Lucio 20120821
 */
public class ImprimirDocumentosCobrancaProcessController extends RunnableProcessControllerBasic implements IRunnableEntityProcessController, IRunnableEntityCollectionProcessController { 

    /** Informa para o gerenciador quais as entidades que são compatíveis com este controlador */
	public static final Class<?>[] RUNNABLE_ENTITIES = {Contrato.class, Pessoa.class};
	public Class<?>[] getRunnableEntities() {return RUNNABLE_ENTITIES;}

	public static final Class<?>[] RUNNABLE_ENTITIES_COLLECTION = {Lancamento.class, DocumentoCobranca.class};
	public Class<?>[] getRunnableEntitiesCollection() {return RUNNABLE_ENTITIES_COLLECTION;}

	public Class<? extends IProcess> getProcessClass() {return ImprimirDocumentosCobrancaProcess.class;}
	
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
					this.setMessage(new BusinessMessage(ImprimirDocumentosCobrancaProcessController.class, "LANCAMENTO_SEM_DOCUMENTO"));
					return false;
				}
			}	
		}catch(BusinessException e){
			throw new ProcessException(e.getErrorList());
		}
		
		return true;	
	}


}
