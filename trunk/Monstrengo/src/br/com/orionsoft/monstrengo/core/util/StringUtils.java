package br.com.orionsoft.monstrengo.core.util;

import org.apache.commons.lang.WordUtils;


/*
 * Created on 11/08/2006
 *
 */

/**
 * Classe utilitária para tratamento de Strings, como formatação por exemplo.
 * @author andre
 * @version 20060811
 */
public class StringUtils {
	
	private static char[] comAcento = {'à','â','ê','ô','û','ã','õ','á','é','í','ó','ú','ç','ü','À','Â','Ê','Ô','Û','Ã','Õ','Á','É','Í','Ó','Ú','Ç','Ü'};
	private static char[] semAcento = {'a','a','e','o','u','a','o','a','e','i','o','u','c','u','A','A','E','O','U','A','O','A','E','I','O','U','C','U'};
	
	/**
	 * <p>Formata uma String com o tamanho (size) passado por parâmetro. Caso o tamanho da String
	 * seja menor que o tamanho passado em size, completa a String resultante com o valor 
	 * passado em fillWith. Verifica também se o tipo do "preenchimento" ou "corte" na String 
	 * é pela esquerda ou pela direita, atráves do parâmetro leftPad. 
	 * <p>No caso em que a String é maior que a variável size, é verificado se a String "cortada" 
	 * é formada apenas por valores "0" ou " "; caso seja, o método retorna a String "cortada" 
	 * com o tamanho informado em size, excluindo os valores à esquerda ou direita de acordo com 
	 * o informado por leftPad (leftPad true excluiria os valores desnecessários à esquerda); caso 
	 * contrário, o método verifica se checkOverFlow está ativado e dá uma mensagem de erro, indicando que não foi possível formata-la. 
	 * 
	 * @param str - A String a ser formatada
	 * @param size - Tamanho em que a String deve ser formatada
	 * @param fillWith - String para completar str caso este seja menor que o tamanho informado
	 * @param leftPad - True: Informa que as operações devem ser feitas à esquerda da String; False: à direita
	 * @param checkOverFlow - True: Realiza uma verificação se a String é maior que o tamanho dimensionado e se haverá perdas de dados; False: Simplesmente preenche ou corta a string para o tamanho desejado
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
					throw new RuntimeException("Dados poderão ser perdidos se a string '" + str + "' for formatada para o tamanho " + size);
			}else{
				if (leftPad)
					result =  org.apache.commons.lang.StringUtils.right(str, size);
				else
					result =  org.apache.commons.lang.StringUtils.left(str, size);
			}
		}
		else{ //se o tamanho for igual, retorna a própria string
			result = str;
		}
		
		return result;
	}

	/**
	 * <p>Formata uma String com o tamanho (size) passado por parâmetro. O valor "0" é 
	 * usado como padrão para completar a String. As operações são efetuadas à esquerda. 
	 * Caso não seja possível formatar a String, retorna uma mensagem de erro.<br>
	 * 
	 * @param str - A String a ser formatada
	 * @param size - Tamanho em que a String deve ser formatada
	 * @param checkOverFlow - True: Realiza uma verificação se a String é maior que o tamanho dimensionado e se haverá perdas de dados; False: Simplesmente preenche ou corta a string para o tamanho desejado
	 * @return A String formatada
	 */
	public static String formatNumber(String str, int size, boolean checkOverFlow){
		return format(str, size, "0", true, checkOverFlow);
	}
	
	/**
	 * <p>Formata uma String com o tamanho (size) passado por parâmetro. O valor " " é 
	 * usado como padrão para completar a String, e as operações são efetuadas à direita.
	 * Caso não seja possível formatar a String, retorna uma mensagem de erro.<br>
	 * 
	 * @param str - A String a ser formatada
	 * @param size - Tamanho em que a String deve ser formatada
	 * @param checkOverFlow - True: Realiza uma verificação se a String é maior que o tamanho dimensionado e se haverá perdas de dados; False: Simplesmente preenche ou corta a string para o tamanho desejado
	 * @return A String formatada
	 */
	public static String formatAlpha(String str, int size, boolean checkOverFlow){
		return format(str, size, " ", false, checkOverFlow);
	}
	
	/**
	 * Retorna uma String que contém apenas os valores numéricos
	 * 
	 * @param str - String a ser formatada
	 * @return String formatada sem pontuação
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
     * Remove os caracteres que não sejam letras de
     * a..z e A..Z
     * Este método é <i>null safe</i>.
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
	 * <p>Função que troca caracteres acentuados e o cedilha (ç) por caracteres correspondentes sem acento.<br>
     * Este método é <i>null safe</i>.
	 * @param str - String a ser formatada
	 * @return String formatada sem acentuação e cedilha (ç)

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
     * maiúscula, exceto "De", "Do", "Dos", "Da", "Das" que não estiverem no inicio.<br>
     * Este método é <i>null safe</i>.
     * @param value - String a ser capitalizada.
     * @return String capitalizada.
     */
    public static String capitalize(String value) {
        // Verifica se é nulo
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
     * Retira os caracteres não numéricos [0-9,.-].<br>
     * Este método é <i>null safe</i>.
     * @param value - String contendo quaisquer caracteres.
     * @return String contendo somente os caracteres númericos da string passada ou vazia.
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
     * Retira os caracteres que não são dígitos numéricos [0-9].<br>
     * Este método é <i>null safe</i>.
     * @param value - String contendo quaisquer caracteres.
     * @return String contendo somente os caracteres númericos da string passada ou vazia.
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
     * Capitaliza e remove espaços em branco iniciais e finais 
     * @param value
     * @return
     */
    public static String prepareStringField(final String value){
    	return org.apache.commons.lang.StringUtils.strip(capitalize(value));
    }

    /**
     * Verifica se a String source possui somente caracteres válidos
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
     * Verifica se a String source possui somente caracteres válidos
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
	 * Converte um vetor de long em uma string separada por vírgula.
	 * útil para gerar a lista de Ids a partir de Long[] para usar em HQLs
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
	 * Verifica se uma String é null, vazia ou "null".
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isBlank(String str) {
		return org.apache.commons.lang.StringUtils.isBlank(str)
				|| "null".equals(str);
	}
	
	/**
	 * Verifica se uma String não é null, vazia ou "null".
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNotBlank(String str) {
		return !isBlank(str);
	}
	
	/**
	 * Verifica se uma String é null, vazia ou "null", retornando 
	 * a String padrão definida em defaultStr.
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
