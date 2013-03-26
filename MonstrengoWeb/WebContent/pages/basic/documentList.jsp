<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles" prefix="tiles" %>

<tiles:insert definition=".menuLayout">
	<tiles:put name="title" value="Lista de Documentos"/>
	<tiles:put name="img" value="../basic/img/document.png"/>
	<tiles:put name="body" value="../basic/tiles/document/list.jsp"/>
</tiles:insert>