package br.com.orionsoft.financeiro.documento.cobranca.processes;

import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobranca;
import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoSituacao;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.process.IProcess;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.core.process.RunnableEntityProcessControllerBasic;
import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;

/**
 * Este controlador define as entidades que são compatíveis com o processo.
 * 
 * @author Lucio 20110404
 * @version 20110404
 * 
 * @spring.bean id="AlterarVencimentoDocumentosCobrancaProcessController" init-method="registerController"
 * @spring.property name="processManager" ref="ProcessManager"
 */
public class AlterarVencimentoDocumentosCobrancaProcessController extends RunnableEntityProcessControllerBasic
{
    /** Informa para o gerenciador quais as entidades que são compatíveis com este controlador */
	public static final Class<?>[] RUNNABLE_ENTITIES = {Lancamento.class, DocumentoCobranca.class};
	public Class<?>[] getRunnableEntities() {return RUNNABLE_ENTITIES;}

	public Class<? extends IProcess> getProcessClass() {return AlterarVencimentoDocumentosCobrancaProcess.class;}

	/**
	 * @throws EntityException 
	 * @see Consulte {@link IRunnableEntityProcessController.canRunWithEntity(IEntity)}
	 */
	public boolean canRunWithEntity(IEntity<?> entity) throws ProcessException {
		/* Limpa a atual mensagem do controlador */
		this.setMessage(null);

		/* Verifica se a entidade é compatível */
		if(entity.getInfo().getType() == Lancamento.class){
			/* Verifica se a situação está em aberto */
			Lancamento oLancamento = (Lancamento) entity.getObject();
			if(oLancamento.getLancamentoSituacao() != LancamentoSituacao.PENDENTE){
				this.setMessage(new BusinessMessage(AlterarVencimentoDocumentosCobrancaProcessController.class, "LANCAMENTO_NAO_PENDENTE"));

				return false;
			}
			/* Verifica se tem documento de cobrança vinculado */
			if(oLancamento.getDocumentoCobranca() == null){
				this.setMessage(new BusinessMessage(AlterarVencimentoDocumentosCobrancaProcessController.class, "SEM_DOCUMENTO_COBRANCA"));

				return false;
			}
		}

		return true;	
	}

}
