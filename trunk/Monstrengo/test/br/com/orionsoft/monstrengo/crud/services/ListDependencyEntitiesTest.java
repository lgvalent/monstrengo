package br.com.orionsoft.monstrengo.crud.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assert;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IEntityMetadata;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IPropertyMetadata;

public class ListDependencyEntitiesTest extends ServiceBasicTest
{
	
	public static void main(String[] args)
	{
		junit.textui.TestRunner.run(ListDependencyEntitiesTest.class);
	}
	
	public void testRun()
	{
		try
		{
			/* Prepara o mapa de relacionamento de entidades */
			Map<String, List<String>> entitiesDependency = new HashMap<String, List<String>>();
			
			/* Obtem o mapa de TODAS as entidades */
			Map<String, IEntityMetadata> entities= this.serviceManager.getEntityManager().getEntitiesMetadata();
			
			/* Constroi o mapa de relacionamento */
			for(IEntityMetadata entity: entities.values()){
				entitiesDependency.put(entity.getType().getSimpleName(), new ArrayList<String>());
			}
			
			/* Para cada entidade analisa os relacionamentos  */
			for(IEntityMetadata entity: entities.values()){
				for(IPropertyMetadata property: entity.getProperties())
					if(property.isEntity()){
						entitiesDependency.get(property.getType().getSimpleName()).add(entity.getType().getSimpleName() + (property.isCollection()?"*":""));
					}
			}
			
			/* Imprime o resultado */
			System.out.println("Entidade:");
			System.out.println("\t Reladionamentos:");
			for(Entry<String, List<String>> entry: entitiesDependency.entrySet()){
				System.out.println(entry.getKey());
				for(String value: entry.getValue()){
					System.out.println("\t" + value);
				}
			}
		}catch (BusinessException e)
		{
			UtilsTest.showMessageList(e.getErrorList());
			
			Assert.assertTrue(false);
		}
		
	}
	
}

