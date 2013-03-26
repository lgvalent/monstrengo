<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<h:form id="form">
	<h:panelGrid columns="3" cellpadding="5" style="text-align:center">
   		<h:panelGrid columns="5" style="font: 12px">
   			<h:outputText value="Digite o CPF"/>
	    	<h:outputLink value="javascript:openSelectOneProp('#{menuBean.infoMap.Fisica.type.name}', document.getElementById('form:numeroDocumento').value, 'documento', document.getElementById('form:numeroDocumento'))" >
				<h:graphicImage value="../basic/img/query.png" title="Pesquisar" style="border:0"/>
		    </h:outputLink>

   			<h:outputText value="/CNPJ" />
	    	<h:outputLink value="javascript:openSelectOneProp('#{menuBean.infoMap.Juridica.type.name}', document.getElementById('form:numeroDocumento').value, 'documento', document.getElementById('form:numeroDocumento'))" >
				<h:graphicImage value="../basic/img/query.png" title="Pesquisar" style="border:0"/>
		    </h:outputLink>
			<h:outputLabel value="*" styleClass="errorMessage" />

   		</h:panelGrid>
		<h:inputText value="#{alterarSenhaPessoaBean.numeroDocumento}"
			id="numeroDocumento" size="15" required="true"/>
		<h:message styleClass="errorMessage" for="numeroDocumento" />

		<h:panelGroup>
			<h:outputText value="Digite a nova senha" />
			<h:outputLabel value="*" styleClass="errorMessage" />
		</h:panelGroup> 
		<h:inputSecret value="#{alterarSenhaPessoaBean.novaSenha}" id="novaSenha"
			size="15" required="true" />
		<h:message styleClass="errorMessage" for="novaSenha" />
		
		<h:panelGroup>
			<h:outputText value="Confirme a nova senha" />
			<h:outputLabel value="*" styleClass="errorMessage" />
		</h:panelGroup> 
		<h:inputSecret value="#{alterarSenhaPessoaBean.novaSenhaConfirmacao}" id="novaSenhaConfirmacao"
			size="15" required="true" />
		<h:message styleClass="errorMessage" for="novaSenhaConfirmacao" />

	</h:panelGrid>
	<h:outputLabel value="* Campos requeridos " styleClass="errorMessage" />
	<h:panelGrid cellpadding="5" style="text-align:center">
		<h:outputText styleClass="errorMessage"
			value="#{messagesBean.firstMessageSummary}" escape="false" />
		<h:commandButton value="Confirmar" action="#{alterarSenhaPessoaBean.actionAlterar}" />
	</h:panelGrid>

</h:form>