package br.com.orionsoft.monstrengo.crud.entity;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.model.SelectItem;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.test.EntityBasicTest;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;
import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IProperty;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IEntityMetadata;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IGroupMetadata;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IPropertyMetadata;
import br.com.orionsoft.monstrengo.crud.entity.metadata.MetadataException;
import br.com.orionsoft.monstrengo.security.entities.ApplicationEntity;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;

/**
 * Esta classe realiza testes para as Entidades gerenciadas pelo <b>EntityManager</b>.
 * <p>Obtem-se as entidades através da classe <b>Utils</b>, com o método <b>Utils.getAllBusinessEntity()</b>.
 * <p>Através do <b>metadataHandle</b>, seta-se as classes para que ele  possa buscar as informações referentes à
 * determinada classe no arquivo de propriedades.
 * <p>Testa-se todas as entidades através de um laço, percorrendo o vetor que contém todas as entidades.
 * <p>Testa-se também, as propriedades do atributo (propriedades das propriedades) para cada entidade, comparando-se 
 * os resultados obtidos pelo EntityManager com os resultados dados pelo MetadataHandle.
 * 
 * @author estagio
 *
 */
public class EntityManagerTestCase extends EntityBasicTest {
	
	IEntity entity;

//	public static void main(String[] args) {
//		junit.textui.TestRunner.run(EntityManagerTestCase.class);
//	}

	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@Test
	public void testQueryEntitySelectItem() throws MetadataException{
		try {
			List<SelectItem> result = this.entityManager.queryEntitySelectItems(ApplicationUser.class, "adm", 10);
			Assert.assertFalse(result.isEmpty());
			for(SelectItem item: result){
				System.out.println(item.getValue() + "->" + item.getLabel());
			}
		} catch (EntityException e) {
			Assert.fail(e.getMessage());
			e.printStackTrace();
		}
		
	}

