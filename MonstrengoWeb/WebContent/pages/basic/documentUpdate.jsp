<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>

<HTML >
 <HEAD>
 	<meta http-equiv="content-type" content="text/html; charset=UTF-8" />
 	<meta name="SHORTCUT Á ICON" content="http://hp.msn.com/global/c/shs/favicon.ico" />
 	<title>Alteração do modelo de documento</title>
 	<link href="../basic/css/styles.css" rel="stylesheet" type="text/css" />

	<SCRIPT language=javaScript src="../basic/js/linkTool.js"></SCRIPT>
	<SCRIPT language=javaScript src="../basic/js/interface.js"></SCRIPT>
	<SCRIPT language=javaScript src="../basic/js/utils.js"></SCRIPT>
	<SCRIPT language=javaScript>
			var borderCreator = new BorderCreator('');
			var gridCreator = new GridCreator();
	</SCRIPT>

 </HEAD>

<BODY style="text-align: center;">
<f:view>
	<h:form>

<%-- Para esta p�gina funcionar o filtro de extens�es do MyFaces deve estar configurado
     no web.xml. Mais informa��es em http://myfaces.apache.org/tomahawk/extensionsFilter.html
--%>
		<%-- Sincroniza a atual entidade manipulada pela view --%>
			<SCRIPT>borderCreator.initDarkBorder('100%', '', 'center', 'top')</SCRIPT>
		<h:panelGrid width="100%">
			<h:outputText styleClass="title" value="Você está alterando os dados de <i>Modelo de Documento de Entidade</i>:<br>" escape="false" />
			<h:outputText value="Esta entidade permite definir modelos de documentos para entidades. É possível definir quais propriedades de uma entidade serão impressas em cada linha do documento. Para isto, o usuário utiliza expressões que serão interpretadas pelo gerador de documentos." escape="false" styleClass="description"/>
		</h:panelGrid>
			<SCRIPT>borderCreator.endDarkBorder()</SCRIPT>

			<fieldset><legend class="tableViewUpdateHeader">
			<h:outputLabel value="#{menuBean.infoMap.ModelDocumentEntity.groups[0].name}"/>
			</legend>

		<h:panelGrid columns="3" rowClasses="tableViewRowEven,tableViewRowOdd" style="border-collapse: collapse">
			<h:outputLabel value="#{menuBean.infoMap.ModelDocumentEntity.propertiesMetadata.applicationEntity.label}" styleClass="tableViewColumnEven" />
			<h:selectOneMenu value="#{documentEntityBean.modelDocumentEntityEdit.propertiesMap.applicationEntity.value.id}" required="#{menuBean.infoMap.ModelDocumentEntity.propertiesMetadata.applicationEntity.required}" styleClass="tableViewColumnOdd">
        		<f:selectItems value="#{documentEntityBean.modelDocumentEntityEdit.propertiesMap.applicationEntity.valuesList}"/>
            </h:selectOneMenu>
			<h:outputText value="#{menuBean.infoMap.ModelDocumentEntity.propertiesMetadata.applicationEntity.description}" styleClass="description" escape="false"/>

			<h:outputLabel value="#{menuBean.infoMap.ModelDocumentEntity.propertiesMetadata.applicationUser.label}" styleClass="tableViewColumnEven"/>
			<h:selectOneMenu value="#{documentEntityBean.modelDocumentEntityEdit.propertiesMap.applicationUser.value.id}" required="#{menuBean.infoMap.ModelDocumentEntity.propertiesMetadata.applicationUser.required}" styleClass="tableViewColumnOdd">
        		<f:selectItems value="#{documentEntityBean.modelDocumentEntityEdit.propertiesMap.applicationUser.valuesList}"/>
            </h:selectOneMenu>
			<h:outputText value="#{menuBean.infoMap.ModelDocumentEntity.propertiesMetadata.applicationUser.description}" styleClass="description"/>

			<h:outputLabel value="#{menuBean.infoMap.ModelDocumentEntity.propertiesMetadata.name.label}" styleClass="tableViewColumnEven"/>
			<h:inputText value="#{documentEntityBean.modelDocumentEntityEdit.propertiesMap.name.value.asString}" styleClass="tableViewColumnOdd"/>
			<h:outputText value="#{menuBean.infoMap.ModelDocumentEntity.propertiesMetadata.name.description}" styleClass="description"/>

			<h:outputLabel value="#{menuBean.infoMap.ModelDocumentEntity.propertiesMetadata.description.label}" styleClass="tableViewColumnEven"/>
			<h:inputTextarea value="#{documentEntityBean.modelDocumentEntityEdit.propertiesMap.description.value.asString}" styleClass="tableViewColumnOdd" rows="5" cols="40"/>
			<h:outputText value="#{menuBean.infoMap.ModelDocumentEntity.propertiesMetadata.description.description}" styleClass="description"/>

			<h:outputLabel value="#{menuBean.infoMap.ModelDocumentEntity.propertiesMetadata.date.label}" styleClass="tableViewColumnEven" />
			<h:inputText value="#{documentEntityBean.modelDocumentEntityEdit.propertiesMap.date.value.asString}" styleClass="tableViewColumnOdd" onblur="return onblurCalendar(this, 'dd/MM/yyyy')"/>
			<h:outputText value="#{menuBean.infoMap.ModelDocumentEntity.propertiesMetadata.date.description}" styleClass="description"/>

		</h:panelGrid>

			</fieldset>

		<fieldset><legend class="tableViewUpdateHeader">
			<h:outputLabel value="#{menuBean.infoMap.ModelDocumentEntity.groups[1].name}" styleClass="tableViewUpdateHeader"/>
			</legend>

		<h:inputTextarea value="#{documentEntityBean.modelDocumentEntityEdit.propertiesMap.source.value.asString}"
						style="width:100%;height:100%;"
						 />
		</fieldset>

		<h:outputLabel value="* Campos requeridos " styleClass="errorMessage" />
			<SCRIPT>borderCreator.initDarkBorder('100%', '', 'center', 'top')</SCRIPT>
		<h:commandButton value="Gravar" onclick="javascript:return confirm('Confirma gravar as alterações?')" action="#{documentEntityBean.actionSave}"/>
		<h:commandButton value="Cancelar" onclick="javascript:return confirm('Confirma abandonar as alterações?')" action="#{documentEntityBean.actionCancel}"/>
			<SCRIPT>borderCreator.endDarkBorder()</SCRIPT>


	</h:form>
</f:view>

			<%--#{documentEntityBean.modelDocumentEntityEdit.propertiesMap.name.value.asString}
		<h:column id="label">
			<h:outputLabel value="Entidade" />
		</h:column>

		<h:column>
			<h:inputTextarea id="txtInput" rows="5" cols="40" />
		</h:column--%>
 </BODY>
</HTML>
