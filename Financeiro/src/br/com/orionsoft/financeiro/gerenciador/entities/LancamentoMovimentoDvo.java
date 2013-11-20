package br.com.orionsoft.financeiro.gerenciador.entities;

import java.math.BigDecimal;

import br.com.orionsoft.financeiro.utils.UtilsConta;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.dvo.DvoBasic;
import br.com.orionsoft.monstrengo.crud.entity.dvo.DvoException;
import br.com.orionsoft.monstrengo.security.entities.UserSession;
/**
 * Classe que valida entidades do tipo Fisica. <br>
 * 
 *  
 * @author Sergio
 * @spring.bean id="LancamentoMovimentoDvo" init-method="registerDvo"
 * @spring.property name="dvoManager" ref="DvoManager"
 */
public class LancamentoMovimentoDvo extends DvoBasic<LancamentoMovimento> {
	
	/***
	 * Metodo que retorna a classe da entidade. 
	 */
	public Class<LancamentoMovimento> getEntityType() {
		return LancamentoMovimento.class;
	}
	
	public void afterCreate(IEntity<LancamentoMovimento> entity, UserSession userSession, ServiceData serviceData) throws DvoException, BusinessException {
		/* Lucio 20100615: Verifica se o Movimento pertence a uma conta que pode ser modificado pelo operador atual */
		if(!UtilsConta.verificarDireitoAcesso(entity.getObject().getConta(), userSession.getUser().getObject()))
			throw new DvoException(MessageList.create(LancamentoMovimentoDvo.class, "SEM_DIREITOS_ACESSO_CONTA", entity.getPropertyValue(LancamentoMovimento.CONTA)));
	}
	
	/** 
	 * Atualiza o valor total
	 */
	public void afterUpdate(IEntity<LancamentoMovimento> entity, UserSession userSession, ServiceData serviceData) throws DvoException, BusinessException {
		/* Lucio 20110425: Verifica se o Movimento pertence a uma conta que pode ser modificado pelo operador atual */
		if(!UtilsConta.verificarDireitoAcesso(entity.getObject().getConta(), userSession.getUser().getObject()))
			throw new DvoException(MessageList.create(LancamentoMovimentoDvo.class, "SEM_DIREITOS_ACESSO_CONTA", entity.getPropertyValue(LancamentoMovimento.CONTA)));

		atualizaValorTotal(entity, serviceData);
	}
	public void afterDelete(IEntity<LancamentoMovimento> entity, UserSession userSession, ServiceData serviceDataOwner) throws DvoException, BusinessException  {
		// TODO Auto-generated method stub
	}

	public void beforeCreate(IEntity<LancamentoMovimento> entity, UserSession userSession, ServiceData serviceDataOwner) throws DvoException, BusinessException  {
		// TODO Auto-generated method stub
	}

	public void beforeDelete(IEntity<LancamentoMovimento> entity, UserSession userSession, ServiceData serviceDataOwner) throws DvoException, BusinessException  {
		/* Lucio 20100615: Verifica se o Movimento pertence a uma conta que pode ser modificado pelo operador atual */
		if(!UtilsConta.verificarDireitoAcesso(entity.getObject().getConta(), userSession.getUser().getObject()))
			throw new DvoException(MessageList.create(LancamentoMovimentoDvo.class, "SEM_DIREITOS_ACESSO_CONTA", entity.getPropertyValue(LancamentoMovimento.CONTA)));

		/* Verifica se o movimento é de uma transferência para forçar
		 * a execução do processo específico */
		LancamentoMovimento oMovimento = entity.getObject();
		if(oMovimento.getLancamentoMovimentoCategoria() == LancamentoMovimentoCategoria.TRANSFERIDO){
	    	throw new DvoException(MessageList.create(LancamentoMovimentoDvo.class, "EXCLUSAO_TRANSFERENCIA_ERRO"));
		}
	}

	public void beforeUpdate(IEntity<LancamentoMovimento> entity, UserSession userSession, ServiceData serviceDataOwner) throws DvoException, BusinessException  {
		/* Lucio 20100615: Verifica se o Movimento pertence a uma conta que pode ser modificado pelo operador atual */
		if(!UtilsConta.verificarDireitoAcesso(entity.getObject().getConta(), userSession.getUser().getObject()))
			throw new DvoException(MessageList.create(LancamentoMovimentoDvo.class, "SEM_DIREITOS_ACESSO_CONTA", entity.getPropertyValue(LancamentoMovimento.CONTA)));
	}

	/**
	 * Método que verifica os movimentos e atualiza o saldo em aberto do
	 * atual lançamento.
	 */
	public void atualizaValorTotal(IEntity<LancamentoMovimento> movimento, ServiceData serviceDataOwner){
			/* Verifica se houve alteração do valor para atualizar saldo em aberto */
			LancamentoMovimento oMovimento = movimento.getObject();
				
			BigDecimal valorTotal = oMovimento.getValor();
									
			/* Atualiza o saldo igual ao valor, para decrementar ou nao */
			if (oMovimento.getJuros() != null){
				valorTotal = valorTotal.add(oMovimento.getJuros());
			}
			if (oMovimento.getMulta() != null){
				valorTotal = valorTotal.add(oMovimento.getMulta());
			}
			if (oMovimento.getAcrescimo() != null){
				valorTotal = valorTotal.add(oMovimento.getAcrescimo());
			}
			if (oMovimento.getDesconto() != null){
				valorTotal = valorTotal.subtract(oMovimento.getDesconto());
			}
			
			oMovimento.setValorTotal(valorTotal);
	}
	
}
