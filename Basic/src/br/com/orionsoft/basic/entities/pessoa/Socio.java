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
	public static final String DATA_NASCIMENTO = "dataNascimento";
	public static final String NOME = "nome";
	
	private long id;
	private Juridica juridica;
	private Fisica fisica;
	private Cargo cargo;
	private Calendar dataEntrada;
	private Calendar dataSaida;
	private Calendar dataNascimento;
	private String nome;
	

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public long getId(){return id;}
	public void setId(long id){this.id = id;}
	
	@ManyToOne
	@JoinColumn(name="fisica")
	@ForeignKey(name="fisica")
	public Fisica getFisica(){return fisica;}
	public void setFisica(Fisica fisica){this.fisica = fisica;}
	
	@ManyToOne
	@JoinColumn(name="juridica")
	@ForeignKey(name="juridica")
	public Juridica getJuridica(){return juridica;}
	public void setJuridica(Juridica juridica){this.juridica = juridica;}
	
	@ManyToOne
	@JoinColumn(name="cargo")
	@ForeignKey(name="cargo")
	public Cargo getCargo(){return cargo;}
	public void setCargo(Cargo cargo){this.cargo = cargo;}
	
	@Column
    @Temporal(TemporalType.DATE)
	public Calendar getDataEntrada(){return dataEntrada;}
	public void setDataEntrada(Calendar dataAdmissao){this.dataEntrada = dataAdmissao;}
	
	@Column
    @Temporal(TemporalType.DATE)
	public Calendar getDataSaida(){return dataSaida;}
	public void setDataSaida(Calendar dataDemissao){this.dataSaida = dataDemissao;}

	@Column
    @Temporal(TemporalType.DATE)
	public Calendar getDataNascimento(){
		if(fisica != null)
			return fisica.getDataInicial();
		return dataNascimento;
	}
	public void setDataNascimento(Calendar dataNascimento){
		if(fisica == null)
			this.dataNascimento = dataNascimento;
	}

    @Column(length=255)
    public String getNome() {
		if(fisica != null)
			return fisica.getNome();
    	return nome;
    }
    public void setNome(String nome) {
		if(fisica == null)
			this.nome = nome;
    }
	
	public String toString()
	{
		String result = getNome(); 

		if(this.cargo!=null)
			result += " ("+this.cargo.getNome()+")";

		return result;
	}

}
