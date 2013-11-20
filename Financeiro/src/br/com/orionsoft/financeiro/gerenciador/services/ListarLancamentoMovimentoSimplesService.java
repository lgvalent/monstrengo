/*
 * Created on 22/03/2006 by antonio
 */
package br.com.orionsoft.financeiro.gerenciador.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.faces.model.SelectItem;

import org.hibernate.Query;

import br.com.orionsoft.basic.entities.Contrato;
import br.com.orionsoft.basic.entities.pessoa.Pessoa;
import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobranca;
import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoItem;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimento;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimentoCategoria;
import br.com.orionsoft.financeiro.gerenciador.entities.Transacao;
import br.com.orionsoft.financeiro.gerenciador.services.ListarLancamentoMovimentoService.QueryLancamentoMovimento;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;

/**
 * Serviço que insere um movimento na conta.
 * 
 * <p>
 * <b>Argumento:</b><br>
 * 
 * <p>
 * <b>Procedimento:</b> 
 * 
 * @author antonio
 * @version 20060322
 * 
 * @spring.bean id="ListarLancamentoMovimentoSimplesService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 */
public class ListarLancamentoMovimentoSimplesService extends ServiceBasic {
    public static final String SERVICE_NAME = "ListarLancamentoMovimentoSimplesService";

    public static final String IN_CONTAS_ID_OPT = "contasId";
    public static final String IN_CONTRATO_ID_OPT = "contratoId";
    public static final String IN_CPF_CNPJ_OPT = "cpfCnpj";
    public static final String IN_DATA_FINAL_OPT = "dataFinal";
    public static final String IN_DATA_INICIAL_OPT = "dataInicial";
    public static final String IN_DOCUMENTO_ID_OPT = "documentoId";
    public static final String IN_DOCUMENTO_COBRANCA_CATEGORIA_ID_OPT = "documentoCobrancaCategoriaId";
    public static final String IN_LANCAMENTO_ID_OPT = "lancamentoId";
    public static final String IN_ITEM_CUSTO_ID_OPT = "itemCustoId";
    public static final String IN_ORDEM = "ordem";
    public static final String IN_PROPRIEDADE_DATA = "propriedadeData";
    public static final String IN_TIPO_OPERACAO_OPT = "tipoOperacao";
    public static final String IN_TIPO_TRANSACAO_OPT = "tipoTransacao";

    public static final int ORDEM_DATA = 1;
    public static final int ORDEM_LANCAMENTO = 2;
    public static final int ORDEM_NOME = 3;
    
//    public static final int TIPO_TRANSACAO_CREDITO = Lancamento.TRANSACAO_CREDITO;
//    public static final int TIPO_TRANSACAO_DEBITO = Lancamento.TRANSACAO_DEBITO;
//    public static final int TIPO_TRANSACAO_TODAS = -1;

    /**
     * Constrói uma lista de opções com os tipo possíveis de 
     * transação que podem ser geradas pelo processo.
     * @return
     */
    public static List<SelectItem> getTiposTransacao() {
        List<SelectItem> result = new ArrayList<SelectItem>(3);
        result.add(new SelectItem(null, "Todas"));
        result.add(new SelectItem(Transacao.CREDITO, Transacao.CREDITO.getNome()));
        result.add(new SelectItem(Transacao.DEBITO, Transacao.DEBITO.getNome()));
        return result;
    }
    
    /**
     * Constrói uma lista de opções com os tipo possíveis de 
     * data que podem ser geradas pelo processo.
     * @return List<SelectItem>
     */
    public static List<SelectItem> getDataTipos() {
        List<SelectItem> result = new ArrayList<SelectItem>(2);
        result.add(new SelectItem(LancamentoMovimento.DATA_COMPENSACAO, "Data de vencimento"));
        result.add(new SelectItem(LancamentoMovimento.DATA, "Data em que eu lancei"));
        return result;
    }
    
