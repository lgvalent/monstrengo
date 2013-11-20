package br.com.orionsoft.financeiro.gerenciador.entities;

public enum LancamentoSituacao {
	PENDENTE("Pendente"),
	QUITADO("Quitado"),
	CANCELADO("Cancelado");
	
	public static final int COLUMN_DISCRIMINATOR_LENGTH = 10;
	
	private String nome;

	private LancamentoSituacao(String nome) {
		this.nome = nome;
	}

	public String getNome() {
		return nome;
	}

	/* Lucio 14/01/2008: As telas jsp do financeiro utilizam o toString() padr�o deste enum.
	 * Isto resulta em strings 'QUITADO', 'PENDENTE', 'CANCELADO'.
	 * Assim, se toString for implementado, resultar� em Strings diferentes e algumas
	 * partes do relat�rio de posi��o do cliente n�o aparecer�o */
//	public String toString(){
//		return this.nome;
//	}
}
