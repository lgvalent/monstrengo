<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles" prefix="tiles" %>

<tiles:insert definition=".emptyLayout">
	<tiles:put name="title" value="Relatório de Auditoria de Cadastro"/>
	<tiles:put name="body" value="tiles/security/checkAuditCrud.jsp"/>
</tiles:insert>