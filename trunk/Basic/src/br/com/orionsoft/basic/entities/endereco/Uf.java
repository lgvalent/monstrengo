package br.com.orionsoft.basic.entities.endereco;

/**
 *
 * @author bellincanta
 *
 */

public enum Uf{

	EX ("(Fora do Brasil)", "EX"),
	AC ("Acre", "AC"),
	AL ("Alagoas", "AL"),
	AM ("Amazonas", "AM"),
	AP ("Amap�", "AP"),
	BA ("Bahia", "BA"),
	CE ("Cear�", "CE"),
	DF ("Distrito Federal", "DF"),
	ES ("Espirito Santo", "ES"),
	GO ("Goias", "GO"),
	MA ("Maranh�o", "MA"),
	MG ("Minas Gerais", "MG"),
	MS ("Mato Grosso do Sul", "MS"),
	MT ("Mato Grosso", "MT"),
    PA ("Par�", "PA"),
    PB ("Para�ba", "PB"),
    PE ("Pernambuco", "PE"),
    PI ("Piau�", "PI"),
	PR ("Paran�", "PR"),
	RJ ("Rio de Janeiro", "RJ"),
	RO ("Rond�nia", "RO"),
	RR ("Roraima", "RR"),
	RN ("Rio Grande do Norte", "RN"),
	RS ("Rio Grande do Sul", "RS"),
	SE ("Sergipe", "SE"),
	SC ("Santa Catarina", "SC"),
	SP ("S�o Paulo", "SP"),
	TO ("Tocantins", "TO")
	;

	public static final int COLUMN_DISCRIMINATOR_LENGTH = 2;

	private String nome;
	private String sigla;

	private Uf (final String nome, final String sigla){

		this.nome = nome;
		this.sigla = sigla;
	}



	public String getNome() {
		return nome;
	}

	public String getSigla() {
		return sigla;
	}

	public String toString() {
		return this.sigla;
	}



}