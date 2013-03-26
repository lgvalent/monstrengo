package br.com.orionsoft.monstrengo.security.processes;

import org.junit.Assert;
import org.junit.Test;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.test.ProcessBasicTest;
import br.com.orionsoft.monstrengo.security.processes.AuthenticateProcess;

/**
 * Esta classe testa a autentica��o de um usuario do sistema. Um usuario (m) j� existente no banco � autenticado
 * e outro (xxxxxxx) n�o existe no banco n�o pode ser autenticado. 
 *
 */
public class AuthenticateProcessTestCase extends ProcessBasicTest
{
    
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(AuthenticateProcessTestCase.class);
    }

    @Test
    public void testRunAuthenticate()
    {
        try
        {
            System.out.println("Testando:" + this.getClass().getName());

            AuthenticateProcess auth = (AuthenticateProcess) processManager.createProcessByName(AuthenticateProcess.PROCESS_NAME, null);
                
            System.out.println(":Pid=" + auth.getPid());
            
            //usu�rio m j� existe no banco
            auth.setLogin("admin");
            auth.setPassword("admin");
            
            System.out.println("auth - " + auth.runAuthenticate());
            if (auth.runAuthenticate())
                System.out.println("OK");
            else
                throw new BusinessException(auth.getMessageList());
            
            /* Autenticar usu�rio sem verificar a senha */
            auth.setLogin("admin");
            auth.setPassword("admin__");
            auth.setCheckPassword(false);
            
            
            System.out.println("auth - " + auth.runAuthenticate());
            if (auth.runAuthenticate())
            	System.out.println("OK- N�o verificou a senha errada");
            else
            	throw new BusinessException(auth.getMessageList());
            
            auth = (AuthenticateProcess) processManager.createProcessByName(AuthenticateProcess.PROCESS_NAME, null);

            System.out.println(":Pid=" + auth.getPid());
            
            auth.setLogin("xxxxxx");
            auth.setPassword("xxxxxx");
            
            System.out.println("auth - " + auth.runAuthenticate());
            if (auth.runAuthenticate())
            {
                System.out.println("NOK");
                throw new BusinessException(MessageList.createSingleInternalError(new Exception("N�o poderia autenticar usu�rio com login xxxxxx")));
            }
            System.out.println("OK");
            for(BusinessMessage er: auth.getMessageList())
                System.out.println(er.getMessageClass().getSimpleName() + ":" + er.getErrorKey() + "=" + er.getMessage());

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
