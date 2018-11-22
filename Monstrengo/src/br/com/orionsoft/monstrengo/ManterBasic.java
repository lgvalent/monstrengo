package br.com.orionsoft.monstrengo;

import java.lang.reflect.Method;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.IServiceManager;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IProperty;


/**
 * Esta classe possui as rotinas básicas das classes Manter que são
 * implementadas em cada módulo.<br>
 * Essa classe faz a verificação da existencia de um determinado registro.
 * Se o registro ja existir no banco, será retornada a entidade
 * prenchida com as informações desse registro, caso contrario
 * sera criada uma nova entidade e gravado no banco. 
 * 
 * @author lucio 20071104
 * @version 20071104
 */
public class ManterBasic
{
    protected IServiceManager serviceManager = null;
    protected ServiceData serviceData = null;
    /**
     * Manipulador de Log para ser utilizado pelas implementações dos
     * serviços. 
     */
    protected Logger log = LogManager.getLogger(getClass());
    
    public ManterBasic(IServiceManager serviceManager, ServiceData serviceDataOwner)
    {
        this.serviceManager = serviceManager;
        if(serviceDataOwner== null){
        	/** Copiado do método privado ServiceManager.prepareSession() */
            log.debug("Criando uma nova sessão");
            serviceDataOwner = new ServiceData("", null);
            serviceDataOwner.setCurrentSession(serviceManager.getEntityManager().getDaoManager().getSessionFactory().openSession());

            /* Lucio 23/04/2009
             * Ao usar o FlushMode.COMMIT e usar o LockMode.UPGRADE em algum objeto, o 
             * hibernate atualizava o objeto 'lockado', mas ao dar um commit, um flush era 
             * executado e uma versao antiga da entidade era gravada, o que causava 
             * inconsistencia no sequenciaNumeroDocumento.
             */
             serviceDataOwner.getCurrentSession().setFlushMode(FlushMode.ALWAYS);
        }
        this.serviceData = serviceDataOwner;
    }
    
    /**
     * Com as repetidas chamadas de manter, o Hibernate vai criando um cache dos objetos
     * já referenciados. A busca neste cache vai se tornando cada vez mais lenta. Assim, 
     * é necessário que de tempo em tempo, a sessão seja resetada.
     * @throws HibernateException
     */
    public void resetSession() throws HibernateException{
    	/* Obtem a atual fabrica de sessao */
    	SessionFactory factory = this.serviceData.getCurrentSession().getSessionFactory();
    	
    	/* Fecha a sessao atual */
    	this.serviceData.getCurrentSession().close();
    	
    	/* Cria uma nova */
    	this.serviceData.setCurrentSession(factory.openSession());
    }
    
    /**
     * Este método analisa uma entidade e um objeto do mesmo tipo do objeto contido dentro da entidade.
     * Para cada propriedade da entidade que seja do tipo de dados primitivo (long, String, Calendar, etc)
     * um valor é obtido no objeto #source. Assim todas as propriedades primitivas da entidade receberão
     * os valores que estão no objeto.  
     * @param dest Entidade que receberá os valores
     * @param source Objeto, do mesmo tipo do objeto mantido pela entidade, o qual fornecerá os valores
     * @throws BusinessException 
     */
    protected void manterPrimitiveProperties(IEntity<?> dest, Object source) throws BusinessException
    {
    	try
    	{
    		for(IProperty prop: dest.getProperties())
  			if (prop.getInfo().isPrimitive() && !prop.getInfo().isReadOnly())
   				prop.getValue().setAsObject(PropertyUtils.getProperty(source, prop.getInfo().getName()));
    	}catch(Exception e)
    	{
    		throw new BusinessException(MessageList.createSingleInternalError(e));
    	}
    	
    }
    
	/** 
	 * Este método tenta localizar a classe manter responsável por manter
	 * um determinado objeto.<br>
	 * É útil principalmente para permitir que quando o manter de um basic.Contrato
	 * for invocado, este método possa localizar o módulo, classe e método responsáveis por manter
	 * uma provável especialização desta classe.<br>
	 * Isto evita que o módulo mais básico possua referência para os módulos que o estende.
	 * 
	 */
	protected IEntity<?> manterClassFinding(Object object) throws BusinessException{

   	   /* Busca executar genericamente uma linha semelhante a esta:
   	    * new br.com.orionsoft.basic.Manter(this.serviceManager, this.serviceData).manterContrato(contrato);
   	    */
    	String moduleName = serviceManager.getApplication().extractModuleName(object.getClass());
    	try {
    		/* Por convenção, todos os módulos devem ter uma classe Manter no seu primeiro pacote, exemplo: br.com.basic. */
    		Class<?> manterClass = Class.forName(moduleName + ".Manter");
    		
    		/* Por convenção, todas as classes manter possuem métodos manter...() para manter seus objetos, exemplo: br.com.basic.Manter.manterContrato(contrato) */
    		Method manterMethod = manterClass.getMethod("manter" + object.getClass().getSimpleName(), object.getClass());
    		
    		/* As classes manter são construídas utilizando como parâmetros iniciais um ServiceManager e um ServiceData para controle de sessão */
    		Object manterObject = manterClass.getConstructor(IServiceManager.class, ServiceData.class).newInstance(this.serviceManager, this.serviceData);
    		
    		/* Executa e retorna o manter do objeto selecionado */
    		return (IEntity<?>) manterMethod.invoke(manterObject, object);
    	} catch (Exception e) {
    		throw new BusinessException(MessageList.createSingleInternalError(e)); 
    	}

    }
    
}

    