package br.com.orionsoft.financeiro.contabilidade.services;

import java.math.BigDecimal;
import java.util.Calendar;

public class QueryLancamentoContabil {

	public static final String SELECT_PREVISTO = "" +
			"SELECT NEW " +
			QueryLancamentoContabil.class.getName() +
			"(" +
			"lan.id," +
			"lan.contrato.pessoa.nome," +
			"lan.descricao," +
			"item.descricao," +
			"item.valor," +
			"lan.dataVencimento," +
			"lan.contaPrevista.contaContabilPrevista," +
			"lan.contaPrevista.contaContabilMovimento," +
			"lan.contaPrevista.contaContabilCompensacao," +
			"lan.contrato.codigoContaContabil," +
			"item.centroCusto.codigoContaAgrupadoraContabil," +
			"item.itemCusto.codigoContaContabil," +
			"lan.contrato.pessoa.documento," +
			"lan.documentoPagamento.documentoPagamentoCategoria.nome," +
			"lan.documentoPagamento.numeroDocumento" +
			")" +
			" FROM Lancamento AS lan" +
			" JOIN lan.lancamentoItens AS item" +
			" WHERE lan.dataVencimento BETWEEN :dataInicial AND :dataFinal" +
			" ORDER BY lan.dataVencimento";
	
	public static final String SELECT_MOVIMENTO = "" +
			"SELECT NEW " +
			QueryLancamentoContabil.class.getName() +
			"(" +
			"mov.id," +
			"mov.lancamento.contrato.pessoa.nome," +
			"mov.lancamento.descricao," +
			"item.descricao," +
			"mov.valor * item.peso," +
			"mov.data," +
			"mov.conta.contaContabilPrevista," +
			"mov.conta.contaContabilMovimento," +
			"mov.conta.contaContabilCompensacao," +
			"mov.lancamento.contrato.codigoContaContabil," +
			"item.centroCusto.codigoContaAgrupadoraContabil," +
			"item.itemCusto.codigoContaContabil," +
			"mov.lancamento.contrato.pessoa.documento," +
			"mov.documentoPagamento.documentoPagamentoCategoria.nome," +
			"mov.documentoPagamento.numeroDocumento" +
			")" +
			" FROM LancamentoMovimento AS mov" +
			" JOIN mov.lancamento.lancamentoItens AS item" +
			" WHERE mov.data BETWEEN :dataInicial AND :dataFinal" +
			" ORDER BY mov.data";

	public static final String SELECT_COMPENSACAO = "" +
			"SELECT NEW " +
			QueryLancamentoContabil.class.getName() +
			"(" +
			"mov.id," +
			"mov.lancamento.contrato.pessoa.nome," +
			"mov.lancamento.descricao," +
			"item.descricao," +
			"mov.valorTotal * item.peso," +
			"mov.data," +
			"mov.conta.contaContabilPrevista," +
			"mov.conta.contaContabilMovimento," +
			"mov.conta.contaContabilCompensacao," +
			"mov.lancamento.contrato.codigoContaContabil," +
			"item.centroCusto.codigoContaAgrupadoraContabil," +
			"item.itemCusto.codigoContaContabil," +
			"mov.lancamento.contrato.pessoa.documento," +
			"mov.documentoPagamento.documentoPagamentoCategoria.nome," +
			"mov.documentoPagamento.numeroDocumento" +
			")" +
			" FROM LancamentoMovimento AS mov" +
			" JOIN mov.lancamento.lancamentoItens AS item" +
			" WHERE mov.dataCompensacao BETWEEN :dataInicial AND :dataFinal" +
			" ORDER BY mov.dataCompensacao";

	/** 
	 * Na listagem do PREVISTO o id referencia um Lancamento -> lan.id
	 * Nas demais, MOVIMENTO e COMPENSACAO, o id referencia um LancamentoMovimento -> mov.id
	 */
	private Long id;
	private String nomePessoa;
	private String descricaoLancamento;
	private String descricaoItem;
	private BigDecimal valor;
	private Calendar dataCompetencia;
	private String contaPrevistaCcc;
	private String contaMovimentoCcc;
	private String contaCompensacaoCcc;
	private String contratoCcc;
	private String centroCustoCcc;
	private String itemCustoCcc;
	private String documentoPessoa;
	private String pagamentoCategoria;
	private String pagamentoNumero;
	
