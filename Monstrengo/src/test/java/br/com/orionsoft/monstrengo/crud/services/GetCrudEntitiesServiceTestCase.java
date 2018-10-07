package br.com.orionsoft.monstrengo.crud.services;

import java.util.List;

import org.junit.Assert;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IEntityMetadata;
import br.com.orionsoft.monstrengo.crud.services.GetCrudEntitiesService;

public class GetCrudEntitiesServiceTestCase extends ServiceBasicTest
{

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(GetCrudEntitiesServiceTestCase.class);
    }
    
    public void testExecute()
    {
        try
        {
            System.out.println("Testando:" +this.getClass().getName());
            System.out.println(":Obtem a lista de entidades registradas CRUD.");
            ServiceData svdg = new ServiceData(GetCrudEntitiesService.SERVICE_NAME, null);
            serviceManager.execute(svdg);
            List list = (List) svdg.getOutputData(0);
            System.out.println(":Mostrando " + list.size() + " items.");
            for(Object klazz: list) 
                System.out.println(((IEntityMetadata)klazz).getName());
            
            Assert.assertTrue(list.size()>0);
        } 
        catch (BusinessException e)
        {
            for(BusinessMessage er: e.getErrorList())
                System.out.println(er.getMessageClass().getSimpleName() + ":" + er.getErrorKey() + "=" + er.getMessage());
            
            Assert.assertTrue(false);
        } 
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            
            Assert.assertTrue(false);
        }
    }

}
