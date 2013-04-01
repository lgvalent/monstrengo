<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<h:form id="form" >
	<h:panelGrid columns="2">
		<h:outputLabel value="Alterar o atual documento de cobrança:"/>
	</h:panelGrid>

	<h:panelGrid width="100%" columns="1" cellpadding="3" styleClass="tableList" columnClasses="tableListColumn" rowClasses="tableListRowEven,tableListRowOdd">
		<h:outputText value="#{alterarDocumentoCobrancaBean.process.categoriaAntigaAsString}" />
	</h:panelGrid>
	
	<f:verbatim><br></f:verbatim>
	
	<h:panelGrid columns="2">
		<h:outputLabel value="para o novo documento de cobrança selecionado:"/>
	</h:panelGrid>
	
	<h:panelGrid width="100%" columns="1" cellpadding="3" styleClass="tableList" columnClasses="tableListColumn" rowClasses="tableListRowEven,tableListRowOdd">
		<h:outputText value="#{alterarDocumentoCobrancaBean.process.categoriaAsString}" />
	</h:panelGrid>
	
	<f:verbatim><br></f:verbatim>
	
	<h:panelGrid columns="2">
		<h:outputLabel value="Deseja confirmar estas alterações?"/>
	</h:panelGrid>
		
	<h:panelGrid columns="2">
		<h:commandButton value="Cancelar" onclick="javascript:if(confirm('Confirma abandonar as alterações?'))window.close();"/>
		<h:commandButton value="Confirmar" action="#{alterarDocumentoCobrancaBean.doConfirmar}" onclick="this.value='Aguarde'"/>
	</h:panelGrid>
</h:form>
	