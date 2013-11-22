<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles" prefix="tiles" %>
<tiles:insert definition=".emptyLayout">
	<tiles:put name="title" value="Pesquisa"/>
	<tiles:put name="img" value="../../public/basic/img/menu_query.png"/>
	<tiles:put name="body" value="tiles/query/query.jsp"/>
</tiles:insert>