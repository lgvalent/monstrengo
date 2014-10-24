package br.com.orionsoft.financeiro.gerenciador.process;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.com.orionsoft.basic.services.CancelarContratoService;
import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimento;
import br.com.orionsoft.financeiro.gerenciador.services.CancelarLancamentoService;
import br.com.orionsoft.monstrengo.auditorship.services.UtilsAuditorship;
import br.com.orionsoft.monstrengo.core.annotations.ProcessMetadata;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.process.IRunnableEntityCollectionProcess;
import br.com.orionsoft.monstrengo.core.process.IRunnableEntityProcess;
import br.com.orionsoft.monstrengo.core.process.ProcessBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityCollection;

/**
 * Este processo cancela os lançamentos pendentes listados, gerando movimentos e alterando a situação.
 * 
 * @author Lucio
 * @version 20060710
 */
@ProcessMetadata(label="Cancelar lançamento", hint="Cancela o lançameto retirando seu estado de PENDENTE", description="Ao cancelar um lançamento o mesmo deixa de ser PENDENTE e passa a ser CANCELADO. Assim, ele não será mais cobrado e seu saldo em aberto passa a ser zero.")
public class CancelarLancamentoProcess extends ProcessBasic implements IRunnableEntityProcess, IRunnableEntityCollectionProcess {
	public static final String PROCESS_NAME="CancelarLancamentoProcess";

	private Calendar data = CalendarUtils.getCalendar();
	private List<IEntity<Lancamento>> lancamentos = new ArrayList<IEntity<Lancamento>>();
	private String descricao = "";
	private Boolean cancelarContrato = false;

	/**
	 * Armazena o LancamentoMovimento gerado pelo cancelamento.
	 */
	public LancamentoMovimento lancamentoMovimento;
	public List<LancamentoMovimento> lancamentoMovimentos = new ArrayList<LancamentoMovimento>();

	public boolean runCancelar() {
		super.beforeRun();
		try {
			/* Cancela a coleçao de lançamentos se tiver 
			 * e guarda os movimentos de cancelamentos gerado na lista */
			this.lancamentoMovimentos.clear();
			for(IEntity<Lancamento> lan: this.lancamentos){
				if(lan.isSelected()){
					ServiceData sds = new ServiceData(CancelarLancamentoService.SERVICE_NAME, null);
					sds.getArgumentList().setProperty(CancelarLancamentoService.IN_DATA, this.data);
					sds.getArgumentList().setProperty(CancelarLancamentoService.IN_DESCRICAO, this.descricao);
					sds.getArgumentList().setProperty(CancelarLancamentoService.IN_LANCAMENTO, lan.getObject());
					this.getProcessManager().getServiceManager().execute(sds);

					/* Registra na auditoria o cancelamento */
					if(sds.getMessageList().isTransactionSuccess()){
						LancamentoMovimento lanMov = sds.getFirstOutput();
						IEntity<LancamentoMovimento> entityLancamentoMovimento = this.getProcessManager().getServiceManager().getEntityManager().getEntity(lanMov);
						UtilsAuditorship.auditCreate(this.getProcessManager().getServiceManager(), this.getUserSession(), entityLancamentoMovimento, null);
						this.lancamentoMovimentos.add(lanMov);
					}

					/* Pega as mensagens do serviço */
					this.getMessageList().addAll(sds.getMessageList());

					if(this.cancelarContrato){
						ServiceData sdc = new ServiceData(CancelarContratoService.SERVICE_NAME, null);
						sdc.getArgumentList().setProperty(CancelarContratoService.IN_CONTRATO, lan.getPropertyValue(Lancamento.CONTRATO));
						sdc.getArgumentList().setProperty(CancelarContratoService.IN_DATA_CANCELAMENTO, this.data);
						sdc.getArgumentList().setProperty(CancelarContratoService.IN_DESCRICAO, this.descricao);
						sdc.getArgumentList().setProperty(CancelarContratoService.IN_USER_SESSION, this.getUserSession());
						this.getProcessManager().getServiceManager().execute(sdc);
						/* Pega as mensagens do serviço */
						this.getMessageList().addAll(sdc.getMessageList());
					}

				}
			}
			/* Limpa a lista de lancamento após o cancelamento para evitar duplo cancelamento */
			this.lancamentos.clear();

			return true;

		} catch (BusinessException e) {
			/* Armazenando a lista de erros */
			this.getMessageList().addAll(e.getErrorList());
			return false;
		}
	}
	
	public String getProcessName() {
		return PROCESS_NAME;
	}


	public Calendar getData() {
		return data;
	}


	public void setData(Calendar data) {
		this.data = data;
	}


	public String getDescricao() {
		return descricao;
	}


	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}


	public List<IEntity<Lancamento>> getLancamentos(){
		return this.lancamentos;
	}


	public LancamentoMovimento getLancamentoMovimento() {
		return lancamentoMovimento;
	}
	
	public Boolean getCancelarContrato() {
		return cancelarContrato;
	}

	public void setCancelarContrato(Boolean cancelarContrato) {
		this.cancelarContrato = cancelarContrato;
	}

	@SuppressWarnings("unchecked")
	public boolean runWithEntity(IEntity<?> entity) {
		super.beforeRun();
		boolean result = false;
		if (entity.getInfo().getType() == Lancamento.class) {
			this.lancamentos.add((IEntity<Lancamento>) entity);
			result = true;
		} else {
			this.getMessageList().add(new BusinessMessage(IRunnableEntityProcess.class, "ENTITY_NOT_COMPATIBLE", PROCESS_NAME, entity.getInfo().getType().getName()));
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean runWithEntities(IEntityCollection<?> entities) {
		super.beforeRun();
		boolean result = false;
		if (entities.getInfo().getType() == Lancamento.class) {
			this.lancamentos.addAll((IEntityCollection<Lancamento>) entities);
			result = true;
		} else {
			this.getMessageList().add(new BusinessMessage(IRunnableEntityProcess.class, "ENTITY_NOT_COMPATIBLE", PROCESS_NAME, entities.getInfo().getType().getName()));
		}
		return result;
	}
	
}
