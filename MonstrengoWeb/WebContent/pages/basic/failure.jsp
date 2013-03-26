<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles" prefix="tiles" %>

<tiles:insert definition=".popupLayout">
	<tiles:put name="title" value="Ocorreu um erro"/>
	<tiles:put name="body" value="tiles/failure.jsp"/>
</tiles:insert>