package br.com.orionsoft.monstrengo.core.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import br.com.orionsoft.monstrengo.core.util.CalendarUtils;

/**
 * Classe que fornece métodos utilitários para tratamento de datas,
 *  como por exemplo a diferença de dias entre duas datas.
 * @author Antonio Alvefs 2005/12/19
 * @version 20060418
 */
public class CalendarUtils {

	public static final String defaultFormat = "dd/MM/yyyy";

	public static final String defaultDateTime = "dd/MM/yyyy HH:mm:ss";

	public static final String defaultTime = "HH:mm";

	/**
     * Retorna uma instância de Calendar sem dados de horário.
     * @param Date date
     * @return Calendar
     */
    public static Calendar getCalendar(String date) {
    	if (StringUtils.isBlank(date))
    		return null;
        Calendar calendar = null;
        try {
        	calendar = getCalendar(parseDate(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }

    /**
     * Retorna uma instância de Calendar sem dados de horário.
     * @param year - Ano.
     * @param month - Mês.
     * @param date - Dia.
     * @return Calendar.
     */
    public static Calendar getCalendar(int year, int month, int date) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, date, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    /**
     * Retorna uma instância de Calendar somente com os dados do horário.
     * @param year - Ano.
     * @param month - Mês.
     * @param date - Dia.
     * @return Calendar.
     */
    public static Calendar getCalendarTime(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.HOUR, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    /**
     * Retorna uma instância de Calendar sem dados de horários.
     * @param date
     * @return Calendar
     */
    public static Calendar getCalendar(Date date) {
        if (date == null)
        	return null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    /**
     * Retorna uma instância de Calendar sem dados de horário
     * @return Retorna um Calendar com HH:mm:ss = 00:00:00
     */
    public static Calendar getCalendar() {
        Calendar calendar = Calendar.getInstance();
        return getCalendar(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DATE));
    }
    
    /**
     * Retorna o primeiro dia do mês corrente.
     * Útil para dataInicial de relatórios
     * @author lucio 20120822
     * @return
     */
    public static Calendar getCalendarFirstMonthDay(){
    	Calendar result = getCalendar();
		result.set(Calendar.DATE, result.getActualMinimum(Calendar.DATE));
		return result;
    }

    /**
     * Retorna o último dia do mês corrente
     * Útil para dataFinal de relatórios
     * @author lucio 20120822
     * @return
     */
    public static Calendar getCalendarLastMonthDay(){
    	Calendar result = getCalendar();
		result.set(Calendar.DATE, result.getActualMaximum(Calendar.DATE));
		return result;
    }

    /**
     * Retorna o primeiro dia do ano corrente.
     * Útil para dataInicial de relatórios
     * @author lucio 20131016
     * @return
     */
    public static Calendar getCalendarFirstYearDay(){
    	Calendar result = getCalendar();
    	result.set(Calendar.MONTH, result.getActualMinimum(Calendar.MONTH));
		result.set(Calendar.DATE, result.getActualMinimum(Calendar.DATE));
		return result;
    }

    /**
     * Retorna o último dia do ano corrente
     * Útil para dataFinal de relatórios
     * @author lucio 20131016
     * @return
     */
    public static Calendar getCalendarLastYearDay(){
    	Calendar result = getCalendar();
		result.set(Calendar.MONTH, result.getActualMaximum(Calendar.MONTH));
		result.set(Calendar.DATE, result.getActualMaximum(Calendar.DATE));
		return result;
    }

    /**
     * Retorna uma instância de Calendar somente com os dados do horário
     * @return Retorna um Calendar com HH:mm:ss = 00:00:00
     */
    public static Calendar getCalendarTime() {
        Calendar calendar = Calendar.getInstance();
        return getCalendarTime(calendar.get(Calendar.HOUR),
                calendar.get(Calendar.MINUTE));
    }

    /**
     * Retorna um Calendar adicionando a quantidade ao campo especificado. Aceita valores negativos.
     *
     * @param data - uma instância de <code>Calendar</code>.
     * @param campo - um campo de Calendar.
     * @param quantidade - a quantidade a ser acrescentada/diminuida de <code>campo</code>.
     * @return uma nova instância de <code>Calendar</code>.
     * @deprecated Esta função deve ser substituida pela Calendar.roll() e Calendar.add().
     */
    public static Calendar roll(Calendar data, int campo, int quantidade) {
        Calendar temp = (Calendar) data.clone();
        temp.add(campo, quantidade);
        if (temp.get(Calendar.DATE) != data.get(Calendar.DATE)) {
            int ano = temp.get(Calendar.YEAR);
            temp = (Calendar) data.clone();
            temp.roll(campo, quantidade);
            temp.set(Calendar.YEAR, ano);
        }
        return temp;
    }

    /**
     * Calcula a diferença de dias entre duas datas.
     * @param arg0
     * @param arg1
     * @return Diferença. Positivo se arg0 for maior que arg1. Negativo se arg1 for maior que arg0. Zero se forem iguais.
     */
    public static long diffDay(Calendar arg0, Calendar arg1) {
        long diferenca = 0;
        try {
            Calendar data0 = Calendar.getInstance();
            data0.clear();
            data0.set(arg0.get(Calendar.YEAR), arg0.get(Calendar.MONTH), arg0.get(Calendar.DATE));

            Calendar data1 = Calendar.getInstance();
            data1.clear();
            data1.set(arg1.get(Calendar.YEAR), arg1.get(Calendar.MONTH), arg1.get(Calendar.DATE));

            diferenca = (data0.getTimeInMillis() / 1000 / 60 / 60 / 24)
                    - (data1.getTimeInMillis() / 1000 / 60 / 60 / 24);
        } catch (Exception e) {}
        return diferenca;
    }

    /**
     * Calcula a diferença de horas em minutos entre duas datas.
     * @param arg0 Data inicial
     * @param arg1 Data final
     * @return Diferença. Positivo se arg1 for maior que arg0. Negativo se arg0 for maior que arg1. Zero se forem iguais.
     */
    public static long diffTime(Calendar arg0, Calendar arg1) {
        long diferenca = 0;
        try {
            diferenca = (arg1.getTimeInMillis() / 1000 / 60)
                    - (arg0.getTimeInMillis() / 1000 / 60);
        } catch (Exception e) {}
        return diferenca;
    }

    /**
     * Formata um data em um padrão pré-definido.<br>
     * O Brasileiro: "dd/MM/yyyy"
     * @param calendar
     * @return Uma string com a data formatada no padrão da função
     * @since 2006/01/04
     */
    public static String formatDate(Calendar calendar){

    	/*Verifica se o calendar é null, se não,
    	 *chama o método formatDate(calendar.getTime());
    	 */
    	if(calendar == null) return "";
    	return formatDate(calendar.getTime());
    }

    /**
     * Formata um data em um padrão pré-definido.<br>
     * O Brasileiro: "dd/MM/yyyy"
     * @param date
     * @return Uma string com a data formatada no padrão da função
     * @since 2006/01/04
     */
    public static String formatDate(Date date){

    	/*Verifica se o date é null*/
    	if(date == null) return "";

    	// TODO IMPLEMENTAR fazer com que calendar Utils obtenha formato de um arquivo externo
    	DateFormat df = new SimpleDateFormat(defaultFormat);

    	return df.format(date);
    }

    /**
     * Formata um data em um padrão definido pelo operador.<br>
     *
     * @param format: String com o formato desejado -
     * 				  consulte a documentação: (SimpleDateFormat) http://java.sun.com/j2se/1.5.0/docs/api/java/text/SimpleDateFormat.html
     * 		  calendar: parâmetro do tipo Calendar
     * @return Uma string com a data formatada no padrão da função
     * @since 2006/08/14
     */
    public static String formatDate(String format, Calendar calendar){

    	/*Verifica se o calendar é null*/
    	if(calendar == null) return "";

    	return formatDate(format, calendar.getTime());
    }

    /**
     * Formata um data em um padrão definido pelo operador.<br>
     * O método SimpleDateFormat retorna a data formatada de acordo com
     * padrão passado pela String format.
     *
     * consulte a documentação: (SimpleDateFormat) http://java.sun.com/j2se/1.5.0/docs/api/java/text/SimpleDateFormat.html
     *
     * @param date
     * @return Uma string com a data formatada no padrão da função
     * @since 2006/08/14
     */
    public static String formatDate(String format, Date date){

    	/*Verifica se o date é null*/
    	if(date == null) return "";

    	// TODO IMPLEMENTAR fazer com que calendar Utils obtenha formato de um arquivo externo
    	DateFormat df = new SimpleDateFormat(format);

    	return df.format(date);
    }

    /**
     * Formata um data em um padrão pré-definido.<br>
     * O Brasileiro: "dd/MM/yyyy hh:mm:ss"
     * @param calendar
     * @return Uma string com a data formatada no padrão da função
     * @since 20060418
     */
    public static String formatDateTime(Calendar calendar){
    	/*Verifica se o calendar é null*/
    	if(calendar == null) return "";

    	return formatDateTime(calendar.getTime());
    }

    /**
     * Formata um data em um padrão pré-definido.<br>
     * O Brasileiro: "dd/MM/yyyy hh:mm:ss"
     * @param date
     * @return Uma string com a data formatada no padrão da função
     * @since 20060418
     */
    public static String formatDateTime(Date date){

    	/*Verifica se o date é null*/
    	if(date == null) return "";

    	// TODO IMPLEMENTAR fazer com que calendar Utils obtenha formato de um arquivo externo
    	DateFormat df = new SimpleDateFormat(defaultDateTime);

    	return df.format(date);
    }

    /**
     * Formata um data em um padrão pré-definido.<br>
     * O Brasileiro: "dd/MM/yyyy hh:mm:ss"
     * @param calendar
     * @return Uma string com a hora formatada no padrão da função
     * @since 20060418
     */
    public static String formatTime(Calendar calendar){
    	/*Verifica se o calendar é null*/
    	if(calendar == null) return "";

    	return formatTime(calendar.getTime());
    }

    /**
     * Formata um data em um padrão pré-definido.<br>
     * O Brasileiro: "dd/MM/yyyy hh:mm:ss"
     * @param date
     * @return Uma string com a hora formatada no padrão da função
     * @since 20060418
     */
    public static String formatTime(Date date){

    	/*Verifica se o date é null*/
    	if(date == null) return "";

    	// TODO IMPLEMENTAR fazer com que calendar Utils obtenha formato de um arquivo externo
    	DateFormat df = new SimpleDateFormat(defaultTime);

    	return df.format(date);
    }

    /**
     * Converte uma String que esteja no formato:<br>
     * O Brasileiro: "dd/MM/yyyy hh:mm:ss"<br>
     * Em um objeto do tipo Calendar.
     * @param date
     * @return Retorn um objeto do tipo Calendar
     * @throws ParseException
     * @since 20060418
     */
    public static Calendar parseCalendarTime(String date) throws ParseException{

        Calendar result = Calendar.getInstance();

        result.setTime(new SimpleDateFormat(defaultDateTime).parse(date));

    	return result;
    }

    /**
     * Converte uma String que esteja no formato:<br>
     * O Brasileiro: "dd/MM/yyyy"<br>
     * Em um objeto do tipo Date.
     * @param date
     * @return Retorna um objeto do tipo Date
     * @throws ParseException
     * @since 2006/01/04
     */
    public static Date parseDate(String date) throws ParseException{

    	// TODO IMPLEMENTAR fazer com que calendar Utils obtenha formato de um arquivo externo
    	return new SimpleDateFormat(defaultFormat).parse(date);
    }

    /**
     * Converte uma String que esteja no formato:<br>
     * O Brasileiro: "dd/MM/yyyy hh:mm:ss"<br>
     * Em um objeto do tipo Date.
     * @param date
     * @return Retorna um objeto do tipo Date
     * @throws ParseException
     * @since 2006/01/04
     */
    public static Date parseDateTime(String date) throws ParseException{

    	// TODO IMPLEMENTAR fazer com que calendar Utils obtenha formato de um arquivo externo
    	return new SimpleDateFormat(defaultDateTime).parse(date);
    }


    /**
     * Converte uma String que esteja no formato:<br>
     * O Brasileiro: "dd/MM/yyyy"<br>
     * Em um objeto do tipo Calendar.
     * @param date
     * @return Retorn um objeto do tipo Calendar
     * @throws ParseException
     * @since 20060316
     */
    public static Calendar parseCalendar(String date) throws ParseException{

        Calendar result = Calendar.getInstance();

        result.setTime(new SimpleDateFormat(defaultFormat).parse(date));

    	return result;
    }

    /**
     * Converte uma String que esteja no formato:<br>
     * O Brasileiro: "HH:mm"<br>
     * Em um objeto do tipo Calendar.
     * @param date
     * @return Retorn um objeto do tipo Calendar
     * @throws ParseException
     * @since 20060316
     */
    public static Calendar parseTime(String time) throws Exception{

    	if(time.length()!=5) throw new Exception("Invalid time format: " + time + ". Use 'HH:mm'");
    	int hour = Integer.parseInt(time.substring(0,2));
    	int minute = Integer.parseInt(time.substring(3,5));

    	return CalendarUtils.getCalendarTime(hour, minute);
    }

    /**
     * Este função é útil para a comparação de outras datas
     * e identificação se as datas são válidas, ou seja, se
     * são maiores de 1900. Isto porque, em alguns casos, as datas
     * são lidas de arquivos ou bancos de dados com valores: <br>
     * 0000-00-00<br>
     * 1899-01-01<br>
     * A função usa uma campo privado para evitar múltiplas
     * instânciações. Há somente uma instanciação e depois a mesma
     * é reutilizada.
     *
     * @return Retorna uma data base válida 01/01/1900;
     * @since 2006/01/04
     */
    public static Date getBaseDate(){
    	Calendar baseDate = Calendar.getInstance();
    	// TODO IMPLEMENTAR fazer com que calendar Utils obtenha data base de um arquivo externo
    	baseDate.set(1900, Calendar.JANUARY, 1);

        return baseDate.getTime();
    }

    public static Calendar getCalendarBaseDate(){
    	Calendar baseDate = Calendar.getInstance();
    	// TODO IMPLEMENTAR fazer com que calendar Utils obtenha data base de um arquivo externo
    	baseDate.set(1900, Calendar.JANUARY, 1);

        return baseDate;
    }

    public static Calendar getBarCodeBaseDate() {
        Calendar dataBase = Calendar.getInstance();
        dataBase.clear();
        dataBase.set(1997, Calendar.OCTOBER, 7);
//        dataBase.set(2000, Calendar.JULY, 3);
        return dataBase;
    }

    /**
     * Este método tenta tratar uma data fornecida
     * completa (dd/MM/yyyy) ou incompleta (dd, MM, yyyy, dd/MM, MM/yyyy) <br>
     * e gerar uma data no formato do banco de dados:<br>
     * yyyy-MM-dd, MM-dd, yyyy-MM<br>
     * O restultado poderá ser utilizado em consultas SQL que buscam por data:<br>
     * where fieldDate like "%yyyy-MM%" e etc.
     * @param date
     * @return
     * @since 20060224
     */
    public static String formatToSQLDate(String date){
        String result="";
        /* Limpa alguns caracteres especiais que não podem ser fornecidos pelo operador*/
        date = date.replace("'", "").replace("\"", "");

        /* Trata entradas sem / do tipo dd, MM ou yyyy sozinhos */
        if(!date.contains("/") && (date.length()==2||date.length()==4) && StringUtils.isNumeric(date))
            result = date;
        else
        /* Trata entradas com / do tipo dd/MM, MM/yyyy */
        if(date.contains("/"))
        {
            try {
                /* DATA COMPLETA  1/1/2006, 01/1/2006, 1/01/2006, 01/01/2006 */
                if(8<=date.length() && date.length()<=10){
                    // Tenta formatar o filtro fornecido como uma data COMPLETA
                    Date date_ = parseDate(date);
                    // Formata a data no padrão do banco
                    result = new SimpleDateFormat("yyyy-MM-dd").format(date_);
                }else
                    /* DATA DIA / MES  1/1 01/1 1/01 01/01 */
                    result = StringUtils.substringAfter(date, "/") + "-" + StringUtils.substringBefore(date, "/");
            }catch(Exception e){
                /* Retorna vazio se não conseguiu formatar a data entrada */
            }
        }

        return result;
    }

    /**
     * Este método tenta tratar uma data fornecida
     * completa (dd/MM/yyyy) ou incompleta (dd, MM, yyyy, dd/MM, MM/yyyy) <br>
     * e gerar uma data no formato do banco de dados:<br>
     * yyyy-MM-dd, MM-dd, yyyy-MM<br>
     * O restultado poderá ser utilizado em consultas SQL que buscam por data:<br>
     * where fieldDate like "%yyyy-MM%" e etc.
     * @param date
     * @return
     * @since 20060224
     */
    public static String formatToSQLDate(Calendar date){
    	/*Verifica se o date é null*/
    	if(date == null) return "";

    	return new SimpleDateFormat("yyyy-MM-dd").format(date.getTime());
    }

    /**
     * Este método formata uma data com hora do tipo Calendar
     * para um formato padrão yyyy-MM-dd HH:mm
     * @param dateTime
     * @return Retorna uma string do tipo '2007-08-28 23:59'
     * @author lucio
     * @since 20070828
     */
    public static String formatToSQLDateTime(Calendar dateTime){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dateTime.getTime());
    }

    /**
	 * Constrói uma lista de opções de meses do ano.
	 * @return
	 */
    public static List<SelectItem> getMonths(){
    	List<SelectItem> months = new ArrayList<SelectItem>(12);
		months.add(new SelectItem(Calendar.JANUARY, "Janeiro"));
		months.add(new SelectItem(Calendar.FEBRUARY, "Fevereiro"));
		months.add(new SelectItem(Calendar.MARCH, "Março"));
		months.add(new SelectItem(Calendar.APRIL, "Abril"));
		months.add(new SelectItem(Calendar.MAY, "Maio"));
		months.add(new SelectItem(Calendar.JUNE, "Junho"));
		months.add(new SelectItem(Calendar.JULY, "Julho"));
		months.add(new SelectItem(Calendar.AUGUST, "Agosto"));
		months.add(new SelectItem(Calendar.SEPTEMBER, "Setembro"));
		months.add(new SelectItem(Calendar.OCTOBER, "Outubro"));
		months.add(new SelectItem(Calendar.NOVEMBER, "Novembro"));
		months.add(new SelectItem(Calendar.DECEMBER, "Dezembro"));
		return months;
	}
    
    /**
     * Calcula a diferença de meses entre duas datas,
     * que podem ser de anos distintos mas consecutivos.
     * Exemplo: quantidade de meses entre 01/07/2007 e 01/03/2008
     * é igual a 8 meses.
     */
    public static int diffMonth(Calendar arg0, Calendar arg1){
    	int diferenca = 0;
    	
    	if(arg0.get(Calendar.YEAR) == arg1.get(Calendar.YEAR)){
    		diferenca = arg1.get(Calendar.MONTH) - arg0.get(Calendar.MONTH);
    	}else{
    		int months = Calendar.DECEMBER - arg0.get(Calendar.MONTH);
    		months += arg1.get(Calendar.MONTH) + 1; //adiciona + 1 pois o vetor de meses em Java inicia em zero
    		int years = arg1.get(Calendar.YEAR) - arg0.get(Calendar.YEAR) -1; 
    		diferenca = months + (12 * years);  
    	}
    	return diferenca;
    }
    
}
