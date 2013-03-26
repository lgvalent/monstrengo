package br.com.orionsoft.monstrengo.crud.processes;

import org.junit.Assert;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.test.ProcessBasicTest;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.processes.DeleteProcess;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;

/**
 * Esta classe testa se o usuário tem direito de editar tal propriedade.  
 * 
 * @author estagio
 */
public class DeleteProcessTestCase extends ProcessBasicTest
{
    
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(DeleteProcessTestCase.class);
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
            System.out.println(":Preparando o processo delete (user1).");
            DeleteProcess delUsr2 = (DeleteProcess) processManager.createProcessByName(DeleteProcess.PROCESS_NAME, this.getAdminSession());
            System.out.println(":Pid=" + delUsr2.getPid());
            delUsr2.setEntityType(this.getUserSession().getUser().getInfo().getType());
            delUsr2.setEntityId(this.getUserSession().getUser().getId());

            System.out.println("userID - " + this.getUserSession().getUser().getId());
            System.out.println("userType - " + this.getUserSession().getUser().getInfo().getType());
                        
            
            //mayDelete() para user1 deve ser true, assim, retrieveEntity() pode ser utilizado
            System.out.println("assertTrue user1 - deve dar true (mayDelete) - " + delUsr2.mayDelete());
            
            Assert.assertTrue(delUsr2.mayDelete());
            
            IEntity entity = delUsr2.retrieveEntity();
            
            System.out.println(":Mostra ANTES de apagar a entidade");
            UtilsTest.showEntityProperties(entity);
            
            //deleta entidade
            
            //deve dar erro se não houver uma justificativa para a exclusão ou se a String tiver tamanho menor que 5
            //delUsr1.setJustification("123");
            delUsr2.setJustification("Deletando para teste");
            
            //delUsr1.runDelete();
            
            //System.out.println(":Mostra DEPOIS de apagar a entidade");
            //UtilsTest.showEntityProperties(entity);
            
            if (delUsr2.runDelete()){
                // Pega os valores anteriores à edição e grava-os novamente
                System.out.println(entity.getProperty(ApplicationUser.LOGIN).getValue().getOldValue());
                entity.setPropertyValue(ApplicationUser.LOGIN, (entity.getProperty(ApplicationUser.LOGIN).getValue().getOldValue()));
                //delUsr1.runUpdate();
                System.out.println("OK");
            }else
                throw new BusinessException(delUsr2.getMessageList());
            
            Assert.assertTrue(true);
            
            //System.out.println(":Alterando alguma coisa com user1");
            //entity.setPropertyValue(ApplicationUser.LOGIN, "newLogin");
            
            //System.out.println("OK para user1");

            //preparando processo edit para user2
//            System.out.println(":Preparando o processo edit (user2).");
//            UpdateProcess editUsr2 = (UpdateProcess) processManager.createProcessByName(UpdateProcess.PROCESS_NAME, this.getUserSession());
//            System.out.println(":Pid=" + editUsr2.getPid());
//            editUsr2.setEntityType(this.getUserSession().getUser().getInfo().getType());
//            editUsr2.setEntityId(this.getUserSession().getUser().getId());
//
//            //testa mayEdit() para user2, que deve ser false, pois não possui permissão alguma
//            System.out.println("assertFalse user2 - deve dar false (mayEdit) - " + editUsr2.mayEdit());
//            assertFalse(editUsr2.mayEdit());
//            
//            System.out.println("OK para user2");

//            if (delUsr1.runUpdate()){
//            	// Pega os valores anteriores à edição e grava-os novamente
//                System.out.println(entity.getProperty(ApplicationUser.LOGIN).getValue().getOldValue());
//            	entity.setPropertyValue(ApplicationUser.LOGIN, (entity.getProperty(ApplicationUser.LOGIN).getValue().getOldValue()));
//            	delUsr1.runUpdate();
//            	System.out.println("OK");
//            }else
//                throw new BusinessException(delUsr1.getErrorList());
//            
//            assertTrue(true);
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
