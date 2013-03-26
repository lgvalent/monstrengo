<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>

<f:subview id="parent">
	<%-- Caso exista um pai, serão exibidas informações deste --%>
	<h:panelGrid columns="3" styleClass="parentGroupHeader" rendered="#{queryBean.currentProcess.userReport.parentParam.hasParent}">
		<h:graphicImage value="../basic/img/query_parent.png" style="border:0"/>
		<h:outputText value="Você está visualizando a propriedade <b>#{queryBean.parentProperty.info.label}</b> do(a) #{queryBean.parentEntity.info.label} <b>#{queryBean.parentEntity.object}</b>." escape="false"/>
		<h:outputLink value="javascript:linkRetrieve('#{queryBean.parentEntity.info.type.name}', '#{queryBean.parentEntity.id}')">
			<h:graphicImage value="../basic/img/retrieve.png" title="Visualizar os detalhes da entidade pai" style="border:0"/>
		</h:outputLink>	
	</h:panelGrid>
</f:subview>
