package br.com.orionsoft.monstrengo.mail.services;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.AuthenticationFailedException;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang.StringUtils;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.mail.entities.EmailAccount;

/**
 * Serviço que envia e-mail para usuários do sistema
 * <p>
 * <b>Argumentos:</b><br>
 * IN_EMAIL_ACCOUNT_OPT: Possui as configurações da conta que enviará os e-mails. Se omitido, será usada a conta marcada como default<br> 
 * IN_RECIPIENT_LIST_OPT: Lista com os e-mails de destino.<br>
 * IN_RECIPIENT_OPT: Um e-mail de destino.<br>
 * IN_SUBJECT: Título da mensagem.<br>
 * IN_MESSAGE: Mensagem a ser enviada.<br>
 * 
 * <p>
 * <b>Procedimentos:</b><br>
 * 1) Obtém as configurações da conta de e-mail;<br>
 * 2) Prepara as configurações de e-mail;
 * 3) Envia as mensagens para os destinatários indicados na lista de e-mails.
 * 
 * 
 * @author andre
 * 
 * @spring.bean id="SendMailService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"

 */

public class SendMailService extends ServiceBasic {

	public static final String SERVICE_NAME = "SendMailService";

	public static final String IN_EMAIL_ACCOUNT_OPT = "emailAccount";
	public static final String IN_RECIPIENT_LIST_OPT = "recipientList";
	public static final String IN_RECIPIENT_OPT = "recipient";
	public static final String IN_SUBJECT = "subject";
	public static final String IN_MESSAGE = "message";
	public static final String IN_MIME_BODY_PART_LIST_OPT = "mimeBodyPartList";
	public static final String IN_FILE_PATH_LIST_OPT = "filePathList";

	public String getServiceName() {
		return SERVICE_NAME;
	}

