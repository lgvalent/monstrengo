package br.com.orionsoft.financeiro.gerenciador.entities;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import br.com.orionsoft.basic.entities.ContratoCategoria;

/**
 * @author Lucio 20060504
 * @version 20060706
 * @hibernate.joined-subclass table="financeiro_contrato_categoria"
 * @hibernate.joined-subclass-key foreign-key="PRIMARY"
 */
@Entity
@DiscriminatorValue("FINC")
public class ContratoFinanceiroCategoria extends ContratoCategoria{
	
	/* Ju-05/11/2007 Inclusao da propriedade diaVencimentoFatura, por necessidade
	 * de haver uma pre-definicao de uma data de vencimento para o documento de 
	 * cobranca gerado (boleto) pelos processos de pre-matricular e confirmar matricula
	 * on line. Caso o dia de vencimento não esteja definido no ContratoFinanceiro, 
	 * obtem-se pelo ContratoFinanceiroCategoria
	 */
	public static final String DIA_VENCIMENTO_FATURA = "diaVencimentoFatura";
	
	private Integer diaVencimentoFatura;

	@Column
	public Integer getDiaVencimentoFatura() {
		return diaVencimentoFatura;
	}

	public void setDiaVencimentoFatura(Integer diaVencimentoFatura) {
		this.diaVencimentoFatura = diaVencimentoFatura;
	}

}
