package br.com.orionsoft.basic.process;

import org.junit.Assert;
import org.junit.Test;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.test.ProcessBasicTest;

public class ConsultarCNPJProcessTestCase extends ProcessBasicTest {

	@Test
	public void test() {
		try {
			ConsultarCNPJProcess process = (ConsultarCNPJProcess) processManager.createProcessByName(ConsultarCNPJProcess.PROCESS_NAME, null);
			process.setCnpj("04175308000129");
			process.consultarEmpresaPorCNPJ();

		} catch (BusinessException e) {
            e.printStackTrace();
        	Assert.assertTrue(false);
        }
	}
}
