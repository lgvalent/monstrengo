package br.com.orionsoft.monstrengo.crud.documents.processes;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.test.ProcessBasicTest;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;
import br.com.orionsoft.monstrengo.crud.documents.entities.ModelDocumentEntity;
import br.com.orionsoft.monstrengo.crud.documents.processes.CompileDocumentProcess;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;
import br.com.orionsoft.monstrengo.security.services.UtilsSecurity;

public class CompileDocumentProcessTestCase extends ProcessBasicTest
{
    CompileDocumentProcess process;

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(CompileDocumentProcessTestCase.class);
    }

    @Before
    public void setUp() throws BusinessException, Exception{
    	super.setUp();
    	
    	process = (CompileDocumentProcess) this.processManager.createProcessByName(CompileDocumentProcess.PROCESS_NAME, this.getAdminSession());
    }

    @After
    public void tearDown() throws Exception{
    	process.finish();
    	process = null;

    	super.tearDown();
    }

    @Test
    public void testCrudExpression()
    {
        try{
        	
			IEntity appEntity = UtilsSecurity.retrieveEntity(this.processManager.getServiceManager(), ApplicationUser.class, null);
			IEntity entity = UtilsCrud.retrieve(this.processManager.getServiceManager(), ApplicationUser.class, 1, null);

        	/* Cria um modelo de etiqueta temporário */
			IEntity documentEntity = UtilsCrud.create(this.processManager.getServiceManager(), ModelDocumentEntity.class, null);
            documentEntity.setPropertyValue(ModelDocumentEntity.NAME, "LabelTest");
            documentEntity.setPropertyValue(ModelDocumentEntity.DESCRIPTION, "Primeiro teste de ModelLabelEntity");
            documentEntity.setPropertyValue(ModelDocumentEntity.APPLICATION_ENTITY, appEntity);
            documentEntity.setPropertyValue(ModelDocumentEntity.APPLICATION_USER, entity);
            documentEntity.setPropertyValue(ModelDocumentEntity.SOURCE, "Documento básico que exibe o nome do usuário: #{ApplicationUser[?].name}");
            
            process.setEntity(entity);
            UtilsCrud.update(this.processManager.getServiceManager(), documentEntity, null);
            process.setModelDocumentEntity(documentEntity);
            process.runCompileCrudExpression();
            
            UtilsTest.showMessageList(process.getMessageList());
            
            System.out.println("--== RESULTADO ==--");
            System.out.println(process.getCompiledCrudDocument());
            
            Assert.assertTrue(process.getMessageList().isTransactionSuccess());

        
        }
        catch(BusinessException e)
        {
        	Assert.assertTrue(false);
        }
    }
    
    @Test
    public void testFieldsExpression()
    {
        try{
        	
			IEntity appEntity = UtilsSecurity.retrieveEntity(this.processManager.getServiceManager(), ApplicationUser.class, null);
			IEntity entity = UtilsCrud.retrieve(this.processManager.getServiceManager(), ApplicationUser.class, 1, null);

        	/* Cria um modelo de etiqueta temporário */
			IEntity documentEntity = UtilsCrud.create(this.processManager.getServiceManager(), ModelDocumentEntity.class, null);
            documentEntity.setPropertyValue(ModelDocumentEntity.NAME, "LabelTest");
            documentEntity.setPropertyValue(ModelDocumentEntity.DESCRIPTION, "Primeiro teste de ModelLabelEntity");
            documentEntity.setPropertyValue(ModelDocumentEntity.APPLICATION_ENTITY, appEntity);
            documentEntity.setPropertyValue(ModelDocumentEntity.APPLICATION_USER, entity);
            documentEntity.setPropertyValue(ModelDocumentEntity.SOURCE, "Documento básico que exibe o nome do usuário: #{ApplicationUser[?].name} e coletar o campo 1 @{Campo 1, Valor padrão}");
            
            process.setEntity(entity);
            UtilsCrud.update(this.processManager.getServiceManager(), documentEntity, null);
            process.setModelDocumentEntity(documentEntity);

            process.runCompileCrudExpression();
            
            UtilsTest.showMessageList(process.getMessageList());
            
            System.out.println("--== RESULTADO CRUD ==--");
            System.out.println(process.getCompiledCrudDocument());
            
            process.runCompileFieldsExpression();
            System.out.println("--== RESULTADO FIELDS ==--");
            System.out.println(process.getCompiledFieldsDocument());

            Assert.assertTrue(process.getMessageList().isTransactionSuccess());

        
        }
        catch(BusinessException e)
        {
        	Assert.assertTrue(false);
        }
    }

}
