package br.com.orionsoft.monstrengo.core.process;

import java.util.List;

import br.com.orionsoft.monstrengo.core.process.IProcess;
import br.com.orionsoft.monstrengo.core.process.ProcessParamBasic;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;

public class ProcessParamEntityList<E> extends ProcessParamBasic<IEntityList<E>> {

	private Class<E> entityType;
	private String staticHqlWhereFilter;

	public ProcessParamEntityList(Class<E> entityType, boolean required, IProcess processOwner) {
		super(List.class, required, processOwner);
		this.entityType = entityType;
	}
	
	public List<IEntity<E>> getList(String filter) {
		return getList(filter, 10);
	}

	@SuppressWarnings("unchecked")
	public List<IEntity<E>> getList(String filter, int resultLimit) {
		// A lista é criada depois com o tamanho já otimizado
		try{
			List<?> result = this.getProcess().getProcessManager().getServiceManager().getEntityManager().queryEntities(this.entityType, filter, staticHqlWhereFilter, resultLimit).getList();
			
			return (List<IEntity<E>>) result;
		}
		catch (BusinessException e)
		{
			throw new RuntimeException(e.getMessage());
		}
	}

	public List<IEntity<E>> getList(int resultLimit) {
		return getList("", resultLimit);
	}


	/**
	 * Prepara uma lista de entidade na primeira chamada que ocorrer
	 * pela busca do valor.
	 * Esta lista tem que ser terdiamente criada, pois quando
	 * um parâmetro de um processo é criado em seu construtor,
	 * o próprio processo ainda não tem referências aos Managers
	 * usados aqui
	 */
	@Override
	public IEntityList<E> getValue() {
		if(isNull()){
			try {
				/* Cria uma lista de entidade preparada para busca e definições */
				setValue(this.getProcess().getProcessManager().getServiceManager().getEntityManager().getEntityList(null, entityType));
			} catch (EntityException e) {
				e.printStackTrace();
				throw new RuntimeException(e.getMessage());
			}
		}

		return super.getValue();
	}

	public String getStaticHqlWhereFilter() {return staticHqlWhereFilter;}
	public void setStaticHqlWhereFilter(String staticHqlFilter) {this.staticHqlWhereFilter = staticHqlFilter;}

}
