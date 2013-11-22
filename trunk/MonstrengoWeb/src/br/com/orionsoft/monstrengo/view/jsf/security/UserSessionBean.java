package br.com.orionsoft.monstrengo.view.jsf.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;
import br.com.orionsoft.monstrengo.security.entities.UserSession;
import br.com.orionsoft.monstrengo.security.processes.AuthenticateProcess;
import br.com.orionsoft.monstrengo.security.web.AuthenticationFilter;
import br.com.orionsoft.monstrengo.view.MenuBean;
import br.com.orionsoft.monstrengo.view.jsf.bean.ApplicationBean;
import br.com.orionsoft.monstrengo.view.jsf.bean.BasicBean;
import br.com.orionsoft.monstrengo.view.jsf.bean.IBasicBean;
import br.com.orionsoft.monstrengo.view.jsf.bean.IRunnableProcessView;
import br.com.orionsoft.monstrengo.view.jsf.util.FacesUtils;

/**
 * Bean que controla o operador que está atualmente autenticado no sistema.
 * Os outros beans que precisam disparar processos e consequentemente identificar
 * o operador, precisam de uma referência para este bean.
 * Além disso, este bean fornece as rotinas de login e logoff do sistema.
 * 
 * @jsf.bean name="userSessionBean" scope="session"
 * @jsf.navigation from="*" result="login" to="/login/login.jsp"
 */
@ManagedBean(name="userSessionBean")
@SessionScoped
public class UserSessionBean
{
    /** Define a view JSF do login do operador */
	public static final String FACES_VIEW_LOGIN = "/public/login/login?faces-redirect=true";

	@ManagedProperty(value="#{applicationBean}")
	private ApplicationBean applicationBean;
	
	public ApplicationBean getApplicationBean() {
		return applicationBean;
	}

	public void setApplicationBean(ApplicationBean applicationBearn) {
		this.applicationBean = applicationBearn;
	}

	private String login;
    private String password;    
    
    private Logger log = Logger.getLogger(getClass()); 
    
    private UserSession userSession = null;
    
    private String host = "";

	private final Map<String, IRunnableProcessView> runnableEntityProcessViews = new HashMap<String, IRunnableProcessView>();

	private final Map<String, IBasicBean> views = new HashMap<String, IBasicBean>();
	
	public UserSessionBean(){
        // Identificar o atual terminal do usuário
        this.host = FacesUtils.getRequestHostName();
    }
    
    public String doLogin()
    {
        AuthenticateProcess authProcess = null;
        String result = null;

        try
        {
            // Criar um processo de autenticação
            authProcess = (AuthenticateProcess)this.getApplicationBean().getProcessManager().createProcessByName(AuthenticateProcess.PROCESS_NAME, null);
            
            // Validar o login e password pelo processo
            authProcess.setLogin(this.login);
            authProcess.setPassword(this.password);
            authProcess.setTerminal(this.host);
            
            /* Verifica se está acontecendo uma autenticação de uma
             * credencial e não precisa utilizar a senha
             */
           	authProcess.setCheckPassword(!authenticatingCredential);
            
            if (authProcess.runAuthenticate())
            {
                // Obter o UserSession validado e armazená-lo
                this.userSession = authProcess.getUserSession();

                // Define a sessão do usuário como válida para o 
                // filtro web
                HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
                request.getSession().setAttribute(AuthenticationFilter.AUTHENTICATED_SESSION_PARAM, "");

                // Definir o fluxo de tela de SUCESSO
                /* Lucio - 16/09/2007: Definindo a URL original antes da necessidade de autenticação.
                 * Se esta URL não estiver armazenada como um atributo da atual sessão HTTP do usuário
                 * então redireciona para a visão faces MenuBean.FACES_VIEW_MENU.
                 */
                Object localBeforeAuthenticateParam = request.getSession().getAttribute(AuthenticationFilter.LOCAL_BEFORE_AUTHENTICATE_PARAM);
                if(localBeforeAuthenticateParam != null){
                	try
					{
            			HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
                		response.sendRedirect(localBeforeAuthenticateParam.toString());
                    	FacesContext.getCurrentInstance().responseComplete();
                    	return "";
					} catch (IOException e)
					{
						/* Caso ocorra algum erro, desvia para uma tela com a mensagem */
						FacesUtils.addErrorMsg(e.getMessage());
	                	result = FacesUtils.FACES_VIEW_FAILURE;
					}

                }else{
                	/* Redirecionando para o menu */
                	result = MenuBean.FACES_VIEW_INDEX;
                }
            }
            else
                throw new ProcessException(authProcess.getMessageList());
            
        }catch(ProcessException e)
        {
            // Adiciona as mensagens de erro no Faces
            FacesUtils.addErrorMsgs(e.getErrorList());
            
            // Exibe o erro na mesma página
            result = "";
        }
        finally
        {
            if (authProcess != null)
            {
                try {
					authProcess.finish();
				} catch (ProcessException e) {
		            // Adiciona as mensagens de erro no Faces
		            FacesUtils.addErrorMsgs(e.getErrorList());
				}
                authProcess = null;
            }
        }
        return result;
    }

