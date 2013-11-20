package br.com.orionsoft.financeiro.documento.pagamento;

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

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import br.com.orionsoft.basic.entities.Contrato;
import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
import br.com.orionsoft.financeiro.gerenciador.entities.Transacao;

@Entity
@Table(name = "financeiro_documento_pagamento")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="discriminator",discriminatorType=DiscriminatorType.STRING,length=3)
@DiscriminatorValue("DOC")
public abstract class DocumentoPagamento {
	/*
	 * Constantes com o nomes das propriedades da classe para serem usadas no
	 * código e evitar erro de digitação.
	 */
	public static final String DATA = "data";
	public static final String DATA_VENCIMENTO = "dataVencimento";
	public static final String DATA_CANCELAMENTO = "dataCancelamento";
    public static final String DATA_IMPRESSAO = "dataImpressao";
	public static final String VALOR = "valor";
	public static final String CONVENIO_PAGAMENTO = "convenioPagamento";
	public static final String DOCUMENTO_PAGAMENTO_CATEGORIA = "documentoPagamentoCategoria";
	public static final String CONTRATO = "contrato";
	public static final String LAYOUT_ID = "layoutId";
	public static final String LANCAMENTOS = "lancamentos";
	public static final String TRANSACAO = "transacao";
    public static final String NUMERO_DOCUMENTO = "numeroDocumento";

	private long id = -1l;
	private Calendar data;
	private Calendar dataVencimento;
	private Calendar dataCancelamento;
    private Calendar dataImpressao;
	private BigDecimal valor;
	private ConvenioPagamento convenioPagamento;
	private DocumentoPagamentoCategoria documentoPagamentoCategoria;
	private Contrato contrato;
	private List<Lancamento> lancamentos = new ArrayList<Lancamento>();
	private int layoutId = 0;
	private Transacao transacao;
	private String numeroDocumento;
//	private BigDecimal multa;
//	private BigDecimal juros;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Column
	public Calendar getData() {
		return data;
	}

	public void setData(java.util.Calendar data) {
		this.data = data;
	}

	@Column
	public Calendar getDataVencimento() {
		return dataVencimento;
	}

	public void setDataVencimento(Calendar dataVencimento) {
		this.dataVencimento = dataVencimento;
	}

	@Column
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
	@JoinColumn(name = "documentoPagamentoCategoria")
	@ForeignKey(name = "documentoPagamentoCategoria")
	public DocumentoPagamentoCategoria getDocumentoPagamentoCategoria() {
		return documentoPagamentoCategoria;
	}

	public void setDocumentoPagamentoCategoria(DocumentoPagamentoCategoria documentoPagamentoCategoria) {
		this.documentoPagamentoCategoria = documentoPagamentoCategoria;
	}

	@ManyToOne
	@JoinColumn(name = "contrato")
	@ForeignKey(name = "contrato")
	public Contrato getContrato() {
		return contrato;
	}

	public void setContrato(Contrato contratoFinanceiro) {
		this.contrato = contratoFinanceiro;
	}

	@OneToMany
	@LazyCollection(LazyCollectionOption.FALSE)
	@JoinColumn(name = "documentoPagamento")
	@ForeignKey(name = "documentoPagamento")
	public List<Lancamento> getLancamentos() {
		return lancamentos;
	}

	public void setLancamentos(List<Lancamento> lancamentos) {
		this.lancamentos = lancamentos;
	}

	@ManyToOne
	@JoinColumn(name = "convenioPagamento")
	@ForeignKey(name = "convenioPagamento")
	public ConvenioPagamento getConvenioPagamento() {
		return convenioPagamento;
	}

	public void setConvenioPagamento(ConvenioPagamento convenioPagamento) {
		this.convenioPagamento = convenioPagamento;
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
	public String getNumeroDocumento(){return numeroDocumento;}
	public void setNumeroDocumento(String numeroDocumento){this.numeroDocumento = numeroDocumento;}

//	public BigDecimal getMulta() {
//		return multa;
//	}
//
//	public void setMulta(BigDecimal multa) {
//		this.multa = multa;
//	}
//
//	public BigDecimal getJuros() {
//		return juros;
//	}
//
//	public void setJuros(BigDecimal juros) {
//		this.juros = juros;
//	}

}
