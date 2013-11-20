package br.com.orionsoft.basic.entities.pessoa;

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

import br.com.orionsoft.basic.entities.Observacoes;

/**
 * JavaBean referente aos contadores dos representados. Um contador pode ou não
 * estar ligado a um escritorio contabil. Um representado deve ter um contador.
 * 
 * Created 2005/12/13
 * 
 * @author Marcia
 * 
 * @version 2005/12/29
 * @hibernate.class table="basic_contador"
 */
@Entity
@Table(name="basic_contador")
public class Contador {
    public static final String ESCRITORIO_CONTABIL = "escritorioContabil";
    public static final String FISICA = "fisica";
    public static final String CRC = "crc";
    public static final String DATA_CADASTRO = "dataCadastro";
    public static final String OBSERVACOES = "observacoes";

    // TODO DEFINIR propriedades desta classe
    private long id = -1;
    private EscritorioContabil escritorioContabil;
    private Fisica fisica;
    private String crc;
    private Calendar dataCadastro;
    private Observacoes observacoes;

    /**
     * @hibernate.id generator-class="native" unsaved-value="-1"
     */
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    /**
     * @hibernate.property length="10"
     */
    @Column(length=10)
    public String getCrc() {
        return crc;
    }

    public void setCrc(String crc) {
        this.crc = crc;
    }

    /**
     * @hibernate.property
     */
    @Column
    @Temporal(TemporalType.DATE)
    public Calendar getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Calendar dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    /**
     * @hibernate.many-to-one foreign-key="escritorioContabil"
     */
    @ManyToOne
    @JoinColumn(name="escritorioContabil")
    @ForeignKey(name="escritorioContabil")
    public EscritorioContabil getEscritorioContabil() {
        return escritorioContabil;
    }

    public void setEscritorioContabil(EscritorioContabil escritorioContabil) {
        this.escritorioContabil = escritorioContabil;
    }

    /**
     * @hibernate.many-to-one unique="true" foreign-key="fisica"
     */
    @ManyToOne /*@Column(unique=true)*/
    @JoinColumn(name="fisica")
    @ForeignKey(name="fisica")
    public Fisica getFisica() {
        return fisica;
    }

    public void setFisica(Fisica fisica) {
        this.fisica = fisica;
    }

    /**
     * @hibernate.many-to-one unique="true" foreign-key="observacoes"
     * @return
     */
    @ManyToOne
    @JoinColumn(name="observacoes")
    @ForeignKey(name="observacoes")
    public Observacoes getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(Observacoes observacoes) {
        this.observacoes = observacoes;
    }
    
    public String toString(){
    	String result = crc;
    	
    	if (fisica != null)
    		result += "-" + fisica.toString(); 

    	return result;
    }

	public void operation_0() {
	}
}
