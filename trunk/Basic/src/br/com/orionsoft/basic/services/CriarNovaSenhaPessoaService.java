package br.com.orionsoft.basic.services;

import org.apache.commons.codec.digest.DigestUtils;

import br.com.orionsoft.basic.entities.pessoa.Pessoa;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

/**
 * Servi�o de altera a senha de uma pessoa tendo como par�metros uma Pessoa e a nova senha.<br>
 * 
 * <p><b>Argumentos:</b><br>
 * IN_PESSOA: Objeto Pessoa que ter� sua senha alterada.<br>
 * IN_SENHA_OPT: A senha atual da pessoa, caso a op��o IN_OVERWRITE_BOL_OPT n�o seja informada como TRUE.<br>
 * IN_CODIGO_SEGURANCA_OPT: Utiliza o c�digo de seguran�a forncecido para a pessoa. Caso o servi�o necessite autenticar a pessoa.<br>
 * IN_NOVA_SENHA: A nova senha fornecida pela pessoa.
 * IN_OVERWRITE_BOL_OPT: Se definido como TRUE o servi�o n�o ir� autenticar a pessoa antes da troca. Simplesmente definir� a nova senha.
 * <p><b>Procedimento:</b><br>
 * Op��o IN_OVERWRITE_BOL?
 * <li>TRUE: Pesquisa a pessoa no banco com o documento fornecido.
 * <li>FALSE: Pega a pessoa pela autentica��o do login no servi�o de autentica��o. Atualiza a senha se estiver tudo OK.<br>
 * Ou exibe uma mensagem de erro de valida��o.<br>
 * <b>N�o retorna nada. S� a mensagem do resultado na lista de mensagens</b>
 * 
 * @author Lucio 20071128
 * 
 * @spring.bean id="CriarNovaSenhaPessoaService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 */

public class CriarNovaSenhaPessoaService extends ServiceBasic {

	public static final String SERVICE_NAME = "CriarNovaSenhaPessoaService";

	public static final String IN_PESSOA = "pessoa";
	public static final String IN_NOVA_SENHA = "novaSenha";

	public String getServiceName() {
		return SERVICE_NAME;
	}

	public void execute(ServiceData serviceData) throws ServiceException {
		try {
			/* Obtem os par�metros */
			Pessoa inPessoa = (Pessoa) serviceData.getArgumentList().getProperty(IN_PESSOA);
			String inNovaSenha = (String) serviceData.getArgumentList().getProperty(IN_NOVA_SENHA);

			/* Muda a senha do usu�rio */
			/*Altera para MD5*/
			inNovaSenha = DigestUtils.md5Hex(inNovaSenha);

			inPessoa.setSenha(inNovaSenha);
			
			UtilsCrud.objectUpdate(this.getServiceManager(), inPessoa, serviceData);
			
			/* Inclui a mensagem de OK */
			this.addInfoMessage(serviceData, "CHANGE_OK", inPessoa);
		} catch (BusinessException e) {
			/* O Servi�o n�o precisa adicionar mensagem local. O Manager j�
			 indica qual srv falhou e os par�metros.*/
			throw new ServiceException(e.getErrorList());
		}
	}

}
