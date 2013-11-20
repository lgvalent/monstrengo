package br.com.orionsoft.financeiro.gerenciador.entities;

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

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.ForeignKey;

/**
 * @author marcia
 *
 * @hibernate.class table="financeiro_conta_bancaria"
 */
@Entity
@Table(name="financeiro_conta_bancaria")
public class ContaBancaria {
    public static final String BANCO = "banco";
    public static final String AGENCIA_CODIGO = "agenciaCodigo";
    public static final String AGENCIA_DIGITO = "agenciaDigito";
    public static final String CONTA_CODIGO = "contaCodigo";
    public static final String CONTA_DIGITO = "contaDigito";
    
    private Long id = -1l;
    
    private TipoConta tipoConta = TipoConta.CONTA_CORRENTE;
    private Banco banco;
    private String agenciaCodigo;
    private String agenciaDigito;
    private String contaCodigo;
    private String contaDigito;
    
    public static enum TipoConta{
    	POUPANCA("Poupança"),
    	CONTA_CORRENTE("Conta corrente");
    	
    	public static final int MAX_LENGTH = 14;
    	
    	private String descricao;

    	private TipoConta(String descricao) {
    		this.descricao = descricao;
		}
    	
    	@Override
    	public String toString() {
    		return this.descricao;
    	}
    }
    
    /**
     * @hibernate.id generator-class="native" unsaved-value="-1"
     */
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
	@Enumerated(EnumType.STRING) @Column(length=TipoConta.MAX_LENGTH)
    public TipoConta getTipoConta() {
		return tipoConta;
	}

	public void setTipoConta(TipoConta tipoConta) {
		this.tipoConta = tipoConta;
	}
    
	/**
     * @hibernate.property length="4"
     */
    @Column(length=4)
    public String getAgenciaCodigo() {
        return agenciaCodigo;
    }
    
    public void setAgenciaCodigo(String agenciaCodigo) {
        this.agenciaCodigo = agenciaCodigo;
    }
    
    /**
     * @hibernate.property length="1"
     */
    @Column(length=1)
    public String getAgenciaDigito() {
        return agenciaDigito;
    }
    
    public void setAgenciaDigito(String agenciaDigito) {
        this.agenciaDigito = agenciaDigito;
    }
    
    /**
     * @hibernate.many-to-one foreign-key="banco"
     */
    @ManyToOne
	@JoinColumn(name="banco")
	@ForeignKey(name="banco")
    public Banco getBanco() {
        return banco;
    }
    
    public void setBanco(Banco banco) {
        this.banco = banco;
    }
    
    /**
     * @hibernate.property length="7"
     */
    @Column(length=7)
    public String getContaCodigo() {
        return contaCodigo;
    }
    
    public void setContaCodigo(String contaCodigo) {
        this.contaCodigo = contaCodigo;
    }
    
    /**
     * @hibernate.property length="1"
     */
    @Column(length=1)
    public String getContaDigito() {
        return contaDigito;
    }
    
    public void setContaDigito(String contaDigito) {
        this.contaDigito = contaDigito;
    }
    
    public String toString(){
    	String result = "";
    	
    	if(this.banco != null)
    		result += "Bc: " + this.banco.getNome();
    	
    	if(!StringUtils.isBlank(this.agenciaCodigo))
    		result += " / " + "Ag: " + this.agenciaCodigo;
    	if(!StringUtils.isBlank(this.agenciaDigito))
    		result += "-" + this.agenciaDigito;

    	if(!StringUtils.isBlank(this.contaCodigo))
    		result += " / " + "Cc: " + this.contaCodigo;
    	if(!StringUtils.isBlank(this.contaDigito))
    		result += "-" + this.contaDigito;
    	
    	return result;
    }
}
