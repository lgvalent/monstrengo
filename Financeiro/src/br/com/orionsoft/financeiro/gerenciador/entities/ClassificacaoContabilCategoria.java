package br.com.orionsoft.financeiro.gerenciador.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="financeiro_classificacao_contabil_categoria")
public class ClassificacaoContabilCategoria {
	/* Constantes com o nomes das propriedades da classe para 
	 * serem usadas no código e evitar erro de digitação. */
    public static final String NOME="nome";
	
	private Long id= -1l;
    private String nome;
	
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Column(length=50)
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	@Override
	public String toString() {
		return this.nome;
	}    
 }
