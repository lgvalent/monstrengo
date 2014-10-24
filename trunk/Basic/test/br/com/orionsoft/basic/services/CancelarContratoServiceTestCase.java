package br.com.orionsoft.basic.services;

import java.util.Calendar;

import org.junit.Assert;
import org.junit.Test;

import br.com.orionsoft.basic.entities.Contrato;
import br.com.orionsoft.basic.entities.pessoa.Fisica;
import br.com.orionsoft.basic.entities.pessoa.Pessoa;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;
import br.com.orionsoft.monstrengo.security.entities.UserSession;


public class CancelarContratoServiceTestCase extends ServiceBasicTest{

	@Test	
	public void testRun(){
		try{
            IEntity<Contrato> contrato = UtilsCrud.create(this.serviceManager, Contrato.class, null);
            IEntity<Fisica> pessoa = UtilsCrud.create(this.serviceManager, Fisica.class, null);
            contrato.getObject().setPessoa(pessoa.getObject());
            UtilsCrud.update(this.serviceManager, pessoa, null);
            UtilsCrud.update(this.serviceManager, contrato, null);
            
            IEntity<ApplicationUser> user = UtilsCrud.list(this.serviceManager, ApplicationUser.class, null).get(0);
            UserSession userSession = new UserSession(user, "localhost");
               
			String descrição = "Cancelado para testes";
			Calendar data = CalendarUtils.getCalendar();
			
			//realiza comparações com o Banco de Dados 
			ServiceData service = new ServiceData(CancelarContratoService.SERVICE_NAME, null);
			service.getArgumentList().setProperty(CancelarContratoService.IN_CONTRATO, contrato);
			service.getArgumentList().setProperty(CancelarContratoService.IN_DATA_CANCELAMENTO, data);
			service.getArgumentList().setProperty(CancelarContratoService.IN_DESCRICAO, descrição);
			service.getArgumentList().setProperty(CancelarContratoService.IN_USER_SESSION, userSession);
			           
			this.serviceManager.execute(service);
			
			UtilsTest.showEntityProperties(contrato);
            		
		} catch (BusinessException e) {

            UtilsTest.showMessageList(e.getErrorList());
            
            Assert.assertTrue(false);
		}
		

	}

}
