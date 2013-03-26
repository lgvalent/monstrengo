package br.com.orionsoft.monstrengo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.InitializationError;

import br.com.orionsoft.monstrengo.core.process.IProcessManager;
import br.com.orionsoft.monstrengo.core.process.ProcessManager;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.security.services.CreateSecurityStructureService;


/**
 * Esta classe cria as diversas entidades do m�dulo para que os testes do m�dulo
 * possam ser executados sobre dados v�lidos.
 * 
 * @author lucio 20070910
 *
 */
public class PopularTabelas extends ServiceBasicTest {

	/**
	 * Permite que esta classe seja executada diretamente por linha de comando.
	 * 
	 * @param args
	 * @throws InitializationError
	 */
	public static void main(String[] args) throws InitializationError
	{
//		new TestClassRunner(PopularTabelas.class).run(new RunNotifier());
	}
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}
	
	@Test
	public void popular() throws ServiceException {
        /* Um ProcessManager � utilizado para obter todos os processos dispon�veis no sistema atual */
		IProcessManager processManager = (ProcessManager)ctx.getBean("ProcessManager");
		
        // Cria a estrutura b�sica da aplica��o para o admin
        ServiceData sd = new ServiceData(CreateSecurityStructureService.SERVICE_NAME, null);
        sd.getArgumentList().setProperty(CreateSecurityStructureService.IN_USER_LOGIN, "admin");
        sd.getArgumentList().setProperty(CreateSecurityStructureService.IN_GROUP_NAME, "admin");
        sd.getArgumentList().setProperty(CreateSecurityStructureService.IN_ALLOW_ALL_BOOL, true);
		sd.getArgumentList().setProperty(CreateSecurityStructureService.IN_PROCESS_MANAGER, processManager);
        this.serviceManager.execute(sd);
        
        if(!sd.getMessageList().isTransactionSuccess()){
//        	Assert.fail(sd.getMessageList().get(0).getMessage());
        	throw new ServiceException(sd.getMessageList());
        }

        // Cria a estrutura b�sica da aplica��o para o user padr�o
        sd = new ServiceData(CreateSecurityStructureService.SERVICE_NAME, null);
        sd.getArgumentList().setProperty(CreateSecurityStructureService.IN_USER_LOGIN, "user");
        sd.getArgumentList().setProperty(CreateSecurityStructureService.IN_GROUP_NAME, "user");
        sd.getArgumentList().setProperty(CreateSecurityStructureService.IN_ALLOW_ALL_BOOL, false);
		sd.getArgumentList().setProperty(CreateSecurityStructureService.IN_PROCESS_MANAGER, processManager);
        this.serviceManager.execute(sd);

        if(!sd.getMessageList().isTransactionSuccess()){
//        	Assert.fail(sd.getMessageList().get(0).getMessage());
        	throw new ServiceException(sd.getMessageList());
        }
	}
}
