package br.com.orionsoft.monstrengo.security.processes;

import org.junit.Assert;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.test.ProcessBasicTest;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;
import br.com.orionsoft.monstrengo.security.processes.OverwritePasswordProcess;

public class OverwritePasswordProcessTestCase extends ProcessBasicTest
{
    
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(OverwritePasswordProcessTestCase.class);
    }

    public void testRun()
    {
        try
        {
            this.runCreateSecurityStructure();
        	
        	System.out.println("Testando:" + this.getClass().getName());

            OverwritePasswordProcess auth = (OverwritePasswordProcess) processManager.createProcessByName(OverwritePasswordProcess.PROCESS_NAME, this.getAdminSession());
                
            System.out.println(":Pid=" + auth.getPid());
            
            //usuário m já existe no banco
            auth.setLogin("user");
            auth.setNewPassword("resu");
            
            if (auth.runOverwrite())
                UtilsTest.showMessageList(auth.getMessageList());
            else
                throw new BusinessException(auth.getMessageList());
            
            auth.finish();

            auth = (OverwritePasswordProcess) processManager.createProcessByName(OverwritePasswordProcess.PROCESS_NAME, this.getAdminSession());
            
            System.out.println(":Pid=" + auth.getPid());
            
            //usuário m já existe no banco
            auth.setLogin("user");
            auth.setNewPassword("user");
            
            if (auth.runOverwrite())
                UtilsTest.showMessageList(auth.getMessageList());
            else
                throw new BusinessException(auth.getMessageList());

            Assert.assertTrue(true);
        } 
        catch (BusinessException e)
        {
            for(BusinessMessage er: e.getErrorList())
                System.out.println(er.getMessageClass().getSimpleName() + ":" + er.getErrorKey() + "=" + er.getMessage());
            
            Assert.assertTrue(false);
        }
    }

}
