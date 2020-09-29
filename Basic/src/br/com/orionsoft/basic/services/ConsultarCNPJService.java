package br.com.orionsoft.basic.services;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.tools.ant.filters.StringInputStream;

import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.core.util.StringUtils;
import br.com.orionsoft.util.CalendarUtils;

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
			URL url = new URL("http://www.receitaws.com.br/v1/cnpj/" + cnpj);
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

			serviceData.getOutputData().add( new ConsultarCNPJBean(json));
		} catch (Exception e) {
			e.printStackTrace();
			log.warn(e.getMessage());
			throw new ServiceException(MessageList.createSingleInternalError(new Exception("Não foi possível recuperar os dados do CNPJ, a Receita Federal não permite várias consultas simultâneas, aguarde 5 segundos e tente novamente.")));
		}
	}

	@Override
	public String getServiceName() {
		return SERVICE_NAME;
	}

	public static class ConsultarCNPJBean{
		private String message;

		private Calendar dataSituacao;
		private String nome;
		private String uf;
		private String email;
		private String situacao;
		private String bairro;
		private String logradouro;
		private Integer numero = 0;
		private String cep;
		private String municipio;
		private String porte;
		private Calendar abertura;
		private String naturezaJuridica;
		private String fantasia;
		private Calendar ultimaAtualizacao;
		private String tipo;
		private String complemento;
		private String efr;
		private String motivoSituacao;
		private String situacaoEspecial;
		private BigDecimal capitalSocial;
		private List<String> telefones;
		private List<Atividade> atividadePrincipal;
		private List<Atividade> atividadesSecundarias;
		private List<QuadroSocietario> quandroSocietario;

		public ConsultarCNPJBean(String json) {
			JsonObject jsonObj = Json.createReader(new StringInputStream(json, "ISO-8859-1")).readObject();

			if (jsonObj.getString("status").equals("OK")) {
				nome = StringUtils.capitalize(jsonObj.getString("nome"));
				uf = jsonObj.getString("uf");
				email = jsonObj.getString("email");
				situacao = jsonObj.getString("situacao");
				bairro = StringUtils.capitalize(jsonObj.getString("bairro"));
				logradouro = StringUtils.capitalize(jsonObj.getString("logradouro"));
				complemento = StringUtils.capitalize(jsonObj.getString("complemento"));
				if(!jsonObj.getString("numero").isEmpty()){
					String numeroStr = StringUtils.removeNonDigit(jsonObj.getString("numero"));
					if(NumberUtils.isNumber(numeroStr))
						numero = Integer.parseInt(numeroStr);
					else{
						numero = numeroStr.isEmpty()?0:Integer.parseInt(numeroStr);
						complemento += " " +StringUtils.capitalize(jsonObj.getString("numero"));
					}
				}
				cep = StringUtils.removeNonDigit(jsonObj.getString("cep"));
				municipio = StringUtils.capitalize(jsonObj.getString("municipio"));
				porte = jsonObj.getString("porte");
				naturezaJuridica = jsonObj.getString("natureza_juridica");
				fantasia = StringUtils.capitalize(jsonObj.getString("fantasia"));
				tipo = jsonObj.getString("tipo");
				efr = jsonObj.getString("efr");
				motivoSituacao = jsonObj.getString("motivo_situacao");
				situacaoEspecial = jsonObj.getString("situacao_especial");
				capitalSocial = new BigDecimal(jsonObj.getString("capital_social"));

				dataSituacao = CalendarUtils.getCalendar(
						Integer.parseInt(jsonObj.getString("data_situacao").substring(6,10)),
						Integer.parseInt(jsonObj.getString("data_situacao").substring(3, 5)) - 1, 
						Integer.parseInt(jsonObj.getString("data_situacao").substring(0, 2))
						);
				abertura = CalendarUtils.getCalendar(
						Integer.parseInt(jsonObj.getString("abertura").substring(6,10)),
						Integer.parseInt(jsonObj.getString("abertura").substring(3, 5)) - 1, 
						Integer.parseInt(jsonObj.getString("abertura").substring(0, 2))
						);
				ultimaAtualizacao = CalendarUtils.getCalendar(
						Integer.parseInt(jsonObj.getString("ultima_atualizacao").substring(0, 4)),
						Integer.parseInt(jsonObj.getString("ultima_atualizacao").substring(5, 7)) - 1, 
						Integer.parseInt(jsonObj.getString("ultima_atualizacao").substring(8,10))
						);

				quandroSocietario = new ArrayList();
				JsonArray ja = jsonObj.getJsonArray("qsa");
				for (int i = 0; i < ja.size(); i++) {
					JsonObject jo = ja.getJsonObject(i);
					QuadroSocietario item = new QuadroSocietario();
					item.qualificacaoCodigo = jo.getString("qual").substring(0, 2);
					item.qualificacaoNome = jo.getString("qual").substring(3, jo.getString("qual").length());
					item.nome = StringUtils.capitalize(jo.getString("nome"));
					quandroSocietario.add(item);
				}

				atividadePrincipal = new ArrayList<>();
				ja = jsonObj.getJsonArray("atividade_principal");
				for (int i = 0; i < ja.size(); i++) {
					JsonObject jo = ja.getJsonObject(i);
					Atividade item = new Atividade();
					item.codigo = StringUtils.removeNonDigit(jo.getString("code"));
					item.texto = jo.getString("text");
					atividadePrincipal.add(item);
				}

				atividadesSecundarias = new ArrayList<>();
				ja = jsonObj.getJsonArray("atividades_secundarias");
				for (int i = 0; i < ja.size(); i++) {
					JsonObject jo = ja.getJsonObject(i);
					Atividade item = new Atividade();
					item.codigo = StringUtils.removeNonDigit(jo.getString("code"));
					item.texto = jo.getString("text");
					atividadesSecundarias.add(item);
				}
				
				telefones = new ArrayList<>();
				if(!jsonObj.getString("telefone").isEmpty())
					for(String fone: jsonObj.getString("telefone").split(" / ")){
						telefones.add(fone);
					}
			} else {
				message = jsonObj.getString("message");
			}

		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public Calendar getDataSituacao() {
			return dataSituacao;
		}

		public void setDataSituacao(Calendar data_situacao) {
			this.dataSituacao = data_situacao;
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

		public List<String> getTelefones() {
			return telefones;
		}

		public void setTelefone(List<String> telefones) {
			this.telefones = telefones;
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

		public Integer getNumero() {
			return numero;
		}

		public void setNumero(Integer numero) {
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

		public String getNaturezaJuridica() {
			return naturezaJuridica;
		}

		public void setNaturezaJuridica(String natureza_juridica) {
			this.naturezaJuridica = natureza_juridica;
		}

		public String getFantasia() {
			return fantasia;
		}

		public void setFantasia(String fantasia) {
			this.fantasia = fantasia;
		}

		public Calendar getUltimaAtualizacao() {
			return ultimaAtualizacao;
		}

		public void setUltimaAtualizacao(Calendar ultima_atualizacao) {
			this.ultimaAtualizacao = ultima_atualizacao;
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

		public String getMotivoSituacao() {
			return motivoSituacao;
		}

		public void setMotivoSituacao(String motivo_situacao) {
			this.motivoSituacao = motivo_situacao;
		}

		public String getSituacaoEspecial() {
			return situacaoEspecial;
		}

		public void setSituacaoEspecial(String situacao_especial) {
			this.situacaoEspecial = situacao_especial;
		}

		public BigDecimal getCapitalSocial() {
			return capitalSocial;
		}

		public void setCapitalSocial(BigDecimal capital_social) {
			this.capitalSocial = capital_social;
		}

		public List<Atividade> getAtividadePrincipal() {
			return atividadePrincipal;
		}

		public void setAtividadePrincipal(List<Atividade> atividade_principal) {
			this.atividadePrincipal = atividade_principal;
		}

		public List<Atividade> getAtividadesSecundarias() {
			return atividadesSecundarias;
		}

		public void setAtividadesSecundarias(List<Atividade> atividades_secundarias) {
			this.atividadesSecundarias = atividades_secundarias;
		}

		public List<QuadroSocietario> getQuadroSocietario() {
			return quandroSocietario;
		}

		public void setQuadroSocietario(List<QuadroSocietario> quadro_societario) {
			this.quandroSocietario = quadro_societario;
		}

		public class QuadroSocietario {
			private String qualificacaoCodigo;
			private String qualificacaoNome;
			private String nome;

			public String getQualificacaoCodigo() {
				return qualificacaoCodigo;
			}

			public void setQualificacaoCodigo(String qualificacaoCodigo) {
				this.qualificacaoCodigo = qualificacaoCodigo;
			}

			public String getQualificacaoNome() {
				return qualificacaoNome;
			}

			public void setQualificacaoNome(String qualificacaoNome) {
				this.qualificacaoNome = qualificacaoNome;
			}

			public String getNome() {
				return nome;
			}

			public void setNome(String nome) {
				this.nome = nome;
			}
		}

		public class Atividade {
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
	
	public static void main(String[] args) {
		System.out.println(NumberUtils.isNumber(""));
		System.out.println(11 - StringUtils.removeNonDigit("(011) 5582-1333").length());
	}

}