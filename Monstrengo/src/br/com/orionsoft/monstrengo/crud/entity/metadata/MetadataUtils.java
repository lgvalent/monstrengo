package br.com.orionsoft.monstrengo.crud.entity.metadata;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * <p>Esta classe implementa as rotinas �teis para serem usadas nas classes que 
 * manipulam metadados.<p>
 * 
 * @author Lucio
 */
public class MetadataUtils
{
    
    /**
     * <p>Obtem todos os campos declarados na classe informada e nas suas 
     * subclasses, excluindo o objeto b�s�co.<p>
     * <p>O objeto b�sico � identificado por aquele que seu antecessor � <code>Object</code>.<p>
     * 
     * @param klazz
     * @return
     * 
     * @deprecated N�o � aconselhado usar Fields para beans, mas PropertyDescriptors
     */
    public static final Field[] getFields(Class klazz)
    {
        Field[] result=null;
        
        // Verifica se a superclasse do atual objeto � Object, isto indica
        // que a classe atual � a b�sica, comum a todas as entidades de neg�cio.
        if(klazz != Object.class){
            
            // Pega os campos da superClasse (RECURSIVAMENTE)
            Field[] superFields = getFields(klazz.getSuperclass());

            // Pega os campos da Classe atual
            Field[] fields = klazz.getDeclaredFields();

            // Concatena os campos encontrados
            // Calcula o tamanho �til pra criar uma lista otimizada
            int size=0;
            if (superFields != null) size+=superFields.length;
            if (fields != null)
            {
                size+=fields.length;
                // N�o conta os campos St�ticos e Abstratos
                for(Field field: fields)
                    if (Modifier.isStatic(field.getModifiers())||
                       (Modifier.isAbstract(field.getModifiers())))
                          size--;
            }
            
            // Cria a lista com o tamanho otimizado
            result = new Field[size];
            
            // Copia os conte�dos das listas para o resulado
            int index=0;
            if (superFields != null) 
                for(Field superField: superFields)
                    result[index++]=superField;
            
            if (fields != null) 
                for(Field field: fields)
                    if (!Modifier.isStatic(field.getModifiers())&&
                       (!Modifier.isAbstract(field.getModifiers())))
                            result[index++]=field;
            
        }
            
        return result;
    }
    
}
