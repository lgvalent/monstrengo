package br.com.orionsoft.financeiro.documento.cobranca.titulo.services;

import java.lang.reflect.Modifier;
import java.lang.reflect.Field;

import br.com.orionsoft.financeiro.documento.cobranca.titulo.Ocorrencia;
import br.com.orionsoft.financeiro.utils.UtilsOcorrencia;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

/**
 * Serviço que faz a persistência de cada ocorrência.<br>
 * 
 * @author marcia
 * @version 2006/04/20
 * 
 * @spring.bean id="PersistirOcorrenciasService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 * @spring.property name="transactional" value="true"
 */
public class PersistirOcorrenciasService extends ServiceBasic{
    public static String SERVICE_NAME = "PersistirOcorrenciasService";

    public String getServiceName() {
        return SERVICE_NAME;
    }
    
    public void execute(ServiceData serviceData) throws ServiceException {
    	log.debug("::Iniciando execução do servico PersistirOcorrenciasService");
    	try {
    		
    		log.debug("Obtendo os tipos de ocorrências definidos na classe Ocorrencia.java");
    		Ocorrencia oOcorrencia = new Ocorrencia();
    		IEntity ocorrencia = null;
    		
    		Field[] fields = oOcorrencia.getClass().getDeclaredFields();
    		for (int i=0; i<fields.length; i++)
    		{
    			if(Modifier.isStatic(fields[i].getModifiers()))
    				if(fields[i].get(oOcorrencia) instanceof Ocorrencia){
    					if (UtilsOcorrencia.obterOcorrencia(this.getServiceManager(), ((Ocorrencia)fields[i].get(oOcorrencia)).getCodigo(), serviceData) == null){
    						ocorrencia = UtilsCrud.create(this.getServiceManager(), Ocorrencia.class, serviceData);
    					}else{
    						ocorrencia = UtilsOcorrencia.obterOcorrencia(this.getServiceManager(), ((Ocorrencia)fields[i].get(oOcorrencia)).getCodigo(), serviceData);
    					}
    					ocorrencia.setPropertyValue(Ocorrencia.CODIGO, ((Ocorrencia)fields[i].get(oOcorrencia)).getCodigo());
						ocorrencia.setPropertyValue(Ocorrencia.DESCRICAO, ((Ocorrencia)fields[i].get(oOcorrencia)).getDescricao());
						UtilsCrud.update(this.getServiceManager(), ocorrencia, serviceData);
						ocorrencia = null;
    				}
    		}
    		log.debug("::Encerrando execução do servico PersistirOcorrenciasService");
    		
    	} catch (BusinessException e) {
    		log.fatal(e.getErrorList());
    		/* O Serviço não precisa adicionar mensagem local. O Manager já indica qual srv falhou e os parâmetros. */
    		throw new ServiceException(e.getErrorList());
    	} catch (Exception e) {
    		log.fatal(e.getMessage());
    		/* Indica que o serviço falhou por causa de uma exceção do hibernate. */
    		throw new ServiceException(MessageList.createSingleInternalError(e));
    	}
    }
    
}