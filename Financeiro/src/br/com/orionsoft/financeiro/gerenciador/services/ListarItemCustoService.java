package br.com.orionsoft.financeiro.gerenciador.services;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.core.util.NativeSQL;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;

/**
 * Lista todos os movimentos por data e item de custo.
 * 
 * @author antonio
 * @version 20061129
 * 
 * @spring.bean id="ListarItemCustoService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 */
public class ListarItemCustoService extends ServiceBasic {
	public class QueryItemCusto {
	    public static final String SQL_QUITADO_BAIXADO = "" +
	    "select " +
	    "	LancamentoMovimento.data as data, " +
	    "	Conta.nome as conta, " +
	    "	ItemCusto.nome as itemCusto, " +
	    "	CentroCusto.nome as centroCusto, " +
	    "	sum(LancamentoMovimento.valorTotal * LancamentoItem.peso) as valorTotal, " +
	    "	sum(if(LancamentoItem.valor > 0, LancamentoMovimento.valorTotal * LancamentoItem.peso, 0)) as valorCredito, " +
	    "	sum(if(LancamentoItem.valor < 0, LancamentoMovimento.valorTotal * LancamentoItem.peso, 0)) as valorDebito " +
	    "from " + 
	    "	financeiro_lancamento_movimento LancamentoMovimento " +
	    "inner join financeiro_lancamento_item LancamentoItem on " + 
	    "	LancamentoItem.lancamento = LancamentoMovimento.lancamento " +
	    "inner join financeiro_item_custo ItemCusto on " + 
	    "	ItemCusto.id = LancamentoItem.itemCusto " +
	    "left outer join financeiro_centro_custo CentroCusto on " + 
	    "	CentroCusto.id = LancamentoItem.centroCusto " +
	    "inner join financeiro_conta Conta on " + 
	    "	Conta.id = LancamentoMovimento.conta " +
	    "where (LancamentoMovimento.data between :dataIni and :dataFin) " +
	    "	and (LancamentoMovimento.conta in (:contas)) " +
	    "	and (LancamentoItem.centroCusto in (:centroCusto)) " +
	    "	and (LancamentoMovimento.lancamentoMovimentoCategoria = 'QUITADO' or LancamentoMovimento.lancamentoMovimentoCategoria = 'QUITADO_ESTORNADO')" +
	    "group by " +
	    "	:colunas " + 
	    "	ItemCusto.nome " + 
	    "order by " +
	    "	:colunas " + 
	    "	ItemCusto.nome "; 
//	            "O.nome as operacaoNome, " +
//	            "M.dataVencimento as dataVencimento, " +
//	            "C.nome as contaNome, " +
//	            "I.nome as itemCustoNome, " +
//	            "CC.nome as centroCustoNome, " +
//	            "sum(if(M.valor > 0, M.valor, 0)) as valorCredito, " +
//	            "sum(if(M.valor < 0, M.valor, 0)) as valorDebito " +
//	            "from financeiro_movimento M " +
//	            "inner join financeiro_grupo G on M.grupo = G.id " +
//	            "inner join financeiro_lancamento L on L.grupo = G.id " +
//	            "inner join financeiro_item_custo I on I.id = L.itemCusto " +
//	            "left outer join financeiro_centro_custo CC on CC.id = L.centroCusto " +
//	            "inner join financeiro_conta C on C.id = M.conta " +
//	            "inner join financeiro_operacao O on M.operacao = O.id " +
//	            "where (M.dataVencimento between :dataIni and :dataFin) " +
//	            "and (M.conta in (:contas)) " +
//	            "and (L.centroCusto in (:centroCusto)) " +
//	            "and (M.operacao in (:operacoes)) " +
//	            "group by :colunas itemCustoNome, operacaoNome " +
//	            "order by :colunas itemCustoNome, operacaoNome ";
	    
//	    private String operacaoNome;
	    private Calendar data;
	    private String conta;
	    private String itemCusto;
	    private String centroCusto;
	    private BigDecimal valorTotal;
	    private BigDecimal valorCredito;
	    private BigDecimal valorDebito;

	    public QueryItemCusto(Calendar data, String conta, String itemCusto, String centroCusto, BigDecimal valorTotal, BigDecimal valorCredito, BigDecimal valorDebito) {
	        super();
	        this.data = data;
	        this.conta = conta;
	        this.itemCusto = itemCusto;
	        this.centroCusto = centroCusto;
	        this.valorTotal = valorTotal;
	        this.valorCredito = valorCredito;
	        this.valorDebito = valorDebito;
	    }
	    
	    public String getConta() {
	        return conta;
	    }

	    public Calendar getData() {
	        return data;
	    }

	    public String getItemCusto() {
	        return itemCusto;
	    }

	    public BigDecimal getValorTotal() {
	        return valorTotal;
	    }

		public String getCentroCusto() {
			return centroCusto;
		}

