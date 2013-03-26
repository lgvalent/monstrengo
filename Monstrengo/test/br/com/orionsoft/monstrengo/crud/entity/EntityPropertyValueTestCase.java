package br.com.orionsoft.monstrengo.crud.entity;

import java.math.BigDecimal;
import java.util.Calendar;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.test.EntityBasicTest;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IPropertyValue;
import br.com.orionsoft.monstrengo.crud.entity.PropertyValueException;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;

/**
 * Esta classe realiza os testes sobre os valores das propriedades de uma entidade
 * 
 * @author marcia
 *
 */
public class EntityPropertyValueTestCase extends EntityBasicTest
{
    
    private static BigDecimal BIGDECIMAL = new BigDecimal("1");
    private static boolean BOOLEAN = true;
    private static Calendar CALENDAR = Calendar.getInstance();
    private static double DOUBLE = 123456.01;
    private static int INT = 1;
    private static long LONG = 123456;
    private static String STRING = "myString";
    
    private IPropertyValue propertyValueId;
    private IPropertyValue propertyValueLogin;
    private IPropertyValue propertyValueSenha;
    @SuppressWarnings("unused")
    private IPropertyValue propertyValueGroups;
    private IEntity entity;
    private IEntity entityTest;
    
//    public static void main(String[] args)
//    {
//        junit.textui.TestRunner.run(EntityPropertyValueTestCase.class);
//    }

    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        
        ApplicationUser user = new ApplicationUser();
        user.setId(10000);
        user.setLogin("Login de teste");
        
        entity = entityManager.getEntity(user);
        
        /* Cria e popula o objeto e a entidade */
        ObjectTest obj = new ObjectTest(); 
        entityTest = entityManager.getEntity(obj);
        ObjectTest ob = (ObjectTest) entityTest.getObject();
        ob.setBigDecimal(BIGDECIMAL);
        ob.setBoolean_(BOOLEAN);
        ob.setCalendar(CALENDAR);
        ob.setDouble_(DOUBLE);
        ob.setInt_(INT);
        ob.setLong_(LONG);
        ob.setString(STRING);

