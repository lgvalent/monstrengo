<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<a4j:region id="inserirLancamento" selfRendered="true">
	<a4j:status>
		<f:facet name="start">
			<f:verbatim>
				<div id="status" style="position:absolute; top:2px; right:2px; font: 14px; color:#FFFFFF; background: #993300">
		   			Carregando...
				</div>
			</f:verbatim>
		</f:facet>
	</a4j:status>

<h:form id="form" >
	<h:panelGrid columns="3" cellpadding="3" styleClass="tableList">
		<h:outputLabel value="Conta origem (-débito)"/>
		<h:selectOneMenu id="contaOrigem" value="#{transferirBean.process.contaOrigem.id}">
			<f:selectItems value="#{transferirBean.process.listConta}"/>
		</h:selectOneMenu>
		<h:message styleClass="errorMessage" for="contaOrigem"/>

		<h:outputLabel value="Conta destino (+crédito)"/>
		<h:selectOneMenu id="contaDestino" value="#{transferirBean.process.contaDestino.id}">
			<f:selectItems value="#{transferirBean.process.listConta}"/>
		</h:selectOneMenu>
		<h:message styleClass="errorMessage" for="contaDestino"/>

		<h:outputLabel value="Data"/>
		<h:inputText id="data" value="#{transferirBean.process.data.time}" required="true" size="10" maxlength="10" onblur="return onblurCalendar(this, 'dd/MM/yyyy')">
			<f:convertDateTime locale="Locale.BRAZIL" pattern="dd/MM/yyyy"/>
		</h:inputText>
		<h:message styleClass="errorMessage" for="data"/>

		<h:outputLabel value="Valor"/>
		<h:inputText id="valor" value="#{transferirBean.process.valor}" style="text-align: right">
			<f:convertNumber pattern="###,###,##0.00" type="number"/>
		</h:inputText>
		<h:message styleClass="errorMessage" for="valor"/>

		<h:outputLabel value="Descrição"/>
		<h:inputTextarea id="descricao" value="#{transferirBean.process.descricao}" rows="3"/>
		<h:message styleClass="errorMessage" for="descricao"/>

		<h:commandButton value="Cancelar" onclick="javascript:if(confirm('Confirma abandonar as alterações?'))window.close();"/>
		<h:commandButton value="Transferir" action="#{transferirBean.actionTransferir}"/>
		<h:outputLabel value=""/>
	</h:panelGrid>
</h:form>
</a4j:region>