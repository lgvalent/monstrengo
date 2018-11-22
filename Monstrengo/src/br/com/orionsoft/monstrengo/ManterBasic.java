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
 * Esta classe possui as rotinas b�sicas das classes Manter que s�o
 * implementadas em cada m�dulo.<br>
 * Essa classe faz a verifica��o da existencia de um determinado registro.
 * Se o registro ja existir no banco, ser� retornada a entidade
 * prenchida com as informa��es desse registro, caso contrario
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
     * Manipulador de Log para ser utilizado pelas implementa��es dos
     * servi�os. 
     */
    protected Logger log = LogManager.getLogger(getClass());
    
    public ManterBasic(IServiceManager serviceManager, ServiceData serviceDataOwner)
    {
        this.serviceManager = serviceManager;
        if(serviceDataOwner== null){
        	/** Copiado do m�todo privado ServiceManager.prepareSession() */
            log.debug("Criando uma nova sess�o");
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
     * j� referenciados. A busca neste cache vai se tornando cada vez mais lenta. Assim, 
     * � necess�rio que de tempo em tempo, a sess�o seja resetada.
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
     * Este m�todo analisa uma entidade e um objeto do mesmo tipo do objeto contido dentro da entidade.
     * Para cada propriedade da entidade que seja do tipo de dados primitivo (long, String, Calendar, etc)
     * um valor � obtido no objeto #source. Assim todas as propriedades primitivas da entidade receber�o
     * os valores que est�o no objeto.  
     * @param dest Entidade que receber� os valores
     * @param source Objeto, do mesmo tipo do objeto mantido pela entidade, o qual fornecer� os valores
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
	 * Este m�todo tenta localizar a classe manter respons�vel por manter
	 * um determinado objeto.<br>
	 * � �til principalmente para permitir que quando o manter de um basic.Contrato
	 * for invocado, este m�todo possa localizar o m�dulo, classe e m�todo respons�veis por manter
	 * uma prov�vel especializa��o desta classe.<br>
	 * Isto evita que o m�dulo mais b�sico possua refer�ncia para os m�dulos que o estende.
	 * 
	 */
	protected IEntity<?> manterClassFinding(Object object) throws BusinessException{

   	   /* Busca executar genericamente uma linha semelhante a esta:
   	    * new br.com.orionsoft.basic.Manter(this.serviceManager, this.serviceData).manterContrato(contrato);
   	    */
    	String moduleName = serviceManager.getApplication().extractModuleName(object.getClass());
    	try {
    		/* Por conven��o, todos os m�dulos devem ter uma classe Manter no seu primeiro pacote, exemplo: br.com.basic. */
    		Class<?> manterClass = Class.forName(moduleName + ".Manter");
    		
    		/* Por conven��o, todas as classes manter possuem m�todos manter...() para manter seus objetos, exemplo: br.com.basic.Manter.manterContrato(contrato) */
    		Method manterMethod = manterClass.getMethod("manter" + object.getClass().getSimpleName(), object.getClass());
    		
    		/* As classes manter s�o constru�das utilizando como par�metros iniciais um ServiceManager e um ServiceData para controle de sess�o */
    		Object manterObject = manterClass.getConstructor(IServiceManager.class, ServiceData.class).newInstance(this.serviceManager, this.serviceData);
    		
    		/* Executa e retorna o manter do objeto selecionado */
    		return (IEntity<?>) manterMethod.invoke(manterObject, object);
    	} catch (Exception e) {
    		throw new BusinessException(MessageList.createSingleInternalError(e)); 
    	}

    }
    
}

    