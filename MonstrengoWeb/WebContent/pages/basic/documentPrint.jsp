<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>

<f:view>
<html >
 <head>
 	<meta http-equiv="content-type" content="text/html; charset=UTF-8" />
 	<meta name="SHORTCUT Á ICON" content="http://hp.msn.com/global/c/shs/favicon.ico" />
 	<title><h:outputText value="#{documentEntityBean.resultDocumentEntityName}"/></title>
 	<%--link href="../../public/basic/css/styles.css" rel="stylesheet" type="text/css" /--%>
 	
	<script type="javaScript" src="../../public/basic/js/linkTool.js"></script>
	<script type="javaScript" src="../../public/basic/js/interface.js"></script>
	<script type="javaScript" src="../../public/basic/js/utils.js"></script>
	<script type="javaScript">
			var borderCreator = new BorderCreator('');
			var gridCreator = new GridCreator();
	</script>
 </head>

<body onload="javascript:window.print()">
		<h:outputText value="#{documentEntityBean.compileDocumentProcess.compiledFieldsDocument}" escape="false" />
 </body>
</html>
</f:view>
