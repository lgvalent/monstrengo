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


/**
 * Essa classe representa a entidade Sócio.
 * Ela deverá conter todas as informações que expressam o relacionamento
 * de uma pessoa Física com um pessoa Juridica como sócio desta.
 * @hibernate.class table="basic_socio"
 */
@Entity
@Table(name="basic_socio")
public class Socio
{
	public static final String JURIDICA = "juridica";
	public static final String FISICA = "fisica";
	public static final String CARGO = "cargo";
	public static final String DATA_ENTRADA = "dataEntrada";
	public static final String DATA_SAIDA = "dataSaida";
	
	private long id;
	private Juridica juridica;
	private Fisica fisica;
	private Cargo cargo;
	private Calendar dataEntrada;
	private Calendar dataSaida;
	
	
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
     *  @hibernate.many-to-one foreign-key="juridica"
     */
	@ManyToOne
	@JoinColumn(name="juridica")
	@ForeignKey(name="juridica")
	public Juridica getJuridica(){return juridica;}
	public void setJuridica(Juridica juridica){this.juridica = juridica;}
	
    /**
     *  @hibernate.many-to-one foreign-key="cargo"
     */
	@ManyToOne
	@JoinColumn(name="cargo")
	@ForeignKey(name="cargo")
	public Cargo getCargo(){return cargo;}
	public void setCargo(Cargo cargo){this.cargo = cargo;}
	
    /**
     *  @hibernate.property
     */
	@Column
    @Temporal(TemporalType.DATE)
	public Calendar getDataEntrada(){return dataEntrada;}
	public void setDataEntrada(Calendar dataAdmissao){this.dataEntrada = dataAdmissao;}
	
    /**
     *  @hibernate.property
     */
	@Column
    @Temporal(TemporalType.DATE)
	public Calendar getDataSaida(){return dataSaida;}
	public void setDataSaida(Calendar dataDemissao){this.dataSaida = dataDemissao;}

	public String toString()
	{
		String result = "";
		if(this.fisica!=null)
			result += this.fisica.getNome(); 

		if(this.cargo!=null)
			result += " ("+this.cargo.getNome()+")";

		return result;
	}

}
