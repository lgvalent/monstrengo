/**
 * 
 */
package br.com.orionsoft.monstrengo.core.util;

/**
 * Classe com métodos estáticos para facilitar a manipulação de arrays.
 * @author Marcia
 */
public class ArrayUtils {

    /**
     * Esse método procura no array fornecido o indice da primeira posição vazia disponível.
     * @param objectArray array a ser explorado
     * @return indice da posição vazia
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
     * Esse método procura no array fornecido o indice da última posição vazia disponível.
     * @param objectArray array a ser explorado
     * @return indice da posição vazia
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
