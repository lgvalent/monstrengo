package br.com.orionsoft.basico.cadastro.pessoa;

import org.junit.Assert;
import org.junit.Test;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.test.ProcessBasicTest;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;
import br.com.orionsoft.monstrengo.security.services.ManageSecurityStructureService;

/**
 * Esse classe testa o relacionamento entre as classes pessoa, juridica e fisica.
 * Tentando verificar as dificuldades de se lidar com herança e não associação.
 * 
 * @author marcia
 *
 */
public class ManageStructureServiceTestCase extends ProcessBasicTest
{

	@Test
    public void testRun()
    {
        try
        {
            ServiceData sd = new ServiceData(ManageSecurityStructureService.SERVICE_NAME, null);
            sd.getArgumentList().setProperty(ManageSecurityStructureService.IN_PROCESS_MANAGER, this.processManager);
            this.processManager.getServiceManager().execute(sd);
        } catch (BusinessException e)
        {
            UtilsTest.showMessageList(e.getErrorList());
            
            Assert.assertTrue(false);
        }
    }

}
