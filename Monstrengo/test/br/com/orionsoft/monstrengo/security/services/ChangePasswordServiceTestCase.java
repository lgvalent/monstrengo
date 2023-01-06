package br.com.orionsoft.monstrengo.security.services;

import org.junit.Assert;
import org.junit.Test;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;
import br.com.orionsoft.monstrengo.security.services.ChangePasswordService;

/**
 * @author Lucio 20060511
 * @version 20060511
 *
 */
public class ChangePasswordServiceTestCase extends ServiceBasicTest{

	/**
	 * @param args
	 */
	public static void main (String[] args) {
		junit.textui.TestRunner.run(ChangePasswordServiceTestCase.class);
	}			
	@Test
    public void testRun(){
		try {
			//realiza comparações com o Banco de Dados 
			ServiceData service = new ServiceData(ChangePasswordService.SERVICE_NAME, null);
			service.getArgumentList().setProperty(ChangePasswordService.IN_LOGIN, "admin");
			service.getArgumentList().setProperty(ChangePasswordService.IN_CURRENT_PASSWORD, "admin");
			service.getArgumentList().setProperty(ChangePasswordService.IN_NEW_PASSWORD, "nimda");
            this.serviceManager.execute(service);
            
            UtilsTest.showMessageList(service.getMessageList());
			
			//realiza comparações com o Banco de Dados 
			service = new ServiceData(ChangePasswordService.SERVICE_NAME, null);
			service.getArgumentList().setProperty(ChangePasswordService.IN_LOGIN, "admin");
			service.getArgumentList().setProperty(ChangePasswordService.IN_CURRENT_PASSWORD, "nimda");
			service.getArgumentList().setProperty(ChangePasswordService.IN_NEW_PASSWORD, "admin");
            this.serviceManager.execute(service);
            
            UtilsTest.showMessageList(service.getMessageList());
			
		} catch (BusinessException e) {

            UtilsTest.showMessageList(e.getErrorList());
            
            Assert.assertTrue(false);
		}
		
	}
    @Test
	public void testOverwrite(){
		try {
			//realiza comparações com o Banco de Dados 
			ServiceData service = new ServiceData(ChangePasswordService.SERVICE_NAME, null);
			service.getArgumentList().setProperty(ChangePasswordService.IN_LOGIN, "admin");
			service.getArgumentList().setProperty(ChangePasswordService.IN_NEW_PASSWORD, "admin");
			service.getArgumentList().setProperty(ChangePasswordService.IN_OVERWRITE_BOL, true);
	        this.serviceManager.execute(service);
	        
	        UtilsTest.showMessageList(service.getMessageList());
			
			//realiza comparações com o Banco de Dados 
			service = new ServiceData(ChangePasswordService.SERVICE_NAME, null);
			service.getArgumentList().setProperty(ChangePasswordService.IN_LOGIN, "user");
			service.getArgumentList().setProperty(ChangePasswordService.IN_NEW_PASSWORD, "user");
			service.getArgumentList().setProperty(ChangePasswordService.IN_OVERWRITE_BOL, true);
	        this.serviceManager.execute(service);
	        
	        UtilsTest.showMessageList(service.getMessageList());
			
		} catch (BusinessException e) {
	
	        UtilsTest.showMessageList(e.getErrorList());
	        
	        Assert.assertTrue(false);
		}
		
	}
}
