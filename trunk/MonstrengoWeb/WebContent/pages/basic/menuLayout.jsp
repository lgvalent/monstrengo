<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="tiles" uri="http://jakarta.apache.org/struts/tags-tiles" %>

<HTML >
 <HEAD>
 	<meta http-equiv="content-type" content="text/html; charset=UTF-8" />
 	<meta name="SHORTCUT Á ICON" content="http://hp.msn.com/global/c/shs/favicon.ico" />
 	<title><tiles:getAsString name="title"/></title>
 	<link href="../basic/css/styles.css" rel="stylesheet" type="text/css" media="screen"/>
 	<link href="../basic/css/stylesPrint.css" rel="stylesheet" type="text/css" media="print"/>
 	
	<SCRIPT language=javaScript src="../basic/js/linkTool.js"></SCRIPT>
	<SCRIPT language=javaScript src="../basic/js/interface.js"></SCRIPT>
	<SCRIPT language=javaScript src="../basic/js/utils.js"></SCRIPT>
	<SCRIPT language=javaScript>
			var borderCreator = new BorderCreator('');
			var gridCreator = new GridCreator();
	</SCRIPT>

	<link href="../basic/js/themes/default.css" rel="stylesheet" type="text/css"/>	
	<link href="../basic/js/themes/alphacube.css" rel="stylesheet" type="text/css"/>

	<script type="text/javascript" src="../basic/js/window/prototype.js"> </script>
	<script type="text/javascript" src="../basic/js/window/effects.js"> </script>
	<script type="text/javascript" src="../basic/js/window/window.js"> </script>
	<script type="text/javascript" src="../basic/js/window/window_effects.js"> </script>
	<script type="text/javascript" src="../basic/js/window/debug.js"> </script>

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

<BODY onload="javascript:placeFocus()" onunload="javascript:if(window.closed)window.opener.focus()" >
  <f:view>
	<table width="100%">
		<tr valign=top>
			<td >
				<%--  SCRIPT>borderCreator.initBorder('100%', '', 'left', 'center')</SCRIPT --%>
			 	    <table>
			 	    	<tr>
			 	    		<td>
						 	    <img class="noprint" src=<tiles:getAsString name="img"/> />
			 	    		</td>
			 	    		<td>
						 		<span class="titleMenu">
						 	  		<tiles:getAsString name="title"/>
						 		</span>
			 	    		</td>
			 	    	</tr>
			 		</table>
				<%--  SCRIPT>borderCreator.endBorder()</SCRIPT --%>
			</td>
		</tr>
		<tr valign=top >
			<td class="noprint" width=100% style="font-size:small">
			  Selecione a atividade que você deseja executar:
			</td>
		</tr>
		<tr valign=top>
			<td width=100% style="font-size:small">
				<SCRIPT>borderCreator.initBorder('100%', '', 'left', 'top')</SCRIPT>
				<%-- h:outputText id="infoMessage" value="#{messagesBean.firstMessageSummary}" escape="false" styleClass="infoMessage"/ --%>
				<h:dataTable 
				    value="#{messagesBean.allMessagesSummary}"
				    var="item">
					<h:column>
						<h:outputText id="message" value="#{item}" escape="false" styleClass="infoMessage"/>
					</h:column>
				</h:dataTable>
				<tiles:insert attribute="body" flush="false"/>
				<SCRIPT>borderCreator.endBorder()</SCRIPT>
			</td>
		</tr>
  </table>

  </f:view>
 </BODY>
</HTML>