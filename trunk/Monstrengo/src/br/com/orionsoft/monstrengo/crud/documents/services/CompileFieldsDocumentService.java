package br.com.orionsoft.monstrengo.crud.documents.services;

import java.util.Map;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.support.DocumentParserFields;

/**
 * Este serviço compila as expressões @{} presentes em documento.
 * Substituindo os campos encontrados pelos valores passados no mapa de valores.
 * 
 * <p><b>Argumento:</b>
 * <br> IN_DOCUMENT_SOURCE: Modelo de documento de entidade.
 * <br> IN_DOCUMENT_FIELDS_MAP: Instância da entidade que será utilizada para a compilação do documento.
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
    
    /** Modelo de etiqueta para a entidade, a qual fornecerá o que da entidade vai em cada linha da etiqueta */
    public static String IN_DOCUMENT_SOURCE = "documentSource";
    /** Instância da entidade da qual será estraída as informações */
    public static String IN_DOCUMENT_FIELDS_MAP = "documentFieldsMap";

    @SuppressWarnings("unchecked")
    public void execute(ServiceData serviceData) throws ServiceException 
    {
        try
        {
            log.debug("Iniciando a execução do serviço CompileFieldsDocumentService");
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
            // O Serviço não precisa adicionar mensagem local. O Manager já indica qual srv falhou e os parâmetros.
            throw new ServiceException(e.getErrorList());
        }
    }
    
    public String getServiceName() {return SERVICE_NAME;}
}