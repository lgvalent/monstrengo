package br.com.orionsoft.monstrengo.crud.services;

import javax.persistence.OneToMany;

import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.core.util.AnnotationUtils;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IProperty;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;

/**
 * Serviço de gravação de entidades.
 * 
 *<p><b>Argumento:</b>
 *<br>IN_ENTITY: Entidade que será gravada(nova/atualizada).
 *  
 *<p><b>Procedimento:</b>
 *<br> Obtem o Dao responsável pelo tipo da entidade.
 *<br> Solicita para o Dao gravar o objeto da entidade.
 * 
 * @author Marcia
 * @version 28/09/2005
 * 
 */
public class UpdateService extends ServiceBasic {
	
    public static String SERVICE_NAME = "UpdateService";
    public static String IN_ENTITY = "entity";
    
    public String getServiceName() {
    		return SERVICE_NAME;
    }
    
    public void execute(ServiceData serviceData) throws ServiceException{
        log.debug("Iniciando execução do servico UpdateService");
        
//      IDAO dao;
        try{
            // Obtendo os parâmetros
            IEntity<?> entity = (IEntity<?>) serviceData.getArgumentList().getProperty(IN_ENTITY);
            
            /*  Lucio 20100615: Esta validacao foi passada para o processo
            // Verificando se a entidade que será gravada é uma NOVA ou uma ATUALIZAÇÂO
            if(entity.getId()==IDAO.ENTITY_UNSAVED){
                // solicitando a validação da entidade pelo DvoManager
                if(this.getServiceManager().getEntityManager().getDvoManager().contains(entity))
                	this.getServiceManager().getEntityManager().getDvoManager().getDvoByEntity(entity).afterCreate(entity, serviceData);
            }
            
            // solicitando a validação da entidade pelo DvoManager
            if(this.getServiceManager().getEntityManager().getDvoManager().contains(entity))
            	this.getServiceManager().getEntityManager().getDvoManager().getDvoByEntity(entity).afterUpdate(entity, serviceData);
            */
//          Lucio 08/11/2006
//          Usava os DAOS para obter os dados, porem cada DAO gera uma sessão particular diferente da sessão 
//          já gerada para o serviço. Agora eu pego a atual sessão do serviço e dela solicito o UPDATE

//            log.debug("Obtendo o dao correspondente à entidade");
//            dao = daoManager.getDaoByEntity(entity.getInfo().getType());
//            
//            log.debug("Gravando o objeto da entidade");
//            dao.update(entity.getObject(), serviceData.getCurrentSession());

            for (IProperty prop : entity.getProperties()){
            	if(!prop.getInfo().isCalculated()&&prop.getInfo().isEntity()&&!prop.getValue().isValueNull()){
        			/* 1o. Trata rotinas de usabilidade */
        			/* Verifica se algum id ou entidade foi colocado no runId ou runEntity e não foi adicionado
        			 * na lista porque o operador geralmente esquece de clicar em Adicionar na tela e o item não vai para a coleção*/
        			if(prop.getInfo().isCollection()){
        				if((prop.getValue().getAsEntityCollection().getRunId() != null))
        					prop.getValue().getAsEntityCollection().runAdd();	
        			}

        			/* 2o. Trata salvamento dos relacionamentos */
            		/* Verifica propriedades EditShowEmbedded para salva elas primeiro */
            		if(!prop.getInfo().isCollection()&&prop.getInfo().isEditShowEmbedded()&&!prop.getInfo().isEmbedded()){
            			UtilsCrud.update(this.getServiceManager(), prop.getValue().getAsEntity(), serviceData);
            		}else
            			/* Verifica propriedades EditShowEmbedded para salvar os elementos primeiros */
            			if(prop.getInfo().isCollection()&&prop.getInfo().isEditShowEmbedded()){
            				for(IEntity<?> entityOneToMany: prop.getValue().getAsEntityCollection()){
            					/* Lucio 10/12/2007: Somente grava se o id é -1, pois, por enquanto,
            					 * as telas crud não permitem editar as subentidades. E ao gravar 
            					 * uma sub entidade já gravada, o hibernate está reclamento de
            					 * NonUniqueObjectException */
            					if(entityOneToMany.getId() == IDAO.ENTITY_UNSAVED){
            						/* Lucio 20110719: Se usar mappedBy e não @JoinColumn o hibernate não define o valor na entidade 
            						 * destino que está na coleção. Assim, a entidade é salva, mas fica sem dono.
            						 * Verifica se está sendo usado mappedBy pra fazer a injeção do pai manualmente */
            						OneToMany oneToMany = AnnotationUtils.findAnnotation(OneToMany.class, entity.getInfo().getType(), prop.getInfo().getName());
            						if((oneToMany!=null) && (oneToMany.mappedBy()!=null) && (!oneToMany.mappedBy().equals(""))){
            							entityOneToMany.setPropertyValue(oneToMany.mappedBy(), entity);
            						}
            						
            						
            						UtilsCrud.update(this.getServiceManager(), entityOneToMany, serviceData);
            					}
            				}
            			}
            	}
            }
            /* 20090331 Lucio - Usava-se session.saveOrUpdate(), mas ao gravar um novo socio
             * data um erro: Batch update returned unexpected row count from update [0]; actual row count: 0; expected: 1
             * lendo os forums achei esta opçao .merge(obj) que segundo a API coloca o atual objeto na atual sessao. Quando a sessao
             * for commitada ela decide se grava o obj ou atualiza. Precisa ser testado ainda se no influenciar o resto do sistema.
             */
            if(entity.getId() == IDAO.ENTITY_UNSAVED)
            	serviceData.getCurrentSession().save(entity.getObject());
            else
            	serviceData.getCurrentSession().merge(entity.getObject());
            /* 20090402 Lucio - O erro de Bacth Update era causado devido a uma restriçao de chave estrangeira do banco na tabela
             * basic_socio.juridica -> basic_juridica.id, sendo que na nova politica de persistencia definimos SINGLE_TABLE, o correto eh
             * basic_socio.juridica -> basic_pessoa.id. O mesmo acontecia com basic_socio.fisica.
             */
//            serviceData.getCurrentSession().saveOrUpdate(entity.getObject()); 
        } catch (BusinessException e){
            log.fatal(e.getErrorList());
            // O Serviço não precisa adicionar mensagem local. O Manager já indica qual srv falhou e os parâmetros.
            throw new ServiceException(e.getErrorList());
        }
        catch (Exception e){
            log.fatal(e.getMessage());
            // O Serviço não precisa adicionar mensagem local. O Manager já indica qual srv falhou e os parâmetros.
            throw new ServiceException(MessageList.createSingleInternalError(e));
        }
    }
}