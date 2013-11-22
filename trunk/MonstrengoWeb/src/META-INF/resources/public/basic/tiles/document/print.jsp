<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="tiles" uri="http://jakarta.apache.org/struts/tags-tiles" %>

<f:verbatim>
<table width="100%">
	<tr>
		<td>
</f:verbatim>
<h:outputText value="#{documentEntityBean.resultDocumentEntity}" escape="false" />
<%-- Para esta p�gina funcionar o filtro de extens�es do MyFaces deve estar configurado
     no web.xml. Mais informa��es em http://myfaces.apache.org/tomahawk/extensionsFilter.html 
--%>
<f:verbatim>
		</td>
	</tr>
</table>
</f:verbatim>


                