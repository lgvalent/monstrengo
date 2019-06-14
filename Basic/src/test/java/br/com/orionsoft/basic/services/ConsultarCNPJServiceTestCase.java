package br.com.orionsoft.basic.services;

import org.junit.Assert;
import org.junit.Test;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;

public class ConsultarCNPJServiceTestCase extends ServiceBasicTest {

	@Test
	public void testExecute() {
		try {
			String cnpj = "04175308000129";
			ServiceData service = new ServiceData(ConsultarCNPJService.SERVICE_NAME, null);
			service.getArgumentList().setProperty(ConsultarCNPJService.IN_CNPJ, cnpj);;
			this.serviceManager.execute(service);
		} catch (BusinessException e) {
            UtilsTest.showMessageList(e.getErrorList());
            Assert.assertTrue(false);
		}
	}

}
