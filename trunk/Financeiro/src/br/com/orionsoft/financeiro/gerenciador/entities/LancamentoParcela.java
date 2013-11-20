package br.com.orionsoft.financeiro.gerenciador.entities;

import java.math.BigDecimal;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;

import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobranca;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.core.util.DecimalUtils;

@Entity
@Table(name = "financeiro_lancamento_parcela")
public class LancamentoParcela {
	/*
	 * Constantes com o nomes das propriedades da classe para serem usadas no
	 * código e evitar erro de digitação.
	 */
	public static final String NUMERO = "numero";
	public static final String DATA_LANCAMENTO = "dataLancamento";
	public static final String DATA_VENCIMENTO = "dataVencimento";
	public static final String VALOR = "valor";
	public static final String DOCUMENTO_COBRANCA = "documentoCobranca";
	public static final String LANCAMENTO_MOVIMENTO = "lancamentoMovimento";
	public static final String LANCAMENTO = "lancamento";

	private Long id = -1l;
	
	private int numero;
	
	private Calendar dataLancamento;
	private Calendar dataVencimento;

	private BigDecimal valor;

	private DocumentoCobranca documentoCobranca;
	
	private LancamentoMovimento lancamentoMovimento;
	
	private Lancamento lancamento;
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
	@Temporal(TemporalType.DATE)
	public java.util.Calendar getDataLancamento() {
		return dataLancamento;
	}

	public void setDataLancamento(java.util.Calendar dataLancamento) {
		this.dataLancamento = dataLancamento;
	}

	@Column
	public BigDecimal getValor() {
		return valor;
	}
	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	@ManyToOne
	@JoinColumn(name = LANCAMENTO)
	@ForeignKey(name = LANCAMENTO)
	public Lancamento getLancamento() {
		return lancamento;
	}

	public void setLancamento(Lancamento lancamento) {
		this.lancamento = lancamento;
	}
	
	@Column
	public int getNumero() {
		return numero;
	}

	public void setNumero(int numero) {
		this.numero = numero;
	}

	@ManyToOne
	@JoinColumn(name = DOCUMENTO_COBRANCA)
	@ForeignKey(name = DOCUMENTO_COBRANCA)
	public DocumentoCobranca getDocumentoCobranca() {
		return documentoCobranca;
	}

	public void setDocumentoCobranca(DocumentoCobranca documentoCobranca) {
		this.documentoCobranca = documentoCobranca;
	}

	@ManyToOne
	@JoinColumn(name = LANCAMENTO_MOVIMENTO)
	@ForeignKey(name = LANCAMENTO_MOVIMENTO)
	public LancamentoMovimento getLancamentoMovimento() {
		return lancamentoMovimento;
	}

	public void setLancamentoMovimento(LancamentoMovimento lancamentoMovimento) {
		this.lancamentoMovimento = lancamentoMovimento;
	}

	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append(numero);
		result.append("ª ");
		if (this.dataLancamento != null)
			result.append("L:" + CalendarUtils.formatDate(this.dataLancamento));
		if (this.dataVencimento != null)
			result.append(" V:" + CalendarUtils.formatDate(this.dataVencimento));
		result.append(" - ");
		if (this.lancamentoMovimento == null)
			result.append("(ABERTA)");
		else
			result.append("(QUITADA)");
		
		result.append(DecimalUtils.formatBigDecimal(this.valor));
		
		return result.toString();
	}
}