		public BigDecimal getValorCredito() {
			return valorCredito;
		}

		public void setValorCredito(BigDecimal valorCredito) {
			this.valorCredito = valorCredito;
		}

		public BigDecimal getValorDebito() {
			return valorDebito;
		}

		public void setValorDebito(BigDecimal valorDebito) {
			this.valorDebito = valorDebito;
		}
	    
	}

	public static final String SERVICE_NAME = "ListarItemCustoService";

	public enum Coluna {
		DATA("Data"),
		ANO("Ano"),
		MES("Mês"),
		CONTA("Conta"),
		CENTRO_CUSTO("Centro de custo");
		
		private String nome;

		private Coluna(String nome) {
			this.nome = nome;
		}

		public String getNome() {
			return nome;
		}
		
		public String toString() {
			return nome;
		}
	}

    /* 
     * Constantes de parâmetros
     */
    public static final String IN_CONTA_ID_OPT = "contasId";
    public static final String IN_CENTRO_CUSTO_ID_LIST_OPT = "centroCustoIdList";
    public static final String IN_COLUNAS_OPT = "colunas";
    public static final String IN_DATA_LANCAMENTO_INICIAL = "dataLancamentoInicial";
    public static final String IN_DATA_LANCAMENTO_FINAL = "dataLancamentoFinal";
    public static final String IN_DATA_VENCIMENTO_INICIAL = "dataVencimentoInicial";
    public static final String IN_DATA_VENCIMENTO_FINAL = "dataVencimentoFinal";
//    public static final String IN_TIPO_OPERACAO = "tipoOperacao";

    public void execute(ServiceData serviceData) throws ServiceException {
        log.debug("Iniciando...");
        try {
            log.debug("Preparando parâmetros.");
            /*
             * Parâmetros obrigatórios
             */
            Calendar inDataLancamentoInicial = (Calendar)serviceData.getArgumentList().getProperty(IN_DATA_LANCAMENTO_INICIAL);
            Calendar inDataLancamentoFinal = (Calendar)serviceData.getArgumentList().getProperty(IN_DATA_LANCAMENTO_FINAL);
//            Calendar inDataVencimentoInicial = (Calendar)serviceData.getArgumentList().getProperty(IN_DATA_VENCIMENTO_INICIAL);
//            Calendar inDataVencimentoFinal = (Calendar)serviceData.getArgumentList().getProperty(IN_DATA_VENCIMENTO_FINAL);
//            int inTipoOperacao = (Integer)serviceData.getArgumentList().getProperty(IN_TIPO_OPERACAO);

            /*
             * Parâmetros opcionais
             */
            Long inContaId = (serviceData.getArgumentList().containsProperty(IN_CONTA_ID_OPT) ?
                    (Long) serviceData.getArgumentList().getProperty(IN_CONTA_ID_OPT) : IDAO.ENTITY_UNSAVED);
            String inCentroCustoIdList = (serviceData.getArgumentList().containsProperty(IN_CENTRO_CUSTO_ID_LIST_OPT) ?
                    (String) serviceData.getArgumentList().getProperty(IN_CENTRO_CUSTO_ID_LIST_OPT) : null);
            int[] inColunas = {};
            if (serviceData.getArgumentList().containsProperty(IN_COLUNAS_OPT))
                    inColunas = (int[]) serviceData.getArgumentList().getProperty(IN_COLUNAS_OPT);

            log.debug("Montando a SQL.");

//            String colunas = "";
            StringBuilder colunas = new StringBuilder("");
            for (int i = 0; i < inColunas.length; i++){
                if (inColunas[i] == Coluna.DATA.ordinal()) {
                    colunas.append("data, ");
                }
                if (inColunas[i] == Coluna.MES.ordinal()) {
                	colunas.append("YEAR( DATA ), ");
                }
                if (inColunas[i] == Coluna.MES.ordinal()) {
                    colunas.append("MONTH(data), ");
                }
                if (inColunas[i] == Coluna.CONTA.ordinal()) {
                    colunas.append("conta, ");
                }
                if (inColunas[i] == Coluna.CENTRO_CUSTO.ordinal()) {
                    colunas.append("centroCusto, ");
                }
            }

            NativeSQL nativeSQL = null;
/* TODO
            switch (inTipoOperacao) {
            case Operacao.TIPO_OPERACAO_LANCAR:
                nativeSQL = new NativeSQL(serviceData.getCurrentSession(), QueryItemCusto.SQL_LANCADO);
                break;
            case Operacao.TIPO_OPERACAO_QUITAR:
                Integer[] tipoOperacaoQuitar = {Operacao.OPERACAO_QUITAR, Operacao.OPERACAO_ESTORNAR_QUITADO};
                nativeSQL = new NativeSQL(serviceData.getCurrentSession(), QueryItemCusto.SQL_QUITADO_BAIXADO);
                nativeSQL.setArrayInteger("operacoes", tipoOperacaoQuitar);
                break;
            case Operacao.TIPO_OPERACAO_BAIXAR:
                Integer[] tipoOperacaoBaixar = {Operacao.OPERACAO_BAIXAR, Operacao.OPERACAO_ESTORNAR_BAIXADO};
                nativeSQL = new NativeSQL(serviceData.getCurrentSession(), QueryItemCusto.SQL_QUITADO_BAIXADO);
                nativeSQL.setArrayInteger("operacoes", tipoOperacaoBaixar);
                break;
            }
 */

            nativeSQL = new NativeSQL(serviceData.getCurrentSession(), QueryItemCusto.SQL_QUITADO_BAIXADO);
            if (inContaId != IDAO.ENTITY_UNSAVED) {
                Long[] listContas = {inContaId};
                nativeSQL.setArrayLong("contas", listContas);
            }
            else
                nativeSQL.setSelect("contas", "id", "financeiro_conta");
            
            if (StringUtils.isNotBlank(inCentroCustoIdList)) {
                nativeSQL.setParameter("centroCusto", inCentroCustoIdList);
            }
            else
                nativeSQL.setSelect("centroCusto", "id", "financeiro_centro_custo");
            
            nativeSQL.setCalendar("dataIni", inDataLancamentoInicial);
            nativeSQL.setCalendar("dataFin", inDataLancamentoFinal);
            nativeSQL.setParameter("colunas", colunas.toString());

            log.debug("Executando a SQL.");
            ResultSet rs = nativeSQL.executeQuery();
            
            Calendar data = CalendarUtils.getCalendar();
            List<QueryItemCusto> list = new ArrayList<QueryItemCusto>();
            while (rs.next()) {
                QueryItemCusto itemCusto = new QueryItemCusto(
                        CalendarUtils.getCalendar(rs.getDate("data", data)),
                        rs.getString("conta"),
                        rs.getString("itemCusto"),
                        rs.getString("centroCusto"),
                        rs.getBigDecimal("valorTotal"),
                        rs.getBigDecimal("valorCredito"),
                        rs.getBigDecimal("valorDebito"));
                list.add(itemCusto);
            }
            serviceData.getOutputData().add(list);
            
            log.debug("Fim da execução do serviço");
        } catch (BusinessException e) {
            log.fatal(e.getErrorList());
            /* O Serviço não precisa adicionar mensagem local. O Manager já indica qual srv falhou e os parâmetros. */
            throw new ServiceException(e.getErrorList());
        } catch (Exception e) {
            log.fatal(e.getMessage());
            /* Indica que o serviço falhou por causa de uma exceção do hibernate. */
            throw new ServiceException(MessageList.createSingleInternalError(e));
        }
    }

