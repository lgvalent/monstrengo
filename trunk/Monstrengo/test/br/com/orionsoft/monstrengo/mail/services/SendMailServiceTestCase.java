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
		
	
	public static EmailAccount createTestMail(){
		EmailAccount account = new EmailAccount();
		
		/* Este servidor requer autenticação. Mas o rafael liberou no Relay o ip da Agile e Uningá */
		account.setHost("smtp.gmail.com");
		account.setSenderMail("user@gmail.com");
		account.setSenderName("Lucio Valentin");
		account.setUser("user@gmail.com");
		account.setPassword("********");
		
		return account;
	}
	
	
    @Test
    public void testRun(){
		try {
			EmailAccount account = createTestMail();
			
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

//    @Test
    public void testUseAsDefault(){
		try {
			CreateProcess process = (CreateProcess) this.processManager.createProcessByName(CreateProcess.PROCESS_NAME, getAdminSession());
			process.setEntityType(EmailAccount.class);
			@SuppressWarnings("unchecked")
			IEntity<EmailAccount> emailAccount = process.retrieveEntity();
			EmailAccount account = emailAccount.getObject();
			EmailAccount account1 = createTestMail();
			
			/* Este servidor requer autenticação. Mas o rafael liberou no Relay o ip da Agile e Uningá */
			account.setUseAsDefault(true);
			account.setHost(account1.getHost());
			account.setSenderMail(account1.getSenderMail());
			account.setSenderName(account1.getSenderName());
			account.setUser(account1.getUser());
			account.setPassword(account1.getPassword());
			process.runUpdate();
			process.finish();
			
			/* Chama o serviço sem passar uma conta para que use a marcada como useAsDefault */
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
