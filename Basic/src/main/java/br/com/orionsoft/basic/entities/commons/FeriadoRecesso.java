package br.com.orionsoft.basic.entities.commons;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
@DiscriminatorValue("R")
public class FeriadoRecesso extends Feriado{

	public static final String DIA_FINAL = "diaFinal";
	public static final String MES_FINAL = "mesFinal";
	public static final String ANO_FINAL = "anoFinal";

	private Integer diaFinal;
	private Mes mesFinal;
	private Integer anoFinal;

	@Column
	public Integer getDiaFinal() {return diaFinal;}
	public void setDiaFinal(Integer diaFinal) {this.diaFinal = diaFinal;}

	@Column
	public Integer getAnoFinal() {return anoFinal;}
	public void setAnoFinal(Integer anoFinal) {this.anoFinal = anoFinal;}

	@Enumerated(EnumType.STRING)
	@Column(length=Mes.COLUMN_DISCRIMINATOR_LENGTH)
	public Mes getMesFinal() {return mesFinal;}
	public void setMesFinal(Mes mesFinal) {this.mesFinal = mesFinal;}

}
