package br.com.orionsoft.financeiro.documento.pagamento.dinheiro;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import br.com.orionsoft.financeiro.documento.pagamento.DocumentoPagamento;

/**
 *Classe de persistencia que descreve o documento tipo dinheiro.
 *@hibernate.joined-subclass table="financeiro_dinheiro" 
 *@hibernate.joined-subclass-key foreing-key="PRIMARY"
 */
@Entity
@DiscriminatorValue("DIN")
public class DocumentoDinheiro extends DocumentoPagamento {

	public String toString(){
		return "Dinheiro";
	}
}
