package br.com.orionsoft.monstrengo.crud.labels.services;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.labels.entities.AddressLabel;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

/**
 * Serviço que insere uma etiqueta de enderaço
 * 
 * @version 20060804
 * 
 * @spring.bean id="InsertAddressLabelService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 */
public class InsertAddressLabelService extends ServiceBasic {
    public static final String SERVICE_NAME = "InsertAddressLabelService";

    public static final String IN_APPLICATION_USER = "applicationUser";
    
    public static final String IN_LINE1_OPT = "line1";
    public static final String IN_LINE2_OPT = "line2";
    public static final String IN_LINE3_OPT = "line3";
    public static final String IN_LINE4_OPT = "line4";
    public static final String IN_LINE5_OPT = "line5";
    public static final String IN_PRINT_BOOL_OPT = "print";

    public String getServiceName() {
        return SERVICE_NAME;
    }

    public void execute(ServiceData serviceData) throws ServiceException {
        log.debug("::Iniciando a execução do serviço InsertAddressLabelService");
        try {
        	/* Obtem os parâmetros obrigatórios */
        	IEntity inApplicationUser = (IEntity) serviceData.getArgumentList().getProperty(IN_APPLICATION_USER); 
        	
        	/* Obtem os parâmetros OPCIONAIS */
        	String inLine1="";
        	if(serviceData.getArgumentList().containsProperty(IN_LINE1_OPT))
            	inLine1 = (String) serviceData.getArgumentList().getProperty(IN_LINE1_OPT);
        	String inLine2="";
        	if(serviceData.getArgumentList().containsProperty(IN_LINE2_OPT))
            	inLine2 = (String) serviceData.getArgumentList().getProperty(IN_LINE2_OPT);
        	String inLine3="";
        	if(serviceData.getArgumentList().containsProperty(IN_LINE3_OPT))
            	inLine3 = (String) serviceData.getArgumentList().getProperty(IN_LINE3_OPT);
        	String inLine4="";
        	if(serviceData.getArgumentList().containsProperty(IN_LINE4_OPT))
            	inLine4 = (String) serviceData.getArgumentList().getProperty(IN_LINE4_OPT);
        	String inLine5="";
        	if(serviceData.getArgumentList().containsProperty(IN_LINE5_OPT))
            	inLine5 = (String) serviceData.getArgumentList().getProperty(IN_LINE5_OPT);

        	boolean inPrint=true;
        	if(serviceData.getArgumentList().containsProperty(IN_PRINT_BOOL_OPT))
            	inPrint = (Boolean) serviceData.getArgumentList().getProperty(IN_PRINT_BOOL_OPT);
        	
            
            /* Cria a entidade Etiqueta */
            IEntity addressLabel = UtilsCrud.create(this.getServiceManager(), AddressLabel.class, serviceData);
            
            /* Define as propriedades da etiqueta */
            addressLabel.setPropertyValue(AddressLabel.APPLICATION_USER, inApplicationUser);
            addressLabel.setPropertyValue(AddressLabel.LINE1, inLine1);
            addressLabel.setPropertyValue(AddressLabel.LINE2, inLine2);
            addressLabel.setPropertyValue(AddressLabel.LINE3, inLine3);
            addressLabel.setPropertyValue(AddressLabel.LINE4, inLine4);
            addressLabel.setPropertyValue(AddressLabel.LINE5, inLine5);
            addressLabel.setPropertyValue(AddressLabel.PRINT, inPrint);
            
            /* Grava a Etiqueta  */
            UtilsCrud.update(this.getServiceManager(), addressLabel, serviceData);
            
            /* Retorna a etiqueta criada */
            serviceData.getOutputData().add(addressLabel);

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
