package br.com.orionsoft.monstrengo.security.services;

import org.junit.Assert;
import org.junit.Test;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.test.ProcessBasicTest;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;
import br.com.orionsoft.monstrengo.security.services.CreateSecurityStructureService;

/**
 * @author Lucio 20060430
 * @version 20060504
 *
 */
public class CreateSecurityStructureServiceTestCase extends ProcessBasicTest{

	/**
	 * @param args
	 */
	public static void main (String[] args) {
		junit.textui.TestRunner.run(CreateSecurityStructureServiceTestCase.class);
	}			
		
	@Test
    public void testRunAll(){
		try {
			//realiza comparações com o Banco de Dados 
			ServiceData service = new ServiceData(CreateSecurityStructureService.SERVICE_NAME, null);
			service.getArgumentList().setProperty(CreateSecurityStructureService.IN_USER_LOGIN, "admin");
			service.getArgumentList().setProperty(CreateSecurityStructureService.IN_GROUP_NAME, "admin");
			service.getArgumentList().setProperty(CreateSecurityStructureService.IN_ALLOW_ALL_BOOL, true);
			service.getArgumentList().setProperty(CreateSecurityStructureService.IN_PROCESS_MANAGER, this.processManager);
            this.processManager.getServiceManager().execute(service);
            
            UtilsTest.showMessageList(service.getMessageList());

            //realiza comparações com o Banco de Dados 
			service = new ServiceData(CreateSecurityStructureService.SERVICE_NAME, null);
			service.getArgumentList().setProperty(CreateSecurityStructureService.IN_USER_LOGIN, "user");
			service.getArgumentList().setProperty(CreateSecurityStructureService.IN_GROUP_NAME, "user");
			service.getArgumentList().setProperty(CreateSecurityStructureService.IN_ALLOW_ALL_BOOL, false);
            this.processManager.getServiceManager().execute(service);
            
            UtilsTest.showMessageList(service.getMessageList());
			
		} catch (BusinessException e) {

            UtilsTest.showMessageList(e.getErrorList());
            
            Assert.assertTrue(false);
		}
		
	}
}
