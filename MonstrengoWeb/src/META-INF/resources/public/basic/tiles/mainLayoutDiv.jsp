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

<STYLE>
body {
   	margin:0 0 0 0;
	padding:0;
	background-color:#EEE;
	font:italic 14px/1.5em  Verdana, Arial, Serif;
}
#title{
	width:auto;
	height:auto;
	background-color:#DDD;
	color:#0f0;
	text-align:left;
}
#sider {
	position:absolute;
	width:190px;
	top:88px;
	left:0px;
	color:#99f;
	padding-bottom:20px;
	border-bottom:3px double #666;
}
#body {
	margin-left:204px;
	height:auto;
	padding-left:0px;
	background:#EEE;
}

</STYLE>
<BODY onload="javascript:placeFocus()" onunload="javascript:if(window.closed)window.opener.focus()" >
  <f:view>
	<div id="title"> 
			 	<h1 class="titleMenu">
			 	  <img src=<tiles:getAsString name="img"/> />
			 	  <tiles:getAsString name="title"/>
			 	</h1>
	</div>
	<div id="sider"> 
					<f:subview id="sider">
					   <tiles:insert attribute="sider" flush="false"/>
					</f:subview>
	</div>
	<div id="body">
					<f:subview id="body">
					   <tiles:insert attribute="body" flush="false"/>
					</f:subview>
	</div>  
  </f:view>
</BODY>
</HTML>