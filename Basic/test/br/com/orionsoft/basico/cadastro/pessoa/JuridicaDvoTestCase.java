package br.com.orionsoft.basico.cadastro.pessoa;

import org.junit.Assert;
import org.junit.Test;

import br.com.orionsoft.basic.entities.pessoa.Juridica;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

public class JuridicaDvoTestCase extends ServiceBasicTest {

	@Test
	public void testRun(){
		try{
			IEntity juridica = UtilsCrud.create(this.serviceManager, Juridica.class , null);
			
			juridica.setPropertyValue(Juridica.DOCUMENTO, "30749810000106");
			
			UtilsCrud.update(this.serviceManager, juridica, null);			
			
			
		}
		catch(BusinessException e){
			UtilsTest.showMessageList(e.getErrorList());
			Assert.assertTrue(false);
			
		}
		
	}
		

}
