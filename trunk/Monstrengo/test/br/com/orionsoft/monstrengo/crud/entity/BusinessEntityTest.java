package br.com.orionsoft.monstrengo.crud.entity;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;

public class BusinessEntityTest extends ServiceBasicTest {

	@Test
	public void testSetId() throws BusinessException {
		IEntity entity = UtilsCrud.retrieve(this.serviceManager, ApplicationUser.class, 1l, null);
		assertEquals("admin", entity.getPropertyValue(ApplicationUser.LOGIN));
		
		entity.setId(2l);
		assertEquals("user", entity.getPropertyValue(ApplicationUser.LOGIN));
		
		UtilsTest.showEntityProperties(entity);
		
		System.out.println(entity.getProperty(ApplicationUser.SECURITY_GROUPS).getInfo().getType());
		
	}

}
