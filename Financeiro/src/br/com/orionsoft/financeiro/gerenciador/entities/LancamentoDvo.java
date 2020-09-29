package br.com.orionsoft.financeiro.gerenciador.entities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;

import org.apache.commons.lang.StringUtils;

import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobranca;
import br.com.orionsoft.financeiro.documento.cobranca.services.AlterarVencimentoDocumentosCobrancaService;
import br.com.orionsoft.financeiro.documento.pagamento.DocumentoPagamento;
import br.com.orionsoft.financeiro.utils.UtilsConta;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.util.DecimalUtils;
import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.EntityList;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.dvo.DvoBasic;
import br.com.orionsoft.monstrengo.crud.entity.dvo.DvoException;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.security.entities.UserSession;
/**
 * Classe que valida entidades do tipo Fisica. <br>
 * 
 *  
 * @author Sergio
 * @spring.bean id="LancamentoDvo" init-method="registerDvo"
 * @spring.property name="dvoManager" ref="DvoManager"
 */
public class LancamentoDvo extends DvoBasic<Lancamento> {

	/***
	 * Metodo que retorna a classe da entidade. 
	 */
	public Class<Lancamento> getEntityType() {
		return Lancamento.class;
	}

	public void afterCreate(IEntity<Lancamento> entity, UserSession userSession, ServiceData serviceData) throws DvoException, BusinessException {
		/* Lucio 20100615: Verifica se o Lancamento pertence a uma conta que pode ser modificado pelo operador atual */
		if(!UtilsConta.verificarDireitoAcesso(entity.getObject().getContaPrevista(), userSession.getUser().getObject()))
			throw new DvoException(MessageList.create(LancamentoDvo.class, "SEM_DIREITOS_ACESSO_CONTA", entity.getPropertyValue(Lancamento.CONTA_PREVISTA)));
	}

	/** 
	 * Cascateia as altera��es de valores nas depend�ncias.
	 * Atualiza o saldo em aberto
	 */
	public void afterUpdate(IEntity<Lancamento> entity, UserSession userSession, ServiceData serviceData) throws DvoException, BusinessException {
		DvoException dvoExceptions = new DvoException(new MessageList());

		/* Verifica se houve altera��o na descri��o para alterar a do documento de cobran�a */
		if(entity.getProperty(Lancamento.DESCRICAO).getValue().isModified()){
			/* Verifica se o documento de cobran�a possui somente um lan�amento vinculado
			 * e se a descri��o � IGUAL e n�o personalizada */
			Lancamento oLancamento = entity.getObject();
			if((oLancamento.getDocumentoCobranca() != null ) &&
					(oLancamento.getDocumentoCobranca().getLancamentos().size()==1)&&
					(StringUtils.equals(oLancamento.getDescricao(), oLancamento.getDocumentoCobranca().getInstrucoes3()))){
				oLancamento.getDocumentoCobranca().setInstrucoes3(oLancamento.getDescricao());
				UtilsCrud.objectUpdate(entity.getEntityManager().getServiceManager(), oLancamento.getDocumentoCobranca(), serviceData);
			}
		}

		try {
			/* Verifica se houve altera��o do valor */
			if(entity.getProperty(Lancamento.VALOR).getValue().isModified()){
				cascateiaValor(entity, userSession, serviceData);
			}
		} catch (DvoException e) {
			dvoExceptions.getErrorList().addAll(e.getErrorList());
		} 

		try {
			cascateiaVencimento(entity, userSession, serviceData);
		} catch (DvoException e) {
			dvoExceptions.getErrorList().addAll(e.getErrorList());
		} 

		try {
			cascateiaContrato(entity, serviceData);
		} catch (DvoException e) {
			dvoExceptions.getErrorList().addAll(e.getErrorList());
		} 
	}
	public void afterDelete(IEntity<Lancamento> entity, UserSession userSession, ServiceData serviceDataOwner) throws DvoException, BusinessException  {
		// TODO Auto-generated method stub
	}

	public void beforeCreate(IEntity<Lancamento> entity, UserSession userSession, ServiceData serviceDataOwner) throws DvoException, BusinessException  {
		// TODO Auto-generated method stub
	}

	public void beforeDelete(IEntity<Lancamento> entity, UserSession userSession, ServiceData serviceDataOwner) throws DvoException, BusinessException  {
		/* Lucio 20100615: Verifica se o Lancamento pertence a uma conta que pode ser modificado pelo operador atual */
		if(!UtilsConta.verificarDireitoAcesso(entity.getObject().getContaPrevista(), userSession.getUser().getObject()))
			throw new DvoException(MessageList.create(LancamentoDvo.class, "SEM_DIREITOS_ACESSO_CONTA", entity.getPropertyValue(Lancamento.CONTA_PREVISTA)));
	}

	public void beforeUpdate(IEntity<Lancamento> entity, UserSession userSession, ServiceData serviceDataOwner) throws DvoException, BusinessException  {
		/* Lucio 20100615: Verifica se o Lancamento pertence a uma conta que pode ser modificado pelo operador atual */
		if(!UtilsConta.verificarDireitoAcesso(entity.getObject().getContaPrevista(), userSession.getUser().getObject()))
			throw new DvoException(MessageList.create(LancamentoDvo.class, "SEM_DIREITOS_ACESSO_CONTA", entity.getPropertyValue(Lancamento.CONTA_PREVISTA)));
	}

