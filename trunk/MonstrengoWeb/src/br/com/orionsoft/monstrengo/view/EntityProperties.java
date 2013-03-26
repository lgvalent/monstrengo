package br.com.orionsoft.monstrengo.view;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.beanutils.PropertyUtils;

import br.com.orionsoft.monstrengo.core.service.IServiceManager;
import br.com.orionsoft.monstrengo.core.util.ArrayUtils;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;

/**
 * 
 * Created on 25/05/2005
 * @author Marcia
 */
public class EntityProperties
{
    private IServiceManager app;

    private EntityData ownerEntity;

    private boolean preparedForView=false;
    private boolean preparedForEdit=false;

    
    private EntityPropertyInfo[] properties;
    private EntityPropertyValue[] values;

    /////////////////////////////////////////////////////////////////////
    // CONSTRUTOR
    /////////////////////////////////////////////////////////////////////
    public EntityProperties(IServiceManager app, EntityData ownerEntity ) throws Exception
    {
//        System.out.println("EntityProperties.constructor: " + ownerEntity.getClassName() + " data: " + ownerEntity.getData());
        
        this.app = app;
        this.ownerEntity = ownerEntity;
        
        // J� prepara para visualiza��o
        prepareForView(); 
    }
    /////////////////////////////////////////////////////////////////////
    // PROPRIEDADES RETORN�VEIS
    /////////////////////////////////////////////////////////////////////
    public int getCount()
    {
      return properties.length;
    }

    /////////////////////////////////////////////////////////////////////
    // M�TODOS
    /////////////////////////////////////////////////////////////////////
    private void prepareForView() throws Exception
    {
//        System.out.println("EntityProperties.prepareForView:");

        // Verifica j� estah preparado para VIEW
        // se j� estiver N�o faz nada
        if(!preparedForView){

            // Pega os campos declarados da classe
            Field[] fields = EntityUtils.getFields(ownerEntity.getEntityClass());
            // Prepara a classe que fornecer� as informa��es dos campos
            EntityInfo entityInfo = new EntityInfo(ownerEntity.getEntityClass());

            // Cria a properties com o TAMANHO DEFINIDO para poder inserir as propriedades no seu indices, mesmo que o a lista ainda seja menor. 
            properties = new EntityPropertyInfo[fields.length];
            values = new EntityPropertyValue[fields.length];
            
            // Para cada campo, cria uma propriedade e preenche ele
            for (int i=0; i<fields.length; i++)
            {
                EntityPropertyInfo prop = new EntityPropertyInfo();
                EntityPropertyValue value = new EntityPropertyValue(app, prop);
                
                // Guarda no nome da propriedade para otimiza��o nas pr�ximas utiliza��es
                String propName = fields[i].getName();

                //Preenche os dados de View 
                prop.setName(propName);
                prop.setLabel(entityInfo.getLabel(propName));
                prop.setHint(entityInfo.getHint(propName));
                prop.setHelp(entityInfo.getHelp(propName));
                prop.setVisible(entityInfo.getVisible(propName));
                prop.setSize(entityInfo.getSize(propName));
                prop.setDisplayFormat(entityInfo.getDisplayFormat(propName));
                prop.setColorName(entityInfo.getColorName(propName));

                // Para definir o �ndice do campo, pesquisa-se o arquivo
                // Caso o arquivo n�o apresente, ent�o � utilizado o �ndica
                // de declara��o.
                prop.setIndex(entityInfo.getIndex(propName));

                // Define o tipo do campo:
                // Se achar definido no arquivo utiliza-o
                // Sen�o utiliza o tipo Java declarado
                Class fieldClass = entityInfo.getTypeList(propName);
                if (fieldClass != null)
                {
                    prop.setType(fieldClass);
                    // Indica que o tipo foi obtido do arquivo para responder ao
                    // m�todo isList;
                    prop.setTypeFromFile(true);
                } else
                    prop.setType(fields[i].getType());

                // Define o valor do campo caso tenha sido passado um objeto j� instanciado
                if (ownerEntity.getData()!= null)
                {
                    try
                    {
                        Object propData = PropertyUtils.getProperty(ownerEntity.getData(), propName);
                        // Armazena o resultado toString() do objeto
//                        value.setValue(propData.toString()); Lucio-Value � baseado no Object.toString()
                        value.setObject(propData);

                        // Armazena o Id do objeto se o objeto n�o for nulo e for uma subClasse
                        if ((propData != null) &&( prop.isSubClass()))
                            value.setId((Long)PropertyUtils.getProperty(propData, IDAO.PROPERTY_ID_NAME));
                    }
                    catch (Exception e)
                    {
                        value.setObject(e);
                    }
                }

                // Verifica se a propriedade tem um �ndice definido. Caso tenha,
                // verifica se o indice da propriedade no array est� ocupado
                // se estiver pega o elemento do array e joga para a ultima
                // posi��o vazio do array e coloca a propriedade na posi��o do
                // indice definido. Se a propriedade 
                //Tem um indice definido
                if (prop.getIndex()!= -1)
                {
                    // J� existe um valor na posi��o do array para esse indice
                    // ent�o retira o valor e coloca-o numa posi��o vazia
                    if (properties[prop.getIndex()] != null)
                    {
                        int index = ArrayUtils.findFirstEmpty(properties);
                        properties[index] = properties[prop.getIndex()];
                        values[index] = values[prop.getIndex()];
                    }
                    // Grava a prop na posi��o do indice.
                    properties[prop.getIndex()]=prop;
                    values[prop.getIndex()] = value;
                }else
                {
                    // Se nenhum indice existir simplesmente joga o valor numa 
                    // posi��o vazia
                    int index = ArrayUtils.findFirstEmpty(properties);
                    properties[index] = prop;
                    values[index] = value;
                }
//                // Verifica se a propriedade tem um �ndice definido 
//                // Adiciona a propriedade na lista no �ndice indicado
//                // Caso haja conflito de indices, para evitar exce��es efetua uma busca
//                // de um lugar vago no array.
//                
//                if ((prop.getIndex()== -1)||(properties[prop.getIndex()] == null))
//                {
//                    // N�o est� DEFINIDO ou est� em CONFLITO
//                    int index = ArrayUtils.findLastEmpty(properties);
//                    properties[index] = prop;
//                    values[index] = value;
//                }
//                else
//                {
//                    // Est� DEFINIDO SEM CONFLITO
//                    properties[prop.getIndex()] = prop;
//                    values[prop.getIndex()] = value;
//                }
            }
            
            // Indica preparedForEdit para true
            preparedForView = true;
        }
    }
    
