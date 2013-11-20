package br.com.orionsoft.basic.entities.pessoa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Essa classe corresponde ao javabean que representa a entidade do profissao
 * que corresponde as profissões associadas às pessoas fisicas. <br>
 * O campo código não obedece a nenhum padrão regulamentado e poder ser livremente
 * usado pelo domínio ao qual o sistema está sendo aplicado. <br>
 * 
 * No caso de sindicatos, este código poderá ser um correspondente ao CNAE.
 * 
 * @author marcia
 * @hibernate.class table="basic_profissao"
 */
@Entity
@Table(name="basic_profissao")
public class Profissao
{
    /* Constantes com o nomes das propriedades da classe para
     * serem usadas no código e evitar erro de digitação. */
    public static final String CODIGO = "codigo";
    public static final String NOME = "nome";

    private long id = -1;
    private String codigo;
    private String nome;
    
    /**
     * @hibernate.id  generator-class="native" unsaved-value="-1"
     * @return id da instância de profissao
     */
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public long getId(){return id;}
    public void setId(long id){this.id = id;}
    
    /**
     * @hibernate.property length="100"
     * @return nome da profissão
     */
    @Column(length=100)
    public String getNome(){return nome;}
    public void setNome(String nome){this.nome = nome;}

    /**
     * @hibernate.property length="4"
     * @return código da profissão sem padrão regulamentado
     */
    @Column(length=4)
    public String getCodigo(){return codigo;}
    public void setCodigo(String codigo){this.codigo = codigo;}
    
    public String toString(){
    	String result = "";
    	
    	if(this.codigo!=null)
    		result += this.codigo + "-";
    	
    	result += this.nome;
    	
    	return result;
    }
}
