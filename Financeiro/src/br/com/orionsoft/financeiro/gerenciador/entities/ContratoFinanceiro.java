/*
 * Created on 25/04/2006 by antonio
 */
package br.com.orionsoft.financeiro.gerenciador.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import br.com.orionsoft.basic.entities.Contrato;

/**
 *
 * @author antonio
 * @version 25/04/2006
 * @hibernate.joined-subclass table="financeiro_contrato"
 * @hibernate.joined-subclass-key foreign-key="PRIMARY"
 */
@Entity
@DiscriminatorValue("FIN")
public class ContratoFinanceiro extends Contrato {
	
	public static final String DESCONTOS_ACRESCIMOS = "descontosAcrescimos";
	public static final String CONTRATO_FINANCEIRO_CATEGORIA = "contratoFinanceiroCategoria";
	public static final String DIA_VENCIMENTO_FATURA = "diaVencimentoFatura";
	
	/* 07/09/2007 - Lucio
	 * Representa os v�rios tipos de descontos ou de acr�scimos (item de custo)
	 * que um contrato pode ter.
	 * Por exemplo, o ContratoAcademico descende de ContratoFinanceiro
	 * pois um contrato acad�mico pode ter descontos ou acr�scimos no item de custo
	 * mensalidade*/
	private List<ContratoFinanceiroDescontoAcrescimo> descontosAcrescimos = new ArrayList<ContratoFinanceiroDescontoAcrescimo>(0);
	
	/* 05/11/2007 - Ju
	 * Inclusao da propriedade diaVencimentoFatura, por necessidade
	 * de haver uma pre-definicao de uma data de vencimento para o documento de 
	 * cobranca gerado (boleto) pelos processos de pre-matricular e confirmar matricula
	 * on line. Caso o dia de vencimento n�o esteja definido no ContratoFinanceiro, 
	 * obtem-se pelo ContratoFinanceiroCategoria
	 */
	private Integer diaVencimentoFatura;

	/**
	 * Uma vez que ContratoCategoria e Contrato s�o abstratas e cada m�dulo
	 * implementa uma especializa��o destas classes, o hibernate deve ser
	 * informado qual � o tipo da classe implementa em categoria, assim, o
	 * xDoclet deve ser definido no especializa��o do contrato, e o m�todo
	 * get/setCategoria dever� ser sobreescrito por uma vers�o da pr�pria
	 * implementa��o do contrato que indicar� qual especializa��o de categoria
	 * utilizar
	 *
	 * @hibernate.many-to-one foreign-key="categoria"
	 */
    @ManyToOne
	@JoinColumn(name=CONTRATO_FINANCEIRO_CATEGORIA)
	@ForeignKey(name=CONTRATO_FINANCEIRO_CATEGORIA)
	public ContratoFinanceiroCategoria getContratoFinanceiroCategoria() {return (ContratoFinanceiroCategoria) categoria;}
	public void setContratoFinanceiroCategoria(ContratoFinanceiroCategoria categoria) {this.categoria = categoria;}

    @OneToMany
	@JoinColumn(name=ContratoFinanceiroDescontoAcrescimo.CONTRATO_FINANCEIRO)
	@ForeignKey(name=ContratoFinanceiroDescontoAcrescimo.CONTRATO_FINANCEIRO)
	@LazyCollection(LazyCollectionOption.FALSE)
	public List<ContratoFinanceiroDescontoAcrescimo> getDescontosAcrescimos() {return descontosAcrescimos;}
	public void setDescontosAcrescimos(List<ContratoFinanceiroDescontoAcrescimo> descontosAcrescimos) {this.descontosAcrescimos = descontosAcrescimos;}

	@Column
	public Integer getDiaVencimentoFatura() {return diaVencimentoFatura;}
	public void setDiaVencimentoFatura(Integer diaVencimentoFatura) {this.diaVencimentoFatura = diaVencimentoFatura;}
}
