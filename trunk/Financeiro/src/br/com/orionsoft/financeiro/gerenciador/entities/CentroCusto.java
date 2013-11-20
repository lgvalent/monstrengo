package br.com.orionsoft.financeiro.gerenciador.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="financeiro_centro_custo")
public class CentroCusto {
	/* Constantes com o nomes das propriedades da classe para 
	 * serem usadas no código e evitar erro de digitação. */
	public static final String NOME ="nome";
	public static final String CODIGO_CONTA_AGRUPADORA_CONTABIL="codigoContaAgrupadoraContabil";
	public static final String INATIVO="inativo";
		
    private Long id = -1l;
    private String nome;
    private String codigoContaAgrupadoraContabil;
    private boolean inativo;
    
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
	@Column
	public boolean isInativo() {
		return inativo;
	}
	public void setInativo(boolean inativo) {
		this.inativo = inativo;
	}
	
	@Column(length=20)
	public String getCodigoContaAgrupadoraContabil() {
		return codigoContaAgrupadoraContabil;
	}
	public void setCodigoContaAgrupadoraContabil(String codigoContaContabil) {
		this.codigoContaAgrupadoraContabil = codigoContaContabil;
	}
	
	@Override
	public String toString() {
		return this.nome;
	}
 }