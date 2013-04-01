<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<h:form id="form" >
	<h:panelGrid width="100%" columns="2" cellpadding="3" styleClass="tableList" columnClasses="tableListColumn" rowClasses="tableListRowEven,tableListRowOdd">
		<h:outputLabel value="#{menuBean.infoMap.ConvenioCobranca.label}"/>		
		<h:outputText value="#{gerarDocumentoCobrancaRemessaBean.process.convenioNome}" />
		
		<h:outputLabel value="Início Período"/>
		<h:outputText value="#{gerarDocumentoCobrancaRemessaBean.process.inicioPeriodo.time}" />
 
		<h:outputLabel value="Final Período"/>
		<h:outputText value="#{gerarDocumentoCobrancaRemessaBean.process.finalPeriodo.time}" />
	</h:panelGrid>
		
	<h:panelGrid columns="2">
		<h:commandButton value="Cancelar" onclick="javascript:if(confirm('Confirma abandonar as alterações?'))window.close();"/>
		<h:commandButton value="Confirmar" action="#{gerarDocumentoCobrancaRemessaBean.actionGerarRemessa}" onclick="this.value='Aguarde, Processando o Arquivo'"/>
	</h:panelGrid>
</h:form>
	