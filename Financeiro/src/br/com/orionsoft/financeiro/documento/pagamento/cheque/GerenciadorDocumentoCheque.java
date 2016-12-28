package br.com.orionsoft.financeiro.documento.pagamento.cheque;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.faces.model.SelectItem;

import org.hibernate.LockMode;

import br.com.orionsoft.basic.entities.Contrato;
import br.com.orionsoft.financeiro.documento.pagamento.DocumentoPagamento;
import br.com.orionsoft.financeiro.documento.pagamento.DocumentoPagamentoBean;
import br.com.orionsoft.financeiro.documento.pagamento.DocumentoPagamentoCategoria;
import br.com.orionsoft.financeiro.documento.pagamento.DocumentoPagamentoException;
import br.com.orionsoft.financeiro.documento.pagamento.GerenciadorDocumentoPagamentoBasic;
import br.com.orionsoft.financeiro.gerenciador.entities.ContaBancaria;
import br.com.orionsoft.financeiro.gerenciador.entities.Transacao;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IProperty;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;


/**
 * <p>Esta classe implementa o gerenciador de documento do tipo cheque.<p>
 *
 * @author Lucio
 * 
 * @spring.bean id="GerenciadorDocumentoCheque" init-method="registrarGerenciador"
 * @spring.property name="provedorDocumentoPagamento" ref="ProvedorDocumentoPagamento"
 * @spring.property name="preenchimentoManual" value="true"
 */
public class GerenciadorDocumentoCheque extends GerenciadorDocumentoPagamentoBasic
{

    public static final String GERENCIADOR_NOME = "GerenciadorDocumentoCheque";

	public static final String LAYOUT_0 = "Nenhum documento impresso";
	public static final String LAYOUT_1 = "Modelo conforme Documento de Pagamento";
//	public static final String LAYOUT_2 = "Impressora de cheque";
	
	public GerenciadorDocumentoCheque() {
		super.setPreenchimentoManual(true);
	}

	public String getNome() {
		return GERENCIADOR_NOME;
	}
	public IEntity<? extends DocumentoPagamento> criarDocumento(IEntity<Contrato> contrato,	IEntity<? extends DocumentoPagamentoCategoria> documentoPagamentoCategoria,	Calendar dataDocumento, Calendar dataVencimento, BigDecimal valorDocumento, Transacao transacao, ServiceData serviceDataOwner) throws BusinessException {
		IEntity<? extends DocumentoPagamento> result = super.criarDocumento(DocumentoCheque.class, contrato, documentoPagamentoCategoria, dataDocumento, dataVencimento, valorDocumento, transacao, serviceDataOwner);
		
		/* Verifica se já possui uma conta bancária definida no convênio da forma de pagamento do documento. 
		 * Se sim, copia os dados BANCO, AGENCIA, CONTA_PADRAO, CONTA_DV para o atual cheque */
		if(!result.getProperty(DocumentoCheque.DOCUMENTO_PAGAMENTO_CATEGORIA).getValue().
			getAsEntity().getProperty(DocumentoPagamentoCategoria.CONVENIO_PAGAMENTO).getValue().
			getAsEntity().getProperty(ConvenioCheque.CONTA_BANCARIA).getValue().isValueNull()){
			
			IEntity<ContaBancaria> contaBancaria = result.getProperty(DocumentoCheque.DOCUMENTO_PAGAMENTO_CATEGORIA).getValue().
								getAsEntity().getProperty(DocumentoPagamentoCategoria.CONVENIO_PAGAMENTO).getValue().
								getAsEntity().getProperty(ConvenioCheque.CONTA_BANCARIA).getValue().getAsEntity();
			
			result.setPropertyValue(DocumentoCheque.BANCO,  contaBancaria.getPropertyValue(ContaBancaria.BANCO));
			result.setPropertyValue(DocumentoCheque.AGENCIA,  contaBancaria.getPropertyValue(ContaBancaria.AGENCIA_CODIGO));
			result.setPropertyValue(DocumentoCheque.CONTA_CORRENTE,  contaBancaria.getPropertyValue(ContaBancaria.CONTA_CODIGO));
			result.setPropertyValue(DocumentoCheque.CONTA_CORRENTE_DIGITO,  contaBancaria.getPropertyValue(ContaBancaria.CONTA_DIGITO));
		}
		
		return result;
	}

