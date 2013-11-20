package br.com.orionsoft.financeiro.gerenciador.process;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimento;
import br.com.orionsoft.financeiro.gerenciador.services.CancelarLancamentoService;
import br.com.orionsoft.monstrengo.auditorship.services.UtilsAuditorship;
import br.com.orionsoft.monstrengo.core.annotations.ProcessMetadata;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.process.IRunnableEntityProcess;
import br.com.orionsoft.monstrengo.core.process.ProcessBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;

/**
 * Este processo realiza a quita��o de um lancamento. Ele localiza o documento ligado
 * ao lancamento para definir um documento de quita��o, se n�o tiver nenhum documento
 * vinculado ele cria um documento de quita��o
 * 
 * <p>
 * <b>Procedimentos:</b><br>
 * Primeiramente deve-se definir o id do lancamento a ser quitaqo:
 * <i>setLancamentoId(long)</i><br>
 * Definir o identificador da conta onde ser� lan�ado movimento de quita��o:
 * <i>setContaId(long)</i><br>
 * Definir a dataQuitacao de quita��o : <i>setDataQuitacao(Calendar)</i><br>
 * Definir o valor da quita��o: <i>setValor(BigDecimal)</i><br>
 * <br>
 * Verificar se a entidade pode ser criada: <i>boolean mayCreate()</i> <br>
 * Obter a entidade por <i>(IEntity) retrieveEntity()</i>.
 * <li>Realizar edi��es pela interface com o usu�rio. <br>
 * Gravar as altera��es por <i>runUpdate()</i>.
 * 
 * @author Lucio
 * @version 20060710
 */
@ProcessMetadata(label="Cancelar lan�amento", hint="Cancela o lan�ameto retirando seu estado de PENDENTE", description="Ao cancelar um lan�amento o mesmo deixa de ser PENDENTE e passa a ser CANCELADO. Assim, ele n�o ser� mais cobrado e seu saldo em aberto passa a ser zero.")
public class CancelarLancamentoProcess extends ProcessBasic implements IRunnableEntityProcess {
	public static final String PROCESS_NAME="CancelarLancamentoProcess";

	private Calendar data = CalendarUtils.getCalendar();
	private IEntity<Lancamento> lancamento = null;
	private List<IEntity<Lancamento>> lancamentos = new ArrayList<IEntity<Lancamento>>();
	private String descricao = "";

	/**
	 * Armazena o LancamentoMovimento gerado pelo cancelamento.
	 */
	public LancamentoMovimento lancamentoMovimento;
	public List<LancamentoMovimento> lancamentoMovimentos = new ArrayList<LancamentoMovimento>();

	public boolean runCancelar() {
		super.beforeRun();
		try {
			/* Cancela um unico lan�amento */
			if((this.lancamento != null)&&(this.lancamento.isSelected())){
				ServiceData sd = new ServiceData(CancelarLancamentoService.SERVICE_NAME, null);
				sd.getArgumentList().setProperty(CancelarLancamentoService.IN_DATA, this.data);
				sd.getArgumentList().setProperty(CancelarLancamentoService.IN_DESCRICAO, this.descricao);
				sd.getArgumentList().setProperty(CancelarLancamentoService.IN_LANCAMENTO, this.lancamento.getObject());
				this.getProcessManager().getServiceManager().execute(sd);

				/* Registra na auditoria do cancelamento */
				if(sd.getMessageList().isTransactionSuccess()){
					this.lancamentoMovimento = sd.getFirstOutput();
					IEntity<LancamentoMovimento> entityLancamentoMovimento = this.getProcessManager().getServiceManager().getEntityManager().getEntity(this.lancamentoMovimento);
					UtilsAuditorship.auditCreate(this.getProcessManager().getServiceManager(), this.getUserSession(), entityLancamentoMovimento, null);
				}

				/* Pega as mensagens do servi�o */
				this.getMessageList().addAll(sd.getMessageList());
				
				/* Limpa o lancamento cancelado para evitar duplo cancelamento */
				this.lancamento = null;
			}
			/* Cancela a cole�ao de lan�amentos se tiver 
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

					/* Pega as mensagens do servi�o */
					this.getMessageList().addAll(sds.getMessageList());
				}
			}
			/* Limpa a lista de lancamento apos o cancelamento para evitar duplo cancelamento */
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


	public IEntity<Lancamento> getLancamento() {
		return lancamento;
	}
	
	public List<IEntity<Lancamento>> getLancamentos(){
		return this.lancamentos;
	}


	public void setLancamento(IEntity<Lancamento> lancamento) {
		this.lancamento = lancamento;
	}


	public LancamentoMovimento getLancamentoMovimento() {
		return lancamentoMovimento;
	}
	
	@SuppressWarnings("unchecked")
	public boolean runWithEntity(IEntity<?> entity) {
		super.beforeRun();
		boolean result = false;
		if (entity.getInfo().getType() == LancamentoMovimento.class) {
			this.lancamento = (IEntity<Lancamento>) entity;
			result = true;
		} else {
			this.getMessageList().add(new BusinessMessage(IRunnableEntityProcess.class, "ENTITY_NOT_COMPATIBLE", PROCESS_NAME, entity.getInfo().getType().getName()));
		}
		return result;
	}

}
