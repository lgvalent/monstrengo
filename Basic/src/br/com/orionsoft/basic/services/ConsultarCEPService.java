package br.com.orionsoft.basic.services;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;

/**
 * Serviço que busca o endereço pelo CEP
 * 
 * <p>
 * <b>Argumentos:</b><br>
 * IN_CEP: Cep do endereço que se quer pesquisar.
 * <p>
 * <b>Procedimento:</b> 
 * 
 * Retorna o endereço a partir de CEP informado.
 *  
 * @spring.bean id="ConsultarCEPService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 */
public class ConsultarCEPService extends ServiceBasic {
	public static final String SERVICE_NAME = "ConsultarCEPService";
	public static final String IN_CEP = "cep";

	@Override
	public void execute(ServiceData serviceData) throws ServiceException {
		String cep = (String) serviceData.getArgumentList().getProperty(IN_CEP);
		String json = "";
		try {
			URL url = new URL("http://viacep.com.br/ws/" + cep + "/json");
			URLConnection urlConnection = url.openConnection();
			InputStream is = urlConnection.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			StringBuilder jsonSb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				jsonSb.append(line.trim());
			}
			json = jsonSb.toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.warn(e.getMessage());
		}
		serviceData.getOutputData().add(json);
	}

	@Override
	public String getServiceName() {
		return SERVICE_NAME;
	}

}
