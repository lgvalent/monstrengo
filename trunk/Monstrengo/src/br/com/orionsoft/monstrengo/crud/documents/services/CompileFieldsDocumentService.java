package br.com.orionsoft.monstrengo.crud.documents.services;

import java.util.Map;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.support.DocumentParserFields;

/**
 * Este servi�o compila as express�es @{} presentes em documento.
 * Substituindo os campos encontrados pelos valores passados no mapa de valores.
 * 
 * <p><b>Argumento:</b>
 * <br> IN_DOCUMENT_SOURCE: Modelo de documento de entidade.
 * <br> IN_DOCUMENT_FIELDS_MAP: Inst�ncia da entidade que ser� utilizada para a compila��o do documento.
 * 
 * <p><b>Procedimento:</b>
 * <br>Compila o documento.
 * <br><b>1 - Retorna o documento compilado.</b>
 * 
 * @spring.bean id="CompileFieldsDocumentService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 */
public class CompileFieldsDocumentService extends ServiceBasic 
{
    
    public static String SERVICE_NAME = "CompileFieldsDocumentService";
    
    /** Modelo de etiqueta para a entidade, a qual fornecer� o que da entidade vai em cada linha da etiqueta */
    public static String IN_DOCUMENT_SOURCE = "documentSource";
    /** Inst�ncia da entidade da qual ser� estra�da as informa��es */
    public static String IN_DOCUMENT_FIELDS_MAP = "documentFieldsMap";

    @SuppressWarnings("unchecked")
    public void execute(ServiceData serviceData) throws ServiceException 
    {
        try
        {
            log.debug("Iniciando a execu��o do servi�o CompileFieldsDocumentService");
            // Pega os argumentos
            String inDocumentSource = (String) serviceData.getArgumentList().getProperty(IN_DOCUMENT_SOURCE);
            Map<String, String> inDocumentFieldsMap = (Map<String, String>) serviceData.getArgumentList().getProperty(IN_DOCUMENT_FIELDS_MAP);

            
            /* 1 PASSO: Substitui os valores dos campos pelos valores encontrados no mapa */
        	String result = DocumentParserFields.replaceFields(inDocumentSource, inDocumentFieldsMap);

        	// Adiciona o registro criado no resultado no serviceData
            serviceData.getOutputData().add(result);
            
        } 
        catch (BusinessException e)
        {
            // O Servi�o n�o precisa adicionar mensagem local. O Manager j� indica qual srv falhou e os par�metros.
            throw new ServiceException(e.getErrorList());
        }
    }
    
    public String getServiceName() {return SERVICE_NAME;}
}