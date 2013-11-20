package br.com.orionsoft.basic.services;

import br.com.orionsoft.basic.entities.Contrato;
import br.com.orionsoft.basic.entities.pessoa.Pessoa;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;

/**
 * Serviço de alteração da senha de um operador.
 * <p><b>Argumentos:</b><br>
 * IN_NUMERO_DOCUMENTO: O CPF ou CNPJ da pessoa que trocará a senha.<br>
 * IN_SENHA: A senha atual da pessoa<br>
 * IN_CODIGO_SEGURANCA: Utiliza o código de segurança forncecido para a pessoa. Utilizado pelo serviço para autenticar a pessoa.<br>
 * IN_NOVA_SENHA: A nova senha fornecida pela pessoa.
 * <p><b>Procedimento:</b><br>
 * <li>Pega a pessoa pela autenticação do login no serviço de autenticação. Atualiza a senha se estiver tudo OK.</li><br>
 * <li>Altera a nova senha da pessoa</li><br>
 * Ou exibe uma mensagem de erro de validação.<br>
 * <b>Não retorna nada. Só a mensagem do resultado na lista de mensagens</b>
 * 
 * @author bellincanta
 * 
 * @spring.bean id="AlterarSenhaPessoaService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 */

public class AlterarSenhaPessoaService extends ServiceBasic {

	public static final String SERVICE_NAME = "AlterarSenhaPessoaService";

	public static final String IN_NUMERO_DOCUMENTO = "numeroDocumento";
	public static final String IN_SENHA = "senha";
	public static final String IN_CODIGO_SEGURANCA_OPT = "codigoSeguranca";
	public static final String IN_NOVA_SENHA = "novaSenha";

	public String getServiceName() {
		return SERVICE_NAME;
	}

	public void execute(ServiceData serviceData) throws ServiceException {
		try {

			/* Obtem os parâmetros */
			String inNumeroDocumento = (String) serviceData.getArgumentList().getProperty(IN_NUMERO_DOCUMENTO);
			String inSenha = (String) serviceData.getArgumentList().getProperty(IN_SENHA);
			String inNovaSenha = (String) serviceData.getArgumentList().getProperty(IN_NOVA_SENHA);
			String inCodigoSeguranca = serviceData.getArgumentList().containsProperty(IN_CODIGO_SEGURANCA_OPT)?(String) serviceData.getArgumentList().getProperty(IN_CODIGO_SEGURANCA_OPT):null;

			/* Chama o srv de autenticarPessoaService*/
			ServiceData auth = new ServiceData(AutenticarPessoaService.SERVICE_NAME, serviceData);
			auth.getArgumentList().setProperty(AutenticarPessoaService.IN_NUMERO_DOCUMENTO,	inNumeroDocumento);
			auth.getArgumentList().setProperty(AutenticarPessoaService.IN_SENHA, inSenha);
			if(inCodigoSeguranca!=null)
				auth.getArgumentList().setProperty(AutenticarPessoaService.IN_CODIGO_SEGURANCA_OPT, inCodigoSeguranca);
			this.getServiceManager().execute(auth);

			/* Verifica se algo deu errado */
			if(!auth.getMessageList().isTransactionSuccess())
				throw new ServiceException(auth.getMessageList());

			IEntityList<Contrato> contrato  = auth.getFirstOutput();
			if(contrato.isEmpty())
				throw new ServiceException(MessageList.create(AlterarSenhaPessoaService.class,
						"ERROR_AUTHENTICATING", inNumeroDocumento));

			/* Deu tudo certo na autenticação. Continua a operação para criar uma nova senha */
			Pessoa pessoa = contrato.getFirst().getProperty(Contrato.PESSOA).getValue().<Pessoa>getAsEntity().getObject();
			
			ServiceData sd = new ServiceData(CriarNovaSenhaPessoaService.SERVICE_NAME, serviceData);
			sd.getArgumentList().setProperty(CriarNovaSenhaPessoaService.IN_PESSOA,	pessoa);
			sd.getArgumentList().setProperty(CriarNovaSenhaPessoaService.IN_NOVA_SENHA, inNovaSenha);
			this.getServiceManager().execute(sd);

			/* Verifica se algo deu errado */
			if(!sd.getMessageList().isTransactionSuccess())
				throw new ServiceException(sd.getMessageList());

			/* Inclui a mensagem de OK */
			serviceData.getMessageList().addAll(sd.getMessageList());
		} catch (BusinessException e) {
			/* O Serviço não precisa adicionar mensagem local. O Manager já
			 indica qual srv falhou e os parâmetros.*/
			throw new ServiceException(e.getErrorList());
		}
	}

}
