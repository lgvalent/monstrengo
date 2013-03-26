package br.com.orionsoft.monstrengo.auditorship.support;

import java.util.HashMap;
import java.util.Map;

import br.com.orionsoft.monstrengo.auditorship.support.AuditorshipSupportException;
import br.com.orionsoft.monstrengo.auditorship.support.PropertyAuditValue;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IProperty;

/**
 * Esta classe manipula a lógica de um valor de auditoria.
 * A classe analisa as propriedades de uma entidade para armazenar
 * os valores de comparação que serão utilizados no final processo para auditar
 * as alterações ocorridas na entidade.
 * 
 * @author Lucio 2005/11/25
 *
 */
public class EntityAuditValue
{
    private IEntity entity;
    
    private Map<String, PropertyAuditValue> propertiesAuditValues=null;

    /**
     * Constrói um valor para ser auditado posteriormente.
     * @param prop Propriedades que será auditada.
     * @throws BusinessException
     */
    public EntityAuditValue(IEntity entity) throws BusinessException
    {
        // Armazena qual a entidade que será monitorada para a auditoria
        this.entity = entity;
      
        // Analisa o valor atual e armazena para posterior comparação e
        // detecção de alterações
        this.prepareAuditValues();
      
    }
    /**
     * Este método analisa a entidade e solicita a cada propriedade 
     * que construa sua descrição de auditoria caso tenha ocorrido alguma
     * alteração.
     *  
     * @return Um string descrevendo as alterações ocorridas na entidade. 
     *         <p>Exemplo: nome_propriedade_não_numérica='valor_anterior';
     *         nome_propriedade_numérica=valor_anterior;
     *         nome_propriedade_entidade.id=antigo_id;
     *         nome_propriedade_entidades.id=+[ids_adicionados]-[ids_removidos];  
     * @throws BusinessException 
     */
    public String retrieveAuditDescription() throws BusinessException
    {
        String result = "";
        try
        {
            // Compara os dois mapas para cada propriedade
            for(PropertyAuditValue prop: propertiesAuditValues.values())
            {
                // Pega a descrição da alteração da propriedade corrente
                String propAudit = prop.retrieveAuditDescriptionIfChanged();
                
                // Verifica se foi retornada alguma alteração na propriedade
                if (propAudit != null)
                {
                    // Adiciona a alteração na descrição geral
                    result += propAudit + "; ";
                }
            }

            return result;
        }
        catch(BusinessException e)
        {
            e.getErrorList().add(new BusinessMessage(AuditorshipSupportException.class, "RETRIEVE_AUDIT_DESCRIPTION", entity.getInfo().getLabel()));
            throw e;
        }
    }

    
    /**
     * Este método prepara as propriedades a serem auditadas.
     * @throws BusinessException 
     */
    private void prepareAuditValues() throws BusinessException
    {
        try
        {
            Map<String, PropertyAuditValue> props = new HashMap<String, PropertyAuditValue>();
            
            // Percorre todas as propriedades da entidade
            for(IProperty prop: entity.getProperties())
            {
                props.put(prop.getInfo().getName(), new PropertyAuditValue(prop));
            }
            
            this.propertiesAuditValues = props;
        }
        catch(BusinessException e)
        {
            e.getErrorList().add(new BusinessMessage(AuditorshipSupportException.class, "PREPRARE_AUDIT_DESCRIPTION", entity.getInfo().getLabel()));
            throw e;
        }
    
    }
	/**
	 * Permite ter acesso a entidade que é referenciada
	 * @version Lucio 20090114 
	 * @return
	 */
    public IEntity getEntity() {return entity;}
    
}