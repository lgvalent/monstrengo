package br.com.orionsoft.basic.entities.pessoa;


/**
 * Esta classe representa o Uf Civil de uma Pessoa, que
 * classifica-se em : Solteiro, Casado, Viúvo, Divorciado

 *@author bellincanta
 */

public enum EstadoCivil {

	SOLTEIRO ("Solteiro"),
	CASADO ("Casado"),
	VIUVO ("Viúvo"),
	DIVORCIADO ("Divorciado"),
	SEPARADO("Separado");

	public static final int COLUMN_DISCRIMINATOR_LENGTH = 10;

	private String nome;

	private EstadoCivil (final String nome){
		this.nome= nome;

	}

	public String getNome() {
		return nome;
	}

	public String toString() {
		return this.nome;
	}

}