	@SuppressWarnings("unchecked")
	public void execute(ServiceData serviceData) throws ServiceException {
		try {
			/* Obtém os parâmetros */
			List<String> inRecipientList = serviceData.getArgumentList().containsProperty(IN_RECIPIENT_LIST_OPT)?(List<String>)serviceData.getArgumentList().getProperty(IN_RECIPIENT_LIST_OPT):null;
			String inRecipient = serviceData.getArgumentList().containsProperty(IN_RECIPIENT_OPT)?(String)serviceData.getArgumentList().getProperty(IN_RECIPIENT_OPT):null;
			EmailAccount inEmailAccount = serviceData.getArgumentList().containsProperty(IN_EMAIL_ACCOUNT_OPT)?(EmailAccount)serviceData.getArgumentList().getProperty(IN_EMAIL_ACCOUNT_OPT):null;
			String inSubject = (String)serviceData.getArgumentList().getProperty(IN_SUBJECT);
			String inMessage = (String)serviceData.getArgumentList().getProperty(IN_MESSAGE);
			List<MimeBodyPart> inMimeBodyPartList = serviceData.getArgumentList().containsProperty(IN_MIME_BODY_PART_LIST_OPT)?(List<MimeBodyPart>)serviceData.getArgumentList().getProperty(IN_MIME_BODY_PART_LIST_OPT):null;
			List<String> inFilePathList = serviceData.getArgumentList().containsProperty(IN_FILE_PATH_LIST_OPT)?(List<String>)serviceData.getArgumentList().getProperty(IN_FILE_PATH_LIST_OPT):null;
			
			/* Verifica se foi fornecida uma conta, senão, seleciona a marcada como padrão */
			if(inEmailAccount == null){
				IEntityList<EmailAccount> emailAccounts = UtilsCrud.list(this.getServiceManager(), EmailAccount.class, EmailAccount.USE_AS_DEFAULT + "=true", serviceData);
				
				if(emailAccounts.isEmpty()){
					throw new ServiceException(MessageList.createSingleInternalError(new Exception("Nenhuma conta de email foi fornecida e não foi encontrada nenhuma cadastrada e marcada como default.")));
				}
				
				inEmailAccount = emailAccounts.get(0).getObject();
			}
			// Pegando uma porta personalizada ou a padrão
			// Permite a definição hostname.com:2525
			String[] hostAndPort = inEmailAccount.getHost().split(":");
			
			
			//configurando o envio de e-mails
			Properties properties = new Properties();
			properties.put("mail.smtp.host", hostAndPort[0]);
			properties.put("mail.smtp.port", hostAndPort.length>1?hostAndPort[1]:"25");
			properties.put("mail.from", inEmailAccount.getSenderMail());
			
			//obtendo as configurações adicionais da conta
			if(StringUtils.isNotEmpty(inEmailAccount.getProperties())){
				for(String prop: inEmailAccount.getProperties().split(";")){
					properties.put(prop.split("=")[0],prop.split("=")[1]);
				}
			}
			//se estiver utilizando autenticação
			Authenticator authenticator = null;
			if (StringUtils.isNotBlank(inEmailAccount.getUser()) && StringUtils.isNotBlank(inEmailAccount.getPassword())){
				authenticator = new MyAuthenticator(inEmailAccount.getUser(),inEmailAccount.getPassword());
 			}
			
			Session session = Session.getInstance(properties, authenticator);
			if(log.isDebugEnabled())
				session.setDebug(true);
			
			//Semp usa a lista de e-mail para enviar os emails,  mas verifica se ela existe para colocar o unitário dentro dela
			if(inRecipientList ==null)
				inRecipientList = new ArrayList<String>();
			
			if(inRecipientList != null)
				inRecipientList.add(inRecipient);

			for (String recipientMail : inRecipientList) {
				MimeMessage message = new MimeMessage(session);

				// setando o endereço de e-mail e o nome para assinatura
				message.setFrom(new InternetAddress(inEmailAccount
						.getSenderMail(), inEmailAccount.getSenderName()));
				// sentando o e-mail de destino
				message.setRecipient(Message.RecipientType.TO,
						new InternetAddress(recipientMail));
				// data
				message.setSentDate(new Date());
				// subject
				message.setSubject(inSubject);
				
				// message multipart content
				Multipart content = new MimeMultipart();

				// TextMessage
				BodyPart textMessage = new MimeBodyPart();
				textMessage.setContent(inMessage, "text/html; charset=utf-8");
				content.addBodyPart(textMessage);
				
				// FileAttachament
				if (inFilePathList != null)
					for (String path : inFilePathList) {
						DataSource fds = new FileDataSource(path);
						BodyPart fileBodyPart = new MimeBodyPart();
						fileBodyPart.setDataHandler(new DataHandler(fds));
						fileBodyPart.setFileName(path.substring(path
								.lastIndexOf("/") + 1));
						content.addBodyPart(fileBodyPart);
					}

				if (inMimeBodyPartList != null)
					for (MimeBodyPart mimeBodyPart : inMimeBodyPartList)
						content.addBodyPart(mimeBodyPart);

				// enviando mensagem
				message.setContent(content);
				Transport.send(message);
				// session.getTransport().sendMessage(message, message.getAllRecipients());
			}

			/* Inclui a mensagem de OK */
			//TODO - colocar mensagens
		}catch (AuthenticationFailedException e) {
			throw new ServiceException(MessageList.createSingleInternalError(new Exception("Falha na autenticação. Verifique se o nome do usuário e senha da conta de email estão corretos.")));
		} catch (BusinessException e) {
			/* O Serviço não precisa adicionar mensagem local. O Manager já
			 indica qual srv falhou e os parâmetros.*/
			throw new ServiceException(e.getErrorList());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new ServiceException(MessageList.createSingleInternalError(e));
		} 
	}
	
	/**
	 * Implementa um Authenticator simples
	 * 
	 * fonte: http://www.javafree.org/javabb/viewtopic.jbb?t=3680&page=2
	 * 
	 * @author lucio
	 *
	 */
	public class MyAuthenticator extends Authenticator{
		public String username = null;
		public String password = null;


		public MyAuthenticator (String username, String password) {
		this.username = username;
		this.password = password;
		}

		protected PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(username,password);
		} 
	}
	
	public static boolean validateEMail(String email){
		try{
			InternetAddress emailAddr = new InternetAddress(email);
			emailAddr.validate();
			return true;
		} catch (MessagingException ex) {
			return false;
		}
	}
	
}
