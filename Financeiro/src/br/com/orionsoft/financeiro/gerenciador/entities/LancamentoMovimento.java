package br.com.orionsoft.financeiro.gerenciador.entities;

import java.math.BigDecimal;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;

import br.com.orionsoft.financeiro.documento.pagamento.DocumentoPagamento;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.core.util.DecimalUtils;

@Entity @Table(name = "financeiro_lancamento_movimento")
public class LancamentoMovimento {
	/*
	 * Constantes com o nomes das propriedades da classe para serem usadas no
	 * código e evitar erro de digitação.
	 */
	public static final String VALOR = "valor";
	public static final String VALOR_TOTAL = "valorTotal";
	public static final String DATA_LANCAMENTO = "dataLancamento";
	public static final String DATA = "data";
	public static final String DATA_COMPENSACAO = "dataCompensacao";
	public static final String COMPENSADO= "compensado";
	public static final String DESCRICAO = "descricao";
	public static final String ESTORNADO = "estornado";
	public static final String LANCAMENTO_MOVIMENTO_CATEGORIA = "lancamentoMovimentoCategoria";
	public static final String CONTA = "conta";
	public static final String TRANSFERENCIA = "transferencia";
	public static final String DOCUMENTO_PAGAMENTO = "documentoPagamento";
	public static final String LANCAMENTO = "lancamento";
	public static final String JUROS = "juros";
	public static final String MULTA = "multa";
	public static final String DESCONTO = "desconto";
	public static final String ACRESCIMO = "acrescimo";

	@Id	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id = -1l;
	@Column
	private BigDecimal valor;
	@Column
	private BigDecimal valorTotal;
	@Column	@Temporal(TemporalType.DATE)
	private Calendar dataLancamento;
	@Column	@Temporal(TemporalType.DATE)
	private Calendar data;
	@Column	@Temporal(TemporalType.DATE)
	private Calendar dataCompensacao;
	@Column(length = 256)
	private String descricao;
	private Boolean estornado = false;
	@Enumerated(EnumType.STRING)
	@Column(length=LancamentoMovimentoCategoria.COLUMN_DISCRIMINATOR_LENGTH)
	private LancamentoMovimentoCategoria lancamentoMovimentoCategoria = LancamentoMovimentoCategoria.QUITADO;
	@ManyToOne @JoinColumn(name = "conta") @ForeignKey(name = "conta")
	private Conta conta;
	@ManyToOne @JoinColumn(name = "transferencia") @ForeignKey(name = "transferencia")
	private LancamentoMovimento transferencia;
	@ManyToOne @JoinColumn(name = "documentoPagamento") @ForeignKey(name = "documentoPagamento")
	private DocumentoPagamento documentoPagamento;
	@ManyToOne @JoinColumn(name = "lancamento")	@ForeignKey(name = "lancamento")
	private Lancamento lancamento;
	@Column
	private BigDecimal juros;
	@Column
	private BigDecimal multa;
	@Column
	private BigDecimal desconto;
	@Column
	private BigDecimal acrescimo;
	
	public String toString() {
		String result = "";
		result = this.lancamentoMovimentoCategoria.getNome() + " em " + CalendarUtils.formatDate(this.data) + " | " + DecimalUtils.formatBigDecimal(this.valorTotal);
		return result;
	}

	/*
	 * Getters & Setters
	 */
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public Calendar getData() {
		return data;
	}

	public void setData(Calendar data) {
		this.data = data;
	}

	public Calendar getDataCompensacao() {
		return dataCompensacao;
	}

	public void setDataCompensacao(Calendar dataCompensacao) {
		this.dataCompensacao = dataCompensacao;
	}

	public boolean isCompensado(){
		return this.dataCompensacao != null;
	}
	
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Boolean getEstornado() {
		return estornado;
	}

	public void setEstornado(Boolean estornado) {
		this.estornado = estornado;
	}

	public LancamentoMovimentoCategoria getLancamentoMovimentoCategoria() {
		return lancamentoMovimentoCategoria;
	}

	public void setLancamentoMovimentoCategoria(LancamentoMovimentoCategoria lancamentoMovimentoCategoria) {
		this.lancamentoMovimentoCategoria = lancamentoMovimentoCategoria;
	}

	public Conta getConta() {
		return conta;
	}

	public void setConta(Conta conta) {
		this.conta = conta;
	}

	public LancamentoMovimento getTransferencia() {
		return transferencia;
	}

	public void setTransferencia(LancamentoMovimento transferencia) {
		this.transferencia = transferencia;
	}

	public DocumentoPagamento getDocumentoPagamento() {
		return documentoPagamento;
	}

	public void setDocumentoPagamento(DocumentoPagamento documentoPagamento) {
		this.documentoPagamento = documentoPagamento;
	}

	public Lancamento getLancamento() {
		return lancamento;
	}

	public void setLancamento(Lancamento lancamento) {
		this.lancamento = lancamento;
	}

	public BigDecimal getJuros() {
		return juros;
	}

	public void setJuros(BigDecimal juros) {
		this.juros = juros;
	}

	public BigDecimal getMulta() {
		return multa;
	}

	public void setMulta(BigDecimal multa) {
		this.multa = multa;
	}

	public BigDecimal getDesconto() {
		return desconto;
	}

	public void setDesconto(BigDecimal desconto) {
		this.desconto = desconto;
	}

	public BigDecimal getAcrescimo() {
		return acrescimo;
	}

	public void setAcrescimo(BigDecimal acrescimo) {
		this.acrescimo = acrescimo;
	}

	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}

	public Calendar getDataLancamento() {
		return dataLancamento;
	}

	public void setDataLancamento(Calendar dataLancamento) {
		this.dataLancamento = dataLancamento;
	}
	
}
