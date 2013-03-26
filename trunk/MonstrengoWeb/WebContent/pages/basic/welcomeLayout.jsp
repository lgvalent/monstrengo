<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="tiles" uri="http://jakarta.apache.org/struts/tags-tiles" %>

<%-- Menu Principal , onde aparecem  todos os módulos do sistema --%>
<HTML>
 <HEAD>
 	<meta http-equiv="content-type" content="text/html; charset=UTF-8" />
 	<meta name="SHORTCUT ICON" content="http://hp.msn.com/global/c/shs/favicon.ico" />
 	<title><tiles:getAsString name="title"/></title>
 	<link href="../basic/css/styles.css" rel="stylesheet" type="text/css" />
 	
	<SCRIPT language=javaScript src="../basic/js/linkTool.js"></SCRIPT>
	<SCRIPT language=javaScript src="../basic/js/interface.js"></SCRIPT>
	<SCRIPT language=javaScript src="../basic/js/utils.js"></SCRIPT>

	<link href="../basic/js/themes/default.css" rel="stylesheet" type="text/css"/>	
	<link href="../basic/js/themes/alphacube.css" rel="stylesheet" type="text/css"/>

	<script type="text/javascript" src="../basic/js/window/prototype.js"> </script>
	<script type="text/javascript" src="../basic/js/window/effects.js"> </script>
	<script type="text/javascript" src="../basic/js/window/window.js"> </script>
	<script type="text/javascript" src="../basic/js/window/window_effects.js"> </script>
	<script type="text/javascript" src="../basic/js/window/debug.js"> </script>

	<SCRIPT language=javaScript>
			var borderCreator = new BorderCreator('');
			var gridCreator = new GridCreator();
	</SCRIPT>
		<style>
			
			#border
			{
				position:absolute;
				top:330px;
				left:0px;
				width:95%;
				height:600px;
				border: 1px solid #E5E5E5;
				overflow:hidden;
			}  
			#menu
			{
				position:absolute;
				top:0px;
				left:0px;
				/*width:95%;*/
				overflow:hidden;
			}
			#container
			{
				position:absolute;
				top:0px;
				left:0px;
				width:100%;
				height:600px;
				overflow:hidden;
				background:#F6F6F6;
				z-index:0;
			}
			#bar_Lateral
			{
				position:absolute;
				top:30px;
				border-left: 1px #E0E0E0 solid;
				border-right:1px #E0E0E0 solid;
				/*left:0px;*/
				width:200px;
				/*height:500px;*/
				overflow:hidden;
				background:#F1F2EE;
				z-index:0;
			}  
		</style>
	
 </HEAD>

<BODY onLoad="javascript:placeFocus()">
<%--
<a href="#" onClick="Dialog.alert('http://www.google.com', {className: 'alphacube',  width:250, id: 'oi'})"><span style="font-size: 16px;">Open a modal window</span></a>
<div id="border">
			<div id="container"></div>
</div--%>
  <f:view>
	<table width="100%">
		<tr valign="top">
			<td colspan="3">
				<SCRIPT>borderCreator.initBorder('100%', '', 'center', 'top')</SCRIPT>

				<tiles:insert attribute="header" flush="false"/>

				<SCRIPT>borderCreator.endBorder()</SCRIPT>
			</td>
		</tr>
		<tr valign="top" height="100%">
			<td width="20000" height="100%">
				<SCRIPT>borderCreator.initBorder('100%', '100%', 'left', 'top')</SCRIPT>

				<tiles:insert attribute="body" flush="false"/>
				
				<SCRIPT>borderCreator.endBorder()</SCRIPT>
			</td>
		</tr>
   		<tr valign="bottom" align="center">
			<td colspan="3">
				<SCRIPT>borderCreator.initBorder('100%', '', 'center', 'top')</SCRIPT>

				<tiles:insert attribute="footer" flush="false"/> 

				<SCRIPT>borderCreator.endBorder()</SCRIPT>
			</td>
   		</tr>
  </table>

  </f:view>
 </BODY>
</HTML>
