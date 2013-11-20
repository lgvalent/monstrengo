package br.com.orionsoft.financeiro.gerenciador.process;

import static org.junit.Assert.assertNotNull;

import java.util.Calendar;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import br.com.orionsoft.financeiro.gerenciador.entities.Conta;
import br.com.orionsoft.financeiro.gerenciador.services.ListarLancamentoMovimentoService.QueryLancamentoMovimento;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.core.test.ProcessBasicTest;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

public class ListarLancamentoMovimentoProcessTest extends ProcessBasicTest {
	private ListarLancamentoMovimentoProcess process;

	@Test
	public void testRunListar() throws BusinessException {
		IEntity conta = UtilsCrud.retrieve(this.processManager.getServiceManager(), Conta.class, 1l, null);
		Calendar dataInicial = CalendarUtils.getCalendar(2009, Calendar.APRIL, 1);
		Calendar dataFinal = CalendarUtils.getCalendar(2009, Calendar.APRIL, 30);
//		dataFinal.set(Calendar.DATE, dataFinal.getActualMaximum(Calendar.DATE));

		process = (ListarLancamentoMovimentoProcess) this.processManager.createProcessByName(ListarLancamentoMovimentoProcess.PROCESS_NAME, getAdminSession());
//		process.setConta(conta.getId());
		process.setDataLancamentoInicial(dataInicial);
		process.setDataLancamentoFinal(dataFinal);
		process.runListar();
		
		List<QueryLancamentoMovimento> list = process.getQueryLancamentoMovimentoList();
		assertNotNull(list);
		System.out.printf("%-6s|", "ID");
		System.out.printf("%-10s|", "Data");
		System.out.printf("%-20s|", "Pessoa");
		System.out.printf("%-20s|", "Conta");
		System.out.printf("%-20s|", "Descrição");
		System.out.printf("%-10s|", "Valor mov.");
		System.out.printf("%-10s|", "Valor item");
		System.out.printf("%-6s|", "Peso");
		System.out.printf("%-10s|", "Valor cont");
		System.out.printf("%-20s|", "Centro custo");
		System.out.printf("%-20s|", "Item custo");
		System.out.println();
		for (QueryLancamentoMovimento queryLancamentoMovimento: list){
			System.out.printf("%06d|", queryLancamentoMovimento.getId());
			System.out.printf("%-10s|", StringUtils.left(queryLancamentoMovimento.getDataQuitacao().getTime().toLocaleString(), 10));
			System.out.printf("%-20s|", StringUtils.left(queryLancamentoMovimento.getPessoa(), 20));
			System.out.printf("%-20s|", StringUtils.left(queryLancamentoMovimento.getConta(), 20));
			System.out.printf("%-20s|", StringUtils.left(queryLancamentoMovimento.getDescricao(), 20));
			System.out.printf("%10.2f|", queryLancamentoMovimento.getValorMovimento());
			System.out.printf("%10.2f|", queryLancamentoMovimento.getValorItem());
			System.out.printf("%6.4f|", queryLancamentoMovimento.getPeso());
			System.out.printf("%10.2f|", queryLancamentoMovimento.getValorContabil());
			System.out.printf("%-20s|", StringUtils.left(queryLancamentoMovimento.getCentroCusto(), 20));
			System.out.printf("%-20s|", StringUtils.left(queryLancamentoMovimento.getItemCusto(), 20));
			System.out.println();
		}
	}
	
//	@Test
	public void getListConta() throws BusinessException {
		process = (ListarLancamentoMovimentoProcess) this.processManager.createProcessByName(ListarLancamentoMovimentoProcess.PROCESS_NAME, getAdminSession());
		List<SelectItem> list = process.getListConta();
		for (SelectItem item : list) {
			System.out.println(item.getLabel());
		}
	}
	
//	@Test
	public void getListOrdem() throws ProcessException {
		process = (ListarLancamentoMovimentoProcess) this.processManager.createProcessByName(ListarLancamentoMovimentoProcess.PROCESS_NAME, getAdminSession());
		List<SelectItem> list = process.getListOrdem();
		for (SelectItem item : list) {
			System.out.println(item.getLabel());
		}
	}

//	@Test
	public void getListTransacao() throws ProcessException {
		process = (ListarLancamentoMovimentoProcess) this.processManager.createProcessByName(ListarLancamentoMovimentoProcess.PROCESS_NAME, getAdminSession());
		List<SelectItem> list = process.getListTransacao();
		for (SelectItem item : list) {
			System.out.println(item.getLabel());
		}
	}

}
