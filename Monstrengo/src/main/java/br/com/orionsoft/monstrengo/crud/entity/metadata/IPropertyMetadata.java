/**
 *
 */
package br.com.orionsoft.monstrengo.crud.entity.metadata;

import java.util.Comparator;
import java.util.List;

import javax.faces.model.SelectItem;

import br.com.orionsoft.monstrengo.crud.entity.metadata.IEntityMetadata;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IPropertyMetadata;

/**
 * @author Lucio
 * @version 20060515
 *
 */
public interface IPropertyMetadata
{
    /** Comparadores usados na cria��o de listas ordenadas das propriedades de uma entidade.
     *  Pode-se ordernar a lista por INDEX, GROUP ou LABEL (alfab�tica) */
	public static Comparator<IPropertyMetadata> COMPARATOR_INDEX = new Comparator<IPropertyMetadata>(){public int compare(IPropertyMetadata arg0, IPropertyMetadata arg1){return  new Integer(arg0.getIndex()).compareTo(arg1.getIndex());}};
    public static Comparator<IPropertyMetadata> COMPARATOR_GROUP = new Comparator<IPropertyMetadata>(){public int compare(IPropertyMetadata arg0, IPropertyMetadata arg1){return  new Integer(arg0.getGroup()).compareTo(arg1.getGroup());}};
    public static Comparator<IPropertyMetadata> COMPARATOR_LABEL = new Comparator<IPropertyMetadata>(){public int compare(IPropertyMetadata arg0, IPropertyMetadata arg1){return  arg0.getLabel().compareTo(arg1.getLabel());}};

    public IEntityMetadata getEntity();

    // Identifica��o do tipo do campo //
    public boolean isNumber();

    public boolean isBigDecimal();
    public boolean isBoolean();
    public boolean isCalendar();
    public boolean isDate();
    public boolean isDouble();
    public boolean isFloat();
    public boolean isLong();
    public boolean isInteger();
    public boolean isString();
    public boolean isCollection();
    public boolean isList();
    public boolean isSet();
    public boolean isEntity();
    public boolean isPrimitive();

    public boolean isEnum();

    // ///////////////////////////////////////////////////////////
    // Propriedades dos campos //
    // ///////////////////////////////////////////////////////////
    public int getIndex();
    public int getGroup();
    public String getName();
    public String getLabel();
    public String getHint();
    public String getDescription();

    public Class<?> getType();
    public String getColorName();
    public String getDisplayFormat();
    public String getDefaultValue();
    public String getEditMask();
    public int getEditMaskSize();
    public boolean isHasEditMask();

    public int getSize();
    public double getMaximum();
    public double getMinimum();

    public boolean isReadOnly();
    public boolean isCalculated();
    public boolean isVisible();
    public boolean isRequired();

    public List<String> getValuesList();
    public List<SelectItem> getEnumValuesList();

    /**
     * Define se esta propriedade � anotada como @Embedded
     */
    public boolean isEmbedded();
    /**
     * Define se na tela de edi��o, dever� ser usado um comboBox para listar o poss�veis valores para a proriedade
     * atual. A lista de valores no valuesList do metadados � utilizada. Se for um Entity, ent�o a lista de entidades cadastradas no banco � utilizadas.
     * @return
     */
    public boolean isEditShowList();

    
    /**
     * Define como as telas de edi��o ir�o se comportar ao lidar com 
     * propriedades do tipo IEntity. Se estiver true, dever� ser inclu�da uma se��o 
     * embutida para preenchimento dos dados da subEntidade. Sen�o, o operador
     * poder� buscar a entidade na lista
     * @return
     */
    public boolean isEditShowEmbedded();


	/**
	 * Esta propriedade permite definir para as m�quinas de pesquisas que
	 * esta propriedade pode ser utilizada como uma entidade
	 * e suas propriedades podem ser analisadas nas pesquisas autom�ticas.
	 * Exemplo: Ao pesquisar numa tabela de contrato, a propriedade pessoa
	 * pode ser definida como allowSubQuery para que tudo que for pesquisado
	 * em contrato seja pesquisado nas propriedades da propriedade pessoa
	 * automaticamente.
	 * Foi necess�rio criar este mecanismo porque se for utilizado todos
	 * os campos para subqueries os gerenciados n�o conseguem terminar a
	 * pesquisa.
	 *
	 * @return
	 * @since 20060515
	 */
    public boolean isAllowSubQuery();

}
