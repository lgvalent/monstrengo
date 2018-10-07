package br.com.orionsoft.basic.entities;

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

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.ForeignKey;

import br.com.orionsoft.basic.entities.pessoa.Representante;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;

/**
 * Esta classe permite registrar em um contrato todos os
 * contatos realizados com um contrato e manter um histórico
 * dos motivos e conclusões que um contato estabeleceu.
 * Alem disto, esta classe permite definir uma data de retorno
 * e um relatório poderá ser utilizado para lembar o operador
 * dos retornos que ele deve dar ou ainda receber do cliente.
 * 
 * @author Lucio 20060716
 * 
 * @hibernate.class table="basic_contrato_contato"
 */
@Entity
@Table(name="basic_contrato_contato")
public class ContratoContato {
    public static final String CONTRATO = "contrato";
    public static final String DATA_HORA = "dataHora";
    public static final String REPRESENTANTE = "representante";
    public static final String MOTIVO = "motivo";
    public static final String OBSERVACOES = "observacoes";
    public static final String DATA_RETORNO = "previsaoRetorno";
    public static final String RETORNO_CLIENTE = "retornoCliente";
    public static final String CONTATO_RETORNO = "contatoRetorno";
    public static final String RETORNADO = "retornado";
    
    
    private long id = -1;
    private Contrato contrato;
    private Calendar dataHora;
    private Representante representante;
    private ContratoContatoMotivo motivo;
    private String observacoes;
    private Calendar dataRetorno;
    private boolean retornoCliente=false;
    private boolean retornado=false;
    private ContratoContato contatoRetorno;
    
	/**
     * @hibernate.id generator-class="native" unsaved-value="-1"
     */
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public long getId() {return id;}
    public void setId(long id) {this.id = id;}

	/**
	 * Contrato representa uma hierarquia extensa de subclasses.
	 * Assim, um política FetchMode.SELECT evitará um JOIN extenso. 
     * @hibernate.many-to-one foreign-key="contrato"
     */
    @ManyToOne
	@Fetch(FetchMode.SELECT)
    @JoinColumn(name="contrato")
    @ForeignKey(name="contrato")
    public Contrato getContrato() {return contrato;}
	public void setContrato(Contrato contrato) {this.contrato = contrato;}
	
	/**
     * @hibernate.property
     */
    @Temporal(TemporalType.TIMESTAMP)
    public Calendar getDataHora() {return dataHora;}
	public void setDataHora(Calendar dataHora) {this.dataHora = dataHora;}
	
	/**
	 * Representante representa uma hierarquia extensa de subclasses.
	 * Assim, um política FetchMode.SELECT evitará um JOIN extenso 
     * @hibernate.many-to-one foreign-key="representante"
     */
	@ManyToOne
	@Fetch(FetchMode.SELECT)
	@JoinColumn(name="representante")
	@ForeignKey(name="representante")
	public Representante getRepresentante() {return representante;}
	public void setRepresentante(Representante representante) {this.representante = representante;}

	/**
     * @hibernate.property
     */
    @Temporal(TemporalType.TIMESTAMP)
	public Calendar getDataRetorno() {return dataRetorno;}
	public void setDataRetorno(Calendar dataRetorno) {this.dataRetorno = dataRetorno;}
	
	/**
     * @hibernate.many-to-one foreign-key="motivo"
     */
	@ManyToOne
	@JoinColumn(name="motivo")
	@ForeignKey(name="motivo")
	public ContratoContatoMotivo getMotivo() {return motivo;}
	public void setMotivo(ContratoContatoMotivo motivo) {this.motivo = motivo;}
	
	/**
     * @hibernate.property length="255"
     */
	@Column(length=255)
	public String getObservacoes() {return observacoes;}
	public void setObservacoes(String observacoes) {this.observacoes = observacoes;}
	
	/**
     * @hibernate.property
     */
	@Column
	public boolean isRetornoCliente() {return retornoCliente;}
	public void setRetornoCliente(boolean retornoCliente) {this.retornoCliente = retornoCliente;}
	
	/**
     * @hibernate.property
     */
	@Column
	public boolean isRetornado() {return retornado;}
	public void setRetornado(boolean retornado) {this.retornado = retornado;}
	
	/**
     * @hibernate.many-to-one foreign-key="contatoRetorno"
     */
	@ManyToOne
	@JoinColumn(name="contatoRetorno")
	@ForeignKey(name="contatoRetorno")
	public ContratoContato getContatoRetorno() {return contatoRetorno;}
	public void setContatoRetorno(ContratoContato contatoRetorno) {this.contatoRetorno = contatoRetorno;}
	
    public String toString() {
        String result="";
        
        if(dataHora!=null)
        	result+=CalendarUtils.formatDateTime(dataHora) + ": ";

        if(motivo!=null)
        	result+=motivo.toString() + " - ";
        
        if(observacoes!=null)
        	result+=observacoes.toString();

        return result;
    }
}
