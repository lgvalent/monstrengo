/*
 * Created on 05/04/2005
 */
package br.com.orionsoft.monstrengo.view.jsf.bean;


import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import br.com.orionsoft.monstrengo.core.process.IProcessManager;
import br.com.orionsoft.monstrengo.core.process.ProcessManager;
import br.com.orionsoft.monstrengo.view.jsf.util.FacesUtils;

/**
 * ApplicationBean deve ser definido com escopo de 'application' no arquivo faces-managed-beans.xml.
 * Desta forma, haver� durante toda a aplica��o somente uma aplica��o rodando e conectada no banco de dados,
 * com os DAOs criados e os Servi�os instanciados. Todas as sess�es dos usu�rios usar�o o mesmo BackingBean.
 * 
 * @author Orion Soft
 * 
 * @jsf.bean name="applicationBean" scope="application"
 */
@ManagedBean(name="applicationBean")
@SessionScoped
public class ApplicationBean implements Serializable
{
	private static final long serialVersionUID = 1L;

	private IProcessManager processManager;
    private ApplicationContext appContext;
    
    private final Logger log = Logger.getLogger(this.getClass());
    
    public ApplicationBean()
    {
        log.debug("Iniciando a aplica��o WEB...");
    	ServletContext context = FacesUtils.getServletContext();
        this.appContext = WebApplicationContextUtils.getRequiredWebApplicationContext(context);
        processManager = (IProcessManager) appContext.getBean(ProcessManager.MANAGER_NAME);
        /* Registrando o ApplicationBean na sess�o HTTP */
        context.setAttribute("applicationBean", this);
        log.debug("Aplica��o WEB CARREGADA COM SUCESSO.");
    }

    public IProcessManager getProcessManager(){return processManager;}
    public void setProcessManager(IProcessManager processManager){this.processManager = processManager;}

    public ApplicationContext getAppContext(){return appContext;}

}
