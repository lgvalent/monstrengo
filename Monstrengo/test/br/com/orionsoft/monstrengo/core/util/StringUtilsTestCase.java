package br.com.orionsoft.monstrengo.core.util;

import java.math.BigDecimal;

import junit.framework.TestCase;
import br.com.orionsoft.monstrengo.core.util.StringUtils;

public class StringUtilsTestCase extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(StringUtilsTestCase.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testFormat() {
        String correta = "0000002";
    	
    	String str = "2";
        System.out.println();
        System.out.println("STR VALUE: " + str);
        System.out.println("STR LENGTH: " + str.length());
        System.out.println();
        String strFormat = br.com.orionsoft.monstrengo.core.util.StringUtils.formatNumber(str, 7, true);
        System.out.println("STR_FORMAT VALUE: " + strFormat);
        System.out.println("STR_FORMAT LENGTH: " + strFormat.length());
        
        assertEquals(strFormat, correta);
        
        System.out.println();
        String str2 = "PÃO - caçada - seqüência - aviÃo - teste - não - têm - NÃO TÊM";
        System.out.println(str2);
        System.out.println("Testando removeAccent()");
        System.out.println(br.com.orionsoft.monstrengo.core.util.StringUtils.removeAccent(str2));
        
        System.out.println();
        System.out.println("Testando removeAccent() juntamente com UpperCase()");
        System.out.println(br.com.orionsoft.monstrengo.core.util.StringUtils.removeAccent(str2).toUpperCase());
        
        String str3 = null;
        System.out.println();
        System.out.println("Testando removeAccent() juntamente com UpperCase()");
        str3 = org.apache.commons.lang.StringUtils.isEmpty(str3)?"":str3; //garantir que não é nulo
        System.out.println(br.com.orionsoft.monstrengo.core.util.StringUtils.removeAccent(str3).toUpperCase());
        
        System.out.println();
        System.out.println("BigDecimal valor: " + String.format("%f", new BigDecimal("9999999999.0000")));
        
        BigDecimal num1 = new BigDecimal("999999.45");
        System.out.println("BigDecimal: " + num1);
        System.out.println();
        System.out.println("BigDecimal formatado sem pontos e vírgulas: " + StringUtils.removeAlpha(num1.toString()));
        System.out.println();
        System.out.println("BigDecimal formatado sem pontos e vírgulas, formatado para 13 caracteres: ");
        System.out.println(StringUtils.formatNumber(StringUtils.removeAlpha(num1.toString()), 13, true));
        
        
//        BigDecimal i = new BigDecimal(59.90).setScale(8, BigDecimal.ROUND_HALF_UP);
//        System.out.println(String.valueOf(i.longValueExact()));
        
        System.out.println();
        String s1 = "12000000002";
        System.out.println("s1 formatada: "+StringUtils.formatNumber(s1, 7, false));
        
        System.out.println();
        String s2 = "Testando";
        System.out.println("Testando - size=8: " + StringUtils.formatAlpha(s2, 3, false));
        
        System.out.println();
        String s3 = "0";
        System.out.println("s3: " + StringUtils.formatAlpha(s3, 1, false));

    }

    public void testCapitalize() {
    	System.out.println(StringUtils.capitalize("lucioGRoniMo vAlentin"));
    	System.out.println(StringUtils.capitalize("l.G.v. oniMo vAlentin"));
    	System.out.println(StringUtils.capitalize("l.G.v oniMo vAlentin"));
    	System.out.println(StringUtils.capitalize("l.G.v E de da dos oniMo vAlentin"));
    	System.out.println(StringUtils.capitalize("lucas sant'ANa"));
    }

}
