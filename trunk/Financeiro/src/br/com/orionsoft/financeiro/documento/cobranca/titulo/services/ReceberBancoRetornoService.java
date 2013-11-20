package br.com.orionsoft.financeiro.documento.cobranca.titulo.services;


import java.io.InputStream;
import java.util.List;

import br.com.orionsoft.financeiro.documento.cobranca.suporte.DocumentoRetornoResultado;
import br.com.orionsoft.financeiro.documento.cobranca.titulo.IGerenciadorBanco;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;

/**
 * Este servi�o � utilizado para gerar o retorno de determinado gerenciador de bancos
 * 
 * <p>Argumentos:</p>
 * IN_CEDENTE: (Long) entidade que indica para qual cedente ser� lido o arquivo de retorno.
 * IN_DADOS_RETORNO: (File) Dados que ser�o lidos pelo retorno do gerenciador de documentos.
 * 
 * @version 20061006
 * 
 * @spring.bean id="ReceberBancoRetornoService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 * @spring.property name="provedorBanco" ref="ProvedorBanco"
 * @spring.property name="transactional" value="true"
 */
public class ReceberBancoRetornoService extends BancoServiceBasic {
	
	public static final String SERVICE_NAME = "ReceberBancoRetornoService";

	public static final String IN_CEDENTE = "inCedente";
	public static final String IN_INPUT_STREAM = "inInputStream";
	
	public String getServiceName() {
		return SERVICE_NAME;
	}
	
	public void execute(ServiceData serviceData) throws ServiceException {
		log.debug("::Iniciando execu��o do servico GerarBancoRemessaService");
		
		try{
			log.debug("Preparando os argumentos");
			IEntity inCedente = (IEntity)serviceData.getArgumentList().getProperty(IN_CEDENTE);
			InputStream inInputStream = (InputStream) serviceData.getArgumentList().getProperty(IN_INPUT_STREAM);
			
			IGerenciadorBanco gerenciadorBanco = this.retrieveGerenciadorBanco(inCedente);
			
			log.debug("Executando o m�todo de remessa avulsa do gerenciador");
			List<DocumentoRetornoResultado>  result = gerenciadorBanco.receberRetorno(inCedente, inInputStream, serviceData);
			
			log.debug("Adicionando a mensagem de sucesso");
			this.addInfoMessage(serviceData, "RECEBER_RETORNO_SUCESSO", gerenciadorBanco.getNome());
			
			serviceData.getOutputData().add(result);
			
		} catch (BusinessException e) {
			log.fatal(e.getErrorList());
			/* O Servi�o n�o precisa adicionar mensagem local. O Manager j� indica qual srv falhou e os par�metros. */
			throw new ServiceException(e.getErrorList());
		} catch (Exception e) {
			log.fatal(e.getMessage());
			
			/* Indica que o servi�o falhou por causa de uma exce��o do hibernate. */
			throw new ServiceException(MessageList.createSingleInternalError(e));
		}
		
	}
}
