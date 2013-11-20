package br.com.orionsoft.financeiro.documento.pagamento.services;

import java.math.BigDecimal;
import java.util.Calendar;

import br.com.orionsoft.basic.entities.Contrato;
import br.com.orionsoft.financeiro.documento.pagamento.ConvenioPagamento;
import br.com.orionsoft.financeiro.documento.pagamento.DocumentoPagamento;
import br.com.orionsoft.financeiro.documento.pagamento.DocumentoPagamentoCategoria;
import br.com.orionsoft.financeiro.documento.pagamento.IGerenciadorDocumentoPagamento;
import br.com.orionsoft.financeiro.documento.pagamento.ProvedorDocumentoPagamento;
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
 * CONVENIO_PAGAMENTO : ConvenioCobranca vinculado ao documento
 * IN_DOCUMENTO_PAGAMENTO_CATEGORIA : Fornece o tipo de documento a ser criado
 * IN_DATA_DOCUMENTO : Data do Documento
 * IN_DATA_VENCIMENTO : Data de vencimento do Documento
 * IN_VALOR_DOCUMENTO : Valor do Documento
 * IN_TRANSACAO : Crédito ou débito
 * 
 * @author Antonio Alves
 * @version 20070724
 * 
 * @spring.bean id="CriarDocumentoPagamentoService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 * @spring.property name="provedorDocumentoPagamento" ref="ProvedorDocumentoPagamento"
 * @spring.property name="transactional" value="true"
 */
public class CriarDocumentoPagamentoService extends DocumentoPagamentoServiceBasic {
	
	public static final String SERVICE_NAME = "CriarDocumentoPagamentoService";
	public static final String IN_CONTRATO = "contrato";
	public static final String IN_DOCUMENTO_PAGAMENTO_CATEGORIA = "documentoPagamentoCategoria"; 
	public static final String IN_DATA_DOCUMENTO = "dataDocumento";
	public static final String IN_DATA_VENCIMENTO = "dataVencimento";
	public static final String IN_VALOR_DOCUMENTO = "valorDocumento";
	public static final String IN_TRANSACAO = "transacao";
	
	public String getServiceName() {
		return SERVICE_NAME;
	}
	
	public void execute(ServiceData serviceData) throws ServiceException {
		log.debug("::Iniciando execução do servico CriarDocumentoPagamentoService");
		
		try{
			log.debug("Preparando os argumentos");
			IEntity<Contrato> inContrato = (IEntity<Contrato>) serviceData.getArgumentList().getProperty(IN_CONTRATO);
			IEntity<? extends DocumentoPagamentoCategoria> inDocumentoPagamentoCategoria = (IEntity<? extends DocumentoPagamentoCategoria>) serviceData.getArgumentList().getProperty(IN_DOCUMENTO_PAGAMENTO_CATEGORIA);
			Calendar inDataDocumento = (Calendar) serviceData.getArgumentList().getProperty(IN_DATA_DOCUMENTO);
			Calendar inDataVencimento = (Calendar) serviceData.getArgumentList().getProperty(IN_DATA_VENCIMENTO);
			BigDecimal inValorDocumento = (BigDecimal) serviceData.getArgumentList().getProperty(IN_VALOR_DOCUMENTO);
			Transacao inTransacao = (Transacao) serviceData.getArgumentList().getProperty(IN_TRANSACAO);
			
			/*			 
			 * pega o provedor
			 */
			ProvedorDocumentoPagamento provedor = (ProvedorDocumentoPagamento) this.getProvedorDocumentoPagamento();
			
			/*			 
			 * pega o nome do gerenciador que está na formaPagamento
			 */			 
			IEntity<? extends DocumentoPagamentoCategoria> convenio = inDocumentoPagamentoCategoria.getProperty(DocumentoPagamentoCategoria.CONVENIO_PAGAMENTO).getValue().getAsEntity();
			if(convenio == null)
				throw new ServiceException(MessageList.create(CriarDocumentoPagamentoService.class, "CONVENIO_VAZIO", inDocumentoPagamentoCategoria.toString()));
			String gerenciadorNome = convenio.getProperty(ConvenioPagamento.NOME_GERENCIADOR_DOCUMENTO).getValue().getAsString();

			/*
			 * pede para o provedor retrieveGerenciadorDocumento(String nome do gerenciador que foi pego na FP)
			 */
			IGerenciadorDocumentoPagamento gerenciador = provedor.retrieveGerenciadorDocumentoPagamento(gerenciadorNome);
			
			/* 
			 * com o gerenciador em mãos, execute o metodo dele criarDocument(1,2,3);
			 */
			IEntity<? extends DocumentoPagamento> documento = gerenciador.criarDocumento(inContrato, inDocumentoPagamentoCategoria, inDataDocumento, inDataVencimento, inValorDocumento, inTransacao, serviceData);

			log.debug("Adicionando a mensagem de sucesso");
			serviceData.getMessageList().add(BusinessMessage.TYPE_INFO, CriarDocumentoPagamentoService.class, "DOCUMENTO_CRIADO", documento.toString());

			/* 
			 * retorna a entidade Documento que o gerenciador criou;
			 */			
			log.debug("::Fim da execução do serviço");
			serviceData.getOutputData().add(documento);
			
		} catch (BusinessException e) {
			log.fatal(e.getErrorList());
			/* 
			 * O Serviço não precisa adicionar mensagem local. O Manager já indica qual srv falhou e os parâmetros. 
			 */
			throw new ServiceException(e.getErrorList());
		} catch (Exception e) {
			log.fatal(e.getMessage());
			
			/* 
			 * Indica que o serviço falhou por causa de uma exceção do hibernate. 
			 */
			throw new ServiceException(MessageList.createSingleInternalError(e));
		}
		
	}
}
