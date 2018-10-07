package br.com.orionsoft.monstrengo.core.util;

import org.apache.commons.lang.WordUtils;


/*
 * Created on 11/08/2006
 *
 */

/**
 * Classe utilit�ria para tratamento de Strings, como formata��o por exemplo.
 * @author andre
 * @version 20060811
 */
public class StringUtils {
	
	private static char[] comAcento = {'�','�','�','�','�','�','�','�','�','�','�','�','�','�','�','�','�','�','�','�','�','�','�','�','�','�','�','�'};
	private static char[] semAcento = {'a','a','e','o','u','a','o','a','e','i','o','u','c','u','A','A','E','O','U','A','O','A','E','I','O','U','C','U'};
	
	/**
	 * <p>Formata uma String com o tamanho (size) passado por par�metro. Caso o tamanho da String
	 * seja menor que o tamanho passado em size, completa a String resultante com o valor 
	 * passado em fillWith. Verifica tamb�m se o tipo do "preenchimento" ou "corte" na String 
	 * � pela esquerda ou pela direita, atr�ves do par�metro leftPad. 
	 * <p>No caso em que a String � maior que a vari�vel size, � verificado se a String "cortada" 
	 * � formada apenas por valores "0" ou " "; caso seja, o m�todo retorna a String "cortada" 
	 * com o tamanho informado em size, excluindo os valores � esquerda ou direita de acordo com 
	 * o informado por leftPad (leftPad true excluiria os valores desnecess�rios � esquerda); caso 
	 * contr�rio, o m�todo verifica se checkOverFlow est� ativado e d� uma mensagem de erro, indicando que n�o foi poss�vel formata-la. 
	 * 
	 * @param str - A String a ser formatada
	 * @param size - Tamanho em que a String deve ser formatada
	 * @param fillWith - String para completar str caso este seja menor que o tamanho informado
	 * @param leftPad - True: Informa que as opera��es devem ser feitas � esquerda da String; False: � direita
	 * @param checkOverFlow - True: Realiza uma verifica��o se a String � maior que o tamanho dimensionado e se haver� perdas de dados; False: Simplesmente preenche ou corta a string para o tamanho desejado
	 * @return A String formatada
	 */
	public static String format(String str, int size, String fillWith, boolean leftPad, boolean checkOverFlow){
		String result = "";
		
		if (str.length() < size){
			if (leftPad)
				result =  org.apache.commons.lang.StringUtils.leftPad(str, size, fillWith);
			else
				result =  org.apache.commons.lang.StringUtils.rightPad(str, size, fillWith);	
		}else if (str.length() > size){
			if(checkOverFlow){
				String resto = "";
				if (leftPad){
					resto = org.apache.commons.lang.StringUtils.left(str, str.length()-size);
				}else
					resto = org.apache.commons.lang.StringUtils.right(str, str.length()-size);
			
				if (org.apache.commons.lang.StringUtils.containsOnly(resto, "0") 
						|| org.apache.commons.lang.StringUtils.containsOnly(resto, " ")){
					if (leftPad)
						result =  org.apache.commons.lang.StringUtils.right(str, size);
					else
						result =  org.apache.commons.lang.StringUtils.left(str, size);
				}else
					throw new RuntimeException("Dados poder�o ser perdidos se a string '" + str + "' for formatada para o tamanho " + size);
			}else{
				if (leftPad)
					result =  org.apache.commons.lang.StringUtils.right(str, size);
				else
					result =  org.apache.commons.lang.StringUtils.left(str, size);
			}
		}
		else{ //se o tamanho for igual, retorna a pr�pria string
			result = str;
		}
		
		return result;
	}

	/**
	 * <p>Formata uma String com o tamanho (size) passado por par�metro. O valor "0" � 
	 * usado como padr�o para completar a String. As opera��es s�o efetuadas � esquerda. 
	 * Caso n�o seja poss�vel formatar a String, retorna uma mensagem de erro.<br>
	 * 
	 * @param str - A String a ser formatada
	 * @param size - Tamanho em que a String deve ser formatada
	 * @param checkOverFlow - True: Realiza uma verifica��o se a String � maior que o tamanho dimensionado e se haver� perdas de dados; False: Simplesmente preenche ou corta a string para o tamanho desejado
	 * @return A String formatada
	 */
	public static String formatNumber(String str, int size, boolean checkOverFlow){
		return format(str, size, "0", true, checkOverFlow);
	}
	
	/**
	 * <p>Formata uma String com o tamanho (size) passado por par�metro. O valor " " � 
	 * usado como padr�o para completar a String, e as opera��es s�o efetuadas � direita.
	 * Caso n�o seja poss�vel formatar a String, retorna uma mensagem de erro.<br>
	 * 
	 * @param str - A String a ser formatada
	 * @param size - Tamanho em que a String deve ser formatada
	 * @param checkOverFlow - True: Realiza uma verifica��o se a String � maior que o tamanho dimensionado e se haver� perdas de dados; False: Simplesmente preenche ou corta a string para o tamanho desejado
	 * @return A String formatada
	 */
	public static String formatAlpha(String str, int size, boolean checkOverFlow){
		return format(str, size, " ", false, checkOverFlow);
	}
	
	/**
	 * Retorna uma String que cont�m apenas os valores num�ricos
	 * 
	 * @param str - String a ser formatada
	 * @return String formatada sem pontua��o
	 */
	public static String removeAlpha(String str){
		String result = "";
		for(int i=0; i<str.length(); i++){
			char c = str.charAt(i);
			if (Character.isDigit(c))
				result += str.charAt(i);
		}
        return result;
	}
	
