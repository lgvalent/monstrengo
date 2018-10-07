package br.com.orionsoft.basic.entities.commons;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.hibernate.annotations.ForceDiscriminator;

@Entity
@Table(name="basic_feriado")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="discriminator", discriminatorType=DiscriminatorType.STRING, length=1) @ForceDiscriminator //achar a annotation para o force
@DiscriminatorValue("F")
public class Feriado {

	public static final String DESCRICAO = "descricao";
	public static final String FIXO = "fixo";
	public static final String DIA = "dia";
	public static final String MES = "mes";
	public static final String ANO = "ano";

	private long id = -1;
	private String descricao;
	private Boolean fixo;
	private Integer dia;
	private Mes mes;
	private Integer ano;

	@Column(length=100)
	public String getDescricao() {return descricao;}
	public void setDescricao(String descricao) {this.descricao = descricao;}

	@Column
	public Integer getDia() {return dia;}
	public void setDia(Integer dia) {this.dia = dia;}

	@Column
	public Boolean getFixo() {return fixo;}
	public void setFixo(Boolean fixo) {this.fixo = fixo;}

	@Column
	public Integer getAno() {return ano;}
	public void setAno(Integer ano) {this.ano = ano;}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public long getId() {return id;}
	public void setId(long id) {this.id = id;}

	@Enumerated(EnumType.STRING)
	@Column(length=Mes.COLUMN_DISCRIMINATOR_LENGTH)
	public Mes getMes() {return mes;}
	public void setMes(Mes mes) {this.mes = mes;}

	public String toString(){
		/* 02/Janeiro - Dia de São Nunca*/
		return this.dia + "/" + this.mes.getNome() + " - " + this.descricao;
	}

}
