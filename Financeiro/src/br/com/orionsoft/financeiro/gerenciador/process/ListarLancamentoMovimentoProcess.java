package br.com.orionsoft.financeiro.gerenciador.process;

import java.util.Calendar;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.ClassUtils;

import br.com.orionsoft.financeiro.gerenciador.entities.CentroCusto;
import br.com.orionsoft.financeiro.gerenciador.entities.Conta;
import br.com.orionsoft.financeiro.gerenciador.entities.ItemCusto;
import br.com.orionsoft.financeiro.gerenciador.entities.Transacao;
import br.com.orionsoft.financeiro.gerenciador.services.ListarLancamentoMovimentoService;
import br.com.orionsoft.financeiro.gerenciador.services.ListarLancamentoMovimentoService.Listagem;
import br.com.orionsoft.financeiro.gerenciador.services.ListarLancamentoMovimentoService.Ordem;
import br.com.orionsoft.financeiro.gerenciador.services.ListarLancamentoMovimentoService.QueryLancamentoMovimento;
import br.com.orionsoft.monstrengo.core.annotations.ProcessMetadata;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.process.IRunnableEntityProcess;
import br.com.orionsoft.monstrengo.core.process.ProcessBasic;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.core.process.ProcessParamEntity;
import br.com.orionsoft.monstrengo.core.process.ProcessParamEntityList;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.core.util.EnumUtils;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;

/**
 * Serviço que fornece uma lista de "LancamentoMovimento" ou seja, a
 * movimentação efetiva da conta.
 * 
 * <p>
 * 
 * @author Antonio Alves
 * @since 13/09/2007
 * 
 * @spring.bean id="ListarLancamentoMovimentoProcess" init-method="start"
 *              destroy-method="finish" singleton="false"
 * @spring.property name="processManager" ref="ProcessManager"
 * 
 */
@ProcessMetadata(
		label="Movimentação da conta", 
		description="Visualize as movimentações que foram realizadas nas contas do financeiro. <br/> Define os filtros do relatório e confira os totalizadores no final da página.",
		hint="Lista os movimentos que foram realizados nas contas do financeiro.")
public class ListarLancamentoMovimentoProcess extends ProcessBasic implements IRunnableEntityProcess{
	public static final String PROCESS_NAME = "ListarLancamentoMovimentoProcess";
	
	private List<QueryLancamentoMovimento> queryLancamentoMovimentoList;

	private Long conta = IDAO.ENTITY_UNSAVED;
	private Long contrato = IDAO.ENTITY_UNSAVED;
	private Calendar dataLancamentoInicial = CalendarUtils.getCalendarFirstMonthDay();
	private Calendar dataLancamentoFinal = CalendarUtils.getCalendarLastMonthDay();
	private Calendar dataQuitacaoInicial = CalendarUtils.getCalendarFirstMonthDay();
	private Calendar dataQuitacaoFinal = CalendarUtils.getCalendarLastMonthDay();
	private Calendar dataCompensacaoInicial = CalendarUtils.getCalendarFirstMonthDay();
	private Calendar dataCompensacaoFinal = CalendarUtils.getCalendarLastMonthDay();
	private Calendar dataVencimentoInicial = CalendarUtils.getCalendarFirstMonthDay();
	private Calendar dataVencimentoFinal = CalendarUtils.getCalendarLastMonthDay();
	private Long documentoPagamentoCategoria = IDAO.ENTITY_UNSAVED;

	private ProcessParamEntity<CentroCusto> paramCentroCusto = new ProcessParamEntity<CentroCusto>(CentroCusto.class, false, this);
	private ProcessParamEntityList<ItemCusto> paramItemCusto = new ProcessParamEntityList<ItemCusto>(ItemCusto.class, false, this);
	
	private Boolean itemCustoNot = false;
	private int[] transacaoList = {
			Transacao.CREDITO.ordinal(),
			Transacao.DEBITO.ordinal()
	};
	private int listagem = Listagem.COMPLETA.ordinal();
	private int ordem;
	private double credito;
	private double debito;
	private double saldo;
	private double somaMovimento;
	private double somaJuros;
	private double somaMulta;
	private double somaDesconto;
	private double somaAcrescimo;
	private double somaTotal;

	public String getProcessName() {
		return PROCESS_NAME;
	}

	@Override
	public void start() throws ProcessException {
		super.start();

		/* Inicializa os filtros dos parâmetros */
		this.ordem = Ordem.DATA.ordinal();

		/* Expande a data de compensação */
		dataCompensacaoInicial.add(Calendar.YEAR, -1);
		dataCompensacaoFinal.add(Calendar.YEAR, 1);
	}