	//	@Test
	public void testEntityManager () throws MetadataException{
		
		try {
			List<Class> listaClasses = UtilsTest.getAllBusinessEntity(this.entityManager.getDaoManager());
			System.out.println("Elementos Lista - " + listaClasses.size());
			System.out.println();
			
			for (Class entity : listaClasses){
				System.out.println("Elem Lista - " + entity);
				System.out.println();
				
				//Não instancia objetos de classe abstrata
				if (!Modifier.isAbstract(entity.getModifiers())){

				
					//informações da entidade
					//pegar instância da classe
					IEntity entityAux = entityManager.getEntity(entity.newInstance());
				
					/* seta a classe para o metadataHandle buscar as informações desta classe 
					 * no arquivo de propriedades. Isto tem que ser depois de entityManager.getEntity(),
					 * pois este gera os metadados de todos
					 */
					
					entityManager.getMetadataHandle().setEntityClass(entity);
					Assert.assertEquals(entity, entityManager.getMetadataHandle().getEntityClass());

					/*
					 * Testa as Propriedades da Entidade
					 */
					//testa getDescription
					//cada elemento tem a informação das propriedades
					String stringEntity = entityAux.getInfo().getDescription();

					//esta informação vem direto do arquivo
					String stringMetadata = entityManager.getMetadataHandle().getEntityDescription();
					System.out.println("entityDescription - " + stringEntity);
					System.out.println("metadataDescription - " + stringMetadata);
					Assert.assertEquals(stringEntity, stringMetadata);
					
					//testa getHint
					stringEntity = entityAux.getInfo().getHint();
					stringMetadata = entityManager.getMetadataHandle().getEntityHint();
					System.out.println("entityHint - " + stringEntity);
					System.out.println("metadataHint - " + stringMetadata);
					Assert.assertEquals(stringEntity, stringMetadata);
					
					//testa getLabel
					stringEntity = entityAux.getInfo().getLabel();
					stringMetadata = entityManager.getMetadataHandle().getEntityLabel();
					System.out.println("entityLabel - " + stringEntity);
					System.out.println("metadataLabel - " + stringMetadata);
					Assert.assertEquals(stringEntity, stringMetadata);
					
					/*
					 * Obtém os campos da classe
					 */
					//getAllFields é usado para concatenar os vetores com os campos da SuperClasse e da SubClasse
//					List allFields = getAllFields(entity);

					/*
					 * obtém as propriedades indexadas no metadado da classe
					 * array vai ter monte de objetos do tipo property; cada objeto tem as informacoes (getInfo),
					 * essas infos retornam as propriedades do atributo (propriedades das propriedades)
					 * getInfo devolve um conjunto de metadados
					 */ 
					IProperty[] propertyVector = entityAux.getProperties();
					
					//testa se os campos obtidos pelo entity são iguais aos obtidos diretamente da classe
					//Assert.assertEquals(allFields.size(), propertyVector.length);
					
					//Teste das propriedades do metadado (propriedades das propriedades)
					for (IProperty property : propertyVector){
                        System.out.println("prop: " + property.getInfo().getLabel() + "=>" + property.getInfo().getIndex());
						//testa getColorName
						stringEntity = property.getInfo().getColorName();
						stringMetadata = entityManager.getMetadataHandle().getPropertyColorName(property.getInfo().getName());
						Assert.assertEquals(stringEntity, stringMetadata);
						
						//testa getDescription
						stringEntity = property.getInfo().getDescription();
						stringMetadata = entityManager.getMetadataHandle().getPropertyDescription(property.getInfo().getName());
						Assert.assertEquals(stringEntity, stringMetadata);
					
						//testa getDisplayFormat
						stringEntity = property.getInfo().getDisplayFormat();
						stringMetadata = entityManager.getMetadataHandle().getPropertyDisplayFormat(property.getInfo().getName());
						Assert.assertEquals(stringEntity, stringMetadata);
						
						//testa getEditMask
						stringEntity = property.getInfo().getEditMask();
						stringMetadata = entityManager.getMetadataHandle().getPropertyEditMask(property.getInfo().getName());
						System.out.println("EditMask="+stringMetadata);
                        /* Vai dar ERRO aqui pois .editMask está sendo pego por default, e é diferente de  " " (vazio) */
                        Assert.assertEquals(stringEntity, stringMetadata);
					
						//testa getHint
						stringEntity = property.getInfo().getHint();
						stringMetadata = entityManager.getMetadataHandle().getPropertyHint(property.getInfo().getName());
						Assert.assertEquals(stringEntity, stringMetadata);

						//testa getLabel 
						stringEntity = property.getInfo().getLabel();
						stringMetadata = entityManager.getMetadataHandle().getPropertyLabel(property.getInfo().getName());					
						Assert.assertEquals(stringEntity, stringMetadata);

						//testa getName
						stringEntity = property.getInfo().getName();
						stringMetadata = entityManager.getMetadataHandle().getPropertyName(property.getInfo().getName());
						Assert.assertEquals(stringEntity, stringMetadata);

						/*
						 * testa getType - classMetadata será diferente de null se a entidade for propriedade
						 * de uma outra entidade
						 */
						Class classProperty = property.getInfo().getType();
						Class classPropertyMetadata = entityManager.getMetadataHandle().getPropertyType(property.getInfo().getName());
						if (classPropertyMetadata != null){
							Assert.assertEquals(classProperty, classPropertyMetadata);	
						}
						
						//testa getIndex
						double intEntity = property.getInfo().getIndex();
						double intMetadata = entityManager.getMetadataHandle().getPropertyIndex(property.getInfo().getName());
//						Assert.assertEquals(intEntity, intMetadata);
						
						//testa getMaximum
						intEntity = property.getInfo().getMaximum();
						intMetadata = entityManager.getMetadataHandle().getPropertyMaximum(property.getInfo().getName());
						Assert.assertEquals(intEntity, intMetadata);
						
						//testa getMinimum
						intEntity = property.getInfo().getMinimum();
						intMetadata = entityManager.getMetadataHandle().getPropertyMinimum(property.getInfo().getName());
						Assert.assertEquals(intEntity, intMetadata);
						
						//testa getSize
						intEntity = property.getInfo().getSize();
						intMetadata = entityManager.getMetadataHandle().getPropertySize(property.getInfo().getName());
						Assert.assertEquals(intEntity, intMetadata);

						//testa getValuesList
						List listEntity = property.getInfo().getValuesList();
						List listMetadata = entityManager.getMetadataHandle().getPropertyValuesList(property.getInfo().getName());
						Assert.assertEquals(listEntity, listMetadata);
						
						//testa isEditShowList
						boolean booleanEntity = property.getInfo().isEditShowList();
						boolean booleanMetadata = entityManager.getMetadataHandle().getPropertyEditShowList(property.getInfo().getName());
						Assert.assertEquals(booleanEntity, booleanMetadata);
						
						//testa isList
						booleanEntity = property.getInfo().isList();
						booleanMetadata = entityManager.getMetadataHandle().getPropertyIsList(property.getInfo().getName());
						Assert.assertEquals(booleanEntity, booleanMetadata);
						
						//testa isReadOnly
						booleanEntity = property.getInfo().isReadOnly();
						booleanMetadata = entityManager.getMetadataHandle().getPropertyReadOnly(property.getInfo().getName());
						Assert.assertEquals(booleanEntity, booleanMetadata);
						
						//testa isRequired
						booleanEntity = property.getInfo().isRequired();
						booleanMetadata = entityManager.getMetadataHandle().getPropertyRequired(property.getInfo().getName());
						System.out.println(booleanEntity);
                        System.out.println(booleanMetadata);
                        Assert.assertEquals(booleanEntity, booleanMetadata);
						
						//testa isVisible
						booleanEntity = property.getInfo().isVisible();
						booleanMetadata = entityManager.getMetadataHandle().getPropertyVisible(property.getInfo().getName());
						Assert.assertEquals(booleanEntity, booleanMetadata);
					}
										
					System.out.println();
			
				}//if abstrato
			}//for 1o

            Assert.assertTrue(true);
		} catch (Exception e) {
            e.printStackTrace();

            Assert.assertTrue(false);

		}
	}	
	
