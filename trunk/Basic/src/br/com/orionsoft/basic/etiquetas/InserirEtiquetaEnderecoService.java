package br.com.orionsoft.basic.etiquetas;

import org.apache.commons.lang.StringUtils;

import br.com.orionsoft.basic.entities.Contrato;
import br.com.orionsoft.basic.entities.endereco.Endereco;
import br.com.orionsoft.basic.entities.pessoa.Pessoa;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.labels.entities.AddressLabel;
import br.com.orionsoft.monstrengo.crud.labels.services.InsertAddressLabelService;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;

/**
 * Serviço que insere uma etiqueta de enderaço
 * 
 * @version 20060804
 * 
 * @spring.bean id="InserirEtiquetaEnderecoService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 */
public class InserirEtiquetaEnderecoService extends ServiceBasic {
    public static final String SERVICE_NAME = "InserirEtiquetaEnderecoService";

    public static final String IN_OPERADOR = "operador";
    
    public static final String IN_PESSOA_ID_OPT = "pessoaId";
    public static final String IN_CONTRATO_ID_OPT = "contratoId";
    public static final String IN_PESSOA_OPT = "pessoa";
    public static final String IN_CONTRATO_OPT = "contrato";

    public String getServiceName() {
        return SERVICE_NAME;
    }

    public void execute(ServiceData serviceData) throws ServiceException {
        log.debug("::Iniciando a execução do serviço InserirEtiquetaEnderecoService");
        try {
            IEntity<ApplicationUser> inOperador = (IEntity<ApplicationUser>)serviceData.getArgumentList().getProperty(IN_OPERADOR);

        	IEntity<Pessoa> inPessoa=null;

        	if(serviceData.getArgumentList().containsProperty(IN_PESSOA_OPT))
            		inPessoa = (IEntity<Pessoa>)serviceData.getArgumentList().getProperty(IN_PESSOA_OPT);
            else
                if(serviceData.getArgumentList().containsProperty(IN_PESSOA_ID_OPT)){
            		long inPessoaId = (Long)serviceData.getArgumentList().getProperty(IN_PESSOA_ID_OPT);
            		inPessoa = UtilsCrud.retrieve(this.getServiceManager(), Pessoa.class, inPessoaId, serviceData);
                }
                else
                    if(serviceData.getArgumentList().containsProperty(IN_CONTRATO_OPT)){
                		IEntity<Contrato> inContrato = (IEntity<Contrato>) serviceData.getArgumentList().getProperty(IN_CONTRATO_OPT);
                		inPessoa = inContrato.getProperty(Contrato.PESSOA).getValue().getAsEntity();
                    }
                    else
                    	if(serviceData.getArgumentList().containsProperty(IN_CONTRATO_ID_OPT)){
                    		long inContratoId = (Long)serviceData.getArgumentList().getProperty(IN_CONTRATO_ID_OPT);
                    		IEntity<Contrato> inContrato = UtilsCrud.retrieve(this.getServiceManager(), Contrato.class, inContratoId, serviceData);
                    		inPessoa = inContrato.getProperty(Contrato.PESSOA).getValue().getAsEntity();
                    	}
            
        	/* Converte a entidade passada para a estrutura de uma etiqueta de endereçamento */
        	String linha1="";
        	String linha2="";
        	String linha3="";
        	String linha4="";
        	String linha5="";
            
        	/*
        	 * 1-Fantasia
        	 * 2-Nome
        	 * 3-Logradouro, Numero
        	 * 4-Complemento e Bairro
        	 * 5-CEP - Cidade-UF
        	 */
        	
            /* LINHA 1 - Nome fantasia */
            linha1 = inPessoa.getProperty(Pessoa.NOME).getValue().getAsString();
            
            /* LINHA 2 - Nome/Razão Social */
        	if(inPessoa.getProperty(Pessoa.JURIDICA).getValue().getAsBoolean())
            	linha2 = inPessoa.getProperty(Pessoa.APELIDO).getValue().getAsString();

            /* LINHA 3 - Logradouro, numero */
            IEntity<Endereco> endereco = inPessoa.getProperty(Pessoa.ENDERECO_CORRESPONDENCIA).getValue().getAsEntity();
            Endereco oEndereco = endereco.getObject();
            linha3 = oEndereco.getLogradouro().getTipoLogradouro().getNome() + " " + oEndereco.getLogradouro().getNome() + ", " +oEndereco.getNumero().toString();
            
            /* LINHA 4 - Complemento - Bairro */
            if(!StringUtils.isEmpty(oEndereco.getComplemento()))
            	linha4 = oEndereco.getComplemento() + " - ";
            
            if(oEndereco.getBairro() != null)
            	linha4  += oEndereco.getBairro().getNome();

            /* LINHA 5 - CEP: ##.###-### - Cidade-UF */
            linha5 = "CEP: " + endereco.getProperty(Endereco.CEP).getValue().getAsString() + " - " + oEndereco.getMunicipio().toString();
            
            ServiceData sdInserir = new ServiceData(InsertAddressLabelService.SERVICE_NAME, serviceData);
            sdInserir.getArgumentList().setProperty(InsertAddressLabelService.IN_APPLICATION_USER, inOperador);
            sdInserir.getArgumentList().setProperty(InsertAddressLabelService.IN_LINE1_OPT, linha1);
            sdInserir.getArgumentList().setProperty(InsertAddressLabelService.IN_LINE2_OPT, linha2);
            sdInserir.getArgumentList().setProperty(InsertAddressLabelService.IN_LINE3_OPT, linha3);
            sdInserir.getArgumentList().setProperty(InsertAddressLabelService.IN_LINE4_OPT, linha4);
            sdInserir.getArgumentList().setProperty(InsertAddressLabelService.IN_LINE5_OPT, linha5);
            this.getServiceManager().execute(sdInserir);

            /* Adiciona uma mensagem de etiqueta inserida com sucesso */
            /* CRIAR O PROPERTIES DA MENSAGEM */
			log.debug("Etiqueta inserida com sucesso");
			IEntity<AddressLabel> etiqueta = sdInserir.getFirstOutput();
			this.addInfoMessage(serviceData, "INSERIR_SUCESSO", etiqueta.toString());

            /* Retorna a etiqueta criada */
            serviceData.getOutputData().add(etiqueta);

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
