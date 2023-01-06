/**
 * 
 */
package br.com.orionsoft.monstrengo.core.util;

import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.beanutils.PropertyUtils;

import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;

/**
 * TODO NÃO-UTILIZADO classe não utilizada. Verificar a necessidade.
 * @author Marcia
 *
 */
public class CollectionUtils
{
    /**
     * Esse método procura um objeto especifico numa coleção, comparando a propriedade
     * id desse objeto com o id dos elementos da collection e o remove.
     * @param col coleção de objetos a ser analisado
     * @param obj objeto a ser removido
     * @return 
     */
    public static boolean removeCollection(Collection col, Object obj){
        
        for(Iterator i = col.iterator(); i.hasNext();){
            Object o = i.next();
            
            try
            {
                long idObj = (Long) PropertyUtils.getProperty(obj, IDAO.PROPERTY_ID_NAME);
                long idO = (Long) PropertyUtils.getProperty(o, IDAO.PROPERTY_ID_NAME);
                
                if (idO == idObj){
                    i.remove();
                    return true;
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        
        return false;
    }
          
    /**
     * Esse método procura um objeto especifico numa coleção, comparando a propriedade
     * id desse objeto com o id dos elementos da collection.
     * @param col coleção de objetos a ser analisado
     * @param obj objeto a ser removido
     * @return 
     */
    public static boolean containsCollection(Collection col, Object obj){
        
          for(Iterator i = col.iterator(); i.hasNext();){
              Object o = i.next();
              
            try
            {
                long idObj = (Long) PropertyUtils.getProperty(obj, IDAO.PROPERTY_ID_NAME);
                long idO = (Long) PropertyUtils.getProperty(o, IDAO.PROPERTY_ID_NAME);

                if (idO == idObj){
                    return true;
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
          }
          return false;
      }

}
