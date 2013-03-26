/**
 * 
 */
package br.com.orionsoft.monstrengo.core.util;

/**
 * Classe com m�todos est�ticos para facilitar a manipula��o de arrays.
 * @author Marcia
 */
public class ArrayUtils {

    /**
     * Esse m�todo procura no array fornecido o indice da primeira posi��o vazia dispon�vel.
     * @param objectArray array a ser explorado
     * @return indice da posi��o vazia
     */
    public static int findFirstEmpty(Object[] objectArray)
    {
        for (int i=0; i<objectArray.length; i++)
        {
            if (objectArray[i] ==null)
                return i;
        }
        return -1;
    }

    /**
     * Esse m�todo procura no array fornecido o indice da �ltima posi��o vazia dispon�vel.
     * @param objectArray array a ser explorado
     * @return indice da posi��o vazia
     */
    public static int findLastEmpty(Object[] objectArray)
    {
        for (int i=(objectArray.length-1); i>=0; i--)
        {
            if (objectArray[i] == null)
                return i;
        }
        return -1;
    }
}
