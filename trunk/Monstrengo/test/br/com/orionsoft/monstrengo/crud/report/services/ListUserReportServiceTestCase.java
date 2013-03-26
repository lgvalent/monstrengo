package br.com.orionsoft.monstrengo.crud.report.services;

import java.util.List;

import javax.faces.model.SelectItem;

import org.junit.Assert;

import br.com.orionsoft.monstrengo.auditorship.entities.AuditCrudRegister;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.report.services.ListUserReportService;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;
import br.com.orionsoft.monstrengo.security.services.UtilsSecurity;

public class ListUserReportServiceTestCase extends ServiceBasicTest
{
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(ListUserReportServiceTestCase.class);
    }

    public void testExecute()
    {
        try{

			IEntity appUser = UtilsCrud.retrieve(this.serviceManager, ApplicationUser.class, 1, null);
			IEntity appEntity = UtilsSecurity.retrieveEntity(this.serviceManager, AuditCrudRegister.class, null);
            
            ServiceData serviceData = new ServiceData(ListUserReportService.SERVICE_NAME, null);
//            serviceData.getArgumentList().setProperty(ListUserReportService.IN_APPLICATION_USER_ID_OPT, appUser.getId());
            serviceData.getArgumentList().setProperty(ListUserReportService.IN_APPLICATION_ENTITY_ID_OPT, appEntity.getId());
            this.serviceManager.execute(serviceData);
            
            for(SelectItem item: (List<SelectItem>) serviceData.getFirstOutput())
            	System.out.println(item.getValue() + "=>" + item.getLabel());
            
        }
        catch(BusinessException e)
        {
        	UtilsTest.showMessageList(e.getErrorList());
        	Assert.assertTrue(false);
        }
    }

}
