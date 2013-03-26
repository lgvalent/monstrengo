<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

	<h:panelGrid columns="1" border="0">
	  <h:outputText value="Alteração da senha de um operador:" styleClass="title" escape="false"/>
	</h:panelGrid>

	  <h:form id="form">
	  	<h:panelGrid columns="3" cellpadding="3">
      		<h:panelGroup>
      			<h:outputText value="Login do operador"/>
		    	<h:outputLink value="javascript:openSelectOneProp('br.com.orionsoft.monstrengo.security.entities.ApplicationUser', document.getElementById('form:login').value, 'login', document.getElementById('form:login'))" >
					<h:graphicImage value="../basic/img/query.png" title="Pesquisar" style="border:0"/>
			    </h:outputLink>
			</h:panelGroup>
      		<h:inputText value="#{overwritePasswordBean.process.login}" id="login" size="20" maxlength="#{menuBean.infoMap.ApplicationUser.propertiesMetadata.login.size}" title="Informe o login do operador que terá sua senha de acesso alterada"/>
	    	<h:message styleClass="errorMessage" for="login" />

      		<h:outputText value="Nova senha:"/>
      		<h:inputSecret value="#{overwritePasswordBean.process.newPassword}" id="newPassword" size="20" maxlength="#{menuBean.infoMap.ApplicationUser.propertiesMetadata.password.size}" title="Informe sua nova senha" required="true"/>
	    	<h:message styleClass="errorMessage" for="newPassword" />

      		<h:outputText value="Confirma nova senha:"/>
      		<h:inputSecret value="#{overwritePasswordBean.process.confirmNewPassword}" id="confirmNewPassword" size="20" maxlength="#{menuBean.infoMap.ApplicationUser.propertiesMetadata.password.size}" title="Confirme sua nova senha" required="true"/>
	    	<h:message styleClass="errorMessage" for="confirmNewPassword" />

	  	</h:panelGrid>
   		<h:commandButton value="Cancelar" action="#{overwritePasswordBean.actionCancel}" onclick="this.value='Cancelando...'" immediate="true"/>
   		<h:commandButton value="Alterar a senha" action="#{overwritePasswordBean.actionOverwrite}" onclick="this.value='Alterando a senha...'"/>
	  </h:form>