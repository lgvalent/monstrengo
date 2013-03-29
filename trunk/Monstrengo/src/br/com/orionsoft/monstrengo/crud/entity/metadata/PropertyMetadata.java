package br.com.orionsoft.monstrengo.crud.entity.metadata;

import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import br.com.orionsoft.monstrengo.crud.entity.metadata.IEntityMetadata;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IMetadataHandle;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IPropertyMetadata;
import br.com.orionsoft.monstrengo.crud.entity.metadata.MetadataException;
import br.com.orionsoft.monstrengo.crud.entity.metadata.PropertyMetadata;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.util.ArrayUtils;

/**
 * Esta classe armazena os metadados de uma propriedade de uma entidade. <br>
 * São fornecidos métodos para identificação dos tipos padrões de um propriedade: <br>
 * isCalendar(); isDouble();... <br>
 * @author marcia
 * @version 20060109
 */
public class PropertyMetadata implements IPropertyMetadata
{
	private IEntityMetadata entity;

    private int index;
    private int group;
    private String name;
    private String label;
    private String hint;
    private String description;
    private Class<?> type;
    private String colorName;
    private String displayFormat;
    private String defaultValue;
    private String editMask;
    private int size;
    private double maximum;
    private double minimum;
    private boolean readOnly;
    private boolean calculated;
    private boolean visible;
    private boolean required;
    private List<String> valuesList;
    private boolean editShowList;
    private boolean editShowEmbedded;
    private boolean isList;
    private boolean isSet;
    private boolean allowSubQuery;
    private boolean embedded;
    private boolean html;


    /* IMPLEMENTAÇÃO DA INTERFACE IPropertyMetada */

    public IEntityMetadata getEntity() {return this.entity;}

    /* Identificação do tipo do campo */
    public boolean isNumber(){return
    		isBigDecimal() ||
    		isDouble() ||
    		isFloat() ||
    		isLong() ||
    		isInteger();}

    public boolean isCalendar(){return (type == Calendar.class);}
    public boolean isDate(){return (type == Date.class);}
    public boolean isBigDecimal(){return (type == BigDecimal.class);}
    public boolean isDouble(){return ((type == Double.class) || (type == double.class));}
    public boolean isFloat(){return ((type == Float.class) || (type == float.class));}
    public boolean isLong(){return ((type == Long.class)|| (type == long.class));}
    public boolean isInteger(){return ((type == Integer.class) || (type == int.class));}
    public boolean isString(){return (type == String.class);}
    public boolean isHtml(){return html;}
    public boolean isBoolean(){return ((type == Boolean.class) || (type == boolean.class));}
    public boolean isEnum(){return type.isEnum();}

    /**
     * Tipos primitivos como int, long e etc não possuem pacotes definidos.
     */
    public boolean isEntity(){return (this.type.getPackage()!=null)&&(!type.getPackage().getName().startsWith("java")&&!isEnum());}
    public boolean isPrimitive(){return !isEntity();}
    public boolean isList(){return isList;}
    public boolean isSet(){return isSet;}
    public boolean isCollection(){return (isList||isSet);}

	public boolean isEditShowEmbedded() {return editShowEmbedded;}
	public void setEditShowEmbedded(boolean isOneToMany) {this.editShowEmbedded = isOneToMany;}

	public int getIndex(){return index;}

	public int getGroup() {return this.group;}

	public String getName(){return name;}

    public String getLabel(){return label;}

    public String getHint(){return hint;}

    public String getDescription(){return description;}

    public Class<?> getType(){return type;}

    public String getColorName(){return colorName;}

    public String getDisplayFormat(){return displayFormat;}

    public String getEditMask(){return editMask;}

    public int getEditMaskSize(){return editMask.length();}

    public boolean isHasEditMask(){return !StringUtils.isEmpty(editMask);}

    public int getSize(){return size;}

    public double getMaximum(){return maximum;}

    public double getMinimum(){return minimum;}

    public boolean isReadOnly(){return readOnly;}

    public boolean isCalculated(){return calculated;}

    public boolean isVisible(){return visible;}

    public boolean isRequired(){return required;}

    public List<String> getValuesList(){return valuesList;}

    public boolean isEditShowList(){return editShowList;}

