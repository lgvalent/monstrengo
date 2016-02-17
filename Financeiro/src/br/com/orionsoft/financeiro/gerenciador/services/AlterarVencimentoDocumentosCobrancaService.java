package br.com.orionsoft.financeiro.gerenciador.services;

import java.util.Calendar;

import org.apache.commons.lang.StringUtils;

import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobranca;
import br.com.orionsoft.monstrengo.auditorship.services.UtilsAuditorship;
import br.com.orionsoft.monstrengo.auditorship.support.EntityAuditValue;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.security.entities.UserSession;

/**
 * Este serviço altera a data de vencimento dos documentod de cobranças.  
 * <p>
 * <b>Parâmetros:</b><br>
 * IN_DATA (Calendar) nova data de vencimento lançamento.<br>
 * IN_DOCUMENTOS (IEntityList) Os documentos que terão suas datas de vencimento alteradas.<br>
 * IN_USER_SESSION_OPT (UserSession) Sessão de um usuário para registrar a auditoria.<br>
 * <p>
 * <b>Retorno:</b><br>
 * Nada
 * 
 * @author Lucio
 * @version 20110404
 * 
 * @spring.bean id="AlterarVencimentoDocumentosCobrancaService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 */
public class AlterarVencimentoDocumentosCobrancaService extends ServiceBasic {
    public static final String SERVICE_NAME = "AlterarVencimentoDocumentosCobrancaService";
    
    public static final String IN_DATA = "data";
    public static final String IN_ADENDO_INSTRUCOES_3 = "adendoInstrucoes3";
    public static final String IN_DOCUMENTOS = "documentos";
    public static final String IN_USER_SESSION_OPT = "userSession";

    public String getServiceName() {
        return SERVICE_NAME;
    }

    public void execute(ServiceData serviceData) throws ServiceException {
    	log.debug("::Iniciando a execução do serviço AlterarVencimentoDocumentosCobrancaService");

        try {
            log.debug("Preparando os argumentos");
            
            /* 
             * Lê os parâmetros obrigatórios. 
             */
            Calendar inData = (Calendar) serviceData.getArgumentList().getProperty(IN_DATA);
            String inAdendoInstrucoes3 = (String) serviceData.getArgumentList().getProperty(IN_ADENDO_INSTRUCOES_3);
            IEntityList<DocumentoCobranca> inDocumentos = (IEntityList<DocumentoCobranca>) serviceData.getArgumentList().getProperty(IN_DOCUMENTOS);
            UserSession inUserSession = (UserSession) (serviceData.getArgumentList().containsProperty(IN_USER_SESSION_OPT)?serviceData.getArgumentList().getProperty(IN_USER_SESSION_OPT):null);
            boolean enableAudit = inUserSession!=null;

            for(IEntity<DocumentoCobranca> doc: inDocumentos){
            	/* Verifica se está selecionado */
            	if(doc.isSelected()){
            		/* Prepara a auditoria da alteração se tiver habilitada */
            		EntityAuditValue entityAuditValue = null;
            		if(enableAudit){
            			entityAuditValue = new EntityAuditValue(doc);
            		}

            		/* Altera a data de vencimento */
            		doc.setPropertyValue(DocumentoCobranca.DATA_VENCIMENTO, inData);
            		/* Adiciona o adendo da instrução 3 */
            		if(StringUtils.isNotBlank(inAdendoInstrucoes3))
            			if(StringUtils.isBlank(doc.getObject().getInstrucoes3())){
            				doc.getObject().setInstrucoes3(inAdendoInstrucoes3);
            			}else if(!doc.getObject().getInstrucoes3().contains(inAdendoInstrucoes3))
            				doc.getObject().setInstrucoes3(doc.getObject().getInstrucoes3() + " - " + inAdendoInstrucoes3);
            		UtilsCrud.update(this.getServiceManager(), doc, serviceData);

            		/* Prepara a auditoria da alteração se tiver habilitada */
            		if(enableAudit){
            			UtilsAuditorship.auditUpdate(this.getServiceManager(), inUserSession, entityAuditValue, serviceData);
            		}

            		this.addInfoMessage(serviceData, "SUCESSO_DOCUMENTO", doc.toString());
            	}
            }
            
            /* 
             * Adiconando a mensagem de sucesso 
             */
            this.addInfoMessage(serviceData, "SUCESSO");
		
            log.debug("::Fim da execução do serviço");
        } catch (BusinessException e) {
            log.fatal(e.getErrorList());
            throw new ServiceException(e.getErrorList());
        } catch (Exception e) {
            log.fatal(e.getMessage());
            throw new ServiceException(MessageList.createSingleInternalError(e));
        }
    }

}
