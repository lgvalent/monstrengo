package br.com.orionsoft.monstrengo.mail.services;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.mail.internet.MimeBodyPart;

import org.junit.Assert;
import org.junit.Test;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.test.ProcessBasicTest;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.processes.CreateProcess;
import br.com.orionsoft.monstrengo.mail.entities.EmailAccount;

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
		
		account.setHost("smtp.gmail.com:587");
		account.setSenderMail("user@user.com");
		account.setSenderName("SUPORTE");
		account.setUser("user@user.com");
		account.setPassword("xxxxxx");
		account.setProperties("mail.smtp.auth=true;mail.smtp.starttls.enable=true");
		
		return account;
	}
	
	
    @Test
    public void testRun(){
		try {
			EmailAccount account = createTestMail();
			
			ServiceData service = new ServiceData(SendMailService.SERVICE_NAME, null);
			service.getArgumentList().setProperty(SendMailService.IN_EMAIL_ACCOUNT_OPT, account);
			service.getArgumentList().setProperty(SendMailService.IN_MESSAGE, "Teste <b>de envio</b> de Mensagem HTML.<br/> Nova linha.");
			service.getArgumentList().setProperty(SendMailService.IN_RECIPIENT_OPT, "xxxxxxxx@gmail.com");
			service.getArgumentList().setProperty(SendMailService.IN_SUBJECT, "Assunto teste");
			
			// Test file attachment
			File tempFile = File.createTempFile("SendMailServiceTestFile", ".tmp");
			PrintWriter printWriter = new PrintWriter(tempFile);
			printWriter.println("This file content is unusefull!");
			printWriter.close();
			List<String> fileList = new ArrayList<String>();
			fileList.add(tempFile.getAbsolutePath());
			service.getArgumentList().setProperty(SendMailService.IN_FILE_PATH_LIST_OPT, fileList);
			
			// Test MimeBodyPart attachment
			MimeBodyPart bodyPart = new MimeBodyPart();
			bodyPart.setContent("%PDF-1.4%5 0 obj", "application/pdf");
			List<MimeBodyPart> bodyList = new ArrayList<MimeBodyPart>();
			bodyList.add(bodyPart);
			service.getArgumentList().setProperty(SendMailService.IN_MIME_BODY_PART_LIST_OPT, bodyList);
			
            this.processManager.getServiceManager().execute(service);
            
		} catch (BusinessException e) {
	
	        UtilsTest.showMessageList(e.getErrorList());
	        
	        Assert.fail(e.getMessage());
		} catch (Exception e) {

			e.printStackTrace();
	        Assert.fail(e.getMessage());
		}
		
	}

    @Test
    public void testUseAsDefault(){
		try {
			@SuppressWarnings("unchecked")
			CreateProcess<EmailAccount> process = (CreateProcess<EmailAccount>) this.processManager.createProcessByName(CreateProcess.PROCESS_NAME, getAdminSession());
			process.setEntityType(EmailAccount.class);
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
			account.setProperties(account1.getProperties());
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
