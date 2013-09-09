<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<h:form id="form" >
	<h:panelGrid columns="3" cellpadding="3">
		<h:outputLabel value="Conta"/>
		<h:selectOneMenu id="conta" value="#{lancamentoBean.conta}">
			<f:selectItems value="#{lancamentoBean.process.listConta}"/>
		</h:selectOneMenu>
		<h:message styleClass="errorMessage" for="conta"/>

		<h:outputLabel value="Data"/>
		<h:inputText id="data" value="#{lancamentoBean.data}" size="10" maxlength="10" onblur="return onblurCalendar('dd/MM/yyyy')"/>
		<h:message styleClass="errorMessage" for="data"/>
<%--
		<h:outputLabel value="Valor"/>
		<h:inputText id="valor" value="#{lancamentoBean.valor}"/>
		<h:message styleClass="errorMessage" for="valor"/>
--%>

		<h:commandButton value="Cancelar" action="#{lancamentoBean.doCancelar}" immediate="true"/>
		<h:commandButton value="Quitar" action="#{lancamentoBean.doQuitar}"/>
		<h:outputLabel value=""/>
	</h:panelGrid>
</h:form>
