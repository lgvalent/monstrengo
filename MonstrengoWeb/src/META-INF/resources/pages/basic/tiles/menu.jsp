<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>

<f:subview id="cadastro">

  <%-- h:form id="suggest">
	  <f:verbatim >	
	    <h0>Busca de entidades:</h0> 
	  </f:verbatim >	
		<h:panelGroup>
			<h:inputText id="findEntity" required="true"  style="width:100%" />
				<!-- Lucio 20110712 Este suggestionBox renderiza um InputHidden para armazenar a classe selecionada.
				     Retorna para o edit o label da classe e possui três opções de seleção.
				     No dropDown são renderizados dois links um pra CREATE e outro pra QUERY.
				     E ainda, o operador por simplesmente selecionar com ENTER.
				     No três casos ocorre o onselect. Por isto, eu verifico,  no onselect,
				     se o suggestion está com o foco. Caso o usuário clique nos
				     links, o suggestion.hasFocus é false, o onSelect não chama o link -->
				<rich:suggestionbox for="findEntity" var="result"
					suggestionAction="#{menuBean.findEntity}"
					minChars="4" width="300" height="200"
					fetchValue="#{result.label}"
					ignoreDupResponses="true"
					onselect="javascript:var entity=suggestion.getCurrentEntry().getElementsByTagName('input')[0].value;if(suggestion.hasFocus)linkQuery(entity);"
					>
					<h:column>
						<h:outputLink value="javascript:linkCreatePopup('#{result.type.name}')">
							<h:graphicImage value="../basic/img/create.png" title="Criar novo registro de #{result.label}" style="border:0" />
					 	</h:outputLink>
						<h:outputLink value="javascript:linkQuery('#{result.type.name}')">
						 	<h:graphicImage value="../basic/img/query.png" title="Abrir pesquisa de #{result.label}" style="border:0" />
							<h:outputText value="#{result.label}" />
					 	</h:outputLink>
						<!-- Este input ÚNICO é usado para pega o nome da classe e lançar o script da tela no evento onselect() -->
						<h:inputHidden id="suggestionClass" value="#{result.type.name}"/>
					</h:column>
				</rich:suggestionbox>
		</h:panelGroup>
	  <f:verbatim >	
	    <hr>
	  </f:verbatim >	
  </h:form--%>
  <h:form >
	<h:dataTable value="#{menuBean.infoList}" 
	             var="item"
			     styleClass="tableView"
			     rowClasses="tableViewRowEven,tableViewRowOdd"
			     style="widht:100%"
	             >
		<h:column >
			<h:panelGroup>
	        	<%-- Acesso por POPUP_LINK --%>
				<h:outputLink value="javascript:linkCreatePopup('#{item.type.name}')" rendered="#{menuBean.crudMap.get(item.type.simpleName).canCreate}">
					<h:graphicImage value="../basic/img/create.png" title="Criar novo registro" style="border:0" />
			 	</h:outputLink>
			 	<h:graphicImage value="../basic/img/create_d.png" title="Você não possui direitos para criar um novo registro" style="border:0" rendered="#{!menuBean.crudMap.get(item.type.simpleName).canCreate}" />

			 	<h:outputText value=" " />
			 	<h:outputLink value="javascript:linkQuery('#{item.type.name}')" rendered="#{menuBean.crudMap.get(item.type.simpleName).canQuery}">
				 	<h:graphicImage value="../basic/img/query.png" title="Pesquisar" style="border:0" />
				</h:outputLink>
	 			<h:graphicImage value="../basic/img/query_d.png" title="Você não possui direitos para visualizar esta entidade" style="border:0" rendered="#{!menuBean.crudMap.get(item.type.simpleName).canQuery}" />
				<h:outputText value=" " />

	        	<%-- Acesso por ACTION
	        		<h:commandLink action="#{createBean.actionCreate}" rendered="#{item.canCreate}">
       				<h:graphicImage value="../basic/img/create.png" title="Criar novo registro" style="border:0"/>
				  	<f:param value="#{item.type.name}" name="entityType" />
			    </h:commandLink>
				<h:graphicImage value="../basic/img/create_d.png" title="Você não possui direitos para criar um novo registro" style="border:0" rendered="#{!item.canCreate}"/>
		        
				<h:outputText value=" "/> 
	        	<h:commandLink action="#{queryBean.actionList}" rendered="#{item.canRetrieve}">
   					<h:graphicImage value="../basic/img/query.png" title="Pesquisar" style="border:0"/>
				  	<f:param value="#{item.type.name}" name="entityType" />
			    </h:commandLink>
				<h:graphicImage value="../basic/img/query_d.png" title="Você não possui direitos para visualizar esta entidade" style="border:0" rendered="#{!item.canRetrieve}"/ --%>
			    
				<h:outputText value="  #{item.label}"/>
		        
			</h:panelGroup>
		</h:column>
	</h:dataTable>
  </h:form>
</f:subview>
