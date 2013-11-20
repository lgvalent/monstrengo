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
 * Este serviço é utilizado para gerar o retorno de determinado gerenciador de bancos
 * 
 * <p>Argumentos:</p>
 * IN_CEDENTE: (Long) entidade que indica para qual cedente será lido o arquivo de retorno.
 * IN_DADOS_RETORNO: (File) Dados que serão lidos pelo retorno do gerenciador de documentos.
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
		log.debug("::Iniciando execução do servico GerarBancoRemessaService");
		
		try{
			log.debug("Preparando os argumentos");
			IEntity inCedente = (IEntity)serviceData.getArgumentList().getProperty(IN_CEDENTE);
			InputStream inInputStream = (InputStream) serviceData.getArgumentList().getProperty(IN_INPUT_STREAM);
			
			IGerenciadorBanco gerenciadorBanco = this.retrieveGerenciadorBanco(inCedente);
			
			log.debug("Executando o método de remessa avulsa do gerenciador");
			List<DocumentoRetornoResultado>  result = gerenciadorBanco.receberRetorno(inCedente, inInputStream, serviceData);
			
			log.debug("Adicionando a mensagem de sucesso");
			this.addInfoMessage(serviceData, "RECEBER_RETORNO_SUCESSO", gerenciadorBanco.getNome());
			
			serviceData.getOutputData().add(result);
			
		} catch (BusinessException e) {
			log.fatal(e.getErrorList());
			/* O Serviço não precisa adicionar mensagem local. O Manager já indica qual srv falhou e os parâmetros. */
			throw new ServiceException(e.getErrorList());
		} catch (Exception e) {
			log.fatal(e.getMessage());
			
			/* Indica que o serviço falhou por causa de uma exceção do hibernate. */
			throw new ServiceException(MessageList.createSingleInternalError(e));
		}
		
	}
}
