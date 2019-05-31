package br.com.orionsoft.financeiro.gerenciador.entities;

public class CartaCobrancaBean {
	private String razaoSocial;
	private String documento;
	private String endereco;
	private String cep;
	private String cidade;
	private String email;
    private String dataMovimento;
    private String descricao;
    private String dataVencimento;
    private String valor;

    public CartaCobrancaBean(String razaoSocial, String documento, String endereco, String cep, String cidade, String email, String dataMovimento, String descricao, String dataVencimento, String valor) {
        super();
        this.razaoSocial = razaoSocial;
        this.documento = documento;
        this.endereco = endereco;
        this.cep = cep;
        this.cidade = cidade;
        this.email = email;
        this.dataMovimento = dataMovimento;
        this.descricao = descricao;
        this.dataVencimento = dataVencimento;
        this.valor = valor;
    }

    public String getDataMovimento() {
        return dataMovimento;
    }

    public void setDataMovimento(String dataMovimento) {
        this.dataMovimento = dataMovimento;
    }

    public String getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(String dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

	public String getRazaoSocial() {
		return razaoSocial;
	}

	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return razaoSocial + "("+ documento +")";
	}
}