	/**
	 * Método que obtém os campos (fields) de determinada entidade, inclusive os herdados da SuperClasse.
	 * <p>O método verifica se existe uma superclasse associada à classe, e armazena os campos em uma lista
	 * (<b>List</b>); logo em seguida, concatena os campos da própria classe nesta lista.
	 * 
	 * @param entityClass
	 * @return uma Lista com os campos (fields)
	 */
	public List getAllFields(Class entityClass){
		List<Field> allFields = new ArrayList<Field>();
		int i = 0;
		int j = 0;
		int k = 0;
		
		//se existir superclass
		if (entityClass.getSuperclass() != Object.class){
			Field[] fieldSuperClass = entityClass.getSuperclass().getDeclaredFields();
			for (i = 0; i < fieldSuperClass.length; i++){
				if (!java.lang.reflect.Modifier.isStatic(fieldSuperClass[i].getModifiers())){
					allFields.add(k, fieldSuperClass[i]);
				}
			}
		}
		
		//caso haja uma superclasse, o contador i indica o ponto em que a lista deve ser preenchida (concatena)
		Field[] fields = entityClass.getDeclaredFields();
		for (j = i; j < (i + fields.length); j++){
				if (!java.lang.reflect.Modifier.isStatic(fields[j-i].getModifiers())){	
					allFields.add(k, fields[j-i]);
					k++;	
				}	
		}//for
		
		return allFields;
	}
	
