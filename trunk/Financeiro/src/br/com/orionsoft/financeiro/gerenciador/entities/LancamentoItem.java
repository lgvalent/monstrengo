package br.com.orionsoft.financeiro.gerenciador.entities;

import java.math.BigDecimal;
import java.util.Calendar;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;

import br.com.orionsoft.basic.entities.AdesaoContrato;
import br.com.orionsoft.basic.entities.Contrato;
import br.com.orionsoft.monstrengo.core.util.DecimalUtils;

@Entity
@Table(name = "financeiro_lancamento_item")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="discriminator",discriminatorType=DiscriminatorType.STRING,length=3)
@DiscriminatorValue("LAN")
public class LancamentoItem {
	/*
	 * Constantes com o nomes das propriedades da classe para serem usadas no
	 * código e evitar erro de digitação.
	 */
	public static final String DESCRICAO = "descricao";
	public static final String PESO = "peso";
	public static final String VALOR = "valor";
	public static final String CENTRO_CUSTO = "centroCusto";
	public static final String ITEM_CUSTO = "itemCusto";
	public static final String CLASSIFICACAO_CONTABIL = "classificacaoContabil";
	public static final String LANCAMENTO = "lancamento";
	public static final String CONTRATO = "contrato";
	public static final String ADESAO_CONTRATO = "adesaoContrato";
	public static final String DATA_LANCAMENTO = "dataLancamento";
	public static final String DATA_COMPETENCIA = "dataCompetencia";

	private Long id = -1l;
	private String descricao;
	
	/* Datas */
	private Calendar dataLancamento;
	private Calendar dataCompetencia;
	
	private BigDecimal peso;
	private BigDecimal valor;
	private CentroCusto centroCusto;
	private ItemCusto itemCusto;
	private ClassificacaoContabil classificacaoContabil;
	private Lancamento lancamento;
	
	private Contrato contrato;
	private AdesaoContrato adesaoContrato;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name = ADESAO_CONTRATO)
	@ForeignKey(name = ADESAO_CONTRATO)
	public AdesaoContrato getAdesaoContrato() {
		return adesaoContrato;
	}
	
	public void setAdesaoContrato(AdesaoContrato adesaoContrato) {
		this.adesaoContrato = adesaoContrato;
	}
	
	@ManyToOne
	@JoinColumn(name = CONTRATO)
	@ForeignKey(name = CONTRATO)
	public Contrato getContrato() {
		return contrato;
	}
	
	public void setContrato(Contrato contrato) {
		this.contrato = contrato;
	}
	
	@Column
	@Temporal(TemporalType.DATE)
	public java.util.Calendar getDataCompetencia() {
		return dataCompetencia;
	}

	public void setDataCompetencia(java.util.Calendar dataCompetencia) {
		this.dataCompetencia = dataCompetencia;
	}

	@Column
	@Temporal(TemporalType.DATE)
	public java.util.Calendar getDataLancamento() {
		return dataLancamento;
	}

	public void setDataLancamento(java.util.Calendar dataLancamento) {
		this.dataLancamento = dataLancamento;
	}

	@Column(length = 256)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(scale=10)
	public BigDecimal getPeso() {
		return peso;
	}
	public void setPeso(BigDecimal peso) {
		this.peso = peso;
	}

	@Column
	public BigDecimal getValor() {
		return valor;
	}
	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	@ManyToOne
	@JoinColumn(name = CENTRO_CUSTO)
	@ForeignKey(name = CENTRO_CUSTO)
	public CentroCusto getCentroCusto() {
		return centroCusto;
	}

	public void setCentroCusto(CentroCusto centroCusto) {
		this.centroCusto = centroCusto;
	}

	@ManyToOne
	@JoinColumn(name = CLASSIFICACAO_CONTABIL)
	@ForeignKey(name = CLASSIFICACAO_CONTABIL)
	public ClassificacaoContabil getClassificacaoContabil() {
		return classificacaoContabil;
	}

	public void setClassificacaoContabil(
			ClassificacaoContabil classificacaoContabil) {
		this.classificacaoContabil = classificacaoContabil;
	}

	@ManyToOne
	@JoinColumn(name = ITEM_CUSTO)
	@ForeignKey(name = ITEM_CUSTO)
	public ItemCusto getItemCusto() {
		return itemCusto;
	}

	public void setItemCusto(ItemCusto itemCusto) {
		this.itemCusto = itemCusto;
	}

	@ManyToOne
	@JoinColumn(name = LANCAMENTO)
	@ForeignKey(name = LANCAMENTO)
	public Lancamento getLancamento() {
		return lancamento;
	}

	public void setLancamento(Lancamento lancamento) {
		this.lancamento = lancamento;
	}
	
	public String toString() {
		StringBuffer result = new StringBuffer();
		if (this.centroCusto != null)
			result.append(this.centroCusto.toString());
		result.append(" - ");
		if (this.itemCusto != null)
			result.append(this.itemCusto.toString());
		result.append(" - ");
		if (this.descricao != null)
			result.append(this.descricao);
		
		result.append(" | ");
		result.append(DecimalUtils.formatBigDecimal(this.valor));
		
		return result.toString();
	}
}
