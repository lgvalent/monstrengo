package br.com.orionsoft.monstrengo.security.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

public class AuthenticationFilter implements Filter
{
    private Logger log = Logger.getLogger(this.getClass());
	/**
     * Este parâmetro deve ser passado para o filtro quando o mesmo é 
     * configurado no arquivo web.xml da aplicação.
     *  Se o filtro for ativado, detectando que a página deverá ser
     *  redirecionada para autenticação do usuário, o filtro usará
     *  o valor definido neste parâmetros para redirecionar a página.<br>
     *  Exemplo:<br>
     *  <filter>
	 *	 <filter-name>FilterName</filter-name>
	 *	 <filter-class>package.FilterImplementation</filter-class>
	 * 	 <init-param>
	 *		  <param-name>redirect</param-name>
	 *		  <param-value>../login.jsp</param-value>
	 *	 </init-param>
	 *  </filter>
	 *  <filter-mapping>
	 *  	<filter-name>Authentication</filter-name>
	 *  	<url-pattern>/faces/pages/*</url-pattern>
	 *  </filter-mapping>
     */
	public static final String REDIRECT_FILTER_PARAM = "redirect";
	
	/**
	 * Nome do parâmetro usado para marcar uma sessão como autenticada.
	 * Este parâmetro da sessão não é utilizado por mais ninguém. 
	 */
	public static final String AUTHENTICATED_SESSION_PARAM = "visit";
    
	/**
	 * Nome do parâmetro usado para marcar uma sessão como autenticada.
	 * Este parâmetro da sessão não é utilizado por mais ninguém. 
	 */
	public static final String LOCAL_BEFORE_AUTHENTICATE_PARAM = "lbap";
	
	private FilterConfig filterConfig;
    
    public void init(FilterConfig config)
    {
        this.filterConfig = config;
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException
    {
        log.debug("Filtrando requisição de URL de entrada");
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        HttpServletResponse httpResponse = (HttpServletResponse)response;
        HttpSession session = httpRequest.getSession();
        
        log.debug("Verificando se a sessão já foi autenticada");
        String visit = (String)session.getAttribute(AUTHENTICATED_SESSION_PARAM);
        
        if (visit == null)
        {
            log.debug("Sessão ainda não autenticada");
            if(log.isDebugEnabled())
            	log.debug("Armazenando o local original da requisição: ../.." + httpRequest.getPathInfo() + "?" + httpRequest.getQueryString());
        	session.setAttribute(LOCAL_BEFORE_AUTHENTICATE_PARAM, "../.." + httpRequest.getPathInfo() + (httpRequest.getQueryString()==null?"":new String("?" + httpRequest.getQueryString())));
        	
        	/* Exibe as informações de URL numa caixa de diálogo */
//        	JOptionPane.showMessageDialog(null,"-PI"+ httpRequest.getPathInfo() + "-CP" + httpRequest.getContextPath() + "-PT" + httpRequest.getPathTranslated() + "- QR" + httpRequest.getQueryString() + "-RU" + httpRequest.getRequestURI() );
            
        	log.debug("Redirecionando a requisição não autenticada para a página que definida no arquivo web.xml no param-filter");
            httpResponse.sendRedirect(filterConfig.getInitParameter(REDIRECT_FILTER_PARAM));
            return;
        }
        log.debug("Sessão já autenticada. Continuando a requisição no encanamento de filtros");
        chain.doFilter(request, response);
    }
    

    public void destroy()
    {
        this.filterConfig = null;
    }
    
}
