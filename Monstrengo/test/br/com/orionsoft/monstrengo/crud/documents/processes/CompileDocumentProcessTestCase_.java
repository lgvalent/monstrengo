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

public class CompileDocumentProcessTestCase_ extends ProcessBasicTest
{
    CompileDocumentProcess process;

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(CompileDocumentProcessTestCase_.class);
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
    public void testExecute()
    {
        try{
        	
        	/* Cria um documento  */
			IEntity documentEntity = UtilsCrud.create(this.processManager.getServiceManager(), ModelDocumentEntity.class, null);
            documentEntity.setPropertyValue(ModelDocumentEntity.NAME, "LabelTest");
            documentEntity.setPropertyValue(ModelDocumentEntity.DESCRIPTION, "Primeiro teste de ModelLabelEntity");
            documentEntity.setPropertyValue(ModelDocumentEntity.SOURCE, "Documento básico que exibe o nome do usuário: #{ApplicationUser[1].name}");
            
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
    public void testExecuteError()
    {
        try{
        	
        	/* Cria um documento  */
			IEntity<ModelDocumentEntity> documentEntity = UtilsCrud.create(this.processManager.getServiceManager(), ModelDocumentEntity.class, null);
            documentEntity.setPropertyValue(ModelDocumentEntity.NAME, "LabelTest");
            documentEntity.setPropertyValue(ModelDocumentEntity.DESCRIPTION, "Primeiro teste de ModelLabelEntity");
            documentEntity.setPropertyValue(ModelDocumentEntity.SOURCE, "Documento básico que exibe o nome do usuário: #{ApplicationUser[?].name}");
            
            process.setModelDocumentEntity(documentEntity);
            process.setEntityType(ApplicationUser.class);
            process.setEntityId(0);
            
            if(process.runCompileCrudExpression()){
            
            	UtilsTest.showMessageList(process.getMessageList());

            	System.out.println("--== RESULTADO ==--");
            	System.out.println(process.getCompiledCrudDocument());

            	Assert.assertTrue(false);
            }

            UtilsTest.showMessageList(process.getMessageList());

        
        }
        catch(BusinessException e)
        {
            UtilsTest.showMessageList(e.getErrorList());
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testExecuteErrorEntityType()
    {
        try{
        	
			IEntity appEntity = UtilsSecurity.retrieveEntity(this.processManager.getServiceManager(), ApplicationUser.class, null);

			/* Cria um documento  */
			IEntity documentEntity = UtilsCrud.create(this.processManager.getServiceManager(), ModelDocumentEntity.class, null);
            documentEntity.setPropertyValue(ModelDocumentEntity.NAME, "LabelTest");
            documentEntity.setPropertyValue(ModelDocumentEntity.DESCRIPTION, "Primeiro teste de ModelLabelEntity");
            documentEntity.setPropertyValue(ModelDocumentEntity.APPLICATION_ENTITY, appEntity);
            documentEntity.setPropertyValue(ModelDocumentEntity.SOURCE, "Documento básico que exibe o nome do usuário: #{ApplicationUser[?].name}");
            
            process.setModelDocumentEntity(documentEntity);
            
            if(process.runCompileCrudExpression()){
            
            	UtilsTest.showMessageList(process.getMessageList());

            	System.out.println("--== RESULTADO ==--");
            	System.out.println(process.getCompiledCrudDocument());

            	Assert.assertTrue(false);
            }

            UtilsTest.showMessageList(process.getMessageList());

        
        }
        catch(BusinessException e)
        {
            UtilsTest.showMessageList(e.getErrorList());
            Assert.assertTrue(true);
        }
    }

}
