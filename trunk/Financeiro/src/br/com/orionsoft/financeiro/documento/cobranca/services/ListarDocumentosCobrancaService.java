package br.com.orionsoft.financeiro.documento.cobranca.services;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobranca;
import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobrancaBean;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;

/**
 * Serviço que retorna uma lista de Documentos que foram
 * emitidos.
 * 
 * <p>
 * <b>Argumento:</b><br>
 * TODO DOCUMENTAÇÂO
 * <p>
 * <b>Procedimento:</b><br>
 * <b>Retorna uma lista de DocumentoCobrancaBean de acordo com os critérios passados.</b>
 * 
 * @author Lucio
 * @version 20070303
 * 
 * @spring.bean id="ListarDocumentosCobrancaService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 */
public class ListarDocumentosCobrancaService extends ServiceBasic {
    public static final String SERVICE_NAME = "ListarDocumentosCobrancaService";
    
    public static final String IN_CPF_CNPJ_OPT = "cpfCnpj";
    public static final String IN_DOCUMENTO_COBRANCA_CATEGORIA_ID_OPT = "documentoCobrancaCategoriaId";
//    public static final String IN_ITEM_CUSTO_ID_OPT = "itemCustoId";
    public static final String IN_ESCRITORIO_CONTABIL_ID_OPT = "escritorioId";
    public static final String IN_MUNICIPIO_ID_OPT = "municipioId";
    public static final String IN_EXCLUIR_MUNICIPIO_OPT = "excluirMunicipio";
    public static final String IN_DATA_VENCIMENTO_DE_OPT = "dataVencimentoDe";
    public static final String IN_DATA_VENCIMENTO_ATE_OPT = "dataVencimentoAte";
    public static final String IN_DATA_DOCUMENTO_DE_OPT = "dataDocumentoDe";
    public static final String IN_DATA_DOCUMENTO_ATE_OPT = "dataDocumentoAte";
    public static final String IN_FIELD_NAME = "fieldName";
    public static final String IN_TIPO_ORDEM_OPT = "ordenar";

    /* Juliana (18/01/2007) Constantes que identificam a 
     * ordem de impressão: ordenar por endereço, por nome ou por contrato*/
    public static final int ORDEM_ENDERECO = 0;
    public static final int ORDEM_NOME = 1;
    public static final int ORDEM_CONTRATO = 2;
    
    public String getServiceName() {
        return SERVICE_NAME;
    }
    
