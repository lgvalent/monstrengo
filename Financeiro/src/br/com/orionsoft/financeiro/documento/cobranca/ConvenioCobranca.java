package br.com.orionsoft.financeiro.documento.cobranca;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.ForeignKey;

import br.com.orionsoft.basic.entities.Contrato;
import br.com.orionsoft.basic.entities.pessoa.Juridica;
import br.com.orionsoft.financeiro.documento.pagamento.DocumentoPagamentoCategoria;
import br.com.orionsoft.financeiro.gerenciador.entities.ContaBancaria;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;

@Entity
@Table(name = "financeiro_convenio_cobranca")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="discriminator",discriminatorType=DiscriminatorType.STRING,length=3)
@DiscriminatorValue("CON")
public class ConvenioCobranca {
	/*
	 * Constantes com o nomes das propriedades da classe para serem usadas no
	 * código e evitar erro de digitação.
	 */
	public static final String NOME = "nome";
	public static final String NOME_GERENCIADOR_DOCUMENTO = "nomeGerenciadorDocumento";
	public static final String OBSERVACOES = "observacoes";
	public static final String DOCUMENTO_PAGAMENTO_CATEGORIA = "documentoPagamentoCategoria";
	public static final String CONTRATANTE = "contratante";
	public static final String CONTRATADO = "contratado";
	public static final String DIAS_ANTECIPACAO = "diasAntecipacao";
    public static final String SEQUENCIA_NUMERO_DOCUMENTO = "sequenciaNumeroDocumento"; //número sequencial para geração do Nosso Número no Título

    public static final String CONTA_BANCARIA = "contaBancaria";
    
    public static final String REMESSA_ULTIMA_DATA = "remessaUltimaData";
    public static final String REMESSA_NUMERO_SEQUENCIAL = "remessaNumeroSequencial";
    
	private Long id = IDAO.ENTITY_UNSAVED;
	private String nome;
	private String nomeGerenciadorDocumento;
	private String observacoes;
	private DocumentoPagamentoCategoria documentoPagamentoCategoria;
	private Juridica contratante;
	private Contrato contratado;
	private int diasAntecipacao;
    private long sequenciaNumeroDocumento;

    private ContaBancaria contaBancaria;

    private Calendar remessaUltimaData;
    private int remessaNumeroSequencial = 1;
    
    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(length = 50)
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Column(length = 50)
	public String getNomeGerenciadorDocumento() {
		return nomeGerenciadorDocumento;
	}

	public void setNomeGerenciadorDocumento(String nomeGerenciadorDocumento) {
		this.nomeGerenciadorDocumento = nomeGerenciadorDocumento;
	}

	@Column(length = 256)
	public String getObservacoes() {
		return observacoes;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

	@ManyToOne
	@JoinColumn(name = "documentoPagamentoCategoria")
	@ForeignKey(name = "documentoPagamentoCategoria")
	public DocumentoPagamentoCategoria getDocumentoPagamentoCategoria() {
		return documentoPagamentoCategoria;
	}

	public void setDocumentoPagamentoCategoria(DocumentoPagamentoCategoria documentoCobrancaCategoria) {
		this.documentoPagamentoCategoria = documentoCobrancaCategoria;
	}

	@ManyToOne
	@JoinColumn(name = "contratante")
	@ForeignKey(name = "contratante")
	public Juridica getContratante() {
		return contratante;
	}

	public void setContratante(Juridica contratante) {
		this.contratante = contratante;
	}

	@ManyToOne
	@JoinColumn(name = "contratado")
	@ForeignKey(name = "contratado")
	public Contrato getContratado() {
		return contratado;
	}

	public void setContratado(Contrato contratado) {
		this.contratado = contratado;
	}

	@Override
	public String toString() {
		return this.nome;
	}
	
	@Column
	public int getDiasAntecipacao() {
		return diasAntecipacao;
	}

	public void setDiasAntecipacao(int diasAntecipacao) {
		this.diasAntecipacao = diasAntecipacao;
	}

	@Column
	@Version
    public long getSequenciaNumeroDocumento() {
        return sequenciaNumeroDocumento;
    }

    public void setSequenciaNumeroDocumento(long sequenciaNumeroDocumento) {
        this.sequenciaNumeroDocumento = sequenciaNumeroDocumento;
    }
    
    @ManyToOne
	@JoinColumn(name = "contaBancaria")
	@ForeignKey(name = "contaBancaria")
    public ContaBancaria getContaBancaria() {
        return contaBancaria;
    }

    public void setContaBancaria(ContaBancaria contaBancaria) {
        this.contaBancaria = contaBancaria;
    }

	@Column
	public int getRemessaNumeroSequencial() {
		return remessaNumeroSequencial;
	}

	public void setRemessaNumeroSequencial(int remessaNumeroSequencial) {
		this.remessaNumeroSequencial = remessaNumeroSequencial;
	}
	@Column
	public Calendar getRemessaUltimaData() {
		return remessaUltimaData;
	}

	public void setRemessaUltimaData(Calendar remessaUltimaData) {
		this.remessaUltimaData = remessaUltimaData;
	}

}