package br.com.orionsoft.monstrengo.core.test;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;

import javax.faces.model.SelectItem;

import org.springframework.context.ApplicationContext;

import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.util.PrintUtils;
import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IGroupProperties;
import br.com.orionsoft.monstrengo.crud.entity.IProperty;
import br.com.orionsoft.monstrengo.crud.entity.PropertyValueException;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDaoManager;

/**
 * 
 * @author marcia 2005/11/21
 * @version 20060110 
 *
 */public class UtilsTest
{
    /**
     * Exibe no console de saída as propriedades e os valores de um entidade.
     * @throws PropertyValueException 
     * @throws EntityException 
     */
    public static final void showEntityProperties(IEntity entity) throws PropertyValueException, EntityException
    {
        System.out.println("===================================================");
        System.out.println("Entidade: " + entity.getInfo().getLabel());
        if(entity != null)
            for(IGroupProperties group: entity.getGroupsProperties()){
            	System.out.println(group.getInfo().getIndex() + ":" + group.getInfo().getName());
                for(IProperty prop: group.getProperties()) 
                	System.out.println("  " + prop.getInfo().getLabel() + "=" + prop.getValue().getAsString() + (prop.getInfo().isVisible()?"":"(oculto)"));
            }
    }
    
    /**
     * Este método retorna a lista de TODAS as entidades do sistemas.
     * @return
     */
    public static final List<Class> getAllBusinessEntity(IDaoManager daoManager) throws Exception
    {
        
        List<Class> result = new ArrayList<Class>();
        for(IDAO dao: daoManager.getDaos().values())
        	result.add(dao.getEntityClass());
        
//        result.addAll(getEntitiesList(getEntitiesProperties("br.com.orionsoft.monstrengo.Framework")));
//      result.addAll(getEntitiesList(getEntitiesProperties("br.com.orionsoft.basico.Basico")));
//        result.addAll(getEntitiesList(getEntitiesProperties("br.com.orionsoft.financeiro.Entities")));
        
        return result;
    }
    
    public static final List<Class> getAllBusinessEntity(ApplicationContext ctx) throws Exception
    {
        List<Class> result = new ArrayList<Class>();

        // Pega a lista de nomes dos Beans do Spring
        String[] beans = ctx.getBeanDefinitionNames();
        for (String str:beans)
        {
            // Identifica qual atende ao padrão "*DAO"
            if (str.endsWith("DAO"))
            {
                // Obtem o Bean Dao
                IDAO dao = (IDAO) ctx.getBean(str);
                
                // Obtem a classe da entidade mantida pelo Dao 
                result.add(dao.getEntityClass());
            }
            
        }

        return result;
    }
    
    private static final ResourceBundle getEntitiesProperties(String path)
    {
        return ResourceBundle.getBundle(path);
    }
    
    @Deprecated
    private static final List<Class> getEntitiesList(ResourceBundle bundle) throws Exception
    {
        try
        {
            List<Class> result = new ArrayList<Class>();
            
            Enumeration<String> keys = bundle.getKeys(); 
            while (keys.hasMoreElements())
                result.add(Class.forName(keys.nextElement()));
            
            return result;
        }
        catch(ClassNotFoundException e)
        {
            throw new Exception("ClassNotFound:" + e.getMessage() + ". Verifique o pacote e o nome da classe e certifique-se que os nomes estejam corretamente definidos no arquivo Entities.properties do pacote.");
        }
    }
    
    public static final void showMessageList(List<BusinessMessage> list)
    {
        System.out.println("--== MENSAGENS ==--");
        for(BusinessMessage e: list)
        {
            System.out.println(e.getMessageClass().getSimpleName() + ":" + e.getErrorKey() + "=" + e.getMessage());
        }
    }
    
    /**
     * Lista os índices e nomes das impressoras para auxiliar na montagem de
     * testes qe utilizam impressão e precisam identificar em qual impressora 
     * imprimir os testes
     */
    public static final void showPrinters(){
		System.out.println("(index=label)");
    	for(SelectItem item: PrintUtils.retrievePrinters())
    		System.out.println(item.getValue() + "=" + item.getLabel());
    }
    
    
}
