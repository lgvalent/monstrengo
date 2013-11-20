package br.com.orionsoft.financeiro.documento.cobranca.processes;

import br.com.orionsoft.financeiro.documento.cobranca.ConvenioCobranca;
import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobranca;
import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobrancaCategoria;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.process.IProcess;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.core.process.RunnableEntityProcessControllerBasic;
import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;

/**
 * Este controlador define as entidades que são compatíveis com o processo 
 * de processamento de arquivo de retorbo de documentos de cobrança.
 * 
 * @author Lucio 24/01/2011
 * @version 20110124
 * 
 * @spring.bean id="ReceberDocumentoCobrancaRetornoProcessController" init-method="registerController"
 * @spring.property name="processManager" ref="ProcessManager"
 */
public class ReceberDocumentoCobrancaRetornoProcessController extends RunnableEntityProcessControllerBasic
{
    /** Informa para o gerenciador quais as entidades que são compatíveis com este controlador */
	public static final Class<?>[] RUNNABLE_ENTITIES = {DocumentoCobranca.class, DocumentoCobrancaCategoria.class, ConvenioCobranca.class};
	public Class<?>[] getRunnableEntities() {return RUNNABLE_ENTITIES;}

	public Class<? extends IProcess> getProcessClass() {return ReceberDocumentoCobrancaRetornoProcess.class;}
	
	/**
	 * @throws EntityException 
	 * @see Consulte {@link IRunnableEntityProcessController.canRunWithEntity(IEntity)}
	 */
	public boolean canRunWithEntity(IEntity<?> entity) throws ProcessException {
		/* Limpa a atual mensagem do controlador */
		this.setMessage(null);

		try{
			/* Verifica se a entidade é compatível */
			if(entity.getInfo().getType() == DocumentoCobranca.class){
				/* Verifica se o documento tem um convênio */
				if(entity.getProperty(DocumentoCobranca.CONVENIO_COBRANCA).getValue().isValueNull()){
					this.setMessage(new BusinessMessage(ReceberDocumentoCobrancaRetornoProcessController.class, "DOCUMENTO_SEM_CONVENIO"));
					return false;
				}
			} else	
			/* Verifica se a entidade é compatível */
			if(entity.getInfo().getType() == DocumentoCobrancaCategoria.class){
				/* Verifica se a categoria tem um convênio */
				if(entity.getProperty(DocumentoCobrancaCategoria.CONVENIO_COBRANCA).getValue().isValueNull()){
					this.setMessage(new BusinessMessage(ReceberDocumentoCobrancaRetornoProcessController.class, "DOCUMENTO_CATEGORIA_SEM_CONVENIO"));
					return false;
				}
			}	
		}catch(BusinessException e){
			throw new ProcessException(e.getErrorList());
		}
		
		return true;	
	}


}
