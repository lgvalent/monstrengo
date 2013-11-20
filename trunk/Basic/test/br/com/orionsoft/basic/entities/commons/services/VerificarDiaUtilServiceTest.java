package br.com.orionsoft.basic.entities.commons.services;

import java.util.Calendar;

import org.junit.Assert;
import org.junit.Test;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;

/*
 * Dados necessários para que os testes funcionem.
INSERT INTO `basic_feriado` (`discriminator`, `id`, `descricao`, `dia`, `fixo`, `diaFinal`, `mesFinal`, `mes`, `ano`, `anoFinal`) VALUES 
('f', 1, 'Páscoa', 6, '0', NULL, NULL, 4, 2007, 0),
('f', 2, 'Maringá', 10, '1', NULL, NULL, 5, 2007, 0),
('r', 3, 'Recesso', 26, '0', 28, 6, 6, 2007, 2007),
('r', 4, 'Férias', 4, '0', 18, 7, 7, 2007, 2007);
 */
public class VerificarDiaUtilServiceTest extends ServiceBasicTest {

	/*
	 * Verificando 29/06/2007 sexta-feira
	 * Retorno: true
	 */
	@Test
	public void testSextaFeira() throws BusinessException {
		Calendar data = CalendarUtils.getCalendar(2007, Calendar.JUNE, 29);
		
		ServiceData sd = new ServiceData(VerificarDiaUtilService.SERVICE_NAME, null);
		sd.getArgumentList().setProperty(VerificarDiaUtilService.IN_DATA, data);
		this.serviceManager.execute(sd);
		
		Assert.assertTrue((Boolean) sd.getOutputData(VerificarDiaUtilService.OUT_DIA_UTIL));
		Assert.assertEquals(sd.getOutputData(VerificarDiaUtilService.OUT_DIA_UTIL_ANTERIOR), data);
		Assert.assertEquals(sd.getOutputData(VerificarDiaUtilService.OUT_DIA_UTIL_PROXIMO), data);
	}

	/*
	 * Verificando 30/06/2007 sábado
	 * Retorno: false, 29/06/2007, 02/07/2007
	 */
	@Test
	public void testSabado() throws BusinessException {
		Calendar data = CalendarUtils.getCalendar(2007, Calendar.JUNE, 30);
		Calendar dataAnt = CalendarUtils.getCalendar(2007, Calendar.JUNE, 29);
		Calendar dataPro = CalendarUtils.getCalendar(2007, Calendar.JULY, 2);
		
		ServiceData sd = new ServiceData(VerificarDiaUtilService.SERVICE_NAME, null);
		sd.getArgumentList().setProperty(VerificarDiaUtilService.IN_DATA, data);
		this.serviceManager.execute(sd);
		
		Assert.assertFalse((Boolean) sd.getOutputData(VerificarDiaUtilService.OUT_DIA_UTIL));
		Assert.assertEquals(sd.getOutputData(VerificarDiaUtilService.OUT_DIA_UTIL_ANTERIOR), dataAnt);
		Assert.assertEquals(sd.getOutputData(VerificarDiaUtilService.OUT_DIA_UTIL_PROXIMO), dataPro);
	}

	/*
	 * Verificando 30/06/2007 sábado
	 * Retorno: true
	 */
	@Test
	public void testIgnorarSabado() throws BusinessException {
		Calendar data = CalendarUtils.getCalendar(2007, Calendar.JUNE, 30);
		
		ServiceData sd = new ServiceData(VerificarDiaUtilService.SERVICE_NAME, null);
		sd.getArgumentList().setProperty(VerificarDiaUtilService.IN_DATA, data);
		sd.getArgumentList().setProperty(VerificarDiaUtilService.IN_IGNORAR_SABADO_OPT, true);
		this.serviceManager.execute(sd);
		
		Assert.assertTrue((Boolean) sd.getOutputData(VerificarDiaUtilService.OUT_DIA_UTIL));
		Assert.assertEquals(sd.getOutputData(VerificarDiaUtilService.OUT_DIA_UTIL_ANTERIOR), data);
		Assert.assertEquals(sd.getOutputData(VerificarDiaUtilService.OUT_DIA_UTIL_PROXIMO), data);
	}

	/*
	 * Verificando 01/07/2007 domingo
	 * Retorno: false, 29/06/2007, 02/07/2007
	 */
	@Test
	public void testDomingo() throws BusinessException {
		Calendar data = CalendarUtils.getCalendar(2007, Calendar.JULY, 1);
		Calendar dataAnt = CalendarUtils.getCalendar(2007, Calendar.JUNE, 29);
		Calendar dataPro = CalendarUtils.getCalendar(2007, Calendar.JULY, 2);
		
		ServiceData sd = new ServiceData(VerificarDiaUtilService.SERVICE_NAME, null);
		sd.getArgumentList().setProperty(VerificarDiaUtilService.IN_DATA, data);
		this.serviceManager.execute(sd);
		
		Assert.assertFalse((Boolean) sd.getOutputData(VerificarDiaUtilService.OUT_DIA_UTIL));
		Assert.assertEquals(sd.getOutputData(VerificarDiaUtilService.OUT_DIA_UTIL_ANTERIOR), dataAnt);
		Assert.assertEquals(sd.getOutputData(VerificarDiaUtilService.OUT_DIA_UTIL_PROXIMO), dataPro);
	}

