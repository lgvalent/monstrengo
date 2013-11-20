package br.com.orionsoft.financeiro.documento.pagamento.services;

import java.util.ArrayList;
import java.util.List;

import br.com.orionsoft.financeiro.documento.pagamento.DocumentoPagamento;
import br.com.orionsoft.financeiro.documento.pagamento.IGerenciadorDocumentoPagamento;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IProperty;

/**
 * Este serviço utiliza o Provedor de documentos
 * para obter da instância de um documento, a lista de
 * propriedades que deverão ser exibidas e preenchidas
 * pelo operador na interface.
 * 
 * <p>Argumentos:
 * IN_DOCUMENTO : Instância IEntity de um Documento do financeiro

 * <p>Procedimento:
 * <b>Retorna uma lista de propriedades (List<IProperty>) já ligadas com sua entidade
 * e que disponibiliza uma interface para alteração dos dados</b>
 * 
 * @version 20080630
 * 
 * ------------------------------------------------------------------------------------------------------------------------
 * ATENÇÃO: ESTE SERVIÇO FOI CRIADO APENAS PARA OBTER AS PROPRIEDADES DE UM DOCUMENTO DE PAGAMENTO NA TELA DE QUITAÇÃO.
 * TENTEI MUDAR O SERVIÇO ORIGINAL, PARA QUE, DEPENDENDO DO TIPO DA INSTÂNCIA DO DOCUMENTO, SELECIONASSE O GERENCIADOR
 * CORRETA, MAS O SERVIÇO EXTENDS DocumentoCobrancaServiceBasic LOGO NÃO FOI POSSÍVEL TRATAR O DocumentoPagamento
 * ------------------------------------------------------------------------------------------------------------------------
 * 
 * @spring.bean id="ObterPropriedadesPreenchimentoManualPagamentoService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 * @spring.property name="provedorDocumentoPagamento" ref="ProvedorDocumentoPagamento"
 */
public class ObterPropriedadesPreenchimentoManualPagamentoService extends DocumentoPagamentoServiceBasic {
	
	public static final String SERVICE_NAME = "ObterPropriedadesPreenchimentoManualPagamentoService";
	public static final String IN_DOCUMENTO = "documento";
	
	public String getServiceName() {
		return SERVICE_NAME;
	}
	
	public void execute(ServiceData serviceData) throws ServiceException {
		log.debug("::Iniciando execução do servico ObterPropriedadesPreenchimentoManualPagamentoService");
		
		try{
			log.debug("Preparando os argumentos");
			IEntity<? extends DocumentoPagamento> inDocumento = (IEntity<? extends DocumentoPagamento>) serviceData.getArgumentList().getProperty(IN_DOCUMENTO);
			
			List<IProperty> result = null; 

			/* Verifica se o documento passado é nulo então retorna uma lista vazia */
			if(inDocumento == null)
				result = new ArrayList<IProperty>();
			else{
				IGerenciadorDocumentoPagamento gerenciador = this.retrieveGerenciadorPorDocumento(inDocumento.getObject());

				// Verifica se o Gerenciador possui campos de preenchimento manual
				if(gerenciador.isPreenchimentoManual()){
					log.debug("Obtendo a lista de propriedades para preenchimento manual");
					result = gerenciador.retrievePropriedadesPreenchimentoManual(inDocumento);
				} else
					// Cria uma lista vazia 
					result = new ArrayList<IProperty>(0);
			}
			
			// retorna a entidade Documento que o gerenciador criou;			
			log.debug("::Fim da execução do serviço");
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
