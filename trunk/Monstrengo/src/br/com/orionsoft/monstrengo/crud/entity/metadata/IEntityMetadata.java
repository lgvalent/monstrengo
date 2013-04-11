/**
 * 
 */
package br.com.orionsoft.monstrengo.crud.entity.metadata;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import br.com.orionsoft.monstrengo.crud.entity.metadata.IEntityMetadata;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IGroupMetadata;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IPropertyMetadata;
import br.com.orionsoft.monstrengo.crud.entity.metadata.MetadataException;

/**
 * Define os dados dos metadados de um entidade e seus
 * relacionamentos com Grupos e Propriedades
 * @author Lucio 20050000
 * @version 20060413
 */
public interface IEntityMetadata
{
    public static Comparator<IEntityMetadata> COMPARATOR_LABEL = new Comparator<IEntityMetadata>(){public int compare(IEntityMetadata arg0, IEntityMetadata arg1){return  arg0.getLabel().compareTo(arg1.getLabel());}};

	public String getName();
    public String getLabel();
    public String getHint();
    public String getDescription();
    public String getColorName();
    public Class<?> getType();
    public List<Class<?>> getSubEntities();
    public boolean isHasSubEntities();
    
    /**
     * Indica se os usu�rios poder�o executar as opera��es de cria��o, recupera��o,
     * atualiza��o e exclus�o(CRUD) na entidade.
     * @return true se a entidade for canCreate, canRetrieve, canUpdate, canDelete, false caso contr�rio.
     */
    public boolean getCanCreate();
    public boolean getCanRetrieve();
    public boolean getCanUpdate();
    public boolean getCanDelete();
    public boolean getCanQuery();
    
    /** Permite definir se as telas de pesquisa exibem TODAS as entidades cadastradas ao serem acessadas */
    public boolean getRunQueryOnOpen();

    public Map<String, IPropertyMetadata> getPropertiesMetadata();
    public IPropertyMetadata getPropertyMetadata(String propertyName) throws MetadataException;
    
    /**
     * Retorna uma lista indexada das propriedades, obedecendo ao �ndice
     * definido nos metadados de cada propriedade.
     * @return
     */
    public IPropertyMetadata[] getProperties();
    
    /**
     * Retorna uma lista indexada das propriedades, obedecendo ao �ndice
     * definido nos metadados de cada propriedade. Somente as propriedades
     * definidas como InQueryGrid.
     * @return
     * @since 20110621
     */
    public IPropertyMetadata[] getPropertiesInQueryGrid();
    
    /**
     * Este m�todo � �til para obter quantos elementos temos no propertiesInQueryGrid().
     * Este informa��o vem do atributo length, que n�o segue o padr�o JavaBeans get/set.
     * Este m�todo segue este padr�o e facilita a utiliza��o da estrutura de metadados
     * em frameworks como o JSF.
     * @return o n�mero de propriedades da entidade que s�o exibidas numa tela de listagem.
     * @author lucio
     * @since 20110830
     */
    public int getPropertiesInQueryGridSize();

    /**
     * Este m�todo � �til para obter o tamanho da lista 
     * de propriedades antes de construir a lista.
     * Como o m�todo #getProperties() retorna uma Array,
     * o tamanho dela s� � obtido pelo m�todo length(), onde 
     * o padr�o JavaBean n�o � seguido. No atual m�todo o par�do
     * get � seguido.  
     * 
     * @return Retorna o tamanho da lista de propriedades.
     */
    public int getPropertiesSize();
    
    public boolean isAbstract();
    
    /**
     * Obtem a lista de grupos que agrupam as propriedades
     * da entidade em grupos que facilitam a visualiza��o
     * das informa��es da entidade
     * @since 20060413
     */
    public List<IGroupMetadata> getGroups();
}
