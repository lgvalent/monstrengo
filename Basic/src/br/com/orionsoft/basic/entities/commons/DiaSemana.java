package br.com.orionsoft.basic.entities.commons;

import java.util.Calendar;


/**
 * Dia da semana é um Enum
 * @author Lucio
 * @version 20070830
 */
public enum DiaSemana
{
	DOMINGO("Domingo", "DOM", Calendar.SUNDAY),
	SEGUNDA("Segunda-feira", "SEG", Calendar.MONDAY),
	TERCA("Terça-feira", "TER", Calendar.TUESDAY),
	QUARTA("Quarta-feira", "QUA", Calendar.WEDNESDAY),
	QUINTA("Quinta-feira", "QUI", Calendar.THURSDAY),
	SEXTA("Sexta-feira", "SEX", Calendar.FRIDAY),
	SABADO("Sábado", "SÁB", Calendar.SATURDAY);

	public static final int COLUMN_DISCRIMINATOR_LENGTH = 7;
	
    private String nome;
    private String abreviaca;
    private int calendarIndex;
    
	private DiaSemana(String nome, String abreviaca, int calendarIndex){
    	this.nome = nome;
    	this.abreviaca = abreviaca;
    	this.calendarIndex = calendarIndex;
    }
    
    public String getNome(){return nome;}
    
    public int getCalendarIndex(){return calendarIndex;}

    public String getAbreviaca(){return abreviaca;}
    
    public String toString()
    {
        return this.nome;
    }
}
