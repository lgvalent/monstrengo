package br.com.orionsoft.financeiro.documento.pagamento.dinheiro;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import br.com.orionsoft.financeiro.documento.pagamento.DocumentoPagamentoBean;
import br.com.orionsoft.financeiro.documento.pagamento.DocumentoPagamentoException;
import br.com.orionsoft.financeiro.documento.pagamento.GerenciadorDocumentoPagamentoBasic;
import br.com.orionsoft.financeiro.gerenciador.entities.Transacao;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;


/**
 * <p>Esta classe implementa o gerenciador de documento do tipo dinheiro.<p>
 *
 * @author Lucio
 * 
 * @spring.bean id="GerenciadorDocumentoDinheiro" init-method="registrarGerenciador"
 * @spring.property name="provedorDocumentoPagamento" ref="ProvedorDocumentoPagamento"
 * @spring.property name="preenchimentoManual" value="false"
 */
public class GerenciadorDocumentoDinheiro extends GerenciadorDocumentoPagamentoBasic
{

    public static final String GERENCIADOR_NOME = "GerenciadorDocumentoDinheiro";

	public String getNome() {
		return GERENCIADOR_NOME;
	}

	public IEntity criarDocumento(IEntity contrato, IEntity documentoPagamentoCategoria, Calendar dataDocumento, Calendar dataVencimento, BigDecimal valorDocumento, Transacao transacao, ServiceData serviceDataOwner) throws BusinessException {
		return super.criarDocumento(DocumentoDinheiro.class, contrato, documentoPagamentoCategoria, dataDocumento, dataVencimento, valorDocumento, transacao, serviceDataOwner);
	}

	public void imprimirDocumento(IEntity documento, OutputStream outputStream, String instrucoesAdicionais, ServiceData serviceDataOwner) throws DocumentoPagamentoException {
		
	}

	public void imprimirDocumentos(List<DocumentoPagamentoBean> documentos, OutputStream outputStream, ServiceData serviceDataOwner) throws DocumentoPagamentoException {
		
	}

 }
