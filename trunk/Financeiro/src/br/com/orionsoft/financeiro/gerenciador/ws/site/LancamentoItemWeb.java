package br.com.orionsoft.financeiro.gerenciador.ws.site;

import java.math.BigDecimal;

import br.com.orionsoft.financeiro.gerenciador.entities.CentroCusto;
import br.com.orionsoft.financeiro.gerenciador.entities.ClassificacaoContabil;
import br.com.orionsoft.financeiro.gerenciador.entities.ItemCusto;
import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoItem;

public class LancamentoItemWeb {

	private Long id = -1l;
	private String descricao;
	private BigDecimal peso;
	private BigDecimal valor;
	private String centroCusto;
	private String itemCusto;
	private String classificacaoContabil;
	
	public LancamentoItemWeb(LancamentoItem item) {
		id = item.getId();
		descricao = item.getDescricao();
		peso = item.getPeso();
		valor = item.getValor();
		
		if(item.getCentroCusto() != null)
			centroCusto = item.getCentroCusto().toString();
		
		if(item.getItemCusto()!= null)
			itemCusto = item.getItemCusto().toString();
		
		if(item.getClassificacaoContabil() != null)
			classificacaoContabil = item.getClassificacaoContabil().toString();
		
	}
	
	public LancamentoItemWeb() {
		// TODO Auto-generated constructor stub
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public BigDecimal getPeso() {
		return peso;
	}

	public void setPeso(BigDecimal peso) {
		this.peso = peso;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public String getCentroCusto() {
		return centroCusto;
	}

	public void setCentroCusto(String centroCusto) {
		this.centroCusto = centroCusto;
	}

	public String getItemCusto() {
		return itemCusto;
	}

	public void setItemCusto(String itemCusto) {
		this.itemCusto = itemCusto;
	}

	public String getClassificacaoContabil() {
		return classificacaoContabil;
	}

	public void setClassificacaoContabil(String classificacaoContabil) {
		this.classificacaoContabil = classificacaoContabil;
	}

}
