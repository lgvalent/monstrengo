package br.com.orionsoft.basic.services;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;

import javax.json.Json;
import javax.json.JsonObject;

import org.apache.tools.ant.filters.StringInputStream;

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
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			StringBuilder jsonSb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				jsonSb.append(line.trim());
			}
			json = jsonSb.toString();
			json = new String(json.getBytes("UTF-8"), "ISO-8859-1");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.warn(e.getMessage());
		}
		serviceData.getOutputData().add(new ConsultarCepBean(json));
	}

	@Override
	public String getServiceName() {
		return SERVICE_NAME;
	}
	
	public static class ConsultarCepBean{
		private String cep;
		private String logradouro;
		private String complemento;
		private String bairro;
		private String localidade;
		private String uf;
		private String ibge;
		private String gia;
		
		public ConsultarCepBean(String json) {
				JsonObject endereco = Json.createReader(new StringInputStream(json, "ISO-8859-1")).readObject();
				logradouro = endereco.getString("logradouro");
				complemento = endereco.getString("complemento");
				bairro = endereco.getString("bairro");
				localidade = endereco.getString("localidade");
				uf = endereco.getString("uf");
				ibge = endereco.getString("ibge");
				gia = endereco.getString("gia");
		}

		public String getCep() {
			return cep;
		}

		public void setCep(String cep) {
			this.cep = cep;
		}

		public String getLogradouro() {
			return logradouro;
		}

		public void setLogradouro(String logradouro) {
			this.logradouro = logradouro;
		}

		public String getComplemento() {
			return complemento;
		}

		public void setComplemento(String complemento) {
			this.complemento = complemento;
		}

		public String getBairro() {
			return bairro;
		}

		public void setBairro(String bairro) {
			this.bairro = bairro;
		}

		public String getLocalidade() {
			return localidade;
		}

		public void setLocalidade(String localidade) {
			this.localidade = localidade;
		}

		public String getUf() {
			return uf;
		}

		public void setUf(String uf) {
			this.uf = uf;
		}

		public String getIbge() {
			return ibge;
		}

		public void setIbge(String ibge) {
			this.ibge = ibge;
		}

		public String getGia() {
			return gia;
		}

		public void setGia(String gia) {
			this.gia = gia;
		}
		
		
	}
	
	public static void main(String[] args) throws Exception {
		String json = "";
		String cep = "87301000";
		URL url = new URL("http://viacep.com.br/ws/" + cep + "/json");
		URLConnection urlConnection = url.openConnection();
		InputStream is = urlConnection.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		StringBuilder jsonSb = new StringBuilder();
		String line;
		while ((line = br.readLine()) != null) {
			jsonSb.append(line.trim());
		}
		json = jsonSb.toString();
		byte[] latin1 = "existência".getBytes();
		byte[] utf8 = new String(latin1, "ISO-8859-1").getBytes("ISO-8859-1");
		System.out.println(new String(utf8));
		System.out.println(json);
		System.out.println(new String(json.getBytes("UTF-8"), "ISO-8859-1"));
	}
}
