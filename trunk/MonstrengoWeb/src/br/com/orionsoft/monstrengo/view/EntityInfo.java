package br.com.orionsoft.monstrengo.view;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

/**
 * Created on 20/04/2005
 * @author Lucio
 */
public class EntityInfo 
{
//---------------------------------------------------------------------------------------
//                    Constantes
//---------------------------------------------------------------------------------------    
    private static final String INDEX = ".index";
    private static final String LABEL = ".label";
    private static final String HINT = ".hint";
    private static final String HELP = ".help";
    private static final String TYPELIST = ".typeList";
    private static final String VISIBLE = ".visible";
    private static final String SIZE = ".size";
    private static final String COLORNAME = ".colorName";
    private static final String DISPLAYFORMAT = ".displayFormat";

    private static final String REQUIRED = ".required";
    private static final String READONLY = ".readOnly";  
    private static final String MINIMUM = ".minimum";
    private static final String MAXIMUM = ".maximum";
    private static final String EDITMASK = ".editMask";
    private static final String EDITSHOWLIST = ".editShowList";
    private static final String EDITLIST = ".editList";
    
    private String bundleName;
    private ResourceBundle resourceBundle;

    public EntityInfo(Class klazz)
    {
        bundleName = klazz.getPackage().getName() + "." + klazz.getSimpleName();
        try{
        resourceBundle = ResourceBundle.getBundle(bundleName);
        }
        catch(MissingResourceException e)
        {   // TODO OTIMIZAR indicar no log do debug que o arquivo de propriedades da classe não foi encontrado
            resourceBundle = null;
        }
    }

    public String getStr(String key) throws MissingResourceException
    {
        if (resourceBundle != null)
            return resourceBundle.getString(key);
        throw new MissingResourceException("","","");
    }

    public String getLabel(String key)
    {
        try
        {
            return getStr(key + LABEL);
        }
        catch (MissingResourceException e)
        { 
            // Caso não encontre a Key procurada no arquivo de propriedade da classe
            // utiliza o nome da Key com a primeira letra em maiúsculo. Assim, propriedades
            // como Class.nome, Class.id que não se encontre no arquivo, são 'deduzidas'.
            return StringUtils.capitalize(key);
        }
    }
    
    public String getHint(String key)
    {
        try
        {
            return getStr(key + HINT);
        }
        catch (MissingResourceException e)
        {
            return StringUtils.capitalize(key);
        }
    }
    
    public String getHelp(String key)
    {
        try
        {
            return getStr(key + HELP);
        }
        catch (MissingResourceException e)
        {
            return "";
        }
    }
    
    public Class getTypeList(String key)
    {
        try
        {
            String value = getStr(key + TYPELIST);
            return Class.forName(value);
        }
        catch (MissingResourceException e)
        {
            // Se não encontrou o tipo declarado no arquivo resulta em nulo
            return null;
        }
        catch (ClassNotFoundException e)
        {
            // Se deu um erro de procura de classe na VM retorna a classe de erro
            return e.getClass();
        }
    }

    public boolean getRequired(String key)
    {
        try
        {
            String value = getStr(key + REQUIRED);
            return value.equals("true");
        }
        catch (MissingResourceException e)
        {
            // Se não encontrou a propriedade declarada retorna a padrão
            return false;
        }
    }
    
    public boolean getReadOnly(String key)
    {
        try
        {
            String value = getStr(key + READONLY);
            return value.equals("true");
        }
        catch (MissingResourceException e)
        {
            // Se não encontrou a propriedade declarada retorna a padrão
            return false;
        }
    }    
    
    public boolean getVisible(String key)
    {
        try
        {
            String value = getStr(key + VISIBLE);
            return value.equals("true");
        }
        catch (MissingResourceException e)
        {
            // Se não encontrou a propriedade declarada retorna a padrão
            return true;
        }
    }    
    
    public int getSize(String key)
    {
        try
        {
            String value = getStr(key + SIZE);
            return Integer.parseInt(value);
        }
        catch (MissingResourceException e)
        {
            // Se não encontrou a propriedade declarada retorna a padrão
            return 40;
        }
    }
    
    public int getMinimum(String key)
    {
        try
        {
            String value = getStr(key + MINIMUM);
            return Integer.parseInt(value);
        }
        catch (MissingResourceException e)
        {
            // Se não encontrou a propriedade declarada retorna a padrão
            return 0;
        }
    }

    public int getMaximum(String key)
    {
        try
        {
            String value = getStr(key + MAXIMUM);
            return Integer.parseInt(value);
        }
        catch (MissingResourceException e)
        {
            // Se não encontrou a propriedade declarada retorna a padrão
            return 999999;
        }
    }

    public String getColorName(String key)
    {
        try
        {
            String value = getStr(key + COLORNAME);
            return value;
        }
        catch (MissingResourceException e)
        {
            // Se não encontrou a propriedade declarada retorna a padrão
            return "";
        }
    }

    public String getEditMask(String key)
    {
        try
        {
            String value = getStr(key + EDITMASK);
            return value;
        }
        catch (MissingResourceException e)
        {
            // Se não encontrou a propriedade declarada retorna a padrão
            return "";
        }
    }
    
    public boolean getEditShowList(String key)
    {
        try
        {
            String editShowList = getStr(key + EDITSHOWLIST);
            
            return editShowList.equals("true");
        }
        catch (MissingResourceException e)
        {
            // Se não encontrou a propriedade declarada retorna a padrão
            return false;
        }
    }
    
    /**
     * Busca a lista que deverá ser mostrada na edição. 
     * @param key = propriedade a ser pesquisada
     * @return lista de elementos
     */
    public List<SelectItem> getEditList(String key)
    {
        try
        {
            String editList = getStr(key + EDITLIST);
            /*
             * Dada uma string, converte-o num array de string considerando que 
             * os elementos estão separados por vírgula.
             */
            String[] list = StringUtils.split(editList,",");
            List<SelectItem> lista = new ArrayList<SelectItem>();
            
            // Coloca os demais item
            for (int i=0; i<list.length; i++)
            {
                lista.add(new SelectItem(list[i]));
            }
            return lista;
        }
        catch (MissingResourceException e)
        {
            // Se não encontrou a propriedade declarada retorna a padrão
            return null;
        }
    }

    public String getDisplayFormat(String key) 
    {
        try
        {
            String value = getStr(key + DISPLAYFORMAT);
            return value;
        }
        catch (MissingResourceException e)
        {
            // Se não encontrou a propriedade declarada retorna a padrão
            return "";
        }
    }
    
    public int getIndex(String key) 
    {
        try
        {
            String value = getStr(key + INDEX);
            return Integer.parseInt(value);
        }
        catch (MissingResourceException e)
        {
            // Se não encontrou a propriedade declarada retorna a padrão
            return -1;
        }
    }
    
}

