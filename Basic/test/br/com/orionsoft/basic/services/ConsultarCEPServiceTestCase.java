package br.com.orionsoft.basic.services;

import javax.json.Json;
import javax.json.JsonObject;

import org.apache.tools.ant.filters.StringInputStream;
import org.junit.Assert;
import org.junit.Test;

import br.com.orionsoft.basic.Manter;
import br.com.orionsoft.basic.entities.endereco.Bairro;
import br.com.orionsoft.basic.entities.endereco.TipoLogradouro;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;
import br.com.orionsoft.monstrengo.core.util.StringUtils;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;

public class ConsultarCEPServiceTestCase /*extends ServiceBasicTest */{

	@Test
	public void testExecute() {
		String log = "Chácara Papa João XXIII";
		int firstSpace = log.indexOf(" ");
		System.out.println(log.substring(0,firstSpace));
		TipoLogradouro.valueOf(StringUtils.removeAccent(log.substring(0,firstSpace).toUpperCase()));
		System.out.println(TipoLogradouro.valueOf(StringUtils.removeAccent(log.substring(0,firstSpace).toUpperCase())));
		System.out.println(log.substring(firstSpace+1));
		
		if(true)return;

//		try {
//			String cep = "87010260";
//			ServiceData service = new ServiceData(ConsultarCEPService.SERVICE_NAME, null);
//			service.getArgumentList().setProperty(ConsultarCEPService.IN_CEP, cep);;
//			this.serviceManager.execute(service);
//			
//			String result = service.getFirstOutput();
//			System.out.println(result);
//			JsonObject json = Json.createReader(new StringInputStream(result)).readObject();
//			System.out.println(json);
//		} catch (BusinessException e) {
//            UtilsTest.showMessageList(e.getErrorList());
//            Assert.assertTrue(false);
//		}
	}

}
