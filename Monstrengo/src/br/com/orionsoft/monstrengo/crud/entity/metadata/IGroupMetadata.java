package br.com.orionsoft.monstrengo.crud.entity.metadata;

import java.util.Comparator;
import java.util.List;

import br.com.orionsoft.monstrengo.crud.entity.metadata.IGroupMetadata;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IPropertyMetadata;

/**
 * Interface que define um metadados de um grupo de
 * propriedade de um entidade
 * @author Tatiana 20060413
 *
 */
public interface IGroupMetadata {
	
    /** Comparadores usados na criação de listas ordenadas das propriedades de uma entidade.
     *  Pode-se ordernar a lista por INDEX, GROUP ou LABEL (alfabética) */
	public static Comparator<IGroupMetadata> COMPARATOR_INDEX = new Comparator<IGroupMetadata>(){public int compare(IGroupMetadata arg0, IGroupMetadata arg1){return  new Integer(arg0.getIndex()).compareTo(arg1.getIndex());}};

	public static final int GROUP_NOT_DEFINED = -1;
	public int getIndex();
	
	/**
	 * É necessário este set() na interface, porque depois que um grupo
	 * é criado, ele poderá ter seu índice readequado para ficar
	 * em uma ordem sequencial e facilitar a construção de Arrays[] corretos,
	 * ou seja, sem índices nulos 
	 * @param index
	 */
	public void setIndex(int index);
	
    public String getName();
    public String getLabel();
    public String getHint();
    public String getDescription();
    
    public String getColorName();

    public List<IPropertyMetadata> getProperties();
    /**
     * Este método é util para comparar e localizar grupos em listas
     * e evitar que uma lista contenha um mesmo grupo mais de uma vez
     */
    public boolean equals(Object o);
}
