package br.com.orionsoft.financeiro.documento.cobranca;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import br.com.orionsoft.basic.entities.Contrato;
import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
import br.com.orionsoft.financeiro.gerenciador.entities.Transacao;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;

@Entity
@Table(name = "financeiro_documento_cobranca")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="discriminator",discriminatorType=DiscriminatorType.STRING,length=3)
@DiscriminatorValue("DOC")
public abstract class DocumentoCobranca {
	/*
	 * Constantes com o nomes das propriedades da classe para serem usadas no
	 * código e evitar erro de digitação.
	 */
	public static final String DATA = "data";
	public static final String DATA_VENCIMENTO = "dataVencimento";
	public static final String DATA_CANCELAMENTO = "dataCancelamento";
    public static final String DATA_IMPRESSAO = "dataImpressao";
	public static final String VALOR = "valor";
    public static final String VALOR_DESCONTO = "valorDesconto";
    public static final String VALOR_JUROS = "valorJuros";
    public static final String VALOR_MULTA = "valorMulta";
    public static final String VALOR_ACRESCIMO = "valorAcrescimo";
    public static final String VALOR_PAGO = "valorPago";
	public static final String CONVENIO_COBRANCA = "convenioCobranca";
	public static final String DOCUMENTO_COBRANCA_CATEGORIA = "documentoCobrancaCategoria";
	public static final String CONTRATO = "contrato";
    public static final String LAYOUT_ID = "layoutId";
    public static final String LANCAMENTOS = "lancamentos";
    public static final String TRANSACAO = "transacao";
    public static final String NUMERO_DOCUMENTO = "numeroDocumento";

    public static final String INSTRUCOES3 = "instrucoes3";

    private long id = IDAO.ENTITY_UNSAVED;
	private Calendar data;
	private Calendar dataVencimento;
	private Calendar dataCancelamento;
    private Calendar dataImpressao;
    // Valores
	private BigDecimal valor;
	private BigDecimal valorDesconto;
	private BigDecimal valorJuros;
	private BigDecimal valorMulta;
	private BigDecimal valorPago;

	private ConvenioCobranca convenioCobranca;
	private DocumentoCobrancaCategoria documentoCobrancaCategoria;
	private Contrato contrato;
	private List<Lancamento> lancamentos = new ArrayList<Lancamento>();
	private int layoutId = 0;
	private Transacao transacao;
	private String numeroDocumento;
	
    private String instrucoes3; //as instruções 0, 1 e 2 fazem parte da DocumentoCobrancaCategoriao 
	private BigDecimal valorAcrescimo;
    
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Column
	@Temporal(TemporalType.DATE)
	public Calendar getData() {
		return data;
	}

	public void setData(Calendar data) {
		this.data = data;
	}

	@Column
	@Temporal(TemporalType.DATE)
	public Calendar getDataVencimento() {
		return dataVencimento;
	}

	public void setDataVencimento(Calendar dataVencimento) {
		this.dataVencimento = dataVencimento;
	}

	@Column
	@Temporal(TemporalType.DATE)
	public Calendar getDataCancelamento() {
		return dataCancelamento;
	}

	public void setDataCancelamento(Calendar dataCancelamento) {
		this.dataCancelamento = dataCancelamento;
	}

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    public Calendar getDataImpressao() {return dataImpressao;}
    public void setDataImpressao(Calendar dataImpressao) {this.dataImpressao = dataImpressao;}
    
	@Column
	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
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

	@ManyToOne
	@JoinColumn(name = "documentoCobrancaCategoria")
	@ForeignKey(name = "documentoCobrancaCategoria")
	public DocumentoCobrancaCategoria getDocumentoCobrancaCategoria() {
		return documentoCobrancaCategoria;
	}

	public void setDocumentoCobrancaCategoria(
			DocumentoCobrancaCategoria documentoCobrancaCategoria) {
		this.documentoCobrancaCategoria = documentoCobrancaCategoria;
	}

	@ManyToOne
	@JoinColumn(name = "contrato")
	@ForeignKey(name = "contrato")
	public Contrato getContrato() {
		return contrato;
	}

	public void setContrato(Contrato contrato) {
		this.contrato = contrato;
	}
	
	@OneToMany
	@LazyCollection(LazyCollectionOption.FALSE)
	@JoinColumn(name = "documentoCobranca")
	@ForeignKey(name = "documentoCobranca")
	public List<Lancamento> getLancamentos() {
		return lancamentos;
	}

	public void setLancamentos(List<Lancamento> lancamentos) {
		this.lancamentos = lancamentos;
	}

	@Column
	public int getLayoutId() {
		return layoutId;
	}

	public void setLayoutId(int layoutId) {
		this.layoutId = layoutId;
	}

	@Column
	public Transacao getTransacao() {
		return transacao;
	}

	public void setTransacao(Transacao transacao) {
		this.transacao = transacao;
	}

    @Column(length=20)
    @Index(name=NUMERO_DOCUMENTO)
	public String getNumeroDocumento() {
		return numeroDocumento;
	}

	public void setNumeroDocumento(String numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}

	@Column(length=80)
    public String getInstrucoes3() {
		return instrucoes3;
	}

	public void setInstrucoes3(String instrucoes) {
		this.instrucoes3 = instrucoes==null?null:StringUtils.substring(instrucoes, 0, 80);
	}

	@Column
	public BigDecimal getValorAcrescimo() {
	    return valorAcrescimo;
	}

	@Column
	public BigDecimal getValorDesconto() {
	    return valorDesconto;
	}

	@Column
	public BigDecimal getValorJuros() {
	    return valorJuros;
	}

	@Column
	public BigDecimal getValorMulta() {
	    return valorMulta;
	}

	@Column
	public BigDecimal getValorPago() {
	    return valorPago;
	}

	public void setValorAcrescimo(BigDecimal valorAcrescimo) {
	    this.valorAcrescimo = valorAcrescimo;
	}

	public void setValorDesconto(BigDecimal valorDesconto) {
	    this.valorDesconto = valorDesconto;
	}

	public void setValorJuros(BigDecimal valorJuros) {
	    this.valorJuros = valorJuros;
	}

	public void setValorMulta(BigDecimal valorMulta) {
	    this.valorMulta = valorMulta;
	}

	public void setValorPago(BigDecimal valorPago) {
	    this.valorPago = valorPago;
	}
}