	/**
	 * M�todo que verifica e cascateia as altera��es de valores
	 * para o os lan�amentos itens, documento de cobran�a e documento de pagamento previsto.
	 * Utilizado pelo DVO do LancamentoItemDVO
	 */
	public static void cascateiaValor(IEntity<Lancamento> lancamento,  UserSession userSession, ServiceData serviceDataOwner) throws BusinessException{
		try {
			Lancamento oLancamento = lancamento.getObject();

			/* Movimentos de quita��o */
			if(oLancamento.getLancamentoMovimentos().size() > 1){
				throw new DvoException(MessageList.create(LancamentoDvo.class, "MULTIPLOS_MOVIMENTOS_ERRO"));
			}else
				/* Verifica se possui SOMENTE um movimento para altera-lo */
				if(oLancamento.getLancamentoMovimentos().size() == 1){
					IEntity<LancamentoMovimento> movimento =  lancamento.getProperty(Lancamento.LANCAMENTO_MOVIMENTOS).getValue().<LancamentoMovimento>getAsEntityCollection().getFirst();

					LancamentoMovimento oLancamentoMovimento = movimento.getObject();
					oLancamentoMovimento.setValor(oLancamento.getValor());
					oLancamentoMovimento.setValorTotal(oLancamento.getValor());

					/* Lucio 20121217: Cascateia o afterUpdate para o Lan�amento atualizar valorTotal caso haja descontos e acr�scimos definidos */
					lancamento.getEntityManager().getDvoManager().getDvoByEntity(movimento).afterUpdate(movimento, userSession, serviceDataOwner);

					UtilsCrud.objectUpdate(lancamento.getEntityManager().getServiceManager(), oLancamentoMovimento, serviceDataOwner);

					/* Atualiza o saldo igual ao valor, para decrementar ou nao */
					oLancamento.setSaldo(BigDecimal.ZERO);
				}else{
					/* Atualiza o saldo igual ao valor*/
					oLancamento.setSaldo(oLancamento.getValor());
				}

			/* Itens - Atualiza os valores de acordo com o peso de cada item */
			for(LancamentoItem oItem: oLancamento.getLancamentoItens()){
				oItem.setValor(oLancamento.getValor().multiply(oItem.getPeso()));
				UtilsCrud.objectUpdate(lancamento.getEntityManager().getDvoManager().getEntityManager().getServiceManager(), oItem, serviceDataOwner);
			}

			/* Valor do Documento de pagamento */
			DocumentoPagamento oDocumentoPagamento = oLancamento.getDocumentoPagamento();
			if(oDocumentoPagamento != null){
				if(oDocumentoPagamento.getLancamentos().size() <= 1){
					oDocumentoPagamento.setValor(oLancamento.getValor());
					UtilsCrud.objectUpdate(lancamento.getEntityManager().getServiceManager(), oDocumentoPagamento, serviceDataOwner);
				}
			}

			/* Valor do Documento de cobran�a */
			DocumentoCobranca oDocumentoCobranca = oLancamento.getDocumentoCobranca();
			if(oDocumentoCobranca != null){
				if(oDocumentoCobranca.getLancamentos().size() <= 1){
					oDocumentoCobranca.setValor(oLancamento.getValor());
					UtilsCrud.objectUpdate(lancamento.getEntityManager().getDvoManager().getEntityManager().getServiceManager(), oDocumentoCobranca, serviceDataOwner);
				}
			}

		} catch (EntityException e) {
			throw new BusinessException(e.getErrorList());
		}
	}

	/**
	 * M�todo que verifica e cascateia as altera��es de contrato nos documentos
	 * de cobran�a e de pagamento, e tambem nos documentos dos movimentos.
	 */
	public void cascateiaContrato(IEntity<Lancamento> lancamento, ServiceData serviceDataOwner) throws BusinessException{
		try {
			/* Verifica se houve altera��o do contrato */
			if(lancamento.getProperty(Lancamento.CONTRATO).getValue().isModified()){
				Lancamento oLancamento = lancamento.getObject();

				/* Items */
				for(LancamentoItem oLancamentoItem: oLancamento.getLancamentoItens()){
					/* TODO Tem que verificar o conflito da Ades�oGeradora com o novo contrato definido
					 * pois uma nova ades�o geradora dever� ser definida para o item, pois ela pertence 
					 * a outro contrato. Sen�o, ades�es geradoras estar�o ligadas a items que outros contratos
					 * que n�o sejam o seu*/
					oLancamentoItem.setContrato(oLancamento.getContrato());
					UtilsCrud.objectUpdate(this.getDvoManager().getEntityManager().getServiceManager(), oLancamentoItem, serviceDataOwner);
				}

				/* Documento dos Movimentos de quita��o */
				for(LancamentoMovimento oMovimento: oLancamento.getLancamentoMovimentos()){
					DocumentoPagamento oDocumentoPagamento = oMovimento.getDocumentoPagamento();
					if(oDocumentoPagamento != null){
						oDocumentoPagamento.setContrato(oLancamento.getContrato());
						UtilsCrud.objectUpdate(this.getDvoManager().getEntityManager().getServiceManager(), oDocumentoPagamento, serviceDataOwner);
					}
				}

				/* Documento de pagamento */
				DocumentoPagamento oDocumentoPagamento = oLancamento.getDocumentoPagamento();
				if(oDocumentoPagamento != null){
					oDocumentoPagamento.setContrato(oLancamento.getContrato());
					UtilsCrud.objectUpdate(this.getDvoManager().getEntityManager().getServiceManager(), oDocumentoPagamento, serviceDataOwner);
				}

				/* Documento de cobran�a */
				DocumentoCobranca oDocumentoCobranca = oLancamento.getDocumentoCobranca();
				if(oDocumentoCobranca != null){
					oDocumentoCobranca.setContrato(oLancamento.getContrato());
					UtilsCrud.objectUpdate(this.getDvoManager().getEntityManager().getServiceManager(), oDocumentoCobranca, serviceDataOwner);
				}
			}
		} catch (EntityException e) {
			throw new BusinessException(e.getErrorList());
		}
	}

