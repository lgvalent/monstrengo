package br.com.orionsoft.monstrengo.crud.services;

import org.junit.Assert;
import org.junit.Test;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityCollection;
import br.com.orionsoft.monstrengo.crud.entity.IProperty;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IEntityMetadata;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IPropertyMetadata;
import br.com.orionsoft.monstrengo.crud.entity.metadata.PropertyMetadata;
import br.com.orionsoft.monstrengo.crud.services.CreateService;
import br.com.orionsoft.monstrengo.crud.services.DeleteService;
import br.com.orionsoft.monstrengo.crud.services.ListService;
import br.com.orionsoft.monstrengo.crud.services.RetrieveService;
import br.com.orionsoft.monstrengo.crud.services.UpdateService;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.security.entities.ApplicationEntityProperty;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;
import br.com.orionsoft.monstrengo.security.entities.RightCrud;

public class CrudServiceTestCase extends ServiceBasicTest
{

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(CrudServiceTestCase.class);
    }
    
    public void testCreate_()
    {
        try
        {	
        	System.out.println(":Testando instanciação de uma entidade com seus metadados");
            ServiceData svdc = new ServiceData(CreateService.SERVICE_NAME, null);
            svdc.getArgumentList().setProperty(CreateService.IN_ENTITY_TYPE, ApplicationEntityProperty.class);
            serviceManager.execute(svdc);

            Assert.assertTrue(true);
        } catch (BusinessException e)
        {
        	UtilsTest.showMessageList(e.getErrorList());
        	Assert.assertTrue(false);
        }
    }

    public void testCreate()
    {
        try
        {	
        	System.out.println(":Testando instanciação de classe abstrata");
        	abstract class MyClass{ }
            ServiceData svdc = new ServiceData(CreateService.SERVICE_NAME, null);
            svdc.getArgumentList().setProperty(CreateService.IN_ENTITY_TYPE, MyClass.class);
            serviceManager.execute(svdc);

            Assert.assertTrue(false);
        } catch (BusinessException e)
        {
        	UtilsTest.showMessageList(e.getErrorList());
        	Assert.assertTrue(true);
        }
    }

    public void testCreateCopy()
    {
        try
        {	
            IEntity user = UtilsCrud.retrieve(this.serviceManager, ApplicationUser.class, 1, null);
            UtilsTest.showEntityProperties(user);

            System.out.println(":Testando instanciação de classe copiando os valores de outra");
            ServiceData svdc = new ServiceData(CreateService.SERVICE_NAME, null);
            svdc.getArgumentList().setProperty(CreateService.IN_ENTITY_TYPE, ApplicationUser.class);
            svdc.getArgumentList().setProperty(CreateService.IN_ENTITY_COPY_ID_OPT, user.getId());
            serviceManager.execute(svdc);

            IEntity newUser = svdc.getFirstOutput();

            UtilsTest.showEntityProperties(newUser);

            /* A cópia tem que ter o mesmo login */
            Assert.assertEquals(user.getProperty(ApplicationUser.NAME), newUser.getProperty(ApplicationUser.NAME));
        } catch (BusinessException e)
        {
        	UtilsTest.showMessageList(e.getErrorList());
        	Assert.assertTrue(false);
        }
    }

    public void testCreateDefaultValue()
    {
        try
        {	
        	IEntityMetadata entM = this.serviceManager.getEntityManager().getEntityMetadata(RightCrud.class);
        	PropertyMetadata propM = (PropertyMetadata) entM.getPropertyMetadata(RightCrud.SECURITY_GROUP);
        	/* Testa o erro */
        	try{
        		propM.setDefaultValue("0");
        		
        		System.out.println(":Testando instanciação de classe com valores padroes");
        		ServiceData svdc = new ServiceData(CreateService.SERVICE_NAME, null);
        		svdc.getArgumentList().setProperty(CreateService.IN_ENTITY_TYPE, RightCrud.class);
        		serviceManager.execute(svdc);
        		
        		Assert.assertTrue(false);
        	}
        	catch(BusinessException e){
            	UtilsTest.showMessageList(e.getErrorList());
            	Assert.assertTrue(true);
        	}

            /* Testa o CERTO  */
        	propM.setDefaultValue("1");
        	
        	System.out.println(":Testando instanciação de classe com valores padroes");
            ServiceData svdc = new ServiceData(CreateService.SERVICE_NAME, null);
            svdc.getArgumentList().setProperty(CreateService.IN_ENTITY_TYPE, RightCrud.class);
            serviceManager.execute(svdc);
            
            IEntity ent = svdc.getFirstOutput();
            IProperty prop = ent.getProperty(RightCrud.SECURITY_GROUP);
            
            UtilsTest.showEntityProperties(ent);

            Assert.assertEquals(new Long(1), prop.getValue().getId());
        } catch (BusinessException e)
        {
        	UtilsTest.showMessageList(e.getErrorList());
        	Assert.assertTrue(false);
        }
    }

    public void testCrud()
    {
        try
        {
            System.out.println(":Cria.");
            ServiceData svdc = new ServiceData(CreateService.SERVICE_NAME, null);
            svdc.getArgumentList().setProperty(CreateService.IN_ENTITY_TYPE, ApplicationUser.class);
            serviceManager.execute(svdc);
            IEntity en = (IEntity) svdc.getOutputData(0);

            Assert.assertNotNull(en);
            System.out.println(":Entidade VAZIA criada.");
            UtilsTest.showEntityProperties(en);
            
            System.out.println(":Atualiza.");
//            en.getProperty(Endereco.NUMERO).getValue().setAsString("12345");
//            en.getProperty(Endereco.COMPLEMENTO).getValue().setAsString("Complemento de teste");
            en.getProperty(ApplicationUser.LOGIN).getValue().setAsString("lucio");
            en.getProperty(ApplicationUser.PASSWORD).getValue().setAsString("lucio");
            System.out.println(":Mostra criado.");
            UtilsTest.showEntityProperties(en);

            ServiceData svdu = new ServiceData(UpdateService.SERVICE_NAME, null);
            svdu.getArgumentList().setProperty(UpdateService.IN_ENTITY, en);
            serviceManager.execute(svdu);
            
            Assert.assertEquals(0, svdu.getMessageList().size());

            
            System.out.println(":Obtem.");
            ServiceData svdr = new ServiceData(RetrieveService.SERVICE_NAME, null);
            svdr.getArgumentList().setProperty(RetrieveService.CLASS, en.getInfo().getType());
            svdr.getArgumentList().setProperty(RetrieveService.ID_LONG, en.getId());
            serviceManager.execute(svdr);
            
            en = (IEntity) svdr.getOutputData(0);

            Assert.assertNotNull(en);
            
            System.out.println(":Mostra obtido.");
            for(IProperty prop: en.getProperties()) 
                System.out.println(prop.getInfo().getLabel() + "=" + prop.getValue().getAsString());
            
            System.out.println(":Delete.");
            ServiceData svdd = new ServiceData(DeleteService.SERVICE_NAME, null);
            svdd.getArgumentList().setProperty(DeleteService.IN_ENTITY, en);
            serviceManager.execute(svdd);
            
            Assert.assertTrue(true);
        } catch (BusinessException e)
        {
            for(BusinessMessage er: e.getErrorList())
                System.out.println(er.getMessageClass().getSimpleName() + ":" + er.getErrorKey() + "=" + er.getMessage());
            Assert.assertTrue(false);
        }
    }

    public void testList()
    {
        try
        {
            System.out.println(":List.");
            ServiceData svdl = new ServiceData(ListService.SERVICE_NAME, null);
            svdl.getArgumentList().setProperty(ListService.CLASS, ApplicationUser.class);
            serviceManager.execute(svdl);
            IEntityCollection<ApplicationUser> enl = svdl.getFirstOutput();
            
            for(IEntity<ApplicationUser> en: enl)
            {
                System.out.println("Entidade:" + en.getInfo().getLabel());
                for(IProperty prop: en.getProperties()) 
                    System.out.println(" " + prop.getInfo().getLabel() + "=" + prop.getValue().getAsString());
            }

            Assert.assertTrue(true);
        } catch (BusinessException e)
        {
            for(BusinessMessage er: e.getErrorList())
                System.out.println(er.getMessageClass().getSimpleName() + ":" + er.getErrorKey() + "=" + er.getMessage());
            Assert.assertTrue(false);
        }
    }

	/**
	 * Testa se o serviço Create cria as entidades de relacionamentos OneToOne da entidade principal.
	 *
	 */
    public void testCreateOneToOne()
	{
	    try
	    {	
	        /* Altera o metadados da classe para OneToOne só pra testar o CreateService */
	    	IEntityMetadata entityMetadata = this.serviceManager.getEntityManager().getEntityMetadata(RightCrud.class);
	        IPropertyMetadata propMetadata = entityMetadata.getPropertyMetadata(RightCrud.SECURITY_GROUP);
	        ((PropertyMetadata)propMetadata).setEditShowEmbedded(true);
	        
	    	System.out.println(":Testando a criação de um objeto com relacionamento OneToOne");
	        ServiceData svdc = new ServiceData(CreateService.SERVICE_NAME, null);
	        svdc.getArgumentList().setProperty(CreateService.IN_ENTITY_TYPE, RightCrud.class);
	        serviceManager.execute(svdc);
	
	        IEntity ent = svdc.getFirstOutput();
	
	        UtilsTest.showEntityProperties(ent);
	
	        Assert.assertFalse(ent.getProperty(RightCrud.SECURITY_GROUP).getValue().isValueNull());
	    } catch (BusinessException e)
	    {
	    	UtilsTest.showMessageList(e.getErrorList());
	    	Assert.assertTrue(false);
	    }
	}

    @Test
    public void testRetrieve()
    {
        try
        {
            System.out.println(":Retrieve");
            ServiceData svdl = new ServiceData(RetrieveService.SERVICE_NAME, null);
            svdl.getArgumentList().setProperty(RetrieveService.CLASS, ApplicationUser.class);
            svdl.getArgumentList().setProperty(RetrieveService.ID_LONG, 1l);
            serviceManager.execute(svdl);
            serviceManager.execute(svdl);
            IEntity en = svdl.getFirstOutput();
            
            System.out.println("Entidade:" + en.getInfo().getLabel());
            for(IProperty prop: en.getProperties()) 
               System.out.println(" " + prop.getInfo().getLabel() + "=" + prop.getValue().getAsString());

            Assert.assertTrue(true);
        } catch (BusinessException e)
        {
            for(BusinessMessage er: e.getErrorList())
                System.out.println(er.getMessageClass().getSimpleName() + ":" + er.getErrorKey() + "=" + er.getMessage());
            Assert.fail(e.getMessage());
        }
    }

}