    @SuppressWarnings("unchecked")
	public void execute(ServiceData serviceData) throws ServiceException {
        log.debug("::Iniciando a execução do serviço ListarLancamentoMovimentoService");
        try {
            log.debug("Preparando os argumentos");
            /* Obrigatórios */
            String inPropriedadeData = (String)serviceData.getArgumentList().getProperty(IN_PROPRIEDADE_DATA);
            int inOrdem = (Integer) serviceData.getArgumentList().getProperty(IN_ORDEM);
            
            /* Opcionais */
            Long[] inContasId = (serviceData.getArgumentList().containsProperty(IN_CONTAS_ID_OPT) ?
                    (Long[]) serviceData.getArgumentList().getProperty(IN_CONTAS_ID_OPT) : null);
            Calendar inDataInicial = (serviceData.getArgumentList().containsProperty(IN_DATA_INICIAL_OPT) ?
                    (Calendar)serviceData.getArgumentList().getProperty(IN_DATA_INICIAL_OPT) :
                        CalendarUtils.getCalendarBaseDate());
            Calendar inDataFinal = (serviceData.getArgumentList().containsProperty(IN_DATA_FINAL_OPT) ?
                    (Calendar)serviceData.getArgumentList().getProperty(IN_DATA_FINAL_OPT) : null);
            Long inLancamentoId = (serviceData.getArgumentList().containsProperty(IN_LANCAMENTO_ID_OPT) ?
                    (Long) serviceData.getArgumentList().getProperty(IN_LANCAMENTO_ID_OPT) : null);
            Long inDocumentoId = (serviceData.getArgumentList().containsProperty(IN_DOCUMENTO_ID_OPT) ? 
                    (Long) serviceData.getArgumentList().getProperty(IN_DOCUMENTO_ID_OPT) : null);
            Long inContratoId = (serviceData.getArgumentList().containsProperty(IN_CONTRATO_ID_OPT) ?
                    (Long) serviceData.getArgumentList().getProperty(IN_CONTRATO_ID_OPT) : null);
            Long inItemCustoId = (serviceData.getArgumentList().containsProperty(IN_ITEM_CUSTO_ID_OPT) ?
                    (Long) serviceData.getArgumentList().getProperty(IN_ITEM_CUSTO_ID_OPT) : null);
            Long inDocumentoCobrancaCategoriaId = (serviceData.getArgumentList().containsProperty(IN_DOCUMENTO_COBRANCA_CATEGORIA_ID_OPT) ?
                    (Long) serviceData.getArgumentList().getProperty(IN_DOCUMENTO_COBRANCA_CATEGORIA_ID_OPT) : null);
            String inCpfCnpj = (serviceData.getArgumentList().containsProperty(IN_CPF_CNPJ_OPT) ?
                    (String) serviceData.getArgumentList().getProperty(IN_CPF_CNPJ_OPT) : null);
            LancamentoMovimentoCategoria inTipoOperacao = (serviceData.getArgumentList().containsProperty(IN_TIPO_OPERACAO_OPT) ?
                    (LancamentoMovimentoCategoria) serviceData.getArgumentList().getProperty(IN_TIPO_OPERACAO_OPT) : null);
            Transacao inTransacao = (serviceData.getArgumentList().containsProperty(IN_TIPO_TRANSACAO_OPT) ?
                    (Transacao)serviceData.getArgumentList().getProperty(IN_TIPO_TRANSACAO_OPT) : null);
            
            log.debug("Buscando registros");

            /* Montando a clausula SELECT */
            String sqlSelect = QueryLancamentoMovimento.SELECT;
            sqlSelect += " left outer join movimento.lancamento.lancamentos as lancamentos ";
            
            /* Montando a clausula WHERE */
            String sqlWhere = " where (true = true)";
            if (inContasId != null) {
            	String listaContas = "";
            	for (int i = 0; i < inContasId.length; i++) {
            		if (i > 0)
            			listaContas += ", ";
            		listaContas += inContasId[i].toString();
            	}
                sqlWhere += " and (lancamentoMovimento."+LancamentoMovimento.CONTA+" in ("+listaContas+"))";
            }
            if (inLancamentoId != null)
                sqlWhere += " and (lancamentoMovimento."+LancamentoMovimento.LANCAMENTO_MOVIMENTO_CATEGORIA+" = "+inLancamentoId+")";
            if (inDocumentoId != null)
                sqlWhere += " and (lancamentoMovimento."+LancamentoMovimento.LANCAMENTO_MOVIMENTO_CATEGORIA+"."+Lancamento.DOCUMENTO_PAGAMENTO+" = "+Long.toString(inDocumentoId)+")";
            if (inContratoId != null)
                sqlWhere += " and (lancamentomovimento."+LancamentoMovimento.LANCAMENTO_MOVIMENTO_CATEGORIA+"."+Lancamento.CONTRATO+" = "+Long.toString(inContratoId)+")";
            if (inItemCustoId != null)
                sqlWhere += " and (lancamentos."+LancamentoItem.ITEM_CUSTO+" = "+Long.toString(inItemCustoId)+")";
            if (inDocumentoCobrancaCategoriaId != null && inDocumentoCobrancaCategoriaId != 0)
                sqlWhere += " and (lancamentoMovimento."+LancamentoMovimento.LANCAMENTO_MOVIMENTO_CATEGORIA+"."+Lancamento.DOCUMENTO_PAGAMENTO+"."+DocumentoCobranca.DOCUMENTO_COBRANCA_CATEGORIA+" = "+Long.toString(inDocumentoCobrancaCategoriaId)+")";
            if (inCpfCnpj != null) 
            	sqlWhere += " and (lancamentoMovimento."+LancamentoMovimento.LANCAMENTO_MOVIMENTO_CATEGORIA+"."+Lancamento.CONTRATO+"."+Contrato.PESSOA+"."+Pessoa.DOCUMENTO+"='"+inCpfCnpj+"')";
            if (inDataFinal != null)
                sqlWhere += " and (lancamentoMovimento."+inPropriedadeData+" between '"+CalendarUtils.formatToSQLDate(inDataInicial)+"' and '"+CalendarUtils.formatToSQLDate(inDataFinal)+"')";

            if (inTransacao == Transacao.CREDITO)
                sqlWhere += " and (lancamentoMovimento.valor>=0)";
            else
                if (inTransacao == Transacao.DEBITO)
                    sqlWhere += " and (lancamentoMovimento.valor<0)";

            /*
             *  Atribui as condições do filtro da Operação
             *  Para cada Tipo de Operação, há duas operações a serem filtradas. 
             *  Exemplo: Lançar e Extorno de Lançado. 
             */
            if (inTipoOperacao == LancamentoMovimentoCategoria.QUITADO) {
                sqlWhere += " and ((lancamentoMovimento."+LancamentoMovimento.LANCAMENTO_MOVIMENTO_CATEGORIA+" = "+LancamentoMovimentoCategoria.QUITADO+
                ") or (lancamentoMovimento."+LancamentoMovimento.LANCAMENTO_MOVIMENTO_CATEGORIA+" = "+LancamentoMovimentoCategoria.QUITADO_ESTORNADO+"))";
            }
            if (inTipoOperacao == LancamentoMovimentoCategoria.CANCELADO) {
                sqlWhere += " and ((lancamentoMovimento."+LancamentoMovimento.LANCAMENTO_MOVIMENTO_CATEGORIA+" = "+LancamentoMovimentoCategoria.CANCELADO+
                    ") or (lancamentoMovimento."+LancamentoMovimento.LANCAMENTO_MOVIMENTO_CATEGORIA+" = "+LancamentoMovimentoCategoria.CANCELADO_ESTORNADO+"))";
            }

            /* Montando a clausula ORDER */
            String sqlOrder = " order by";
            if (inOrdem == ORDEM_DATA) {
                sqlOrder += " lancamentoMovimento."+inPropriedadeData+", lancamentoMovimento."+IDAO.PROPERTY_ID_NAME;
            }
            if (inOrdem == ORDEM_LANCAMENTO) {
                sqlOrder += " lancamentoMovimento."+LancamentoMovimento.LANCAMENTO_MOVIMENTO_CATEGORIA+", lancamentoMovimento."+inPropriedadeData+", lancamentoMovimento."+IDAO.PROPERTY_ID_NAME;
            }
            if (inOrdem == ORDEM_NOME) {
                sqlOrder += " lancametoMovimento."+LancamentoMovimento.LANCAMENTO_MOVIMENTO_CATEGORIA+"."+Lancamento.CONTRATO+"."+Contrato.PESSOA+"."+Pessoa.NOME+", lancamentoMovimento."+LancamentoMovimento.LANCAMENTO+", lancamentoMovimento."+inPropriedadeData+", lancamentoMovimento."+IDAO.PROPERTY_ID_NAME;
            }
            
            /* Executando serviço da query */
            String sql = sqlSelect.concat(sqlWhere).concat(sqlOrder);
            Query query = serviceData.getCurrentSession().createQuery(sql);
            List<QueryLancamentoMovimento> beanList = query.list();
            
            log.debug("::Fim da execução do serviço");
            serviceData.getOutputData().add(beanList);

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
    
    public String getServiceName() {
        return SERVICE_NAME;
    }
}
