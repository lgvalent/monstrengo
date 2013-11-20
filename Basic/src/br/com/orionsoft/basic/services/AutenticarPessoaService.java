package br.com.orionsoft.basic.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;

import br.com.orionsoft.basic.entities.Contrato;
import br.com.orionsoft.basic.entities.pessoa.Pessoa;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.services.Operator;
import br.com.orionsoft.monstrengo.crud.services.QueryCondiction;
import br.com.orionsoft.monstrengo.crud.services.QueryService;

/**
 * Este serviço permite autenticar uma pessoa utilizando o número do documento da pessoa,
 * sua senha e opcionalmente seu código de segurança.
 * 
 * <p>Argumentos:</p>
 * IN_NUMERO_DOCUMENTO: (String) Numero do CNPJ/CPF ou outro documento que é descendente de Pessoa.documento<br> 
 * IN_SENHA: (String) Senha pessoal
 * IN_CODIGO_SEGURANCA_OPT: (String) Código de segurança de três dígitos gerado pelo sistema e fornecido à pessoa.
 *                               Este código é essencial para diferenciar pessoas que utilizam o mesmo CPF e seus dependentes.<br>
 * IN_OPCAO_CONTRATOS_OPT: (Integer) Permite definir se o serviço deve buscar somente contratos Ativos, Inativos ou Todos.<br>
 * 		<li>OPCAO_CONTRATOS_TODOS: </li>
 * 		<li>OPCAO_CONTRATOS_ATIVOS: </li>
 * 		<li>OPCAO_CONTRATOS_INATIVOS:</li>
 * IN_OPCAO_CONTRATOS_OPT: (Class) Permite definir qual o tipo de classe de contrato específico. Exemplo: ContratoFinanceiro.class retornará somente os contratos deste tipo.<br>
 * 
 * <b>Procedimento:</b><br>
 * Executa uma query
 * <b>Retorna uma lista de List<IEntityList> com os contratos encontrados</b>
 * Se não encontrar, retorna uma lista vazia.
 * 
 * 
 * @author Lucio
 * @since 20071017
 * @version 20071128
 * 
 * @spring.bean id="AutenticarPessoaService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 */
public class AutenticarPessoaService extends ServiceBasic {

	public static final String SERVICE_NAME = "AutenticarPessoaService";
	
	public static final String IN_NUMERO_DOCUMENTO = "numeroDocumento";
	public static final String IN_SENHA = "senha";
	public static final String IN_CODIGO_SEGURANCA_OPT = "codigoSeguranca";
	public static final String IN_OPCAO_CONTRATOS_OPT = "opcaoContratos";
	public static final String IN_CLASSE_CONTRATOS_OPT = "classeContrato";
	
	public static final int OPCAO_CONTRATOS_TODOS    = 0;
	public static final int OPCAO_CONTRATOS_ATIVOS   = 1;
	public static final int OPCAO_CONTRATOS_INATIVOS = 2;

	public String getServiceName() {
		return SERVICE_NAME;
	}

	@SuppressWarnings("unchecked")
	public void execute(ServiceData serviceData) throws ServiceException {
		log.debug("::Iniciando execução do servico");

		try{
			String inNumeroDocumento = (String) serviceData.getArgumentList().getProperty(IN_NUMERO_DOCUMENTO);
			String inSenha = (String) serviceData.getArgumentList().getProperty(IN_SENHA);
			String inCodigoSeguranca = serviceData.getArgumentList().containsProperty(IN_CODIGO_SEGURANCA_OPT)?(String) serviceData.getArgumentList().getProperty(IN_CODIGO_SEGURANCA_OPT):null;

			Integer inOpcaoContratos = serviceData.getArgumentList().containsProperty(IN_OPCAO_CONTRATOS_OPT)?(Integer) serviceData.getArgumentList().getProperty(IN_OPCAO_CONTRATOS_OPT):OPCAO_CONTRATOS_TODOS;
			Class inClasseContrato = serviceData.getArgumentList().containsProperty(IN_CLASSE_CONTRATOS_OPT)?(Class) serviceData.getArgumentList().getProperty(IN_CLASSE_CONTRATOS_OPT):null;

			List<QueryCondiction> condictions = new ArrayList<QueryCondiction>();

			QueryCondiction condiction = new QueryCondiction(this.getServiceManager().getEntityManager(), Contrato.class,
					Contrato.PESSOA + '.' + Pessoa.DOCUMENTO,
					Operator.EQUAL,
					inNumeroDocumento,
					"");
			condictions.add(condiction);

			condiction = new QueryCondiction(this.getServiceManager().getEntityManager(), Contrato.class,
					Contrato.PESSOA + '.' + Pessoa.SENHA,
					Operator.EQUAL,
					DigestUtils.md5Hex(inSenha),
					"");
			condictions.add(condiction);

			if(inCodigoSeguranca!=null){
				condiction = new QueryCondiction(this.getServiceManager().getEntityManager(), Contrato.class,
						Contrato.PESSOA + '.' + Pessoa.CODIGO_SEGURANCA,
						Operator.EQUAL,
						inCodigoSeguranca,
				"");
				condictions.add(condiction);
			}

			/* Filtra somente os contratos ativos ou inativos de acordo com a opção passada */
			if(inOpcaoContratos == OPCAO_CONTRATOS_ATIVOS){
				condiction = new QueryCondiction(this.getServiceManager().getEntityManager(), Contrato.class,
						Contrato.INATIVO,
						Operator.NOT_EQUAL,
						"true",
				"");
				condictions.add(condiction);
			}
			else if(inOpcaoContratos == OPCAO_CONTRATOS_INATIVOS){
				condiction = new QueryCondiction(this.getServiceManager().getEntityManager(), Contrato.class,
						Contrato.INATIVO,
						Operator.EQUAL,
						"true",
				"");
				condictions.add(condiction);
			}
			
			ServiceData sdQuery = new ServiceData(QueryService.SERVICE_NAME, null);
			
			/* Filtra somente os contrato de uma classe específica */
			if(inClasseContrato != null)
				sdQuery.getArgumentList().setProperty(QueryService.IN_ENTITY_TYPE, inClasseContrato);
			else
				sdQuery.getArgumentList().setProperty(QueryService.IN_ENTITY_TYPE, Contrato.class);
			
			sdQuery.getArgumentList().setProperty(QueryService.IN_QUERY_CONDICTIONS, condictions);

			this.getServiceManager().execute(sdQuery);

			IEntityList entityList = (IEntityList) sdQuery.getOutputData(QueryService.OUT_ENTITY_LIST);

			if(log.isDebugEnabled())
				log.debug("Foram encontrados " + entityList.size() + " contratos");

			serviceData.getOutputData().add(entityList);

			// retorna a entidade Documento que o gerenciador criou;			
			log.debug("::Fim da execução do serviço");

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
