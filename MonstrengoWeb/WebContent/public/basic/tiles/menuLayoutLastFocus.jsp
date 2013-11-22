<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="tiles" uri="http://jakarta.apache.org/struts/tags-tiles" %>

<HTML >
 <HEAD>
 	<meta http-equiv="content-type" content="text/html; charset=UTF-8" />
 	<meta name="SHORTCUT Á ICON" content="http://hp.msn.com/global/c/shs/favicon.ico" />
 	<title><tiles:getAsString name="title"/></title>
 	<link href="../../public/basic/css/styles.css" rel="stylesheet" type="text/css" media="screen"/>
 	<link href="../../public/basic/css/stylesPrint.css" rel="stylesheet" type="text/css" media="print"/>
 	
	<SCRIPT language=javaScript src="../../public/basic/js/linkTool.js"></SCRIPT>
	<SCRIPT language=javaScript src="../../public/basic/js/interface.js"></SCRIPT>
	<SCRIPT language=javaScript src="../../public/basic/js/utils.js"></SCRIPT>
	<SCRIPT language=javaScript>
			var borderCreator = new BorderCreator('');
			var gridCreator = new GridCreator();
	</SCRIPT>
	
 </HEAD>

<BODY onload="javascript:placeLastFocus()" onunload="javascript:if(window.closed)window.opener.focus()" >
  <f:view>
	<table width="100%">
		<tr valign=top>
			<td >
				<SCRIPT>borderCreator.initBorder('100%', '', 'left', 'top')</SCRIPT>
			 	<font class="titleMenu">
			 	  <img src=<tiles:getAsString name="img"/> />
			 	  <tiles:getAsString name="title"/>
			 	</font>
				<SCRIPT>borderCreator.endBorder()</SCRIPT>
			</td>
		</tr>
		<tr valign=top >
			<td width=100% style="font-size:small">
			  Selecione a atividade que você deseja executar:
			</td>
		</tr>
		<tr valign=top>
			<td width=100% style="font-size:small">
				<SCRIPT>borderCreator.initBorder('100%', '', 'left', 'top')</SCRIPT>
				<h:outputText value="#{messagesBean.firstMessageSummary}" escape="false" styleClass="infoMessage"/>
				<tiles:insert attribute="body" flush="false"/>
				<SCRIPT>borderCreator.endBorder()</SCRIPT>
			</td>
		</tr>
  </table>

  </f:view>
 </BODY>
</HTML>