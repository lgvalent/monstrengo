package br.com.orionsoft.financeiro.gerenciador.entities;

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
import javax.persistence.Table;

@Entity
@Table(name="financeiro_item_custo")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="discriminator", discriminatorType=DiscriminatorType.STRING, length=3)
@DiscriminatorValue("FIN")
public class ItemCusto {
	/* Constantes com o nomes das propriedades da classe para 
	 * serem usadas no código e evitar erro de digitação. */
	public static final String NOME="nome";
	public static final String CODIGO_CONTA_CONTABIL="codigoContaContabil";
	public static final String INATIVO="inativo";
	
	private Long id = -1l;
    private String nome;
    private String codigoContaContabil;
    private boolean inativo;

    @Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
    public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Column(length=150)
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	@Column
	public boolean isInativo() {
		return inativo;
	}
	public void setInativo(boolean inativo) {
		this.inativo = inativo;
	}

	@Column(length=20)
	public String getCodigoContaContabil() {
		return codigoContaContabil;
	}
	public void setCodigoContaContabil(String codigoContaContabil) {
		this.codigoContaContabil = codigoContaContabil;
	}
	
	@Override
	public String toString() {
		return this.nome;
	}
 }
