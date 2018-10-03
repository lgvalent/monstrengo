package br.com.orionsoft.basic.process;

import org.junit.Assert;
import org.junit.Test;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.test.ProcessBasicTest;

public class ConsultarCEPProcessTestCase extends ProcessBasicTest {

	@Test
	public void test() {
		try {
			ConsultarCEPProcess process = (ConsultarCEPProcess) processManager.createProcessByName(ConsultarCEPProcess.PROCESS_NAME, null);
			process.setCep("87010260");
			process.consultarEnderecoPorCep();

		} catch (BusinessException e) {
            e.printStackTrace();
        	Assert.assertTrue(false);
        }
	}

}
