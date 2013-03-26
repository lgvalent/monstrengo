<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles" prefix="tiles" %>

<tiles:insert definition=".emptyLayout">
	<tiles:put name="title" value="Relatório de Pesquisa"/>
	<tiles:put name="body" value="tiles/query/queryReport.jsp"/>
</tiles:insert>