    public void testGetEntitiesMetadata()
    {
        try
        {
            System.out.println("Testando:" +this.getClass().getName());
            System.out.println(":Obtem a lista de TODAS as entidades registradas.");
            Collection<IEntityMetadata> coll = this.entityManager.getEntitiesMetadata().values();
            

            System.out.println(":Obtem a lista de entidades de todos os DAOS registrados como Bean no contexto ctx");
            List<Class> list2 = UtilsTest.getAllBusinessEntity(this.ctx);
            
            
            if (coll.size() != list2.size())
            {
                System.out.println(":ERRO: Está registrada e não arquivada em .properties.");
                for(IEntityMetadata metadata: coll)
                    if (!list2.contains(metadata.getType()))
                        System.out.println(metadata);
                
                System.out.println(":ERRO: Está arquivada e não registrada pelos Daos.");
                for(Class klazz: list2){
                	boolean achou = false;
                    for(IEntityMetadata metadata: coll){
                    	if(metadata.getType().equals(klazz))
                    		achou = true;
                    }
                    if(!achou)
                    	System.out.println(klazz);
                }
                
                throw new Exception("ERRO: O número de entidades registradas difere das indicadas em arquivo.Registradas:" + coll.size() + " Arquivadas:" + list2.size());
            }

            Assert.assertTrue(true);
        } 
        catch (BusinessException e)
        {
            for(BusinessMessage er: e.getErrorList())
                System.out.println(er.getMessageClass().getSimpleName() + ":" + er.getErrorKey() + "=" + er.getMessage());
            
            Assert.assertTrue(false);
        } 
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            
            Assert.assertTrue(false);
            
        }
    }

    @SuppressWarnings("unchecked")
	public void testListAllEntitiesProperty()
    {
        try
        {
            Collection<IEntityMetadata> colls = this.entityManager.getEntitiesMetadata().values();
            List<IEntityMetadata> list = new ArrayList<IEntityMetadata>(colls);

            /* Ordena a lista por ordem alfabética */
			Collections.<IEntityMetadata>sort(list, new Comparator<IEntityMetadata>(){public int compare(IEntityMetadata c1, IEntityMetadata c2){return c1.getLabel().compareTo(c2.getLabel());}});

			/* Busca e adiciona os metadados de cada entidade */
            for(IEntityMetadata ent: list){
            	System.out.println("Entidade:,"+ ent.getLabel());
            	for(IPropertyMetadata prop: ent.getPropertiesInQueryGrid()){
                	System.out.println(prop.getLabel() + ",\"" + prop.getHint() + "\",\"" + prop.getDescription()+"\"");
            	}
            	System.out.println("");
            }

            Assert.assertTrue(true);
        } 
        catch (BusinessException e)
        {
            for(BusinessMessage er: e.getErrorList())
                System.out.println(er.getMessageClass().getSimpleName() + ":" + er.getErrorKey() + "=" + er.getMessage());
            
            Assert.assertTrue(false);
        } 
    }

    public void testGetEntitiesMetadataDefault()
    {
        try
        {
            System.out.println("Testando:" +this.getClass().getName());
            System.out.println(":Obtem os metadados padrões da classe.");
            IEntityMetadata entityMetadata = this.entityManager.getEntityMetadataDefaults(ApplicationEntity.class);
            
            System.out.println(":Mostrando as propriedades");
            for(IPropertyMetadata pro: entityMetadata.getPropertiesMetadata().values()){
                System.out.println("Mostrando a propriedade " + pro.getName());
                System.out.println(":: " + pro.getLabel());
            }

            System.out.println(":Mostrando os grupos");
            for(IGroupMetadata group: entityMetadata.getGroups()){
                System.out.println("Mostrando o grupo " + group);
                System.out.println(":: " + group.getName());
            }
            
//            for(Object obj: BeanUtils.describe(entityMetadata).entrySet()){
//            	Entry entry = (Entry) obj;
//            	System.out.println(":: " + entry.getKey() + "=" + entry.getValue() + "=>" + entry.getValue().getClass().getName());
//                if(entry.getValue() instanceof Map)
//            	for(Object objPro: ((Map)obj).entrySet()){
//                	Entry entryPro = (Entry) objPro;
//                	System.out.println(":::: " + entryPro.getKey() + "=" + entryPro.getValue());
//                }
//            }
            Assert.assertTrue(true);
        } 
        catch (BusinessException e)
        {
            for(BusinessMessage er: e.getErrorList())
                System.out.println(er.getMessageClass().getSimpleName() + ":" + er.getErrorKey() + "=" + er.getMessage());
            
            Assert.assertTrue(false);
        } 
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            
            Assert.assertTrue(false);
            
        }
    }


}

