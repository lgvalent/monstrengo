package br.com.orionsoft.monstrengo.crud.processes;

import org.junit.Assert;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.test.ProcessBasicTest;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.processes.UpdateProcess;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;

/**
 * Esta classe testa se o usuário tem direito de editar tal propriedade.  
 * 
 * @author estagio
 */
public class UpdateProcessTestCase extends ProcessBasicTest
{
    
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(UpdateProcessTestCase.class);
    }

    /**
     * Este método testa o caminho normal se todos os dados
     * estiverem corretos.
     */
    public void testRunEdit()
    {
        try
        {
        	System.out.println("Testando:" + this.getClass().getName());

            //preparando processo edit para user1
            System.out.println(":Preparando o processo edit (user1).");
            UpdateProcess editUsr1 = (UpdateProcess) processManager.createProcessByName(UpdateProcess.PROCESS_NAME, this.getAdminSession());
            System.out.println(":Pid=" + editUsr1.getPid());
            editUsr1.setEntityType(this.getAdminSession().getUser().getInfo().getType());
            editUsr1.setEntityId(this.getAdminSession().getUser().getId());

            
            System.out.println("userID - " + this.getAdminSession().getUser().getId());
            System.out.println("userType - " + this.getAdminSession().getUser().getInfo().getType());
                        
            
            //mayEdit() para user1 deve ser true, assim, retrieveEntity() pode ser utilizado
            System.out.println("Assert.assertTrue user1 - deve dar true (mayEdit) - " + editUsr1.mayEdit());
            
            Assert.assertTrue(editUsr1.mayEdit());
            
            IEntity entity = editUsr1.retrieveEntity();
            
            System.out.println(":Mostra antes da edição");
            UtilsTest.showEntityProperties(entity);

            System.out.println(":Alterando alguma coisa com user1");
            entity.setPropertyValue(ApplicationUser.LOGIN, "newLogin");
            
            System.out.println("OK para user1");

            //preparando processo edit para user2
            System.out.println(":Preparando o processo edit (user2).");
            UpdateProcess editUsr2 = (UpdateProcess) processManager.createProcessByName(UpdateProcess.PROCESS_NAME, this.getUserSession());
            System.out.println(":Pid=" + editUsr2.getPid());
            editUsr2.setEntityType(this.getUserSession().getUser().getInfo().getType());
            editUsr2.setEntityId(this.getUserSession().getUser().getId());

            //testa mayEdit() para user2, que deve ser false, pois não possui permissão alguma
            System.out.println("Assert.assertFalse user2 - deve dar false (mayEdit) - " + editUsr2.mayEdit());
            Assert.assertFalse(editUsr2.mayEdit());
            
            System.out.println("OK para user2");

            if (editUsr1.runUpdate()){
            	// Pega os valores anteriores à edição e grava-os novamente
                System.out.println(entity.getProperty(ApplicationUser.LOGIN).getValue().getOldValue());
            	entity.setPropertyValue(ApplicationUser.LOGIN, (entity.getProperty(ApplicationUser.LOGIN).getValue().getOldValue()));
            	editUsr1.runUpdate();
            	System.out.println("OK");
            }else
                throw new BusinessException(editUsr1.getMessageList());
            
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
