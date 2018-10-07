package br.com.orionsoft.basic.process;

import org.junit.Assert;
import org.junit.Test;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.test.ProcessBasicTest;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;


public class AlterarSenhaPessoaProcessTestCase extends ProcessBasicTest {
	
	
	@Test
	 public void testRun(){
        try{
            

            AlterarSenhaPessoaProcess alterar =  (AlterarSenhaPessoaProcess) processManager.createProcessByName(AlterarSenhaPessoaProcess.PROCESS_NAME,null);
                
            alterar.setNumeroDocumento("00736581979");
            alterar.setNovaSenha("teste");
            alterar.setNovaSenhaConfirmacao("teste");
            
            if (alterar.runAlterar()){
            	
                UtilsTest.showMessageList(alterar.getMessageList());
                Assert.assertTrue(true);
            }
            else
            {
                UtilsTest.showMessageList(alterar.getMessageList());
                Assert.assertTrue(false);
            }
            

        /*  
            alterar = (AlterarSenhaPessoaProcess) processManager.createProcessByName(AlterarSenhaPessoaProcess.PROCESS_NAME, this.getUserSession());
            
            
            //usuário m já existe no banco
            alterar.setNumeroDocumento("00736581979");
            alterar.setSenha("teste");
            alterar.setCodigoSeguranca("123");
            alterar.setSenhaNova("orion");
            
            if (alterar.runAlterar())
                UtilsTest.showMessageList(alterar.getMessageList());
            else
                throw new BusinessException(alterar.getMessageList());

         */
        } 
        catch (BusinessException e)
        {
            e.printStackTrace();
        	Assert.assertTrue(false);
        }
    }
	

}
