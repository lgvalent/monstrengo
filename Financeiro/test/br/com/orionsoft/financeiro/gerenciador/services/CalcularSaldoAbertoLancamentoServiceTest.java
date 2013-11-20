package br.com.orionsoft.financeiro.gerenciador.services;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;

import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.core.util.DecimalUtils;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

public class CalcularSaldoAbertoLancamentoServiceTest extends ServiceBasicTest {

	@Test
	public void calcularLancado() throws Exception {
		InserirLancamentoServiceTest inserirLancamentoServiceTest = new InserirLancamentoServiceTest();
		inserirLancamentoServiceTest.setUp();
		inserirLancamentoServiceTest.inserir();
		inserirLancamentoServiceTest.tearDown();
		Lancamento lancamento = inserirLancamentoServiceTest.getLancamento();
		
		ServiceData sd = new ServiceData(CalcularSaldoAbertoLancamentoService.SERVICE_NAME, null);
		sd.getArgumentList().setProperty(CalcularSaldoAbertoLancamentoService.IN_LANCAMENTO, lancamento);
		this.serviceManager.execute(sd);
		
		BigDecimal saldo = sd.getFirstOutput();
		
		assertEquals(DecimalUtils.getBigDecimal(30.00), saldo);
	}

	@Test
	public void calcularQuitado() throws Exception {
		QuitarLancamentoServiceTest quitarLancamentoServiceTest = new QuitarLancamentoServiceTest();
		quitarLancamentoServiceTest.setUp();
		quitarLancamentoServiceTest.quitar();
		quitarLancamentoServiceTest.tearDown();
		Lancamento lancamento = quitarLancamentoServiceTest.getLancamentoMovimento().getLancamento();
		lancamento = UtilsCrud.objectRetrieve(this.serviceManager, Lancamento.class, lancamento.getId(), null);
		
		ServiceData sd = new ServiceData(CalcularSaldoAbertoLancamentoService.SERVICE_NAME, null);
		sd.getArgumentList().setProperty(CalcularSaldoAbertoLancamentoService.IN_LANCAMENTO, lancamento);
		this.serviceManager.execute(sd);
		
		BigDecimal saldo = sd.getFirstOutput();
		
		assertEquals(DecimalUtils.ZERO, saldo);
	}

}
