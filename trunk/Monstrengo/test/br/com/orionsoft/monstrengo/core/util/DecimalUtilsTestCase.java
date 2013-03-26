package br.com.orionsoft.monstrengo.core.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import br.com.orionsoft.monstrengo.core.util.DecimalUtils;

public class DecimalUtilsTestCase {

	@Test
    public void testGetBigDecimalStringInt() {
		assertEquals("0.00", DecimalUtils.ZERO.toString());
		assertEquals("1.00", DecimalUtils.ONE.toString());
		assertEquals("10.00", DecimalUtils.TEN.toString());
		
        String val = "12";
        assertEquals("0.012", DecimalUtils.getBigDecimal(val, 3).toString());
        
        val = "000125400145850";
        assertEquals("1254001458.50", DecimalUtils.getBigDecimal(val, 2).toString());
        
        val = "000000000000009";
        assertEquals("0.09", DecimalUtils.getBigDecimal(val).toString());
        
        val = "000000000000105";
        assertEquals("1.05", DecimalUtils.getBigDecimal(val, 2).toString());
        
        val = "000000000001057";
        assertEquals("10.57", DecimalUtils.getBigDecimal(val).toString());
        
        val = "1.014";
        assertEquals("1.01", DecimalUtils.getBigDecimal(val, 2).toString());
        
        val = "1.014";
        assertEquals("1.01", DecimalUtils.getBigDecimal(val).toString());
        
    }

}
