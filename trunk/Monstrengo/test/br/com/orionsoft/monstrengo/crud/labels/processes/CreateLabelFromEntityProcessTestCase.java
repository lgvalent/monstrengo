package br.com.orionsoft.monstrengo.crud.labels.processes;

import org.junit.Assert;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.test.ProcessBasicTest;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.labels.entities.ModelLabelEntity;
import br.com.orionsoft.monstrengo.crud.labels.processes.CreateLabelFromEntityProcess;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.security.entities.ApplicationEntity;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;
import br.com.orionsoft.monstrengo.security.services.UtilsSecurity;

public class CreateLabelFromEntityProcessTestCase extends ProcessBasicTest
{
    CreateLabelFromEntityProcess process;

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(CreateLabelFromEntityProcessTestCase.class);
    }
    
    public void setUp() throws BusinessException, Exception{
    	super.setUp();
    	
    	process = (CreateLabelFromEntityProcess) this.processManager.createProcessByName(CreateLabelFromEntityProcess.PROCESS_NAME, this.getAdminSession());
    }

    public void tearDown() throws Exception{
    	process.finish();
    	process = null;

    	super.tearDown();
    }

    public void testExecute()
    {
        try{
        	
			IEntity appEntity = UtilsSecurity.retrieveEntity(this.processManager.getServiceManager(), ApplicationUser.class, null);
			IEntity entity = UtilsCrud.retrieve(this.processManager.getServiceManager(), ApplicationUser.class, 1, null);

        	/* Cria um modelo de etiqueta temporário */
			IEntity labelEntity = UtilsCrud.create(this.processManager.getServiceManager(), ModelLabelEntity.class, null);
            labelEntity.setPropertyValue(ModelLabelEntity.NAME, "LabelTest");
            labelEntity.setPropertyValue(ModelLabelEntity.DESCRIPTION, "Primeiro teste de ModelLabelEntity");
            labelEntity.setPropertyValue(ModelLabelEntity.APPLICATION_ENTITY, appEntity);
            labelEntity.setPropertyValue(ModelLabelEntity.LINE1, "Nome do usuário: #{ApplicationUser[?].name}");
            labelEntity.setPropertyValue(ModelLabelEntity.LINE2, "Login: #{ApplicationUser[?].login}");
            labelEntity.setPropertyValue(ModelLabelEntity.LINE3, "Senha: #{ApplicationUser[?].password}");
            labelEntity.setPropertyValue(ModelLabelEntity.LINE4, "Grupo: #{ApplicationUser[?].securityGroups}");
            labelEntity.setPropertyValue(ModelLabelEntity.LINE5, "Inativo: #{ApplicationUser[?].inactive}");
            
            process.setEntity(entity);
            process.setModelLabelEntity(labelEntity);
            process.runCreate();
            
            UtilsTest.showMessageList(process.getMessageList());
            
            Assert.assertTrue(process.getMessageList().isTransactionSuccess());

            /* Testa se usando o mesmo processo não vai dar erro */
            process.setEntityType(entity.getInfo().getType());
            process.setEntityId(entity.getId());
//            process.setModelLabelEntity(labelEntity); // Esta labelEntity nao esta persistida, logo nao dah pra testar pelo getId();
            UtilsCrud.update(this.processManager.getServiceManager(), labelEntity, null);
            process.setModelLabelEntityId(labelEntity.getId()); // Esta labelEntity nao esta persistida, logo nao dah pra testar pelo getId();
            process.runCreate();
            
            UtilsTest.showMessageList(process.getMessageList());
            
            Assert.assertTrue(process.getMessageList().isTransactionSuccess());
        
        }
        catch(BusinessException e)
        {
            Assert.assertTrue(false);
        }
    }

    public void testIncompatibleModel()
    {
        try{
        	
			IEntity appEntity = UtilsSecurity.retrieveEntity(this.processManager.getServiceManager(), ApplicationEntity.class, null);
			IEntity entity = UtilsCrud.retrieve(this.processManager.getServiceManager(), ApplicationUser.class, 1, null);

        	/* Cria um modelo de etiqueta temporário */
			IEntity labelEntity = UtilsCrud.create(this.processManager.getServiceManager(), ModelLabelEntity.class, null);
            labelEntity.setPropertyValue(ModelLabelEntity.NAME, "LabelTest");
            labelEntity.setPropertyValue(ModelLabelEntity.DESCRIPTION, "Primeiro teste de ModelLabelEntity");
            labelEntity.setPropertyValue(ModelLabelEntity.APPLICATION_ENTITY, appEntity);
            labelEntity.setPropertyValue(ModelLabelEntity.LINE1, "Nome do usuário: #{ApplicationUser[?].name}");
            labelEntity.setPropertyValue(ModelLabelEntity.LINE2, "Login: #{ApplicationUser[?].login}");
            labelEntity.setPropertyValue(ModelLabelEntity.LINE3, "Senha: #{ApplicationUser[?].password}");
            labelEntity.setPropertyValue(ModelLabelEntity.LINE4, "Grupo: #{ApplicationUser[?].securityGroups}");
            labelEntity.setPropertyValue(ModelLabelEntity.LINE5, "Inativo: #{ApplicationUser[?].inactive}");
            
            process.setEntity(entity);
            process.setModelLabelEntity(labelEntity);
            process.runCreate();
            
            UtilsTest.showMessageList(process.getMessageList());
            
            Assert.assertFalse(process.getMessageList().isTransactionSuccess());
        
        }
        catch(BusinessException e)
        {
            Assert.assertTrue(false);
        }
    }

}
