<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

	<h:panelGrid columns="1" border="0">
	  <h:outputText value="Processo de criação de operador e seus direitos" styleClass="title"/>
	</h:panelGrid>

	  <h:form id="form">
	  	<h:panelGrid columns="4" cellpadding="3">

      		<h:outputText value="Login do novo operador ou de um já existente"/>
	    	<h:outputLink value="javascript:openSelectOneProp('br.com.orionsoft.monstrengo.security.entities.ApplicationUser', document.getElementById('form:login').value, 'login', document.getElementById('form:login'))" >
				<h:graphicImage value="../../public/basic/img/query.png" title="Pesquisar" style="border:0"/>
		    </h:outputLink>
      		<h:inputText value="#{createSecurityStructureBean.login}" id="login" size="20" maxlength="#{menuBean.infoMap.ApplicationUser.propertiesMetadata.login.size}" title="Se for fornecido um login não existente o sistema criará o novo operador, senão será usado o operador já cadastrado"/>
	    	<h:message styleClass="errorMessage" for="login" />

      		<h:outputText value="Nome do novo grupo ou de um já existente"/>
	    	<h:outputLink value="javascript:openSelectOneProp('br.com.orionsoft.monstrengo.security.entities.SecurityGroup', document.getElementById('form:groupName').value, 'name', document.getElementById('form:groupName'))" >
				<h:graphicImage value="../../public/basic/img/query.png" title="Pesquisar" style="border:0"/>
		    </h:outputLink>
      		<h:inputText value="#{createSecurityStructureBean.groupName}" id="groupName" size="20" maxlength="#{menuBean.infoMap.SecurityGroup.propertiesMetadata.name.size}" title="Se for fornecido um nome não existente o sistema criará o novo grupo, senão será usado o grupo já cadastrado"/>
	    	<h:message styleClass="errorMessage" for="groupName" />

      		<h:outputText value="Permitir acesso a todos os módulos do sistema"/>
  	  		<h:outputLabel value=""/>
	    	<h:selectBooleanCheckbox id="allowAll" value="#{createSecurityStructureBean.allowAll}"/>
	    	<h:message styleClass="errorMessage" for="alloAll" />
	    	
    	
    		<h:commandButton value="Criar/Manutenir direitos" action="#{createSecurityStructureBean.actionCreate}" onclick="this.value='Aguarde...'"/>
	  	</h:panelGrid>

		<h:outputText styleClass="errorMessage" value="#{messagesBean.firstMessageSummary}" escape="false" />
	  </h:form>