package br.com.orionsoft.financeiro.gerenciador.services;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;

import br.com.orionsoft.financeiro.gerenciador.entities.ItemCusto;
import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoItem;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoSituacao;
import br.com.orionsoft.financeiro.gerenciador.services.ListarLancamentoService.Situacao;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.crud.services.Operator;
import br.com.orionsoft.monstrengo.crud.services.QueryCondiction;

public class ListarPosicaoContratoServiceTest extends ServiceBasicTest{
	
//	@Test
	public void teste() {
		List<ItemCusto> list = new ArrayList<ItemCusto>(0);
		
		ItemCusto item1 = new ItemCusto();
		item1.setNome("item1");
		list.add(item1);
		
		ItemCusto item2 = new ItemCusto();
		item2.setNome("item2");
		list.add(item2);
		
		System.out.println(list);
	}

	@Test
	public void testExecute() throws BusinessException {
//		String documento = "04175308000129";
		String documento = "";
		Calendar dataInicial = CalendarUtils.getCalendar(2009, Calendar.MARCH, 1);
		Calendar dataFinal = CalendarUtils.getCalendar(2009, Calendar.MARCH, 31);
//		List<ItemCusto> itemCustoList = UtilsCrud.objectList(this.serviceManager, ItemCusto.class, "nome like 'Material%'", null);
		Situacao situacao = Situacao.TODOS;
		
		ServiceData sd = new ServiceData(ListarPosicaoContratoService.SERVICE_NAME, null);
		sd.getArgumentList().setProperty(ListarPosicaoContratoService.IN_DOCUMENTO_OPT, documento);
		sd.getArgumentList().setProperty(ListarPosicaoContratoService.IN_CONTA_LIST_OPT, new Long[0]);
//		sd.getArgumentList().setProperty(ListarPosicaoContratoService.IN_ITEM_CUSTO_LIST_OPT, itemCustoList);
		sd.getArgumentList().setProperty(ListarPosicaoContratoService.IN_DATA_INICIAL_OPT, dataInicial);
		sd.getArgumentList().setProperty(ListarPosicaoContratoService.IN_DATA_FINAL_OPT, dataFinal);
		sd.getArgumentList().setProperty(ListarPosicaoContratoService.IN_SITUACAO_OPT, situacao);
		this.serviceManager.execute(sd);
		
		List<Lancamento> list = sd.getFirstOutput();
		assertTrue(list.size() > 0);
		
		System.out.printf("%-6s ", "ID");
		System.out.printf("%-30s ", "Nome");
		System.out.printf("%-30s ", "Descrição");
		System.out.printf("%10s ", "Valor");
		System.out.println();
//		System.out.println("------ ------------------------------ ");
		for (Lancamento lancamento : list) {
			System.out.printf("%6s ", lancamento.getId());
			System.out.printf("%-30s ", lancamento.getContrato().getPessoa().getNome());
			System.out.printf("%-30s ", lancamento.getDescricao());
			System.out.printf("%10.2f ", lancamento.getSaldo());
			System.out.println();
//			System.out.println(
//					lancamento.getId() + " | " + 
//					CalendarUtils.formatDate(lancamento.getData()) + " | " + 
//					lancamento.getLancamentoItens().get(0).getItemCusto());
		}
	}

//	@Test
	public void testSituacao() throws Exception {
		Lancamento lancamento = new Lancamento();
		lancamento.setLancamentoSituacao(LancamentoSituacao.PENDENTE);
		System.out.println(lancamento.getLancamentoSituacao().toString());
	}
	
//	@Test
	public void operator() throws BusinessException {
		List<ItemCusto> itemCustoList = new ArrayList<ItemCusto>();
		ItemCusto item1 = new ItemCusto();
		item1.setId(1L);
		ItemCusto item2 = new ItemCusto();
		item2.setId(2L);
		itemCustoList.add(item1);
		itemCustoList.add(item2);
		QueryCondiction qc = new QueryCondiction(
				this.serviceManager.getEntityManager(),
				Lancamento.class,
				Lancamento.LANCAMENTO_ITENS+'.'+LancamentoItem.ITEM_CUSTO,
				Operator.IN,
				itemCustoList.toString(),
				"");
		System.out.println(qc.toString());
	}
	
//	@Test
	public void testPrintf() {
		System.out.printf("%-6s ", "ID");
		System.out.printf("%-10s ", "Data");
		System.out.println();
		System.out.println("------ ---------- ");
		System.out.printf("%6s ", "1");
		System.out.printf("%-10s ", "01/01/2009");
		System.out.println();
		System.out.printf("%6s ", "2");
		System.out.printf("%-10s ", "02/01/2009");
		System.out.println();

	}
}
