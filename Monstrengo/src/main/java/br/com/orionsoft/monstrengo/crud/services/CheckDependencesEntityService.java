package br.com.orionsoft.monstrengo.crud.services;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IEntityMetadata;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IPropertyMetadata;

/**
 * Dada uma entidade a ser excluida, este servi�o verifica quais s�o as 
 * entidades do sistema que a possuem como uma propriedade, para n�o ocorrer
 * erro de chave estrangeira no processo de exclus�o.
 * 
 * Este servi�o retorna uma estrutura de dados (static class DependencesBean) 
 * que comporta as entidades que est�o relacionadas a ela.
 * 
 * <p><b>Argumento:</b>
 * <br> IN_ENTITY: Entidade que ser� exclu�da.
 *  
 * <p><b>Procedimento:</b>
 * <li>� fornecida uma entidade</li>
 * <li>Verificar se a entidade fornecida � um atributo (propriedade) de alguma entidade do sistema </li>
 * <li>Se a entidade fornecida for uma propriedade de outra entidade do sistema, obter as 
 * entidades que est�o vinculadas a esta propriedade</li>
 * 
 * @version 20070126
 * 
 */
public class CheckDependencesEntityService extends ServiceBasic 
{
    public static String SERVICE_NAME = "CheckDependencesEntityService";
    public static String IN_ENTITY = "inEntity";

    public String getServiceName() {return SERVICE_NAME;}
    
    /**
     * Estrutura que comporta as entidades que mantem dependencia com a 
     * entidade fornecida (a entidade que se deseja deletar)
     * 
     * Recebe o nome de DependencesBean pois essa estrutura possui apenas 
     * propriedades get/set
     * 
     * @param entity - a entidade que possui a entidade fornecida (IN_ENTITY) como uma propriedade
     * @param property - a propriedade pela qual a entidade se relaciona com a entidade fornecida
     * @param entityList - a lista de entidades que est�o relacionadas a entidade
     *
     */
    public static class DependencesBean{
    	private IEntityMetadata entity;
    	private IPropertyMetadata property;
    	private IEntityList<?> entityList;
    	
		public IEntityMetadata getEntity() {return entity;}
		public void setEntity(IEntityMetadata entity) {this.entity = entity;}
		
		public IEntityList<?> getEntityList() {return entityList;}
		public void setEntityList(IEntityList<?> entityList) {this.entityList = entityList;}
		
		public IPropertyMetadata getProperty() {return property;}
		public void setProperty(IPropertyMetadata property) {this.property = property;}
    }
    
    public void execute(ServiceData serviceData) throws ServiceException
    {
        log.debug("Iniciando a execu��o do servi�o CheckDependencesEntityService");
        IEntity<?> inEntity = null; 

		try
        {
            log.debug("Obtendo os par�metros");
            inEntity = (IEntity<?>) serviceData.getArgumentList().getProperty(IN_ENTITY);
            
            /* Prepara a hierarquia de super classes da atual entidade
             * para verificar se h� alguma propriedade que se relaciona n�o com o tipo da entidade atual, 
             * mas com algum tipo das poss�veis super classes da entidade atual. COmo por exemplo, o ContratoAcademico
             * � uma subclasse do ContratoFincaneiro, que por sua vez, � uma subclasse do Contrato. No entanto, um Lancamento
             * se relaciona com a classe Contrato e n�o com a classe ContratoAcademico. Assim, � necess�rio percorrer 
             * todas as super classes de ContratoAcademico at� chegar em Contrato e detectar que um Lan�amento
             * pode estar relacionado com um ContratoAcademico */
            List<Class<?>> superTypes = new ArrayList<Class<?>>(3);
            Class<?> type = inEntity.getInfo().getType();
            while(!type.equals(Object.class)){
            	superTypes.add(type);
            	type = type.getSuperclass();
            }
            
            /*Pesquisando as outras entidades*/
            
			/* Obtem o mapa de TODAS as entidades cadastradas no sistema*/
			Map<String, IEntityMetadata> entities= this.getServiceManager().getEntityManager().getEntitiesMetadata();
			
			/* Este mapa ser� usado para cadastrar as classes j� analisadas, evitando que
			 * nas dependencias, tanto uma classe quanto a sua super classe sejam analisadas */
//			Map<String, IEntityMetadata> entitiesSuperType= new HashMap<String, IEntityMetadata>();

  			/* Para cada entidade analisa os relacionamentos  */
			for(IEntityMetadata entity: entities.values()){
				/* Verifica se a entidade n�o � uma classe abstrata.
				 * Se a entidade � uma classe abstrata, ou seja, somente 
				 * classes concretas, os objetos desta classe pertencem 
				 * a alguma classe concreta que ser� analisada*/
				if(!Modifier.isAbstract(entity.getType().getModifiers())){

					/*Verifica se a entidade da lista est� contida na lista de dependencias*/
					for(IPropertyMetadata property: entity.getProperties()){

						/* Verifica se a propriedade � uma entidade e se � a entidade que se quer deletar, 
						 * comparando-se os nomes, e ainda se n�o � uma propriedade calculada e se n�o � uma cole��o. */
						if(property.isEntity() && !property.isCalculated() && !property.isCollection()){

							/* Verifica se a propriedade � do tipo de umas das super classes da entidade atual */
							boolean dependence = false;
							for(Class<?> klazz: superTypes)
								if(property.getType().equals(klazz)){
									dependence = true;
									break;
								}
							/* Se a propriedade atual � do tipo (considerando a hierarquia) da entidade atual 
							 * ent�o busca no banco alguma entidade cuja sua propriedade aponte para
							 * o id da atual entidade */
							if(dependence){
								/*obtem a lista de entidades que est�o relacionadas com a entidade a ser deletada, procurando pelo id da entidade*/
								IEntityList<?> list;

								list = UtilsCrud.list(this.getServiceManager(), entity.getType(), IDAO.ENTITY_ALIAS_HQL + "." + property.getName() + "=" + inEntity.getId(), serviceData);
								if(list.size()>0){
									log.debug("CheckDependencesEntitiesService - Criando a estrutura DependencesBean");
									/* Cria uma bean de resultado que identificar� a depend�ncia */
									DependencesBean resultBean = new DependencesBean();
									/* Preenche a estrutura de depend�ncia com a lista das entidades dependentes */
									resultBean.setEntity(entity);
									resultBean.setProperty(property);
									resultBean.setEntityList(list);
									/* Adiciona a lista no resultado de saida do servi�o */
									serviceData.getOutputData().add(resultBean);
									log.debug("Estrutura DependencesBean criada com sucesso");
								}
							}
						}
					}
				}
			}
        } catch (DataIntegrityViolationException e) {
            log.fatal(e.getMessage());
            // O Servi�o n�o precisa adicionar mensagem local. O Manager j� indica qual srv falhou e os par�metros.
            throw new ServiceException(MessageList.create(CheckDependencesEntityService.class, "ERROR_DELETE_FOREING", inEntity.getObject().toString()));
        } catch (BusinessException e) {
            log.fatal(e.getErrorList());
            // O Servi�o n�o precisa adicionar mensagem local. O Manager j� indica qual srv falhou e os par�metros.
            throw new ServiceException(e.getErrorList());
//        } catch (HibernateException e) {
//            throw new ServiceException(MessageList.createSingleInternalError(e));
		}
    }
}