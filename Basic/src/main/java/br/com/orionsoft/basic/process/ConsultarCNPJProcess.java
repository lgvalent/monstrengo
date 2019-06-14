package br.com.orionsoft.basic.process;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.json.JsonArray;
import javax.json.JsonObject;

import br.com.orionsoft.basic.services.ConsultarCNPJService;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.process.ProcessBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;

/**
 * Processo de consultar CNPJ</p>
 *
 * @author Antonio Alves
 * @since 20190606
 *
 * @spring.bean id="ConsultarCNPJProcess" destroy-method="finish" singleton="false"
 * @spring.property name="processManager" ref="ProcessManager"
 *
 */
public class ConsultarCNPJProcess extends ProcessBasic {
	public static final String PROCESS_NAME = "ConsultarCNPJProcess";
	
	private String cnpj;
	private String message;
	
    private Calendar data_situacao;
    private String nome;
    private String uf;
    private String telefone;
    private String email;
    private String situacao;
    private String bairro;
    private String logradouro;
    private String numero;
    private String cep;
    private String municipio;
    private String porte;
    private Calendar abertura;
    private String natureza_juridica;
    private String fantasia;
    private Calendar ultima_atualizacao;
    private String tipo;
    private String complemento;
    private String efr;
    private String motivo_situacao;
    private String situacao_especial;
//    private Calendar data_situacao_especial;
    private BigDecimal capital_social;
    private List<Atividade> atividade_principal;
    private List<Atividade> atividades_secundarias;
    private List<QuadroSocietario> quandro_societario;

    public void consultarEmpresaPorCNPJ() {
		try {
			ServiceData service = new ServiceData(ConsultarCNPJService.SERVICE_NAME, null);
			service.getArgumentList().setProperty(ConsultarCNPJService.IN_CNPJ, cnpj);
			this.getProcessManager().getServiceManager().execute(service);
			JsonObject obj = (JsonObject) service.getOutputData().get(0);
			if (obj.getString("status").equals("OK")) {
                nome = obj.getString("nome").replaceAll("[^\\p{ASCII}]", "");
                uf = obj.getString("uf");
                telefone = obj.getString("telefone");
                email = obj.getString("email");
                situacao = obj.getString("situacao");
                bairro = obj.getString("bairro").replaceAll("[^\\p{ASCII}]", "");
                logradouro = obj.getString("logradouro").replaceAll("[^\\p{ASCII}]", "");
                numero = obj.getString("numero");
                cep = obj.getString("cep");
                municipio = obj.getString("municipio").replaceAll("[^\\p{ASCII}]", "");
                porte = obj.getString("porte");
                natureza_juridica = obj.getString("natureza_juridica").replaceAll("[^\\p{ASCII}]", "");
                fantasia = obj.getString("fantasia").replaceAll("[^\\p{ASCII}]", "");
                tipo = obj.getString("tipo");
                complemento = obj.getString("complemento").replaceAll("[^\\p{ASCII}]", "");
                efr = obj.getString("efr");
                motivo_situacao = obj.getString("motivo_situacao").replaceAll("[^\\p{ASCII}]", "");
                situacao_especial = obj.getString("situacao_especial");
                capital_social = new BigDecimal(obj.getString("capital_social"));

                data_situacao = new Calendar.Builder().setDate(
                        Integer.parseInt(obj.getString("data_situacao").substring(6,10)),
                        Integer.parseInt(obj.getString("data_situacao").substring(3, 5)) - 1, 
                        Integer.parseInt(obj.getString("data_situacao").substring(0, 2))
                ).setTimeOfDay(0, 0, 0).build();
                abertura = new Calendar.Builder().setDate(
                        Integer.parseInt(obj.getString("abertura").substring(6,10)),
                        Integer.parseInt(obj.getString("abertura").substring(3, 5)) - 1, 
                        Integer.parseInt(obj.getString("abertura").substring(0, 2))
                ).setTimeOfDay(0, 0, 0).build();
                ultima_atualizacao = new Calendar.Builder().setDate(
                        Integer.parseInt(obj.getString("ultima_atualizacao").substring(0, 4)),
                        Integer.parseInt(obj.getString("ultima_atualizacao").substring(5, 7)) - 1, 
                        Integer.parseInt(obj.getString("ultima_atualizacao").substring(8,10))
                ).setTimeOfDay(0, 0, 0).build();

                quandro_societario = new ArrayList<>();
                JsonArray ja = obj.getJsonArray("qsa");
                for (int i = 0; i < ja.size(); i++) {
                    JsonObject jo = ja.get(i).asJsonObject();
                    QuadroSocietario item = new QuadroSocietario();
                    item.qualificacao = jo.getString("qual").replaceAll("[^\\p{ASCII}]", "");
                    item.nome = jo.getString("nome").replaceAll("[^\\p{ASCII}]", "");
                    quandro_societario.add(item);
                }
                
                atividade_principal = new ArrayList<>();
                ja = obj.getJsonArray("atividade_principal");
                for (int i = 0; i < ja.size(); i++) {
                    JsonObject jo = ja.get(i).asJsonObject();
                    Atividade item = new Atividade();
                    item.codigo = jo.getString("code");
                    item.texto = jo.getString("text").replaceAll("[^\\p{ASCII}]", "");
                    atividade_principal.add(item);
                }

                atividades_secundarias = new ArrayList<>();
                ja = obj.getJsonArray("atividades_secundarias");
                for (int i = 0; i < ja.size(); i++) {
                    JsonObject jo = ja.get(i).asJsonObject();
                    Atividade item = new Atividade();
                    item.codigo = jo.getString("code");
                    item.texto = jo.getString("text").replaceAll("[^\\p{ASCII}]", "");
                    atividades_secundarias.add(item);
                }
			} else {
				message = obj.getString("message").replaceAll("[^\\p{ASCII}]", "");
			}
			
		} catch (BusinessException e) {
			this.getMessageList().addAll(e.getErrorList());
		}
	}

