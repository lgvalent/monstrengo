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
	 * Representa os vários tipos de descontos ou de acréscimos (item de custo)
	 * que um contrato pode ter.
	 * Por exemplo, o ContratoAcademico descende de ContratoFinanceiro
	 * pois um contrato acadêmico pode ter descontos ou acréscimos no item de custo
	 * mensalidade*/
	private List<ContratoFinanceiroDescontoAcrescimo> descontosAcrescimos = new ArrayList<ContratoFinanceiroDescontoAcrescimo>(0);
	
	/* 05/11/2007 - Ju
	 * Inclusao da propriedade diaVencimentoFatura, por necessidade
	 * de haver uma pre-definicao de uma data de vencimento para o documento de 
	 * cobranca gerado (boleto) pelos processos de pre-matricular e confirmar matricula
	 * on line. Caso o dia de vencimento não esteja definido no ContratoFinanceiro, 
	 * obtem-se pelo ContratoFinanceiroCategoria
	 */
	private Integer diaVencimentoFatura;

	/**
	 * Uma vez que ContratoCategoria e Contrato são abstratas e cada módulo
	 * implementa uma especialização destas classes, o hibernate deve ser
	 * informado qual é o tipo da classe implementa em categoria, assim, o
	 * xDoclet deve ser definido no especialização do contrato, e o método
	 * get/setCategoria deverá ser sobreescrito por uma versão da própria
	 * implementação do contrato que indicará qual especialização de categoria
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
