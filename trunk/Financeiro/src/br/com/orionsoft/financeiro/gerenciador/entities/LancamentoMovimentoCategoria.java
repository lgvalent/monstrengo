package br.com.orionsoft.financeiro.gerenciador.entities;

public enum LancamentoMovimentoCategoria {
	QUITADO(1, "Quitado"),
    CANCELADO(2, "Cancelado"),
    QUITADO_ESTORNADO(3, "Quitação estornada"),
    CANCELADO_ESTORNADO(4, "Cancelamento estornado"),
    TRANSFERIDO(5, "Transferido");

	public static final int COLUMN_DISCRIMINATOR_LENGTH = 20;

	private final int id;
	private final String nome;

	private LancamentoMovimentoCategoria(final int id, final String nome) {
		this.id = id;
		this.nome = nome;
	}

	public int getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}
}