    @SuppressWarnings("unchecked")
	public void execute(ServiceData serviceData) throws ServiceException {
        try {
            log.debug("Preparando os argumentos");

            String inCpfCnpj = "";
            if (serviceData.getArgumentList().containsProperty(IN_CPF_CNPJ_OPT))
                inCpfCnpj = (String) serviceData.getArgumentList().getProperty(IN_CPF_CNPJ_OPT);
            Long inDocumentoCobrancaCategoriaId = null;
            if (serviceData.getArgumentList().containsProperty(IN_DOCUMENTO_COBRANCA_CATEGORIA_ID_OPT))
            	inDocumentoCobrancaCategoriaId = (Long) serviceData.getArgumentList().getProperty(IN_DOCUMENTO_COBRANCA_CATEGORIA_ID_OPT);
//            Long inItemCustoId = null;
//            if (serviceData.getArgumentList().containsProperty(IN_ITEM_CUSTO_ID_OPT))
//            	inItemCustoId = (Long) serviceData.getArgumentList().getProperty(IN_ITEM_CUSTO_ID_OPT);
            Long inEscritorioId = null;
            if (serviceData.getArgumentList().containsProperty(IN_ESCRITORIO_CONTABIL_ID_OPT))
                inEscritorioId = (Long) serviceData.getArgumentList().getProperty(IN_ESCRITORIO_CONTABIL_ID_OPT);
            Long inMunicipioId = null;
            if (serviceData.getArgumentList().containsProperty(IN_MUNICIPIO_ID_OPT))
                inMunicipioId = (Long) serviceData.getArgumentList().getProperty(IN_MUNICIPIO_ID_OPT);
            Boolean inExcluirMunicipioId = false;
            if (serviceData.getArgumentList().containsProperty(IN_EXCLUIR_MUNICIPIO_OPT))
                inExcluirMunicipioId = (Boolean) serviceData.getArgumentList().getProperty(IN_EXCLUIR_MUNICIPIO_OPT);

            Calendar inDataVencimentoDe = CalendarUtils.getCalendarBaseDate();
            if (serviceData.getArgumentList().containsProperty(IN_DATA_VENCIMENTO_DE_OPT))
                inDataVencimentoDe = (Calendar) serviceData.getArgumentList().getProperty(IN_DATA_VENCIMENTO_DE_OPT);
            Calendar inDataVencimentoAte = CalendarUtils.getCalendar(2100, Calendar.DECEMBER, 31);
            if (serviceData.getArgumentList().containsProperty(IN_DATA_VENCIMENTO_ATE_OPT))
                inDataVencimentoAte = (Calendar) serviceData.getArgumentList().getProperty(IN_DATA_VENCIMENTO_ATE_OPT);


            Calendar inDataDocumentoDe = CalendarUtils.getCalendarBaseDate();
            if (serviceData.getArgumentList().containsProperty(IN_DATA_DOCUMENTO_DE_OPT))
                inDataDocumentoDe = (Calendar) serviceData.getArgumentList().getProperty(IN_DATA_DOCUMENTO_DE_OPT);
            Calendar inDataDocumentoAte = CalendarUtils.getCalendar(2100, Calendar.DECEMBER, 31);
            if (serviceData.getArgumentList().containsProperty(IN_DATA_DOCUMENTO_ATE_OPT))
                inDataDocumentoAte = (Calendar) serviceData.getArgumentList().getProperty(IN_DATA_DOCUMENTO_ATE_OPT);
            /* Juliana (18/01/2007) ordenar por endereço ou nome - default: ordenação por endereço*/
            int ordenar = ORDEM_ENDERECO;
            if (serviceData.getArgumentList().containsProperty(IN_TIPO_ORDEM_OPT))
                ordenar = (Integer) serviceData.getArgumentList().getProperty(IN_TIPO_ORDEM_OPT);
            

			String sqlWhere = "select " + 
			        DocumentoCobrancaBean.QUERY_SELECT +
					" from " +  DocumentoCobranca.class.getSimpleName()  + " entity where (true = true) ";
			
			/* Data do Documento */
			sqlWhere += "and entity.data between '" + new SimpleDateFormat("yyyy-MM-dd").format(inDataDocumentoDe.getTime()) + "' and '"+ new SimpleDateFormat("yyyy-MM-dd").format(inDataDocumentoAte.getTime()) + "' ";

			/* Data de vencimento */
			sqlWhere += "and entity.dataVencimento between '" + new SimpleDateFormat("yyyy-MM-dd").format(inDataVencimentoDe.getTime()) + "' and '"+ new SimpleDateFormat("yyyy-MM-dd").format(inDataVencimentoAte.getTime()) + "' ";

            if (inDocumentoCobrancaCategoriaId != null)
                sqlWhere += "and entity." + DocumentoCobranca.DOCUMENTO_COBRANCA_CATEGORIA + "= " + inDocumentoCobrancaCategoriaId + " ";

//            if (inItemCustoId != null)
//                sqlWhere += "and entity. = " + in + " ";

            if (StringUtils.isNotEmpty(inCpfCnpj))
            	sqlWhere += "and entity.contrato.pessoa.documento = '" + inCpfCnpj + "' ";
            else {
            	if (inEscritorioId != null)
            		sqlWhere += "and entity.contrato.pessoa.id in (select pj.id from Juridica pj where pj.escritorioContabil.id = " + inEscritorioId + ") ";
            	if (inMunicipioId != null)
            		sqlWhere += "and entity.contrato.pessoa.enderecoCorrespondencia.municipio.id " + (inExcluirMunicipioId ? "<>" : "=") + " " + inMunicipioId + " ";
            }
            
            
            /*Define a ORDEM*/
            String sqlOrder =  "order by ";
            if(ordenar == ORDEM_ENDERECO){
                sqlOrder += 
                "entity.contrato.pessoa.enderecoCorrespondencia.municipio.nome, " +
                "entity.contrato.pessoa.enderecoCorrespondencia.logradouro.tipoLogradouro.nome, " +
                "entity.contrato.pessoa.enderecoCorrespondencia.logradouro.nome, " +
                "entity.contrato.pessoa.enderecoCorrespondencia.numero "; 
            }else            	
            /*ordenar pelo nome*/
            if(ordenar == ORDEM_NOME){
                sqlOrder +=  "entity.contrato.pessoa.nome ";
            /*ordenar pelo contrato*/
            }else
            if(ordenar == ORDEM_CONTRATO){
                sqlOrder +=  "entity.contrato";
            }

            String sql = sqlWhere.concat(sqlOrder);
            
            List<DocumentoCobrancaBean> list = serviceData.getCurrentSession().createQuery(sql).list();
            if(log.isDebugEnabled())
            	log.debug("Expressão:" + sql);
            
            serviceData.getOutputData().add(list);

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
    
	/**
	 * Constrói uma lista de opções para  
	 * ordenar pelo endereço ou pelo nome.
	 * @return
	 * @author Juliana 20070118
	 */
    public static List<SelectItem> getTiposOrdem(){
		List<SelectItem> result = new ArrayList<SelectItem>(3);
		result.add(new SelectItem(ListarDocumentosCobrancaService.ORDEM_ENDERECO, "Endereço"));
		result.add(new SelectItem(ListarDocumentosCobrancaService.ORDEM_NOME, "Nome"));
		result.add(new SelectItem(ListarDocumentosCobrancaService.ORDEM_CONTRATO, "Código do Contrato"));
		return result;
	}

}
