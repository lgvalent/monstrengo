package br.com.orionsoft.basic.entities.pessoa;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import br.com.orionsoft.basic.entities.endereco.Municipio;
import br.com.orionsoft.basic.entities.endereco.Pais;
import br.com.orionsoft.basic.entities.endereco.Uf;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;


/**
 * Essa classe corresponde ao javabean que representa a entidade do tipo fisica, que é uma
 * especialização da classe pessoa. Os objetos do tipo fisica são armazenados na tabela
 * 'pessoa' do banco de dados, sendo discriminado pela letra f na coluna jf da tabela.
 *
 * Created on 15/02/2005
 * @author Marcia
 * @hibernate.joined-subclass table="basic_fisica"
 * Esta tag, informa que o índice a ser usado para ligação entre as classes é
 * o PRIMARY. Assim, o hibernate não cria novo índice com nomes "FDFS8DD8765".
 * @hibernate.joined-subclass-key foreign-key="PRIMARY"
 */
@Entity
@DiscriminatorValue("F")
public class Fisica extends Pessoa
{
    /* Constantes com o nomes das propriedades da classe para
     * serem usadas no código e evitar erro de digitação. */
    public static final String RG_NUMERO = "rgNumero";
    public static final String RG_ORGAO_EXPEDIDOR = "rgOrgaoExpedidor";
    public static final String RG_UF_EXPEDIDOR = "rgUfExpedidor";
    public static final String RG_DATA_EXPEDICAO = "rgDataExpedicao";
    public static final String PIS_PASEP = "pisPasep";
    public static final String INSS = "inss";
    public static final String ESTADO_CIVIL = "estadoCivil";
    public static final String PROFISSAO = "profissao";
    public static final String SEXO = "sexo";
    public static final String CANHOTA = "canhota";
    public static final String NATURALIDADE = "naturalidade";
    public static final String NACIONALIDADE = "nacionalidade";
    public static final String USA_RESPONSAVEL_CPF = "usaResponsavelCpf";
    public static final String RESPONSAVEL_CPF = "responsavelCpf";
	public static final String NECESSIDADES_ESPECIAIS = "necessidadesEspeciais";
	public static final String APPLICATION_USER = "applicationUser";

	public static final String NOME_MAE = "nomeMae";
	public static final String NOME_PAI = "nomePai";

    public static final String DOCUMENTO_MILITAR = "documentoMilitarNumero";
    public static final String DOCUMENTO_MILITA_ORGAO = "documentoMilitarOrgao";
    public static final String DOCUMENTO_MILITAR_TIPO = "documentoMilitarTipo";
    public static final String DOCUMENTO_MILITAR_DATA_EXPEDICAO = "documentoMilitarDataExpedicao";
    
    public static final String CCO = "cco";

    private String rgOrgaoExpedidor;
    private String rgNumero;
    private Uf rgUfExpedidor;
    private Calendar rgDataExpedicao;
    private String pisPasep;
    private String inss;
    private EstadoCivil estadoCivil;
    private Profissao profissao;
    private String sexo;
    private boolean canhota;
    private Municipio naturalidade;
    private Pais nacionalidade;
    private ResponsavelCpf responsavelCpf;
    private ApplicationUser applicationUser;
    private List<NecessidadeEspecial> necessidadesEspeciais = new ArrayList<NecessidadeEspecial>(0);
    private String nomeMae;
    private String nomePai;
    private String documentoMilitarNumero;
    private String documentoMilitarOrgao;
    private DocumentoMilitarTipo documentoMilitarTipo;
    private Calendar documentoMilitarDataExpedicao;
    private String cco;

    @Column(length=12)
    public String getCco() {return cco;}
	public void setCco(String cco) {this.cco = cco;}
	
    @Column(length=10)
    public String getRgNumero() {return rgNumero;}
	public void setRgNumero(String rgNumero) {this.rgNumero = rgNumero;}

	/**
     * @hibernate.property length="5"
     */
	@Column(length=5)
	public String getRgOrgaoExpedidor() {
		return rgOrgaoExpedidor;
	}
	public void setRgOrgaoExpedidor(String rgOrgaoExpedidor) {
		this.rgOrgaoExpedidor = rgOrgaoExpedidor;
	}

	@Enumerated(EnumType.STRING)
    @Column(length=Uf.COLUMN_DISCRIMINATOR_LENGTH)
	public Uf getRgUfExpedidor() {return rgUfExpedidor;}
	public void setRgUfExpedidor(Uf rgUfExpedidor) {this.rgUfExpedidor = rgUfExpedidor;}

	/**
     * @hibernate.property
     */
	@Column
    @Temporal(TemporalType.DATE)
	public Calendar getRgDataExpedicao() {
		return rgDataExpedicao;
	}
	public void setRgDataExpedicao(Calendar rgDataExpedicao) {
		this.rgDataExpedicao = rgDataExpedicao;
	}

	/**
     * @hibernate.property length="11"
     */
	@Column(length=11)
	public String getPisPasep() {
		return pisPasep;
	}
	public void setPisPasep(String pisPasep) {
		this.pisPasep = pisPasep;
	}

	@Column(length=11)
	public String getInss() {
		return inss;
	}
	public void setInss(String inss) {
		this.inss = inss;
	}

