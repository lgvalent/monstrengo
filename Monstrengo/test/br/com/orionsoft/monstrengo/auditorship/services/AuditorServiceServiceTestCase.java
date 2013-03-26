package br.com.orionsoft.monstrengo.auditorship.services;

import org.junit.Assert;
import org.junit.Test;

import br.com.orionsoft.monstrengo.auditorship.services.AuditorServiceService;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;

public class AuditorServiceServiceTestCase extends ServiceBasicTest
{
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(AuditorServiceServiceTestCase.class);
    }

    @Test
    public void testExecute()
    {
        try{
             
            IEntityList<ApplicationUser> users = UtilsCrud.list(this.serviceManager, ApplicationUser.class, null);
            IEntity<ApplicationUser> user = users.get(0);
            user.setPropertyValue(ApplicationUser.NAME, user.getObject().getName() + "-Test");
            UtilsCrud.update(this.serviceManager, user, null);

            ServiceData serviceData = new ServiceData(AuditorServiceService.SERVICE_NAME, null);
            serviceData.getArgumentList().setProperty(AuditorServiceService.IN_APPLICATION_USER_OPT, user);
            serviceData.getArgumentList().setProperty(AuditorServiceService.IN_SERVICE_DATA, serviceData);
            serviceData.getArgumentList().setProperty(AuditorServiceService.IN_TERMINAL, "terminalTeste");
            serviceData.getArgumentList().setProperty(AuditorServiceService.IN_DESCRIPTION_STR, "No descriptions avaliable");
            this.serviceManager.execute(serviceData);
               
        }
        catch(BusinessException e)
        {
            Assert.fail(e.getMessage());
        }
    }

}
