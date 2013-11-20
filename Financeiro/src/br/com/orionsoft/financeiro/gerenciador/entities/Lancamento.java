package br.com.orionsoft.financeiro.gerenciador.entities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import br.com.orionsoft.basic.entities.Contrato;
import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobranca;
import br.com.orionsoft.financeiro.documento.pagamento.DocumentoPagamento;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.core.util.DecimalUtils;

@Entity
@Table(name = "financeiro_lancamento")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="discriminator",discriminatorType=DiscriminatorType.STRING,length=3)
@DiscriminatorValue("LAN")
public class Lancamento {

	/*
	 * Constantes com o nomes das propriedades da classe para serem usadas no
	 * código e evitar erro de digitação.
	 */
	public static final String CONTA_PREVISTA = "contaPrevista";
	public static final String VALOR = "valor";
	public static final String DESCRICAO = "descricao";
	public static final String DATA = "data";
	public static final String DATA_VENCIMENTO = "dataVencimento";
	public static final String SALDO = "saldo";
	public static final String OPERACAO = "operacao";
	public static final String LANCAMENTO_ITENS = "lancamentoItens";
	public static final String LANCAMENTO_MOVIMENTOS = "lancamentoMovimentos";
	public static final String DOCUMENTO_COBRANCA = "documentoCobranca";
	public static final String DOCUMENTO_PAGAMENTO = "documentoPagamento";
	public static final String CONTRATO = "contrato";
	public static final String NAO_RECEBER_APOS_VENCIMENTO = "naoReceberAposVencimento";
	public static final String LANCAMENTO_SITUACAO = "lancamentoSituacao";
	public static final String CODIGO_EXTERNO = "codigoExterno";

	private long id = -1l;
	private Conta contaPrevista;
	private BigDecimal valor;
	private String descricao;
	private Calendar data;
	private Calendar dataVencimento;
	private BigDecimal saldo;
	private Operacao operacao;
	private List<LancamentoItem> lancamentoItens = new ArrayList<LancamentoItem>();
	private List<LancamentoMovimento> lancamentoMovimentos = new ArrayList<LancamentoMovimento>();
	private DocumentoCobranca documentoCobranca;
	private DocumentoPagamento documentoPagamento;
	private Contrato contrato;
	private boolean naoReceberAposVencimento;
	private LancamentoSituacao lancamentoSituacao;
	private String codigoExterno;

	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append(CalendarUtils.formatDate(this.dataVencimento));
		result.append(" - ");
		result.append(DecimalUtils.formatBigDecimal(this.valor));
		result.append(" - ");
		result.append(this.descricao);
		return result.toString();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Column
	public java.math.BigDecimal getValor() {
		return valor;
	}

	public void setValor(java.math.BigDecimal valor) {
		this.valor = valor;
	}

	@Column(length = 256)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column
	@Temporal(TemporalType.DATE)
	public java.util.Calendar getData() {
		return data;
	}

	public void setData(java.util.Calendar data) {
		this.data = data;
	}

	@Column
	@Temporal(TemporalType.DATE)
	public java.util.Calendar getDataVencimento() {
		return dataVencimento;
	}

	public void setDataVencimento(java.util.Calendar dataVencimento) {
		this.dataVencimento = dataVencimento;
	}

	@Column
	public java.math.BigDecimal getSaldo() {
		return saldo;
	}

	public void setSaldo(java.math.BigDecimal saldo) {
		this.saldo = saldo;
	}

	@ManyToOne
	@JoinColumn(name = "operacao")
	@ForeignKey(name = "operacao")
	public Operacao getOperacao() {
		return operacao;
	}

	public void setOperacao(Operacao operacao) {
		this.operacao = operacao;
	}

	@OneToMany(cascade=CascadeType.ALL)
	@ForeignKey(name=LancamentoItem.LANCAMENTO)
	@JoinColumn(name=LancamentoItem.LANCAMENTO)
	@LazyCollection(LazyCollectionOption.FALSE)
	public List<LancamentoItem> getLancamentoItens() {
		return lancamentoItens;
	}

	public void setLancamentoItens(List<LancamentoItem> lancamentoItens) {
		this.lancamentoItens = lancamentoItens;
	}

	@OneToMany(cascade=CascadeType.ALL)
	@ForeignKey(name=LancamentoMovimento.LANCAMENTO)
	@JoinColumn(name=LancamentoMovimento.LANCAMENTO)
	@LazyCollection(LazyCollectionOption.FALSE)
	public List<LancamentoMovimento> getLancamentoMovimentos() {
		return lancamentoMovimentos;
	}

	public void setLancamentoMovimentos(
			List<LancamentoMovimento> lancamentoMovimentos) {
		this.lancamentoMovimentos = lancamentoMovimentos;
	}

	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "documentoCobranca")
	@ForeignKey(name = "documentoCobranca")
	public DocumentoCobranca getDocumentoCobranca() {
		return documentoCobranca;
	}

	public void setDocumentoCobranca(DocumentoCobranca documentoCobranca) {
		this.documentoCobranca = documentoCobranca;
	}

	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "documentoPagamento")
	@ForeignKey(name = "documentoPagamento")
	public DocumentoPagamento getDocumentoPagamento() {
		return documentoPagamento;
	}

	public void setDocumentoPagamento(DocumentoPagamento documentoPagamento) {
		this.documentoPagamento = documentoPagamento;
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

	@ManyToOne
	@JoinColumn(name = "contaPrevista")
	@ForeignKey(name = "contaPrevista")
	public Conta getContaPrevista() {return contaPrevista;}
	public void setContaPrevista(Conta contaPrevista) {this.contaPrevista = contaPrevista;}

	@Column
	public boolean isNaoReceberAposVencimento() {return naoReceberAposVencimento;}
	public void setNaoReceberAposVencimento(boolean naoReceberAposVencimento) {this.naoReceberAposVencimento = naoReceberAposVencimento;}

	@Enumerated(EnumType.STRING)
	@Column(length=LancamentoSituacao.COLUMN_DISCRIMINATOR_LENGTH)
	public LancamentoSituacao getLancamentoSituacao() {return lancamentoSituacao;}
	public void setLancamentoSituacao(LancamentoSituacao lancamentoSituacao) {this.lancamentoSituacao = lancamentoSituacao;}

	@Column(length=20)
	public String getCodigoExterno() {return codigoExterno;}
	public void setCodigoExterno(String codigoExterno) {this.codigoExterno = codigoExterno;}
}