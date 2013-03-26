<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles" prefix="tiles" %>

<tiles:insert definition=".menuLayout">
	<tiles:put name="title" value="Cadastro"/>
	<tiles:put name="img" value="../basic/img/menu_cadastro.png"/>
	<tiles:put name="sider" value="../basic/tiles/menu.jsp"/>
	<tiles:put name="body" value="../basic/tiles/menu.jsp"/>
</tiles:insert>