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
	AP ("Amapá", "AP"),
	BA ("Bahia", "BA"),
	CE ("Ceará", "CE"),
	DF ("Distrito Federal", "DF"),
	ES ("Espirito Santo", "ES"),
	GO ("Goias", "GO"),
	MA ("Maranhão", "MA"),
	MG ("Minas Gerais", "MG"),
	MS ("Mato Grosso do Sul", "MS"),
	MT ("Mato Grosso", "MT"),
    PA ("Pará", "PA"),
    PB ("Paraíba", "PB"),
    PE ("Pernambuco", "PE"),
    PI ("Piauí", "PI"),
	PR ("Paraná", "PR"),
	RJ ("Rio de Janeiro", "RJ"),
	RO ("Rondônia", "RO"),
	RR ("Roraima", "RR"),
	RN ("Rio Grande do Norte", "RN"),
	RS ("Rio Grande do Sul", "RS"),
	SE ("Sergipe", "SE"),
	SC ("Santa Catarina", "SC"),
	SP ("São Paulo", "SP"),
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