    public PropertyMetadata (PropertyDescriptor property, IMetadataHandle metadataHandle, IEntityMetadata entityOwner) throws MetadataException
    {
        String propertyName = property.getName();

        this.entity = entityOwner;

        /* Define o tipo do campo:
        Se achar definido no arquivo utiliza-o
        Senão utiliza o tipo Java declarado */
        this.type = metadataHandle.getPropertyType(propertyName);
        if (this.type == null){
        	try{
        		this.type = entityOwner.getType().getDeclaredField(property.getName()).getType();
        	}catch (NoSuchFieldException e){
        		this.type = property.getPropertyType();
        	}
        }

        /* Preenche os dados */
        this.name = propertyName;
        this.label = metadataHandle.getPropertyLabel(propertyName);
        this.hint = metadataHandle.getPropertyHint(propertyName);
        this.description = metadataHandle.getPropertyDescription(propertyName);

        this.displayFormat = metadataHandle.getPropertyDisplayFormat(propertyName);
        this.colorName = metadataHandle.getPropertyColorName(propertyName);
        this.editMask = metadataHandle.getPropertyEditMask(propertyName);
        this.defaultValue = metadataHandle.getPropertyDefaultValue(propertyName);

        this.visible = metadataHandle.getPropertyVisible(propertyName);
        this.html = metadataHandle.getPropertyHtml(propertyName);
        this.calculated = metadataHandle.getPropertyCalculated(propertyName);

        /* Verifica se a classe implementa o método set, ou seja, a propriedade
           não é ReadOnly. Assim, o metadado não é ignorado */
        if (property.getWriteMethod() == null)
            this.readOnly = true;
        else
            this.readOnly = metadataHandle.getPropertyReadOnly(propertyName);

        /* TODO CORRIGIR Foi colocado required false pra todos
         * para nao dar problema na fase de implantação do Sivamar*/
//        this.required = false;
        this.required = metadataHandle.getPropertyRequired(propertyName);
        
        this.size = metadataHandle.getPropertySize(propertyName);

        this.maximum = metadataHandle.getPropertyMaximum(propertyName);
        this.minimum = metadataHandle.getPropertyMinimum(propertyName);

        this.editShowList = metadataHandle.getPropertyEditShowList(propertyName);
        this.editShowEmbedded = metadataHandle.getPropertyEditShowEmbedded(propertyName);
        this.embedded = metadataHandle.getPropertyEmbedded(propertyName);

        this.allowSubQuery = metadataHandle.getPropertyAllowSubQuery(propertyName);

        this.isList = metadataHandle.getPropertyIsList(propertyName);
        this.isSet = metadataHandle.getPropertyIsSet(propertyName);


        /* Para definir o índice do campo, pesquisa-se o arquivo
           Caso o arquivo não apresente, posteriormente será tratado o -1. */
        this.index = metadataHandle.getPropertyIndex(propertyName);

        this.group = metadataHandle.getPropertyGroup(propertyName);

       	this.valuesList = metadataHandle.getPropertyValuesList(propertyName);
       	if(this.valuesList.size()>0)
       		this.editShowList = true;
        
    }

    /**
     * Este método organiza os índices das propriedades de forma a deixá-las
     * ordenadas, mesmo as propriedades que não tiveram seus índices definidos
     * nos metadados.<br>
     * Um vetor é criado para verifica concorrência de índices.
     *
     * @param props
     * @throws MetadataException
     * @since 20060109
     */
    public static void arrangePropertiesIndex(Collection<IPropertyMetadata> props) throws MetadataException{
        // Obs.: Nem todas propriedades tem um índice definido.
        // Pode ocorrer de os metadados não informar e estar o valor -1.
        // Esta rotina trata esta situação realocando os campos em uma
        // ordem, obedecendo aos campos que conterem o índice.
        IPropertyMetadata[] indexes = new IPropertyMetadata[props.size()];

        for(IPropertyMetadata prop_: props)
        {
            /* converta para a classe local para ter acesso aos métodos
             * set()
             */
            PropertyMetadata prop = (PropertyMetadata) prop_;

            int index = prop.getIndex();

            /* Verifica se o índice da propriedade é válido */
            if(index > (indexes.length-1))
               throw new MetadataException(MessageList.create(PropertyMetadata.class, "INVALID_INDEX", index, prop.getName(), prop.getEntity().getName()));

           // Verifica se a propriedade tem um índice definido. Caso tenha,
           // verifica se o indice da propriedade no array está ocupado
           // se estiver pega o elemento do array e joga para a ultima
           // posição vazio do array e coloca a propriedade na posição do
           // indice definido.
           if (index!= -1)
           {
               // Já existe um valor na posição do array para esse indice
               // então retira o valor e coloca-o numa posição vazia
               if (indexes[index] != null)
               {
                   int index2 = ArrayUtils.findFirstEmpty(indexes);
                   indexes[index2] = indexes[index];

                   /* Grava o novo índice */
                   ((PropertyMetadata) indexes[index2]).index = index2;
               }
               // Grava a prop na posição do indice.
               indexes[prop.getIndex()] = prop;


           }
           else
           {
               // Se nenhum indice existir simplesmente joga o valor numa
               // posição vazia
               index = ArrayUtils.findFirstEmpty(indexes);
               indexes[index] = prop;
               /* Grava o novo índice */
               prop.index = index;
           }

        }
    }
	public String getDefaultValue(){return defaultValue;}
	public void setDefaultValue(String defaultValue){this.defaultValue = defaultValue;}

	public boolean isAllowSubQuery() {return allowSubQuery;}
	public void setAllowSubQuery(boolean allowSubQuery) {this.allowSubQuery = allowSubQuery;}

	public boolean isEmbedded() {return embedded;}
	public void setEmbedded(boolean embedded) {this.embedded = embedded;}

	public List<SelectItem> getEnumValuesList() {
		if(this.isEnum())
		{
			/* A lista é criada com o tamanho já otimizado */
			List<SelectItem> result = new ArrayList<SelectItem>(this.getType().getEnumConstants().length);

			for(Object enumValue: this.getType().getEnumConstants())
				/* Para a persistência de ENUM no banco é utilizada a anotação: @Enumerated(EnumType.STRING)
				 * Assim, na pesquisa no banco deve-se utilizar enum.name(). POr isto, é criada uma lista 
				 * com o enum.name()
				 */
				result.add(new SelectItem(((Enum<?>) enumValue).name(), enumValue.toString()));
			
			return result;
		}

		return null;
	}
}
