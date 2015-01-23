/*
 /*
 * MyPetStore Project.
 */
package br.com.orionsoft.monstrengo.view.jsf.util;

import java.net.InetAddress;
import java.util.Iterator;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.exception.MessageList;

/**
 * Utility class for JavaServer Faces.
 * 
 * @author <a href="mailto:derek@derekshen.com">Derek Y. Shen</a>
 */
public class FacesUtils
{
    
    /** Define a view JSF que é ativada para uma mensagem de sucesso PADRÃO */
    public static final String FACES_VIEW_SUCCESS = "/public/basic/success";

    /** Define a view JSF que é ativada para uma mensagem de erro PADRÃO */
    public static final String FACES_VIEW_FAILURE = "/public/basic/failure";
    
    /** Define a view JSF que é ativada para abrir uma visão que fechará a atual janela do browser */
    public static final String FACES_VIEW_CLOSE = "/public/basic/close?faces-redirect=true";

	/**
     * Este método obtem a atual requisição do Faces.
     * @return
     */
    public static HttpServletRequest getRequest()
    {
    	return (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    }

    /**
     * Este método obtem os parâmetros passados pela atual requisição.
     * Todos os parâmetros, inclusive os de controle do JSF estarão neste Map.
     * o ParameterMap. 
     * @return
     */
    public static Map getRequestParams()
    {
        return FacesContext.getCurrentInstance()
        .getExternalContext().getRequestParameterMap();
    }
    
    /**
     * Este método verifica se um determinado parâmetro foi passado para a requisição.
     * @return
     */
    public static boolean checkRequestParam(String paramName)
    {
        return getRequestParams().containsKey(paramName);
    }
    
    /**
     * Este método obtem um parâmetro específico pela atual requisição.
     * @return
     */
    public static String getRequestParam(String paramName)
    {
        return getRequestParams().get(paramName).toString();
    }
    
    /**
     * Get servlet context.
     * 
     * @return the servlet context
     */
    public static ServletContext getServletContext()
    {
        return (ServletContext) FacesContext.getCurrentInstance()
                .getExternalContext().getContext();
    }

    /**
     * Adiciona uma mensagem de saída para ser renderizada pelo Faces na página.
     * @param msg
     */
    public static void addMsg(String msg)
    {
        addInfoMsg(msg);
     
    }
    
    /**
     * Adiciona uma mensagem de informação saída para ser renderizada pelo Faces na página.
     * @param msg
     */
    public static void addInfoMsg(String msg)
    {
        FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO, msg, msg);
     
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
     
    }
    
    /**
     * Adiciona uma mensagem de informação saída para ser renderizada pelo Faces na página.
     * @param msg
     */
    public static void addErrorMsg(String msg)
    {
        FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg);
     
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
    }
    
    /**
     * Adiciona uma mensagem de erro saída para ser renderizada pelo Faces na página.
     * @param msg
     */
    public static void addErrorMsgs(MessageList errorList)
    {
        
        // Adiciona todas as mensagem para serem exibidas no Faces
        for (BusinessMessage e: errorList)
        {
            // Define a mensagem SUMMARY e DETAIL
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessageClass().getSimpleName() + ":" + e.getErrorKey() + "=" + e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
        }
     
    }
    
    /**
     * Adiciona várias mensagens de informações para serem renderizadas pelo Faces na página.
     * @param msg
     */
    public static void addInfoMsgs(MessageList errorList)
    {
        // Adiciona todas as mensagem para serem exibidas no Faces
        for (BusinessMessage e: errorList)
        {
            // Define a mensagem SUMMARY e DETAIL
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO, e.getMessage(), e.getMessageClass().getSimpleName() + ":" + e.getErrorKey() + "=" + e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
        }
    }
    
    public static String getMessages(){
    	String result = "";
    	
    	for(Iterator<FacesMessage> it = FacesContext.getCurrentInstance().getMessages(); it.hasNext();){
    		result += it.next().getSummary() + "\n";
    	}
    	
    	return result;
    }
    
    public static boolean isNotNull(Object obj)
    {
        if ((obj != null) && (!obj.equals("")) && (!obj.equals("null")) && (!obj.equals("NULL")))
        {
            return true;
        }
        return false;
    }
    
    /**
     * Tenta obter o nome do host da atual requisição.
     * Converte o IP do host em um vetor de Byte (com sinal).
     * Depois usa a função InetAddress.getByAddress(byte[]) para tentar
     * traduzir o nome.
     * 
     * @return O nome do host ou somente o endereço IP
     */
    public static String getRequestHostName() {
    	/* Obtem o host que originou a requisição. Ex: 10.0.0.1 */
    	String name = getRequest().getRemoteHost();

    	/* Quebra o endereço do host em um conjunto de bytes */
    	try {
    		String[] add = StringUtils.split(name, ".");
    		byte[] addByte = new byte[]{new Integer(add[0]).byteValue(),
    				new Integer(add[1]).byteValue(),
    				new Integer(add[2]).byteValue(),
    				new Integer(add[3]).byteValue()};
    		/* Tenta traduzir o endereço em nome do host. 
    		 * Se o IneAddess não conseguir ele retorna o ip mesmo.*/
    		return InetAddress.getByAddress(addByte).getHostName();
    	} catch (Exception e) {
    		/* Se ocorrer uma exceção, o ip é retornado */
    		return name;
    	}
    }
    
