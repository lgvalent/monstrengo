<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles" prefix="tiles" %>


<tiles:insert definition=".popupLayout">
	<tiles:put name="title" value="Gerar arquivo de Remessa"/>
	<tiles:put name="body" value="../financeiro/tiles/documentoCobranca/gerarRemessa4.jsp"/>
</tiles:insert>