<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles" prefix="tiles" %>

<tiles:insert definition=".popupLayout">
	<tiles:put name="title" value="Criação de novo registro"/>
	<tiles:put name="onload" value="Nifty('div#EntityTitle','big')"/>
	<tiles:put name="body" value="tiles/create.jsp"/>
</tiles:insert>