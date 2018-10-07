package br.com.orionsoft.monstrengo.core.util;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;

import org.junit.Test;

import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import junit.framework.TestCase;

public class CalendarUtilsTestCase extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(CalendarUtilsTestCase.class);
    }

    @Test
    public void testDiffDay() {
        Calendar dataMenor, dataMaior;
        
        dataMenor = CalendarUtils.getCalendar(2006, Calendar.JANUARY, 15);
        dataMaior = CalendarUtils.getCalendar(2006, Calendar.JANUARY, 31);
        assertEquals(CalendarUtils.diffDay(dataMenor, dataMaior), -16l);
        
        dataMenor = CalendarUtils.getCalendar(2006, Calendar.JANUARY, 15);
        dataMaior = CalendarUtils.getCalendar(2006, Calendar.JANUARY, 31);
        assertEquals(CalendarUtils.diffDay(dataMaior, dataMenor), 16l);
        
        dataMenor = CalendarUtils.getCalendar(2006, Calendar.JANUARY, 15);
        dataMaior = CalendarUtils.getCalendar(2006, Calendar.JANUARY, 15);
        assertEquals(CalendarUtils.diffDay(dataMenor, dataMaior), 0l);
        
        dataMenor = CalendarUtils.getCalendar(2006, Calendar.JANUARY, 31);
        dataMaior = CalendarUtils.getCalendar(2007, Calendar.JANUARY, 31);
        assertEquals(CalendarUtils.diffDay(dataMaior, dataMenor), 365l);
        
        dataMenor = CalendarUtils.getCalendar(2006, Calendar.JANUARY, 15);
        dataMaior = CalendarUtils.getCalendar(2006, Calendar.FEBRUARY, 15);
        assertEquals(CalendarUtils.diffDay(dataMaior, dataMenor), 31l);
        
        dataMenor = CalendarUtils.getCalendar(2006, Calendar.FEBRUARY, 15);
        dataMaior = CalendarUtils.getCalendar(2006, Calendar.MARCH, 15);
        assertEquals(CalendarUtils.diffDay(dataMaior, dataMenor), 28l);
        
        dataMenor = CalendarUtils.getCalendar(2008, Calendar.FEBRUARY, 15);
        dataMaior = CalendarUtils.getCalendar(2008, Calendar.MARCH, 15);
        assertEquals(CalendarUtils.diffDay(dataMaior, dataMenor), 29l);
    }
    
    @Test
    public void testGetCalendar() {
        assertEquals("08/12/2006 00:00:00", CalendarUtils.getCalendar(2006, Calendar.DECEMBER, 8).getTime().toLocaleString());
        assertEquals("08/12/2006 00:00:00", CalendarUtils.getCalendar("08/12/2006").getTime().toLocaleString());
    }

}
