package br.com.orionsoft.monstrengo.crud.labels.services;

import org.junit.Assert;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.labels.entities.AddressLabel;
import br.com.orionsoft.monstrengo.crud.labels.entities.ModelLabelEntity;
import br.com.orionsoft.monstrengo.crud.labels.services.CreateLabelFromEntityService;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.security.entities.ApplicationEntity;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;
import br.com.orionsoft.monstrengo.security.services.UtilsSecurity;

public class CreateLabelFromEntityServiceTestCase extends ServiceBasicTest
{
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(CreateLabelFromEntityServiceTestCase.class);
    }

    public void testExecute()
    {
        try{
            IEntity addressLabel = UtilsCrud.create(this.serviceManager, AddressLabel.class, null);

            System.out.println(addressLabel);
            
        	IEntity user = UtilsCrud.retrieve(this.serviceManager, ApplicationUser.class, 1, null);

			IEntity appEntity = UtilsSecurity.retrieveEntity(this.serviceManager, ApplicationEntity.class, null);
			IEntity entity = UtilsCrud.retrieve(this.serviceManager, ApplicationUser.class, 1, null);

        	IEntity labelEntity = UtilsCrud.create(this.serviceManager, ModelLabelEntity.class, null);
            labelEntity.setPropertyValue(ModelLabelEntity.NAME, "LabelTest");
            labelEntity.setPropertyValue(ModelLabelEntity.DESCRIPTION, "Primeiro teste de ModelLabelEntity");
            labelEntity.setPropertyValue(ModelLabelEntity.APPLICATION_ENTITY, appEntity);
            labelEntity.setPropertyValue(ModelLabelEntity.LINE1, "Nome do usuário: #{ApplicationUser[?].name}");
            labelEntity.setPropertyValue(ModelLabelEntity.LINE2, "Login: #{ApplicationUser[?].login}");
            labelEntity.setPropertyValue(ModelLabelEntity.LINE3, "Senha: #{ApplicationUser[?].password}");
            labelEntity.setPropertyValue(ModelLabelEntity.LINE4, "Grupo: #{ApplicationUser[?].securityGroups}");
            labelEntity.setPropertyValue(ModelLabelEntity.LINE5, "Inativo: #{ApplicationUser[?].inactive}");
            
            ServiceData serviceData = new ServiceData(CreateLabelFromEntityService.SERVICE_NAME, null);
            serviceData.getArgumentList().setProperty(CreateLabelFromEntityService.IN_APPLICATION_USER, user);
            serviceData.getArgumentList().setProperty(CreateLabelFromEntityService.IN_MODEL_LABEL_ENTITY, labelEntity);
            serviceData.getArgumentList().setProperty(CreateLabelFromEntityService.IN_ENTITY, entity);
            this.serviceManager.execute(serviceData);
            
        }
        catch(BusinessException e)
        {
            Assert.assertTrue(false);
        }
    }

}
