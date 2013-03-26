<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles" prefix="tiles" %>

<tiles:insert definition=".queryLayout">
	<tiles:put name="body" value="queryBody.jsp"/>
	<tiles:put name="filter" value="queryFilter.jsp"/>
</tiles:insert>