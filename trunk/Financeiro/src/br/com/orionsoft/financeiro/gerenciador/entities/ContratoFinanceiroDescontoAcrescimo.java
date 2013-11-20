package br.com.orionsoft.financeiro.gerenciador.entities;

import java.math.BigDecimal;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;

import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.core.util.DecimalUtils;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;

/***
 * 07/09/2007
 * Esta classe representa o desconto ou acréscimo representado por um item de custo, 
 * que pode ser dado para um valor cobrado dos contratos.
 * O desconto ou acréscimo tem validade por determinado período de tempo,
 * delimitado pelas propriedades dataInicial e dataFinal.
 * Observação: se for um desconto, o operador deverá preencher o campo valor
 * com um valor negativo 
 *  
 * @author Lucio
 *
 */
@Entity
@Table(name="financeiro_contrato_descontos_acrescimos")
public class ContratoFinanceiroDescontoAcrescimo {

	public static final String DATA_INICIAL = "dataInicial";
	public static final String DATA_FINAL = "dataFinal";
	public static final String ITEM_CUSTO = "itemCusto";
	public static final String CLASSIFICACAO_CONTABIL = "classificacaoContabil";
	public static final String VALOR = "valor";
	public static final String CONTRATO_FINANCEIRO = "contratoFinanceiro";

	private long id = IDAO.ENTITY_UNSAVED;
	private Calendar dataInicial;
	private Calendar dataFinal;
	private ItemCusto itemCusto;
	private ClassificacaoContabil classificacaoContabil;
	
	/** Permite definir a ordem de aplicação dos descontos e acréscimos */
	private int ordem = 0;
    
	/** Define o valor do parâmetro */
    private BigDecimal valor;
    private BigDecimal percentual;
    
    private ContratoFinanceiro contratoFinanceiro;

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	public long getId(){return this.id;}
	public void setId(long id){this.id = id;}

	@Column
	@Temporal(TemporalType.DATE)
	public Calendar getDataFinal() {return dataFinal;}
	public void setDataFinal(Calendar dataFinal) {this.dataFinal = dataFinal;}

	@Column
	@Temporal(TemporalType.DATE)
	public Calendar getDataInicial() {return dataInicial;}
	public void setDataInicial(Calendar dataInicial) {this.dataInicial = dataInicial;}
	
	@ManyToOne
	@ForeignKey(name="itemCusto")
	@JoinColumn(name="itemCusto")
	public ItemCusto getItemCusto() {return itemCusto;}
	public void setItemCusto(ItemCusto itemCusto) {this.itemCusto = itemCusto;}
	
	@ManyToOne
	@ForeignKey(name="classificacaoContabil")
	@JoinColumn(name="classificacaoContabil")
	public ClassificacaoContabil getClassificacaoContabil() {return classificacaoContabil;}
	public void setClassificacaoContabil(ClassificacaoContabil classificacaoContabil) {this.classificacaoContabil = classificacaoContabil;}
	
	@Column
	public BigDecimal getValor() {return valor;}
	public void setValor(BigDecimal valor) {this.valor = valor;}
	
	@ManyToOne
	@ForeignKey(name="contratoFinanceiro")
	@JoinColumn(name="contratoFinanceiro")
	public ContratoFinanceiro getContratoFinanceiro() {return contratoFinanceiro;}
	public void setContratoFinanceiro(ContratoFinanceiro contratoFinanceiro) {this.contratoFinanceiro = contratoFinanceiro;}

	@Column
	public int getOrdem() {return ordem;}
	public void setOrdem(int ordem) {this.ordem = ordem;}
	
	@Column
	public BigDecimal getPercentual() {return percentual;}
	public void setPercentual(BigDecimal percentual) {this.percentual = percentual;}

	public String toString(){
		String result = CalendarUtils.formatDate(this.dataInicial) + " - " + CalendarUtils.formatDate(this.dataFinal);
		
		result += this.itemCusto!=null?this.itemCusto.getNome():"";

		result += DecimalUtils.formatBigDecimal(this.valor);

		return result;
	}

}
