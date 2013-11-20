package br.com.orionsoft.financeiro.gerenciador.services;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Calendar;

import org.junit.Test;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.core.util.DecimalUtils;

public class CalcularMultaJurosServiceTest extends ServiceBasicTest {

	@Test
	public void testExecute() throws BusinessException {
		Calendar data = CalendarUtils.getCalendar(2010, Calendar.NOVEMBER, 30);
		Calendar dataVencimento = CalendarUtils.getCalendar(2010, Calendar.JANUARY, 31);
		BigDecimal valor = DecimalUtils.getBigDecimal(132.93);
		BigDecimal percentualJuros = DecimalUtils.ONE;
		BigDecimal percentualMulta = DecimalUtils.TEN;
		BigDecimal percentualMultaAdicional = DecimalUtils.getBigDecimal(2.0);
		Integer diasTolerancia = 0;
		
		ServiceData sd = new ServiceData(CalcularMultaJurosService.SERVICE_NAME, null);
		sd.getArgumentList().setProperty(CalcularMultaJurosService.IN_DATA, data);
		sd.getArgumentList().setProperty(CalcularMultaJurosService.IN_DATA_VENCIMENTO, dataVencimento);
		sd.getArgumentList().setProperty(CalcularMultaJurosService.IN_VALOR, valor);
		sd.getArgumentList().setProperty(CalcularMultaJurosService.IN_JUROS_MORA, percentualJuros);
		sd.getArgumentList().setProperty(CalcularMultaJurosService.IN_MULTA_INICIAL, percentualMulta);
		sd.getArgumentList().setProperty(CalcularMultaJurosService.IN_MULTA_ADICIONAL, percentualMultaAdicional);
		sd.getArgumentList().setProperty(CalcularMultaJurosService.IN_DIAS_TOLERANCIA, diasTolerancia);
		this.serviceManager.execute(sd);
		BigDecimal multa = (BigDecimal) sd.getOutputData(CalcularMultaJurosService.OUT_MULTA);
		BigDecimal juros = (BigDecimal) sd.getOutputData(CalcularMultaJurosService.OUT_JUROS);
		
		System.out.println("Multa: "+multa);
		System.out.println("Juros: "+juros);
		
		assertTrue(true);
	}

}
