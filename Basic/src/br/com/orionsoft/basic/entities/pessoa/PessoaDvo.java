package br.com.orionsoft.basic.entities.pessoa;

import br.com.orionsoft.basic.entities.endereco.Endereco;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.util.StringUtils;
import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.PropertyValueException;
import br.com.orionsoft.monstrengo.crud.entity.dvo.DvoBasic;
import br.com.orionsoft.monstrengo.crud.entity.dvo.DvoException;
import br.com.orionsoft.monstrengo.mail.services.SendMailService;
import br.com.orionsoft.monstrengo.security.entities.UserSession;
/**
 * Classe que valida entidades do tipo T. <br>
 * 
 * @author Lucio
 * 
 */
public abstract class PessoaDvo<T extends Pessoa> extends DvoBasic<T> {

	
	/* Método que verifica o CPF de uma entidade do tipo Fisica antes de alterá-la. 
	 * Ele verifica se existe um CPF para validá-lo, se o documento é obrigatório é evitada a gravação sem o CPF.
	 */
	public void afterUpdate(IEntity<T> entity, UserSession userSession, ServiceData serviceData) throws DvoException, BusinessException {
		DvoException dvoExceptions = new DvoException(new MessageList());

		try{
			validarEMail(entity);
		}
		catch(DvoException e){
			dvoExceptions.getErrorList().addAll(e.getErrorList());
		}
		
		try{
			IEntity<Endereco> endereco = entity.getProperty(Pessoa.ENDERECO_CORRESPONDENCIA).getValue().getAsEntity();
			this.getDvoManager().getDvoByEntity(endereco).afterUpdate(endereco, userSession, serviceData);
		}
		catch(DvoException e){
			dvoExceptions.getErrorList().addAll(e.getErrorList());
		}

		if(!dvoExceptions.getErrorList().isEmpty()){
			throw dvoExceptions;
		}
	}
	
	/***
	 * Método que verifica se os são válidos.
	 */
	private void validarEMail(IEntity<T> entity) throws DvoException{
		try{
			String emails = entity.getProperty(Pessoa.EMAIL).getValue().getAsString();
			if(!StringUtils.isBlank(emails)){
				for(String email: emails.split(";"))
					if(!SendMailService.validateEMail(email))
						throw new DvoException(MessageList.create(PessoaDvo.class, "EMAIL_INVALIDO", email));
			}
		}
		catch(PropertyValueException e) {
			throw new DvoException(e.getErrorList());
		}
		catch(EntityException e) {
			throw new DvoException(e.getErrorList());
		}
		
	}
	

}