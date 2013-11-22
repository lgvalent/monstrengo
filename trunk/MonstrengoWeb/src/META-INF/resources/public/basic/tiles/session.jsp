<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>

<f:subview id="session">
   <h:form>
	    <h:panelGrid columns="3" rendered="#{userSessionBean.logged}" border="0" width="100%" style="text-align:right;">
    	    <h:outputText value="Usuário conectado:" />
    	    <h:outputText value="#{userSessionBean.userSession.user.object}" />
    	    <h:commandLink action="#{userSessionBean.doLogoff}">
	    	    <h:outputText value="Sair"/>
    	    </h:commandLink>
    	    
	    </h:panelGrid>
	    <h:panelGrid columns="1" rendered="#{not userSessionBean.logged}" border="0" width="100%" style="text-align:right;">
    	    <h:outputText value="Usuário não autenticado."/>
    	    <h:commandLink action="login"></h:commandLink>
	    </h:panelGrid>
   </h:form>    
</f:subview> 
