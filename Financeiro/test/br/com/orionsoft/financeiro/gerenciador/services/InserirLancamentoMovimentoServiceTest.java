package br.com.orionsoft.financeiro.gerenciador.services;

import java.math.BigDecimal;
import java.util.Calendar;

import org.junit.Assert;
import org.junit.Test;

import br.com.orionsoft.financeiro.gerenciador.entities.Conta;
import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimento;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimentoCategoria;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.core.util.DecimalUtils;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

public class InserirLancamentoMovimentoServiceTest extends ServiceBasicTest {
	private LancamentoMovimento lancamentoMovimento;
	
	@Test
	public void inserir()throws Exception{
		BigDecimal valorTotal = DecimalUtils.getBigDecimal(30.00);
		Calendar data = CalendarUtils.getCalendar(2007, Calendar.JULY, 05);
		Calendar dataCompensacao = CalendarUtils.getCalendar(2007, Calendar.JULY, 30);
		String descricao = "InserirLancamentoMovimentoServiceTest";
		Conta conta = UtilsCrud.objectRetrieve(this.serviceManager, Conta.class, 1l, null);

		InserirLancamentoServiceTest test = new InserirLancamentoServiceTest();
		test.setUp();
		test.inserir();
		test.tearDown();
		Lancamento lancamento = test.getLancamento();

		ServiceData sd = new ServiceData(InserirLancamentoMovimentoService.SERVICE_NAME, null);
		sd.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_LANCAMENTO, lancamento);
		sd.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_VALOR, valorTotal);
		sd.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_DATA, data);
		sd.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_DATA_COMPENSACAO_OPT, dataCompensacao);
		sd.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_DESCRICAO, descricao);
		sd.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_LANCAMENTO_MOVIMENTO_CATEGORIA, LancamentoMovimentoCategoria.QUITADO);
		sd.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_CONTA, conta);
		this.serviceManager.execute(sd);
		lancamentoMovimento = sd.getFirstOutput();
		Assert.assertNotNull(lancamentoMovimento);
		
		lancamentoMovimento = UtilsCrud.retrieve(this.serviceManager, LancamentoMovimento.class, lancamentoMovimento.getId(), null).getObject();
		Assert.assertEquals(lancamentoMovimento.getValor(), valorTotal);
		Assert.assertEquals(lancamentoMovimento.getData(), data);
		Assert.assertEquals(lancamentoMovimento.getDataCompensacao(), dataCompensacao);
		Assert.assertEquals(lancamentoMovimento.getDescricao(), descricao);
		Assert.assertEquals(lancamentoMovimento.getLancamentoMovimentoCategoria(), LancamentoMovimentoCategoria.QUITADO);
		Assert.assertEquals(lancamentoMovimento.getConta().toString(),conta.toString());
	}

	public LancamentoMovimento getLancamentoMovimento() {
		return lancamentoMovimento;
	}
}