    public String getServiceName() {
        return SERVICE_NAME;
    }

}

/*
select 
	LancamentoMovimento.data as data, 
	Conta.nome as conta, 
	ItemCusto.nome as itemCusto, 
	CentroCusto.nome as centroCusto, 
	sum(LancamentoMovimento.valorTotal) as valorTotal, 
	sum(if(LancamentoMovimento.juros > 0, LancamentoMovimento.juros, 0)) as valorJurosCredito, 
	sum(if(LancamentoMovimento.juros < 0, LancamentoMovimento.juros, 0)) as valorJurosDebito, 
	sum(if(LancamentoMovimento.multa > 0, LancamentoMovimento.multa, 0)) as valorMultaCredito, 
	sum(if(LancamentoMovimento.multa < 0, LancamentoMovimento.multa, 0)) as valorMultaDebito, 
	sum(if(LancamentoMovimento.desconto > 0, LancamentoMovimento.desconto, 0)) as valorDescontoCredito, 
	sum(if(LancamentoMovimento.desconto < 0, LancamentoMovimento.desconto, 0)) as valorDescontoDebito
from  
	financeiro_lancamento_movimento LancamentoMovimento 
inner join financeiro_lancamento Lancamento on  
	LancamentoMovimento.lancamento = Lancamento.id 
inner join financeiro_lancamento_item LancamentoItem on  
	LancamentoItem.lancamento = Lancamento.id 
inner join financeiro_item_custo ItemCusto on  
	ItemCusto.id = LancamentoItem.itemCusto 
left outer join financeiro_centro_custo CentroCusto on  
	CentroCusto.id = LancamentoItem.centroCusto 
inner join financeiro_conta Conta on  
	Conta.id = LancamentoMovimento.conta 
where (LancamentoMovimento.data between '2010-03-01' and '2010-03-31') 
	and (LancamentoItem.itemCusto in (4)) 
	and (LancamentoMovimento.lancamentoMovimentoCategoria = 'QUITADO' or LancamentoMovimento.lancamentoMovimentoCategoria = 'QUITADO_ESTORNADO')
group by 
	ItemCusto.nome  
order by 
	ItemCusto.nome
 */
