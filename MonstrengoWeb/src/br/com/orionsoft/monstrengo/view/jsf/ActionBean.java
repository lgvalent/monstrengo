package br.com.orionsoft.monstrengo.view.jsf;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.view.jsf.bean.BeanRequestBasic;

/**
 * bean em construção. Era um protótipo de centralizador das ações do sistemas
 * 
 * _@jsf.bean name="actionBean" scope="session" 
 */
public class ActionBean extends BeanRequestBasic
{
    private String module;
    private String action;
    
    public String getAction()
    {
        return action;
    }
    
    public String getModule()
    {
        return module;
    }
    
    public String loadCurrentModule(){
//        System.out.println("ActionBean.load:" + module);
//        if (module == "list") {
//            list Bean = Faces.getContext.getBean("listbean");
//            bean.prepare(param);
//            return bean.moduleName;
//        }
        return null;
    }

    public String load(){
        
//        System.out.println("ActionBean.load:" + module);
        return module;
    }

	public void loadEntityParams() throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void doReset() throws BusinessException, Exception {
		// TODO Auto-generated method stub
		
	}

	public void doReload() throws BusinessException, Exception {
		// TODO Auto-generated method stub
		
	}
}