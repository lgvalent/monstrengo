package br.com.orionsoft.financeiro.gerenciador.entities;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.orionsoft.monstrengo.core.util.CalendarUtils;


/**
 * Esta classe mantem os valores básicos referentes à mensalidade
 * @version 20110718
 */
@Entity
@Table(name="financeiro_irrf_tabela")
public class IRRFTabela{
    public static final String INICIO_VIGENCIA = "inicioVigencia";
    public static final String FINAL_VIGENCIA = "finalVigencia";
    public static final String DEDUCAO_DEPENDENTE = "deducaoDependente";
    public static final String DEDUCAO_APOSENTADO = "deducaoAposentado";
    public static final String VALOR_MINIMO_RECOLHIMENTO = "valorMinimoRecolhimento";
    public static final String ALIQUOTAS = "aliquotas";
    
	private long id = -1;
	
    private Calendar inicioVigencia;
    private Calendar finalVigencia;
    private BigDecimal deducaoDependente;
    private BigDecimal deducaoAposentado;
    private BigDecimal valorMinimoRecolhimento;
    private Set<IRRFAliquota> aliquotas = new HashSet<IRRFAliquota>();

    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    public long getId(){return id;}
    public void setId(long id){this.id = id;}

    @Temporal(TemporalType.DATE)
	public Calendar getInicioVigencia() {return inicioVigencia;}
	public void setInicioVigencia(Calendar inicioVigencia) {this.inicioVigencia = inicioVigencia;}
    
    @Temporal(TemporalType.DATE)
    public Calendar getFinalVigencia() {return finalVigencia;}
	public void setFinalVigencia(Calendar finalVigencia) {this.finalVigencia = finalVigencia;}
	

    public BigDecimal getDeducaoDependente() {
		return deducaoDependente;
	}
	public void setDeducaoDependente(BigDecimal deducaoDependente) {
		this.deducaoDependente = deducaoDependente;
	}

	public BigDecimal getDeducaoAposentado() {
		return deducaoAposentado;
	}
	public void setDeducaoAposentado(BigDecimal deducaoAposentado) {
		this.deducaoAposentado = deducaoAposentado;
	}

	public BigDecimal getValorMinimoRecolhimento() {
		return valorMinimoRecolhimento;
	}
	public void setValorMinimoRecolhimento(BigDecimal valorMinimoRecolhimento) {
		this.valorMinimoRecolhimento = valorMinimoRecolhimento;
	}

	@OneToMany(mappedBy=IRRFAliquota.TABELA, fetch=FetchType.EAGER, cascade={CascadeType.ALL})
	public Set<IRRFAliquota> getAliquotas() {
		return aliquotas;
	}
	public void setAliquotas(Set<IRRFAliquota> aliquotas) {
		this.aliquotas = aliquotas;
	}
	
	public String toString(){
    	return " "+ CalendarUtils.formatDate(inicioVigencia) + " " + CalendarUtils.formatDate(finalVigencia);
    }
}