        propertyValueId = entity.getProperty("id").getValue();
        propertyValueLogin = entity.getProperty(ApplicationUser.LOGIN).getValue();
        propertyValueSenha = entity.getProperty(ApplicationUser.PASSWORD).getValue();
        propertyValueGroups = entity.getProperty(ApplicationUser.SECURITY_GROUPS).getValue();
    
    }
    
    @After
    public void tearDown() throws Exception
    {
        super.tearDown();
    }
    
    @Test
    public void testIsValueNull() throws PropertyValueException
    {
    	System.out.println("testIsValueNull - início");
    	Assert.assertFalse(propertyValueId.isValueNull());
        Assert.assertFalse(propertyValueLogin.isValueNull());
        Assert.assertTrue(propertyValueSenha.isValueNull());
        System.out.println("testIsValueNull - fim");
    }

    @Test
    public void testIsModified()
    {
    	System.out.println("testIsModified - início");
    	Assert.assertFalse(propertyValueId.isModified());
        Assert.assertFalse(propertyValueLogin.isModified());
        Assert.assertFalse(propertyValueSenha.isModified());
        System.out.println("testIsModified - fim");
    }

    @Test
    public void testGetId()
    {
      try{
    	  System.out.println("testGetId - início");
    	  propertyValueId.getId();
          
          Assert.assertTrue(false);
          
      }catch(BusinessException e)
      {
    	  System.out.println("testGetId - fim - exception");
    	  Assert.assertTrue(true);
          UtilsTest.showMessageList(e.getErrorList());
      }
    }

    @Test
    public void testSetId()
    {
        try{
            System.out.println("testSetId - início");
        	propertyValueId.setId(new Long(2));
            
            Assert.assertTrue(false);
     
        }catch(BusinessException e)
        {
        	System.out.println("testSetId - fim - exception");
        	Assert.assertTrue(true);
            UtilsTest.showMessageList(e.getErrorList());
        }
    }

    @Test
    public void testSetAsBoolean()
    {
        try{
        	System.out.println("testSetAsBoolean - início");
        	propertyValueId.setAsBoolean(true);
            
            Assert.assertTrue(false);
            
        }catch(BusinessException e)
        {
        	System.out.println("testSetAsBoolean - fim - exception");
        	Assert.assertTrue(true);
            UtilsTest.showMessageList(e.getErrorList());
        }
    }

    @Test
    public void testGetAsBoolean()
    {
        try{
        	System.out.println("testGetAsBoolean - início");
        	propertyValueId.getAsBoolean();
            
            Assert.assertTrue(false);
        }catch(BusinessException e)
        {
        	System.out.println("testGetAsBoolean - fim - exception");
            UtilsTest.showMessageList(e.getErrorList());
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testSetAsLong()
    {
        try
        {
        	System.out.println("testSetAsLong - início");
        	propertyValueId.setAsLong(new Long(2));
            Assert.assertEquals(new Long(2), propertyValueId.getAsLong());
            System.out.println("testSetAsInt - fim");
            
        }catch(BusinessException e)
        {
        	System.out.println("testSetAsLong - fim - exception");
        	Assert.assertTrue(false);
            UtilsTest.showMessageList(e.getErrorList());
        }
    }
    
    @Test
    public void testSetAsInteger(){
        try{
            System.out.println("testSetAsInteger - início");
            entityTest.getProperty("int_").getValue().setAsInteger(1);
            Assert.assertTrue(true);
            
        }catch (BusinessException e) {
            System.out.println("testSetAsInteger - fim - exception");
            UtilsTest.showMessageList(e.getErrorList());
            Assert.assertTrue(false);
        }
    }

    @Test
    public void testGetAsInteger(){
        try{
            System.out.println("testGetAsInteger - início");
            Assert.assertEquals(entityTest.getProperty("int_").getValue().getAsInteger().intValue(), INT);
            Assert.assertTrue(true);
            
        }catch (BusinessException e) {
            System.out.println("testGetAsInteger - fim - exception");
            UtilsTest.showMessageList(e.getErrorList());
            Assert.assertTrue(false);
        }
    }

    @Test
    public void testSetAsString()
    {
        try
        {
        	System.out.println("testSetAsString - início");
        	propertyValueId.setAsString("3");
            Assert.assertEquals("3", propertyValueId.getAsString());
            
            propertyValueLogin.setAsString("setAsString");
            Assert.assertEquals("setAsString", propertyValueLogin.getAsString());
            
            propertyValueSenha.setAsString("setAsString");
            Assert.assertEquals("setAsString", propertyValueSenha.getAsString());
            
            
            entityTest.getProperty("int_").getValue().setAsString("1");
            entityTest.getProperty("long_").getValue().setAsString("1234567");
            entityTest.getProperty("double_").getValue().setAsString("1,45");
            entityTest.getProperty("bigDecimal").getValue().setAsString("1.253,1");
            entityTest.getProperty("boolean_").getValue().setAsString("sim");
            entityTest.getProperty("boolean_").getValue().setAsString("não");
            entityTest.getProperty("calendar").getValue().setAsString("31/01/2006");
            entityTest.getProperty("string").getValue().setAsString("myString");
            
            System.out.println("testSetAsString - fim");
            Assert.assertTrue(true);
        }catch(BusinessException e)
        {
        	System.out.println("testSetAsString - fim - exception");
        	UtilsTest.showMessageList(e.getErrorList());
            Assert.assertTrue(false);
        }

        // Teste de exceções
        try
        {
        	System.out.println("testSetAsString - início2 - deve dar exception pois tenta setar Id como String");
        	propertyValueId.setAsString("abc");
         
            //System.out.println("testSetAsString - DEVE DAR EXCEPTION - tenta setar um Inteiro como String");
            //entityTest.setPropertyValue("int_", "xx");
            
            Assert.assertTrue(false);
        }catch(BusinessException e)
        {
        	System.out.println("testSetAsString - fim2 - exception - tenta setar Id como String");
        	UtilsTest.showMessageList(e.getErrorList());
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testGetAsString()
    {
        try
        {
        	System.out.println("testGetAsString - início");
        	Assert.assertEquals("10000", propertyValueId.getAsString());
            
            Assert.assertEquals("Login de teste", propertyValueLogin.getAsString());
            
            Assert.assertEquals("", propertyValueSenha.getAsString());
            
            /* 
             * descomentar as linhas abaixocaso queira verificar que as propriedades
             * estão sendo populadas e convertidas corretamente pelo setAsString e getAsString
             */             
//          entityTest.getProperty("int_").getValue().setAsString("1");
//          entityTest.getProperty("long_").getValue().setAsString("1234567");
//          entityTest.getProperty("double_").getValue().setAsString("1,45");
//          entityTest.getProperty("bigDecimal").getValue().setAsString("1.253,1");
//          entityTest.getProperty("boolean_").getValue().setAsString("sim");
//          entityTest.getProperty("boolean_").getValue().setAsString("não");
//          entityTest.getProperty("calendar").getValue().setAsString("31/01/2006");
//          entityTest.getProperty("string").getValue().setAsString("myString");
            
            System.out.println("int_="+entityTest.getProperty("int_").getValue().getAsString());
            System.out.println("long_="+entityTest.getProperty("long_").getValue().getAsString());
            System.out.println("double_="+entityTest.getProperty("double_").getValue().getAsString());
            System.out.println("bigDecimal="+entityTest.getProperty("bigDecimal").getValue().getAsString());
            System.out.println("boolean_="+entityTest.getProperty("boolean_").getValue().getAsString());
            System.out.println("boolean_="+entityTest.getProperty("boolean_").getValue().getAsString());
            System.out.println("calendar="+entityTest.getProperty("calendar").getValue().getAsString());
            System.out.println("string="+entityTest.getProperty("string").getValue().getAsString());
            
            System.out.println("testGetAsString - fim");
        }catch(BusinessException e)
        {
        	System.out.println("testGetAsString - fim - exception");
            UtilsTest.showMessageList(e.getErrorList());
            Assert.assertTrue(false);
        }
    }

    @Test
    public void testSetAsDate()
    {
        try
        {
            //causa um erro tentando colocar Date em Id
        	System.out.println("tesSetAsDate - início - deve causar exceção pois tenta setar Id como Date");
        	propertyValueId.setAsCalendar(Calendar.getInstance());
            
            Assert.assertTrue(false);
        }catch(BusinessException e)
        {
            Assert.assertTrue(true);
            UtilsTest.showMessageList(e.getErrorList());
            System.out.println("testSetAsDate - fim - exception");
        }
    }

    @Test
    public void testGetAsDate()
    {
        try
        {
        	System.out.println("testGetAsDate - início - deve causar exceção pois tenta obter Id como Date");
        	propertyValueId.getAsCalendar();
            
            Assert.assertTrue(false);
        }catch(BusinessException e)
        {
            Assert.assertTrue(true);
            UtilsTest.showMessageList(e.getErrorList());
            System.out.println("testGetAsDate - fim - exception");
        }
    }

    @Test
    public void testGetAsObject()
    {
        try
        {
        	System.out.println("testGetAsObject - início - testa o tipo da classe");        	
        	Assert.assertEquals(Long.class, propertyValueId.getAsObject().getClass());
            Assert.assertEquals(String.class, propertyValueLogin.getAsObject().getClass());
            
            try
            {
            	System.out.println("testGetAsObject - início - testa senha nula");
            	// A senha é nula e null.getClass() gera um erro
            	Assert.assertEquals(String.class, propertyValueSenha.getAsObject().getClass());
                Assert.assertTrue(false);
           }catch(Exception e){
                Assert.assertTrue(true);
                System.out.println("testGetAsObject - fim - exception - classe null (senha)");
            }

           
            Assert.assertTrue(true);
            System.out.println("testGetAsObject - fim - SEM exception - para a comparação entre os tipos das classes");
        }catch(BusinessException e)
        {
            Assert.assertTrue(false);
            UtilsTest.showMessageList(e.getErrorList());
            System.out.println("testGetAsObject - fim - exception - o objeto não é do mesmo tipo");
        }
    }

    @Test
    public void testSetAsObject()
    {
        try{
        	System.out.println("testSetAsObject - início");
        	String teste = propertyValueLogin.getAsString();
            Assert.assertNotNull(teste);
            Assert.assertTrue(true);
            System.out.println("testSetAsObject - fim - SEM exception");
            
        }catch(BusinessException e)
        {
        	System.out.println("testSetAsObject - fim - exception");
            Assert.assertTrue(false);
            UtilsTest.showMessageList(e.getErrorList());
        }
    }

    @Test
    public void testGetAsEntity()
    {
        // Testar exceções
        try
        {
            System.out.println("testGetAsEntity - início");
        	propertyValueLogin.getAsEntity();
            Assert.assertTrue(false);
            
        }catch(BusinessException e)
        {
        	System.out.println("testGetAsEntity - fim - exception");
        	UtilsTest.showMessageList(e.getErrorList());
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testgetAsEntitySet()
    {
        // Testar exceções
        try
        {
        	System.out.println("testgetAsEntitySet - início");
        	propertyValueLogin.getAsEntitySet();
        	
            Assert.assertTrue(false);
            
        }catch(BusinessException e)
        {
        	System.out.println("testgetAsEntitySet - fim - exception");
        	UtilsTest.showMessageList(e.getErrorList());
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testGetOldValue()
    {
        try{

            System.out.println("testGetOldValue - início");
        	// Verifica quando ainda não houve modificação
            Assert.assertNull(propertyValueLogin.getOldValue());
            Assert.assertFalse(propertyValueLogin.isModified());

            // Realiza uma modificação
            propertyValueLogin.setAsString("Lucio");
            
            Assert.assertTrue(propertyValueLogin.isModified());
            Assert.assertEquals("Login de teste", propertyValueLogin.getOldValue());
            
            System.out.println("testGetOldValue - fim");
            
        }catch(BusinessException e)
        {
            System.out.println("testGetOldValue - fim - exception");
            UtilsTest.showMessageList(e.getErrorList());
            Assert.assertTrue(false);
        }
    }

    @Test
    public void testGetPropetyOwner()
    {
    	System.out.println("testGetPropetyOwner - início - testa se não é null");
    	Assert.assertNotNull(propertyValueLogin.getPropetyOwner());
        System.out.println("testGetPropetyOwner - fim");
    }

}
