package br.com.orionsoft.financeiro.gerenciador.process;

import java.util.Calendar;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.core.test.ProcessBasicTest;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;

public class ListarItemCustoProcessTest extends ProcessBasicTest {
	private ListarItemCustoProcess process;
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		process = (ListarItemCustoProcess) this.processManager.createProcessByName(ListarItemCustoProcess.PROCESS_NAME, this.getAdminSession());
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
		process = null;
	}

//	@Test
	public void testRetrieveQuitados() throws ProcessException {
		Calendar dataInicial = CalendarUtils.getCalendar(2008, Calendar.MAY, 1);
		Calendar dataFinal = CalendarUtils.getCalendar(2008, Calendar.AUGUST, 31);
		
		process.setDataInicial(dataInicial);
		process.setDataFinal(dataFinal);
		process.retrieveQuitados();
	}

	@Test
	public void testCalcularSaldoInicial() {
		process.setContaId(1);
		System.out.println("Saldo inicial: " + process.calcularSaldoInicial());
	}

}
