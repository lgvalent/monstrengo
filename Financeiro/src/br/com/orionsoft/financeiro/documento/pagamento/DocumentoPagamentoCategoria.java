package br.com.orionsoft.financeiro.documento.pagamento;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;

import br.com.orionsoft.financeiro.gerenciador.entities.Conta;

@Entity
@Table(name = "financeiro_documento_pagamento_categoria")
public class DocumentoPagamentoCategoria {
	/*
	 * Constantes com o nomes das propriedades da classe para serem usadas no
	 * código e evitar erro de digitação.
	 */
	public static final String NOME = "nome";
	public static final String CONTA_PADRAO = "contaPadrao";
	public static final String CONVENIO_PAGAMENTO = "convenioPagamento";
	public static final String LAYOUT_ID = "layoutId";

	private Long id = -1l;
	private String nome;
	private Conta contaPadrao;
	private ConvenioPagamento convenioPagamento;
	private int layoutId = 0;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(length = 50)
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@ManyToOne
	@JoinColumn(name = "contaPadrao")
	@ForeignKey(name = "contaPadrao")
	public Conta getContaPadrao() {return contaPadrao;}
	public void setContaPadrao(Conta conta) {this.contaPadrao = conta;}

	@ManyToOne
	@JoinColumn(name = "convenioPagamento")
	@ForeignKey(name = "convenioPagamento")
	public ConvenioPagamento getConvenioPagamento() {return convenioPagamento;}
	public void setConvenioPagamento(ConvenioPagamento convenioPagamento) {this.convenioPagamento = convenioPagamento;}

	@Column
	public int getLayoutId() {return layoutId;}
	public void setLayoutId(int layoutId) {this.layoutId = layoutId;}

	@Override
	public String toString() {return this.nome;}

}
