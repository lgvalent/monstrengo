<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles" prefix="tiles" %>

<tiles:insert definition=".popupLayout">
	<tiles:put name="title" value="Visualização"/>
	<tiles:put name="body" value="tiles/retrieve/retrieve.jsp"/>
</tiles:insert>