package br.com.orionsoft.basic.entities.commons;

import java.util.Calendar;

/**
 * Meses do ano
 * @author Lucio
 * @version 20070903
 */
public enum Mes {
	JANEIRO("Janeiro", "JAN", Calendar.JANUARY),
	FEVEREIRO("Fevereiro", "FEV", Calendar.FEBRUARY),
	MARCO("Março", "MAR", Calendar.MARCH),
	ABRIL("Abril", "ABR", Calendar.APRIL),
	MAIO("Maio", "MAI", Calendar.MAY),
	JUNHO("Junho", "JUN", Calendar.JUNE),
	JULHO("Julho", "JUL", Calendar.JULY),
	AGOSTO("Agosto", "AGO", Calendar.AUGUST),
	SETEMBRO("Setembro", "SET", Calendar.SEPTEMBER),
	OUTUBRO("Outubro", "OUT", Calendar.OCTOBER),
	NOVEMBRO("Novembro", "NOV", Calendar.NOVEMBER),
	DEZEMBRO("Dezembro", "DEZ", Calendar.DECEMBER);

	public static final int COLUMN_DISCRIMINATOR_LENGTH = 10;

	private Mes(String nome, String abreviacao, int calendarIndex)
	{
		this.nome = nome;
		this.abreviacao = abreviacao;
		this.calendarIndex = calendarIndex;
	}

	private String nome;
	private String abreviacao;
	private int calendarIndex;


	public int getCalendarIndex()
	{
		return calendarIndex;
	}

	public String getNome() {return nome;}

	public String getAbreviacao() {return abreviacao;}

}
