package br.com.orionsoft.financeiro.gerenciador.services;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import br.com.orionsoft.basic.entities.Contrato;
import br.com.orionsoft.financeiro.gerenciador.entities.CentroCusto;
import br.com.orionsoft.financeiro.gerenciador.services.ListarLancamentoService.Ordem;
import br.com.orionsoft.financeiro.gerenciador.services.ListarLancamentoService.QueryLancamento;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

public class ListarLancamentoServiceTest extends ServiceBasicTest {
	
	@SuppressWarnings("deprecation")
	@Test
	public void testExecute() throws BusinessException{
		Long[] contaList = new Long[2];
		contaList[0] = 3l;
		contaList[1] = 10l;
		String cnpj = "04175308000129";
		
//		Contrato contrato = UtilsCrud.objectRetrieve(this.serviceManager, Contrato.class, 6598l, null);
		Calendar dataInicial = CalendarUtils.getCalendar(2010, Calendar.JANUARY, 1);
		Calendar dataFinal = CalendarUtils.getCalendar(2010, Calendar.DECEMBER, 31);
		CentroCusto centroCusto = UtilsCrud.objectRetrieve(this.serviceManager, CentroCusto.class, 20l, null);
		String itemCusto = "4";
		Integer situacao = ListarLancamentoService.Situacao.TODOS.ordinal();
		int ordem = Ordem.DATA.ordinal();
		
		ServiceData sd = new ServiceData(ListarLancamentoService.SERVICE_NAME,null);
//		sd.getArgumentList().setProperty(ListarLancamentoService.IN_PESSOA_OPT, cnpj);
//		sd.getArgumentList().setProperty(ListarLancamentoService.IN_CONTA_LIST_OPT, contaList);
//		sd.getArgumentList().setProperty(ListarLancamentoService.IN_CONTRATO_OPT, contrato);
		sd.getArgumentList().setProperty(ListarLancamentoService.IN_DATA_INICIAL_OPT, dataInicial);
		sd.getArgumentList().setProperty(ListarLancamentoService.IN_DATA_FINAL_OPT, dataFinal);
//		sd.getArgumentList().setProperty(ListarLancamentoService.IN_CENTRO_CUSTO_OPT, centroCusto);
//		sd.getArgumentList().setProperty(ListarLancamentoService.IN_ITEM_CUSTO_LIST_OPT, itemCusto);
//		sd.getArgumentList().setProperty(ListarLancamentoService.IN_ITEM_CUSTO_LIST_NOT_OPT, true);
		sd.getArgumentList().setProperty(ListarLancamentoService.IN_ORDEM_OPT, ordem);
		sd.getArgumentList().setProperty(ListarLancamentoService.IN_SITUACAO_OPT, situacao);
		this.serviceManager.execute(sd);
		List<QueryLancamento> list = sd.getFirstOutput();
		assertNotNull(list);
		assertFalse(list.size() == 0);
		System.out.println(list.size());
		
		System.out.printf("%-6s|", "ID");
		System.out.printf("%-10s|", "Data");
		System.out.printf("%-10s|", "Data venc.");
		System.out.printf("%-20s|", "Pessoa");
		System.out.printf("%-20s|", "Conta prevista");
		System.out.printf("%-20s|", "Descrição");
		System.out.printf("%-10s|", "Saldo");
		System.out.printf("%-10s|", "Valor");
		System.out.printf("%-20s|", "Centro custo");
		System.out.printf("%-20s|", "Item custo");
		System.out.println();
		for (QueryLancamento queryLancamento: list){
			System.out.printf("%06d|", queryLancamento.getId());
			System.out.printf("%-10s|", StringUtils.left(queryLancamento.getData().getTime().toLocaleString(), 10));
			System.out.printf("%-10s|", StringUtils.left(queryLancamento.getDataVencimento().getTime().toLocaleString(), 10));
			System.out.printf("%-20s|", StringUtils.left(queryLancamento.getPessoa(), 20));
			System.out.printf("%-20s|", StringUtils.left(queryLancamento.getContaPrevista(), 20));
			System.out.printf("%-20s|", StringUtils.left(queryLancamento.getDescricao(), 20));
			System.out.printf("%10.2f|", queryLancamento.getSaldo());
			System.out.printf("%10.2f|", queryLancamento.getValor());
			System.out.println();
		}
	}
}
