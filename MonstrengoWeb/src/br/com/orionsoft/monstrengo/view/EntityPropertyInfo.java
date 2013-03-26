package br.com.orionsoft.monstrengo.view;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;

/**
 * Classe que armazena as propriedades estáticas de um atributo
 * 
 * Created on 01/04/2005
 * @author Marcia
 */
public class EntityPropertyInfo
{
    private String name;
    private int index;
    private String label;
    private String hint;
    private String help;
    private Class type;
    private boolean visible;
    private int size;
    private String colorName;
    private String displayFormat;
    private boolean typeFromFile = false;

    /////////////////////////////////////////////////////////////////////////////
    // Propriedades de Edição
    /////////////////////////////////////////////////////////////////////////////
    private boolean required;
    private boolean readOnly;
    private int minimum;
    private int maximum;
    private String editMask;
    private List<SelectItem> editList; // TODO Fazer com que EditList seja uma subClasse com suas propriedades
//    private SelectItem[] editList; // TODO Fazer com que EditList seja uma subClasse com suas propriedades
    private boolean editShowList;
    private boolean listFromFile;
    
    
    public boolean isRequired(){return required;}
    public void setRequired(boolean required){this.required = required;}

    public int getMaximum(){return maximum;}
    public void setMaximum(int maximum){this.maximum = maximum;}

    public int getMinimum(){return minimum;}
    public void setMinimum(int minimum){this.minimum = minimum;}

    public boolean isReadOnly(){return readOnly || this.name.equals(IDAO.PROPERTY_ID_NAME);}
    public void setReadOnly(boolean readonly){this.readOnly = readonly;}

    public String getEditMask(){return editMask;}
    public void setEditMask(String editMask){this.editMask = editMask;}

    public List<SelectItem> getEditList(){return editList;}
    public void setEditList(List<SelectItem> editList){this.editList = editList;}
    
//    public SelectItem[] getEditList(){return editList;}
//    public void setEditList(SelectItem[] editList){this.editList = editList;}

    
    public boolean isEditShowList(){return editShowList;}
    public void setEditShowList(boolean editShowList){this.editShowList = editShowList;}

    public boolean isListFromFile(){return listFromFile;}
    public void setListFromFile(boolean listFromFile){this.listFromFile = listFromFile;}

    
    // ///////////////////////////////////////////////////////////
    // CONSTRUTOR
    // ///////////////////////////////////////////////////////////

    // ///////////////////////////////////////////////////////////
    // Identificação do tipo do campo //
    // ///////////////////////////////////////////////////////////
    public boolean isDate(){return (type == Date.class);}
    public boolean isFloat(){return (type == Float.class);}
    public boolean isInteger(){return (type == Integer.class) || (type == int.class)
                || (type == long.class);}
    public boolean isString(){return (type == String.class);}
    public boolean isList(){return (typeFromFile || (type == Collection.class));}
    public boolean isBoolean(){return (type == boolean.class);}
    public boolean isSubClass(){return (!(isDate() || isFloat() || isInteger() 
            || isString() || isList() || isBoolean()));}


    // ///////////////////////////////////////////////////////////
    // Propriedades dos campos //
    // ///////////////////////////////////////////////////////////
    
    public String getName(){return name;}
    public void setName(String name){this.name = name;}

    public String getHint(){return hint;}
    public void setHint(String hint){this.hint = hint;}

    public Class getType(){return type;}
    public void setType(Class type){this.type = type;}

    public String getClassName(){return type.getName();}

    public int getSize(){return size;}
    public void setSize(int size){this.size = size;}

    public String getLabel(){return label;}
    public void setLabel(String label){this.label = label;}

    public boolean isVisible(){return visible;}
    public void setVisible(boolean visible){this.visible = visible;}

    public String getColorName(){return colorName;}
    public void setColorName(String colorName){this.colorName = colorName;}

    public boolean isTypeFromFile(){return typeFromFile;}
    public void setTypeFromFile(boolean typeFromFile){this.typeFromFile = typeFromFile;}
    
    public String getDisplayFormat() {return displayFormat;}
    public void setDisplayFormat(String displayFormat) {this.displayFormat = displayFormat;}

    public int getIndex() {return index;}
    public void setIndex(int index) {this.index = index;}

    public String getHelp() {return help;}
    public void setHelp(String help) {this.help = help;}
    
    
    
}
