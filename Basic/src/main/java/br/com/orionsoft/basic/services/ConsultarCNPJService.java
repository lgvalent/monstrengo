package br.com.orionsoft.basic.services;

import java.io.InputStream;
import java.net.URL;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;

/**
 * Serviço que busca os dados da empresa pelo CNPJ
 * 
 * <p>
 * <b>Argumentos:</b><br>
 * IN_CNPJ: CNPJ da empresa que se quer pesquisar.
 * <p>
 * <b>Procedimento:</b> 
 * 
 * Retorna os dados a partir de CNPJ informado.
 *  
 * @spring.bean id="ConsultarCNPJService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 */
public class ConsultarCNPJService extends ServiceBasic {
	public static final String SERVICE_NAME = "ConsultarCNPJService";
	public static final String IN_CNPJ = "cnpj";

	@Override
	public void execute(ServiceData serviceData) throws ServiceException {
		String cnpj = (String) serviceData.getArgumentList().getProperty(IN_CNPJ);
		String json = "";
		try {
			URL url = new URL("https://www.receitaws.com.br/v1/cnpj/" + cnpj);
			try (InputStream is = url.openStream();
				JsonReader rdr = Json.createReader(is)) {
				JsonObject obj = rdr.readObject();
				json = obj.toString();
			}
		} catch (Exception e) {
			log.warn(e.getMessage());
		}
		serviceData.getOutputData().add(json);
	}

	@Override
	public String getServiceName() {
		return SERVICE_NAME;
	}

}