	public boolean runListar() {
		try {
			ServiceData sd = new ServiceData(ListarLancamentoMovimentoService.SERVICE_NAME, null);
			sd.getArgumentList().setProperty(ListarLancamentoMovimentoService.IN_CONTA_ID_OPT, this.conta);
			sd.getArgumentList().setProperty(ListarLancamentoMovimentoService.IN_DATA_LANCAMENTO_INICIAL_OPT, this.dataLancamentoInicial);
			sd.getArgumentList().setProperty(ListarLancamentoMovimentoService.IN_DATA_LANCAMENTO_FINAL_OPT, this.dataLancamentoFinal);
			sd.getArgumentList().setProperty(ListarLancamentoMovimentoService.IN_DATA_QUITACAO_INICIAL_OPT, this.dataQuitacaoInicial);
			sd.getArgumentList().setProperty(ListarLancamentoMovimentoService.IN_DATA_QUITACAO_FINAL_OPT, this.dataQuitacaoFinal);
			sd.getArgumentList().setProperty(ListarLancamentoMovimentoService.IN_DATA_COMPENSACAO_INICIAL_OPT, this.dataCompensacaoInicial);
			sd.getArgumentList().setProperty(ListarLancamentoMovimentoService.IN_DATA_COMPENSACAO_FINAL_OPT, this.dataCompensacaoFinal);
			sd.getArgumentList().setProperty(ListarLancamentoMovimentoService.IN_DATA_VENCIMENTO_INICIAL_OPT, this.dataVencimentoInicial);
			sd.getArgumentList().setProperty(ListarLancamentoMovimentoService.IN_DATA_VENCIMENTO_FINAL_OPT, this.dataVencimentoFinal);
			sd.getArgumentList().setProperty(ListarLancamentoMovimentoService.IN_ORDEM_OPT, Ordem.values()[this.ordem]);
			sd.getArgumentList().setProperty(ListarLancamentoMovimentoService.IN_TRANSACAO_OPT, this.transacaoList);
			sd.getArgumentList().setProperty(ListarLancamentoMovimentoService.IN_LISTAGEM_OPT, Listagem.values()[this.listagem]);
			sd.getArgumentList().setProperty(ListarLancamentoMovimentoService.IN_CENTRO_CUSTO_ID_OPT, this.paramCentroCusto.isNull()?null:this.paramCentroCusto.getValue().getId());
			sd.getArgumentList().setProperty(ListarLancamentoMovimentoService.IN_ITEM_CUSTO_LIST_OPT, this.paramItemCusto.isNull()?null:this.paramItemCusto.getValue().getIds());
			sd.getArgumentList().setProperty(ListarLancamentoMovimentoService.IN_ITEM_CUSTO_LIST_NOT_OPT, this.itemCustoNot);
			sd.getArgumentList().setProperty(ListarLancamentoMovimentoService.IN_APPLICATION_USER_OPT, this.getUserSession().getUser().getObject());
			this.getProcessManager().getServiceManager().execute(sd);
			
			somaMovimento = somaJuros = somaMulta = somaDesconto = somaAcrescimo = somaTotal = credito = debito = 0.0;
			this.queryLancamentoMovimentoList = sd.getFirstOutput();
	    	for (QueryLancamentoMovimento obj : queryLancamentoMovimentoList) {
	    		if (obj.getValorMovimento().signum() < 0)
	    			debito += obj.getValorMovimento().doubleValue();
	    		if (obj.getValorMovimento().signum() > 0)
	    			credito += obj.getValorMovimento().abs().doubleValue();
	    		if (obj.getValorJuros() != null)
	    			somaJuros += obj.getValorJuros().doubleValue();
	    		if (obj.getValorMulta() != null)
	    			somaMulta += obj.getValorMulta().doubleValue();
	    		if (obj.getValorDesconto() != null)
	    			somaDesconto += obj.getValorDesconto().doubleValue();
	    		if (obj.getValorAcrescimo() != null)
	    			somaAcrescimo += obj.getValorAcrescimo().doubleValue();
	    		somaTotal += obj.getValorTotal().doubleValue();
	    	}
	    	somaMovimento = credito + debito;

			/* Pegas as mensagens do serviço */
			this.getMessageList().add(sd.getMessageList());
			return true;

		} catch (BusinessException e) {
			/* Armazenando a lista de erros */
			this.getMessageList().addAll(e.getErrorList());
			return false;
		}
	}
	
	/*
	 * Listas
	 */
	public List<SelectItem> getListConta() throws BusinessException {
		List<SelectItem> result = this.getProcessManager().getServiceManager().getEntityManager().getEntitySelectItems(Conta.class, IDAO.ENTITY_ALIAS_HQL + "." + Conta.INATIVO + " = false" + " and entity.id in (SELECT c.id FROM Conta c inner join c.applicationUsers as user where user.id=" + this.getUserSession().getUser().getId() + ") order by entity.nome");
		result.add(0, new SelectItem(IDAO.ENTITY_UNSAVED, "(todas as contas)"));
		return result;
	}
	
	public List<SelectItem> getListListagem() {
		return EnumUtils.enumToSelectItemList(Listagem.class);
	}
	
	public List<SelectItem> getListTransacao() {
		return EnumUtils.enumToSelectItemList(Transacao.class);
	}
	
	public List<SelectItem> getListOrdem() {
		return EnumUtils.enumToSelectItemList(Ordem.class);
	}

