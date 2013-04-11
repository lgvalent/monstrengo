package br.com.orionsoft.monstrengo.security.processes;

import org.junit.Assert;
import org.junit.Test;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.test.ProcessBasicTest;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;
import br.com.orionsoft.monstrengo.security.processes.CreateSecurityStructureProcess;
import br.com.orionsoft.monstrengo.security.services.UtilsSecurity;

/**
 * Esta classe testa a criação e manutenção de um operador e grupo pelo login, groupName e allowAll (true,false). 
 *
 */
public class CreateSecurityStructureProcessTestCase extends ProcessBasicTest
{
    
//    public static void main(String[] args)
//    {
//        junit.textui.TestRunner.run(CreateSecurityStructureProcessTestCase.class);
//    }

//    @Test
	public void testRunCreate()
    {
        try
        {
            System.out.println("Testando:" + this.getClass().getName());
            
            this.runCreateSecurityStructure();

            CreateSecurityStructureProcess auth = (CreateSecurityStructureProcess) processManager.createProcessByName(CreateSecurityStructureProcess.PROCESS_NAME, this.getAdminSession());
                
            System.out.println(":Pid=" + auth.getPid());
            
            //usuário m já existe no banco
            auth.setLogin("admin");
            auth.setGroupName("admin");
            auth.setAllowAll(true);
            
            if (auth.runCreate()){
                UtilsTest.showMessageList(auth.getMessageList());
            }else
                throw new BusinessException(auth.getMessageList());
            
            Assert.assertTrue(true);
        } 
        catch (BusinessException e)
        {
            UtilsTest.showMessageList(e.getErrorList());
            
            Assert.assertTrue(false);
        }
    }

    @Test
	public void testRunView()
    {
        try
        {
            System.out.println("Testando:" + this.getClass().getName());
            
            AuthenticateProcess auth = (AuthenticateProcess) processManager.createProcessByName(AuthenticateProcess.PROCESS_NAME, this.getAdminSession());
                
            System.out.println(":Pid=" + auth.getPid());
            
            //usuário m já existe no banco
            auth.setLogin("admin");
            auth.setPassword("admin");
            
            if (auth.runAuthenticate()){
                UtilsTest.showMessageList(auth.getMessageList());
            	System.out.println(UtilsSecurity.checkRightQuery(processManager.getServiceManager(), ApplicationUser.class, auth.getUserSession(), null));
            }else
                throw new BusinessException(auth.getMessageList());
            
            Assert.assertTrue(true);
        } 
        catch (BusinessException e)
        {
            UtilsTest.showMessageList(e.getErrorList());
            
            Assert.assertTrue(false);
        }
    }

}
