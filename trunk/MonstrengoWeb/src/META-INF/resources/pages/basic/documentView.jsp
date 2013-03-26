<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<f:view>
<html>
 <head>
 	<meta http-equiv="content-type" content="text/html; charset=UTF-8" />
 	<meta name="SHORTCUT Á ICON" content="http://hp.msn.com/global/c/shs/favicon.ico" />
 	<title>Pré-Visualização de documento - <h:outputText value="#{documentEntityBean.resultDocumentEntityName}"/></title>
 	<link href="../basic/css/styles.css" rel="stylesheet" type="text/css" />
 	
	<script type="javaScript" src="../basic/js/linkTool.js"></script>
	<script type="javaScript" src="../basic/js/interface.js"></script>
	<script type="javaScript" src="../basic/js/utils.js"></script>
	<script type="javaScript">
			var borderCreator = new BorderCreator('');
			var gridCreator = new GridCreator();
	</script>
	
 </head>
<body style="text-align: center;">
	<h:messages layout="table"/>
	<h:form>
		<h:panelGroup rendered="#{!documentEntityBean.hasDocumentFieldsEntry}">
	    <f:verbatim>
			<script>borderCreator.initDarkBorder('100%', '', 'left', 'top')</script>
		</f:verbatim>
		<h:panelGrid>
			<h:outputText styleClass="title" value="Preencha os campos necessários" escape="false"/>
			<h:outputText value="Este documento possui campos que podem ser preenchidos neste momento, antes de sua impressão. Defina o conteúdo de cada campo que é listado abaixo" escape="false" styleClass="description"/>

	<h:dataTable width="100%" 
			 value="#{documentEntityBean.documentFieldsEntry}" 
			 var='item' 
			 headerClass="tableAdvancedQueryHeader" 
			 styleClass="tableCollectionAdvancedQuery" 
			 rowClasses="tableAdvancedQueryRowEven,tableAdvancedQueryRowOdd" 
			 columnClasses="tableAdvancedQueryColumnOdd"
			 style="border-collapse: collapse" 
			 >

			<h:column>
				<f:facet name="header">
					<h:outputText value="Propriedade:" />
				</f:facet>

				<h:outputText  value="#{item.key}" />

			</h:column>
			<h:column>
				<f:facet name="header">
						<h:outputText value="Conteúdo: " />
				</f:facet>

				<h:inputText  value="#{item.value}" />
			</h:column>
	</h:dataTable>
		    
		</h:panelGrid>
	    <f:verbatim>
			<script>borderCreator.endDarkBorder()</script>
		</f:verbatim>
	</h:panelGroup>	

<%-- Para esta p�gina funcionar o filtro de extens�es do MyFaces deve estar configurado
     no web.xml. Mais informa��es em http://myfaces.apache.org/tomahawk/extensionsFilter.html 
--%>

			<h:commandButton value="Imprimir" action="#{documentEntityBean.actionPrint}"/>

			<h:inputTextarea value="#{documentEntityBean.compileDocumentProcess.compiledCrudDocument}"/>
                
		</h:form>
 </body>
</html>
</f:view>



<%--
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles" prefix="tiles" %>


<tiles:insert definition=".emptyLayout">
	<tiles:put name="title" value="Visualização de documento de entidade"/>
	<tiles:put name="body" value="../basic/tiles/document/view.jsp"/>
</tiles:insert> --%>