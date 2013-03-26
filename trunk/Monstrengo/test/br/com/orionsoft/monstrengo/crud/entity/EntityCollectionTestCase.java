package br.com.orionsoft.monstrengo.crud.entity;

import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.test.EntityBasicTest;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityCollection;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.services.ListService;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;

public class EntityCollectionTestCase extends EntityBasicTest
{
    private IEntityCollection collection;
    
//    public static void main(String[] args)
//    {
//        junit.textui.TestRunner.run(EntityCollectionTestCase.class);
//    }

    public void setUp() throws Exception
    {
        super.setUp();
        
        
        ServiceData sd = new ServiceData(ListService.SERVICE_NAME, null);
        sd.getArgumentList().setProperty(ListService.CLASS, ApplicationUser.class);
        this.entityManager.getServiceManager().execute(sd);
        
        collection = (IEntityCollection) sd.getFirstOutput();
    }

    public void tearDown() throws Exception
    {
        super.tearDown();
    }

    @Test
    public void testRun()
    {
        try{
            
            Assert.assertTrue(collection.size()>0);
            
            Assert.assertFalse(collection.isEmpty());
            
            Assert.assertTrue(collection.contains(collection.getFirst()));
            
            // Iterator
            IEntity ent0 = collection.getFirst(); // Pega a primeira entidade para posteriormente remover ela
            int size0 = collection.size();
            for(Iterator<IEntity> it = collection.iterator(); it.hasNext();)
            {
                
                IEntity actual = it.next();

                // Testa o .remove() do Iterator
                if (actual.getObject().equals(ent0.getObject()))
                    it.remove();
            }

            // A coleção deverá ter um elemento a menos
            Assert.assertTrue(collection.size()==(size0-1));
            
        
        }
        catch(Exception e)
        {
            e.printStackTrace();
            
            Assert.assertTrue(false);
        }
    }


    /*
     * Test method for 'br.com.orionsoft.monstrengo.crud.services.entity.EntityCollection.toArray()'
     */
    @Test
    public void testRunAddAndRemove()
    {
        try{
            
        	collection.clear();
        	
        	System.out.println("Size:" + collection.getSize());
        	UtilsTest.showEntityProperties(collection.getRunEntity());
        	
        	System.out.println("Size:" + collection.getSize());
        	
        	collection.runAdd();
        	System.out.println("Size:" + collection.getSize());
        	
        	collection.setRunEntity(collection.getFirst());
        	collection.runRemove();
        	System.out.println("Size:" + collection.getSize());
        }
        catch(Exception e)
        {
            e.printStackTrace();
            
            Assert.assertTrue(false);
        }
    }


    /**
     * Testa se o método de remoção por iterator está funcioando 
     */
    @Test
    public void testIteratorRemove()
    {
        try{
            
            System.out.println("collection.size()="+collection.size());

            Assert.assertTrue(collection.size()>0);
            Assert.assertFalse(collection.isEmpty());
            Assert.assertTrue(collection.contains(collection.getFirst()));
            
            // Iterator
            IEntity ent0 = collection.getFirst(); // Pega a primeira entidade para posteriormente remover ela
            int size0 = collection.size();
            for(Iterator<IEntity> it = collection.iterator(); it.hasNext();)
            {
                
                IEntity actual = it.next();

                // Testa o .remove() do Iterator
                if (actual.getObject().equals(ent0.getObject())){
                    System.out.println("Remove o primeiro elemento da lista");
                    it.remove();
                }
            }

            // A coleção deverá ter um elemento a menos
            Assert.assertTrue(collection.size()==(size0-1));
            System.out.println("collection.size()="+collection.size());
            System.out.println("collection.size()="+((IEntityList)collection).getObjectList().size());
        }
        catch(Exception e)
        {
            e.printStackTrace();
            
            Assert.assertTrue(false);
        }
    }



}