	@Override
	public String getProcessName() {
		return PROCESS_NAME;
	}

	public String getCnpj() {
		return cnpj;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Calendar getData_situacao() {
		return data_situacao;
	}

	public void setData_situacao(Calendar data_situacao) {
		this.data_situacao = data_situacao;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSituacao() {
		return situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getLogradouro() {
		return logradouro;
	}

	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public String getMunicipio() {
		return municipio;
	}

	public void setMunicipio(String municipio) {
		this.municipio = municipio;
	}

	public String getPorte() {
		return porte;
	}

	public void setPorte(String porte) {
		this.porte = porte;
	}

	public Calendar getAbertura() {
		return abertura;
	}

	public void setAbertura(Calendar abertura) {
		this.abertura = abertura;
	}

	public String getNatureza_juridica() {
		return natureza_juridica;
	}

	public void setNatureza_juridica(String natureza_juridica) {
		this.natureza_juridica = natureza_juridica;
	}

	public String getFantasia() {
		return fantasia;
	}

	public void setFantasia(String fantasia) {
		this.fantasia = fantasia;
	}

	public Calendar getUltima_atualizacao() {
		return ultima_atualizacao;
	}

	public void setUltima_atualizacao(Calendar ultima_atualizacao) {
		this.ultima_atualizacao = ultima_atualizacao;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	public String getEfr() {
		return efr;
	}

	public void setEfr(String efr) {
		this.efr = efr;
	}

	public String getMotivo_situacao() {
		return motivo_situacao;
	}

	public void setMotivo_situacao(String motivo_situacao) {
		this.motivo_situacao = motivo_situacao;
	}

	public String getSituacao_especial() {
		return situacao_especial;
	}

	public void setSituacao_especial(String situacao_especial) {
		this.situacao_especial = situacao_especial;
	}

//	public Calendar getData_situacao_especial() {
//		return data_situacao_especial;
//	}

//	public void setData_situacao_especial(Calendar data_situacao_especial) {
//		this.data_situacao_especial = data_situacao_especial;
//	}

	public BigDecimal getCapital_social() {
		return capital_social;
	}

	public void setCapital_social(BigDecimal capital_social) {
		this.capital_social = capital_social;
	}

	public List<Atividade> getAtividade_principal() {
		return atividade_principal;
	}

	public void setAtividade_principal(List<Atividade> atividade_principal) {
		this.atividade_principal = atividade_principal;
	}

	public List<Atividade> getAtividades_secundarias() {
		return atividades_secundarias;
	}

	public void setAtividades_secundarias(List<Atividade> atividades_secundarias) {
		this.atividades_secundarias = atividades_secundarias;
	}

	public List<QuadroSocietario> getQuadro_societario() {
		return quandro_societario;
	}

	public void setQuadro_societario(List<QuadroSocietario> quadro_societario) {
		this.quandro_societario = quadro_societario;
	}
	
    class QuadroSocietario {
        private String qualificacao;
        private String nome;

        public String getQualificacao() {
            return qualificacao;
        }

        public void setQualificacao(String qualificacao) {
            this.qualificacao = qualificacao;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }
    }

    class Atividade {
        private String texto;
        private String codigo;

        public String getTexto() {
            return texto;
        }

        public void setTexto(String texto) {
            this.texto = texto;
        }

        public String getCodigo() {
            return codigo;
        }

        public void setCodigo(String codigo) {
            this.codigo = codigo;
        }
    }
}