	public QueryLancamentoContabil(
			Long id,
			String nomePessoa,
			String descricaoLancamento,
			String descricaoItem,
			BigDecimal valor,
			Calendar dataCompetencia,
			String contaPrevistaCcc,
			String contaMovimentoCcc,
			String contaCompensacaoCcc,
			String contratoCcc,
			String centroCustoCcc,
			String itemCustoCcc,
			String documentoPessoa,
			String pagamentoCategoria,
			String pagamentoNumero
			) {
		this.id = id;
		this.nomePessoa = nomePessoa;
		this.descricaoLancamento = descricaoLancamento;
		this.descricaoItem = descricaoItem;
		this.valor = valor;
		this.dataCompetencia = dataCompetencia;
		this.contaPrevistaCcc = contaPrevistaCcc;
		this.contaMovimentoCcc = contaMovimentoCcc;
		this.contaCompensacaoCcc = contaCompensacaoCcc;
		this.contratoCcc = contratoCcc;
		this.centroCustoCcc = centroCustoCcc;
		this.itemCustoCcc = itemCustoCcc;
		this.documentoPessoa = documentoPessoa;
		this.pagamentoCategoria = pagamentoCategoria;
		this.pagamentoNumero = pagamentoNumero;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNomePessoa() {
		return nomePessoa;
	}
	public void setNomePessoa(String nomePessoa) {
		this.nomePessoa = nomePessoa;
	}
	public String getDescricaoLancamento() {
		return descricaoLancamento;
	}
	public void setDescricaoLancamento(String descricaoLancamento) {
		this.descricaoLancamento = descricaoLancamento;
	}
	public String getDescricaoItem() {
		return descricaoItem;
	}
	public void setDescricaoItem(String descricaoItem) {
		this.descricaoItem = descricaoItem;
	}
	public BigDecimal getValor() {
		return valor;
	}
	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}
	public Calendar getDataCompetencia() {
		return dataCompetencia;
	}
	public void setDataCompetencia(Calendar dataCompetencia) {
		this.dataCompetencia = dataCompetencia;
	}
	public String getContaPrevistaCcc() {
		return contaPrevistaCcc;
	}
	public void setContaPrevistaCcc(String contaPrevistaCcc) {
		this.contaPrevistaCcc = contaPrevistaCcc;
	}
	public String getContaMovimentoCcc() {
		return contaMovimentoCcc;
	}
	public void setContaMovimentoCcc(String contaMovimentoCcc) {
		this.contaMovimentoCcc = contaMovimentoCcc;
	}
	public String getContaCompensacaoCcc() {
		return contaCompensacaoCcc;
	}
	public void setContaCompensacaoCcc(String contaCompensacaoCcc) {
		this.contaCompensacaoCcc = contaCompensacaoCcc;
	}
	public String getContratoCcc() {
		return contratoCcc;
	}
	public void setContratoCcc(String contratoCcc) {
		this.contratoCcc = contratoCcc;
	}
	public String getCentroCustoCcc() {
		return centroCustoCcc;
	}
	public void setCentroCustoCcc(String centroCustoCcc) {
		this.centroCustoCcc = centroCustoCcc;
	}
	public String getItemCustoCcc() {
		return itemCustoCcc;
	}
	public void setItemCustoCcc(String itemCustoCcc) {
		this.itemCustoCcc = itemCustoCcc;
	}
	public String getDocumentoPessoa() {
		return documentoPessoa;
	}
	public void setDocumentoPessoa(String documentoPessoa) {
		this.documentoPessoa = documentoPessoa;
	}
	public String getPagamentoCategoria() {
		return pagamentoCategoria;
	}
	public void setPagamentoCategoria(String pagamentoCategoria) {
		this.pagamentoCategoria = pagamentoCategoria;
	}
	public String getPagamentoNumero() {
		return pagamentoNumero;
	}
	public void setPagamentoNumero(String pagamentoNumero) {
		this.pagamentoNumero = pagamentoNumero;
	}
}