package br.com.orionsoft.financeiro.documento.cobranca.titulo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobranca;

/**
 * @author marcia
 * @hibernate.joined-subclass table="financeiro_titulo"
 * @hibernate.joined-subclass-key foreign-key="PRIMARY"
 */
@Entity
@DiscriminatorValue("TIT")
public class DocumentoTitulo extends DocumentoCobranca{
    /*
     * Constantes com os nomes das propriedades da classe para serem usadas no código e evitar erro de digitação.
     */
    public static final String OCORRENCIAS = "ocorrencias";
    public static final String ULTIMA_OCORRENCIA = "ultimaOcorrencia";
    public static final String DATA_ULTIMA_OCORRENCIA = "dataUltimaOcorrencia";
    
    // Datas
    public static final String DATA_CREDITO = "dataCredito";
    
    // Valores
    public static final String VALOR_TARIFA = "valorTarifa";
    public static final String VALOR_IOF = "valorIOF";
    public static final String OUTRAS_DEDUCOES = "outrasDeducoes";
    

    //Controle
    private List<OcorrenciaControle> ocorrencias = new ArrayList<OcorrenciaControle>(0);
    private Ocorrencia ultimaOcorrencia;
    
    // Datas
    private Calendar dataCredito;
    private Calendar dataUltimaOcorrencia;
    
    private BigDecimal valorTarifa;
    private BigDecimal valorIOF;
    private BigDecimal outrasDeducoes;
    
    /**
     * Propriedade calculada para facilitar o acesso
     * ao convênio atualmente ligado a este documento
     * como um Convênio
     */
    @Transient
    public Cedente getCedente() {
        return Cedente.class.cast(this.getConvenioCobranca());
    }
    
    /**
     * @hibernate.property
     */
    @Column
    @Temporal(TemporalType.DATE)
    public Calendar getDataCredito() {
        return dataCredito;
    }

    public void setDataCredito(Calendar dataCredito) {
        this.dataCredito = dataCredito;
    }

    /**
     * @hibernate.property
     */
    @Column
    @Temporal(TemporalType.DATE)
    public Calendar getDataUltimaOcorrencia() {
        return dataUltimaOcorrencia;
    }

    public void setDataUltimaOcorrencia(Calendar dataOcorrencia) {
        this.dataUltimaOcorrencia = dataOcorrencia;
    }
    
    /**
     * @hibernate.set cascade="all" lazy="false" 
     * @hibernate.collection-one-to-many class="br.com.orionsoft.financeiro.documento.titulo.OcorrenciaControle"
     * @hibernate.collection-key foreign-key="titulo"
     * @hibernate.collection-key-column name="titulo" index="titulo"
     */
    @OneToMany
	@LazyCollection(LazyCollectionOption.FALSE)
	@JoinColumn(name = OcorrenciaControle.TITULO)
	@ForeignKey(name = OcorrenciaControle.TITULO)
    public List<OcorrenciaControle> getOcorrencias() {
		return ocorrencias;
	}

	public void setOcorrencias(List<OcorrenciaControle> ocorrencias) {
		this.ocorrencias = ocorrencias;
	}

	/**
     * @hibernate.many-to-one foreign-key="ultimaOcorrencia"
     */
	@ManyToOne
	@JoinColumn(name = ULTIMA_OCORRENCIA)
	@ForeignKey(name = ULTIMA_OCORRENCIA)
    public Ocorrencia getUltimaOcorrencia() {
    	return ultimaOcorrencia;
    }

    public void setUltimaOcorrencia(Ocorrencia ocorrencia) {
        this.ultimaOcorrencia = ocorrencia;
    }

    /**
     * @hibernate.property
     * @hibernate.column name="valorIOF" sql-type="Decimal(15,4)" 
     */
    @Column
    public BigDecimal getValorIOF() {
        return valorIOF;
    }

    public void setValorIOF(BigDecimal valorIOF) {
        this.valorIOF = valorIOF;
    }

    /**
     * @hibernate.property
     * @hibernate.column name="outrasDeducoes" sql-type="Decimal(15,4)" 
     */
    @Column
    public BigDecimal getOutrasDeducoes() {
        return outrasDeducoes;
    }

    public void setOutrasDeducoes(BigDecimal outrasDeducoes) {
        this.outrasDeducoes = outrasDeducoes;
    }

    /**
     * @hibernate.property
     * @hibernate.column name="valorTarifa" sql-type="Decimal(15,4)" 
     */
    @Column
    public BigDecimal getValorTarifa() {
        return valorTarifa;
    }

    public void setValorTarifa(BigDecimal valorTarifa) {
        this.valorTarifa = valorTarifa;
    }

	public String toString() {
		String result = "";
		if (this.getNumeroDocumento() != null)
			result= this.getNumeroDocumento() + " | ";
		if (this.getContrato() != null)
			result += this.getContrato().toString();
		if (result.equals(""))
			result += "Título id: " + this.getId();
        
        return result;
    }
	
}