    private void prepareForEdit() throws Exception
    {
//        System.out.println("EntityProperties.prepareForEdit:");

        // Verifica se estah preparado para VIEW, sen�o PREPARA
        if(!preparedForView) prepareForView();

        // Verifica j� estah preparado para EDIT, se j� estiver N�o faz nada
        if(!preparedForEdit)
        {
            // Prepara a classe que fornecer� as informa��es dos campos
            EntityInfo entityInfo = new EntityInfo(ownerEntity.getEntityClass());

            for(int i = 0; i <properties.length; i++)
            {
                 EntityPropertyInfo prop = properties[i];
                
                 prop.setRequired(entityInfo.getRequired(prop.getName()));
                 prop.setReadOnly(entityInfo.getReadOnly(prop.getName()));
                 prop.setMinimum(entityInfo.getMinimum(prop.getName()));
                 prop.setMaximum(entityInfo.getMaximum(prop.getName()));
                 prop.setEditMask(entityInfo.getEditMask(prop.getName()));
                 
                 // Verifica se a propriedade � uma lista a ser exibida como um ComboBox
                 prop.setEditShowList(entityInfo.getEditShowList(prop.getName()));

                 /*
                  * Caso a propriedade deva ser exibida como um comboBox, verifica se
                  * a lista est� disponivel no arquivo de propriedades. Caso esteja
                  * busca a lista, caso n�o esteja, busca no dao.
                  */
                 if (prop.isEditShowList())
                 {
                     List<SelectItem> list = entityInfo.getEditList(prop.getName());
                     if (list != null)
                     {
                         // Pega a lista do arquivo de configura��es
                         prop.setEditList(list);
                         
                         // Verifica se n�o for requirido insere um elemento em branco para escolha
                         if (!prop.isRequired())
                             prop.getEditList().add(0, new SelectItem("", ""));

                         prop.setListFromFile(true);
                     } else 
                     {
                         // Pega a lista do DAO
                         prop.setListFromFile(false);
                         
//                         ICrudService service = (ICrudService) app.getServiceByName(ICrudService.SERVICE_NAME);
//                         List objectList = service.getList(prop.getType());

                         // TODO IMPLEMENTAR ou achar uma fun��o que converta uma List e uma List<SelectItem>
//                         prop.setEditList(EntityUtils.convertObjectListToSelectItemList(objectList));
                         
                         // Verifica se n�o for requerido insere um elemento em branco para escolha
                         if (!prop.isRequired())
                             prop.getEditList().add(0, new SelectItem(IDAO.ENTITY_UNSAVED, ""));
                     }
                 }
            }
            
            // Indica preparedForEdit para true
            preparedForEdit = true;
        }
        
    }

