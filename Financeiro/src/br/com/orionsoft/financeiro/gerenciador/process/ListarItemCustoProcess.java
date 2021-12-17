package br.com.orionsoft.financeiro.gerenciador.process;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.ClassUtils;

import br.com.orionsoft.financeiro.gerenciador.entities.Conta;
import br.com.orionsoft.financeiro.gerenciador.entities.ItemCusto;
import br.com.orionsoft.financeiro.gerenciador.services.ListarItemCustoService;
import br.com.orionsoft.financeiro.gerenciador.services.ListarItemCustoService.Coluna;
import br.com.orionsoft.financeiro.gerenciador.services.ListarItemCustoService.QueryItemCusto;
import br.com.orionsoft.monstrengo.core.annotations.ProcessMetadata;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.process.IRunnableEntityProcess;
import br.com.orionsoft.monstrengo.core.process.ProcessBasic;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.core.util.EnumUtils;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

/**
 * Este processo lista os movimentos inseridos agrupados 
 * por data e item de custo.
 * 
 * @author Antônio 20061207
 * @version 20061207
 */
@ProcessMetadata(label="Listar movimentação por item de custo", hint="Gera uma listagem com a sumarização dos valores dos items de custos lançados em um período", description="Você pode filtrar a listagem por um item de custo, uma conta e um período de lançamento específico.<br/>Selecione as colunas a serem mostradas para que o sistema gere uma listagem mais ou menos detalhada.")
public class ListarItemCustoProcess extends ProcessBasic implements IRunnableEntityProcess{
    public static final String PROCESS_NAME = "ListarItemCustoProcess";
    
    private long contaId = IDAO.ENTITY_UNSAVED;
    private String centroCustoIdList = null;
    private int[] colunaList = {
    		Coluna.DATA.ordinal(), 
    		Coluna.ANO.ordinal(), 
    		Coluna.MES.ordinal(), 
    		Coluna.CONTA.ordinal(), 
    		Coluna.CENTRO_CUSTO.ordinal()
    		};
    private Calendar dataInicial = CalendarUtils.getCalendar();
    private Calendar dataFinal = CalendarUtils.getCalendar();
//    private Calendar dataVencimentoInicial = CalendarUtils.getCalendar();
//    private Calendar dataVencimentoFinal = CalendarUtils.getCalendar();
//    private BigDecimal totalCredito;
//    private BigDecimal totalDebito;
//    private BigDecimal saldoInicial;
//    private BigDecimal saldoFinal;
	private double credito;
	private double debito;
	private double saldo;
    
    public void start() throws ProcessException {
        super.start();
        this.dataInicial.set(
                dataInicial.get(Calendar.YEAR), 
                dataInicial.get(Calendar.MONTH), 
                dataInicial.getActualMinimum(Calendar.DATE));
        this.dataFinal.set(
                dataFinal.get(Calendar.YEAR), 
                dataFinal.get(Calendar.MONTH), 
                dataFinal.getActualMaximum(Calendar.DATE));
    }
    
    public List<QueryItemCusto> retrieveQuitados() {
        try {
            ServiceData sd = new ServiceData(ListarItemCustoService.SERVICE_NAME, null);
            sd.getArgumentList().setProperty(ListarItemCustoService.IN_DATA_LANCAMENTO_INICIAL, this.dataInicial);
            sd.getArgumentList().setProperty(ListarItemCustoService.IN_DATA_LANCAMENTO_FINAL, this.dataFinal);
            sd.getArgumentList().setProperty(ListarItemCustoService.IN_CONTA_ID_OPT, contaId);
            sd.getArgumentList().setProperty(ListarItemCustoService.IN_CENTRO_CUSTO_ID_LIST_OPT, this.centroCustoIdList);
            sd.getArgumentList().setProperty(ListarItemCustoService.IN_COLUNAS_OPT, colunaList);
            this.getProcessManager().getServiceManager().execute(sd);
            List<QueryItemCusto> list = sd.getFirstOutput();
            
			saldo = credito = debito = 0.0;
	    	for (QueryItemCusto obj : list) {
//	    		if (obj.getValorTotal().signum() < 0)
	    			debito += obj.getValorDebito().doubleValue();
//	    		if (obj.getValorTotal().signum() > 0)
	    			credito += obj.getValorCredito().doubleValue();
	    	}
	    	saldo = credito + debito;
//            this.totalDebito = DecimalUtils.getBigDecimal(0.0);
//            this.totalCredito = DecimalUtils.getBigDecimal(0.0);
//            for (QueryItemCusto item : list) {
//            	this.totalCredito = this.totalCredito.add(item.getValorCredito().abs());
//            	this.totalDebito  = this.totalDebito.add(item.getValorDebito().abs());
//            }
//            this.saldoInicial = calcularSaldoInicial();
//            this.saldoFinal = this.saldoInicial.add(this.totalCredito).subtract(this.totalDebito);

            return list;
        } catch (BusinessException e) {
            /* Armazenando a lista de erros */
            this.getMessageList().addAll(e.getErrorList());
            return null;
        }
    }

