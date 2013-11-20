package br.com.orionsoft.basic.ws.site;

import br.com.orionsoft.basic.entities.endereco.Telefone;

public class TelefoneWeb {

    private long id = -1;
    private String ddd;
    private String numero;
    private String ramal;
    private String tipoTelefone;

    public TelefoneWeb() {}
	
	public TelefoneWeb(Telefone tel) {
	    id = tel.getId();
	    ddd = tel.getDdd();
	    numero = tel.getNumero();
	    ramal = tel.getRamal();
	    tipoTelefone = tel.getTipoTelefone().toString();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDdd() {
		return ddd;
	}

	public void setDdd(String ddd) {
		this.ddd = ddd;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getRamal() {
		return ramal;
	}

	public void setRamal(String ramal) {
		this.ramal = ramal;
	}

	public String getTipoTelefone() {
		return tipoTelefone;
	}

	public void setTipoTelefone(String tipoTelefone) {
		this.tipoTelefone = tipoTelefone;
	}

}
