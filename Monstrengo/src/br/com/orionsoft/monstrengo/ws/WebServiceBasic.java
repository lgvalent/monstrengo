package br.com.orionsoft.monstrengo.ws;


import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import br.com.orionsoft.monstrengo.core.service.ServiceManager;

public class WebServiceBasic {

	private ServiceManager serviceManager;
	
	public WebServiceBasic() {
		ApplicationContext ctx = new FileSystemXmlApplicationContext("applicationContext.xml");
		serviceManager = (ServiceManager) ctx.getBean("ServiceManager");
	}

	public ServiceManager getServiceManager() {
		return serviceManager;
	}
}
