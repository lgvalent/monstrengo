package br.com.orionsoft.monstrengo.security.processes;

import org.junit.Assert;
import org.junit.Test;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.test.ProcessBasicTest;
import br.com.orionsoft.monstrengo.security.processes.AuthenticateProcess;

/**
 * Esta classe testa a autenticação de um usuario do sistema. Um usuario (m) já existente no banco é autenticado
 * e outro (xxxxxxx) não existe no banco não pode ser autenticado. 
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
            
            //usuário m já existe no banco
            auth.setLogin("admin");
            auth.setPassword("admin");
            
            System.out.println("auth - " + auth.runAuthenticate());
            if (auth.runAuthenticate())
                System.out.println("OK");
            else
                throw new BusinessException(auth.getMessageList());
            
            /* Autenticar usuário sem verificar a senha */
            auth.setLogin("admin");
            auth.setPassword("admin__");
            auth.setCheckPassword(false);
            
            
            System.out.println("auth - " + auth.runAuthenticate());
            if (auth.runAuthenticate())
            	System.out.println("OK- Não verificou a senha errada");
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
                throw new BusinessException(MessageList.createSingleInternalError(new Exception("Não poderia autenticar usuário com login xxxxxx")));
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
