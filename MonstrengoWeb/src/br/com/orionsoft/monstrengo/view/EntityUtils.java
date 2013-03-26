package br.com.orionsoft.monstrengo.view;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;

import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;

/**
 * Created on 05/04/2005
 * @author Marcia
 */
public class EntityUtils
{
//    public static final String URL_PARAM_ENTITY_TYPE = "entity";
//    public static final String URL_PARAM_ENTITY_ID = "entityId";
   
    public static List<SelectItem> convertObjectListToSelectItemList(List list) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        List<SelectItem> result = new ArrayList<SelectItem>(list.size());
        
        // Coloca os demais item
        for (Iterator item = list.iterator(); item.hasNext();)
        {
            Object obj = item.next();

            long objId = (Long)PropertyUtils.getProperty(obj, IDAO.PROPERTY_ID_NAME);

            result.add( new SelectItem(objId, obj.toString()));
        }

        return result;
    }
    
    public static SelectItem[] convertListToArraySelectItem(List list)
    {
        SelectItem[] result = new SelectItem[list.size()];
        
        for (int i = 0; i <list.size(); i++)
        {
            Object obj = list.get(i);

            result[i]= new SelectItem(obj, obj.toString());
        }
        return result;
    }
    
    public static java.lang.reflect.Field[] getDeclaredFields(String className)
    {
        try
        {
            return Class.forName(className).getDeclaredFields();
        }
        catch (SecurityException e)
        {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Dada uma lista de objetos e um array de campos do tipo EntityPropertyInfo obtém, para 
     * cada objeto da lista, um array de string contendo os valores dos campos, e retorna 
     * um array de string bidirecional contendo os objetos e seus valores de campos   
     * 
     * @param collection
     * @param fields
     * @return
     * @throws Exception
     * 
     * TODO NÃO-UTILIZADO
     */
    public static String[][] convertListToDataStr(java.util.Collection collection,
            br.com.orionsoft.monstrengo.view.EntityPropertyInfo[] fields) throws Exception
    {
        // Cria uma lista de campos formatados com o mesmo tamanho dos campos
        // declarados
        String[][] result = new String[collection.size()][fields.length];
        int i=0;
        for (Iterator iterator = collection.iterator(); iterator.hasNext();)
        {
            result[i] = convertObjToDataStr(iterator.next(), fields);
            i++;
        }
        return result;
    }

    /**
     * Dado um objeto e um array de campos do tipo EntityPropertyInfo obtém os valores de cada 
     * campo e converte para uma string. Retorna um array de strings contendo todos os
     * valores das propriedades do objeto
     * 
     * @param obj 
     * @param fields
     * @return
     * @throws Exception
     * 
     * @see br.com.orionsoft.monstrengo.view.EntityPropertyInfo
     * 
     * TODO NÃO-UTILIZADO
     */
    public static String[] convertObjToDataStr(Object obj,
            br.com.orionsoft.monstrengo.view.EntityPropertyInfo[] fields) throws Exception
    {
//        System.out.println("EntityUtils.convertObjToDataStr: " + obj + "fields: " + fields);

        // Cria uma lista de campos formatados com o mesmo tamanho dos campos
        // declarados
        String[] result = new String[fields.length];
        // Estas variáveis foram declaradas aqui por otimização.
        // Para não serem criadas a cada iteração dos comandos 'for'
        String propr;
        for (int i = 0; i < fields.length; i++)
        {
            // Dada uma instância da classe (objeto), pede-se para o campo se
            // localizar dentro desta instância corrente e retornar o valor do 
            // campo como um objeto
            try{
                propr = BeanUtils.getProperty(obj, fields[i].getName());
            }catch(Exception e){
                propr = e.getMessage();
            }
            /*
             * TODO: Pegar todas as propriedades REAIS do campo por reflexão ou
             * qq outra coisa a ser definida Obtido o objeto que se refere ao
             * valor da propriedade da instância corrente, converte-o para o
             * tipo de dados, formatando-o adequadamente para exibição
             */
            result[i] = propr;
            // Limpa os ponteiros usados
            propr = null;
        }
        return result;
    }


    /**
     * Dado um nome completo da classe (contendo o pacote) retira o nome do pacote e retorna
     * apenas o nome da classe.
     * 
     * @param fullClassName
     * @return
     */
    public static String getClassNameFromPath(String fullClassName)
    {
        return fullClassName.substring(fullClassName.lastIndexOf(".") + 1,
                fullClassName.length());
    }

    public static Class getClassForName(String className)
    {
        try
        {
            return Class.forName(className);
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Dada uma classe procura pelos campos dessa classe e, caso a classe tenha uma 
     * superClasse obtem também os campos da superclasse e adiciona em uma lista.
     * @param classObj
     * @return lista de campos da classe e da super classe dessa classe, se existir.
     * @throws Exception
     */
    public static java.lang.reflect.Field[] getFields(Class classObj)
            throws Exception
    {
//        System.out.println("EntityUtils.getFields:" + classObj);

        ArrayList<Field> fieldList = new ArrayList<Field>();

        // Pega os campos declarados na classe passada
        java.lang.reflect.Field[] fields = classObj.getDeclaredFields();
        // Adiciona na lista
        for (int i = fields.length-1; i >= 0 ; i--)
            fieldList.add(0, fields[i]);
        
        // Pega os campos da super classe da classe passada, caso haja
        Class superClass = classObj.getSuperclass();
        while (superClass != null)
        {
            // Pega os campos da super classe na classe passada
            fields = superClass.getDeclaredFields();
            // Adiciona na lista
            for (int i = fields.length-1; i >=0 ; i--)
                fieldList.add(0, fields[i]);
            // Verifica se terá mais superClasse na hierarquia
            superClass = superClass.getSuperclass();
        }

        // Passa os campos conseguidos para o array do resultado
        java.lang.reflect.Field[] result = new Field[fieldList.size()];
        for (int i = 0; i < fieldList.size(); i++)
            result[i] = fieldList.get(i);
        
        return result;
    }

//    public static boolean isLazyProperty(Object obj)
//    {
//        try
//        {
//            return ((Collection) obj).isLazy();
//        }
//        catch (Exception e)
//        {
//            return false;
//        }
//    }
//    
}
