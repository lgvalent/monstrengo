package br.com.orionsoft.financeiro.gerenciador.services;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import br.com.orionsoft.financeiro.gerenciador.entities.Transacao;
import br.com.orionsoft.financeiro.gerenciador.services.ListarLancamentoMovimentoService.QueryLancamentoMovimento;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;

public class ListarLancamentoMovimentoServiceTest extends ServiceBasicTest {

	@SuppressWarnings("deprecation")
	@Test
	public void testExecute() throws BusinessException {
//		List<Conta> conta = new ArrayList<Conta>(0);
//		Conta conta = UtilsCrud.objectRetrieve(this.serviceManager, Conta.class, 1l, null);
//		conta.add(conta);
		
//		Long conta = new Long(1);
//		Long contrato = new Long(1);
		Calendar dataLancamentoInicial = CalendarUtils.getCalendar(2009, Calendar.APRIL, 1);
		Calendar dataLancamentoFinal = CalendarUtils.getCalendar(2009, Calendar.APRIL, 30);
//		Long centroCusto = new Long(1);
//		String itemCustoList = null;
		ListarLancamentoMovimentoService.Ordem ordem = ListarLancamentoMovimentoService.Ordem.NOME;
		ListarLancamentoMovimentoService.Listagem tipoListagem = ListarLancamentoMovimentoService.Listagem.COMPLETA;
		int[] transacao = {Transacao.CREDITO.ordinal(), Transacao.DEBITO.ordinal()};
		
		ServiceData sd = new ServiceData(ListarLancamentoMovimentoService.SERVICE_NAME,null);
//		sd.getArgumentList().setProperty(ListarLancamentoMovimentoService.IN_CONTA_ID_OPT, conta);
//		sd.getArgumentList().setProperty(ListarLancamentoMovimentoService.IN_CONTRATO_ID_OPT, contrato);
		sd.getArgumentList().setProperty(ListarLancamentoMovimentoService.IN_DATA_LANCAMENTO_INICIAL_OPT, dataLancamentoInicial);
		sd.getArgumentList().setProperty(ListarLancamentoMovimentoService.IN_DATA_LANCAMENTO_FINAL_OPT, dataLancamentoFinal);
		sd.getArgumentList().setProperty(ListarLancamentoMovimentoService.IN_DATA_QUITACAO_INICIAL_OPT, dataLancamentoInicial);
		sd.getArgumentList().setProperty(ListarLancamentoMovimentoService.IN_DATA_QUITACAO_FINAL_OPT, dataLancamentoFinal);
//		sd.getArgumentList().setProperty(ListarLancamentoMovimentoService.IN_CENTRO_CUSTO_ID_OPT, centroCusto);
//		sd.getArgumentList().setProperty(ListarLancamentoMovimentoService.IN_ITEM_CUSTO_LIST_OPT, itemCustoList);
		sd.getArgumentList().setProperty(ListarLancamentoMovimentoService.IN_ORDEM_OPT, ordem);
		sd.getArgumentList().setProperty(ListarLancamentoMovimentoService.IN_LISTAGEM_OPT, tipoListagem);
		sd.getArgumentList().setProperty(ListarLancamentoMovimentoService.IN_TRANSACAO_OPT, transacao);
		this.serviceManager.execute(sd);
		List<QueryLancamentoMovimento> list = sd.getFirstOutput();
		assertNotNull(list);
		assertFalse(list.size() == 0);
		System.out.println(list.size());
		
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
}
