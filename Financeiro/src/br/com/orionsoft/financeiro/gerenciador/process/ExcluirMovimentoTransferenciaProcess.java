package br.com.orionsoft.financeiro.gerenciador.process;

import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimento;
import br.com.orionsoft.financeiro.gerenciador.services.ExcluirMovimentoTransferenciaService;
import br.com.orionsoft.monstrengo.auditorship.services.UtilsAuditorship;
import br.com.orionsoft.monstrengo.core.annotations.ProcessMetadata;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.process.IRunnableEntityProcess;
import br.com.orionsoft.monstrengo.core.process.ProcessBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;

/**
 * Este processo realiza a exclus�o de um movimento que seja transfer�ncia.
 * Uma transfer�ncia � registrada no sistema com dois movimentos interligados.
 * Para excluir uma transfer�ncia � necess�rio excluir os dois movimentos.
 * 
 * <p>
 * <b>Procedimentos:</b><br>
 * Primeiramente deve-se definir o id do movimento a ser exclu�do:
 * <i>setLancamentoMovimentoId(long)</i><br>
 * <i>setJustificativa(String)</i><br>
 * <br>
 * Obter a entidade por <i>(IEntity) retrieveEntity()</i>.
 * <li>Obtem a justificativa (Motivo) da exclus�o para a auditoria. <br>
 * Gravar as altera��es por <i>runExcluir()</i>.
 * 
 * @author Lucio Valentin
 * @version 20090518
 * 
 * @spring.bean id="ExcluirMovimentoTransferenciaProcess" init-method="start"
 *              destroy-method="finish" singleton="false"
 * @spring.property name="processManager" ref="ProcessManager"
 * 
 */
@ProcessMetadata(label="Excluir transfer�ncias", hint="Exclui os dois movimentos gerados por uma transfer�ncia", description="Uma transfer�ncia � composta de dois movimentos (cr�dito e d�bito) em duas contas distintas.<br/>A exclus�o de uma transfer�ncia ser� refletida automaticamente nas duas contas.")
public class ExcluirMovimentoTransferenciaProcess extends ProcessBasic implements IRunnableEntityProcess {
	public static final String PROCESS_NAME="ExcluirMovimentoTransferenciaProcess";
	
	private IEntity<LancamentoMovimento> lancamentoMovimento = null;
	private String justificativa = "";
	
	public boolean runExcluir() {
        try {
            ServiceData sd = new ServiceData(ExcluirMovimentoTransferenciaService.SERVICE_NAME, null);
            sd.getArgumentList().setProperty(ExcluirMovimentoTransferenciaService.IN_LANCAMENTO_MOVIMENTO, this.lancamentoMovimento.getObject());
            this.getProcessManager().getServiceManager().execute(sd);
            
            /* Registra na auditoria as exclus�es dos dois movimentos das transfer�ncia */
            if(sd.getMessageList().isTransactionSuccess()){
            	UtilsAuditorship.auditDelete(this.getProcessManager().getServiceManager(), 
            			this.getUserSession(), 
            			LancamentoMovimento.class, 
            			(Long) sd.getOutputData(ExcluirMovimentoTransferenciaService.OUT_LANCAMENTO_MOVIMENTO_ID_1), 
            			"Transfer�ncia:" + this.justificativa + ":'"+ this.lancamentoMovimento.toString() + "'", null);
            	
            	UtilsAuditorship.auditDelete(this.getProcessManager().getServiceManager(), 
            			this.getUserSession(), 
            			LancamentoMovimento.class, 
            			(Long) sd.getOutputData(ExcluirMovimentoTransferenciaService.OUT_LANCAMENTO_MOVIMENTO_ID_2), 
            			"Transfer�ncia:" + this.justificativa + ":'"+ this.lancamentoMovimento.getObject().getTransferencia().toString() + "'", null);
            }

            /* Pegas as mensagens do servi�o */
            this.getMessageList().add(sd.getMessageList());
            
            return true;

        } catch (BusinessException e) {
            /* Armazenando a lista de erros */
            this.getMessageList().addAll(e.getErrorList());
            return false;
        }
    }
	
	public boolean runWithEntity(IEntity entity) {
		super.beforeRun();
		boolean result = false;
		if (entity.getInfo().getType() == LancamentoMovimento.class) {
			this.lancamentoMovimento = entity;
			result = true;
		} else {
			this.getMessageList().add(new BusinessMessage(IRunnableEntityProcess.class, "ENTITY_NOT_COMPATIBLE", PROCESS_NAME, entity.getInfo().getType().getName()));
		}
		return result;
	}

	public String getProcessName() {
		return PROCESS_NAME;
	}

	public IEntity<LancamentoMovimento> getLancamentoMovimento() {
		return lancamentoMovimento;
	}

	public void setLancamentoMovimento(IEntity<LancamentoMovimento> lancamentoMovimento) {
		this.lancamentoMovimento = lancamentoMovimento;
	}

	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}
}
