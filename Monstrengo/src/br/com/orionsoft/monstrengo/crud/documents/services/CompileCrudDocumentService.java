package br.com.orionsoft.monstrengo.crud.documents.services;

import java.util.Map;

import br.com.orionsoft.monstrengo.crud.documents.services.CompileCrudDocumentService;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.core.util.ClassUtils;
import br.com.orionsoft.monstrengo.crud.documents.entities.ModelDocumentEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.support.DocumentParserCrudExpression;
import br.com.orionsoft.monstrengo.crud.support.DocumentParserFields;

/**
 * Este serviço recebe um modelo de documento de entidade e, opcionalmente, uma entidade
 * que será utilizada na compilação das expressões CRUD presente no documento.
 * 
 * <p><b>Argumento:</b>
 * <br> IN_MODEL_DOCUMENT_ENTITY: Modelo de documento de entidade.
 * <br> IN_ENTITY_OPT: Instância da entidade que será utilizada para a compilação do documento.
 * 
 * <p><b>Procedimento:</b>
 * <br>Verifica se o modelo é compatível com a entidade específica.
 * <br>Obtem o código fonte do documento.
 * <br>Compila o documento.
 * <br><b>1 - Retorna o documento compilado.</b>
 * <br><b>2 - Retorna o mapa de campos encontrados no documento.</b>
 * 
 * 
 * @spring.bean id="CompileCrudDocumentService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 */
public class CompileCrudDocumentService extends ServiceBasic 
{
    
    public static String SERVICE_NAME = "CompileCrudDocumentService";
    
    /** Modelo de etiqueta para a entidade, a qual fornecerá o que da entidade vai em cada linha da etiqueta */
    public static String IN_MODEL_DOCUMENT_ENTITY = "modelDocumentEntity";
    /** Instância da entidade da qual será estraída as informações */
    public static String IN_ENTITY_OPT = "entity";

    /** Índice do resultado do documento compilado na lista de resultados do serviço */
    public static int OUT_COMPILED_DOCUMENT = 0;
    /** Índice do resultado do mapa com os campos que foram encontrados no documento. Estes campos 
     * deverão ter seus valores preenchidos neste mapa  */
    public static int OUT_FIELDS_MAP = 1;

    public void execute(ServiceData serviceData) throws ServiceException 
    {
        try
        {
            log.debug("Iniciando a execução do serviço CompileCrudDocumentService");
            // Pega os argumentos
            @SuppressWarnings("unchecked")
			IEntity<ModelDocumentEntity> inDocumentEntity = (IEntity<ModelDocumentEntity>) serviceData.getArgumentList().getProperty(IN_MODEL_DOCUMENT_ENTITY);

            IEntity<?> inEntity = null;
            if(serviceData.getArgumentList().containsProperty(IN_ENTITY_OPT))
            	inEntity = (IEntity<?>) serviceData.getArgumentList().getProperty(IN_ENTITY_OPT);
            
            // Preenche o novo registro
            /* Pega a expressão do modelo de etiqueta */
            String source=inDocumentEntity.getProperty(ModelDocumentEntity.SOURCE).getValue().getAsString();

            /* Se tiver uma entidade, verifica se a entidade é compativel com o modelo */
            if(inEntity !=null){
            	ModelDocumentEntity oModelDocumentEntity = inDocumentEntity.getObject();
           	
            	try {
					if (!ClassUtils.isAssignable(inEntity.getInfo().getType(), Class.forName(oModelDocumentEntity.getApplicationEntity().getClassName()))){
						throw new ServiceException(MessageList.create(CompileCrudDocumentService.class, "INCOMPATIBLE_MODEL", inDocumentEntity, inEntity.getInfo().getType().getSimpleName()));
					}
				} catch (ClassNotFoundException e) {
					throw new ServiceException(MessageList.createSingleInternalError(e));
				}
            	/* O modelo pode ser de uma classe da hierarquia da entidade e não diretamente da classe da entidade.
            	 * Assim, uma expressão {Pessoa[?].nome} pode ser compilada por uma IEntity(Juridica).
            	 * Para isto, e evitar problema nos compiladores de expressão, vamos substitui a palavra Pessoa para a classe
            	 * específica da Entidade no modelo.
            	 */
            	source = source.replace(inDocumentEntity.getObject().getApplicationEntity().getName() + "[?]", inEntity.getObject().getClass().getSimpleName() + "[?]");            
            }
            
            /* 1 PASSO: Compila as expressões CRUD que estão no documento para elas terem valores legiveis */
            String outCompiledDocument;
            if(inEntity == null)
            	outCompiledDocument = DocumentParserCrudExpression.parseString(source, this.getServiceManager().getEntityManager());
            else
            	outCompiledDocument = DocumentParserCrudExpression.parseString(source, inEntity, this.getServiceManager().getEntityManager());
            
            /* 2 PASSO: Analisa no documento a presença de campos dentro de expressões @{fieldName}
             * e cria um mapa com os campos encontrados para serem, preenchidos */
        	Map<String, String> outDocumentFieds = DocumentParserFields.findFields(outCompiledDocument);

        	// Adiciona o registro criado no resultado no serviceData
            serviceData.getOutputData().add(OUT_COMPILED_DOCUMENT, outCompiledDocument);
            serviceData.getOutputData().add(OUT_FIELDS_MAP, outDocumentFieds);
            
        } 
        catch (BusinessException e)
        {
            // O Serviço não precisa adicionar mensagem local. O Manager já indica qual srv falhou e os parâmetros.
            throw new ServiceException(e.getErrorList());
        }
    }
    
    public String getServiceName() {return SERVICE_NAME;}
}