<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles" prefix="tiles" %>

<tiles:insert definition=".menuLayout">
	<tiles:put name="title" value="Alterar senha de acesso de um operador"/>
	<tiles:put name="img" value="../administracao/img/menu_administracao.png"/>
	<tiles:put name="body" value="../basic/tiles/security/overwritePassword.jsp"/>
</tiles:insert>