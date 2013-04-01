<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles" prefix="tiles" %>

<tiles:insert definition=".menuLayout">
	<tiles:put name="title" value="Impressão de documentos de pagamento"/>
	<tiles:put name="img" value="../financeiro/img/imprimirDocumento_b.png"/>
	<tiles:put name="body" value="../financeiro/tiles/documentoPagamento/imprimir1.jsp"/>
</tiles:insert>