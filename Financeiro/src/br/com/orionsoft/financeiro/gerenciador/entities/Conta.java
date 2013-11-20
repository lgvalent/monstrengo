package br.com.orionsoft.financeiro.gerenciador.entities;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;

@Entity
@Table(name="financeiro_conta")
public class Conta {
	/* Constantes com o nomes das propriedades da classe para 
	 * serem usadas no código e evitar erro de digitação. */
    public static final String NOME ="nome";
    public static final String CONTA_CATEGORIA ="contaCategoria";
    public static final String SALDO_ABERTURA = "saldoAbertura";
    public static final String DATA_ABERTURA = "dataAbertura";
    public static final String DATA_ENCERRAMENTO = "dataEncerramento";
    public static final String DATA_FECHAMENTO = "dataFechamento";
	public static final String INATIVO="inativo";
	public static final String APPLICATION_USERS="applicationUsers";
	public static final String COMPENSACAO_AUTOMATICA="compensacaoAutomatica";
	public static final String CONTA_CONTABIL_PREVISTA="contaContabilPrevista";
	public static final String CONTA_CONTABIL_MOVIMENTO="contaContabilMovimento";
	public static final String CONTA_CONTABIL_COMPENSACAO="contaContabilCompensacao";
	
	private Long id=-1l;
    private String nome;
    private ContaCategoria contaCategoria;
    private BigDecimal saldoAbertura = new BigDecimal(0, new MathContext(2));
    private Calendar dataAbertura;
    private Calendar dataEncerramento;
    private Calendar dataFechamento;
    private boolean inativo;
    private Set<ApplicationUser> applicationUsers = new HashSet<ApplicationUser>();
    private boolean compensacaoAutomatica;
	private String contaContabilPrevista;
	private String contaContabilMovimento;
	private String contaContabilCompensacao;

	
    @Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((contaCategoria == null) ? 0 : contaCategoria.hashCode());
		result = PRIME * result + ((dataAbertura == null) ? 0 : dataAbertura.hashCode());
		result = PRIME * result + ((dataEncerramento == null) ? 0 : dataEncerramento.hashCode());
		result = PRIME * result + ((dataFechamento == null) ? 0 : dataFechamento.hashCode());
		result = PRIME * result + ((id == null) ? 0 : id.hashCode());
		result = PRIME * result + ((nome == null) ? 0 : nome.hashCode());
		result = PRIME * result + ((saldoAbertura == null) ? 0 : saldoAbertura.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Conta other = (Conta) obj;
		if (contaCategoria == null) {
			if (other.contaCategoria != null)
				return false;
		} else if (!contaCategoria.equals(other.contaCategoria))
			return false;
		if (dataAbertura == null) {
			if (other.dataAbertura != null)
				return false;
		} else if (!dataAbertura.equals(other.dataAbertura))
			return false;
		if (dataEncerramento == null) {
			if (other.dataEncerramento != null)
				return false;
		} else if (!dataEncerramento.equals(other.dataEncerramento))
			return false;
		if (dataFechamento == null) {
			if (other.dataFechamento != null)
				return false;
		} else if (!dataFechamento.equals(other.dataFechamento))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (nome == null) {
			if (other.nome != null)
				return false;
		} else if (!nome.equals(other.nome))
			return false;
		if (saldoAbertura == null) {
			if (other.saldoAbertura != null)
				return false;
		} else if (!saldoAbertura.equals(other.saldoAbertura))
			return false;
		return true;
	}
	@Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Column(length=50)
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	@ManyToOne
	@JoinColumn(name=CONTA_CATEGORIA)
	@ForeignKey(name=CONTA_CATEGORIA)
	public ContaCategoria getContaCategoria() {
		return contaCategoria;
	}
	public void setContaCategoria(ContaCategoria contaCategoria) {
		this.contaCategoria = contaCategoria;
	}
	@Column
	@Temporal(TemporalType.DATE)
	public Calendar getDataAbertura() {
		return dataAbertura;
	}
	public void setDataAbertura(Calendar dataAbertura) {
		this.dataAbertura = dataAbertura;
	}
	@Column
	@Temporal(TemporalType.DATE)
	public Calendar getDataEncerramento() {
		return dataEncerramento;
	}
	public void setDataEncerramento(Calendar dataEncerramento) {
		this.dataEncerramento = dataEncerramento;
	}
	@Column
	@Temporal(TemporalType.DATE)
	public Calendar getDataFechamento() {
		return dataFechamento;
	}
	public void setDataFechamento(Calendar dataFechamento) {
		this.dataFechamento = dataFechamento;
	}
	@Column
	public BigDecimal getSaldoAbertura() {
		return saldoAbertura;
	}
	public void setSaldoAbertura(BigDecimal saldoAbertura) {
		this.saldoAbertura = saldoAbertura;
	}

	@Column
	public boolean isInativo() {
		return inativo;
	}
	public void setInativo(boolean inativo) {
		this.inativo = inativo;
	}

    
	@Column
	public boolean isCompensacaoAutomatica() {
		return compensacaoAutomatica;
	}
	public void setCompensacaoAutomatica(boolean compensacaoAutomatica) {
		this.compensacaoAutomatica = compensacaoAutomatica;
	}

	@ManyToMany @LazyCollection(LazyCollectionOption.FALSE)
    @Fetch(FetchMode.SELECT)
	@JoinTable(
    		name="financeiro_conta_user",
    	    joinColumns={@JoinColumn(name="conta")},
    	    inverseJoinColumns={@JoinColumn(name="applicationUser")}
    		)
	@ForeignKey(name="conta")
	@OrderBy(ApplicationUser.NAME)
    public Set <ApplicationUser>getApplicationUsers() {return applicationUsers;}
    public void setApplicationUsers(Set <ApplicationUser>applicationUsers){this.applicationUsers = applicationUsers;}

	@Column(length=20)
	public String getContaContabilPrevista() {return contaContabilPrevista;}
	public void setContaContabilPrevista(String codigoContaContabil) {this.contaContabilPrevista = codigoContaContabil;}

	@Column(length=20)
	public String getContaContabilMovimento() {return contaContabilMovimento;}
	public void setContaContabilMovimento(String codigoContaContabil) {this.contaContabilMovimento = codigoContaContabil;}

	@Column(length=20)
	public String getContaContabilCompensacao() {return contaContabilCompensacao;}
	public void setContaContabilCompensacao(String codigoContaContabil) {this.contaContabilCompensacao = codigoContaContabil;}

	@Override
	public String toString() {
		return this.nome;
	}
 }
