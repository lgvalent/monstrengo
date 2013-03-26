<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles" prefix="tiles" %>

<tiles:insert definition=".popupLayout">
	<tiles:put name="title" value="Exclusão de registro"/>
	<tiles:put name="body" value="tiles/delete2.jsp"/>
</tiles:insert>