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
 * Esta classe manipula a l�gica de um valor de auditoria.
 * A classe analisa as propriedades de uma entidade para armazenar
 * os valores de compara��o que ser�o utilizados no final processo para auditar
 * as altera��es ocorridas na entidade.
 * 
 * @author Lucio 2005/11/25
 *
 */
public class EntityAuditValue
{
    private IEntity entity;
    
    private Map<String, PropertyAuditValue> propertiesAuditValues=null;

    /**
     * Constr�i um valor para ser auditado posteriormente.
     * @param prop Propriedades que ser� auditada.
     * @throws BusinessException
     */
    public EntityAuditValue(IEntity entity) throws BusinessException
    {
        // Armazena qual a entidade que ser� monitorada para a auditoria
        this.entity = entity;
      
        // Analisa o valor atual e armazena para posterior compara��o e
        // detec��o de altera��es
        this.prepareAuditValues();
      
    }
    /**
     * Este m�todo analisa a entidade e solicita a cada propriedade 
     * que construa sua descri��o de auditoria caso tenha ocorrido alguma
     * altera��o.
     *  
     * @return Um string descrevendo as altera��es ocorridas na entidade. 
     *         <p>Exemplo: nome_propriedade_n�o_num�rica='valor_anterior';
     *         nome_propriedade_num�rica=valor_anterior;
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
                // Pega a descri��o da altera��o da propriedade corrente
                String propAudit = prop.retrieveAuditDescriptionIfChanged();
                
                // Verifica se foi retornada alguma altera��o na propriedade
                if (propAudit != null)
                {
                    // Adiciona a altera��o na descri��o geral
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
     * Este m�todo prepara as propriedades a serem auditadas.
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
	 * Permite ter acesso a entidade que � referenciada
	 * @version Lucio 20090114 
	 * @return
	 */
    public IEntity getEntity() {return entity;}
    
}