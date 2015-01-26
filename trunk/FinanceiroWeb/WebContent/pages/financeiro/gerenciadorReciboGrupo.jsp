<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles" prefix="tiles" %>

<tiles:insert definition=".emptyLayout">
	<tiles:put name="title" value="Recibo de lançamento"/>
	<tiles:put name="body" value="/pages/financeiro/tiles/gerenciador/reciboGrupo.jsp"/>
</tiles:insert>