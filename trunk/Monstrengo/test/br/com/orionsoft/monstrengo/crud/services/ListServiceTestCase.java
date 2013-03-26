package br.com.orionsoft.monstrengo.crud.services;

import org.junit.Assert;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.services.ListService;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;

public class ListServiceTestCase extends ServiceBasicTest
{
    
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(ListServiceTestCase.class);
    }

    public void testRun()
    {
        try
        {
            
            ServiceData sd = new ServiceData(ListService.SERVICE_NAME, null);
            sd.getArgumentList().setProperty(ListService.CLASS, ApplicationUser.class);
            
            this.serviceManager.execute(sd);
            
            IEntityList collection = (IEntityList) sd.getOutputData(0);
            
            Assert.assertTrue(collection.size()>0);
        }catch (BusinessException e)
        {
            UtilsTest.showMessageList(e.getErrorList());
            
            Assert.assertTrue(false);
        }
        
    }

}
