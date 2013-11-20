package br.com.orionsoft.financeiro.documento.cobranca.services;

import java.math.BigDecimal;
import java.util.Calendar;

import br.com.orionsoft.financeiro.documento.cobranca.ConvenioCobranca;
import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobrancaCategoria;
import br.com.orionsoft.financeiro.documento.cobranca.IGerenciadorDocumentoCobranca;
import br.com.orionsoft.financeiro.documento.cobranca.ProvedorDocumentoCobranca;
import br.com.orionsoft.financeiro.gerenciador.entities.Transacao;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;

/**
 * Este serviço cria um Documento que será passado para o serviço 
 * InserirLancamento.
 * 
 * <p>Argumentos:</p>
 * IN_CONTRATO : Contrato relacionado ao documento
 * IN_DOCUMENTO_COBRANCA_CATEGORIA : Fornece o tipo de documento a ser criado
 * IN_DATA_DOCUMENTO : Data do Documento
 * IN_DATA_VENCIMENTO : Data de vencimento do Documento
 * IN_VALOR_DOCUMENTO : Valor do Documento
 * 
 * @version 20060529
 * 
 * @spring.bean id="CriarDocumentoCobrancaService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 * @spring.property name="provedorDocumentoCobranca" ref="ProvedorDocumentoCobranca"
 * @spring.property name="transactional" value="true"
 */
public class CriarDocumentoCobrancaService extends DocumentoCobrancaServiceBasic {
	
	public static final String SERVICE_NAME = "CriarDocumentoCobrancaService";
	public static final String IN_CONTRATO = "contrato";
	public static final String IN_DOCUMENTO_COBRANCA_CATEGORIA = "documentoCobrancaCategoria"; 
	public static final String IN_DATA_DOCUMENTO = "dataDocumento";
	public static final String IN_DATA_VENCIMENTO = "dataVencimento";
	public static final String IN_VALOR_DOCUMENTO = "valorDocumento";
	public static final String IN_TRANSACAO = "transacao";
	
	public String getServiceName() {
		return SERVICE_NAME;
	}
	
	public void execute(ServiceData serviceData) throws ServiceException {
		log.debug("::Iniciando execução do servico CriarDocumentoCobrancaService");
		
		try{
			log.debug("Preparando os argumentos");
			IEntity inContrato = (IEntity) serviceData.getArgumentList().getProperty(IN_CONTRATO);
			IEntity inDocumentoCobrancaCategoria = (IEntity) serviceData.getArgumentList().getProperty(IN_DOCUMENTO_COBRANCA_CATEGORIA);
			Calendar inDataDocumento = (Calendar) serviceData.getArgumentList().getProperty(IN_DATA_DOCUMENTO);
			Calendar inDataVencimento = (Calendar) serviceData.getArgumentList().getProperty(IN_DATA_VENCIMENTO);
			BigDecimal inValorDocumento = (BigDecimal) serviceData.getArgumentList().getProperty(IN_VALOR_DOCUMENTO);
			Transacao inTransacao = (Transacao) serviceData.getArgumentList().getProperty(IN_TRANSACAO);
			
			// pega o provedor
			ProvedorDocumentoCobranca provedor = (ProvedorDocumentoCobranca) this.getProvedorDocumentoCobranca();
			
			// pega o nome do gerenciador que está na documentoCobrancaCategoria
			IEntity convenio = inDocumentoCobrancaCategoria.getProperty(DocumentoCobrancaCategoria.CONVENIO_COBRANCA).getValue().getAsEntity();
			if(convenio == null)
				throw new ServiceException(MessageList.create(CriarDocumentoCobrancaService.class, "CONVENIO_VAZIO", inDocumentoCobrancaCategoria.toString()));
			String gerenciadorNome = convenio.getProperty(ConvenioCobranca.NOME_GERENCIADOR_DOCUMENTO).getValue().getAsString();

			// pede para o provedor retrieveGerenciadorDocumento(String nome do gerenciador que foi pego na FP)
			IGerenciadorDocumentoCobranca gerenciador = provedor.retrieveGerenciadorDocumentoCobranca(gerenciadorNome);
			
			log.debug("Criando o documento pelo gerenciador");
			// com o gerenciador em mãos, execute o metodo dele criarDocument(1,2,3);
			IEntity documento = gerenciador.criarDocumento(inContrato, inDocumentoCobrancaCategoria, inDataDocumento, inDataVencimento, inValorDocumento, inTransacao, serviceData);

			log.debug("Adicionando a mensagem de sucesso");
			serviceData.getMessageList().add(BusinessMessage.TYPE_INFO, CriarDocumentoCobrancaService.class, "DOCUMENTO_CRIADO", documento.toString());

			// retorna a entidade Documento que o gerenciador criou;			
			log.debug("::Fim da execução do serviço");
			serviceData.getOutputData().add(documento);
			
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
