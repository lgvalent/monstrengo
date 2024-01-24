package br.com.orionsoft.basic.services;

import org.junit.Assert;
import org.junit.Test;

import br.com.orionsoft.basic.services.ConsultarCEPService.ConsultarCepBean;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;

public class ConsultarCEPServiceTestCase extends ServiceBasicTest{
	@Test
	public void testExecute() {
		try {
			String cep = "87010260";
			ServiceData service = new ServiceData(ConsultarCEPService.SERVICE_NAME, null);
			service.getArgumentList().setProperty(ConsultarCEPService.IN_CEP, cep);;
			this.serviceManager.execute(service);
			
			ConsultarCepBean result = service.getFirstOutput();
			System.out.println(result.getLogradouro());
		} catch (BusinessException e) {
            UtilsTest.showMessageList(e.getErrorList());
            Assert.assertTrue(false);
		}
	}

}
