package br.com.orionsoft.monstrengo.mail.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;

@Entity
@Table(name="framework_email_account")
public class EmailAccount {

	//propriedade para conexão com o servidor de e-mails - os campos 'user' e 'password' podem ser ignorados caso não se utilize autenticação para envio dos e-mails
	public static final String USE_AS_DEFAULT = "useAsDefault";
	public static final String HOST = "host";
	public static final String USER = "user";
	public static final String PASSWORD = "password";
	
	//remetente
	public static final String SENDER_NAME = "senderName";
	public static final String SENDER_MAIL = "senderMail";
	
	private long id = IDAO.ENTITY_UNSAVED;
	private boolean useAsDefault = false;
	private String host;
	private String user;
	private String password;
	private String senderName;
	private String senderMail;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public long getId() {return id;}
	public void setId(long id) {this.id = id;}
	
	@Column
	public boolean isUseAsDefault() {
		return useAsDefault;
	}
	public void setUseAsDefault(boolean useAsdefault) {
		this.useAsDefault = useAsdefault;
	}
	
	@Column(length=50)
	public String getHost() {
		return host;
	}
	
	public void setHost(String host) {
		this.host = host;
	}
	
	@Column(length=50)
	public String getUser() {
		return user;
	}
	
	public void setUser(String user) {
		this.user = user;
	}
	
	@Column(length=50)
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	@Column(length=100)
	public String getSenderName() {
		return senderName;
	}
	
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	
	@Column(length=100)
	public String getSenderMail() {
		return senderMail;
	}
	
	public void setSenderMail(String senderMail) {
		this.senderMail = senderMail;
	}
	
	public String toString(){
		return this.senderMail + "(" + this.host + ")";
	}
	
}
