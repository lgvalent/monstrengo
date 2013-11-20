package br.com.orionsoft.financeiro.documento.pagamento.cheque;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.ForeignKey;

import br.com.orionsoft.financeiro.documento.pagamento.DocumentoPagamento;
import br.com.orionsoft.financeiro.gerenciador.entities.Banco;

/**
 * Classe de persistencia que descreve o documento tipo cheque.
 * 
 * @hibernate.joined-subclass table="financeiro_cheque"
 * @hibernate.joined-subclass-key foreing-key="PRIMARY"
 */
@Entity
@DiscriminatorValue("CHQ")
public class DocumentoCheque extends DocumentoPagamento {

	public static final String BANCO = "banco";
	public static final String AGENCIA = "agencia";
	public static final String CONTA_CORRENTE = "contaCorrente";
	public static final String CONTA_CORRENTE_DIGITO = "contaCorrenteDigito";

	private Banco banco;
	private String agencia; // 4
	private String contaCorrente; // 8
	private String contaCorrenteDigito; // 2

	/**
	 * @hibernate.property length="4"
	 */
	@Column(length=4)
	public String getAgencia() {
		return agencia;
	}

	public void setAgencia(String agencia) {
		this.agencia = agencia;
	}

	/**
	 * @hibernate.many-to-one foreign-key="banco"
	 */
	@ManyToOne
	@JoinColumn(name = "banco")
	@ForeignKey(name = "banco")
	public Banco getBanco() {
		return banco;
	}

	public void setBanco(Banco banco) {
		this.banco = banco;
	}

	/**
	 * @hibernate.property length="8"
	 */
	@Column(length=8)
	public String getContaCorrente() {
		return contaCorrente;
	}

	public void setContaCorrente(String contaCorrente) {
		this.contaCorrente = contaCorrente;
	}

	/**
	 * @hibernate.property length="2"
	 */
	@Column(length=2)
	public String getContaCorrenteDigito() {
		return contaCorrenteDigito;
	}

	public void setContaCorrenteDigito(String contaCorrenteDigito) {
		this.contaCorrenteDigito = contaCorrenteDigito;
	}

	public String toString() {
		String result = "";

		if (this.banco != null)
			result += "BC:" + banco.getCodigo();

		if (this.banco != null)
			result += " AG:" + this.agencia;
		
		if (this.contaCorrente != null)
			result += " CC:" + this.contaCorrente;
		
		if (this.contaCorrenteDigito != null)
			result += "-" + this.contaCorrenteDigito;

		result += " Nº:" + (this.getNumeroDocumento()==null?"S/N":this.getNumeroDocumento());

		return result;
	}
}
