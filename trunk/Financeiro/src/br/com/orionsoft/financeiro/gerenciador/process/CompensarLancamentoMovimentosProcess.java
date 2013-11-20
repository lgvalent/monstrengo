/*
 * Created on 31/03/2006 by antonio
 */
package br.com.orionsoft.financeiro.gerenciador.process;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimento;
import br.com.orionsoft.financeiro.gerenciador.services.CompensarLancamentoMovimentosService;
import br.com.orionsoft.monstrengo.auditorship.services.UtilsAuditorship;
import br.com.orionsoft.monstrengo.auditorship.support.EntityAuditValue;
import br.com.orionsoft.monstrengo.core.annotations.ProcessMetadata;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.process.IRunnableEntityProcess;
import br.com.orionsoft.monstrengo.core.process.ProcessBasic;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;

/**
 * Este processo realiza a compensa��o de v�rios movimentos.
 * 
 * @author Lucio
 * @since 2013109
 */
@ProcessMetadata(label = "Compensar movimentos", hint = "Registra a data de compensa��o do movimento. Esta data indica, por exemplo, quando um cheque foi efetivamente compensado na conta.", description="Este processo marca os movimentos de uma conta como <b>compensados</b>. Em uma conta caixa, compensar pode significar conferir.<br/>Em uma conta banc�ria, a compensa��o ocorre quando efetivamente h� o cr�dito de um boleto banc�rio ou o d�bito de um cheque emitido dias antes.<br/>A compensa��o de um movimento far� que seu valor fa�a parte do saldo cont�bil de compensa��o.")
public class CompensarLancamentoMovimentosProcess extends ProcessBasic implements IRunnableEntityProcess {
	public static final String PROCESS_NAME = "CompensarLancamentoMovimentosProcess";
	public String getProcessName() {return PROCESS_NAME;}

	/** Lan�amentos que ser�o quitados */
	private IEntityList<LancamentoMovimento> lancamentoMovimentos;

	private Calendar dataCompensacao = CalendarUtils.getCalendar();

	private List<EntityAuditValue> lancamentoMovimentosAuditValue = new ArrayList<EntityAuditValue>();

	@Override
	public void start() throws ProcessException {
		try {
			super.start();
			this.lancamentoMovimentos = this.getProcessManager().getServiceManager().getEntityManager().getEntityList(null, LancamentoMovimento.class);
		} catch (BusinessException e) {
			throw new ProcessException(e.getErrorList());
		}
	}

	public boolean runCompensar() {
		super.beforeRun();

		try {
			/* Prepara as altera��es da descri��o na AuditoriaCRUD */
			this.lancamentoMovimentosAuditValue.clear();
			for(IEntity<LancamentoMovimento> lancamentoMovimento: this.lancamentoMovimentos){
				this.lancamentoMovimentosAuditValue.add(new EntityAuditValue(lancamentoMovimento));
			}

			/* Chama o servi�o CompensarLancamentoService. */
			ServiceData sd = new ServiceData(CompensarLancamentoMovimentosService.SERVICE_NAME, null);
			sd.getArgumentList().setProperty(CompensarLancamentoMovimentosService.IN_MOVIMENTOS, this.lancamentoMovimentos);
			sd.getArgumentList().setProperty(CompensarLancamentoMovimentosService.IN_DATA_COMPENSACAO, this.dataCompensacao);
			this.getProcessManager().getServiceManager().execute(sd);

			/* Pegas as mensagens do servi�o */
			super.getMessageList().addAll(sd.getMessageList());

			/* Registra as altera��es da descri��o na AuditoriaCRUD */
			for(EntityAuditValue entityAuditValue: this.lancamentoMovimentosAuditValue){
				UtilsAuditorship.auditUpdate(this.getProcessManager().getServiceManager(), this.getUserSession(), entityAuditValue, null);
			}
			
			/* Limpa os movimentos compensados */
			this.lancamentoMovimentos.clear();

			return true;
		} catch (BusinessException e) {
			/*
			 * Armazenando a lista de erros
			 */
			super.getMessageList().addAll(e.getErrorList());
			return false;
		}
	}

	public Calendar getDataCompensacao() {
		return dataCompensacao;
	}

	public void setDataCompensacao(Calendar dataQuitacao) {
		this.dataCompensacao = dataQuitacao;
	}

	public IEntityList<LancamentoMovimento> getLancamentoMovimentos() {
		return lancamentoMovimentos;
	}

	public BigDecimal getValorTotalCompensacao(){
		BigDecimal total = BigDecimal.ZERO;
		for(IEntity<LancamentoMovimento> movimento: this.lancamentoMovimentos){
			total = total.add(movimento.getObject().getValorTotal());
		}

		return total;
	}
	public BigDecimal getValorTotalCompensacaoSelecionada(){
		BigDecimal total = BigDecimal.ZERO;
		for(IEntity<LancamentoMovimento> movimento: this.lancamentoMovimentos){
			if(movimento.isSelected())
				total = total.add(movimento.getObject().getValorTotal());
		}

		return total;
	}

	/*
	 * ==========================================================================
	 * IRunnableEntityProcess
	 * ==========================================================================
	 */
	@SuppressWarnings("unchecked")
	public boolean runWithEntity(IEntity<?> entity) {
		super.beforeRun();

		boolean result = false;

		/* Verifica se a entidade � compat�vel */
		if (entity.getInfo().getType() == LancamentoMovimento.class) {
			this.lancamentoMovimentos.clear();
			this.lancamentoMovimentos.add((IEntity<LancamentoMovimento>) entity);

			result = true;
		} else {
			this.getMessageList().add(new BusinessMessage(IRunnableEntityProcess.class,"ENTITY_NOT_COMPATIBLE", PROCESS_NAME, entity.getInfo().getType().getName()));
		}

		return result;
	}


}
