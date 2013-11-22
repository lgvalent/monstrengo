<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>

<f:subview id="header">
  <h:panelGrid columns="2" width="100%">
   <h:graphicImage value="../../public/basic/img/header.gif" title="Sistema OrionSoft" />
 <h:form>
   <h:panelGrid columns="1" border="0" width="100%" style="text-align:right; vertical-align:center;">
   		<h:outputText value="Sua esta&ccedil;&atilde;o foi identificada como: <b>#{userSessionBean.host}</b>" escape="false"/>
	    <h:panelGroup rendered="#{userSessionBean.logged}">
    	    <h:outputText value="Operador autenticado: <b>#{userSessionBean.userSession.user.propertiesMap.name.value.asString}</b> " escape="false" />
			<h:outputLink value="javascript:popupPage('../basic/securityChangePassword.jsp', 550,400)" rendered="#{menuBean.processMap.ChangePasswordProcess}" accesskey="a">
	    	    <h:outputText value="[alterar senha]"/>
		  	</h:outputLink>
    	    <h:commandLink action="#{menuBean.doRefreshEntitiesMetadata}" immediate="true">
	    	    <h:outputText value="[refresh]"/>
    	    </h:commandLink>
    	    <h:commandLink action="#{userSessionBean.doLogoff}" immediate="true" accesskey="s" onmouseup="javascript:closeAllWindows()">
	    	    <h:outputText value="[sair]"/>
    	    </h:commandLink>
	    </h:panelGroup>
	    
	    <h:panelGrid columns="1" rendered="#{not userSessionBean.logged}" width="100%" style="text-align:right; color:red">
    	    <h:outputText value="Usuário não autenticado."/>
 	    	<f:verbatim >	
		    	<a href="../../login/login.jsp" title="Clique aqui para sair do sistema">Entrar</a>
	  		</f:verbatim >	
	    </h:panelGrid>
    </h:panelGrid>
 </h:form>    
  </h:panelGrid>
</f:subview> 
