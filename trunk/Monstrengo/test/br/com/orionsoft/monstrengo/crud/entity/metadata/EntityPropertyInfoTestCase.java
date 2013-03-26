package br.com.orionsoft.monstrengo.crud.entity.metadata;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.test.EntityBasicTest;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IProperty;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IEntityMetadata;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IGroupMetadata;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IPropertyMetadata;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;
import br.com.orionsoft.monstrengo.security.entities.SecurityGroup;

/**
 * Esta classe realiza os testes sobre as informações das 
 * propriedades de uma entidade
 *
 */
public class EntityPropertyInfoTestCase extends EntityBasicTest
{
	public static final String PROPERTY_NAME = "login";
	public static final Class PROPERTY_TYPE = String.class;
	public static final int PROPERTY_SIZE = 20;
	protected IPropertyMetadata propertyInfo;
	protected IProperty property;
	protected IEntity entity;
	
	
	public static void main(String[] args)
	{
		junit.textui.TestRunner.run(EntityPropertyInfoTestCase.class);
	}

	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		
		ApplicationUser user = new ApplicationUser();
		user.setId(-1);
		user.setLogin("Login de teste");
		user.setPassword("teste");
		entity = entityManager.getEntity(user);
		property = entity.getProperty(PROPERTY_NAME);
		propertyInfo = property.getInfo();
		
	}
	
	@Test
	public void testPropertyExecute()
	{
		try {
			Assert.assertTrue(entity.getInfo().getCanRetrieve());
			Assert.assertTrue(propertyInfo.isRequired());
			Assert.assertEquals(999999.0, propertyInfo.getMaximum());
			Assert.assertEquals(0.0, propertyInfo.getMinimum());
			Assert.assertFalse(propertyInfo.isReadOnly());
			Assert.assertEquals("", propertyInfo.getEditMask());
			Assert.assertFalse(propertyInfo.isEditShowList());
			Assert.assertFalse(propertyInfo.isCalendar());
			Assert.assertFalse(propertyInfo.isDouble());
			Assert.assertFalse(propertyInfo.isLong());
			Assert.assertTrue(propertyInfo.isString());
			Assert.assertFalse(propertyInfo.isList());
			Assert.assertFalse(propertyInfo.isBoolean());
			Assert.assertFalse(propertyInfo.isEntity());
			Assert.assertEquals(PROPERTY_NAME,propertyInfo.getName());
			Assert.assertNotNull(propertyInfo.getHint());
			Assert.assertTrue(propertyInfo.getType().equals(PROPERTY_TYPE));
			Assert.assertEquals(PROPERTY_SIZE, propertyInfo.getSize());
			Assert.assertNotNull(propertyInfo.getLabel());
			Assert.assertTrue(propertyInfo.isVisible());
			Assert.assertNotNull(propertyInfo.getColorName());
			Assert.assertEquals("", propertyInfo.getDisplayFormat());
			Assert.assertEquals("O login é um nome curto usado para junto com a senha para realizar a autenticação no sistema", propertyInfo.getDescription());
			Assert.assertEquals(0, propertyInfo.getGroup());
			Assert.assertTrue(this.entity.getInfo().getCanCreate());
			Assert.assertTrue(this.entity.getInfo().getCanRetrieve());
			Assert.assertTrue(this.entity.getInfo().getCanUpdate());
			Assert.assertTrue(this.entity.getInfo().getCanDelete());
			
			UtilsTest.showEntityProperties(entity);
			
			System.out.println("Entity" + this.entity.getInfo().getLabel());
			
			
			IEntityMetadata info = 	entityManager.getEntityMetadata(SecurityGroup.class);
;
			for(IGroupMetadata group: info.getGroups()){
				System.out.println("::Grupo " + group.getIndex() + "-" + group.getName());
				
				for(IPropertyMetadata prop: group.getProperties()){
					System.out.println("::Prop " + prop.getIndex() + "-" + prop.getLabel());
				}
			}
			
			

		} catch (BusinessException e) {
			UtilsTest.showMessageList(e.getErrorList());
			Assert.assertTrue(false);
		}
	}
	
	@Test
	public void testGroupsExecute()
	{
		ApplicationUser oEntity = new ApplicationUser();
		oEntity.setId(-1);
		oEntity.setName("Entidade de teste");
		

		try {
			IEntity entity = entityManager.getEntity(oEntity);
//			IEntityMetadata entityMetadata = entity.getInfo();
			IEntityMetadata entityMetadata = entityManager.getEntityMetadataDefaults(entity.getInfo().getType());

			for(IGroupMetadata groupM: entityMetadata.getGroups()){
				System.out.println("::Grupo " + groupM.getIndex() + "-" + groupM.getName());
				
				for(IPropertyMetadata prop: groupM.getProperties()){
					System.out.println("::Prop " + prop.getIndex() + "-" + prop.getLabel() + " G:" +prop.getGroup());
				}
			}
			for(IGroupMetadata groupMetadata: entity.getInfo().getGroups())
				System.out.println(groupMetadata.getName() + "---" + groupMetadata.getProperties());
			
			
			UtilsTest.showEntityProperties(entity);

		} catch (BusinessException e) {
			UtilsTest.showMessageList(e.getErrorList());
			Assert.assertTrue(false);
		}
	}
}
