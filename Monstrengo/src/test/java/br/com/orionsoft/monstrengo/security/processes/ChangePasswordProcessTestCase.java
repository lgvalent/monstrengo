package br.com.orionsoft.monstrengo.security.processes;

import org.junit.Assert;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.test.ProcessBasicTest;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;
import br.com.orionsoft.monstrengo.security.processes.ChangePasswordProcess;

public class ChangePasswordProcessTestCase extends ProcessBasicTest
{
    
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(ChangePasswordProcessTestCase.class);
    }

    public void testRun()
    {
        try
        {
            
        	System.out.println("Testando:" + this.getClass().getName());

            ChangePasswordProcess auth = (ChangePasswordProcess) processManager.createProcessByName(ChangePasswordProcess.PROCESS_NAME, this.getUserSession());
                
            System.out.println(":Pid=" + auth.getPid());
            
            //usuário m já existe no banco
            auth.setCurrentPassword("user");
            auth.setNewPassword("resu");
            
            if (auth.runChange())
                UtilsTest.showMessageList(auth.getMessageList());
            else
                throw new BusinessException(auth.getMessageList());
            
            auth.finish();

            auth = (ChangePasswordProcess) processManager.createProcessByName(ChangePasswordProcess.PROCESS_NAME, this.getUserSession());
            
            System.out.println(":Pid=" + auth.getPid());
            
            //usuário m já existe no banco
            auth.setCurrentPassword("resu");
            auth.setNewPassword("user");
            
            if (auth.runChange())
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
