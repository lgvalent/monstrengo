package br.com.orionsoft.monstrengo.crud.entity;

import java.util.List;

import javax.faces.model.SelectItem;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.com.orionsoft.monstrengo.core.test.EntityBasicTest;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;

/**
 * Esta classe realiza testes para a classe Property e IProperty
 * @author Lucio 20111209
 */
public class PropertyTestCase extends EntityBasicTest {
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@Test
	public void testGetValuesList(){
		try {
			IEntity<ApplicationUser> entity = this.entityManager.getEntity(new ApplicationUser());
			List<SelectItem> items = entity.getProperty(ApplicationUser.SECURITY_GROUPS).getValuesList();
			Assert.assertFalse("Deve ter grupos listados para esta propriedade", items.isEmpty());
			showSelectItems(items);
			
			items = entity.getProperty(ApplicationUser.SECURITY_GROUPS).getValuesList("adm");
			Assert.assertFalse(items.isEmpty());
			showSelectItems(items);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
			e.printStackTrace();
		}
	}
	
	private static void showSelectItems(List<SelectItem> items){
		for(SelectItem item: items){
			System.out.println("value:" + item.getValue() +  "->" + item.getLabel());
		}
			
	}

}

