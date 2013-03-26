package br.com.orionsoft.monstrengo.view.jsf.bean;

import java.io.Serializable;

import javax.faces.bean.ManagedProperty;

import org.apache.log4j.Logger;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.view.jsf.security.UserSessionBean;
import br.com.orionsoft.monstrengo.view.jsf.util.FacesUtils;

/**
 * Esta classe implementa as propriedades básica de qualquer Bean integrado no 
 * sistema.
 * @author Lucio
 */
public abstract class BasicBean implements IBasicBean, Serializable
{
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value="#{userSessionBean}")
    private UserSessionBean userSessionBean;
    
	/** 
	 * Obtem o bean que controla a atual sessão Web do operador.
	 *  
     * @jsf.managed-property value="#{userSessionBean}"
     */
	public UserSessionBean getUserSessionBean() {
		return userSessionBean;
	}
	public void setUserSessionBean(UserSessionBean userSessionBean) {
		this.userSessionBean = userSessionBean;
        /* Lucio - 04/06/2007 
         * Registra o atual bean no UserSessionBean que controla todas as visões do atual operador.
         * O registro é realizado aqui, porque no facesConfig.xml não é possível
         * definir um ini-method como é possível no applicationContext do Spring. */
        try {
			this.userSessionBean.registerView(this);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
	}
	
    /**
     * @deprecated Este método foi criado para evitar que o novo método getVieName() 
     * da interface IBasicBean tenha que ser implementado por todos os atuais Beans
     * que foram criados seguindo a antiga interface. Assim, este método deve
     * ser removido e todos os beans passados para a nova interface. Para isto, as
     * página JSP deverão ser atualizadas também. O ApplicationBean 
	 */
	public static final String VIEW_NAME_NOT_IMPLEMENTED = "viewNameNotImplemented"; 
	
	protected final Logger log = Logger.getLogger(this.getClass());

    /**
     * Referência à instancia do backing bean ServiceManager mantido em escopo de aplicação pelo JSF e definido por IoF
     */
	@ManagedProperty(value="#{applicationBean}")
    private ApplicationBean applicationBean;
    
    /**
     *  @jsf.managed-property value="#{applicationBean}"
     */
    public ApplicationBean getApplicationBean(){ return applicationBean;}
    public void setApplicationBean(ApplicationBean applicationBean)
    { 
        this.applicationBean = applicationBean;
    }
    
    /**
     * @deprecated Este método foi criado para evitar que o novo método actionStart() 
     * da interface IBasicBean tenha que ser implementado por todos os atuais Beans
     * que foram criados seguindo a antiga interface. Assim, este método deve
     * ser removido e todos os beans passados para a nova interface. Para isto, as
     * página JSP deverão ser atualizadas também. 
     */
    public String actionStart(){
    	FacesUtils.addInfoMsg("O método actionStart() pelo objeto da classe:" + this.getClass().getName());
    	return "";
    }

    /**
     * @deprecated Este método foi criado para evitar que o novo método getVieName() 
     * da interface IBasicBean tenha que ser implementado por todos os atuais Beans
     * que foram criados seguindo a antiga interface. Assim, este método deve
     * ser removido e todos os beans passados para a nova interface. Para isto, as
     * página JSP deverão ser atualizadas também. O ApplicationBean 
     */
    public String getViewName(){
    	return VIEW_NAME_NOT_IMPLEMENTED;
    }
    
}
