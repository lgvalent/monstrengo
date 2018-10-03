package br.com.orionsoft.basic.process;

import javax.json.JsonObject;

import br.com.orionsoft.basic.services.ConsultarCEPService;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.process.ProcessBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;

/**
 * Processo de consultar CEP</p>
 *
 * @author Antonio Alves
 * @since 20181003
 *
 * @spring.bean id="ConsultarCEPProcess" destroy-method="finish" singleton="false"
 * @spring.property name="processManager" ref="ProcessManager"
 *
 */
public class ConsultarCEPProcess extends ProcessBasic {
	public static final String PROCESS_NAME = "ConsultarCEPProcess";
	
	private String cep;
	private String logradouro;
	private String complemento;
	private String bairro;
	private String localidade;
	private String uf;
	private String unidade;
	private String ibge;
	private String gia;
	
	public void consultarEnderecoPorCep() {
		try {
			ServiceData service = new ServiceData(ConsultarCEPService.SERVICE_NAME, null);
			service.getArgumentList().setProperty(ConsultarCEPService.IN_CEP, cep);
			this.getProcessManager().getServiceManager().execute(service);
			
			JsonObject endereco = (JsonObject) service.getOutputData().get(0);
			logradouro = endereco.getString("logradouro");
			complemento = endereco.getString("complemento");
			bairro = endereco.getString("bairro");
			localidade = endereco.getString("localidade");
			uf = endereco.getString("uf");
			unidade = endereco.getString("unidade");
			ibge = endereco.getString("ibge");
			gia = endereco.getString("gia");
			
		} catch (BusinessException e) {
			this.getMessageList().addAll(e.getErrorList());
		}
	}

	@Override
	public String getProcessName() {
		return PROCESS_NAME;
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

	public String getUnidade() {
		return unidade;
	}

	public void setUnidade(String unidade) {
		this.unidade = unidade;
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
