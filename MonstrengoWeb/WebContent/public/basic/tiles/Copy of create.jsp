<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>

<f:subview id="create">
	<h:form>
		<%-- Sincroniza a atual entidade manipulada pela view --%>
		<h:inputHidden immediate="true" id="currentEntityKey"
			value="#{createBean.currentEntityKey}"
			validator="#{createBean.validateCurrentEntityKey}" />
		<%-- Fornece um acesso para que o BEAN invalide um valor da view JSF e forçe a reconstrução quando necessário --%>
		<h:inputHidden immediate="true"
			binding="#{createBean.inputCurrentEntityKey}" />

		<div id="EntityTitle_">
			<%--<SCRIPT>borderCreator.initDarkBorder('100%', '', 'center', 'top')</SCRIPT> --%>
			<h:panelGrid width="100%"
				style="background-color:#{createBean.currentEntity.info.colorName}">
				<h:outputText styleClass="title"
					value="Você está criando um novo registro de <i>#{createBean.currentEntity.info.label}</i>:<br>"
					escape="false" />
				<h:outputText
					value="#{createBean.currentEntity.info.description}<br>"
					escape="false" styleClass="description" />
			</h:panelGrid>
			<%-- <SCRIPT>borderCreator.endDarkBorder()</SCRIPT> --%>
		</div>

		<h:dataTable value="#{createBean.currentEntity.groupsProperties}"
			var='group' width="100%">

			<h:column>
				<fieldset class="tableViewCreateGroup">
					<legend class="tableViewCreateHeader">
						<h:outputLabel value="#{group.info.label}" />
					</legend>

					<h:dataTable value="#{group.properties}" var='item'
						styleClass="tableView"
						rowClasses="tableViewRowEven,tableViewRowOdd"
						columnClasses="tableViewColumnEven, tableViewColumnOdd"
						style="border-collapse: collapse">

						<h:column id="label"
							rendered="#{item.info.visible && !item.info.readOnly}">
							<h:outputLabel value="#{item.info.label}"
								title="#{item.info.hint}" style="color:#{item.info.colorName};" />
							<h:outputLabel value="*" rendered="#{item.info.required}"
								styleClass="errorMessage" />
						</h:column>

						<h:column id="values"
							rendered="#{item.info.visible && !item.info.readOnly}">
							<%-- Exibe lista de escolha para campos primitivos  --%>
							<h:panelGroup
								rendered="#{item.info.editShowList && item.info.primitive && !item.info.isEnum()}">
								<h:selectOneMenu id="selectPrimitiveInput"
									value="#{item.value.asString}" required="#{item.info.required}">
									<f:selectItems value="#{item.valuesList}" />
								</h:selectOneMenu>
								<h:message for="selectPrimitiveInput" styleClass="errorMessage" />
							</h:panelGroup>

							<%-- Exibe lista de escolha para ENUMS  --%>
							<h:panelGroup
								rendered="#{item.info.isEnum() and !item.info.collection}">
								<h:selectOneMenu id="selectEnumInput" value="#{item.value.id}"
									required="#{item.info.required}">
									<f:selectItems value="#{item.valuesList}" />
								</h:selectOneMenu>
								<h:message for="selectEnumInput" styleClass="errorMessage" />
							</h:panelGroup>

							<%-- Exibe caixa de entrada de texto para campos primitivos  --%>
							<h:panelGroup
								rendered="#{!item.info.editShowList && item.info.primitive && !item.info.isEnum()}">
								<h:inputTextarea id="txtInput" style="width:100%"
									value="#{item.value.asString}" title="#{item.info.hint}"
									required="#{item.info.required}"
									rendered="#{item.info.isString() && item.info.size>255  && !item.info.hasEditMask}"
									disabled="#{item.info.readOnly}" rows="2" />
								<h:message for="txtInput" styleClass="errorMessage" />

								<h:inputText id="strInput" style="width:100%"
									value="#{item.value.asString}" title="#{item.info.hint}"
									required="#{item.info.required}"
									rendered="#{item.info.isString() && item.info.size<256  && !item.info.hasEditMask}"
									disabled="#{item.info.readOnly}" maxlength="#{item.info.size}" />
								<h:message for="strInput" styleClass="errorMessage" />

								<h:inputText id="strMaskInput" style="width:100%"
									value="#{item.value.asString}" title="#{item.info.hint}"
									required="#{item.info.required}"
									rendered="#{item.info.isString() && item.info.hasEditMask}"
									disabled="#{item.info.readOnly}"
									maxlength="#{item.info.editMaskSize}"
									onkeypress="return keyPressMask(this,'#{item.info.editMask}')" />
								<h:message for="strMaskInput" styleClass="errorMessage" />

								<h:inputText id="intInput" value="#{item.value.asString}"
									title="#{item.info.hint}" required="#{item.info.required}"
									rendered="#{item.info.isInteger()}"
									disabled="#{item.info.readOnly}" size="10"
									onkeypress="return keyPressInt(this,event)">
									<f:validateLongRange minimum="-999999999" maximum="999999999" />
								</h:inputText>
								<h:message for="intInput" styleClass="errorMessage" />

								<h:inputText id="longInput" value="#{item.value.asString}"
									title="#{item.info.hint}" required="#{item.info.required}"
									rendered="#{item.info.isLong()}" disabled="#{item.info.readOnly}"
									size="10" onkeypress="return keyPressInt(this,event)">
									<f:validateLongRange minimum="-999999999" maximum="999999999" />
								</h:inputText>
								<h:message for="longInput" styleClass="errorMessage" />

								<h:inputText id="bigInput" value="#{item.value.asString}"
									title="#{item.info.hint}" required="#{item.info.required}"
									rendered="#{item.info.bigDecimal}"
									disabled="#{item.info.readOnly}" size="10"
									onkeypress="return keyPressFloat(this,event)" />
								<h:message for="bigInput" styleClass="errorMessage" />

								<h:inputText id="floatInput" value="#{item.value.asString}"
									title="#{item.info.hint}" required="#{item.info.required}"
									rendered="#{item.info.isFloat()}" disabled="#{item.info.readOnly}"
									size="10" onkeypress="return keyPressFloat(this,event)" />
								<h:message for="floatInput" styleClass="errorMessage" />

								<h:inputText id="doubleInput" value="#{item.value.asString}"
									title="#{item.info.hint}" required="#{item.info.required}"
									rendered="#{item.info.isDouble()}" disabled="#{item.info.readOnly}"
									size="10" onkeypress="return keyPressFloat(this,event)" />
								<h:message for="doubleInput" styleClass="errorMessage" />

								<h:inputText id="dateInput" value="#{item.value.asString}"
									title="#{item.info.hint}" required="#{item.info.required}"
									rendered="#{item.info.calendar}"
									disabled="#{item.info.readOnly}" size="14"
									onblur="return onblurCalendar(this,'#{item.info.editMask}')" />
								<h:message for="dateInput" styleClass="errorMessage" />
							</h:panelGroup>

							<%-- Exibe informações sobre a máscara do campo --%>
							<h:panelGroup
								rendered="#{item.info.hasEditMask && !item.info.isBoolean()}">
								<h:outputLabel value="#{item.info.editMask}"
									title="Utilize este formato para preencher o campo corretamente."
									style="color:orange;" />
								<%-- Lucio 04102007: Ao usar o tab, sempre o focu pára nele.
							 h:outputLink value="javascript:popupPage('../basic/help/editMask.html', 500,600)">
							   <h:outputText value="[?]" title="Abre uma tela com ajuda"/>
							</h:outputLink --%>
							</h:panelGroup>

							<h:selectBooleanCheckbox id="bolInput"
								value="#{item.value.asBoolean}" title="#{item.info.hint}"
								required="#{item.info.required}" rendered="#{item.info.isBoolean()}"
								disabled="#{item.info.readOnly}">
							</h:selectBooleanCheckbox>
							<h:message for="bolInput" styleClass="errorMessage" />

							<%-- Subclasse em SelectMenu --%>
							<h:panelGroup
								rendered="#{item.info.editShowList && item.info.entity && !item.info.collection}">
								<h:selectOneMenu id="selectSubClassInput"
									value="#{item.value.id}" required="#{item.info.required}">
									<f:selectItems value="#{item.valuesList}" />
								</h:selectOneMenu>

								<h:outputLink
									value="javascript:linkRetrieve('#{item.info.type.name}', '#{item.value.asEntity.id}')"
									rendered="#{not item.value.valueNull}">
									<h:graphicImage value="../../public/basic/img/retrieve.png"
										title="Visualiza os detalhes do registro" style="border:0" />
								</h:outputLink>
								<h:outputLink
									value="javascript:linkCreatePopup('#{item.info.type.name}')">
									<h:graphicImage value="../../public/basic/img/create.png"
										title="Cria um novo registro na lista. Será necessário clicar em validar para atualizar a lista com o novo item"
										style="border:0" />
								</h:outputLink>
								<h:outputLink value="javascript:document.forms[0].submit()">
									<h:graphicImage value="../../public/basic/img/reload.png"
										title="Recarrega a página e a lista de seleção"
										style="border:0" />
								</h:outputLink>
							</h:panelGroup>
							<h:message for="selectSubClassInput" styleClass="errorMessage" />

							<%-- Subclasse em por link --%>
							<h:panelGroup
								rendered="#{!item.info.editShowList && item.info.entity && !item.info.collection}">
								<%-- Subclasse em por link --%>
								<h:panelGroup rendered="#{!item.info.editShowEmbedded}">
									<h:inputText id="idSubClassInput" value="#{item.value.id}"
										styleClass="queryInputSelectOne"
										title="Aperte a tecla 'p' ou dê um clique na caixa de texto para abrir a pesquisa"
										required="#{item.info.required}" size="6"
										onclick="javascript:openSelectOneId('#{item.info.type.name}',this.value,this)"
										onkeypress="javascript:return checkKeyClick(this, event, KEY_OPEN_QUERY);"
										style="text-align:right;"
										onblur="javascript: if(!isNumber(this.value))this.value='';" />
									<h:graphicImage value="../../public/basic/img/query_open_select.png"
										title="Aperte a tecla 'p' ou dê um clique na caixa de texto para abrir a pesquisa"
										style="border:0" />
									<h:outputLink
										onclick="javascript:linkRetrieve('#{item.info.type.name}', '#{item.value.asEntity.id}')"
										rendered="#{not item.value.valueNull}">
										<h:outputText value="  #{item.value.asString}"
											title="Visualiza mais informações de #{item.info.label}" />

									</h:outputLink>
								</h:panelGroup>
								<%-- Subclasse OneToOne --%>

								<h:panelGroup rendered="#{item.info.editShowEmbedded}">
									<%-- ======================================================================================================= --%>
									<h:dataTable value="#{item.value.asEntity.properties}"
										var='itemOneToOne' width="100%" styleClass="tableView"
										rowClasses="tableViewRowEven,tableViewRowOdd"
										columnClasses="tableViewColumnEven_OneToOne, tableViewColumnOdd_OneToOne, description"
										style="border: 1px solid #E9EDF5;">

										<h:column id="label"
											rendered="#{itemOneToOne.info.visible && !itemOneToOne.info.readOnly}">
											<h:outputLabel value="#{itemOneToOne.info.label}"
												title="#{itemOneToOne.info.hint}" />
											<h:outputLabel value="*"
												rendered="#{itemOneToOne.info.required}"
												styleClass="errorMessage" />
										</h:column>

										<h:column id="values"
											rendered="#{itemOneToOne.info.visible && !itemOneToOne.info.readOnly}">
											<%-- Exibe lista de escolha para campos primitivos  --%>
											<h:panelGroup
												rendered="#{itemOneToOne.info.editShowList && itemOneToOne.info.primitive && !itemOneToOne.info.isEnum()}">
												<h:selectOneMenu id="selectPrimitiveInput"
													value="#{itemOneToOne.value.asString}"
													required="#{itemOneToOne.info.required}">
													<f:selectItems value="#{itemOneToOne.valuesList}" />
												</h:selectOneMenu>
												<h:message for="selectPrimitiveInput"
													styleClass="errorMessage" />
											</h:panelGroup>

											<%-- Exibe lista de escolha para ENUMS  --%>
											<h:panelGroup
												rendered="#{itemOneToOne.info.isEnum() && !itemOneToOne.info.collection}">
												<h:selectOneMenu id="selectEnumInput"
													value="#{itemOneToOne.value.id}"
													required="#{itemOneToOne.info.required}">
													<f:selectItems value="#{itemOneToOne.valuesList}" />
												</h:selectOneMenu>
											</h:panelGroup>

											<%-- Exibe caixa de entrada de texto para campos primitivos  --%>
											<h:panelGroup
												rendered="#{!itemOneToOne.info.editShowList && itemOneToOne.info.primitive && !itemOneToOne.info.isEnum()}">
												<h:inputTextarea id="txtInput" style="width:100%"
													value="#{itemOneToOne.value.asString}"
													title="#{itemOneToOne.info.hint}"
													required="#{itemOneToOne.info.required}"
													rendered="#{itemOneToOne.info.isString() && itemOneToOne.info.size>255  && !itemOneToOne.info.hasEditMask}"
													disabled="#{itemOneToOne.info.readOnly}" rows="2" />
												<h:message for="txtInput" styleClass="errorMessage" />

												<h:inputText id="strInput" style="width:100%"
													value="#{itemOneToOne.value.asString}"
													title="#{itemOneToOne.info.hint}"
													required="#{itemOneToOne.info.required}"
													rendered="#{itemOneToOne.info.isString() && itemOneToOne.info.size<256  && !itemOneToOne.info.hasEditMask}"
													disabled="#{itemOneToOne.info.readOnly}"
													maxlength="#{itemOneToOne.info.size}" />
												<h:message for="strInput" styleClass="errorMessage" />

												<h:inputText id="strMaskInput"
													value="#{itemOneToOne.value.asString}"
													title="#{itemOneToOne.info.hint}"
													required="#{itemOneToOne.info.required}"
													rendered="#{itemOneToOne.info.isString() && itemOneToOne.info.hasEditMask}"
													disabled="#{itemOneToOne.info.readOnly}" size="20"
													maxlength="#{itemOneToOne.info.editMaskSize}"
													onkeypress="return keyPressMask(this,'#{itemOneToOne.info.editMask}')" />
												<h:message for="strMaskInput" styleClass="errorMessage" />

												<h:inputText id="intInput"
													value="#{itemOneToOne.value.asString}"
													title="#{itemOneToOne.info.hint}"
													required="#{itemOneToOne.info.required}"
													rendered="#{itemOneToOne.info.isInteger()}"
													disabled="#{itemOneToOne.info.readOnly}" size="10"
													onkeypress="return keyPressInt(this,event)">
													<f:validateLongRange minimum="-999999999"
														maximum="999999999" />
												</h:inputText>
												<h:message for="intInput" styleClass="errorMessage" />

												<h:inputText id="longInput"
													value="#{itemOneToOne.value.asString}"
													title="#{itemOneToOne.info.hint}"
													required="#{itemOneToOne.info.required}"
													rendered="#{itemOneToOne.info.isLong()}"
													disabled="#{itemOneToOne.info.readOnly}" size="10"
													onkeypress="return keyPressInt(this,event)">
													<f:validateLongRange minimum="-999999999"
														maximum="999999999" />
												</h:inputText>
												<h:message for="longInput" styleClass="errorMessage" />

												<h:inputText id="bigInput"
													value="#{itemOneToOne.value.asString}"
													title="#{itemOneToOne.info.hint}"
													required="#{itemOneToOne.info.required}"
													rendered="#{itemOneToOne.info.bigDecimal}"
													disabled="#{itemOneToOne.info.readOnly}" size="10"
													onkeypress="return keyPressFloat(this,event)" />
												<h:message for="bigInput" styleClass="errorMessage" />

												<h:inputText id="floatInput"
													value="#{itemOneToOne.value.asString}"
													title="#{itemOneToOne.info.hint}"
													required="#{itemOneToOne.info.required}"
													rendered="#{itemOneToOne.info.isFloat()}"
													disabled="#{itemOneToOne.info.readOnly}" size="10"
													onkeypress="return keyPressFloat(this,event)" />
												<h:message for="floatInput" styleClass="errorMessage" />

												<h:inputText id="doubleInput"
													value="#{itemOneToOne.value.asString}"
													title="#{itemOneToOne.info.hint}"
													required="#{itemOneToOne.info.required}"
													rendered="#{itemOneToOne.info.isDouble()}"
													disabled="#{itemOneToOne.info.readOnly}" size="10"
													onkeypress="return keyPressFloat(this,event)" />
												<h:message for="doubleInput" styleClass="errorMessage" />

												<h:inputText id="dateInput"
													value="#{itemOneToOne.value.asString}"
													title="#{itemOneToOne.info.hint}"
													required="#{itemOneToOne.info.required}"
													rendered="#{itemOneToOne.info.calendar}"
													disabled="#{itemOneToOne.info.readOnly}" size="15"
													onblur="return onblurCalendar(this,'#{itemOneToOne.info.editMask}')" />
												<h:message for="dateInput" styleClass="errorMessage" />
											</h:panelGroup>

											<%-- Exibe informações sobre a máscara do campo --%>
											<h:panelGroup
												rendered="#{itemOneToOne.info.hasEditMask && !itemOneToOne.info.isBoolean()}">
												<h:outputLabel value="#{itemOneToOne.info.editMask}"
													title="Utilize este formato para preencher o campo corretamente."
													style="color:orange;" />
												<%-- Lucio 04102007: Ao usar o tab, sempre o focu pára nele.
							h:outputLink value="javascript:popupPage('../basic/help/editMask.html', 500,600)">
							   <h:outputText value="[?]" title="Abre uma tela com ajuda"/>
							</h:outputLink --%>
											</h:panelGroup>

											<h:selectBooleanCheckbox id="bolInput"
												value="#{itemOneToOne.value.asBoolean}"
												title="#{itemOneToOne.info.hint}"
												required="#{itemOneToOne.info.required}"
												rendered="#{itemOneToOne.info.isBoolean()}"
												disabled="#{itemOneToOne.info.readOnly}">
											</h:selectBooleanCheckbox>
											<h:message for="bolInput" styleClass="errorMessage" />

											<%-- Subclasse em SelectMenu --%>
											<h:panelGroup
												rendered="#{itemOneToOne.info.editShowList && itemOneToOne.info.entity && !itemOneToOne.info.collection}">
												<h:selectOneMenu id="selectSubClassInput"
													value="#{itemOneToOne.value.id}"
													required="#{itemOneToOne.info.required}">
													<f:selectItems value="#{itemOneToOne.valuesList}" />
												</h:selectOneMenu>

												<h:outputLink
													value="javascript:linkRetrieve('#{itemOneToOne.info.type.name}', '#{itemOneToOne.value.asEntity.id}')"
													rendered="#{not itemOneToOne.value.valueNull}">
													<h:graphicImage value="../../public/basic/img/retrieve.png"
														title="Visualiza os detalhes do registro" style="border:0" />
												</h:outputLink>
												<h:outputLink
													value="javascript:linkCreatePopup('#{itemOneToOne.info.type.name}')">
													<h:graphicImage value="../../public/basic/img/create.png"
														title="Cria um novo registro na lista. Será necessário clicar em validar para atualizar a lista com o novo item"
														style="border:0" />
												</h:outputLink>
												<h:outputLink value="javascript:document.forms[0].submit()">
													<h:graphicImage value="../../public/basic/img/reload.png"
														title="Recarrega a página e a lista de seleção"
														style="border:0" />
												</h:outputLink>
											</h:panelGroup>
											<h:message for="selectSubClassInput"
												styleClass="errorMessage" />

											<%-- Subclasse em por link --%>
											<h:panelGroup
												rendered="#{!itemOneToOne.info.editShowList && itemOneToOne.info.entity && !itemOneToOne.info.collection}">

												<h:inputText id="idSubClassInput"
													value="#{itemOneToOne.value.id}"
													styleClass="queryInputSelectOne"
													title="Aperte a tecla 'p' ou dê um clique na caixa de texto para abrir a pesquisa"
													required="#{itemOneToOne.info.required}" size="6"
													onclick="javascript:openSelectOneId('#{itemOneToOne.info.type.name}',this.value,this)"
													onkeypress="javascript:return checkKeyClick(this, event, KEY_OPEN_QUERY);"
													style="text-align:right;"
													onblur="javascript: if(!isNumber(this.value))this.value='';" />
												<h:graphicImage value="../../public/basic/img/query_open_select.png"
													title="Aperte a tecla 'p' ou dê um clique na caixa de texto para abrir a pesquisa"
													style="border:0" />

												<h:outputLink
													onclick="javascript:linkRetrieve('#{itemOneToOne.info.type.name}', '#{itemOneToOne.value.asEntity.id}')"
													rendered="#{not itemOneToOne.value.valueNull}">
													<h:outputText value="  #{itemOneToOne.value.asString}"
														title="Visualiza mais informações de #{itemOneToOne.info.label}" />
												</h:outputLink>
											</h:panelGroup>
											<h:message for="idSubClassInput" styleClass="errorMessage" />

											<%-- Lista --%>
											<%-- Primitiva --%>
											<%-- Entidade --%>
											<h:panelGroup
												rendered="#{itemOneToOne.info.collection and itemOneToOne.info.entity and !itemOneToOne.info.editShowList}">
												<h:outputLabel
													value="Para adicionar um novo item na lista de #{itemOneToOne.info.label}, pesquise o item aqui" />

												<h:inputText id="idCollInput"
													value="#{itemOneToOne.value.asEntityCollection.runId}"
													styleClass="queryInputSelectOne"
													title="Aperte a tecla 'p' ou dê um clique na caixa de texto para abrir a pesquisa"
													size="6"
													onclick="javascript:openSelectOneId('#{itemOneToOne.info.type.name}',this.value,this)"
													onkeypress="javascript:return checkKeyClick(this, event, KEY_OPEN_QUERY);"
													style="text-align:right;"
													onblur="javascript: if(!isNumber(this.value))this.value='';" />
												<h:graphicImage value="../../public/basic/img/query_open_select.png"
													title="Aperte a tecla 'p' ou dê um clique na caixa de texto para abrir a pesquisa"
													style="border:0" />
												<h:outputLabel value=" e depois clique em " />
												<h:commandLink action="#{createBean.doAddToCollection}">
													<h:outputText value="adicionar." />
													<f:param name="collProperty"
														value="#{item.info.name}.#{itemOneToOne.info.name}" />
												</h:commandLink>
											</h:panelGroup>
											<h:panelGroup
												rendered="#{itemOneToOne.info.collection and itemOneToOne.info.entity and itemOneToOne.info.editShowList}">
												<h:outputLabel
													value="Para adicionar um novo item na lista de #{itemOneToOne.info.label}, selecione o item aqui" />

												<h:selectOneMenu id="selectIdCollInput"
													value="#{itemOneToOne.value.asEntityCollection.runId}">
													<f:selectItems value="#{itemOneToOne.valuesList}" />
												</h:selectOneMenu>
												<h:outputLink
													value="javascript:linkCreatePopup('#{itemOneToOne.info.type.name}')">
													<h:graphicImage value="../../public/basic/img/create.png"
														title="Cria um novo registro na lista. Será necessário clicar em validar para atualizar a lista com o novo item"
														style="border:0" />
												</h:outputLink>
												<h:outputLink value="javascript:document.forms[0].submit()">
													<h:graphicImage value="../../public/basic/img/reload.png"
														title="Recarrega a página e a lista de seleção"
														style="border:0" />
												</h:outputLink>
												<h:outputLabel value=" e depois clique em " />
												<h:commandLink action="#{createBean.doAddToCollection}">
													<h:outputText value="adicionar." />
													<f:param name="collProperty"
														value="#{item.info.name}.#{itemOneToOne.info.name}" />
												</h:commandLink>
											</h:panelGroup>
											<h:dataTable width="100%"
												value="#{itemOneToOne.value.asEntityCollection.array}"
												var='col' styleClass="tableCollectionUpdate"
												footerClass="tableCollectionUpdateHeader"
												rowClasses="tableViewRowEven,tableViewRowOdd"
												columnClasses="tableViewColumnOdd"
												style="border-collapse: collapse"
												rendered="#{itemOneToOne.info.collection and itemOneToOne.info.entity}">

												<h:column>
													<h:commandLink
														action="#{createBean.doRemoveFromCollection}">
														<h:graphicImage value="../../public/basic/img/delete.png"
															title="Remove esta entidade da lista" style="border:0" />
														<f:param name="collProperty"
															value="#{item.info.name}.#{itemOneToOne.info.name}" />
														<f:param name="collItemId" value="#{col.id}" />
													</h:commandLink>

													<h:outputLink
														value="javascript:linkRetrieve('#{col.info.type.name}', '#{col.id}')">
														<h:outputText value="#{col.object}:"
															title="#{col.info.hint}" />
													</h:outputLink>
												</h:column>
											</h:dataTable>
											<h:outputText id="help"
												value="<br/>#{itemOneToOne.info.description}" escape="false" />
										</h:column>

									</h:dataTable>

									<%-- ======================================================================================================= --%>
									<%-- Subclasse OneToOne --%>

								</h:panelGroup>
							</h:panelGroup>
							<h:message for="idSubClassInput" styleClass="errorMessage" />

							<%-- Lista --%>
							<%-- Primitiva --%>
							<%-- Entidade --%>
							<h:panelGroup
								rendered="#{item.info.collection && item.info.entity && !item.info.editShowEmbedded && !item.info.editShowList}">
								<h:outputLabel
									value="Para adicionar um novo item na lista de #{item.info.label}, pesquise o item aqui" />

								<h:inputText id="idCollInput"
									value="#{item.value.asEntityCollection.runId}"
									styleClass="queryInputSelectOne"
									title="Aperte a tecla 'p' ou dê um clique na caixa de texto para abrir a pesquisa"
									size="6"
									onclick="javascript:openSelectOneId('#{item.info.type.name}',this.value,this)"
									onkeypress="javascript:return checkKeyClick(this, event, KEY_OPEN_QUERY);"
									style="text-align:right;"
									onblur="javascript: if(!isNumber(this.value))this.value='';" />
								<h:graphicImage value="../../public/basic/img/query_open_select.png"
									title="Aperte a tecla 'p' ou dê um clique na caixa de texto para abrir a pesquisa"
									style="border:0" />
								<h:outputLabel value=" e depois clique em " />
								<h:commandLink action="#{createBean.doAddToCollection}">
									<h:outputText value="[adicionar]." />
									<f:param name="collProperty" value="#{item.info.name}" />
								</h:commandLink>
							</h:panelGroup>
							<h:panelGroup
								rendered="#{item.info.collection && item.info.entity && !item.info.editShowEmbedded && item.info.editShowList}">
								<h:outputLabel
									value="Para adicionar um novo item na lista de #{item.info.label}, selecione o item aqui" />

								<h:selectOneMenu id="selectIdCollInput"
									value="#{item.value.asEntityCollection.runId}">
									<f:selectItems value="#{item.valuesList}" />
								</h:selectOneMenu>
								<h:outputLink
									value="javascript:linkCreatePopup('#{item.info.type.name}')">
									<h:graphicImage value="../../public/basic/img/create.png"
										title="Cria um novo registro na lista. Será necessário clicar em validar para atualizar a lista com o novo item"
										style="border:0" />
								</h:outputLink>
								<h:outputLink value="javascript:document.forms[0].submit()">
									<h:graphicImage value="../../public/basic/img/reload.png"
										title="Recarrega a página e a lista de seleção"
										style="border:0" />
								</h:outputLink>
								<h:outputLabel value=" e depois clique em " />
								<h:commandLink action="#{createBean.doAddToCollection}">
									<h:outputText value="adicionar." />
									<f:param name="collProperty" value="#{item.info.name}" />
								</h:commandLink>
							</h:panelGroup>
							<h:panelGroup
								rendered="#{item.info.collection and item.info.entity and item.info.editShowEmbedded}">
								<fieldset class="tableViewCreateGroup">
									<legend class="tableViewCreateHeader">
										<h:outputLabel value="Atalho para #{item.info.label}"
											title="#{item.info.hint}" />
									</legend>
									<%-- Collection OneToMany --%>
									<%-- ======================================================================================================= --%>
									<h:dataTable
										value="#{item.value.asEntityCollection.runEntity.properties}"
										var='itemOneToMany' width="100%" styleClass="tableView"
										rowClasses="tableViewRowEven,tableViewRowOdd"
										columnClasses="tableViewColumnEven_OneToMany, tableViewColumnOdd_OneToMany, description"
										style="border: 1px solid #E9EDF5;">

										<h:column id="label"
											rendered="#{itemOneToMany.info.visible && !itemOneToMany.info.readOnly}">
											<h:outputLabel value="#{itemOneToMany.info.label}"
												title="#{itemOneToMany.info.hint}" />
											<h:outputLabel value="*"
												rendered="#{itemOneToMany.info.required}"
												styleClass="errorMessage" />
										</h:column>

										<h:column id="values"
											rendered="#{itemOneToMany.info.visible && !itemOneToMany.info.readOnly}">
											<%-- Exibe lista de escolha para campos primitivos  --%>
											<h:panelGroup
												rendered="#{itemOneToMany.info.editShowList && itemOneToMany.info.primitive && !itemOneToMany.info.isEnum()}">
												<h:selectOneMenu id="selectPrimitiveInput"
													value="#{itemOneToMany.value.asString}">
													<f:selectItems value="#{itemOneToMany.valuesList}" />
												</h:selectOneMenu>
												<h:message for="selectPrimitiveInput"
													styleClass="errorMessage" />
											</h:panelGroup>

											<%-- Exibe lista de escolha para ENUMS  --%>
											<h:panelGroup rendered="#{itemOneToMany.info.isEnum()}">
												<h:selectOneMenu id="selectEnumInput"
													value="#{itemOneToMany.value.id}"
													required="#{itemOneToMany.info.required}">
													<f:selectItems value="#{itemOneToMany.valuesList}" />
												</h:selectOneMenu>
											</h:panelGroup>

											<%-- Exibe caixa de entrada de texto para campos primitivos  --%>
											<%-- Este bloco não possui a validação Required, somente a marcação --%>
											<h:panelGroup
												rendered="#{!itemOneToMany.info.editShowList && itemOneToMany.info.primitive && !itemOneToMany.info.isEnum()}">
												<h:inputTextarea id="txtInput" style="width:100%"
													value="#{itemOneToMany.value.asString}"
													title="#{itemOneToMany.info.hint}"
													rendered="#{itemOneToMany.info.isString() && itemOneToMany.info.size>255  && !itemOneToMany.info.hasEditMask}"
													disabled="#{itemOneToMany.info.readOnly}" rows="2" />
												<h:message for="txtInput" styleClass="errorMessage" />

												<h:inputText id="strInput" style="width:100%"
													value="#{itemOneToMany.value.asString}"
													title="#{itemOneToMany.info.hint}"
													rendered="#{itemOneToMany.info.isString() && itemOneToMany.info.size<256  && !itemOneToMany.info.hasEditMask}"
													disabled="#{itemOneToMany.info.readOnly}"
													maxlength="#{itemOneToMany.info.size}" />
												<h:message for="strInput" styleClass="errorMessage" />

												<h:inputText id="strMaskInput"
													value="#{itemOneToMany.value.asString}"
													title="#{itemOneToMany.info.hint}"
													rendered="#{itemOneToMany.info.isString() && itemOneToMany.info.hasEditMask}"
													disabled="#{itemOneToMany.info.readOnly}" size="20"
													maxlength="#{itemOneToMany.info.editMaskSize}"
													onkeypress="return keyPressMask(this,'#{itemOneToMany.info.editMask}')" />
												<h:message for="strMaskInput" styleClass="errorMessage" />

												<h:inputText id="intInput"
													value="#{itemOneToMany.value.asString}"
													title="#{itemOneToMany.info.hint}"
													rendered="#{itemOneToMany.info.isInteger()}"
													disabled="#{itemOneToMany.info.readOnly}" size="10"
													onkeypress="return keyPressInt(this,event)">
													<f:validateLongRange minimum="-999999999"
														maximum="999999999" />
												</h:inputText>
												<h:message for="intInput" styleClass="errorMessage" />

												<h:inputText id="longInput"
													value="#{itemOneToMany.value.asString}"
													title="#{itemOneToMany.info.hint}"
													rendered="#{itemOneToMany.info.isLong()}"
													disabled="#{itemOneToMany.info.readOnly}" size="10"
													onkeypress="return keyPressInt(this,event)">
													<f:validateLongRange minimum="-999999999"
														maximum="999999999" />
												</h:inputText>
												<h:message for="longInput" styleClass="errorMessage" />

												<h:inputText id="bigInput"
													value="#{itemOneToMany.value.asString}"
													title="#{itemOneToMany.info.hint}"
													rendered="#{itemOneToMany.info.bigDecimal}"
													disabled="#{itemOneToMany.info.readOnly}" size="10"
													onkeypress="return keyPressFloat(this,event)" />
												<h:message for="bigInput" styleClass="errorMessage" />

												<h:inputText id="floatInput"
													value="#{itemOneToMany.value.asString}"
													title="#{itemOneToMany.info.hint}"
													rendered="#{itemOneToMany.info.isFloat()}"
													disabled="#{itemOneToMany.info.readOnly}" size="10"
													onkeypress="return keyPressFloat(this,event)" />
												<h:message for="floatInput" styleClass="errorMessage" />

												<h:inputText id="doubleInput"
													value="#{itemOneToMany.value.asString}"
													title="#{itemOneToMany.info.hint}"
													rendered="#{itemOneToMany.info.isDouble()}"
													disabled="#{itemOneToMany.info.readOnly}" size="10"
													onkeypress="return keyPressFloat(this,event)" />
												<h:message for="doubleInput" styleClass="errorMessage" />

												<h:inputText id="dateInput"
													value="#{itemOneToMany.value.asString}"
													title="#{itemOneToMany.info.hint}"
													rendered="#{itemOneToMany.info.calendar}"
													disabled="#{itemOneToMany.info.readOnly}" size="14"
													onblur="return onblurCalendar(this,'#{itemOneToMany.info.editMask}')" />
												<h:message for="dateInput" styleClass="errorMessage" />
											</h:panelGroup>

											<%-- Exibe informações sobre a máscara do campo --%>
											<h:panelGroup
												rendered="#{itemOneToMany.info.hasEditMask && !itemOneToMany.info.isBoolean()}">
												<h:outputLabel value="#{itemOneToMany.info.editMask}"
													title="Utilize este formato para preencher o campo corretamente."
													style="color:orange;" />
												<%-- Lucio 04102007: Ao usar o tab, sempre o focu pára nele.
							<h:outputLink value="javascript:popupPage('../basic/help/editMask.html', 500,600)">
							   <h:outputText value="[?]" title="Abre uma tela com ajuda"/>
							</h:outputLink --%>
											</h:panelGroup>

											<h:selectBooleanCheckbox id="bolInput"
												value="#{itemOneToMany.value.asBoolean}"
												title="#{itemOneToMany.info.hint}"
												rendered="#{itemOneToMany.info.isBoolean()}"
												disabled="#{itemOneToMany.info.readOnly}">
											</h:selectBooleanCheckbox>
											<h:message for="bolInput" styleClass="errorMessage" />

											<%-- Subclasse em SelectMenu --%>
											<h:panelGroup
												rendered="#{itemOneToMany.info.editShowList && itemOneToMany.info.entity && !itemOneToMany.info.collection}">
												<h:selectOneMenu id="selectSubClassInput"
													value="#{itemOneToMany.value.id}">
													<f:selectItems value="#{itemOneToMany.valuesList}" />
												</h:selectOneMenu>

												<h:outputLink
													value="javascript:linkRetrieve('#{itemOneToMany.info.type.name}', '#{itemOneToMany.value.asEntity.id}')"
													rendered="#{not itemOneToMany.value.valueNull}">
													<h:graphicImage value="../../public/basic/img/retrieve.png"
														title="Visualiza os detalhes do registro" style="border:0" />
												</h:outputLink>
												<h:outputLink
													value="javascript:linkCreatePopup('#{itemOneToMany.info.type.name}')">
													<h:graphicImage value="../../public/basic/img/create.png"
														title="Cria um novo registro na lista. Será necessário clicar em validar para atualizar a lista com o novo item"
														style="border:0" />
												</h:outputLink>
												<h:outputLink value="javascript:document.forms[0].submit()">
													<h:graphicImage value="../../public/basic/img/reload.png"
														title="Recarrega a página e a lista de seleção"
														style="border:0" />
												</h:outputLink>
											</h:panelGroup>
											<h:message for="selectSubClassInput"
												styleClass="errorMessage" />

											<%-- Subclasse em por link --%>
											<h:panelGroup
												rendered="#{!itemOneToMany.info.editShowList && itemOneToMany.info.entity && !itemOneToMany.info.collection}">

												<h:inputText id="idSubClassInput"
													value="#{itemOneToMany.value.id}"
													styleClass="queryInputSelectOne"
													title="Aperte a tecla 'p' ou dê um clique na caixa de texto para abrir a pesquisa"
													size="6"
													onclick="javascript:openSelectOneId('#{itemOneToMany.info.type.name}',this.value,this)"
													onkeypress="javascript:return checkKeyClick(this, event, KEY_OPEN_QUERY);"
													style="text-align:right;"
													onblur="javascript: if(!isNumber(this.value))this.value='';" />
												<h:graphicImage value="../../public/basic/img/query_open_select.png"
													title="Aperte a tecla 'p' ou dê um clique na caixa de texto para abrir a pesquisa"
													style="border:0" />
												<h:outputLink
													onclick="javascript:linkRetrieve('#{itemOneToMany.info.type.name}', '#{itemOneToMany.value.asEntity.id}')"
													rendered="#{not itemOneToMany.value.valueNull}">
													<h:outputText value="  #{itemOneToMany.value.asString}"
														title="Visualiza mais informações de #{itemOneToMany.info.label}" />
												</h:outputLink>
											</h:panelGroup>
											<h:message for="idSubClassInput" styleClass="errorMessage" />

											<%-- Lista --%>
											<%-- Primitiva --%>
											<h:panelGroup
												rendered="#{itemOneToOne.info.collection and !itemOneToOne.info.entity}">
												<h:selectManyCheckbox value="#{itemOneToOne.value.ids}"
													styleClass="tableViewColumnOdd">
													<f:selectItems value="#{itemOneToOne.valuesList}" />
												</h:selectManyCheckbox>
											</h:panelGroup>
											<%-- Entidade --%>
											<h:panelGroup
												rendered="#{itemOneToMany.info.collection and itemOneToMany.info.entity}">
												<h:outputLabel
													value="Para adicionar um novo item na lista de #{itemOneToMany.info.label}, pesquise o item aqui" />

												<h:inputText id="idCollInput"
													value="#{itemOneToMany.value.asEntityCollection.runId}"
													styleClass="queryInputSelectOne"
													title="Aperte a tecla 'p' ou dê um clique na caixa de texto para abrir a pesquisa"
													size="6"
													onclick="javascript:openSelectOneId('#{itemOneToMany.info.type.name}',this.value,this)"
													onkeypress="javascript:return checkKeyClick(this, event, KEY_OPEN_QUERY);"
													style="text-align:right;"
													onblur="javascript: if(!isNumber(this.value))this.value='';" />
												<h:graphicImage value="../../public/basic/img/query_open_select.png"
													title="Aperte a tecla 'p' ou dê um clique na caixa de texto para abrir a pesquisa"
													style="border:0" />
												<h:outputLabel value=" e depois clique em " />
												<h:commandLink action="#{createBean.doAddToCollection}">
													<h:outputText value="adicionar." />
													<f:param name="collProperty"
														value="#{item.info.name}.#{itemOneToMany.info.name}" />
												</h:commandLink>
											</h:panelGroup>
											<h:dataTable width="100%"
												value="#{itemOneToMany.value.asEntityCollection.array}"
												var='col' styleClass="tableCollectionUpdate"
												footerClass="tableCollectionUpdateHeader"
												rowClasses="tableViewRowEven,tableViewRowOdd"
												columnClasses="tableViewColumnOdd"
												style="border-collapse: collapse"
												rendered="#{itemOneToMany.info.collection and itemOneToMany.info.entity}">

												<h:column>
													<h:commandLink
														action="#{createBean.doRemoveFromCollection}">
														<h:graphicImage value="../../public/basic/img/delete.png"
															title="Remove esta entidade da lista" style="border:0" />
														<f:param name="collProperty"
															value="#{item.info.name}.#{itemOneToMany.info.name}" />
														<f:param name="collItemId" value="#{col.id}" />
													</h:commandLink>

													<h:outputLink
														value="javascript:linkRetrieve('#{col.info.type.name}', '#{col.id}')">
														<h:outputText value="#{col.object}:"
															title="#{col.info.hint}" />
													</h:outputLink>
												</h:column>
											</h:dataTable>
											<h:outputText id="help"
												value="<br/>#{itemOneToMany.info.description}"
												escape="false" />
										</h:column>

									</h:dataTable>
									<%-- ======================================================================================================= --%>
									<%-- Collection OneToMany --%>
									<h:commandLink action="#{createBean.doAddToCollection}">
										<h:commandButton value="Adicionar item na lista" />
										<f:param name="collProperty" value="#{item.info.name}" />
									</h:commandLink>
								</fieldset>
							</h:panelGroup>
							<h:dataTable width="100%"
								value="#{item.value.asEntityCollection.array}" var='col'
								styleClass="tableCollectionUpdate"
								footerClass="tableCollectionUpdateHeader"
								rowClasses="tableViewRowEven,tableViewRowOdd"
								columnClasses="tableViewColumnOdd"
								style="border-collapse: collapse"
								rendered="#{item.info.collection and item.info.entity}">

								<h:column>
									<h:commandLink action="#{createBean.doRemoveFromCollection}">
										<h:graphicImage value="../../public/basic/img/delete.png"
											title="Remove esta entidade da lista" style="border:0" />
										<f:param name="collProperty" value="#{item.info.name}" />
										<f:param name="collItemId" value="#{col.id}" />
									</h:commandLink>

									<%--h:outputLink value="javascript:linkRetrieve('#{col.info.type.name}', '#{col.id}')">
								</h:outputLink Lucio 02102007: Em tela de create o objeto é transiente na listra e nao pode haver link--%>
									<h:outputText value="#{col.object}:" title="#{col.info.hint}" />
								</h:column>
							</h:dataTable>
							<h:outputText id="help" value="<br/>#{item.info.description}"
								escape="false" />
						</h:column>

					</h:dataTable>
				</fieldset>
			</h:column>
		</h:dataTable>


		<h:outputLabel value="* Campos requeridos " styleClass="errorMessage" />

		<div id="EntityTitle_">
			<%--<SCRIPT>borderCreator.initDarkBorder('100%', '', 'center', 'top')</SCRIPT> --%>
			<h:commandButton value="Gravar"
				onclick="javascript:return confirm('Confirma gravar as alterações?')"
				action="#{createBean.actionUpdate}" />
			<h:commandButton value="Cancelar"
				onclick="javascript:if(confirm('Confirma abandonar as alterações?'))window.close();" />
			<h:commandButton value="Limpar" onclick="this.value='Aguarde...'"
				title="Limpa os dados, descartando as alterações realizadas até aqui"
				action="#{createBean.doReloadEntity}" />
			<h:commandButton value="Validar" onclick="this.value='Validando...'"
				title="Submete os dados digitados até aqui para uma validação"
				action="#{createBean.doValidateEntity}" immediate="false" />

		</div>
	</h:form>

</f:subview>
