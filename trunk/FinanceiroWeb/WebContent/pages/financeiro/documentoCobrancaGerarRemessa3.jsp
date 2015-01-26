<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles" prefix="tiles" %>


<tiles:insert definition=".menuLayout">
	<tiles:put name="title" value="Gerar arquivo de Remessa"/>
	<tiles:put name="img" value="../financeiro/img/menu_financeiro.png"/>
	<tiles:put name="body" value="/pages/financeiro/tiles/documentoCobranca/gerarRemessa3.jsp"/>
</tiles:insert>