package br.com.orionsoft.financeiro.gerenciador.ws.site;

import java.math.BigDecimal;
import java.util.Calendar;

import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoItem;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimento;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoSituacao;

/**
 * @author lucio
 *
 */
public class LancamentoWeb {

	private long id = -1l;
	private String contrato;
	private String contaPrevista;
	private BigDecimal valor;
	private String descricao;
	private Calendar data;
	private Calendar dataVencimento;
	private BigDecimal saldo;
	private BigDecimal saldoCorrigido;
	private String operacao;
	private LancamentoItemWeb[] lancamentoItens;
	private LancamentoMovimentoWeb[] lancamentoMovimentos;
	private String documentoCobranca;
	private String documentoPagamento;
	private boolean naoReceberAposVencimento;
	private String lancamentoSituacao;
    
	public LancamentoWeb() {}
	
	public LancamentoWeb(Lancamento lancamento) {
	    id = lancamento.getId();
		
	    if(lancamento.getContrato()!=null)
			contrato = lancamento.getContrato().toString();
		
	    if(lancamento.getContaPrevista()!=null)
			contaPrevista = lancamento.getContaPrevista().toString();
		
	    valor = lancamento.getValor();
		descricao = lancamento.getDescricao();
		data = lancamento.getData();
		dataVencimento = lancamento.getDataVencimento();
		saldo = lancamento.getSaldo();
		
		/*Lucio 20120510: Verifica se é um lançamento pendente para pegar o valor CORRIGIDO indicado no documentoCobranca.valor */
		if((lancamento.getLancamentoSituacao() == LancamentoSituacao.PENDENTE) &&
				(lancamento.getDocumentoCobranca()!=null)){
			saldoCorrigido = lancamento.getDocumentoCobranca().getValor();
		}else{
			saldoCorrigido = lancamento.getSaldo();
		}
		
		if(lancamento.getOperacao() != null)
			operacao = lancamento.getOperacao().toString();

	    int i = 0;
	    lancamentoItens = new LancamentoItemWeb[lancamento.getLancamentoItens().size()];
	    for(LancamentoItem item: lancamento.getLancamentoItens()){
	    	lancamentoItens[i++] = new LancamentoItemWeb(item);
	    }
		
	    i = 0;
	    lancamentoMovimentos = new LancamentoMovimentoWeb[lancamento.getLancamentoMovimentos().size()];
	    for(LancamentoMovimento mov: lancamento.getLancamentoMovimentos()){
	    	lancamentoMovimentos[i++] = new LancamentoMovimentoWeb(mov);
	    }

	    if(lancamento.getDocumentoCobranca()!=null)
			documentoCobranca = lancamento.getDocumentoCobranca().toString();
		
	    if(lancamento.getDocumentoPagamento()!=null)
			documentoPagamento = lancamento.getDocumentoPagamento().toString();
		
		naoReceberAposVencimento = lancamento.isNaoReceberAposVencimento();
		lancamentoSituacao = lancamento.getLancamentoSituacao().toString();

	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getContaPrevista() {
		return contaPrevista;
	}

	public void setContaPrevista(String contaPrevista) {
		this.contaPrevista = contaPrevista;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Calendar getData() {
		return data;
	}

	public void setData(Calendar data) {
		this.data = data;
	}

	public Calendar getDataVencimento() {
		return dataVencimento;
	}

	public void setDataVencimento(Calendar dataVencimento) {
		this.dataVencimento = dataVencimento;
	}

	public BigDecimal getSaldo() {
		return saldo;
	}

	public void setSaldo(BigDecimal saldo) {
		this.saldo = saldo;
	}
	
	public BigDecimal getSaldoCorrigido() {
		return saldoCorrigido;
	}

	public void setSaldoCorrigido(BigDecimal saldoCorrigido) {
		this.saldoCorrigido = saldoCorrigido;
	}

	public String getOperacao() {
		return operacao;
	}

	public void setOperacao(String operacao) {
		this.operacao = operacao;
	}

	public LancamentoItemWeb[] getLancamentoItens() {
		return lancamentoItens;
	}

	public void setLancamentoItens(LancamentoItemWeb[] lancamentoItens) {
		this.lancamentoItens = lancamentoItens;
	}

	public LancamentoMovimentoWeb[] getLancamentoMovimentos() {
		return lancamentoMovimentos;
	}

	public void setLancamentoMovimentos(
			LancamentoMovimentoWeb[] lancamentoMovimentos) {
		this.lancamentoMovimentos = lancamentoMovimentos;
	}

	public String getDocumentoCobranca() {
		return documentoCobranca;
	}

	public void setDocumentoCobranca(String documentoCobranca) {
		this.documentoCobranca = documentoCobranca;
	}

	public String getDocumentoPagamento() {
		return documentoPagamento;
	}

	public void setDocumentoPagamento(String documentoPagamento) {
		this.documentoPagamento = documentoPagamento;
	}

	public boolean isNaoReceberAposVencimento() {
		return naoReceberAposVencimento;
	}

	public void setNaoReceberAposVencimento(boolean naoReceberAposVencimento) {
		this.naoReceberAposVencimento = naoReceberAposVencimento;
	}

	public String getLancamentoSituacao() {
		return lancamentoSituacao;
	}

	public void setLancamentoSituacao(String lancamentoSituacao) {
		this.lancamentoSituacao = lancamentoSituacao;
	}

	public String getContrato() {
		return contrato;
	}

	public void setContrato(String contrato) {
		this.contrato = contrato;
	}
}