//    /**
//     * Get managed bean based on the bean name.
//     * 
//     * @param beanName
//     *            the bean name
//     * @return the managed bean associated with the bean name
//     */
//    public static Object getManagedBean(String beanName)
//    {
//        Object o = getValueBinding(getJsfEl(beanName)).getValue(
//                FacesContext.getCurrentInstance());
//        return o;
//    }
//
//    /**
//     * Remove the managed bean based on the bean name.
//     * 
//     * @param beanName
//     *            the bean name of the managed bean to be removed
//     */
//    public static void resetManagedBean(String beanName)
//    {
//        getValueBinding(getJsfEl(beanName)).setValue(
//                FacesContext.getCurrentInstance(), null);
//    }
//
//    /**
//     * Store the managed bean inside the session scope.
//     * 
//     * @param beanName
//     *            the name of the managed bean to be stored
//     * @param managedBean
//     *            the managed bean to be stored
//     */
//    public static void setManagedBeanInSession(String beanName,
//            Object managedBean)
//    {
//        FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
//                .put(beanName, managedBean);
//    }
//
//    /**
//     * Get parameter value from request scope.
//     * 
//     * @param name
//     *            the name of the parameter
//     * @return the parameter value
//     */
//    public static String getRequestParameter(String name)
//    {
//        return (String) FacesContext.getCurrentInstance().getExternalContext()
//                .getRequestParameterMap().get(name);
//    }
//
//    /**
//     * Get <code>ApplicationBean</code>.
//     * <p>
//     * Specific for this application.
//     * 
//     * @return the application bean
//     */
//    public static ApplicationBean getApplicationBean()
//    {
//        return (ApplicationBean) getManagedBean(BeanNames.APPLICATION_BEAN);
//    }
//
//    /**
//     * Get <code>SessionBean</code>.
//     * <p>
//     * Specific for this applicaiton.
//     * 
//     * @return the session bean
//     */
//    public static SessionBean getSessionBean()
//    {
//        return (SessionBean) getManagedBean(BeanNames.SESSION_BEAN);
//    }
//
//    /**
//     * Add information message.
//     * 
//     * @param msg
//     *            the information message
//     */
//    public static void addInfoMessage(String msg)
//    {
//        addInfoMessage(null, msg);
//    }
//
//    /**
//     * Add information message to a sepcific client.
//     * 
//     * @param clientId
//     *            the client id
//     * @param msg
//     *            the information message
//     */
//    public static void addInfoMessage(String clientId, String msg)
//    {
//        FacesContext.getCurrentInstance().addMessage(clientId,
//                new FacesMessage(FacesMessage.SEVERITY_INFO, msg, msg));
//    }
//
//    /**
//     * Add error message.
//     * 
//     * @param msg
//     *            the error message
//     */
//    public static void addErrorMessage(String msg)
//    {
//        addErrorMessage(null, msg);
//    }
//
//    /**
//     * Add error message to a sepcific client.
//     * 
//     * @param clientId
//     *            the client id
//     * @param msg
//     *            the error message
//     */
//    public static void addErrorMessage(String clientId, String msg)
//    {
//        FacesContext.getCurrentInstance().addMessage(clientId,
//                new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg));
//    }
//
//    /**
//     * Evaluate the integer value of a JSF expression.
//     * 
//     * @param el
//     *            the JSF expression
//     * @return the integer value associated with the JSF expression
//     */
//    public static Integer evalInt(String el)
//    {
//        if (el == null)
//        {
//            return null;
//        }
//        if (UIComponentTag.isValueReference(el))
//        {
//            Object value = getElValue(el);
//            if (value == null)
//            {
//                return null;
//            } else if (value instanceof Integer)
//            {
//                return (Integer) value;
//            } else
//            {
//                return new Integer(value.toString());
//            }
//        } else
//        {
//            return new Integer(el);
//        }
//    }
//
//    /**
//     * Set request attribute.
//     * 
//     * @param attName
//     *            the attribute name
//     * @param attValue
//     *            the attribute value
//     */
//    public static void setRequestAttribute(String attName, Object attValue)
//    {
//        getServletRequest().setAttribute(attName, attValue);
//    }
//
//    public static Object getSessionAttribute(String attName)
//    {
//        return getServletRequest().getSession().getAttribute(attName);
//    }
//
//    public static void removeSessionAttribute(String attName)
//    {
//        getServletRequest().getSession().removeAttribute(attName);
//    }
//
//    public static HttpServletResponse getServletResponse()
//    {
//        return (HttpServletResponse) FacesContext.getCurrentInstance()
//                .getExternalContext().getResponse();
//    }
//
//    private static ServiceManager getApplication()
//    {
//        ApplicationFactory appFactory = (ApplicationFactory) FactoryFinder
//                .getFactory(FactoryFinder.APPLICATION_FACTORY);
//        return appFactory.getApplication();
//    }
//
//    private static ValueBinding getValueBinding(String el)
//    {
//        return getApplication().createValueBinding(el);
//    }
//
//    private static HttpServletRequest getServletRequest()
//    {
//        return (HttpServletRequest) FacesContext.getCurrentInstance()
//                .getExternalContext().getRequest();
//    }
//
//    private static Object getElValue(String el)
//    {
//        return getValueBinding(el).getValue(FacesContext.getCurrentInstance());
//    }
//
//    private static String getJsfEl(String value)
//    {
//        return "#{" + value + "}";
//    }
}
