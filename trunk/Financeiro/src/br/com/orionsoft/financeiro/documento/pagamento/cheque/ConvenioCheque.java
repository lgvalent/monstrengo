package br.com.orionsoft.financeiro.documento.pagamento.cheque;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.ForeignKey;

import br.com.orionsoft.financeiro.documento.pagamento.ConvenioPagamento;
import br.com.orionsoft.financeiro.gerenciador.entities.ContaBancaria;

/** 
 * @author Lucio
 * @hibernate.joined-subclass table="financeiro_cheque_convenio"
 * @hibernate.joined-subclass-key foreign-key="PRIMARY"
 */
@Entity
@DiscriminatorValue("CHQ")
public class ConvenioCheque extends ConvenioPagamento{

    public static final String CONTA_BANCARIA = "contaBancaria";
    public static final String CHEQUE_MODELO = "chequeModelo";
    public static final String PROXIMO_NUMERO_CHEQUE = "proximoNumeroCheque";
    
    private ContaBancaria contaBancaria;
    private ChequeModelo chequeModelo;
    private long proximoNumeroCheque;
    
    /**
     * @hibernate.many-to-one foreign-key="contaBancaria" 
     */
	@ManyToOne
	@JoinColumn(name = "contaBancaria")
	@ForeignKey(name = "contaBancaria")
    public ContaBancaria getContaBancaria() {return contaBancaria;}
    public void setContaBancaria(ContaBancaria contaBancaria) {this.contaBancaria = contaBancaria;}

	 /**
     * @hibernate.many-to-one foreign-key="chequeModelo"
     */
    @ManyToOne
	@JoinColumn(name = "chequeModelo")
	@ForeignKey(name = "chequeModelo")
	public ChequeModelo getChequeModelo() {return chequeModelo;}
	public void setChequeModelo(ChequeModelo chequeModelo) {this.chequeModelo = chequeModelo;}
    
	 /**
     * @hibernate.property
     */
	@Column
	public long getProximoNumeroCheque() {return proximoNumeroCheque;}
	public void setProximoNumeroCheque(long proximoNumeroCheque) {this.proximoNumeroCheque = proximoNumeroCheque;}

	public String toString() {
        String result="ConvenioCheque ";
        result += this.getId();
        result += ": " + this.getNome();
        
        return result;
    }
}