    public List<EntityPropertyInfo> getListEdit() throws Exception
    {
//        System.out.println("EntityProperties.getListEdit:");

        /* Verifica se as propriedades est�o atualmente preparadas
         * para Edi��o. Se n�o estiverem, prepara-as.
         */
        if(!preparedForEdit) prepareForEdit();
            
        
        List<EntityPropertyInfo> result = new ArrayList<EntityPropertyInfo>(properties.length);
        
        for(int i=0; i<properties.length; i++)
            result.add(properties[i]); 
        return result;
    }

    public EntityPropertyInfo[] getProperties() throws Exception
    {
//        System.out.println("EntityProperties.getProperties:" + this.properties);

        /* Verifica se as propriedades est�o atualmente preparadas
         * para visualiza��o. Se n�o estiverem, prepara-as.
         */
        if(!preparedForView) prepareForView();
            
        return properties;
    }
 
    public IServiceManager getApp() {
        return app;
    }
    
    public EntityData getOwnerEntity() {
        return ownerEntity;
    }
    
    public EntityPropertyValue[] getValuesView() throws Exception {
//        System.out.println("EntityProperties.getValuesView:");

        /* Verifica se os valores est�o atualmente preparados
         * para VISUALIZA��O. Se n�o estiverem, prepara-os.
         */
        if(!preparedForView) prepareForView();
            
        return values;
    }
    
    public EntityPropertyValue[] getValuesEdit() throws Exception {
        /* Verifica se os valores est�o atualmente preparados
         * para EDI��O. Se n�o estiverem, prepara-os.
         */
        if(!preparedForEdit) prepareForEdit();
            
        return values;
    }
    
    public void save() throws Exception
    {
//      System.out.println("EntityProperties.save: " + ownerEntity.getClassName() + ":" + ownerEntity.getData());

//      // Obs.: Existe uma sess�o 'mantida', assim o proxies s�o todos v�lidos
//      // para acessar as subCole��es e subClasses da classe: objectData
//      
        // Popular a classe (Nova ou do Retrieve)
      EntityPropertyInfo prop; 
      EntityPropertyValue value; 
      for (int i = 0; i < properties.length; i++)
      {
          prop = properties[i];
          value = values[i];
//          System.out.print(i + ": property: " + prop.getName());
//          System.out.println(", propertyValue: " + value.getValue());
//          if ((!entityBean.getEntity().getProperties()[i].isSubClass())
//                  && (!entityBean.getEntity().getProperties()[i].isList()))
//          {
//              propertyValue = entityBean.getEntity().getProperties()[i]
//                      .getValue();
//
//          } else
//          {
              if (prop.isSubClass())
              {
                  long id = value.getId();
                  if (id != IDAO.ENTITY_UNSAVED)
                  {
//                      ICrudService service = (ICrudService) app.getServiceByName(ICrudService.SERVICE_NAME);
//                      value.setObject(service.retrieve(id, properties[i].getType()));
                  }
              }
              if (prop.isList())
              {
              } 
                  
              PropertyUtils.setProperty(ownerEntity.getData(), prop.getName(), value.getObject());

      }

//      ICrudService service = (ICrudService) app.getServiceByName(ICrudService.SERVICE_NAME);
//      service.update(ownerEntity.getData());

      // se for do tipo primitivo: com os dados
      // entityBean.entity.properties[n].value
      // entityBean.entity.data('nome propriedade') = {novo valor}
      // se for do tipo subClass:
      // Verifica se o properties[n].id � !=-1, o que indica que foi alterado
      // ent�o
      // obtem (retrive) o objeto do banco e executa um
      // setProperty(retrivedObj).
      // Obs.: Utilizar as rotinas de BeansUtils e PropertiesUtils
      // Executar alguma valida��o de neg�cio (DEPOIS IMPLEMENTA ISTO)
      // Persiste a entidade
      // Remove a entidade da lista de classes em edi��o
      
  }
    
}
