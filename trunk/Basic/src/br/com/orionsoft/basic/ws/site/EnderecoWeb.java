package br.com.orionsoft.basic.ws.site;

import br.com.orionsoft.basic.entities.endereco.Endereco;

public class EnderecoWeb {

	private long id = -1;
    private Integer numero;
    private String complemento;
    private String caixaPostal;

    private String tipoLogradouro;
    private String nomeLogradouro;
    private String bairro;
    private String cep;
    private String municipio;
    private String uf;

	public EnderecoWeb() {}
	
    public EnderecoWeb(Endereco endereco, String Categoria) {
    	id = endereco.getId();
        numero = endereco.getNumero();
        complemento = endereco.getComplemento();
        caixaPostal = endereco.getCaixaPostal();

        tipoLogradouro = endereco.getLogradouro().getTipoLogradouro().toString();
        nomeLogradouro = endereco.getLogradouro().getNome();
        bairro = endereco.getBairro() == null?"":endereco.getBairro().toString();
        cep = endereco.getCep();
        municipio = endereco.getMunicipio().getNome();
        uf = endereco.getMunicipio().getUf().toString();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	public String getCaixaPostal() {
		return caixaPostal;
	}

	public void setCaixaPostal(String caixaPostal) {
		this.caixaPostal = caixaPostal;
	}

	public String getTipoLogradouro() {
		return tipoLogradouro;
	}

	public void setTipoLogradouro(String tipoLogradouro) {
		this.tipoLogradouro = tipoLogradouro;
	}

	public String getNomeLogradouro() {
		return nomeLogradouro;
	}

	public void setNomeLogradouro(String nomeLogradouro) {
		this.nomeLogradouro = nomeLogradouro;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
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

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

}
