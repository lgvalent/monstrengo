package br.com.orionsoft.basic.ws.site;

import java.util.Calendar;

import br.com.orionsoft.basic.entities.Contrato;
import br.com.orionsoft.basic.entities.endereco.Endereco;
import br.com.orionsoft.basic.entities.endereco.Telefone;
import br.com.orionsoft.basic.entities.pessoa.Pessoa;

public class PessoaWeb {
	
    private long id = -1;
    private int erroValidacao = 0;
	private String nome;
    private EnderecoWeb[] enderecos;
    private EnderecoWeb enderecoCorrespondencia;
    private TelefoneWeb[] telefones;
    private String email;
    private Calendar dataCadastro;
    private String www;
    private String documento;
    private String apelido;
    private Calendar dataInicial;
    private Calendar dataFinal;
    
    private ContratoWeb[] contratos;
    
	public PessoaWeb() {}
	
	public PessoaWeb(Pessoa pessoa) {
		
	    id = pessoa.getId();
	    nome = pessoa.getNome();
	    enderecos = new EnderecoWeb[pessoa.getEnderecos().size()];
	    int i = 0;
	    for(Endereco end: pessoa.getEnderecos()){
	    	enderecos[i++] = new EnderecoWeb(end, end.getEnderecoCategoria()==null?"":end.getEnderecoCategoria().toString());
	    }
	    enderecoCorrespondencia = new EnderecoWeb(pessoa.getEnderecoCorrespondencia(), "Correspondência");
	    telefones = new TelefoneWeb[pessoa.getTelefones().size()];
	    i = 0;
	    for(Telefone tel: pessoa.getTelefones()){
	    	telefones[i++] = new TelefoneWeb(tel);
	    }
	    email = pessoa.getEmail();
	    dataCadastro = pessoa.getDataCadastro();
	    www = pessoa.getWww();
	    documento = pessoa.getDocumento();
	    apelido = pessoa.getApelido();
	    dataInicial = pessoa.getDataInicial();
	    dataFinal = pessoa.getDataFinal();

	    contratos = new ContratoWeb[pessoa.getContratos().size()];
	    i = 0;
	    for(Contrato cont: pessoa.getContratos()){
	    	contratos[i++] = new ContratoWeb(cont);
	    }
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public EnderecoWeb getEnderecoCorrespondencia() {
		return enderecoCorrespondencia;
	}

	public void setEnderecoCorrespondencia(EnderecoWeb enderecoCorrespondencia) {
		this.enderecoCorrespondencia = enderecoCorrespondencia;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Calendar getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Calendar dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public String getWww() {
		return www;
	}

	public void setWww(String www) {
		this.www = www;
	}

	public String getDocumento() {
		return documento;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}

	public String getApelido() {
		return apelido;
	}

	public void setApelido(String apelido) {
		this.apelido = apelido;
	}

	public Calendar getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Calendar dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Calendar getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Calendar dataFinal) {
		this.dataFinal = dataFinal;
	}

	public EnderecoWeb[] getEnderecos() {
		return enderecos;
	}

	public void setEnderecos(EnderecoWeb[] enderecos) {
		this.enderecos = enderecos;
	}

	public TelefoneWeb[] getTelefones() {
		return telefones;
	}

	public void setTelefones(TelefoneWeb[] telefones) {
		this.telefones = telefones;
	}

	public ContratoWeb[] getContratos() {
		return contratos;
	}

	public void setContratos(ContratoWeb[] contratos) {
		this.contratos = contratos;
	}

	public int getErroValidacao() {
		return erroValidacao;
	}

	public void setErroValidacao(int erro) {
		this.erroValidacao = erro;
	}

}
