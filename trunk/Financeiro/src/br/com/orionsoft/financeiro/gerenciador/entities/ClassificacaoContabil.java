package br.com.orionsoft.financeiro.gerenciador.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;

@Entity
@Table(name="financeiro_classificacao_contabil")
public class ClassificacaoContabil {
	/* Constantes com o nomes das propriedades da classe para 
	 * serem usadas no código e evitar erro de digitação. */	
	public static final String NOME ="nome";
	public static final String CLASSIFICAO_CONTABIL_CATEGORIA = "classificacaoContabilCategoria";
	public static final String INATIVO="inativo";
	
	private Long id = -1l;
    private String nome;   
    private ClassificacaoContabilCategoria classificacaoContabilCategoria;
    private boolean inativo;
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Column(length=100)
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	@ManyToOne
	@JoinColumn(name="classificacaoContabilCategoria")
	@ForeignKey(name="classificacaoContabilCategoria")
	public ClassificacaoContabilCategoria getClassificacaoContabilCategoria() {
		return classificacaoContabilCategoria;
	}
	public void setClassificacaoContabilCategoria(
			ClassificacaoContabilCategoria classificacaoContabilCategoria) {
		this.classificacaoContabilCategoria = classificacaoContabilCategoria;
	}

	@Column
	public boolean isInativo() {
		return inativo;
	}
	public void setInativo(boolean inativo) {
		this.inativo = inativo;
	}
	
	@Override
	public String toString() {
		return this.nome;
	}
 }