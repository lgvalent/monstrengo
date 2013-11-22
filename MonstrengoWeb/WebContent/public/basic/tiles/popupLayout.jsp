<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="tiles" uri="http://jakarta.apache.org/struts/tags-tiles" %>

<HTML>
 <HEAD>
 	<meta http-equiv="content-type" content="text/html; charset=UTF-8" />
 	<meta name="SHORTCUT ICON" content="http://hp.msn.com/global/c/shs/favicon.ico" />
 	<title><tiles:getAsString name="title"/></title>
 	<link href="../../public/basic/css/styles.css" rel="stylesheet" type="text/css" media="screen"/>
 	<link href="../../public/basic/css/stylesPrint.css" rel="stylesheet" type="text/css" media="print"/>
 	<link href="../../public/basic/js/border/niftyCorners.css" rel="stylesheet" type="text/css" />
 	
	<SCRIPT language=javaScript src="../../public/basic/js/linkTool.js"></SCRIPT>
	<SCRIPT language=javaScript src="../../public/basic/js/interface.js"></SCRIPT>
	<SCRIPT language=javaScript src="../../public/basic/js/utils.js"></SCRIPT>
	<SCRIPT language=javaScript>
			var borderCreator = new BorderCreator('');
			var gridCreator = new GridCreator();
	</SCRIPT>
	<%-- JS para cantos arredondados... --%>
	<SCRIPT type="text/javascript" src="../../public/basic/js/border/niftycube.js"></script>


<style>
div#EntityTitle{padding: 3px;margin:0 auto;
    background:#FFFFFF;color:#000}
div#EntityTitle_{padding: 3px;margin:0 auto;
    background:#F6F6F6;}
</style>

	<link href="../../public/basic/js/themes/default.css" rel="stylesheet" type="text/css"/>	
	<link href="../../public/basic/js/themes/alphacube.css" rel="stylesheet" type="text/css"/>

	<script type="text/javascript" src="../../public/basic/js/window/prototype.js"> </script>
	<script type="text/javascript" src="../../public/basic/js/window/effects.js"> </script>
	<script type="text/javascript" src="../../public/basic/js/window/window.js"> </script>
	<script type="text/javascript" src="../../public/basic/js/window/window_effects.js"> </script>
	<script type="text/javascript" src="../../public/basic/js/window/debug.js"> </script>

	
 </HEAD>

<%-- Ao carregar a página tenta colocar o foco no primeiro controle diponível

<BODY onLoad="javascript: Nifty('div#EntityTitle,div#EntityTitle_','big');" onUnload="javascript:if(window.closed)window.opener.focus()" >

 --%>

<BODY onLoad="javascript: <tiles:getAsString name="onload"/>" onUnload="javascript:if(window.closed)window.opener.focus()" >
  <f:view>
		<h:panelGroup rendered="#{messagesBean.hasMessages}">
			<h:outputText value="#{messagesBean.firstMessageSummary}" escape="false" styleClass="infoMessage"/>
		</h:panelGroup>
		<tiles:insert attribute="body" flush="false"/>
  </f:view>
 </BODY>
</HTML>