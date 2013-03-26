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
 * Gerenciador que mantém o registro de todos os serviços existentes e
 * controla transações. Existe uma única instância desse gerenciador
 * na aplicação e todos os serviços possuem referência a ele.
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
     * Mantem uma referência para o gerenciador de entidades e seus metadados.
     * Os serviços poderão solicitar conversão de Object para IEntity, e ainda,
     * List para IEntityList.
     */
    private IEntityManager entityManager;

    private IApplication application;

	public IApplication getApplication() {return application;}
	public void setApplication(IApplication application) {this.application = application;}

    public IEntityManager getEntityManager(){return entityManager;}
    public void setEntityManager(IEntityManager entityManager){this.entityManager = entityManager;}

	/**
	 * Este método cria a lista de Serviços, buscando todas as classes que
	 * implementam a interface IService, instanciando e registrando.
	 */
	@SuppressWarnings("unchecked")
	public void init(){
		if(services != null)
			throw new RuntimeException("ServiceManager já iniciado anteriormente. O método init() não pode ser executado.");
		
		services  = new HashMap<String, IService>();
		
		/* Prepara as entidades que implementam IService */
		for (Class<? extends IService> klazz: this.getApplication().findModulesClasses(IService.class)){
			log.info("Registrando Serviço: " + klazz.getSimpleName());
			try {
				IService service = (IService) klazz.newInstance();
				service.setServiceManager(this);
				/* Solicita ao serviço que ele se registre.
				 * Este método pode ser utilizado pelo serviço para preparar alguma estrutura 
				 * interna necessária */
				service.registerService();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
     * Obtem a instância do Serviço com o nome fornecido.
     *
     * @param serviceName Nome do serviço procurado.
     */
    public IService getServiceByName(String serviceName) throws ServiceException
    {
        if(log.isDebugEnabled())
           log.debug("Obtendo o serviço pelo nome: " + serviceName);

        if (!services.containsKey(serviceName))
        {
            throw new ServiceException(MessageList.create(ServiceException.class, "SERVICE_NOT_FOUND", serviceName) );
        }

        return services.get(serviceName);
    }

    public void registerService(IService service) throws Exception
    {

    	// Verifica se há serviço duplicado
    	if (services.containsKey(service.getServiceName()))
    		throw new Exception("ServiceManager.registerService(): Serviço duplicado." + service.getServiceName());

    	services.put(service.getServiceName(), service);
    }

    /**
     * Este método executa um determinado serviço e controla a questão de sessões e transações do
     * mecanismo de persistência (hibernate). <br>
     * Quando um serviço inicia uma transação, somente no término da execução deste serviço é que
     * a transação será confirmada (commit). Os demais serviços que podem ser invocados dentro deste serviço
     * utilizarão a transação ativa.
     */
    public void execute(ServiceData serviceData) throws ServiceException
    {
        IService service=null;
        Transaction currentTransaction=null;

        if(log.isDebugEnabled())
            log.debug("::Executando o serviço: " + serviceData.getServiceName() + "\n Parämetros:" + serviceData.toString());

        try
        {
            log.debug("Procurando o serviço que será invocado");
            service = getServiceByName(serviceData.getServiceName());

            prepareSession(serviceData);

            log.debug("Verificando se é necessário criar um nova transação para o atual serviço");
            if(service.isTransactional() && !checkTransactionStarted(serviceData))
            {
                log.debug("Indicando que uma transação foi ativada para o atual serviço");
                serviceData.setTransactionStarted(true);

                log.debug("Iniciando uma transação");
                currentTransaction=serviceData.getCurrentSession().beginTransaction();

                log.debug("::Executando o serviço dentro de uma transação");
                service.execute(serviceData);

                log.debug("::Verificando se o serviço foi finalizado com sucesso");
                if(serviceData.getMessageList().isTransactionSuccess())
                {
                   log.debug("Executando COMMIT...");
                   commitTransaction(service, currentTransaction);
                 }else{
                   log.debug("Alguma operação interna do serviço falhou. Executando ROLLBACK...");
                   rollbackTransaction(service, currentTransaction);
                 }

                 log.debug("::Fim da execução com transação: " + service.getServiceName());
            }
            else
            {
                log.debug("::Executando o serviço sem criar uma nova transação");
                service.execute(serviceData);
                log.debug("::Fim da execução sem transação: " + service.getServiceName());
            }
        }
        catch (ServiceException e)
        {
            log.debug("O serviço falhou. Executando ROLLBACK...");
            if(service!=null && currentTransaction!=null)
            	rollbackTransaction(service, currentTransaction);

            log.debug("Tratando erro ocorrido no serviço " + serviceData.getServiceName() + ". Erro:" + e.getMessage());
            e.getErrorList().addAll(MessageList.create(ServiceException.class, "ERROR_EXECUTING_SERVICE", serviceData.getServiceName(), serviceData.toString()));

            throw new ServiceException(e.getErrorList());
        } catch (Exception e)
        {
            log.debug("O serviço falhou. Executando ROLLBACK...");
            if(service!=null && currentTransaction!=null)
            	rollbackTransaction(service, currentTransaction);

            log.debug("Tratando erro geral ocorrido durante a execução do serviço " + serviceData.getServiceName() + ". Erro:" + e.getMessage());
            throw new ServiceException(MessageList.createSingleInternalError(e));
        }
        finally
        {
            // Se for o primeiro serviço a ser executado, fecha a sessao
            if (serviceData.getServiceDataOwner()==null)
                try
                {
                    log.debug("Finalizando a sessão");
                    if (serviceData.getCurrentSession()!=null)
                    	serviceData.getCurrentSession().close();
                } catch (HibernateException e)
                {
                    e.printStackTrace();
                }
        }
    }

    /**
     * Este método analisa a atual pilha de execução dos serviços
     * para ver se alguma transação já foi iniciada.
     *
     * @param serviceData
     * @return
     */
    private boolean checkTransactionStarted(ServiceData serviceData)
    {

        if(log.isDebugEnabled())
            log.debug("Pegando o pai do atual serviço:" + serviceData.getServiceName());
        serviceData = serviceData.getServiceDataOwner();

        log.debug("Analisando todos os pais e a existência de uma transação ativa");
        while (serviceData != null)
        {
            log.debug("Verificando se o pai selecionado iniciou uma transação");
            if (serviceData.isTransactionStarted())
            {
                if(log.isDebugEnabled())
                    log.debug("Um pai com uma transação ativa foi encontrado:" + serviceData.getServiceName());
                return true;
            }
            if(log.isDebugEnabled())
            	log.debug("Andando na pilha de serviços em execução: " + serviceData.getServiceName());
            serviceData = serviceData.getServiceDataOwner();
        }

        log.debug("Nenhum pai foi encontrado com uma transação ativa");
        return false;
    }

    /**
     * Verifica se ja existe alguma sessão criada disponível no serviceData.
     * Caso não exista cria uma nova e o adiciona no serviceData.
     * @param serviceData
     */
    private void prepareSession(ServiceData serviceData)
    {
        log.debug("Verificando a necessidade de criar uma sessão");

        // Cria a sessão somente para o primeiro Service Data chamado
        // Os demais, copiam a sessão do seu pai.
        if (serviceData.getServiceDataOwner() == null)
        {
            try
            {
                log.debug("Criando uma nova sessão");
                serviceData.setCurrentSession(this.getEntityManager().getDaoManager().getSessionFactory().openSession());

                /* Lucio 23/04/2009
                 * Ao usar o FlushMode.COMMIT e usar o LockMode.UPGRADE em algum objeto, o 
                 * hibernate atualizava o objeto 'lockado', mas ao dar um commit, um flush era 
                 * executado e uma versao antiga da entidade era gravada, o que causava 
                 * inconsistencia no sequenciaNumeroDocumento.
                 */
                 serviceData.getCurrentSession().setFlushMode(FlushMode.ALWAYS);
                 /* Lucio 19/09/2006
                  * Isto define para o hibernate somente executar um flush de entidades que estão
                  * na memória e que não foram para o banco quando houver uma operação commit.
                  * Isto porque, quando se relacionava alguns objetos transientes e ia executar
                  * alguma busca, ele tentava um flush e dava erro porque durante o flush ele encontrou
                  * objetos relacionados com outros objetos transientes */
                //serviceData.getCurrentSession().setFlushMode(FlushMode.COMMIT);
                /* Lucio 03/11/2006
                 * Volta a configuração AUTO, porque durante o retorno de arquivos do banco
                 * algumas entidades eram criadas e depois os sub-serviços executavam retrieve
                 * pelo id da entidade, no entanto ela ainda não tinha sido flushada */
                //serviceData.getCurrentSession().setFlushMode(FlushMode.AUTO);
            } catch (HibernateException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            log.debug("Usando a sessão do serviço antecessor");
            serviceData.setCurrentSession(serviceData.getServiceDataOwner().getCurrentSession());
        }

    }

    /**
     * Tenta realizar a operação commit para a transação informada
     * @param service Serviço atuamente em execução
     * @param transaction Transaçao correntemente ativa
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
     * Tenta realizar a operação rollback para a transação informada
     * @param service Serviço atuamente em execução
     * @param transaction Transaçao correntemente ativa
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
