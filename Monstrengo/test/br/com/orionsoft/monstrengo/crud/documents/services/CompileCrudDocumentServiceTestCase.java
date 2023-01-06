package br.com.orionsoft.monstrengo.crud.documents.services;

import org.junit.Assert;
import org.junit.Test;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;
import br.com.orionsoft.monstrengo.crud.documents.entities.ModelDocumentEntity;
import br.com.orionsoft.monstrengo.crud.documents.services.CompileCrudDocumentService;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.labels.entities.AddressLabel;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;
import br.com.orionsoft.monstrengo.security.services.UtilsSecurity;

public class CompileCrudDocumentServiceTestCase extends ServiceBasicTest
{
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(CompileCrudDocumentServiceTestCase.class);
    }

    @Test
    public void testExecute()
    {
        try{
            IEntity addressLabel = UtilsCrud.create(this.serviceManager, AddressLabel.class, null);

            System.out.println(addressLabel);
            

			IEntity appEntity = UtilsSecurity.retrieveEntity(this.serviceManager, ApplicationUser.class, null);
			IEntity entity = UtilsCrud.retrieve(this.serviceManager, ApplicationUser.class, 1, null);

        	IEntity labelEntity = UtilsCrud.create(this.serviceManager, ModelDocumentEntity.class, null);
            labelEntity.setPropertyValue(ModelDocumentEntity.NAME, "LabelTest");
            labelEntity.setPropertyValue(ModelDocumentEntity.DESCRIPTION, "Primeiro teste de ModelDocumentEntity");
            labelEntity.setPropertyValue(ModelDocumentEntity.APPLICATION_ENTITY, appEntity);
            labelEntity.setPropertyValue(ModelDocumentEntity.SOURCE, "Nome do usuário: #{ApplicationUser[?].name}");
            
            ServiceData serviceData = new ServiceData(CompileCrudDocumentService.SERVICE_NAME, null);
            serviceData.getArgumentList().setProperty(CompileCrudDocumentService.IN_MODEL_DOCUMENT_ENTITY, labelEntity);
            serviceData.getArgumentList().setProperty(CompileCrudDocumentService.IN_ENTITY_OPT, entity);
            this.serviceManager.execute(serviceData);
            
            System.out.println(serviceData.getFirstOutput());
            
            UtilsCrud.update(this.serviceManager, labelEntity, null);
        }
        catch(BusinessException e)
        {
        	UtilsTest.showMessageList(e.getErrorList());
        	Assert.assertTrue(false);
        }
    }

}
