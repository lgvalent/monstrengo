package br.com.orionsoft.basic.entities;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

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
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import br.com.orionsoft.basic.entities.pessoa.Pessoa;
import br.com.orionsoft.basic.entities.pessoa.Representante;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;

/**
 * Classe abstrata que possui os dados básicos de um contrato
 * que poderá ser extendida pelos módulos para implementar 
 * novos contratos.<br>
 * Os seguinte discriminadores são utilizados:<br>
 * <li>CON - Contrato</li> 
 * <li>CCA - Contrato Com Adesão</li> 
 * <li>FIN - ContratoFinanceiro</li> 
 * <li>SIN - ContratoSindicato</li> 
 * <li>BIB - ContratoBiblioteca</li> 
 * <li>PRS - ContratoProcessoSeletivo</li> 
 * <li>ACA - ContratoAcademico</li> 
 * <li>PRF - ContratoProfessor</li> 
 * <li>CLI - ContratoClinico</li> 
 * <li>CLA - ContratoClinicoAtendente</li> 
 * <li>AGE - ContratoAgenda</li> 
 * 
 * @author Antonio Alves
 * @version 20060424 Lucio 
 * @hibernate.class table="basic_contrato" 
 * @hibernate.joined-subclass-key column="id"
 */
@Entity
@Table(name="basic_contrato")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="discriminator", discriminatorType=DiscriminatorType.STRING, length=3)
@DiscriminatorValue("CON")
public class Contrato {
	public static final String CATEGORIA="categoria";
    public static final String PESSOA = "pessoa";
    public static final String DATA_INICIO = "dataInicio";
    public static final String DATA_RESCISAO = "dataRescisao";
    public static final String DATA_VENCIMENTO = "dataVencimento";
    public static final String INATIVO = "inativo";
    public static final String REPRESENTANTE = "representante"; 
    public static final String OBSERVACOES = "observacoes";
	public static final String CONTATOS = "contatos";
	public static final String CODIGO = "codigo";
	public static final String CODIGO_CONTA_CONTABIL="codigoContaContabil";
    
    protected long id = -1;
    protected Pessoa pessoa;
    protected Calendar dataInicio;
    protected Calendar dataRescisao;
    protected Calendar dataVencimento;
    protected boolean inativo;
    private Representante representante;
    protected ContratoCategoria categoria;
    protected Observacoes observacoes;
    private Set <ContratoContato>contatos = new HashSet<ContratoContato>();
    private String codigo;
	private String codigoContaContabil;

    /**
     * @hibernate.id generator-class="native" unsaved-value="-1"
     */
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    /**
     * @hibernate.property
     */
    @Column
    @Temporal(TemporalType.DATE)
    public Calendar getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Calendar dataInicio) {
        this.dataInicio = dataInicio;
    }

    /**
     * @hibernate.property
     */
    @Column
    @Temporal(TemporalType.DATE)
    public Calendar getDataRescisao() {
        return dataRescisao;
    }

    public void setDataRescisao(Calendar dataRecisao) {
        this.dataRescisao = dataRecisao;
    }

    /**
     * @hibernate.property
     */
    @Column
    @Temporal(TemporalType.DATE)
    public Calendar getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(Calendar dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    /**
     * @hibernate.property
     */
    @Column
    public boolean isInativo() {
        return inativo;
    }

    public void setInativo(boolean inativo) {
        this.inativo = inativo;
    }

    /**
	 * Pessoa representa uma hierarquia extensa de subclasses.
	 * Assim, um política FetchMode.SELECT evitará um JOIN extenso 
     * @hibernate.many-to-one foreign-key="pessoa"
     */
    @ManyToOne
	@Fetch(FetchMode.SELECT)
    @JoinColumn(name="pessoa")
    @ForeignKey(name="pessoa")
    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    /**
     * @hibernate.many-to-one unique="true" foreign-key="observacoes" cascade="delete"
     */
    @ManyToOne @Cascade(CascadeType.ALL) /*@Column(unique=true)*/
	@JoinColumn(name="observacoes")
	@ForeignKey(name="observacoes")
    public Observacoes getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(Observacoes observacoes) {
        this.observacoes = observacoes;
    }

    /**
	 * Representante representa uma hierarquia extensa de subclasses.
	 * Assim, um política FetchMode.SELECT evitará um JOIN extenso 
     * @hibernate.many-to-one foreign-key="representante"
     */
    @ManyToOne 
	@Fetch(FetchMode.SELECT)
	@JoinColumn(name="representante")
	@ForeignKey(name="representante")
	public Representante getRepresentante(){
		return representante;
	}

	public void setRepresentante(Representante representante){
		this.representante = representante;
	}
    

	/**
	 * Uma vez que ContratoCategoria e Contrato são abstratas e cada módulo implementa 
	 * uma especialização destas classes, o hibernate deve ser informado 
	 * qual é o tipo da classe implementa em categoria, assim, o xDoclet deve ser 
	 * definido no especialização do contrato, e o método get/setCategoria deverá
	 * ser sobreescrito por uma versão da própria implementação do contrato que indicará
	 * qual especialização de categoria utilizar		
	 * #hibernate.many-to-one foreign-key="categoria"
	 */
	@ManyToOne
	@JoinColumn(name="categoria")
	@ForeignKey(name="categoria")
	public ContratoCategoria getCategoria() {return categoria;}
	public void setCategoria(ContratoCategoria categoria) {this.categoria = categoria;}

    /**
     * @hibernate.set lazy="false"
     * @hibernate.collection-key-column index="contrato" name="contrato"  
     * @hibernate.collection-key foreign-key="contrato" 
     * @hibernate.collection-one-to-many class="br.com.orionsoft.basic.entities.ContratoContato"
     */
	@OneToMany @LazyCollection(LazyCollectionOption.FALSE)
	@ForeignKey(name="contrato")
	@JoinColumn(name="contrato")
	@OrderBy(ContratoContato.DATA_HORA)
	public Set<ContratoContato> getContatos() {return contatos;}
	public void setContatos(Set<ContratoContato> contatos) {this.contatos = contatos;}

	@Column(length=20)
	@Index(name=CODIGO)
	public String getCodigo() {
		return codigo;
	}
	
	public void setCodigo(String codigo) {
		this.codigo = codigo==null?null:StringUtils.substring(codigo, 0, 20);
	}
	
	@Column(length=20)
	public String getCodigoContaContabil() {
		return codigoContaContabil;
	}

	public void setCodigoContaContabil(String codigoContaContabil) {
		this.codigoContaContabil = codigoContaContabil;
	}

	public String toString() {
		StringBuffer result = new StringBuffer();

		if (this.categoria != null)
			result.append("(" + this.categoria.toString() + ")");

		if (this.inativo){
			result.append("[Inativo");
			if(this.dataRescisao!=null){
				result.append(" em ");
				result.append(CalendarUtils.formatDate(this.dataRescisao));
			}
			else if(this.dataVencimento!=null){
				result.append(" em ");
				result.append(CalendarUtils.formatDate(this.dataVencimento));
			}
			result.append("]");
		}

		if (this.pessoa != null)
			result.append(" " + this.pessoa.toString());
        
        return result.toString();
    }


}
