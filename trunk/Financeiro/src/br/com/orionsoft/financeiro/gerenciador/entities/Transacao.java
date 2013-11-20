package br.com.orionsoft.financeiro.gerenciador.entities;

import java.math.BigDecimal;

import br.com.orionsoft.monstrengo.core.util.DecimalUtils;

public enum Transacao {
	CREDITO(DecimalUtils.ONE, "Entrada"),
	DEBITO(DecimalUtils.ONE.negate(), "Saída");
	
	private BigDecimal multiplicador;
	private String nome;
	
	private Transacao(BigDecimal multiplicador, String nome) {
		this.multiplicador = multiplicador;
		this.nome = nome;
	}

	public BigDecimal getMultiplicador() {
		return multiplicador;
	}

	public String getNome() {
		return nome;
	}
	
	public String toString(){
		return nome;
	}
}
