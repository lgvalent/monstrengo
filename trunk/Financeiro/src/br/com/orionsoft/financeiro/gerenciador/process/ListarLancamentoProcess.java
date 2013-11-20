package br.com.orionsoft.financeiro.gerenciador.process;

import java.util.Calendar;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.ClassUtils;

import br.com.orionsoft.basic.entities.Contrato;
import br.com.orionsoft.basic.entities.pessoa.Pessoa;
import br.com.orionsoft.financeiro.gerenciador.entities.Conta;
import br.com.orionsoft.financeiro.gerenciador.entities.Transacao;
import br.com.orionsoft.financeiro.gerenciador.services.ListarLancamentoService;
import br.com.orionsoft.financeiro.gerenciador.services.ListarLancamentoService.Ordem;
import br.com.orionsoft.financeiro.gerenciador.services.ListarLancamentoService.QueryLancamento;
import br.com.orionsoft.financeiro.gerenciador.services.ListarLancamentoService.Situacao;
import br.com.orionsoft.monstrengo.core.annotations.ProcessMetadata;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.process.IRunnableEntityProcess;
import br.com.orionsoft.monstrengo.core.process.ProcessBasic;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.core.process.ProcessParamEntity;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.core.util.EnumUtils;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;

/**
 * @author Antonio
 * @version 20090706
 * 
 * @spring.bean id="ListarLancamentoProcess" init-method="start"
 *              destroy-method="finish" singleton="false"
 * @spring.property name="processManager" ref="ProcessManager"
 * 
 */
@ProcessMetadata(
		label="Listar lançamentos", 
		description="Lista os lançamentos financeiros previstos e os efetivados.",
		hint="Lista os lançamentos financeiros previstos e os efetivados.")
public class ListarLancamentoProcess extends ProcessBasic implements IRunnableEntityProcess{
	public static final String PROCESS_NAME ="ListarLancamentoProcess";
	
	private List<QueryLancamento> queryLancamentoList = null;
	
	private ProcessParamEntity<Pessoa> paramPessoa = new ProcessParamEntity<Pessoa>(Pessoa.class, false, this);

	private Long conta = IDAO.ENTITY_UNSAVED;
	private Calendar dataFinal = CalendarUtils.getCalendar();
	private Calendar dataInicial = CalendarUtils.getCalendar();
	private Long centroCusto = null;
	private String itemCustoIdList = null;
	private Boolean itemCustoIdListNot = false;
	private Integer ordem = Ordem.DATA.ordinal();
	private Integer situacao = Situacao.TODOS.ordinal();
	private double soma;
	private double credito;
	private double debito;
	
	@Override
	public void start() throws ProcessException {
		/* Seta a data inicial como primeiro dia do mês corrente */
		super.start();
		dataInicial.set(Calendar.DATE, dataInicial.getMinimum(Calendar.DATE));
	}
	
