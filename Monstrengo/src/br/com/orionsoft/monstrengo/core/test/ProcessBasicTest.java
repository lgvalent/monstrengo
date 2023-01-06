package br.com.orionsoft.monstrengo.core.test;

import br.com.orionsoft.monstrengo.core.test.ApplicationBasicTest;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.process.ProcessManager;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.security.entities.UserSession;
import br.com.orionsoft.monstrengo.security.processes.AuthenticateProcess;
import br.com.orionsoft.monstrengo.security.services.CreateSecurityStructureService;
import br.com.orionsoft.monstrengo.security.services.ManageSecurityStructureService;


/**
 * 
 * @author orion
 * @version 20060417
 *
 */
public class ProcessBasicTest extends ApplicationBasicTest
{

    protected ProcessManager processManager;
    
    /**
     * Mantem uma sessáo autenticada do operador admin e user.<br>
     * admin: com direito a tudo;
     * user: com direitos restritos;
     * É necessário que hajam estes operadores cadastrados no
     * banco.
     * @since 20060417
     */
    private UserSession adminSession;
    private UserSession userSession;
    
    private static final String USER_1 = "admin";
    private static final String USER_2 = "user";

//    public static void main(String[] args)
//    {
//        junit.textui.TestRunner.run(ProcessBasicTest.class);
//    }

    public void setUp() throws Exception, BusinessException
    {
        super.setUp();
        processManager = (ProcessManager)ctx.getBean("ProcessManager");
        
        /* Prepara a estrutura básica de operador */
		//autenticando usuario (admin com senha admin)
		System.out.println("Autenticando usuario admin");
		AuthenticateProcess authProc = (AuthenticateProcess) processManager.createProcessByName(AuthenticateProcess.PROCESS_NAME, null);
		authProc.setLogin("admin");
		authProc.setPassword("admin");
		if (authProc.runAuthenticate()){
			adminSession = authProc.getUserSession();	
		}else{
			throw new BusinessException(authProc.getMessageList());
		}

		//autenticando usuario (admin com senha admin)
		System.out.println("Autenticando usuario user");
		authProc = (AuthenticateProcess) processManager.createProcessByName(AuthenticateProcess.PROCESS_NAME, null);
		authProc.setLogin("user");
		authProc.setPassword("user");
		if (authProc.runAuthenticate()){
			userSession = authProc.getUserSession();	
		}else{
			throw new BusinessException(authProc.getMessageList());
		}
    }

    public void tearDown() throws Exception
    {
        super.tearDown();
        processManager=null;
    }

	public UserSession getAdminSession(){return adminSession;}
    
	public UserSession getUserSession(){return userSession;}
    
    public void runManageSecurityStructure() throws ServiceException{
        // Cria a estrutura básica da aplicação
        ServiceData sd = new ServiceData(ManageSecurityStructureService.SERVICE_NAME, null);
        this.processManager.getServiceManager().execute(sd);
    }

    /**
     * Este método cria/mantem duas estruturas de direitos:<br>
     * 1-Administrador (admin): Com todos os direitos.<br>
     * 2-Usuário padrão (user): Com nenhum direito para acessos.<br>
     * Estas estruturas poderão utilizadas na execução dos testes.<br>
     * Utilize o método this.getAdminSession() ou this.getUserSession();
     */
    public void runCreateSecurityStructure() throws ServiceException{
        // Cria a estrutura básica da aplicação para o admin
        ServiceData sd = new ServiceData(CreateSecurityStructureService.SERVICE_NAME, null);
        sd.getArgumentList().setProperty(CreateSecurityStructureService.IN_USER_LOGIN, USER_1);
        sd.getArgumentList().setProperty(CreateSecurityStructureService.IN_GROUP_NAME, USER_1);
        sd.getArgumentList().setProperty(CreateSecurityStructureService.IN_ALLOW_ALL_BOOL, true);
		sd.getArgumentList().setProperty(CreateSecurityStructureService.IN_PROCESS_MANAGER, this.processManager);
        this.processManager.getServiceManager().execute(sd);

        // Cria a estrutura básica da aplicação para o user padrão
        sd = new ServiceData(CreateSecurityStructureService.SERVICE_NAME, null);
        sd.getArgumentList().setProperty(CreateSecurityStructureService.IN_USER_LOGIN, USER_2);
        sd.getArgumentList().setProperty(CreateSecurityStructureService.IN_GROUP_NAME, USER_2);
        sd.getArgumentList().setProperty(CreateSecurityStructureService.IN_ALLOW_ALL_BOOL, false);
		sd.getArgumentList().setProperty(CreateSecurityStructureService.IN_PROCESS_MANAGER, this.processManager);
        this.processManager.getServiceManager().execute(sd);
    }

}
