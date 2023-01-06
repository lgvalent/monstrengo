package br.com.orionsoft.monstrengo.crud.services;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;

public class UtilsCrudTestCase extends ServiceBasicTest
{
    
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(UtilsCrudTestCase.class);
    }

    /**
     * Testa os métodos do UtilsCrud que pega objetos e não IEntity
     */
    @Test
    public void testCrudObject()
    {
        try
        {
            System.out.println("Retrieving...");
            ApplicationUser user = UtilsCrud.objectRetrieve(this.serviceManager, ApplicationUser.class, 1l, null);
            System.out.println(user);

            System.out.println("Creating...");
            user = UtilsCrud.objectCreate(this.serviceManager, ApplicationUser.class, null);
            System.out.println(user);
            
            System.out.println("Updating...");
            UtilsCrud.objectUpdate(this.serviceManager, user, null);
            System.out.println(user);
            
            System.out.println("Deleting...");
            UtilsCrud.objectDelete(this.serviceManager, user, null);
            
            System.out.println("Listing...");
            List<ApplicationUser> users = UtilsCrud.objectList(this.serviceManager, ApplicationUser.class, null);
            System.out.println(users);
            
            System.out.println("Filtering...");
            users = UtilsCrud.objectList(this.serviceManager, ApplicationUser.class, "entity.id = 1", null);
            System.out.println(users);
            
        }catch (BusinessException e)
        {
            UtilsTest.showMessageList(e.getErrorList());
            
            Assert.fail(e.getMessage());
        }
        
    }

}
