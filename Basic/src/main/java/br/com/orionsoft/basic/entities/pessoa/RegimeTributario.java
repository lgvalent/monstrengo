package br.com.orionsoft.basic.entities.pessoa;


public enum RegimeTributario {

	SP("Simples"),
	LP("Lucro presumido"),
	LR("Lucro real");

	public static final int COLUMN_DISCRIMINATOR_LENGTH = 2;
	
	private String nome;
	
	private RegimeTributario(String nome) {
		this.nome = nome;
	}

	public String getNome() {
		return nome;
	}
	
	public String toString(){
		return nome;
	}
}
