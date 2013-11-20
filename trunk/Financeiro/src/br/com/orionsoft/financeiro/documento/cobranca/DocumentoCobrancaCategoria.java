package br.com.orionsoft.financeiro.documento.cobranca;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.ForeignKey;

import br.com.orionsoft.financeiro.gerenciador.entities.Conta;

@Entity
@Table(name = "financeiro_documento_cobranca_categoria")
public class DocumentoCobrancaCategoria {
	/*
	 * Constantes com o nomes das propriedades da classe para serem usadas no
	 * código e evitar erro de digitação.
	 */
	public static final String NOME = "nome";
	public static final String CONTA_PADRAO = "contaPadrao";
	public static final String CONVENIO_COBRANCA = "convenioCobranca";
	public static final String LAYOUT_ID = "layoutId";
	public static final String FORMATO_INTRUCOES0 = "formatoInstrucoes0";
	public static final String INSTRUCOES0 = "instrucoes0";
    public static final String INSTRUCOES1 = "instrucoes1";
    public static final String INSTRUCOES2 = "instrucoes2";
	public static final String JUROS_MORA = "jurosMora";
	public static final String MULTA_ATRASO = "multaAtraso";
	public static final String DESCONTO_ANTECIPACAO = "descontoAntecipacao";
	public static final String DIAS_TOLERANCIA_MULTA_ATRASO = "diasToleranciaMultaAtraso";
//	public static final String PERCENTUAL_MULTA_ADICIONAL = "percentualMultaAdicional";
//	public static final String FREQUENCIA_MULTA_ADICIONAL = "frequenciaMultaAdicional";
    
	private Long id = -1l;
	private String nome;
	private Conta contaPadrao;
	private ConvenioCobranca convenioCobranca;
	private int layoutId = 0;
	private String formatoInstrucoes0;
    private String instrucoes1;
    private String instrucoes2;
	private BigDecimal jurosMora;
	private BigDecimal multaAtraso;
	private BigDecimal descontoAntecipacao;
	private int diasToleranciaMultaAtraso;
//	private BigDecimal percentualMultaAdicional;
//  private Frequencia frequenciaMultaAdicional;
    
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(length = 50)
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@ManyToOne
	@JoinColumn(name = "contaPadrao")
	@ForeignKey(name = "contaPadrao")
	public Conta getContaPadrao() {
		return contaPadrao;
	}

	public void setContaPadrao(Conta contaPadrao) {
		this.contaPadrao = contaPadrao;
	}

	@ManyToOne
	@JoinColumn(name = "convenioCobranca")
	@ForeignKey(name = "convenioCobranca")
	public ConvenioCobranca getConvenioCobranca() {
		return convenioCobranca;
	}
	public void setConvenioCobranca(ConvenioCobranca convenioCobranca) {
		this.convenioCobranca = convenioCobranca;
	}

	@Override
	public String toString() {
		return this.nome;
	}

	@Column
	public int getLayoutId() {
		return layoutId;
	}

	public void setLayoutId(int layoutId) {
		this.layoutId = layoutId;
	}
	
    /**
     * Este campo é calculado, analisa os Juros e Multa e gera a Intrução
     */
    @Transient
    public String getInstrucoes0() {
    	try{
	    	if (StringUtils.isNotEmpty(this.getFormatoInstrucoes0())){
	    		return String.format(getFormatoInstrucoes0(), getDiasToleranciaMultaAtraso(), getMultaAtraso().floatValue(), getJurosMora().floatValue());
	    	}
	    	return "";
    	
    	}catch(RuntimeException e){
    		return "!Erro com a String de formatação da instrução 0:" + e.getMessage();
    	}
    }
    
	@Column(length=200)
	public String getFormatoInstrucoes0() {
		return formatoInstrucoes0;
	}

	public void setFormatoInstrucoes0(String formatoInstrucoes0) {
		this.formatoInstrucoes0 = formatoInstrucoes0;
	}
	
    @Column(length=80)
    public String getInstrucoes1() {
        return instrucoes1;
    }

    public void setInstrucoes1(String instrucoes1) {
        this.instrucoes1 = instrucoes1;
    }

    @Column(length=80)
    public String getInstrucoes2() {
        return instrucoes2;
    }

    public void setInstrucoes2(String instrucoes2) {
        this.instrucoes2 = instrucoes2;
    }
    
	@Column
	public BigDecimal getJurosMora() {
		return jurosMora;
	}

	public void setJurosMora(BigDecimal jurosMora) {
		this.jurosMora = jurosMora;
	}

	@Column
	public BigDecimal getMultaAtraso() {
		return multaAtraso;
	}

	public void setMultaAtraso(BigDecimal multaAtraso) {
		this.multaAtraso = multaAtraso;
	}

	@Column
	public BigDecimal getDescontoAntecipacao() {
		return descontoAntecipacao;
	}

	public void setDescontoAntecipacao(BigDecimal descontoAntecipacao) {
		this.descontoAntecipacao = descontoAntecipacao;
	}
	
	@Column
	public int getDiasToleranciaMultaAtraso() {
		return diasToleranciaMultaAtraso;
	}

	public void setDiasToleranciaMultaAtraso(int diasToleranciaMultaAtraso) {
		this.diasToleranciaMultaAtraso = diasToleranciaMultaAtraso;
	}

//	@Column
//	public BigDecimal getPercentualMultaAdicional() {
//		return percentualMultaAdicional;
//	}
//
//	public void setPercentualMultaAdicional(BigDecimal percentualMultaAdicional) {
//		this.percentualMultaAdicional = percentualMultaAdicional;
//	}
//
//	@Enumerated(EnumType.STRING)
//	@Column(length=Frequencia.COLUMN_DISCRIMINATOR_LENGTH)
//    public Frequencia getFrequenciaMultaAdicional() {
//		return frequenciaMultaAdicional;
//	}
//
//	public void setFrequenciaMultaAdicional(Frequencia frequenciaMultaAdicional) {
//		this.frequenciaMultaAdicional = frequenciaMultaAdicional;
//	}
}
