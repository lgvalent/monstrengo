<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles" prefix="tiles" %>

<tiles:insert definition=".menuLayout">
	<tiles:put name="img" value="../../login/img/login.gif"/>
	<tiles:put name="title" value="Alterar senha da pessoa"/>
	<tiles:put name="body" value="../basic/tiles/alterarSenhaPessoa.jsp"/>
</tiles:insert>