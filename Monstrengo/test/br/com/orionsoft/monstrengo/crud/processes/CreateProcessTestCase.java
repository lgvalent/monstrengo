package br.com.orionsoft.monstrengo.crud.processes;

import org.junit.Assert;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.test.ProcessBasicTest;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.processes.CreateProcess;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;

/**
 * Esta classe testa se o usuário tem direito de criar tal entidade.  
 * 
 * @author estagio
 * 
 */
public class CreateProcessTestCase extends ProcessBasicTest
{
    
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(CreateProcessTestCase.class);
    }

    /**
     * Este método testa o caminho normal se todos os dados
     * estiverem corretos.
     */
    public void testRunCreate()
    {
        try
        {
        	System.out.println("Testando:" + this.getClass().getName());

            //preparando processo create para user1
            System.out.println(":Preparando o processo create (user1).");
            CreateProcess createUsr1 = (CreateProcess) processManager.createProcessByName(CreateProcess.PROCESS_NAME, this.getAdminSession());
            System.out.println(":Pid=" + createUsr1.getPid());
            createUsr1.setEntityType(this.getAdminSession().getUser().getInfo().getType());
            
            System.out.println("userID - " + this.getAdminSession().getUser().getId());
            System.out.println("userType - " + this.getAdminSession().getUser().getInfo().getType());
                        
            
            //mayCreate() para user1 deve ser true, assim, retrieveEntity() pode ser utilizado
            System.out.println("Assert.assertTrue user1 - deve dar true (mayCreate) - " + createUsr1.mayCreate());
            
            Assert.assertTrue(createUsr1.mayCreate());
            
            IEntity entity = createUsr1.retrieveEntity();
            
            System.out.println(":Mostra antes da edição");
            UtilsTest.showEntityProperties(entity);

            System.out.println(":Alterando alguma coisa com user1");
            entity.setPropertyValue(ApplicationUser.LOGIN, "newLogin");
            
            System.out.println("OK para user1");

            if (createUsr1.runUpdate()){
            	// Pega os valores anteriores à edição e grava-os novamente
                System.out.println(entity.getProperty(ApplicationUser.LOGIN).getValue().getOldValue());
            	entity.setPropertyValue(ApplicationUser.LOGIN, (entity.getProperty(ApplicationUser.LOGIN).getValue().getOldValue()));
            	System.out.println("OK");
            }else
                throw new BusinessException(createUsr1.getMessageList());
            
            Assert.assertTrue(true);
        } 
        catch (BusinessException e)
        {
            UtilsTest.showMessageList(e.getErrorList());

            Assert.assertTrue(false);
        }
//      não é possível deletar usuário, pois está vinculado ao registro (deletar direto do banco) 
//      finally{
//      BasicStructureRigth.destroyRigths();
//      }
    }
}
