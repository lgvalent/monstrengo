package br.com.orionsoft.monstrengo.auditorship.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;

import br.com.orionsoft.monstrengo.auditorship.entities.AuditRegister;
import br.com.orionsoft.monstrengo.security.entities.ApplicationEntity;

/**
 * TODO DOCUMENTAR essa classe
 * @author Lucio 06/10/2005
 * @version 06/10/2005
 * @hibernate.class table="auditorship_crud"
 */
@Entity
@Table(name="auditorship_crud")
public class AuditCrudRegister extends AuditRegister
{
    /* Constantes com o nomes das propriedades da classe para
     * serem usadas no código e evitar erro de digitação. */
    public static final String APPLICATION_ENTITY = "applicationEntity";
    public static final String ENTITY_ID = "entityId";
    public static final String CREATED = "created";
    public static final String UPDATED = "updated";
    public static final String DELETED = "deleted";
    
    private ApplicationEntity applicationEntity;
    private long entityId;
    private boolean created;
    private boolean updated;
    private boolean deleted;
    
    /**
     * @hibernate.property 
     */
    @Column
    public boolean isCreated()
    {
        return created;
    }

    public void setCreated(boolean created)
    {
        this.created = created;
    }

    /**
     * @hibernate.property 
     */
    @Column
    public boolean isDeleted()
    {
        return deleted;
    }

    public void setDeleted(boolean deleted)
    {
        this.deleted = deleted;
    }

    /**
     * @hibernate.property 
     */
    @Column
    public boolean isUpdated()
    {
        return updated;
    }

    public void setUpdated(boolean updated)
    {
        this.updated = updated;
    }

    /**
     * @hibernate.property 
     */
    @Column
    public long getEntityId()
    {
        return entityId;
    }
    
    public void setEntityId(long entityId)
    {
        this.entityId = entityId;
    }
    
    /**
     * @hibernate.many-to-one foreign-key="applicationEntity"
     */
    @ManyToOne
    @JoinColumn(name="applicationEntity")
    @ForeignKey(name="applicationEntity")
    public ApplicationEntity getApplicationEntity()
    {
        return applicationEntity;
    }

    public void setApplicationEntity(ApplicationEntity applicationEntity)
    {
        this.applicationEntity = applicationEntity;
    }

    public String toString()
    {
        String result = "";
        result += super.toString() + this.applicationEntity.toString() + " (" + this.entityId +") [" + super.description + "]"; 
        return result;

    }
}
