<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

	<h:panelGrid columns="1" border="0">
	  <h:outputText value="Alteração da senha do operador <b>#{userSessionBean.userSession.user.object}</b>:" styleClass="title" escape="false"/>
	</h:panelGrid>

	  <h:form id="form">
	  	<h:panelGrid columns="3" cellpadding="3">
      		<h:outputText value="Senha atual"/>
      		<h:inputSecret value="#{changePasswordBean.currentPassword}" id="currentPassword" size="20" maxlength="#{menuBean.infoMap.ApplicationUser.propertiesMetadata.password.size}" title="Informe sua atual senha de acesso ao sistema" required="true"/>
	    	<h:message styleClass="errorMessage" for="currentPassword" />

      		<h:outputText value="Nova senha:"/>
      		<h:inputSecret value="#{changePasswordBean.newPassword}" id="newPassword" size="20" maxlength="#{menuBean.infoMap.ApplicationUser.propertiesMetadata.password.size}" title="Informe sua nova senha" required="true"/>
	    	<h:message styleClass="errorMessage" for="newPassword" />

      		<h:outputText value="Confirma nova senha:"/>
      		<h:inputSecret value="#{changePasswordBean.confirmNewPassword}" id="confirmNewPassword" size="20" maxlength="#{menuBean.infoMap.ApplicationUser.propertiesMetadata.password.size}" title="Confirme sua nova senha" required="true"/>
	    	<h:message styleClass="errorMessage" for="confirmNewPassword" />

      		<h:outputText value=""/>
    		<h:commandButton value="Alterar a senha" action="#{changePasswordBean.actionChange}" onclick="this.value='Alterando sua senha...'"/>
	  	</h:panelGrid>

		<h:outputText styleClass="errorMessage" value="#{messagesBean.firstMessageSummary}" escape="false" />
	  </h:form>