	/**
	 * Somente quando o cheque é impresso seu número e definido pelo gerenciado, isto para manter correspondência nas folhas.
	 */
	public void imprimirDocumentos(List<DocumentoPagamentoBean> documentosBean, OutputStream outputStream, int printerIndex, ServiceData serviceDataOwner) throws DocumentoPagamentoException {
		try {
		log.debug("::Iniciando a execução da impressão de Cheque" );
			log.debug("Preparando os argumentos");
			if(!documentosBean.isEmpty()) {
				/* Criando lista de beans */
				List<ChequePrintBean> beans = new ArrayList<ChequePrintBean>(documentosBean.size());
				
				for(DocumentoPagamentoBean documento: documentosBean) {
					if(documento.isChecked()){
						IEntity<DocumentoCheque> cheque = UtilsCrud.retrieve(this.getProvedorDocumentoPagamento().getServiceManager(), DocumentoCheque.class, documento.getId(), serviceDataOwner);
						DocumentoCheque oCheque = cheque.getObject();
						
						/* Obtem o controlado de cheques para gerar o número de forma que números não se repitam.
						 * Para isto, utiliza-se LockMode.UPGRADE */
						ConvenioCheque oConvenioCheque= (ConvenioCheque) serviceDataOwner.getCurrentSession().get(ConvenioCheque.class,
								oCheque.getConvenioPagamento().getId(),
								LockMode.UPGRADE);


						long lProximoNumeroCheque = oConvenioCheque.getProximoNumeroCheque();
						/* Verifica se o cheque é de pagamento e se tem uma número diferente de zero para 
						 * controlar a sequencia do cheque na configuração do seu convênio da forma da pagamento */
						if ((cheque.getPropertyValue(DocumentoPagamento.TRANSACAO) == Transacao.DEBITO) && (lProximoNumeroCheque > 0)) {
                			log.debug("Marcando documento como impresso");
							oCheque.setDataImpressao(Calendar.getInstance());

							/* Usa o proximo documento e grava o documento */
							cheque.getProperty(DocumentoCheque.NUMERO_DOCUMENTO).getValue().setAsString(String.format("%06d",lProximoNumeroCheque));
							UtilsCrud.update(this.getProvedorDocumentoPagamento().getServiceManager(), cheque, serviceDataOwner);
							
							/* Incrementa e grava o proximo documento */
							oConvenioCheque.setProximoNumeroCheque(oConvenioCheque.getProximoNumeroCheque()+1);
							UtilsCrud.objectUpdate(this.getProvedorDocumentoPagamento().getServiceManager(), oConvenioCheque, serviceDataOwner);
						}else {
							/* Somente marca o cheque como impresso e grava */
                			log.debug("Marcando documento como impresso");
							oCheque.setDataImpressao(Calendar.getInstance());
							UtilsCrud.update(this.getProvedorDocumentoPagamento().getServiceManager(), cheque, serviceDataOwner);
						}
						
						log.debug("Criando o ChequePrintBean para os relatórios");
						ChequePrintBean chequeBean = new ChequePrintBean(cheque); 

						log.debug("Adicionando na lista");
						beans.add(chequeBean);
					} // if(documentoBean.isChecked())
				} // for

				log.debug("Obtendo o layout dos cheques");
				IEntity<DocumentoCheque> documentoCheque = UtilsCrud.retrieve(this.getProvedorDocumentoPagamento().getServiceManager(), DocumentoCheque.class, documentosBean.get(0).getId(), serviceDataOwner);
				ChequeModelo chequeModelo = documentoCheque.getProperty(DocumentoCheque.CONVENIO_PAGAMENTO).getValue().getAsEntity().getProperty(ConvenioCheque.CHEQUE_MODELO).getValue().<ChequeModelo>getAsEntity().getObject();
				
				log.debug("Imprimindo a lista de cheques em PDF.");
				ChequeImpressaoJasper.print(beans, chequeModelo, outputStream, printerIndex);
			}else{
				log.debug("Lista de Guias Vazia. Nenhuma guia foi impressa.");
				/* FIXME verificar este erro */
//				throw new BusinessException(MessageList.create(ChequeImpressaoJasper.class, "PROPRIEDADE_NAO_DEFINIDA"));
			}
			
			log.debug("::Fim da execução da impressão do cheque");
			
		} catch (BusinessException e) {
			throw new DocumentoPagamentoException(e.getErrorList());
		} catch (RuntimeException e) {
			throw new DocumentoPagamentoException(MessageList.createSingleInternalError(e));
		}
	}
	
	/**isPreenchimentoManual
	 * O cheque somente exibirá os campos de entrada se for um documento de recebimento, assim,
	 * o operador precisa entrar todas as informações do cheque do cliente
	 */
	public List<IProperty> retrievePropriedadesPreenchimentoManual(IEntity<? extends DocumentoPagamento> documento) throws BusinessException {
		List<IProperty> result = new ArrayList<IProperty>();

		/* Se for um documento de recebimento exibe a lista básica cria uma lista com os campos
		 * necessários */
//		if(documento.getProperty(DocumentoPagamento.RECEBIMENTO).getValue().getAsBoolean()){
		if (documento.getPropertyValue(DocumentoPagamento.TRANSACAO) == Transacao.CREDITO) {
			result.add(documento.getProperty(DocumentoCheque.BANCO));
			result.add(documento.getProperty(DocumentoCheque.AGENCIA));
			result.add(documento.getProperty(DocumentoCheque.CONTA_CORRENTE));
			result.add(documento.getProperty(DocumentoCheque.CONTA_CORRENTE_DIGITO));
			result.add(documento.getProperty(DocumentoCheque.NUMERO_DOCUMENTO));
			result.add(documento.getProperty(DocumentoCheque.DATA_VENCIMENTO));
		}

		return result;
	}
	
	public List<SelectItem> getLayouts(){
		List<SelectItem> result = new ArrayList<SelectItem>(1);
		result.add(new SelectItem(0, LAYOUT_0));
		result.add(new SelectItem(1, LAYOUT_1));
//		result.add(new SelectItem(2, LAYOUT_2));
		return result;
	}

 }