    /**
     * Remove os caracteres que n�o sejam letras de
     * a..z e A..Z
     * Este m�todo � <i>null safe</i>.
     */
	public static String removeNonAlpha(String str){
		String result = "";
        if(str != null)
			for(int i=0; i<str.length(); i++){
				char c = str.charAt(i);
        	    if((c >= 'A' && c <= 'Z')||(c >= 'a' && c <= 'z'))
					result += str.charAt(i);
			
			}
        return result;
	}
	
	/**
	 * <p>Fun��o que troca caracteres acentuados e o cedilha (�) por caracteres correspondentes sem acento.<br>
     * Este m�todo � <i>null safe</i>.
	 * @param str - String a ser formatada
	 * @return String formatada sem acentua��o e cedilha (�)

	 */
	public static String removeAccent(String str){
		String result = str;
        if(str != null)
			for (int i=0; i<comAcento.length; i++){
				result = result.replace(comAcento[i], semAcento[i]);
			}
		return result;
	}
	
    /**
     * Retorna uma String com todas as palavras com a primeira letra 
     * mai�scula, exceto "De", "Do", "Dos", "Da", "Das" que n�o estiverem no inicio.<br>
     * Este m�todo � <i>null safe</i>.
     * @param value - String a ser capitalizada.
     * @return String capitalizada.
     */
    public static String capitalize(String value) {
        // Verifica se � nulo
        if (value==null) return ""; 
        
        char[] delimiters = new char[]{'.', ',', ' '};
        String str = WordUtils.capitalizeFully(value, delimiters);
        String[] lista = { "De", "Do", "Dos", "Da", "Das", " E" };
        for (String it : lista) {
            if (str.indexOf(it) > 0)
                str = org.apache.commons.lang.StringUtils.replace(str, it + " ", it.toLowerCase() + " ");
        }
        return str;
    }
    
    /**
     * Retira os caracteres n�o num�ricos [0-9,.-].<br>
     * Este m�todo � <i>null safe</i>.
     * @param value - String contendo quaisquer caracteres.
     * @return String contendo somente os caracteres n�mericos da string passada ou vazia.
     */
    public static String removeNonNumeric(String value) {
        String str = "";
        if(value != null)
	        for(int i = 0; i < value.length(); i++) {
	            char ch = value.charAt(i);
	            if((ch >= '0' && ch <= '9')|| ch== ',' || ch == '.' || ch == '-')
	                str += String.valueOf(ch);
	        }
        return str;
    }
    
    /**
     * Retira os caracteres que n�o s�o d�gitos num�ricos [0-9].<br>
     * Este m�todo � <i>null safe</i>.
     * @param value - String contendo quaisquer caracteres.
     * @return String contendo somente os caracteres n�mericos da string passada ou vazia.
     */
    public static String removeNonDigit(String value) {
        String str = "";
        if(value != null)
	        for(int i = 0; i < value.length(); i++) {
	            char ch = value.charAt(i);
	            if(ch >= '0' && ch <= '9')
	                str += String.valueOf(ch);
	        }
        return str;
    }
    
    /**
     * Capitaliza e remove espa�os em branco iniciais e finais 
     * @param value
     * @return
     */
    public static String prepareStringField(final String value){
    	return org.apache.commons.lang.StringUtils.strip(capitalize(value));
    }

    /**
     * Verifica se a String source possui somente caracteres v�lidos
     * @param source
     * @param validChars
     * @return
     * @author lucio 20121010
     */
	public static boolean checkValidChars(String source, char[] validChars){
		for(char c: source.toCharArray()){
			boolean invalid = true;
			for(char vc: validChars){
				if(c == vc){
					invalid = false;
					break;
				}
			}
			if (invalid)
				return false;
		}
		
		return true;
		
	}
	
    /**
     * Verifica se a String source possui somente caracteres v�lidos
     * @param source
     * @param validChars
     * @return
     * @author lucio 20121010
     */
	public static boolean checkValidChars(String source, String validChars){
		for(char c: source.toCharArray()){
			if (validChars.lastIndexOf(c) < 0)
				return false;
		}
		
		return true;
	}
	
	/**
	 * Converte um vetor de long em uma string separada por v�rgula.
	 * �til para gerar a lista de Ids a partir de Long[] para usar em HQLs
	 */
	public static String toString(Long[] values){
		StringBuffer str = new StringBuffer();
		
		for(Long l: values){
			str.append(l);
			str.append(',');
		}
		
		str.setLength(str.length()-1);
		return str.toString();
	}
	/**
	 * Verifica se uma String � null, vazia ou "null".
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isBlank(String str) {
		return org.apache.commons.lang.StringUtils.isBlank(str)
				|| "null".equals(str);
	}
	
	/**
	 * Verifica se uma String n�o � null, vazia ou "null".
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNotBlank(String str) {
		return !isBlank(str);
	}
	
	/**
	 * Verifica se uma String � null, vazia ou "null", retornando 
	 * a String padr�o definida em defaultStr.
	 * 
	 * @param str
	 * @param defaultStr
	 * @return
	 * @author andre 20121203
	 */
	public static String defaultIfBlank(String str, String defaultStr) {
		if (isBlank(str)) {
			return defaultStr;
		} else {
			return str;
		}
	}
	
}