    /**
     * @hibernate.many-to-one foreign-key="estadoCivil"
     */
	@Enumerated(EnumType.STRING)
	@Column(length=EstadoCivil.COLUMN_DISCRIMINATOR_LENGTH)
    public EstadoCivil getEstadoCivil() {
		return estadoCivil;
	}
	public void setEstadoCivil(EstadoCivil estadoCivil) {
		this.estadoCivil = estadoCivil;
	}

    /**
     * @hibernate.many-to-one foreign-key="profissao"
     */
	@ManyToOne
	@JoinColumn(name=PROFISSAO)
	@ForeignKey(name=PROFISSAO)
    public Profissao getProfissao()
    {
        return profissao;
    }
    public void setProfissao(Profissao profissao)
    {
        this.profissao = profissao;
    }

    /**
     * @hibernate.property  length="1"
     */
    @Column(length=1)
    public String getSexo() {
		return sexo;
	}
	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	/**
     * @hibernate.many-to-one foreign-key="naturalidade"
     */
	@ManyToOne
	@JoinColumn(name=NATURALIDADE)
	@ForeignKey(name=NATURALIDADE)
	public Municipio getNaturalidade() {
		return naturalidade;
	}
	public void setNaturalidade(Municipio naturalidade) {
		this.naturalidade = naturalidade;
	}

	/**
     * @hibernate.many-to-one foreign-key="nacionalidade"
     */
	@ManyToOne
	@JoinColumn(name=NACIONALIDADE)
	@ForeignKey(name=NACIONALIDADE)
	public Pais getNacionalidade() {
		return nacionalidade;
	}
	public void setNacionalidade(Pais nacionalidade) {
		this.nacionalidade = nacionalidade;
	}

    /**
     * @hibernate.many-to-one foreign-key="responsavelCpf"
     */
    @ManyToOne
    @JoinColumn(name=RESPONSAVEL_CPF)
    @ForeignKey(name=RESPONSAVEL_CPF)
    public ResponsavelCpf getResponsavelCpf() {
		return responsavelCpf;
	}

	public void setResponsavelCpf(ResponsavelCpf responsavelCpf) {
		this.responsavelCpf = responsavelCpf;
	}

	@Transient
	public boolean isUsaResponsavelCpf() {
		return (this.responsavelCpf != null);
	}

	@Column
	public boolean isCanhota() {
		return canhota;
	}

	public void setCanhota(boolean canhota) {
		this.canhota = canhota;
	}

	/**
	 * @hibernate.bag table="basic_fisica_necessidade_especial" cascade="save-update" lazy="false"
     * @hibernate.collection-key column="fisica"
     * @hibernate.collection-many-to-many class="br.com.orionsoft.basic.entities.pessoa.NecessidadeEspecial" column="necessidadeEspecial"
	 */
    @ManyToMany
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @LazyCollection(LazyCollectionOption.FALSE)
	@JoinTable(
			name="basic_fisica_necessidade_especial",
    	    joinColumns={@JoinColumn(name=FISICA)},
    	    inverseJoinColumns={@JoinColumn(name=NECESSIDADES_ESPECIAIS)}
    		)
	@ForeignKey(name=FISICA)
	public List<NecessidadeEspecial> getNecessidadesEspeciais() {return necessidadesEspeciais;}
	public void setNecessidadesEspeciais(List<NecessidadeEspecial> necessidadesEspeciais) {this.necessidadesEspeciais = necessidadesEspeciais;}

	/**
	 * @hibernate.many-to-one foreign-key="applicationUser"
	 */
    @ManyToOne
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name=APPLICATION_USER)
    @ForeignKey(name=APPLICATION_USER)
	public ApplicationUser getApplicationUser() {return applicationUser;}
	public void setApplicationUser(ApplicationUser applicationUser) {this.applicationUser = applicationUser;}

	@Column(length=100)
	public String getNomeMae() {return nomeMae;}
	public void setNomeMae(String nomeMae) {		this.nomeMae = nomeMae;}

	@Column(length=100)
	public String getNomePai() {return nomePai;}
	public void setNomePai(String nomePai) {this.nomePai = nomePai;}

	@Column(length=20)
	public String getDocumentoMilitarNumero() {return documentoMilitarNumero;}
	public void setDocumentoMilitarNumero(String documentoMilitarNumero) {this.documentoMilitarNumero = documentoMilitarNumero;}
	
	@Column(length=3)
	public String getDocumentoMilitarOrgao() {return documentoMilitarOrgao;}
	public void setDocumentoMilitarOrgao(String documentoMilitarOrgao) {this.documentoMilitarOrgao = documentoMilitarOrgao;}
	
	@Enumerated(EnumType.STRING)
	@Column(length=DocumentoMilitarTipo.COLUMN_DISCRIMINATOR_LENGTH)
	public DocumentoMilitarTipo getDocumentoMilitarTipo() {return documentoMilitarTipo;}
	public void setDocumentoMilitarTipo(DocumentoMilitarTipo documentoMilitarTipo) {this.documentoMilitarTipo = documentoMilitarTipo;}
	
	@Temporal(TemporalType.DATE)
	public Calendar getDocumentoMilitarDataExpedicao() {return documentoMilitarDataExpedicao;}
	public void setDocumentoMilitarDataExpedicao(Calendar documentoMilitarDataExpedicao) {this.documentoMilitarDataExpedicao = documentoMilitarDataExpedicao;}
	
	public String toString() {
        String result = getNome();

        if(StringUtils.isNotEmpty(super.getDocumento()))
        	result += " (CPF:" + super.getDocumento() + ")";
    	return result;
    }
}
