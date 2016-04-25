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
 * Este processo realiza a exclusão de um movimento que seja transferência.
 * Uma transferência é registrada no sistema com dois movimentos interligados.
 * Para excluir uma transferência é necessário excluir os dois movimentos.
 * 
 * <p>
 * <b>Procedimentos:</b><br>
 * Primeiramente deve-se definir o id do movimento a ser excluído:
 * <i>setLancamentoMovimentoId(long)</i><br>
 * <i>setJustificativa(String)</i><br>
 * <br>
 * Obter a entidade por <i>(IEntity) retrieveEntity()</i>.
 * <li>Obtem a justificativa (Motivo) da exclusão para a auditoria. <br>
 * Gravar as alterações por <i>runExcluir()</i>.
 * 
 * @author Lucio Valentin
 * @version 20090518
 * 
 * @spring.bean id="ExcluirMovimentoTransferenciaProcess" init-method="start"
 *              destroy-method="finish" singleton="false"
 * @spring.property name="processManager" ref="ProcessManager"
 * 
 */
@ProcessMetadata(label="Excluir transferências", hint="Exclui os dois movimentos gerados por uma transferência", description="Uma transferência é composta de dois movimentos (crédito e débito) em duas contas distintas.<br/>A exclusão de uma transferência será refletida automaticamente nas duas contas.")
public class ExcluirMovimentoTransferenciaProcess extends ProcessBasic implements IRunnableEntityProcess {
	public static final String PROCESS_NAME="ExcluirMovimentoTransferenciaProcess";
	
	private IEntity<LancamentoMovimento> lancamentoMovimento = null;
	private String justificativa = "";
	
	public boolean runExcluir() {
        try {
            ServiceData sd = new ServiceData(ExcluirMovimentoTransferenciaService.SERVICE_NAME, null);
            sd.getArgumentList().setProperty(ExcluirMovimentoTransferenciaService.IN_LANCAMENTO_MOVIMENTO, this.lancamentoMovimento.getObject());
            this.getProcessManager().getServiceManager().execute(sd);
            
            /* Registra na auditoria as exclusões dos dois movimentos das transferência */
            if(sd.getMessageList().isTransactionSuccess()){
            	UtilsAuditorship.auditDelete(this.getProcessManager().getServiceManager(), 
            			this.getUserSession(), 
            			LancamentoMovimento.class, 
            			(Long) sd.getOutputData(ExcluirMovimentoTransferenciaService.OUT_LANCAMENTO_MOVIMENTO_ID_1), 
            			"Transferência:" + this.justificativa + ":'"+ this.lancamentoMovimento.toString() + "'", null);
            	
            	UtilsAuditorship.auditDelete(this.getProcessManager().getServiceManager(), 
            			this.getUserSession(), 
            			LancamentoMovimento.class, 
            			(Long) sd.getOutputData(ExcluirMovimentoTransferenciaService.OUT_LANCAMENTO_MOVIMENTO_ID_2), 
            			"Transferência:" + this.justificativa + ":'"+ this.lancamentoMovimento.getObject().getTransferencia().toString() + "'", null);
            }

            /* Pegas as mensagens do serviço */
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
