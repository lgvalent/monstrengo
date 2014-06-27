<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles" prefix="tiles" %>

<tiles:insert definition=".popupLayout">
	<tiles:put name="title" value="Operação executada com sucesso"/>
	<tiles:put name="body" value="success.jsp"/>
</tiles:insert>