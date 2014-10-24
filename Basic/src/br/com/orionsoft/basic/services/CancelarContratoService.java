package br.com.orionsoft.basic.services;

import java.util.Calendar;

import br.com.orionsoft.basic.entities.Contrato;
import br.com.orionsoft.monstrengo.auditorship.services.UtilsAuditorship;
import br.com.orionsoft.monstrengo.auditorship.support.EntityAuditValue;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.security.entities.UserSession;

/**
 * Este servi�o cancela um contrato, definindo uma data de rescis�o e acresccenta nas observa��es.  
 * <p>
 * <b>Par�metros:</b><br>
 * IN_CONTRATO (IEntity<Contrato>) contrato a ser cancelado.<br>
 * IN_USER_SESSION (UserSession) credenciais do usu�rio que efetuar� a a��o.<br>
 * IN_DATA_CANCELAMENTO (Calendar) data de cancelamento.<br>
 * IN_DESCRICAO (String) descri��o do motivo do cancelamento.<br>
 * <p>
 * <b>Retorno:</b><br>
 * 
 * @author Lucio Valentin
 * @version 20141024
 * 
 */
public class CancelarContratoService extends ServiceBasic {
    public static final String SERVICE_NAME = "CancelarContratoService";
    
    public static final String IN_CONTRATO = "contrato";
    public static final String IN_USER_SESSION = "userSession";
    public static final String IN_DATA_CANCELAMENTO = "data";
    public static final String IN_DESCRICAO = "descricao";

    public String getServiceName() {
        return SERVICE_NAME;
    }

    public void execute(ServiceData serviceData) throws ServiceException {
    	log.debug("::Iniciando a execu��o do servi�o CancelarContratoService");

        try {
            log.debug("Preparando os argumentos");
            
            /* 
             * L� os par�metros obrigat�rios. 
             */
            IEntity<Contrato> inContrato = (IEntity<Contrato>) serviceData.getArgumentList().getProperty(IN_CONTRATO);
            UserSession inUserSession = (UserSession) serviceData.getArgumentList().getProperty(IN_USER_SESSION);
            Calendar inDataCancelamento = (Calendar) serviceData.getArgumentList().getProperty(IN_DATA_CANCELAMENTO);
            String inDescricao = (String) serviceData.getArgumentList().getProperty(IN_DESCRICAO);

            /*
             * Prepara a auditoria de altera��o do registro.
             */
            EntityAuditValue auditValue = new EntityAuditValue(inContrato);
            Contrato contrato = inContrato.getObject();
            
            contrato.setInativo(true);
            contrato.setDataRescisao(inDataCancelamento);
            contrato.getObservacoes().setObservacoes(contrato.getObservacoes().getObservacoes() + "\n CANCELADO: " + inDescricao);
            
            // Registra a auditoria da atualiza��o
            UtilsAuditorship.auditUpdate(this.getServiceManager(), 
                                         inUserSession,
                                         auditValue, serviceData);

            /* 
             * Adiconando a mensagem de sucesso 
             */
            this.addInfoMessage(serviceData, "SUCESSO", inContrato);
		
            log.debug("::Fim da execu��o do servi�o");
        } catch (BusinessException e) {
            log.fatal(e.getErrorList());
            throw new ServiceException(e.getErrorList());
        } catch (Exception e) {
            log.fatal(e.getMessage());
            throw new ServiceException(MessageList.createSingleInternalError(e));
        }
    }

}
