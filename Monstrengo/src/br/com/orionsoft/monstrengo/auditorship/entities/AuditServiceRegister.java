package br.com.orionsoft.monstrengo.auditorship.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import br.com.orionsoft.monstrengo.auditorship.entities.AuditRegister;

/**
 * @author Lucio 20120521
 * @version 20120521
 */
@Entity
@Table(name="auditorship_service")
public class AuditServiceRegister extends AuditRegister
{
    /* Constantes com o nomes das propriedades da classe para
     * serem usadas no código e evitar erro de digitação. */
    public static final String SERVICE_NAME = "serviceName";
    
    private String serviceName;
    
    @Column(length=50)
    public String getServiceName()
    {
        return serviceName;
    }
    public void setServiceName(String serviceName)
    {
        this.serviceName = serviceName;
    }
    
    public String toString()
    {
        String result = "";
        result += super.toString() + this.serviceName.toString() + " [" + super.description + "]"; 
        return result;

    }
}
