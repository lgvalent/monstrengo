<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles" prefix="tiles" %>

<tiles:insert definition=".emptyLayout">
	<tiles:put name="title" value="Relatório de movimento"/>
	<tiles:put name="body" value="/pages/financeiro/tiles/gerenciador/movimentoReport.jsp"/>
</tiles:insert>