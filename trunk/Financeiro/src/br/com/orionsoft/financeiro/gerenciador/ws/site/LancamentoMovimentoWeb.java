package br.com.orionsoft.financeiro.gerenciador.ws.site;

import java.math.BigDecimal;
import java.util.Calendar;

import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimento;

public class LancamentoMovimentoWeb {

	private long id = -1l;
	private BigDecimal valor;
	private BigDecimal valorTotal;
	private Calendar dataLancamento;
	private Calendar data;
	private Calendar dataCompensacao;
	private String descricao;
	private Boolean estornado;
	private String lancamentoMovimentoCategoria;
	private String conta;
	private String documentoPagamento;
	private BigDecimal juros;
	private BigDecimal multa;
	private BigDecimal desconto;
	private BigDecimal acrescimo;

	
	public LancamentoMovimentoWeb(LancamentoMovimento movimento) {
		id = movimento.getId();
		valor = movimento.getValor();
		valorTotal = movimento.getValorTotal();
		dataLancamento = movimento.getDataLancamento();
		data = movimento.getData();
		dataCompensacao = movimento.getDataCompensacao();
		descricao = movimento.getDescricao();
		estornado = movimento.getEstornado();
		
		if(movimento.getLancamentoMovimentoCategoria()!= null)
			lancamentoMovimentoCategoria = movimento.getLancamentoMovimentoCategoria().toString();
		
		conta = movimento.getConta()==null?"":movimento.getConta().toString(); // Movimentos cancelados perdem suas contas!!!
		
		if(movimento.getDocumentoPagamento() != null)
			documentoPagamento = movimento.getDocumentoPagamento().toString();
		
		juros = movimento.getJuros();
		multa = movimento.getMulta();
		desconto = movimento.getDesconto();
		acrescimo = movimento.getAcrescimo();
	}
	
	public LancamentoMovimentoWeb() {
		// TODO Auto-generated constructor stub
	}

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

	public String getLancamentoMovimentoCategoria() {
		return lancamentoMovimentoCategoria;
	}

	public void setLancamentoMovimentoCategoria(String lancamentoMovimentoCategoria) {
		this.lancamentoMovimentoCategoria = lancamentoMovimentoCategoria;
	}

	public String getConta() {
		return conta;
	}

	public void setConta(String conta) {
		this.conta = conta;
	}

	public String getDocumentoPagamento() {
		return documentoPagamento;
	}

	public void setDocumentoPagamento(String documentoPagamento) {
		this.documentoPagamento = documentoPagamento;
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

}
