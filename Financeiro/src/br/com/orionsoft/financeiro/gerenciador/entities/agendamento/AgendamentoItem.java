package br.com.orionsoft.financeiro.gerenciador.entities.agendamento;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoItem;

@Entity
@DiscriminatorValue("AGE")
public class AgendamentoItem extends LancamentoItem {

}