	/**
	 * M�todo que verifica e cascateia as altera��es de contrato nos documentos
	 * de cobran�a e de pagamento, e tambem nos documentos dos movimentos.
	 */
	public void cascateiaVencimento(IEntity<Lancamento> lancamento, UserSession userSession, ServiceData serviceDataOwner) throws BusinessException{
		try {
			/* Verifica se houve altera��o na data do vencimento */
			if(lancamento.getProperty(Lancamento.DATA_VENCIMENTO).getValue().isModified()){
				Lancamento oLancamento = lancamento.getObject();

				/* Documento de pagamento */
				DocumentoPagamento oDocumentoPagamento = oLancamento.getDocumentoPagamento();
				if(oDocumentoPagamento != null){
					oDocumentoPagamento.setDataVencimento(oLancamento.getDataVencimento());
					UtilsCrud.objectUpdate(this.getDvoManager().getEntityManager().getServiceManager(), oDocumentoPagamento, serviceDataOwner);
				}

				/* Documento de cobran�a */
				DocumentoCobranca oDocumentoCobranca = oLancamento.getDocumentoCobranca();
				if(oDocumentoCobranca != null){
					
					EntityList<Object> docs = new EntityList<>(new ArrayList<Object>(), this.getDvoManager().getEntityManager().getEntityMetadata(DocumentoCobranca.class), this.getDvoManager().getEntityManager());
					docs.add(lancamento.getProperty(Lancamento.DOCUMENTO_COBRANCA).getValue().getAsEntity());
					
					ServiceData sds = new ServiceData(AlterarVencimentoDocumentosCobrancaService.SERVICE_NAME, null);
					sds.getArgumentList().setProperty(AlterarVencimentoDocumentosCobrancaService.IN_DATA, oLancamento.getDataVencimento());
					sds.getArgumentList().setProperty(AlterarVencimentoDocumentosCobrancaService.IN_ADENDO_INSTRUCOES_3, "");
					sds.getArgumentList().setProperty(AlterarVencimentoDocumentosCobrancaService.IN_DOCUMENTOS, docs);
					sds.getArgumentList().setProperty(AlterarVencimentoDocumentosCobrancaService.IN_USER_SESSION_OPT, userSession);
					this.getDvoManager().getEntityManager().getServiceManager().execute(sds);
				}
			}
		} catch (EntityException e) {
			throw new BusinessException(e.getErrorList());
		}
	}

	/**
	 * @param inLancamentoItemList
	 * @param valorTotal
	 * @throws NoSuchMethodException
	 */
	public static void calculaPesos(List<LancamentoItem> inLancamentoItemList, BigDecimal valorTotal){
		/* Calculando o peso dos itens e colocando o item no lancamento */
		for (LancamentoItem lancamentoItem : inLancamentoItemList) {
			/* Extrair o valor digitado na propriedade PESO */
			BigDecimal valorItem = (BigDecimal) lancamentoItem.getValor();

			/* Define o peso */
			//				lancamentoItem.setPropertyValue(LancamentoItem.PESO, valorItem.divide(valorTotal));
			Double divisao = valorItem.doubleValue() / valorTotal.doubleValue();

			/* O valor do grupo e item pode ser Zero, assim, se a divis�o para saber a propor��o n�o retornou um n�mero ent�o colocar
			 * como peso o valor ZERO */
			if(divisao.isNaN())
				lancamentoItem.setPeso(BigDecimal.ONE);
			else
				try {
					/* Efetua o c�lculo do peso de acordo com a escala definida na anota��o da entidade LancamentoItem.getPeso() */
					lancamentoItem.setPeso(DecimalUtils.getBigDecimal(divisao, LancamentoItem.class.getMethod("getPeso", new Class<?>[]{}).getAnnotation(Column.class).scale()));
				} catch (Exception  e) {
					throw new RuntimeException(e);
				}
		}
	}
}
