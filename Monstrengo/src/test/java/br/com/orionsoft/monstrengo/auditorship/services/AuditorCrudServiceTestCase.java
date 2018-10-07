package br.com.orionsoft.monstrengo.auditorship.services;

import org.junit.Assert;
import org.junit.Test;

import br.com.orionsoft.monstrengo.auditorship.services.AuditorCrudService;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.security.entities.ApplicationEntity;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;
import br.com.orionsoft.monstrengo.security.entities.SecurityGroup;
import br.com.orionsoft.monstrengo.security.entities.UserSession;
import br.com.orionsoft.monstrengo.security.services.UtilsSecurity;

public class AuditorCrudServiceTestCase extends ServiceBasicTest
{
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(AuditorCrudServiceTestCase.class);
    }

    @Test
    public void testExecute()
    {
        try{
            IEntity<ApplicationEntity> entity = UtilsSecurity.retrieveEntity(this.serviceManager, SecurityGroup.class, null);
            IEntityList<SecurityGroup> groups = UtilsCrud.list(this.serviceManager, SecurityGroup.class, null);
            IEntity<SecurityGroup> group = groups.get(0);
            group.setPropertyValue(SecurityGroup.NAME, group.getObject().getName() + "-Test");
            UtilsCrud.update(this.serviceManager, group, null);
            
            IEntityList<ApplicationUser> users = UtilsCrud.list(this.serviceManager, ApplicationUser.class, null);
            IEntity<ApplicationUser> user = users.get(0);
            user.setPropertyValue(ApplicationUser.NAME, user.getObject().getName() + "-Test");
            UtilsCrud.update(this.serviceManager, user, null);

            UserSession userSession = new UserSession(user, "Teste");
            
            ServiceData serviceData = new ServiceData(AuditorCrudService.SERVICE_NAME, null);
            serviceData.getArgumentList().setProperty(AuditorCrudService.IN_APPLICATION_ENTITY, entity);
            serviceData.getArgumentList().setProperty(AuditorCrudService.IN_ENTITY_ID, group.getId());
            serviceData.getArgumentList().setProperty(AuditorCrudService.IN_USER_SESSION, userSession);
            serviceData.getArgumentList().setProperty(AuditorCrudService.IN_CREATED_BOOL_OPT, true);
            serviceData.getArgumentList().setProperty(AuditorCrudService.IN_DESCRIPTION_STR, "No descriptions avaliable");
            this.serviceManager.execute(serviceData);
               
        }
        catch(BusinessException e)
        {
            Assert.fail(e.getMessage());
        }
    }

}
