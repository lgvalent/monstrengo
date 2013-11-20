package br.com.orionsoft.financeiro.documento.cobranca.processes;

import br.com.orionsoft.basic.entities.Contrato;
import br.com.orionsoft.basic.entities.pessoa.Pessoa;
import br.com.orionsoft.monstrengo.core.process.IProcess;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.core.process.RunnableEntityProcessControllerBasic;
import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;

/**
 * Este controlador define as entidades que s�o compat�veis com o processo 
 * de impress�o de documentos de cobran�a.
 * 
 * @author Lucio 20120821
 */
public class ImprimirDocumentosCobrancaProcessController extends RunnableEntityProcessControllerBasic
{
    /** Informa para o gerenciador quais as entidades que s�o compat�veis com este controlador */
	public static final Class<?>[] RUNNABLE_ENTITIES = {Contrato.class, Pessoa.class};
	public Class<?>[] getRunnableEntities() {return RUNNABLE_ENTITIES;}

	public Class<? extends IProcess> getProcessClass() {return ImprimirDocumentosCobrancaProcess.class;}
	
	/**
	 * @throws EntityException 
	 * @see Consulte {@link IRunnableEntityProcessController.canRunWithEntity(IEntity)}
	 */
	public boolean canRunWithEntity(IEntity<?> entity) throws ProcessException {
		/* Limpa a atual mensagem do controlador */
		this.setMessage(null);

//		try{
//			/* Verifica se a entidade � compat�vel */
//			if(entity.getInfo().getType() == Lancamento.class){
//				/* Verifica se o lan�amento tem algum documento de cobran�a para imprimir */
//				if(entity.getProperty(Lancamento.DOCUMENTO_COBRANCA).getValue().isValueNull()){
//					this.setMessage(new BusinessMessage(ImprimirDocumentosCobrancaProcessController.class, "LANCAMENTO_SEM_DOCUMENTO"));
//					return false;
//				}
//			}	
//		}catch(BusinessException e){
//			throw new ProcessException(e.getErrorList());
//		}
		
		return true;	
	}


}
