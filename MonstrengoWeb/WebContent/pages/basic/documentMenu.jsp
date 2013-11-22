<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles" prefix="tiles" %>


<tiles:insert definition=".menuLayout">
	<tiles:put name="title" value="Controle de Documentos"/>
	<tiles:put name="img" value="../../public/basic/img/label.png"/>
	<tiles:put name="body" value="../../public/basic/tiles/document/menu.jsp"/>
</tiles:insert>