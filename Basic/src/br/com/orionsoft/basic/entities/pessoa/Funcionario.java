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
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;


/**
 * Essa classe representa a entidade Funcionários.
 * Ela deverá conter todas as informações que expressam o relacionamento
 * de uma pessoa Física com um pessoa Juridica como funcionário desta.
 * @hibernate.class table="basic_funcionario"
 */
@Entity
@Table(name="basic_funcionario")
public class Funcionario
{
	public static final String FISICA = "fisica";
	public static final String JURIDICA = "juridica";
	public static final String CARGO = "cargo";
	public static final String DATA_ADMISSAO = "dataAdmissao";
	public static final String DATA_DEMISSAO = "dataDemissao";
	
	private long id = -1;
	private Fisica fisica;
	private Juridica juridica;
	
	private Cargo cargo;
	private Calendar dataAdmissao;
	private Calendar dataDemissao;
	
    /**
     * @hibernate.id generator-class="native" unsaved-value="-1"
     */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public long getId(){return id;}
	public void setId(long id){this.id = id;}
	
    /**
     *  @hibernate.many-to-one foreign-key="fisica"
     */
	@ManyToOne
	@JoinColumn(name="fisica")
	@ForeignKey(name="fisica")
	public Fisica getFisica(){return fisica;}
	public void setFisica(Fisica fisica){this.fisica = fisica;}
	
	/**
     * @hibernate.many-to-one foreign-key="juridica"
     * @return
     */
	@ManyToOne
	@JoinColumn(name="juridica")
	@ForeignKey(name="juridica")
	public Juridica getJuridica() {return juridica;}
	public void setJuridica(Juridica juridica) {this.juridica = juridica;}
	
	/**
     *  @hibernate.property
     */
	@Column
    @Temporal(TemporalType.DATE)
	public Calendar getDataAdmissao(){return dataAdmissao;}
	public void setDataAdmissao(Calendar dataAdmissao){this.dataAdmissao = dataAdmissao;}
	
    /**
     *  @hibernate.property
     */
	@Column
    @Temporal(TemporalType.DATE)
	public Calendar getDataDemissao(){return dataDemissao;}
	public void setDataDemissao(Calendar dataDemissao){this.dataDemissao = dataDemissao;}
	
    /**
     *  Propriedade calculada
     */
	@Transient
	public boolean isDemitido(){return dataDemissao != null;}
	
    /**
     *  @hibernate.many-to-one foreign-key="cargo"
     */
	@ManyToOne
	@JoinColumn(name="cargo")
	@ForeignKey(name="cargo")
	public Cargo getCargo(){return cargo;}
	public void setCargo(Cargo cargo){this.cargo = cargo;}
	
	public String toString()
	{
		String result = "";
		result += this.fisica.getNome();

		if(this.cargo!=null)
			result += " ("+this.cargo.getNome()+")";

		return result;
	}

}
