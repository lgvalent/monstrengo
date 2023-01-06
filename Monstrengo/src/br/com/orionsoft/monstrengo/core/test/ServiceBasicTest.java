package br.com.orionsoft.monstrengo.core.test;

import org.junit.After;
import org.junit.Before;

import br.com.orionsoft.monstrengo.core.test.ApplicationBasicTest;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.core.service.ServiceManager;
import br.com.orionsoft.monstrengo.security.services.ManageSecurityStructureService;

public class ServiceBasicTest extends ApplicationBasicTest {

	protected ServiceManager serviceManager;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		serviceManager = (ServiceManager) ctx.getBean("ServiceManager");

	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
		serviceManager = null;
	}

	public void runManageSecurityStructure() throws ServiceException {
		// Cria a estrutura básica da aplicação
		ServiceData sd = new ServiceData(ManageSecurityStructureService.SERVICE_NAME, null);
		this.serviceManager.execute(sd);
	}
}
