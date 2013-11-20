package br.com.orionsoft.financeiro.documento.pagamento;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;

import br.com.orionsoft.basic.entities.pessoa.Juridica;

@Entity
@Table(name = "financeiro_convenio_pagamento")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="discriminator",discriminatorType=DiscriminatorType.STRING,length=3)
@DiscriminatorValue("CON")
public class ConvenioPagamento {
	/*
	 * Constantes com o nomes das propriedades da classe para serem usadas no
	 * código e evitar erro de digitação.
	 */
	public static final String NOME = "nome";
	public static final String NOME_GERENCIADOR_DOCUMENTO = "nomeGerenciadorDocumento";
	public static final String OBSERVACOES = "observacoes";
	public static final String CONTRANTE = "contratante";
	public static final String CONTRATADO = "contratado";

	private Long id = -1l;
	private String nome;
	private String nomeGerenciadorDocumento;
	private String observacoes;
	private Juridica contratante;
	private Juridica contratado;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {this.id = id;}

	@Column(length = 50)
	public String getNome() {return nome;}

	public void setNome(String nome) {this.nome = nome;}

	@Column(length = 50)
	public String getNomeGerenciadorDocumento() {return nomeGerenciadorDocumento;}

	public void setNomeGerenciadorDocumento(String nomeGerenciadorDocumento) {
		this.nomeGerenciadorDocumento = nomeGerenciadorDocumento;}

	@Column(length = 256)
	public String getObservacoes() {return observacoes;	}

	public void setObservacoes(String observacoes) {this.observacoes = observacoes;	}

	@ManyToOne
	@JoinColumn(name = "contratante")
	@ForeignKey(name = "contratante")
	public Juridica getContratante() {return contratante;}

	public void setContratante(Juridica contratante) {this.contratante = contratante;}

	@ManyToOne
	@JoinColumn(name = "contratado")
	@ForeignKey(name = "contratado")
	public Juridica getContratado() {return contratado;	}

	public void setContratado(Juridica contratado) {this.contratado = contratado;}

	@Override
	public String toString() {
		return this.nome;
	}
}
