package br.com.orionsoft.monstrengo.mail.services;

import org.junit.Assert;
import org.junit.Test;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.test.ProcessBasicTest;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.processes.CreateProcess;
import br.com.orionsoft.monstrengo.mail.entities.EmailAccount;
import br.com.orionsoft.monstrengo.mail.services.SendMailService;

/**
 * @author Lucio 20060511
 * @version 20060511
 *
 */
public class SendMailServiceTestCase extends ProcessBasicTest{

	/**
	 * @param args
	 */
	public static void main (String[] args) {
		junit.textui.TestRunner.run(SendMailServiceTestCase.class);
	}			
		
    @Test
    public void testRun(){
		try {
			EmailAccount account = new EmailAccount();
			
			/* Este servidor requer autentica��o. Mas o rafael liberou no Relay o ip da Agile e Uning� */
			account.setHost("smtp.gmail.com");
			account.setSenderMail("lgvalent@gmail.com");
			account.setSenderName("Lucio Valentin");
			account.setUser("lgvalent@gmail.com");
			account.setPassword("********");
			
			ServiceData service = new ServiceData(SendMailService.SERVICE_NAME, null);
			service.getArgumentList().setProperty(SendMailService.IN_EMAIL_ACCOUNT_OPT, account);
			service.getArgumentList().setProperty(SendMailService.IN_MESSAGE, "TESTE AGILE");
			service.getArgumentList().setProperty(SendMailService.IN_RECIPIENT_OPT, "lgvalent@gmail.com");
			service.getArgumentList().setProperty(SendMailService.IN_SUBJECT, "Assunto teste");
            this.processManager.getServiceManager().execute(service);
            
		} catch (BusinessException e) {
	
	        UtilsTest.showMessageList(e.getErrorList());
	        
	        Assert.fail(e.getMessage());
		}
		
	}

    @Test
    public void testUseAsDefault(){
		try {
			CreateProcess process = (CreateProcess) this.processManager.createProcessByName(CreateProcess.PROCESS_NAME, getAdminSession());
			process.setEntityType(EmailAccount.class);
			@SuppressWarnings("unchecked")
			IEntity<EmailAccount> emailAccount = process.retrieveEntity();
			EmailAccount account = emailAccount.getObject();
			
			/* Este servidor requer autentica��o. Mas o rafael liberou no Relay o ip da Agile e Uning� */
			account.setUseAsDefault(true);
			account.setHost("smtp.gmail.com");
			account.setSenderMail("lgvalent@gmail.com");
			account.setSenderName("Lucio Valentin");
			account.setUser("lgvalent@gmail.com");
			account.setPassword("********");
			process.runUpdate();
			process.finish();
			
			/* Chama o servi�o sem passar uma conta para que use a marcada como useAsDefault */
			ServiceData service = new ServiceData(SendMailService.SERVICE_NAME, null);
			service.getArgumentList().setProperty(SendMailService.IN_MESSAGE, "TESTE AGILE");
			service.getArgumentList().setProperty(SendMailService.IN_RECIPIENT_OPT, "lgvalent@gmail.com");
			service.getArgumentList().setProperty(SendMailService.IN_SUBJECT, "Assunto teste");
            this.processManager.getServiceManager().execute(service);
            
		} catch (BusinessException e) {
	
	        UtilsTest.showMessageList(e.getErrorList());
	        
	        Assert.fail(e.getMessage());
		}
		
	}

}