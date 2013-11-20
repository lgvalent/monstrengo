package br.com.orionsoft.financeiro.documento.cobranca;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.util.StringUtils;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.dvo.DvoBasic;
import br.com.orionsoft.monstrengo.crud.entity.dvo.DvoException;
import br.com.orionsoft.monstrengo.security.entities.UserSession;
/**
 * Classe que valida entidades do tipo DocumentoCobrancaCategoria. <br>
 */
public class DocumentoCobrancaCategoriaDvo extends DvoBasic<DocumentoCobrancaCategoria> {

	private static String CARACTERES_VALIDOS = "0123456789.,:;?@[]_-{}<>=!#$%&()*/\\+ AÀÁÃÂBCÇDEÈÉÊFGHIÍJKLMNOÓÔÕPQRSTUÚVWXZaàáãâbcçdeèéêfghiíjklmnoóôõpqrstuúvwxz";

	/**
	 * Metodo que retorna a classe da entidade.
	 */
	public Class<DocumentoCobrancaCategoria> getEntityType(){
		return DocumentoCobrancaCategoria.class;	
	}
	
	/* Método que verifica o CNPJ de uma entidade do tipo DocumentoCobrancaCategoria antes de gravá-la.
	 * Ele verifica se existe um CNPJ para validá-lo, se o documento é obrigatório é evitada a gravação sem o CNPJ.
	 */
	public void afterCreate(IEntity<DocumentoCobrancaCategoria> entity, UserSession userSession, ServiceData serviceData) throws DvoException, BusinessException {
		 
	}
	
	/* Método que verifica os caracteres o CPF de uma entidade do tipo Fisica antes de alterá-la. 
	 * Ele verifica se existe um CPF para validá-lo, se o documento é obrigatório é evitada a gravação sem o CPF.
	 */
	public void afterUpdate(IEntity<DocumentoCobrancaCategoria> entity, UserSession userSession, ServiceData serviceData) throws DvoException, BusinessException {
        DvoException dvoExceptions = new DvoException(new MessageList());
        
        DocumentoCobrancaCategoria oDocumentoCobrancaCategoria = entity.getObject();
        
        // Lucio 20121010: Valida somente quando o layout é 1: O banco imprime o documento
        if(oDocumentoCobrancaCategoria.getLayoutId() == 1){
        	if(!StringUtils.checkValidChars(oDocumentoCobrancaCategoria.getInstrucoes0(), CARACTERES_VALIDOS)){
        		MessageList ml = MessageList.create(DocumentoCobrancaCategoriaDvo.class, "CARACTERES_INVALIDOS", entity.getProperty(DocumentoCobrancaCategoria.INSTRUCOES0).getInfo().getLabel());
        		dvoExceptions.getErrorList().addAll(ml);
        	}

        	if(!StringUtils.checkValidChars(oDocumentoCobrancaCategoria.getInstrucoes1(), CARACTERES_VALIDOS)){
        		MessageList ml = MessageList.create(DocumentoCobrancaCategoriaDvo.class, "CARACTERES_INVALIDOS", entity.getProperty(DocumentoCobrancaCategoria.INSTRUCOES1).getInfo().getLabel());
        		dvoExceptions.getErrorList().addAll(ml);
        	}

        	if(!StringUtils.checkValidChars(oDocumentoCobrancaCategoria.getInstrucoes2(), CARACTERES_VALIDOS)){
        		MessageList ml = MessageList.create(DocumentoCobrancaCategoriaDvo.class, "CARACTERES_INVALIDOS", entity.getProperty(DocumentoCobrancaCategoria.INSTRUCOES2).getInfo().getLabel());
        		dvoExceptions.getErrorList().addAll(ml);
        	}
        }
		if(!dvoExceptions.getErrorList().isEmpty()){
			throw dvoExceptions;
		}
	}
	
	public void afterDelete(IEntity<DocumentoCobrancaCategoria> arg0, UserSession userSession, ServiceData arg1) throws DvoException, BusinessException {
		// TODO Auto-generated method stub
		
	}
	
	public void beforeCreate(IEntity<DocumentoCobrancaCategoria> arg0, UserSession userSession, ServiceData arg1) throws DvoException, BusinessException {
		
	}

	public void beforeDelete(IEntity<DocumentoCobrancaCategoria> arg0, UserSession userSession, ServiceData arg1) throws DvoException, BusinessException {
		// TODO Auto-generated method stub
		
	}

	public void beforeUpdate(IEntity<DocumentoCobrancaCategoria> arg0, UserSession userSession, ServiceData arg1) throws DvoException, BusinessException {
		// TODO Auto-generated method stub
		
	}
}