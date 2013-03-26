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
	
 </HEAD>

<BODY onload="<tiles:getAsString name='onload'/>" style="text-align: center;" onunload="javascript:if(window.closed)window.opener.focus()">
	<f:view>
		<table width="100%">
			<tr>
				<td align="center">
					<tiles:insert attribute="body" flush="false"/>
				</td>
			</tr>
		</table>
	</f:view>
 </BODY>
</HTML>