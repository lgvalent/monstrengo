<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>

<h:panelGrid columns="2" styleClass="noprint">
	<h:graphicImage value="../../public/basic/img/label.png" style="border:0"/>
	<h:panelGroup>
		<h:outputText value="Para gerar etiquetas com a atual lista de <i>#{queryBean.info.label}</i>, selecione o modelo de etiqueta " escape="false"/>
		<h:selectOneMenu id="modelLabelEntityId" value="#{queryBean.modelLabelEntityId}" title="Selecione o modelo de etiqueta de entidade">
			<f:selectItems value="#{queryBean.modelsLabelEntity}"/>
		</h:selectOneMenu>
		<h:outputText value=" e o grupo "/>
		<h:selectOneMenu id="addressLabelGroupId" value="#{queryBean.addressLabelGroupId}" title="Selecione o grupo desejado para a etiqueta gerada">
			<f:selectItems value="#{queryBean.addressLabelGroupList}"/>
		</h:selectOneMenu>
		<h:outputText value=" e clique em "/>
		<h:commandLink action="#{queryBean.actionCreateLabels}">
			<h:outputText value=" GERAR "/>
   	 		<f:param name="entityType" value="#{queryBean.entityParam.typeName}"/>
   		 	<f:param name="entityId"   value="#{queryBean.entityParam.id}"/>
		</h:commandLink>
		<h:outputText value=". As etiquetas serão inseridas na "/>
   		<h:outputLink value="javascript:popupPage('../basic/labelView.jsp',0,0, VIEW_LABEL_LIST)" rendered="#{menuBean.crudMap.AddressLabel.canRetrieve}">
			<h:outputText value=" Lista de Etiquetas"/>
   		 </h:outputLink>
	</h:panelGroup>
</h:panelGrid>
   		 
