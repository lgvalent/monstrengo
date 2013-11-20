package br.com.orionsoft.basic.entities.pessoa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

/**
 * CNAE - Classificação Nacional de Atividade Econômica
 * Informações: http://www.cnae.ibge.gov.br
 * 
 * @author Marcia 2005/12/13
 * @hibernate.class table="basic_cnae"
 */
@Entity
@Table(name="basic_cnae")
public class CNAE
{
    /* Constantes com o nomes das propriedades da classe para
     * serem usadas no código e evitar erro de digitação. */
    public static final String CODIGO = "codigo";
    public static final String NOME = "nome";

    private long id=-1;
    private String codigo;
    private String nome;
    
    /**
     * @hibernate.id generator-class="native" unsaved-value="-1"
     */
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public long getId(){return id;}
    public void setId(long id){this.id = id;}
    
    /**
     * @hibernate.property length="7"
     * 
     */
    @Column(length=7)
    public String getCodigo()
    {
        return codigo;
    }
    public void setCodigo(String codigo)
    {
        this.codigo = codigo;
    }

    /**
     * @hibernate.property length="255"
     */
    @Column(length=255)
    public String getNome()
    {
        return nome;
    }
    public void setNome(String nome)
    {
        this.nome = nome;
    }
    
    /**
     * Obtem o campo Divisão do código CNAE
     *  
     * @return
     */
    @Transient
    public String getDivisao()
    {
       if (codigo != null)
           return StringUtils.substring(codigo, 0, 2);
       
       return "";
    }
 
    /**
     * Obtem o campo Grupo do código CNAE
     *  
     * @return
     */
    @Transient
    public String getGrupo()
    {
       if (codigo != null)
           return StringUtils.substring(codigo, 0, 3);
       
       return "";
    }
 
    /**
     * Obtem o campo Classe do código CNAE
     *  
     * @return
     */
    @Transient
    public String getClasse()
    {
        if (codigo != null)
           return StringUtils.substring(codigo, 0, 5);
       
       return "";
    }
    
    
    public String toString(){
    	return this.codigo + "-" + this.nome;
    }
 
}
