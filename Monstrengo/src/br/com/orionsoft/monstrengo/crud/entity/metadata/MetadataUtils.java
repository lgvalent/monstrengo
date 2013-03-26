package br.com.orionsoft.monstrengo.crud.entity.metadata;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * <p>Esta classe implementa as rotinas úteis para serem usadas nas classes que 
 * manipulam metadados.<p>
 * 
 * @author Lucio
 */
public class MetadataUtils
{
    
    /**
     * <p>Obtem todos os campos declarados na classe informada e nas suas 
     * subclasses, excluindo o objeto básíco.<p>
     * <p>O objeto básico é identificado por aquele que seu antecessor é <code>Object</code>.<p>
     * 
     * @param klazz
     * @return
     * 
     * @deprecated Não é aconselhado usar Fields para beans, mas PropertyDescriptors
     */
    public static final Field[] getFields(Class klazz)
    {
        Field[] result=null;
        
        // Verifica se a superclasse do atual objeto é Object, isto indica
        // que a classe atual é a básica, comum a todas as entidades de negócio.
        if(klazz != Object.class){
            
            // Pega os campos da superClasse (RECURSIVAMENTE)
            Field[] superFields = getFields(klazz.getSuperclass());

            // Pega os campos da Classe atual
            Field[] fields = klazz.getDeclaredFields();

            // Concatena os campos encontrados
            // Calcula o tamanho útil pra criar uma lista otimizada
            int size=0;
            if (superFields != null) size+=superFields.length;
            if (fields != null)
            {
                size+=fields.length;
                // Não conta os campos Státicos e Abstratos
                for(Field field: fields)
                    if (Modifier.isStatic(field.getModifiers())||
                       (Modifier.isAbstract(field.getModifiers())))
                          size--;
            }
            
            // Cria a lista com o tamanho otimizado
            result = new Field[size];
            
            // Copia os conteúdos das listas para o resulado
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
