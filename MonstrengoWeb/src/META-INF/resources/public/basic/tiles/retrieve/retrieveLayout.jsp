<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="tiles" uri="http://jakarta.apache.org/struts/tags-tiles" %>

<f:subview id="Retrieve">
	<table id="tabela" border="0" cellpadding="0" cellspacing="0" width="100%">
		<tr valign="top">
			<td><tiles:insert attribute="parent" ignore="true" flush="false"/></td>
		</tr>
		<tr>
			<td align="center"><tiles:insert attribute="body" ignore="true" flush="false"/></td>
		</tr>
		<tr>
			<td><tiles:insert attribute="command" ignore="true" flush="false"/></td>
		</tr>
	</table>
</f:subview>