    /* Este flag permite indicar ao método doLogin() que
     * a autenticação atual é de uma credencial e ele não precisa
     * verificar a SENHA */
    private boolean authenticatingCredential = false;
    public boolean doLogin(ApplicationUser user){
        boolean result = false;
    	try
        {
            /* Ativa autenticação de credencial sem a senha */
        	authenticatingCredential = true;
            
            // Validar o login e password pelo processo
            this.login = user.getLogin();
            this.password = user.getPassword();
            
            /* Verifica se houve algum erro no login */
            if(this.doLogin().equals(""))
            	throw new RuntimeException(FacesUtils.getMessages());
        }
        finally
        {
            /* Garante a desativação da autenticação de credencial sem a senha */
            authenticatingCredential = false;
        }
        
        return result;
    }

    public String doLogoff()
    {
        String result = null;

        // Limpar os atuais dados
        this.login = "";
        this.password = "";
        
        // Indica que a sessão não está válida
        // Finalizar a UserSession
        this.userSession = null;
        
        // Invalida a sessão corrente
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        request.getSession().invalidate();
        
        // Definir o fluxo de tela de SUCESSO
        result = FACES_VIEW_LOGIN;
        
        return result;
    }

    public String getLogin(){return login;}
    public void setLogin(String login){this.login = login;}

    public String getPassword(){return password;}
    public void setPassword(String password){this.password = password;}

	public String getHost(){return host;}
	public void setHost(String host){this.host = host;}

    public UserSession getUserSession()
    {
        return userSession;
    }

    public boolean isLogged()
    {
        return userSession!=null;
    }

	public Map<?, ?> getRequestParams() {
		// TODO Auto-generated method stub
		return null;
	}

	public void doReset() throws BusinessException, Exception {
		// TODO Auto-generated method stub
		
	}

