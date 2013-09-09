<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<h:form id="form">
	<h:dataTable value="#{messagesBean.fromSecondMessage}" var='msg'style="color:blue">
		<h:column>
			<f:facet name="header">
				<h:outputText value="Mensagens adicionais:" />
			</f:facet>
			
			<h:outputText value="#{msg.summary}" escape="false" />
		</h:column>
	</h:dataTable>
</h:form>
	