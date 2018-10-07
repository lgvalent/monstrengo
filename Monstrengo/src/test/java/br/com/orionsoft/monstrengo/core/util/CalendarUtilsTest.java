package br.com.orionsoft.monstrengo.core.util;

import java.util.Calendar;

import br.com.orionsoft.monstrengo.core.util.CalendarUtils;

public class CalendarUtilsTest {

    public static void main(String[] args) throws Exception {
    	testDiffMonth();
    	
    	if(true) return;
    	
        System.out.println(CalendarUtils.formatDateTime(CalendarUtils.getCalendarFirstMonthDay()) + "-" + CalendarUtils.formatDateTime(CalendarUtils.getCalendarLastMonthDay()));
        System.out.println(CalendarUtils.formatDateTime(CalendarUtils.getCalendarFirstYearDay()) + "-" + CalendarUtils.formatDateTime(CalendarUtils.getCalendarLastYearDay()));
    	
    	
        Calendar data1 = CalendarUtils.parseTime("12:30");
        System.out.println(CalendarUtils.formatDateTime(data1));
        System.out.println(CalendarUtils.formatToSQLDateTime(data1));
//        data1 = CalendarUtils.parseTime("5:30");
//        System.out.println(CalendarUtils.formatTime(data1));
//        data1 = CalendarUtils.parseTime("21/10/2007");
//        System.out.println(CalendarUtils.formatTime(data1));

    
    }

    public static void main__(String[] args) {
        long diferenca = 0;
        
        Calendar data1 = Calendar.getInstance();

        Calendar data2 = Calendar.getInstance();
        data2.add(Calendar.MINUTE, 2);
        
        System.out.println(CalendarUtils.diffTime(data1, data2));
    }

    public static void testDiffMonth() {
        
        Calendar data1 = Calendar.getInstance();

        Calendar data2 = Calendar.getInstance();
        data2.add(Calendar.MONTH, 26);
        
        System.out.println(CalendarUtils.diffMonth(data1, data2));
    }

    public static void main_(String[] args) {
        try {
            long diferenca = 0;
            
            Calendar data1 = Calendar.getInstance();
            data1.clear();
            data1.set(2000, Calendar.JULY, 3);

            Calendar data2 = Calendar.getInstance();
            data2.clear();
            data2.set(2006, Calendar.APRIL, 28);

            diferenca = (data2.getTimeInMillis() / 1000 / 60 / 60 / 24)
                    - (data1.getTimeInMillis() / 1000 / 60 / 60 / 24)
                    + 1000;
            

            System.out.println(diferenca);
            System.out.println(data1.getTimeInMillis() / 1000 / 60 / 60 / 24);
            System.out.println(data2.getTimeInMillis() / 1000 / 60 / 60 / 24);
            
//            diferenca = (data1.getTimeInMillis()/1000/60/60/24) + 2996;
//            Calendar data3 = Calendar.getInstance();
//            data3.clear();
//            data3.setTimeInMillis(diferenca);
//            
//            System.out.println(data3.getTime());
            
            Calendar data3 = Calendar.getInstance();
            String nossoNumero = "06000001";
            int ano = Integer.parseInt(nossoNumero.substring(0,2)) + 2000;
            System.out.println("ano string: "+ano);
            System.out.println("ano calendar: "+data3.get(Calendar.YEAR));
            if (ano == data3.get(Calendar.YEAR)){
                System.out.println("Comparação OK");
                String sequencialString = nossoNumero.substring(2,nossoNumero.length());
                System.out.println("Nosso Numero posicoes de 2-6: "+sequencialString);
                int sequencialInt = Integer.parseInt(sequencialString);
                sequencialInt += 1;
                System.out.println("Sequencial em Integer: "+sequencialInt);
                sequencialString = String.format("%06d", sequencialInt);
                System.out.println("Sequencial em String: "+sequencialString);
                
                sequencialString = String.format("%02d",(data3.get(Calendar.YEAR) - 2000)) + sequencialString;
                
                System.out.println("Codigo inteiro: " + sequencialString);
            }
            
            Calendar data4 = CalendarUtils.getCalendar();
            data4.set(2006, Calendar.SEPTEMBER, 9);
            Calendar data5 = CalendarUtils.getCalendar();
            data5.set(2006, Calendar.SEPTEMBER, 8);
            //diffday: 1a data do parametro - 2a data do parametro
            //caso a 1a data seja anterior, o valor será negativo
            System.out.println("diferença: "+CalendarUtils.diffDay(data4, data5));
            
        } catch (Exception e) {}
    }

}
