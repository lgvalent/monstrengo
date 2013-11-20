package br.com.orionsoft.financeiro.gerenciador.entities;

import java.math.BigDecimal;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.dvo.DvoBasic;
import br.com.orionsoft.monstrengo.crud.entity.dvo.DvoException;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.security.entities.UserSession;
/**
 * @author Lucio 20130114
 */
public class LancamentoItemDvo extends DvoBasic<LancamentoItem> {
	
	/***
	 * Metodo que retorna a classe da entidade. 
	 */
	public Class<LancamentoItem> getEntityType() {
		return LancamentoItem.class;
	}
	
	public void afterCreate(IEntity<LancamentoItem> entity, UserSession userSession, ServiceData serviceData) throws DvoException, BusinessException {
	}
	
	/** 
	 * Cascateia as alterações de valores nas dependências.
	 * Atualiza o saldo e peso
	 */
	public void afterUpdate(IEntity<LancamentoItem> entity, UserSession userSession, ServiceData serviceData) throws DvoException, BusinessException {
        DvoException dvoExceptions = new DvoException(new MessageList());

        try {
        	/* Verifica se houve alteração do valor */
			if(entity.getProperty(LancamentoItem.VALOR).getValue().isModified()){
				cascateiaValor(entity, userSession, serviceData);
			}
		} catch (DvoException e) {
			dvoExceptions.getErrorList().addAll(e.getErrorList());
		} 
	}

	public void afterDelete(IEntity<LancamentoItem> entity, UserSession userSession, ServiceData serviceDataOwner) throws DvoException, BusinessException  {
		// TODO Auto-generated method stub
	}

	public void beforeCreate(IEntity<LancamentoItem> entity, UserSession userSession, ServiceData serviceDataOwner) throws DvoException, BusinessException  {
		// TODO Auto-generated method stub
	}

	public void beforeDelete(IEntity<LancamentoItem> entity, UserSession userSession, ServiceData serviceDataOwner) throws DvoException, BusinessException  {
	}

	public void beforeUpdate(IEntity<LancamentoItem> entity, UserSession userSession, ServiceData serviceDataOwner) throws DvoException, BusinessException  {
	}
	
	/**
	 * Método que verifica e cascateia as alterações de valores
	 * para o os lançamentos itens, documento de cobrança e documento de pagamento previsto.
	 * 
	 * Quando eu altero o valor de um Item, o valor total do lançamento deverá ser recalculado e os pesos
	 * ajustados. Contudo, ao alterar o valor o lançamento, seu movimento e documentos deverão ser atualizado CONSEQUENTEMENTE.
	 */
	public void cascateiaValor(IEntity<LancamentoItem> lancamentoItem,  UserSession userSession, ServiceData serviceDataOwner) throws BusinessException{
		try {
			LancamentoItem oLancamentoItem = lancamentoItem.getObject();
			
			/* Se houve, verifica qual era o valor anterior e recalcula o valor de todos os itens
			 * Gerando uma alteração em cascata do valor do Lancamento */
			BigDecimal newTotalValue =  BigDecimal.ZERO;
			for(LancamentoItem oItem: oLancamentoItem.getLancamento().getLancamentoItens()){
				newTotalValue =  newTotalValue.add(oItem.getValor());
			}

			/* Verifica a diferença e coloca no valor do lançamento */
			oLancamentoItem.getLancamento().setValor(newTotalValue);
			/* Atualiza os pesos dos demais items */
			LancamentoDvo.calculaPesos(oLancamentoItem.getLancamento().getLancamentoItens(), newTotalValue);
			
			LancamentoDvo.cascateiaValor(lancamentoItem.getProperty(LancamentoItem.LANCAMENTO).getValue().<Lancamento>getAsEntity(), userSession, serviceDataOwner);
			
			UtilsCrud.objectUpdate(lancamentoItem.getEntityManager().getServiceManager(), oLancamentoItem.getLancamento(), serviceDataOwner);
				
		} catch (EntityException e) {
			throw new BusinessException(e.getErrorList());
		}
	}
}
