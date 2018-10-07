package br.com.orionsoft.monstrengo.core.util;


/**
 * Esta classe possui alguns c�lculos �teis a outros m�dulos, como o c�lculo 
 * de d�gito verificador para determinada String em m�dulo 10 e 11.
 * 
 * @author andre
 *
 */
public class MathUtils {

	/**
     * Calcula o DV m�dulo 11 do c�digo de barra
     * Para resto 0,1,10 assume d�gito 1.
     * @param campo o campo a ser calculado
     * @return uma String contendo o DV
     */
    public static String modulo11(String codigo) {
        int total = 0;
        int peso = 2;
        for (int i = 0; i < codigo.length(); i++) {
            try {
                total += (codigo.charAt(codigo.length() - 1 - i) - '0') * peso;
            } catch (StringIndexOutOfBoundsException e) {
                throw e;
            }
            peso++;
            if (peso == 10)
                peso = 2;
        }
        int resto = total % 11;
        return (resto == 0 || resto == 1 || resto == 10) ? "1" : String.valueOf(11 - resto);
    }

	/**
     * Calcula o DV m�dulo 11 do c�digo de barra
     * Para resto 0,1 assume d�gito 0.
     * @param campo o campo a ser calculado
     * @return uma String contendo o DV
     */
    public static String modulo11a(String codigo) {
        int total = 0;
        int peso = 2;
        for (int i = 0; i < codigo.length(); i++) {
            try {
                total += (codigo.charAt(codigo.length() - 1 - i) - '0') * peso;
            } catch (StringIndexOutOfBoundsException e) {
                throw e;
            }
            peso++;
            if (peso == 10)
                peso = 2;
        }
        int resto = total % 11;
        return (resto == 0 || resto == 1) ? "0" : String.valueOf(11 - resto);
    }

    /**
     * Calcula o DV m�dulo 10 da linha digit�vel.
     * 
     * @param campo
     *            o campo a ser calculado
     * @return uma String contendo o DV
     */
    public static String modulo10(String codigo) {
        int total = 0;
        int peso;
        if (codigo.length() % 2 != 0)
            peso = 2;
        else
            peso = 1;
        for (int f = 1; f <= codigo.length(); f++) {
            int k = Integer.valueOf(codigo.substring(f - 1, f)) * peso;
            if (k > 9)
                k = 1 + (k - 10);
            total += k;
            if (peso == 1)
                peso = 2;
            else
                peso = 1;
        }
        peso = 1000 - total;
        return (String.valueOf(peso).substring(String.valueOf(peso).length() - 1, String.valueOf(peso).length()));
    }

    public static void main(String[] args) {
		System.out.println(modulo11("0165020062307200003"));
		System.out.println(modulo11a("0718283518212200011"));
	}
}