	public void doReload() throws BusinessException, Exception {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Controla a lista de visões que implementam a interface {@link IRunnableProcessView}.<br>
	 * Esta lista é montada durante o método {@link UserSessionBean.registerView()}. O método
	 * separa as visões comuns das visões 'Runnable'. Assim, é possível obter esta lista para
	 * verificar os processos manipulados pelas visões e verificar o direito do atual operador
	 * sobre os processos.  
	 * @return
	 * @deprecated Deve ser usado o getRunnableEntityProcessViewByName, pois a manutenção da lista de visões ainda está indefinida
	 */
	public Map<String, IRunnableProcessView> getRunnableEntityProcessViews() {return runnableEntityProcessViews;}
	
	/**
	 * Este método obtém a visão que controla o processo solicitado.
	 * 
	 * Converte o nome do processo no nome da visão correspondente.
	 * Para isto, um padrão de  correspondência foi estabelecido:
	 * MyPersonalProcess -> myPersonalBean
	 * - A primeira letra do nome do processo minúscula;
	 * - O sufixo 'Process' é substituído por 'Bean';
	 * Uma visão no faces-config.xml com o nome correspondente
	 * deverá estar devidamente configurada.
	 * TODO APERFEIÇOAMENTO O ideal é que todos os beans de visão tivesse o sufixo View e não Bean, pois Bean é muito usado no Hibernate e Jasper, para dados e não controladores de visão! Lucio 24/06/2008. 
	 * @return
	 */
	public IRunnableProcessView getRunnableEntityProcessViewByProcessName(String processName) {
		/* Pega o nome original: MyPersonalProcess */
		String viewName = processName;
		/* Uncapitalize: myPersonalProcess*/
		viewName = StringUtils.uncapitalize(viewName);
		/* Substitui o Process por View */
		viewName = StringUtils.replace(viewName, "Process", "Bean");
		
		if(log.isDebugEnabled())
			log.debug("getRunnableEntityProcessViewByProcessName(" + processName + ")=" +viewName);

		return this.getRunnableEntityProcessViewByName(viewName);
	}
	
	/**
	 * Este método obtém o objeto da visão pelo nome da mesma.
	 * @return
	 */
	public IRunnableProcessView getRunnableEntityProcessViewByName(String viewName) {
		FacesContext context = FacesContext.getCurrentInstance();
		/* Lucio 2011-11-29: Usando o novo método ELContext, pois ValueBinding foi deprecado */
		
//		ValueExpression value = ExpressionFactory.newInstance().createValueExpression(context.getELContext(), "#{" + viewName+ "}", IRunnableProcessView.class);
//		return (IRunnableProcessView) value.getValue(context.getELContext());
		return context.getApplication().evaluateExpressionGet(context, "#{" + viewName+ "}", IRunnableProcessView.class);
		
	}
	/**
	 *	 
	 * @deprecated metodo depreciado, pois a manutenção da lista de visões ainda está indefinida
	 */
	public Map<String, IBasicBean> getViews() {return views;}
	/**
	 *	 
	 * @deprecated metodo depreciado, pois a manutenção da lista de visões ainda está indefinida
	 */
	
	public void registerView(IBasicBean view) throws BusinessException{
		if(log.isDebugEnabled())
			log.debug("Registrando visão: " + view.getViewName() + "(" + view.getClass().getName() + ")");
	
		final String viewName = view.getViewName();
		
	    if (views.containsKey(viewName))
	    {
	        throw new BusinessException(MessageList.create(ApplicationBean.class, "DUPLICATED_VIEW", viewName, view.getClass().getName(), views.get(viewName).getClass().getName()));
	    }
	    
		/* Verifica se a visão não implementa o recurso de identificador de visão ainda */
	    if(!view.getViewName().equals(BasicBean.VIEW_NAME_NOT_IMPLEMENTED))
	    	views.put(view.getViewName(), view);
	    
		/* Verifica se a visão é IRunnableProcess para registra-la separadamente
		 * pelo nome do processo que ela manipula. Em caso de duplicidade 
		 * SOMENTE um warning será disparado e a última visão registrada será
		 * utilizada */
	    if(view instanceof IRunnableProcessView){
	    	IRunnableProcessView runnableView = (IRunnableProcessView) view;
	    	
	    	if(log.isDebugEnabled())
	    		log.debug("A visão: " + view.getViewName() + "(" + view.getClass().getName() + ") foi identifica como uma visão IRunnableProcessView e será registrada para execução do processo " + runnableView.getRunnableEntityProcessName());
	    	
	    	/* Verifica se já tem uma visão controladora deste processo para emitir um WARNING */
	    	if(runnableEntityProcessViews.containsKey(runnableView.getRunnableEntityProcessName())){
	    		if(log.isInfoEnabled())
	        		log.info("A visão: " + view.getViewName() + "(" + view.getClass().getName() + ") que manipula  o processo " + runnableView.getRunnableEntityProcessName() + " será registrada sobre a classe " + runnableEntityProcessViews.get(runnableView.getRunnableEntityProcessName()).getClass().getName() + " que foi registrada anteriormente para o mesmo processo.");
	    	}
	    	
	    	/* Registra a visão para o processo que ela controla */
	    	runnableEntityProcessViews.put(runnableView.getRunnableEntityProcessName(), runnableView);
	    }
	    
	}
	/**
	 *		 
	 * @deprecated metodo depreciado, pois a manutenção da lista de visões ainda está indefinida
	 */
	public void unregisterView(IBasicBean view) throws BusinessException{
		if(log.isDebugEnabled())
			log.debug("Desregistrando visão: " + view.getViewName() + "(" + view.getClass().getName() + ")");
	
		final String viewName = view.getViewName(); 
	    if (!views.containsKey(viewName))
	    {
	        throw new BusinessException(MessageList.create(ApplicationBean.class, "VIEW_NOT_FOUND", viewName));
	    }
	    
	    views.remove(view.getViewName());
	}

}