    public BigDecimal calcularSaldoInicial() {
        BigDecimal saldoAbertura = BigDecimal.ZERO;
        BigDecimal saldoInicial = BigDecimal.ZERO;
        try {
            
            /* Verifica se foi especificada uma conta */
            if(this.contaId != IDAO.ENTITY_UNSAVED){
                IEntity<Conta> conta = UtilsCrud.retrieve(this.getProcessManager().getServiceManager(), Conta.class, this.contaId, null);
                saldoAbertura = conta.getProperty(Conta.SALDO_ABERTURA).getValue().getAsBigDecimal();
            }
            
            Calendar dataInicial = CalendarUtils.getCalendar(this.dataInicial.getTime());
            dataInicial.add(Calendar.DATE, -1);

            /* saldo inicial */
/* TODO
            ServiceData sd = new ServiceData(CalcularSaldoMovimentoService.SERVICE_NAME, null);
            if(this.contaId != IDAO.ENTITY_UNSAVED)
                sd.getArgumentList().setProperty(CalcularSaldoMovimentoService.IN_CONTA_ID_OPT, this.contaId);
            sd.getArgumentList().setProperty(CalcularSaldoMovimentoService.IN_DATA_FINAL_OPT, dataInicial);
            sd.getArgumentList().setProperty(CalcularSaldoMovimentoService.IN_TIPO_OPERACAO, Operacao.TIPO_OPERACAO_QUITAR);
            this.getProcessManager().getServiceManager().execute(sd);
            saldoInicial = (BigDecimal) sd.getFirstOutput();
 */            
            saldoInicial = saldoInicial.add(saldoAbertura);
        } catch (BusinessException e) {
            /* Armazenando a lista de erros */
            this.getMessageList().addAll(e.getErrorList());
        }
        return saldoInicial;
    }

    public String getProcessName() {
        return PROCESS_NAME;
    }

    /**
     * Constrói uma lista de Contas
     * 
     * @return
     * @throws BusinessException
     */
    public List<SelectItem> getListConta() throws BusinessException {
		List<SelectItem> result = this.getProcessManager().getServiceManager().getEntityManager().getEntitySelectItems(Conta.class, IDAO.ENTITY_ALIAS_HQL + "." + Conta.INATIVO + " = false");
		result.add(0, new SelectItem(IDAO.ENTITY_UNSAVED, "(todas as contas)"));
		return result;
    }

    /**
     * Constrói uma lista de Colunas
     * @return
     * @throws BusinessException
     */
    public List<SelectItem> getListColunas() throws BusinessException {
    	return EnumUtils.enumToSelectItemList(Coluna.class);
    }

    public long getContaId() {
        return contaId;
    }

    public void setContaId(long contaId) {
        this.contaId = contaId;
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

//    public BigDecimal getSaldoFinal() {
//        return saldoFinal;
//    }
//
//    public void setSaldoFinal(BigDecimal saldoFinal) {
//        this.saldoFinal = saldoFinal;
//    }
//
//    public BigDecimal getSaldoInicial() {
//        return saldoInicial;
//    }
//
//    public void setSaldoInicial(BigDecimal saldoInicial) {
//        this.saldoInicial = saldoInicial;
//    }
//
//    public BigDecimal getTotalCredito() {
//        return totalCredito;
//    }
//
//    public void setTotalCredito(BigDecimal totalCredito) {
//        this.totalCredito = totalCredito;
//    }
//
//    public BigDecimal getTotalDebito() {
//        return totalDebito;
//    }
//
//    public void setTotalDebito(BigDecimal totalDebito) {
//        this.totalDebito = totalDebito;
//    }

    public int[] getColunaList() {
        return colunaList;
    }

    public void setColunaList(int[] colunaList) {
        this.colunaList = colunaList;
    }

	public String getCentroCustoIdList() {
		return centroCustoIdList;
	}

	public void setCentroCustoIdList(String centroCustoIdList) {
		this.centroCustoIdList = centroCustoIdList;
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

	public double getSaldo() {
		return saldo;
	}

	public void setSaldo(double saldo) {
		this.saldo = saldo;
	}

	/*==============================================================================
	 * IRunnableEntityProcess	
	 *==============================================================================*/
	public boolean runWithEntity(IEntity<?> entity) {
		super.beforeRun();

		boolean result = true;

		/* Verifica se a entidade é compatível */
		/* Verifica se a entidade passada eh um DocumentoCobranca ou pertence eh descendente */
		if (ClassUtils.isAssignable(entity.getInfo().getType(), ItemCusto.class)) {
				ItemCusto oItemCusto = (ItemCusto) entity.getObject();
				this.centroCustoIdList = oItemCusto.getId() + "";

				/* Alguns dados poderao ser inicializados aqui */
				this.contaId = IDAO.ENTITY_UNSAVED; // Todas as contas

				/* Executa a listagem com os parâmetros definidos acima */
//				result = this.runListar();
		}else
		if (ClassUtils.isAssignable(entity.getInfo().getType(), Conta.class)) {
			Conta oConta = (Conta) entity.getObject();
			this.contaId = oConta.getId();

			/* Alguns dados poderao ser inicializados aqui */
			this.centroCustoIdList = "";

			/* Executa a listagem com os parâmetros definidos acima */
//			result = this.runListar();
		}else
		{
			this.getMessageList().add(new BusinessMessage(IRunnableEntityProcess.class, "ENTITY_NOT_COMPATIBLE", PROCESS_NAME, entity.getInfo().getType().getName()));
		}

		return result;
	}

}
