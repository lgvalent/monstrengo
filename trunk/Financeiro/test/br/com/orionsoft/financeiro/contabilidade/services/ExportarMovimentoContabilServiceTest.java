package br.com.orionsoft.financeiro.contabilidade.services;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;

public class ExportarMovimentoContabilServiceTest extends ServiceBasicTest {
	
	@Test
	public void testExecute() throws BusinessException{
		
		Calendar dataInicial = CalendarUtils.getCalendar(2012, Calendar.NOVEMBER, 1);
		Calendar dataFinal = CalendarUtils.getCalendar(2012, Calendar.NOVEMBER, 1);
		
		ServiceData sd = new ServiceData(ExportarMovimentoContabilService.SERVICE_NAME,null);
		sd.getArgumentList().setProperty(ExportarMovimentoContabilService.IN_DATA_INICIAL, dataInicial);
		sd.getArgumentList().setProperty(ExportarMovimentoContabilService.IN_DATA_FINAL, dataFinal);
		this.serviceManager.execute(sd);

		List<QueryLancamentoContabil> listPrevisto = sd.getOutputData(ExportarMovimentoContabilService.OUT_PREVISTO);
		assertNotNull(listPrevisto);
		assertFalse(listPrevisto.size() == 0);
		show(listPrevisto);
	
		List<QueryLancamentoContabil> listMovimento = sd.getOutputData(ExportarMovimentoContabilService.OUT_MOVIMENTO);
		assertNotNull(listMovimento);
		assertFalse(listMovimento.size() == 0);
		show(listMovimento);

		List<QueryLancamentoContabil> listCompensacao = sd.getOutputData(ExportarMovimentoContabilService.OUT_COMPENSACAO);
		assertNotNull(listCompensacao);
		assertFalse(listCompensacao.size() == 0);
		show(listCompensacao);
	}

	private void show(List<QueryLancamentoContabil> listMovimento) {
		System.out.println("Registros selecionados : " + listMovimento.size());
		System.out.printf("%-6s|", "ID");
		System.out.printf("%-30s|", "Pessoa");
		System.out.printf("%-30s|", "Descrição Lançamento");
		System.out.printf("%-30s|", "Descrição Item");
		System.out.printf("%-10s|", "Valor");
		System.out.printf("%-10s|", "Data comp.");
		System.out.printf("%-20s|", "Conta prevista CCC");
		System.out.printf("%-20s|", "Contrato CCC");
		System.out.printf("%-20s|", "Centro Custo CCC");
		System.out.printf("%-20s|", "Item Custo CCC");
		System.out.println();
		for (QueryLancamentoContabil queryLancamento: listMovimento){
			System.out.printf("%06d|", queryLancamento.getId());
			System.out.printf("%30s|", StringUtils.left(queryLancamento.getNomePessoa(), 30));
			System.out.printf("%30s|", StringUtils.left(queryLancamento.getDescricaoLancamento(), 30));
			System.out.printf("%30s|", StringUtils.left(queryLancamento.getDescricaoItem(), 30));
			System.out.printf("%10.2f|", queryLancamento.getValor());
			System.out.printf("%-10s|", StringUtils.left(queryLancamento.getDataCompetencia().getTime().toLocaleString(), 10));
			System.out.printf("%-20s|", StringUtils.left(queryLancamento.getContaPrevistaCcc(), 20));
			System.out.printf("%-20s|", StringUtils.left(queryLancamento.getContratoCcc(), 20));
			System.out.printf("%-20s|", StringUtils.left(queryLancamento.getCentroCustoCcc(), 20));
			System.out.printf("%-20s|", StringUtils.left(queryLancamento.getItemCustoCcc(), 20));
			System.out.println();
		}
	}
}
