package br.com.orionsoft.monstrengo.core.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;

import br.com.orionsoft.monstrengo.core.service.IService;
import br.com.orionsoft.monstrengo.core.service.IServiceManager;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.core.IApplication;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.crud.entity.IEntityManager;

/**
 * Gerenciador que mant�m o registro de todos os servi�os existentes e
 * controla transa��es. Existe uma �nica inst�ncia desse gerenciador
 * na aplica��o e todos os servi�os possuem refer�ncia a ele.
 *
 * @author Lucio
 *
 * @spring.bean id="ServiceManager" init-method="init"
 * @spring.property name="entityManager" ref="EntityManager"
 * @spring.property name="aplication" ref="Aplication"
 */
public class ServiceManager implements IServiceManager
{
    public static final String MANAGER_NAME = "ServiceManager";

    protected Logger log = LogManager.getLogger(getClass());

    private Map<String, IService> services;
    
    /**
     * Mantem uma refer�ncia para o gerenciador de entidades e seus metadados.
     * Os servi�os poder�o solicitar convers�o de Object para IEntity, e ainda,
     * List para IEntityList.
     */
    private IEntityManager entityManager;

    private IApplication application;

	public IApplication getApplication() {return application;}
	public void setApplication(IApplication application) {this.application = application;}

    public IEntityManager getEntityManager(){return entityManager;}
    public void setEntityManager(IEntityManager entityManager){this.entityManager = entityManager;}

