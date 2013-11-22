<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>

<f:subview id="parent">
  <h:form>
	<%-- Ativa o monitor de requisição para esta página.
	     Assim, toda vez que esta página é carregada, por Action ou URL,
	     o monitor será acionado. --%>
	<h:inputHidden rendered="#{requestMonitorBean.load}"/>

	<%-- Caso exista um pai, serão exibidas informações deste --%>
	<h:panelGrid columns="4" styleClass="parentGroupHeader" rendered="#{parentBean.hasParent}">
		<h:outputText value="Entidade pai: #{parentBean.entity.info.label}" />
		<h:commandLink action="#{queryBean.actionList}" >
			<h:outputText value="[Listar]" />
	    	<f:param name="entityType" value="#{parentBean.entity.info.type.name}"/>
		</h:commandLink>
		<h:commandLink action="#{updateBean.actionEdit}" >
			<h:outputText value="[Alterar]" />
	    	<f:param name="entityType" value="#{parentBean.entity.info.type.name}"/>
	    	<f:param name="entityId" value="#{parentBean.entity.id}"/>
		</h:commandLink>
		<h:commandLink action="#{retrieveBean.actionView}" >
			<h:outputText value="[Apagar]" />
	    	<f:param name="entityType" value="#{parentBean.entity.info.type.name}"/>
	    	<f:param name="entityId" value="#{parentBean.entity.id}"/>
		</h:commandLink>
	</h:panelGrid>

	<%-- Caso exista um pai, serão exibidas informações deste --%>
	<h:panelGrid styleClass="parentGroupDetail" rendered="#{parentBean.hasParent}">
		<h:commandLink action="#{retrieveBean.actionView}" >
			<h:outputText value="#{parentBean.entity.object}"/>
	    	<f:param name="entityType" value="#{parentBean.entity.info.type.name}"/>
	    	<f:param name="entityId" value="#{parentBean.entity.id}"/>
		</h:commandLink>
	</h:panelGrid>
  </h:form>
</f:subview>
