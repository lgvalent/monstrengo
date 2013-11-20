package br.com.orionsoft.financeiro.gerenciador.entities;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.com.orionsoft.monstrengo.core.util.DecimalUtils;


/**
 * Esta classe mantem os valores básicos referentes à mensalidade
 * @version 20110718
 */
@Entity
@Table(name="financeiro_irrf_aliquota")
public class IRRFAliquota{
    public static final String VALOR_INICIAL = "valorInicial";
    public static final String VALOR_FINAL = "valorFinal";
    public static final String ALIQUOTA = "aliquota";
    public static final String VALOR_DEDUCAO = "valorDeducao";
    public static final String TABELA = "tabela";
    
	private long id = -1;
	
    private BigDecimal valorInicial;
    private BigDecimal valorFinal;
    private BigDecimal aliquota;
    private BigDecimal valorDeducao;
    
    private IRRFTabela tabela;

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    public long getId(){return id;}
    public void setId(long id){this.id = id;}
    
    public BigDecimal getValorInicial() {
		return valorInicial;
	}
	public void setValorInicial(BigDecimal valorInicial) {
		this.valorInicial = valorInicial;
	}

	public BigDecimal getValorFinal() {
		return valorFinal;
	}
	public void setValorFinal(BigDecimal valorFinal) {
		this.valorFinal = valorFinal;
	}

	public BigDecimal getAliquota() {
		return aliquota;
	}
	public void setAliquota(BigDecimal aliquota) {
		this.aliquota = aliquota;
	}

	public BigDecimal getValorDeducao() {
		return valorDeducao;
	}
	public void setValorDeducao(BigDecimal valorDeducao) {
		this.valorDeducao = valorDeducao;
	}
	
    @ManyToOne
	public IRRFTabela getTabela() {
		return tabela;
	}
	public void setTabela(IRRFTabela tabela) {
		this.tabela = tabela;
	}

	public String toString(){
    	return "R$ " + DecimalUtils.formatBigDecimal(valorInicial) + " - R$ " + DecimalUtils.formatBigDecimal(valorFinal) + ":" + DecimalUtils.formatBigDecimal(aliquota) + "% (Dedução R$ " + DecimalUtils.formatBigDecimal(valorDeducao) +")";
    }
}
