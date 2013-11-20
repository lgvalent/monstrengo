package br.com.orionsoft.financeiro.documento.cobranca.titulo;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;

import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;

/**
 * 
 * @author marcia
 *
 * @hibernate.class table="financeiro_titulo_ocorrencia_controle"
 */
@Entity
@Table(name="financeiro_titulo_ocorrencia_controle")
public class OcorrenciaControle {
    
	public static final String TITULO = "titulo";
    public static final String OCORRENCIA = "ocorrencia";
    public static final String DATA_OCORRENCIA = "dataOcorrencia";
    
	private long id = IDAO.ENTITY_UNSAVED;
    private DocumentoTitulo titulo;
    private Ocorrencia ocorrencia;	
    private Calendar dataOcorrencia;
    private String motivo;
    
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
     * @hibernate.many-to-one foreign-key="titulo"
     */
	@ManyToOne
	@JoinColumn(name = "titulo")
	@ForeignKey(name = "titulo")
	public DocumentoTitulo getTitulo() {
		return titulo;
	}

	public void setTitulo(DocumentoTitulo titulo) {
		this.titulo = titulo;
	}

    /**
     * @hibernate.many-to-one foreign-key="ocorrencia"
     */
	@ManyToOne
	@JoinColumn(name = "ocorrencia")
	@ForeignKey(name = "ocorrencia")
	public Ocorrencia getOcorrencia() {
		return ocorrencia;
	}

	public void setOcorrencia(Ocorrencia ocorrencia) {
		this.ocorrencia = ocorrencia;
	}

	/**
     * @hibernate.property
     */	
	@Column
	public Calendar getDataOcorrencia() {
		return dataOcorrencia;
	}

	public void setDataOcorrencia(Calendar dataUltimaOcorrencia) {
		this.dataOcorrencia = dataUltimaOcorrencia;
	}
	
	@Column(length=100)
	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	public String toString() {
        String result = "";
        if (this.ocorrencia != null) {
            result = "Código: " + this.ocorrencia.getCodigo();
            result += " / Descrição: " + this.ocorrencia.getDescricao();
            result += " / Data: " + CalendarUtils.formatDate(this.dataOcorrencia);
        }
        return result;
    }
}
