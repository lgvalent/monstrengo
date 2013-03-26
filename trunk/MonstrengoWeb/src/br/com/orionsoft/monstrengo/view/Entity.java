package br.com.orionsoft.monstrengo.view;

import javax.faces.event.ValueChangeEvent;

/**
 * Created on 01/04/2005
 * @author Marcia
 */
public class Entity
{
    private String entidade = "br.com.orionsoft.basic.entities.endereco.Endereco";
    private br.com.orionsoft.monstrengo.view.EntityPropertyInfo[] fields;
    
    public br.com.orionsoft.monstrengo.view.EntityPropertyInfo[] getFields()
    {
        return fields;
    }
    

    public void setFields(br.com.orionsoft.monstrengo.view.EntityPropertyInfo[] fields)
    {
        this.fields = fields;
    }

    public String getEntidade()
    {
        return entidade;
    }
    

    public void setEntidade(String entidade)
    {
//        System.out.println("setEntidade:" + entidade);
        if (!this.entidade.equals(entidade))
        {
            this.entidade = entidade;
            setInit(entidade);
        }
    }
   
    public void setInit(String str){
//        System.out.println("EntityData.setInit:" + str);

        /* Verifica os parâmetros da página para preparar a entidade */
//        Map m = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
//        Object o = m.get("entidade");
        if (str != null){
            try
            {
//                setEntidade(o.toString());
                prepararEntidade();
            }
            catch (SecurityException e)
            {
                e.printStackTrace();
            }
            catch (ClassNotFoundException e)
            {
                e.printStackTrace();
            }
       
        }
    }
    
    public String prepararEntidade() throws SecurityException, ClassNotFoundException
    {
        
//        System.out.println("prepareEntidade: ");
//        System.out.println("Entidade= "+getEntidade());

        java.lang.reflect.Field[] field = Class.forName(getEntidade()).getDeclaredFields();
//        System.out.println("Número de campos declarados:" + field.length);
        
        fields = new br.com.orionsoft.monstrengo.view.EntityPropertyInfo[field.length];
        for (int i = 0; i < field.length; i++)
        {
            br.com.orionsoft.monstrengo.view.EntityPropertyInfo f = new EntityPropertyInfo();
            f.setName(field[i].getName());
            f.setHint("O hint de '" + field[i].getName() + "'");
            f.setType(field[i].getType());
       //     f.setRequired(false);
           
            fields[i] = f;
        }
        
        return "cadastro";
    }
    
    public void processValueChanged(ValueChangeEvent event)
    {
//        System.out.println("-----------------"+event.getComponent().getClass());
    }
}