	public boolean runListar() {
		try {
			ServiceData sd = new ServiceData(ListarLancamentoService.SERVICE_NAME, null);
			if (!this.paramPessoa.isNull())
				sd.getArgumentList().setProperty(ListarLancamentoService.IN_PESSOA_OPT, this.paramPessoa.getValue().getObject());
			sd.getArgumentList().setProperty(ListarLancamentoService.IN_CONTA_OPT,this.conta);
			sd.getArgumentList().setProperty(ListarLancamentoService.IN_DATA_FINAL_OPT, this.dataFinal);
			sd.getArgumentList().setProperty(ListarLancamentoService.IN_DATA_INICIAL_OPT, this.dataInicial);
			sd.getArgumentList().setProperty(ListarLancamentoService.IN_CENTRO_CUSTO_OPT, this.centroCusto);
			sd.getArgumentList().setProperty(ListarLancamentoService.IN_ITEM_CUSTO_LIST_OPT, this.itemCustoIdList);
			sd.getArgumentList().setProperty(ListarLancamentoService.IN_ITEM_CUSTO_LIST_NOT_OPT, this.itemCustoIdListNot);
			sd.getArgumentList().setProperty(ListarLancamentoService.IN_ORDEM_OPT,this.ordem);
			sd.getArgumentList().setProperty(ListarLancamentoService.IN_SITUACAO_OPT, this.situacao);
			sd.getArgumentList().setProperty(ListarLancamentoService.IN_APPLICATION_USER_OPT, this.getUserSession().getUser().getObject());

			this.getProcessManager().getServiceManager().execute(sd);

			queryLancamentoList = sd.getFirstOutput();
			
			soma = credito = debito = 0.0;
			this.queryLancamentoList = sd.getFirstOutput();
	    	for (QueryLancamento obj : queryLancamentoList) {
	    		if (obj.getValor().signum() < 0)
	    			debito += obj.getValor().doubleValue();
	    		if (obj.getValor().signum() > 0)
	    			credito += obj.getValor().abs().doubleValue();
	    		soma += obj.getValor().doubleValue();
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

	/*
	 * Listas
	 */
	public List<SelectItem> getListConta() throws BusinessException {
		List<SelectItem> result = this.getProcessManager().getServiceManager().getEntityManager().getEntitySelectItems(Conta.class, IDAO.ENTITY_ALIAS_HQL + "." + Conta.INATIVO + " = false" + " and entity.id in (SELECT c.id FROM Conta c inner join c.applicationUsers as user where user.id=" + this.getUserSession().getUser().getId() + ") order by entity.nome ");
		result.add(0, new SelectItem(IDAO.ENTITY_UNSAVED, "(todas as contas)"));
		return result;
	}
	
	public List<SelectItem> getListTransacao() {
		return EnumUtils.enumToSelectItemList(Transacao.class);
	}
	
	public List<SelectItem> getListOrdem() {
		return EnumUtils.enumToSelectItemList(Ordem.class);
	}
	
	public List<SelectItem> getListSituacao() {
		return EnumUtils.enumToSelectItemList(Situacao.class);
	}

	/*
	 * Getters/Setters
	 */
	public String getProcessName() {
		return PROCESS_NAME;
	}

	public Calendar getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Calendar dataFinal) {
		this.dataFinal = dataFinal;
	}

	public Calendar getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Calendar dataInicial) {
		this.dataInicial = dataInicial;
	}

	public String getItemCustoIdList() {
		return itemCustoIdList;
	}

	public void setItemCustoList(String itemCustoIdList) {
		this.itemCustoIdList = itemCustoIdList;
	}

	public List<QueryLancamento> getQueryLancamentoList() {
		return queryLancamentoList;
	}

	public void setQueryLancamentoList(List<QueryLancamento> queryLancamentoList) {
		this.queryLancamentoList = queryLancamentoList;
	}

	public Boolean getItemCustoIdListNot() {
		return itemCustoIdListNot;
	}

	public void setItemCustoIdListNot(Boolean itemCustoIdListNot) {
		this.itemCustoIdListNot = itemCustoIdListNot;
	}

	public Long getConta() {
		return conta;
	}

	public void setConta(Long conta) {
		this.conta = conta;
	}

	public Long getCentroCusto() {
		return centroCusto;
	}

	public void setCentroCusto(Long centroCusto) {
		this.centroCusto = centroCusto;
	}

	public Integer getSituacao() {
		return situacao;
	}

	public void setSituacao(Integer situacao) {
		this.situacao = situacao;
	}

//	public Integer[] getTransacao() {
//		return transacao;
//	}
//
//	public void setTransacao(Integer[] transacao) {
//		this.transacao = transacao;
//	}

	public void setItemCustoIdList(String itemCustoIdList) {
		this.itemCustoIdList = itemCustoIdList;
	}

	public Integer getOrdem() {
		return ordem;
	}

	public void setOrdem(Integer ordem) {
		this.ordem = ordem;
	}

	public double getSoma() {
		return soma;
	}

	public double getCredito() {
		return credito;
	}

	public double getDebito() {
		return debito;
	}

	public ProcessParamEntity<Pessoa> getParamPessoa() {
		return paramPessoa;
	}

	/*==============================================================================
	 * IRunnableEntityProcess	
	 *==============================================================================*/
	@SuppressWarnings("unchecked")
	public boolean runWithEntity(IEntity<?> entity) {
		super.beforeRun();

		boolean result = false;

		/* Verifica se a entidade é compatível */
		/* Verifica se a entidade passada eh um DocumentoCobranca ou pertence eh descendente */
		if (ClassUtils.isAssignable(entity.getInfo().getType(), Contrato.class)) {
			try {
				this.paramPessoa.setValue((IEntity<Pessoa>) entity.getPropertyValue(Contrato.PESSOA));

				/* Alguns dados poderao ser inicializados aqui */
				this.situacao = Situacao.TODOS.ordinal();

				/* Executa a listagem com os parâmetros definidos acima */
				result = this.runListar();
			} catch (BusinessException e) {
				this.getMessageList().addAll(e.getErrorList());
			}
		}else
		if (ClassUtils.isAssignable(entity.getInfo().getType(), Pessoa.class)) {
			this.paramPessoa.setValue((IEntity<Pessoa>) entity);

			/* Alguns dados poderao ser inicializados aqui */
			this.situacao = Situacao.TODOS.ordinal();

			/* Executa a listagem com os parâmetros definidos acima */
			result = this.runListar();
		}else
		{
			this.getMessageList().add(new BusinessMessage(IRunnableEntityProcess.class, "ENTITY_NOT_COMPATIBLE", PROCESS_NAME, entity.getInfo().getType().getName()));
		}

		return result;
	}

}