	/*
	 * Verificando 02/07/2007 segunda-feira
	 * Retorno: true
	 */
	@Test
	public void testSegundaFeira() throws BusinessException {
		Calendar data = CalendarUtils.getCalendar(2007, Calendar.JULY, 2);
		
		ServiceData sd = new ServiceData(VerificarDiaUtilService.SERVICE_NAME, null);
		sd.getArgumentList().setProperty(VerificarDiaUtilService.IN_DATA, data);
		this.serviceManager.execute(sd);
		
		Assert.assertTrue((Boolean) sd.getOutputData(VerificarDiaUtilService.OUT_DIA_UTIL));
		Assert.assertEquals(sd.getOutputData(VerificarDiaUtilService.OUT_DIA_UTIL_ANTERIOR), data);
		Assert.assertEquals(sd.getOutputData(VerificarDiaUtilService.OUT_DIA_UTIL_PROXIMO), data);
	}

	/*
	 * Verificando 06/04/2007 Páscoa
	 * Retorno: false, 05/04/2007, 09/04/2007
	 */
	@Test
	public void testFeriadoPascoa() throws BusinessException {
		Calendar data = CalendarUtils.getCalendar(2007, Calendar.APRIL, 6);
		Calendar dataAnt = CalendarUtils.getCalendar(2007, Calendar.APRIL, 5);
		Calendar dataPro = CalendarUtils.getCalendar(2007, Calendar.APRIL, 9);
		
		ServiceData sd = new ServiceData(VerificarDiaUtilService.SERVICE_NAME, null);
		sd.getArgumentList().setProperty(VerificarDiaUtilService.IN_DATA, data);
		this.serviceManager.execute(sd);
		
		Assert.assertFalse((Boolean) sd.getOutputData(VerificarDiaUtilService.OUT_DIA_UTIL));
		Assert.assertEquals(sd.getOutputData(VerificarDiaUtilService.OUT_DIA_UTIL_ANTERIOR), dataAnt);
		Assert.assertEquals(sd.getOutputData(VerificarDiaUtilService.OUT_DIA_UTIL_PROXIMO), dataPro);
	}

	/*
	 * Verificando 10/05/2007 Maringá
	 * Retorno: false, 09/05/2007, 11/05/2007
	 */
	@Test
	public void testFeriadoMaringa() throws BusinessException {
		Calendar data = CalendarUtils.getCalendar(2007, Calendar.MAY, 10);
		Calendar dataAnt = CalendarUtils.getCalendar(2007, Calendar.MAY, 9);
		Calendar dataPro = CalendarUtils.getCalendar(2007, Calendar.MAY, 11);
		
		ServiceData sd = new ServiceData(VerificarDiaUtilService.SERVICE_NAME, null);
		sd.getArgumentList().setProperty(VerificarDiaUtilService.IN_DATA, data);
		this.serviceManager.execute(sd);
		
		Assert.assertFalse((Boolean) sd.getOutputData(VerificarDiaUtilService.OUT_DIA_UTIL));
		Assert.assertEquals(sd.getOutputData(VerificarDiaUtilService.OUT_DIA_UTIL_ANTERIOR), dataAnt);
		Assert.assertEquals(sd.getOutputData(VerificarDiaUtilService.OUT_DIA_UTIL_PROXIMO), dataPro);
	}

	/*
	 * Verificando 27/06/2007 Recesso
	 * Retorno: false, 25/06/2007, 29/05/2007
	 */
	@Test
	public void testRecesso26a28junho() throws BusinessException {
		Calendar data = CalendarUtils.getCalendar(2007, Calendar.JUNE, 27);
		Calendar dataAnt = CalendarUtils.getCalendar(2007, Calendar.JUNE, 25);
		Calendar dataPro = CalendarUtils.getCalendar(2007, Calendar.JUNE, 29);
		
		ServiceData sd = new ServiceData(VerificarDiaUtilService.SERVICE_NAME, null);
		sd.getArgumentList().setProperty(VerificarDiaUtilService.IN_DATA, data);
		this.serviceManager.execute(sd);
		
		Assert.assertFalse((Boolean) sd.getOutputData(VerificarDiaUtilService.OUT_DIA_UTIL));
		Assert.assertEquals(sd.getOutputData(VerificarDiaUtilService.OUT_DIA_UTIL_ANTERIOR), dataAnt);
		Assert.assertEquals(sd.getOutputData(VerificarDiaUtilService.OUT_DIA_UTIL_PROXIMO), dataPro);
	}

	/*
	 * Verificando 10/07/2007 Recesso
	 * Retorno: false, 03/07/2007, 19/07/2007
	 */
	@Test
	public void testRecesso04a18julho() throws BusinessException {
		Calendar data = CalendarUtils.getCalendar(2007, Calendar.JULY, 10);
		Calendar dataAnt = CalendarUtils.getCalendar(2007, Calendar.JULY, 3);
		Calendar dataPro = CalendarUtils.getCalendar(2007, Calendar.JULY, 19);
		
		ServiceData sd = new ServiceData(VerificarDiaUtilService.SERVICE_NAME, null);
		sd.getArgumentList().setProperty(VerificarDiaUtilService.IN_DATA, data);
		this.serviceManager.execute(sd);
		
		Assert.assertFalse((Boolean) sd.getOutputData(VerificarDiaUtilService.OUT_DIA_UTIL));
		Assert.assertEquals(sd.getOutputData(VerificarDiaUtilService.OUT_DIA_UTIL_ANTERIOR), dataAnt);
		Assert.assertEquals(sd.getOutputData(VerificarDiaUtilService.OUT_DIA_UTIL_PROXIMO), dataPro);
	}

}
