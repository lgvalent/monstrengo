package br.com.orionsoft.basic.entities.pessoa;

/**
 * Tipos de documento militar.
 * Para possibilitar a escolha dos tipos disponíveis e exibir o tipo completo
 * @author Lucio 20071129
 * @version 20071129
 */
public enum DocumentoMilitarTipo {
	CDI("Certificado de Dispensa de Incorporação", "CDI"),
	CR2("Certificado de Reservista de 2a. Categoria", "CR2");

	public static final int COLUMN_DISCRIMINATOR_LENGTH = 3;

	private DocumentoMilitarTipo(String nome, String sigla)
	{
		this.nome = nome;
		this.sigla = sigla;
	}

	private String nome;
	private String sigla;


	public String getNome() {return nome;}

	public String getAbreviacao() {return sigla;}
	
	public String toString(){
		return nome + "(" + sigla + ")";
	}

}