	/*
	 * getters e setters
	 */
	public Long getConta() {
		return conta;
	}

	public void setConta(Long conta) {
		this.conta = conta;
	}

	public Long getContrato() {
		return contrato;
	}

	public void setContrato(Long contrato) {
		this.contrato = contrato;
	}

	public Calendar getDataLancamentoInicial() {
		return dataLancamentoInicial;
	}

	public void setDataLancamentoInicial(Calendar dataLancamentoInicial) {
		this.dataLancamentoInicial = dataLancamentoInicial;
	}

	public Calendar getDataLancamentoFinal() {
		return dataLancamentoFinal;
	}

	public void setDataLancamentoFinal(Calendar dataLancamentoFinal) {
		this.dataLancamentoFinal = dataLancamentoFinal;
	}

	public Calendar getDataQuitacaoInicial() {
		return dataQuitacaoInicial;
	}

	public void setDataQuitacaoInicial(Calendar dataQuitacaoInicial) {
		this.dataQuitacaoInicial = dataQuitacaoInicial;
	}

	public Calendar getDataQuitacaoFinal() {
		return dataQuitacaoFinal;
	}

	public void setDataQuitacaoFinal(Calendar dataQuitacaoFinal) {
		this.dataQuitacaoFinal = dataQuitacaoFinal;
	}
	
	public Calendar getDataCompensacaoInicial() {
		return dataCompensacaoInicial;
	}

	public void setDataCompensacaoInicial(Calendar dataCompensacaoInicial) {
		this.dataCompensacaoInicial = dataCompensacaoInicial;
	}

	public Calendar getDataCompensacaoFinal() {
		return dataCompensacaoFinal;
	}

	public void setDataCompensacaoFinal(Calendar dataCompensacaoFinal) {
		this.dataCompensacaoFinal = dataCompensacaoFinal;
	}

	public Calendar getDataVencimentoInicial() {
		return dataVencimentoInicial;
	}

	public void setDataVencimentoInicial(Calendar dataVencimentoInicial) {
		this.dataVencimentoInicial = dataVencimentoInicial;
	}

	public Calendar getDataVencimentoFinal() {
		return dataVencimentoFinal;
	}

	public void setDataVencimentoFinal(Calendar dataVencimentoFinal) {
		this.dataVencimentoFinal = dataVencimentoFinal;
	}

	public Long getDocumentoPagamentoCategoria() {
		return documentoPagamentoCategoria;
	}

	public void setDocumentoPagamentoCategoria(Long documentoPagamentoCategoria) {
		this.documentoPagamentoCategoria = documentoPagamentoCategoria;
	}

	public int getListagem() {
		return listagem;
	}

	public void setListagem(int listagem) {
		this.listagem = listagem;
	}

	public int getOrdem() {
		return ordem;
	}

	public void setOrdem(int ordem) {
		this.ordem = ordem;
	}

	public List<QueryLancamentoMovimento> getQueryLancamentoMovimentoList() {
		return queryLancamentoMovimentoList;
	}

	public int[] getTransacaoList() {
		return transacaoList;
	}

	public void setTransacaoList(int[] transacaoList) {
		this.transacaoList = transacaoList;
	}

	public double getCredito() {
		return credito;
	}

	public void setCredito(double credito) {
		this.credito = credito;
	}

	public double getDebito() {
		return debito;
	}

	public void setDebito(double debito) {
		this.debito = debito;
	}

	public Boolean getItemCustoNot() {
		return itemCustoNot;
	}

	public void setItemCustoNot(Boolean itemCustoNot) {
		this.itemCustoNot = itemCustoNot;
	}

	public double getSaldo() {
		return saldo;
	}

	public void setSaldo(double saldo) {
		this.saldo = saldo;
	}

	public double getSomaMovimento() {
		return somaMovimento;
	}

	public double getSomaJuros() {
		return somaJuros;
	}

	public double getSomaMulta() {
		return somaMulta;
	}

	public double getSomaTotal() {
		return somaTotal;
	}

	public double getSomaDesconto() {
		return somaDesconto;
	}

	public double getSomaAcrescimo() {
		return somaAcrescimo;
	}
	
	public ProcessParamEntity<CentroCusto> getParamCentroCusto() {
		return paramCentroCusto;
	}

	public ProcessParamEntityList<ItemCusto> getParamItemCusto() {
		return paramItemCusto;
	}

	/*==============================================================================
	 * IRunnableEntityProcess	
	 *==============================================================================*/
	public boolean runWithEntity(IEntity<?> entity) {
		super.beforeRun();

		boolean result = false;

		/* Verifica se a entidade é compatível */
		if (ClassUtils.isAssignable(entity.getInfo().getType(), Conta.class)) {
				this.conta = entity.getId();

				/* Executa a listagem com os parâmetros definidos acima */
				result = this.runListar();
		}else
		{
			this.getMessageList().add(new BusinessMessage(IRunnableEntityProcess.class, "ENTITY_NOT_COMPATIBLE", PROCESS_NAME, entity.getInfo().getType().getName()));
		}

		return result;
	}
}
