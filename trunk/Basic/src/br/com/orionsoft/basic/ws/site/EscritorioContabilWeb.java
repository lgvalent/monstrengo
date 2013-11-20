package br.com.orionsoft.basic.ws.site;

import java.util.Calendar;

import br.com.orionsoft.basic.entities.pessoa.EscritorioContabil;

public class EscritorioContabilWeb {

    private long id = -1;
    private PessoaWeb pessoa;
    private Calendar dataCadastro;
    
	public EscritorioContabilWeb() {}
	
	public EscritorioContabilWeb(EscritorioContabil escritorioContabil) {
	    id = escritorioContabil.getId();
	    pessoa = new PessoaWeb(escritorioContabil.getPessoa());
	    dataCadastro = escritorioContabil.getDataCadastro();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public PessoaWeb getPessoa() {
		return pessoa;
	}

	public void setPessoa(PessoaWeb juridica) {
		this.pessoa = juridica;
	}

	public Calendar getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Calendar dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

}
