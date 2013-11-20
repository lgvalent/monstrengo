package br.com.orionsoft.basic.services;

import org.apache.commons.lang.StringUtils;

import br.com.orionsoft.basic.entities.endereco.Endereco;
import br.com.orionsoft.basic.entities.endereco.Municipio;
import br.com.orionsoft.basic.entities.endereco.Uf;
import br.com.orionsoft.basic.entities.pessoa.Pessoa;
import br.com.orionsoft.monstrengo.core.service.ServiceResultBean;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;

/**
 * Classe que descreve as propriedades de uma pessoa
 * 
 * @author andre
 * 
 */
public class ListarPessoaQuery extends ServiceResultBean {
	public static final String QUERY_SELECT = "new " + ListarPessoaQuery.class.getName() + 
		"(entity.id" + 
		",entity." + Pessoa.NOME +
		",entity." + Pessoa.DOCUMENTO +
		",entity." + Pessoa.ENDERECO_CORRESPONDENCIA + "." + Endereco.MUNICIPIO + "." + Municipio.NOME +
		",entity." + Pessoa.ENDERECO_CORRESPONDENCIA + "." + Endereco.MUNICIPIO + "." + Municipio.UF +
		")"
		;

	public static final String QUERY_ORDER_NOME = "entity."	+ Pessoa.NOME;
	
	private Long id;
	private String nome;
	private String documento;
	private String enderecoMunicipio;
	private String enderecoUf;

	public ListarPessoaQuery(){
		super();
	}
	
	public ListarPessoaQuery(Long id, String nome, String documento,
			String enderecoMunicipio, Uf enderecoUf) {
		super();
		this.id = id;
		this.nome = nome;
		this.documento = documento;
		this.enderecoMunicipio = enderecoMunicipio;
		this.enderecoUf = enderecoUf.toString();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDocumento() {
		return documento;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}

	public String getEnderecoMunicipio() {
		return enderecoMunicipio;
	}

	public void setEnderecoMunicipio(String enderecoMunicipio) {
		this.enderecoMunicipio = enderecoMunicipio;
	}

	public String getEnderecoUf() {
		return enderecoUf;
	}

	public void setEnderecoUf(String enderecoUf) {
		this.enderecoUf = enderecoUf;
	}

	public String toString() {
		String result = "";
		if (this.id != null && this.id != IDAO.ENTITY_UNSAVED){
			result += String.valueOf(this.id);
		}
		if (StringUtils.isNotBlank(this.nome)) {
			result += "-" + this.nome;
		}

		return result;
	}

}