	/**
	 * Este m�todo cria a lista de Servi�os, buscando todas as classes que
	 * implementam a interface IService, instanciando e registrando.
	 */
	@SuppressWarnings("unchecked")
	public void init(){
		if(services != null)
			throw new RuntimeException("ServiceManager j� iniciado anteriormente. O m�todo init() n�o pode ser executado.");
		
		services  = new HashMap<String, IService>();
		
		/* Prepara as entidades que implementam IService */
		for (Class<? extends IService> klazz: this.getApplication().findModulesClasses(IService.class)){
			log.info("Registrando Servi�o: " + klazz.getSimpleName());
			try {
				IService service = (IService) klazz.newInstance();
				service.setServiceManager(this);
				/* Solicita ao servi�o que ele se registre.
				 * Este m�todo pode ser utilizado pelo servi�o para preparar alguma estrutura 
				 * interna necess�ria */
				service.registerService();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
     * Obtem a inst�ncia do Servi�o com o nome fornecido.
     *
     * @param serviceName Nome do servi�o procurado.
     */
    public IService getServiceByName(String serviceName) throws ServiceException
    {
        if(log.isDebugEnabled())
           log.debug("Obtendo o servi�o pelo nome: " + serviceName);

        if (!services.containsKey(serviceName))
        {
            throw new ServiceException(MessageList.create(ServiceException.class, "SERVICE_NOT_FOUND", serviceName) );
        }

        return services.get(serviceName);
    }

    public void registerService(IService service) throws Exception
    {

    	// Verifica se h� servi�o duplicado
    	if (services.containsKey(service.getServiceName()))
    		throw new Exception("ServiceManager.registerService(): Servi�o duplicado." + service.getServiceName());

    	services.put(service.getServiceName(), service);
    }

    /**
     * Este m�todo executa um determinado servi�o e controla a quest�o de sess�es e transa��es do
     * mecanismo de persist�ncia (hibernate). <br>
     * Quando um servi�o inicia uma transa��o, somente no t�rmino da execu��o deste servi�o � que
     * a transa��o ser� confirmada (commit). Os demais servi�os que podem ser invocados dentro deste servi�o
     * utilizar�o a transa��o ativa.
     */
    public void execute(ServiceData serviceData) throws ServiceException
    {
        IService service=null;
        Transaction currentTransaction=null;

        if(log.isDebugEnabled())
            log.debug("::Executando o servi�o: " + serviceData.getServiceName() + "\n Par�metros:" + serviceData.toString());

        try
        {
            log.debug("Procurando o servi�o que ser� invocado");
            service = getServiceByName(serviceData.getServiceName());

            prepareSession(serviceData);

            log.debug("Verificando se � necess�rio criar um nova transa��o para o atual servi�o");
            if(service.isTransactional() && !checkTransactionStarted(serviceData))
            {
                log.debug("Indicando que uma transa��o foi ativada para o atual servi�o");
                serviceData.setTransactionStarted(true);

                log.debug("Iniciando uma transa��o");
                currentTransaction=serviceData.getCurrentSession().beginTransaction();

                log.debug("::Executando o servi�o dentro de uma transa��o");
                service.execute(serviceData);

                log.debug("::Verificando se o servi�o foi finalizado com sucesso");
                if(serviceData.getMessageList().isTransactionSuccess())
                {
                   log.debug("Executando COMMIT...");
                   commitTransaction(service, currentTransaction);
                 }else{
                   log.debug("Alguma opera��o interna do servi�o falhou. Executando ROLLBACK...");
                   rollbackTransaction(service, currentTransaction);
                 }

                 log.debug("::Fim da execu��o com transa��o: " + service.getServiceName());
            }
            else
            {
                log.debug("::Executando o servi�o sem criar uma nova transa��o");
                service.execute(serviceData);
                log.debug("::Fim da execu��o sem transa��o: " + service.getServiceName());
            }
        }
        catch (ServiceException e)
        {
            log.debug("O servi�o falhou. Executando ROLLBACK...");
            if(service!=null && currentTransaction!=null)
            	rollbackTransaction(service, currentTransaction);

            log.debug("Tratando erro ocorrido no servi�o " + serviceData.getServiceName() + ". Erro:" + e.getMessage());
            e.getErrorList().addAll(MessageList.create(ServiceException.class, "ERROR_EXECUTING_SERVICE", serviceData.getServiceName(), serviceData.toString()));

            throw new ServiceException(e.getErrorList());
        } catch (Exception e)
        {
            log.debug("O servi�o falhou. Executando ROLLBACK...");
            if(service!=null && currentTransaction!=null)
            	rollbackTransaction(service, currentTransaction);

            log.debug("Tratando erro geral ocorrido durante a execu��o do servi�o " + serviceData.getServiceName() + ". Erro:" + e.getMessage());
            throw new ServiceException(MessageList.createSingleInternalError(e));
        }
        finally
        {
            // Se for o primeiro servi�o a ser executado, fecha a sessao
            if (serviceData.getServiceDataOwner()==null)
                try
                {
                    log.debug("Finalizando a sess�o");
                    if (serviceData.getCurrentSession()!=null)
                    	serviceData.getCurrentSession().close();
                } catch (HibernateException e)
                {
                    e.printStackTrace();
                }
        }
    }

    /**
     * Este m�todo analisa a atual pilha de execu��o dos servi�os
     * para ver se alguma transa��o j� foi iniciada.
     *
     * @param serviceData
     * @return
     */
    private boolean checkTransactionStarted(ServiceData serviceData)
    {

        if(log.isDebugEnabled())
            log.debug("Pegando o pai do atual servi�o:" + serviceData.getServiceName());
        serviceData = serviceData.getServiceDataOwner();

        log.debug("Analisando todos os pais e a exist�ncia de uma transa��o ativa");
        while (serviceData != null)
        {
            log.debug("Verificando se o pai selecionado iniciou uma transa��o");
            if (serviceData.isTransactionStarted())
            {
                if(log.isDebugEnabled())
                    log.debug("Um pai com uma transa��o ativa foi encontrado:" + serviceData.getServiceName());
                return true;
            }
            if(log.isDebugEnabled())
            	log.debug("Andando na pilha de servi�os em execu��o: " + serviceData.getServiceName());
            serviceData = serviceData.getServiceDataOwner();
        }

        log.debug("Nenhum pai foi encontrado com uma transa��o ativa");
        return false;
    }

    /**
     * Verifica se ja existe alguma sess�o criada dispon�vel no serviceData.
     * Caso n�o exista cria uma nova e o adiciona no serviceData.
     * @param serviceData
     */
    private void prepareSession(ServiceData serviceData)
    {
        log.debug("Verificando a necessidade de criar uma sess�o");

        // Cria a sess�o somente para o primeiro Service Data chamado
        // Os demais, copiam a sess�o do seu pai.
        if (serviceData.getServiceDataOwner() == null)
        {
            try
            {
                log.debug("Criando uma nova sess�o");
                serviceData.setCurrentSession(this.getEntityManager().getDaoManager().getSessionFactory().openSession());

                /* Lucio 23/04/2009
                 * Ao usar o FlushMode.COMMIT e usar o LockMode.UPGRADE em algum objeto, o 
                 * hibernate atualizava o objeto 'lockado', mas ao dar um commit, um flush era 
                 * executado e uma versao antiga da entidade era gravada, o que causava 
                 * inconsistencia no sequenciaNumeroDocumento.
                 */
                 serviceData.getCurrentSession().setFlushMode(FlushMode.ALWAYS);
                 /* Lucio 19/09/2006
                  * Isto define para o hibernate somente executar um flush de entidades que est�o
                  * na mem�ria e que n�o foram para o banco quando houver uma opera��o commit.
                  * Isto porque, quando se relacionava alguns objetos transientes e ia executar
                  * alguma busca, ele tentava um flush e dava erro porque durante o flush ele encontrou
                  * objetos relacionados com outros objetos transientes */
                //serviceData.getCurrentSession().setFlushMode(FlushMode.COMMIT);
                /* Lucio 03/11/2006
                 * Volta a configura��o AUTO, porque durante o retorno de arquivos do banco
                 * algumas entidades eram criadas e depois os sub-servi�os executavam retrieve
                 * pelo id da entidade, no entanto ela ainda n�o tinha sido flushada */
                //serviceData.getCurrentSession().setFlushMode(FlushMode.AUTO);
            } catch (HibernateException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            log.debug("Usando a sess�o do servi�o antecessor");
            serviceData.setCurrentSession(serviceData.getServiceDataOwner().getCurrentSession());
        }

    }

    /**
     * Tenta realizar a opera��o commit para a transa��o informada
     * @param service Servi�o atuamente em execu��o
     * @param transaction Transa�ao correntemente ativa
     */
    private void commitTransaction(IService service, Transaction transaction) throws ServiceException{
        log.debug("Iniciando COMMIT...");
        try{
        	transaction.commit();
        }catch(HibernateException e){
            log.debug(e.getMessage());
            log.debug("COMMIT falhou. Executando ROLLBACK..");
            rollbackTransaction(service, transaction);

            ServiceException se = new ServiceException(MessageList.createSingleInternalError(e));
            se.getErrorList().add(new BusinessMessage(ServiceException.class, "SERVICE_COMMIT_FAILED", service.getServiceName()));

            throw se;
        }
        log.debug("COMMIT executado com sucesso!");
    }

    /**
     * Tenta realizar a opera��o rollback para a transa��o informada
     * @param service Servi�o atuamente em execu��o
     * @param transaction Transa�ao correntemente ativa
     */
    private void rollbackTransaction(IService service, Transaction transaction) throws ServiceException{
        log.debug("Iniciando ROLLBACK...");
        try{
        	transaction.rollback();
        }catch(HibernateException e){
        	log.debug(e.getMessage());
        	log.debug("ROLLBACK falhou");

        	ServiceException se = new ServiceException(MessageList.createSingleInternalError(e));
        	se.getErrorList().add(new BusinessMessage(ServiceException.class, "SERVICE_ROLLBACK_FAILED", service.getServiceName()));

        	throw se;
        }
        log.debug("ROLLBACK executado com sucesso!");
    }

}
