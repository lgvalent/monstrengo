package br.com.orionsoft.financeiro.documento.pagamento;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.com.orionsoft.basic.entities.Contrato;
import br.com.orionsoft.financeiro.gerenciador.entities.Transacao;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IProperty;


/**
 * <p>Gerenciador de Documento Pagamento padr�o para documentos de 
 * pagamento que n�o possuam controle de impress�o e 
 * numera��o espec�fica
 *
 * @author Lucio 20071219
 * 
 * @spring.bean id="GerenciadorDocumentoPagamento" init-method="registrarGerenciador"
 * @spring.property name="provedorDocumentoPagamento" ref="ProvedorDocumentoPagamento"
 * @spring.property name="preenchimentoManual" value="false"
 */
public class GerenciadorDocumentoPagamento extends GerenciadorDocumentoPagamentoBasic
{

    public static final String GERENCIADOR_NOME = "GerenciadorDocumentoPagamento";

	public String getNome() {
		return GERENCIADOR_NOME;
	}

	public IEntity<? extends DocumentoPagamento> criarDocumento(
			IEntity<Contrato> contrato,
			IEntity<? extends DocumentoPagamentoCategoria> documentoPagamentoCategoria,
			Calendar dataDocumento, Calendar dataVencimento,
			BigDecimal valorDocumento, Transacao transacao,
			ServiceData serviceDataOwner) throws BusinessException {
		return super.criarDocumento(DocumentoPagamento.class, contrato, documentoPagamentoCategoria, dataDocumento, dataVencimento, valorDocumento, transacao, serviceDataOwner);
	}
	
	/**
	 * Um documento gen�rico exibir� os campos:
	 * - n�mero do documento
	 * - data de vencimento do documento
	 */
	public List<IProperty> retrievePropriedadesPreenchimentoManual(IEntity<? extends DocumentoPagamento> documento) throws BusinessException {
		List<IProperty> result = new ArrayList<IProperty>();

		/* Se for um documento de recebimento exibe a lista b�sica cria uma lista com os campos
		 * necess�rios */
		result.add(documento.getProperty(DocumentoPagamento.NUMERO_DOCUMENTO));
		result.add(documento.getProperty(DocumentoPagamento.DATA_VENCIMENTO));

		return result;
	}
	
 }
