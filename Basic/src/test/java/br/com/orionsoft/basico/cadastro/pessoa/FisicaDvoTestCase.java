package br.com.orionsoft.basico.cadastro.pessoa;

import org.junit.Assert;
import org.junit.Test;

import br.com.orionsoft.basic.entities.pessoa.Fisica;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.dvo.DvoException;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

public class FisicaDvoTestCase extends ServiceBasicTest {

	@Test
	public void testRun(){
         
    	try{
            IEntity fisica = UtilsCrud.create(this.serviceManager, Fisica.class, null);
            
            fisica.setPropertyValue(Fisica.NOME, "lucio valentin da silva sant'ana");
            fisica.setPropertyValue(Fisica.NOME, "lucio valentin da silva sant'ana");
            fisica.setPropertyValue(Fisica.DOCUMENTO , "41322401934");
            fisica.setPropertyValue(Fisica.DATA_INICIAL,CalendarUtils.getCalendar(2005,06,01));

            UtilsCrud.update(this.serviceManager, fisica, null);
            
            UtilsTest.showEntityProperties(fisica);
            
            UtilsCrud.delete(this.serviceManager, fisica, null);
            
        }
        catch(DvoException e){
        	UtilsTest.showMessageList(e.getErrorList());
        	Assert.assertTrue(false);
        	
        }
    	
    	catch (BusinessException e){
            UtilsTest.showMessageList(e.getErrorList());
            
            Assert.assertTrue(false);
        }
    }
}