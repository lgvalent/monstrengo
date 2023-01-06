package br.com.orionsoft.monstrengo.crud.entity;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.beanutils.PropertyUtils;

import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityCollection;
import br.com.orionsoft.monstrengo.crud.entity.PropertyValueException;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;


/**
 * Esta classe implementa atributos comuns
 * às entidades EntitySet e EntityList
 *
 */
public abstract class EntityCollection<T> implements IEntityCollection<T>
{
	private Long runId=null;
	
	/* Esta entidade nunca deve ser nula, mas sim, uma entidade vazia, desta forma
	 * no primeiro get que houver, se ela estiver nula ela é criada */
	private IEntity<T> runEntity=null;
	
	public Long getRunId() {return runId;}
	public void setRunId(Long runId) {this.runId = runId;}

	public IEntity<T> getRunEntity() {
		if(runEntity == null )
			try {
				prepareRunParams();
			} catch (BusinessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return runEntity;
		
	}
	public void setRunEntity(IEntity<T> runEntity) {this.runEntity = runEntity;}

	/**
	 * Este método utiliza as variáveis runId e runEntity para executar suas operações
	 */
	public boolean runAdd() throws BusinessException {
    	boolean result = false;

    	/* Verifica se a entidade já se encontra na lista */
		for(IEntity<T> ent: this){
			if((this.runId!=null) && ent.getId()==this.runId){
    	    	/* A entidade já se encontra na lista */
    			result = true;
    			break;
    		}
    	}

		if(!result){
			/* A entidade ainda não se encontra na lista, adiciona ela */
			/* Se foi definido um runId então usa ele */
			if(this.runId != null)
				this.runEntity  = (IEntity<T>) UtilsCrud.retrieve(this.getEntityManager().getServiceManager(), 
						this.getInfo().getType(),
						this.runId,
						null);
			
			/* Adiciona ela na lista */
			this.add(this.runEntity);

			result = true;
		}
		
    	/* Limpa as variáveis de runId e runEntity */
    	prepareRunParams();

    	return result;
	}


	/**
	 * Este método utiliza as variáveis runId e runEntity para executar suas operações
	 */
	public boolean runRemove() throws BusinessException {
		boolean result = false;
		/* Verifica se o elemento está na lista para removretrieveer */
		for(IEntity<T> ent: this){
    		/* Compara pelos ids */
    		if((this.runId != null) && (ent.getId() == this.runId)){
        		/* Remove a entidade da lista */
    			this.remove(ent);
    			result = true;
    			break;
    		}else
        		/* Compara pelos objeto */
        		if((this.runEntity!=null)&&(ent.getObject().equals(this.runEntity.getObject()))){
            		/* Remove a entidade da lista */
        			this.remove(ent);
        			result = true;
        			break;
        		}
    			
    	}
		
		/* Limpa as variáveis de runId e runEntity */
		prepareRunParams();
		
		/* O id não foi encontrado para remoção */
		return result;
	}

	/**
	 * Este método remove todas as entidades que estão selecionadas na coleção
	 */
	public void runRemoveSelected() throws BusinessException {
		/* Verifica os elementos marcados */
		for(Iterator<IEntity<T>> it = this.iterator(); it.hasNext();){
			IEntity<T> ent = it.next(); 
			if(ent.isSelected()){
				/* Remove a entidade da lista */
				it.remove();
				
				// Lucio 20110325: Comentei isto, pois parece que remove duas vezes!!!
				// this.remove(ent);
			}
		}
	}

	private void prepareRunParams() throws BusinessException {
		this.runId = null;
		IEntity<T> create = (IEntity<T>) UtilsCrud.create(this.getEntityManager().getServiceManager(),
				                          this.getInfo().getType(),
				                          null);
		this.runEntity = create;
	}
	
	/**
	 * Obtem a lista de valores dos id's dos objetos que estão na lista.<br>
     * Este método é útil para que a própria interface exiba uma lista
     * de opção de entidades e defina na lista atual as entidades que foram
     * marcadas. 
	 */
	public Long[] getIds() throws BusinessException
	{
		try {
			Long[] result = new Long[this.size()];

			if(result.length >0){
				int i = 0;
				for(IEntity<T> obj: this){
					if(obj.isSelected()){
						result[i] = obj.getId();
						i++;
					}
				}
				
				/* Redimensiona o vetor para o espaço útil */
				result = Arrays.copyOfRange(result, 0, i);
			}
			return result;
			
		} catch (Exception e) {
			throw new BusinessException(MessageList.createSingleInternalError(e));
		}
	}

	/**
	 * A cada Id apresentado, o objeto é buscado no DAO responsável e armazenado.
	 * <b>OBSERVAÇÃO</b> Quando estiver dentro de um serviço é ideal que utilize o método setIds(Long[], ServiceData)
	 * para preservar a sessão e transação do serviço.
	 *  
	 * @throws PropertyValueException 
	 * 
	 */
	public void setIds(Long[] ids) throws BusinessException
	{
		setIds(ids, null);
	}
	
	public void setIds(Long[] ids, ServiceData serviceDataOwner) throws BusinessException
	{
		/* Limpa as atuais entidades da lista para adicionar as entidades referenciadas pelos ids */ 
		this.clear();

		/* Obtem, pelo id, as entidades e coloca-as na atual coleção */ 
		for (int i = 0; i < ids.length; i++){
			IEntity<T> entity = (IEntity<T>) UtilsCrud.retrieve(this.getEntityManager().getServiceManager(), this.getInfo().getType(), ids[i], serviceDataOwner);

			this.add(entity);
		}

	}
	
    /**
     * Este método cria uma lista de entidades em forma de uma lista de seleção. Muito
     * útil quando algum processo quer gerar exibir uma lista de possíveis entidades
     * 
     * @return Retorna uma lista de itens de seleção preenchido com as entidades atuais da lista.
     * @throws EntityException
     * @since 20070607
     */
    public List<SelectItem> getEntitySelectItems() throws EntityException{
		try {
			List<SelectItem> result = new ArrayList<SelectItem>(this.getObjectCollection().size());

			for(Object oEntity: this.getObjectCollection()){
				result.add(new SelectItem(PropertyUtils.getProperty(oEntity, IDAO.PROPERTY_ID_NAME), oEntity.toString()));
			}
			
			return result;
		} catch (Exception e) {
			throw new EntityException(MessageList.createSingleInternalError(e));
		}
    }


	

}
