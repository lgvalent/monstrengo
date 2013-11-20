package br.com.orionsoft.financeiro.gerenciador.entities.agendamento;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.ForeignKey;

import br.com.orionsoft.basic.entities.commons.Frequencia;
import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;

@Entity
@DiscriminatorValue("AGE")
public class Agendamento extends Lancamento {
	/*
	 * Constantes com o nomes das propriedades da classe para serem usadas no
	 * código e evitar erro de digitação.
	 */
	public static final String QUANTIDADE_RESTANTE = "quantidadeRestante";
	public static final String FREQUENCIA = "frequencia";
	public static final String LANCAMENTO = "ultimoLancamento";
	public static final String AGENDAMENTO_ITEM = "agendamentoItem";

	private Integer quantidadeRestante;
	private Frequencia frequencia;
	private Lancamento ultimoLancamento;
	private AgendamentoItem agendamentoItem;

	@Column
	public Integer getQuantidadeRestante() {
		return quantidadeRestante;
	}

	public void setQuantidadeRestante(Integer quantidadeRestante) {
		this.quantidadeRestante = quantidadeRestante;
	}

	@Enumerated(EnumType.STRING)
	@Column(length=Frequencia.COLUMN_DISCRIMINATOR_LENGTH)
	public Frequencia getFrequencia() {
		return frequencia;
	}

	public void setFrequencia(Frequencia frequencia) {
		this.frequencia = frequencia;
	}

	@ManyToOne @JoinColumn(name = "ultimoLancamento") @ForeignKey(name = "ultimoLancamento")
	public Lancamento getUltimoLancamento() {
		return ultimoLancamento;
	}

	public void setUltimoLancamento(Lancamento ultimoLancamento) {
		this.ultimoLancamento = ultimoLancamento;
	}

	@ManyToOne @JoinColumn(name = "agendamentoItem") @ForeignKey(name = "agendamentoItem")
	public AgendamentoItem getAgendamentoItem() {
		return agendamentoItem;
	}

	public void setAgendamentoItem(AgendamentoItem agendamentoItem) {
		this.agendamentoItem = agendamentoItem;
	}
}
