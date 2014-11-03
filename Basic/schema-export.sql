create table auditorship_crud (id bigint not null auto_increment, description varchar(255), ocurrencyDate datetime, terminal varchar(255), created bit, deleted bit, entityId bigint, updated bit, applicationUser bigint, applicationEntity bigint, primary key (id)) ENGINE=InnoDB;
create table auditorship_process (id bigint not null auto_increment, description varchar(255), ocurrencyDate datetime, terminal varchar(255), applicationUser bigint, applicationProcess bigint, primary key (id)) ENGINE=InnoDB;
create table basic_bairro (id bigint not null auto_increment, nome varchar(50), primary key (id)) ENGINE=InnoDB;
create table basic_cargo (id bigint not null auto_increment, nome varchar(100), primary key (id)) ENGINE=InnoDB;
create table basic_cnae (id bigint not null auto_increment, codigo varchar(7), nome varchar(255), primary key (id)) ENGINE=InnoDB;
create table basic_contador (id bigint not null auto_increment, crc varchar(10), dataCadastro datetime, fisica bigint, observacoes bigint, escritorioContabil bigint, primary key (id)) ENGINE=InnoDB;
create table basic_contato (id bigint not null auto_increment, nomeContato varchar(50), cargo bigint, primary key (id)) ENGINE=InnoDB;
create table basic_contrato (discriminator varchar(3) not null, id bigint not null auto_increment, codigo varchar(20), dataInicio datetime, dataRescisao datetime, dataVencimento datetime, inativo bit, pessoa bigint, categoria bigint, observacoes bigint, representante bigint, primary key (id)) ENGINE=InnoDB;
create table basic_contrato_categoria (discriminator varchar(4) not null, id bigint not null auto_increment, nome varchar(50), observacoes bigint, primary key (id)) ENGINE=InnoDB;
create table basic_contrato_contato (id bigint not null auto_increment, dataHora datetime, dataRetorno datetime, observacoes varchar(255), retornado bit, retornoCliente bit, contatoRetorno bigint, motivo bigint, contrato bigint, representante bigint, primary key (id)) ENGINE=InnoDB;
create table basic_contrato_contato_motivo (id bigint not null auto_increment, descricao varchar(50), primary key (id)) ENGINE=InnoDB;
create table basic_endereco (id bigint not null auto_increment, caixaPostal varchar(20), cep varchar(8), complemento varchar(50), numero integer, logradouro bigint, bairro bigint, municipio bigint, primary key (id)) ENGINE=InnoDB;
create table basic_endereco_categoria (id bigint not null auto_increment, nome varchar(20), primary key (id)) ENGINE=InnoDB;
create table basic_escritorio_contabil (id bigint not null auto_increment, dataCadastro datetime, juridica bigint, observacoes bigint, primary key (id)) ENGINE=InnoDB;
create table basic_feriado (discriminator varchar(1) not null, id bigint not null auto_increment, ano integer, descricao varchar(100), dia integer, fixo bit, mes varchar(10), anoFinal integer, diaFinal integer, mesFinal varchar(10), primary key (id)) ENGINE=InnoDB;
create table basic_fisica_necessidade_especial (fisica bigint not null, necessidadesEspeciais bigint not null) ENGINE=InnoDB;
create table basic_funcionario (id bigint not null auto_increment, dataAdmissao datetime, dataDemissao datetime, cargo bigint, fisica bigint, juridica bigint, primary key (id)) ENGINE=InnoDB;
create table basic_grau_parentesco (id bigint not null auto_increment, nome varchar(255), primary key (id)) ENGINE=InnoDB;
create table basic_grupo_representante (id bigint not null auto_increment, nome varchar(50), juridica bigint, primary key (id)) ENGINE=InnoDB;
create table basic_grupo_representante_comissao (discriminator varchar(3) not null, id bigint not null auto_increment, percentual double precision, valor numeric(19,2), grupoRepresentante bigint, primary key (id)) ENGINE=InnoDB;
create table basic_logradouro (id bigint not null auto_increment, nome varchar(50), tipoLogradouro varchar(11), primary key (id)) ENGINE=InnoDB;
create table basic_municipio (id bigint not null auto_increment, codigoIbge varchar(7), nome varchar(50), uf varchar(2), primary key (id)) ENGINE=InnoDB;
create table basic_necessidade_especial (id bigint not null auto_increment, descricao varchar(255), primary key (id)) ENGINE=InnoDB;
create table basic_observacoes (id bigint not null auto_increment, exibir bit, observacoes text, primary key (id)) ENGINE=InnoDB;
create table basic_pais (id bigint not null auto_increment, nome varchar(50), sigla2 varchar(2), sigla3 varchar(3), primary key (id)) ENGINE=InnoDB;
create table basic_pessoa (discriminator varchar(1) not null, id bigint not null auto_increment, apelido varchar(255), codigoSeguranca varchar(3), dataCadastro datetime, dataFinal datetime, dataInicial datetime, documento varchar(15), email varchar(70), nome varchar(255), senha varchar(50), www varchar(70), capitalSocial Decimal(15,4), cmc varchar(7), ie varchar(14), numeroFuncionarios integer, tipoEstabelecimento varchar(10), canhota bit, documentoMilitarDataExpedicao date, documentoMilitarNumero varchar(20), documentoMilitarOrgao varchar(3), documentoMilitarTipo varchar(3), estadoCivil varchar(10), nomeMae varchar(100), nomePai varchar(100), pisPasep varchar(11), rgDataExpedicao datetime, rgNumero varchar(10), rgOrgaoExpedidor varchar(5), rgUfExpedidor varchar(2), sexo varchar(1), escritorioContabil bigint, cnae bigint, enderecoCorrespondencia bigint, applicationUser bigint, responsavelCpf bigint, contador bigint, nacionalidade bigint, naturalidade bigint, profissao bigint, contato bigint, primary key (id)) ENGINE=InnoDB;
create table basic_pessoa_endereco (id bigint not null auto_increment, endereco bigint, categoria bigint, pessoa bigint, primary key (id)) ENGINE=InnoDB;
create table basic_profissao (id bigint not null auto_increment, codigo varchar(4), nome varchar(100), primary key (id)) ENGINE=InnoDB;
create table basic_representante (id bigint not null auto_increment, grupoRepresentante bigint, fisica bigint, primary key (id)) ENGINE=InnoDB;
create table basic_responsavel_cpf (id bigint not null auto_increment, nome varchar(255), grauParentesco bigint, primary key (id)) ENGINE=InnoDB;
create table basic_socio (id bigint not null auto_increment, dataEntrada datetime, dataSaida datetime, juridica bigint, cargo bigint, fisica bigint, primary key (id)) ENGINE=InnoDB;
create table basic_telefone (id bigint not null auto_increment, ddd varchar(4), numero varchar(8), ramal varchar(5), pessoa_id bigint, tipoTelefone bigint, primary key (id)) ENGINE=InnoDB;
create table basic_tipo_telefone (id bigint not null auto_increment, nome varchar(20), primary key (id)) ENGINE=InnoDB;
create table framework_document_model_entity (id bigint not null auto_increment, date datetime, description varchar(255), name varchar(50), source Text, applicationUser bigint, applicationEntity bigint, primary key (id)) ENGINE=InnoDB;
create table framework_label_address (id bigint not null auto_increment, line1 varchar(150), line2 varchar(150), line3 varchar(150), line4 varchar(150), line5 varchar(150), ocurrencyDate datetime, print bit, applicationEntity bigint, applicationUser bigint, addressLabelGroup bigint, primary key (id)) ENGINE=InnoDB;
create table framework_label_address_group (id bigint not null auto_increment, name varchar(100), applicationUser bigint, primary key (id)) ENGINE=InnoDB;
create table framework_label_model (id bigint not null auto_increment, envelope bit, fontName varchar(50), fontSize integer, horizontalDistance float, labelHeight float, labelWidth float, marginLeft float, marginTop float, name varchar(50), pageHeight float, pageWidth float, verticalDistance float, primary key (id)) ENGINE=InnoDB;
create table framework_label_model_entity (id bigint not null auto_increment, description varchar(255), line1 varchar(255), line2 varchar(255), line3 varchar(255), line4 varchar(255), line5 varchar(255), name varchar(50), applicationEntity bigint, primary key (id)) ENGINE=InnoDB;
create table framework_order_condiction (id bigint not null auto_increment, active bit, orderDirection integer, propertyPath varchar(200), userReport bigint, orderIndex integer, primary key (id)) ENGINE=InnoDB;
create table framework_page_condiction (id bigint not null, itemsCount integer, page integer, pageSize integer, primary key (id)) ENGINE=InnoDB;
create table framework_parent_condiction (id bigint not null, property varchar(50), applicationEntity bigint, primary key (id)) ENGINE=InnoDB;
create table framework_query_condiction (id bigint not null auto_increment, active bit, closePar bit, initOperator integer, openPar bit, operatorId integer, propertyPath varchar(200), value1 varchar(50), value2 varchar(50), userReport bigint, orderIndex integer, primary key (id)) ENGINE=InnoDB;
create table framework_result_condiction (id bigint not null auto_increment, propertyPath varchar(200), resultIndex integer, userReport bigint, primary key (id)) ENGINE=InnoDB;
create table framework_user_report (id bigint not null auto_increment, date datetime, description Text, filterCondiction varchar(50), hqlWhereCondiction Text, name varchar(100), applicationUser bigint, applicationEntity bigint, primary key (id)) ENGINE=InnoDB;
create table security_entity (id bigint not null auto_increment, className varchar(200), description text, hint varchar(255), label varchar(100), name varchar(100), runQueryOnOpen bit, applicationModule bigint, primary key (id)) ENGINE=InnoDB;
create table security_entity_property (id bigint not null auto_increment, allowSubQuery bit, colorName varchar(50), defaultValue varchar(50), description text, displayFormat varchar(50), editMask varchar(50), editShowList bit, hint varchar(255), indexGroup integer, indexProperty integer, label varchar(50), maximum double precision, minimum double precision, name varchar(50), readOnly bit, required bit, valuesList varchar(255), visible bit, primary key (id)) ENGINE=InnoDB;
create table security_entity_property_group (id bigint not null auto_increment, indexGroup integer, name varchar(100), applicationEntity bigint, primary key (id)) ENGINE=InnoDB;
create table security_group (id bigint not null auto_increment, name varchar(50), primary key (id)) ENGINE=InnoDB;
create table security_module (id bigint not null auto_increment, name varchar(50), primary key (id)) ENGINE=InnoDB;
create table security_process (id bigint not null auto_increment, description Text, hint varchar(255), label varchar(100), name varchar(50), applicationModule bigint, primary key (id)) ENGINE=InnoDB;
create table security_process_runnable_entities (applicationProcess bigint not null, applicationEntity bigint not null) ENGINE=InnoDB;
create table security_right_crud (id bigint not null auto_increment, createAllowed bit, deleteAllowed bit, retrieveAllowed bit, updateAllowed bit, applicationEntity bigint, securityGroup bigint, primary key (id)) ENGINE=InnoDB;
create table security_right_process (id bigint not null auto_increment, executeAllowed bit, securityGroup bigint, applicationProcess bigint, primary key (id)) ENGINE=InnoDB;
create table security_user (id bigint not null auto_increment, inactive bit, login varchar(20) unique, name varchar(50), password varchar(50), primary key (id)) ENGINE=InnoDB;
create table security_user_group (applicationUser bigint not null, securityGroup bigint not null, primary key (securityGroup, applicationUser)) ENGINE=InnoDB;
alter table auditorship_crud add index applicationEntity (applicationEntity), add constraint auditorship_crud_applicationEntity foreign key (applicationEntity) references security_entity (id);
alter table auditorship_crud add index applicationUser (applicationUser), add constraint auditorship_crud_applicationUser foreign key (applicationUser) references security_user (id);
alter table auditorship_process add index applicationProcess (applicationProcess), add constraint auditorship_process_applicationProcess foreign key (applicationProcess) references security_process (id);
alter table auditorship_process add index applicationUser (applicationUser), add constraint auditorship_process_applicationUser foreign key (applicationUser) references security_user (id);
alter table basic_contador add index escritorioContabil (escritorioContabil), add constraint basic_contador_escritorioContabil foreign key (escritorioContabil) references basic_escritorio_contabil (id);
alter table basic_contador add index observacoes (observacoes), add constraint basic_contador_observacoes foreign key (observacoes) references basic_observacoes (id);
alter table basic_contador add index fisica (fisica), add constraint basic_contador_fisica foreign key (fisica) references basic_pessoa (id);
alter table basic_contato add index cargo (cargo), add constraint basic_contato_cargo foreign key (cargo) references basic_cargo (id);
alter table basic_contrato add index representante (representante), add constraint basic_contrato_representante foreign key (representante) references basic_representante (id);
alter table basic_contrato add index categoria (categoria), add constraint basic_contrato_categoria foreign key (categoria) references basic_contrato_categoria (id);
alter table basic_contrato add index observacoes (observacoes), add constraint basic_contrato_observacoes foreign key (observacoes) references basic_observacoes (id);
alter table basic_contrato add index pessoa (pessoa), add constraint basic_contrato_pessoa foreign key (pessoa) references basic_pessoa (id);
alter table basic_contrato_categoria add index observacoes (observacoes), add constraint basic_contrato_categoria_observacoes foreign key (observacoes) references basic_observacoes (id);
alter table basic_contrato_contato add index representante (representante), add constraint basic_contrato_contato_representante foreign key (representante) references basic_representante (id);
alter table basic_contrato_contato add index motivo (motivo), add constraint basic_contrato_contato_motivo foreign key (motivo) references basic_contrato_contato_motivo (id);
alter table basic_contrato_contato add index contrato (contrato), add constraint basic_contrato_contato_contrato foreign key (contrato) references basic_contrato (id);
alter table basic_contrato_contato add index contatoRetorno (contatoRetorno), add constraint basic_contrato_contato_contatoRetorno foreign key (contatoRetorno) references basic_contrato_contato (id);
alter table basic_endereco add index municipio (municipio), add constraint basic_endereco_municipio foreign key (municipio) references basic_municipio (id);
alter table basic_endereco add index logradouro (logradouro), add constraint basic_endereco_logradouro foreign key (logradouro) references basic_logradouro (id);
alter table basic_endereco add index bairro (bairro), add constraint basic_endereco_bairro foreign key (bairro) references basic_bairro (id);
alter table basic_escritorio_contabil add index observacoes (observacoes), add constraint basic_escritorio_contabil_observacoes foreign key (observacoes) references basic_observacoes (id);
alter table basic_escritorio_contabil add index juridica (juridica), add constraint basic_escritorio_contabil_juridica foreign key (juridica) references basic_pessoa (id);
alter table basic_fisica_necessidade_especial add index FK79B0473C67533F9B (necessidadesEspeciais), add constraint basic_fisica_necessidade_especial_FK79B0473C67533F9B foreign key (necessidadesEspeciais) references basic_necessidade_especial (id);
alter table basic_fisica_necessidade_especial add index fisica (fisica), add constraint basic_fisica_necessidade_especial_fisica foreign key (fisica) references basic_pessoa (id);
alter table basic_funcionario add index fisica (fisica), add constraint basic_funcionario_fisica foreign key (fisica) references basic_pessoa (id);
alter table basic_funcionario add index cargo (cargo), add constraint basic_funcionario_cargo foreign key (cargo) references basic_cargo (id);
alter table basic_funcionario add index juridica (juridica), add constraint basic_funcionario_juridica foreign key (juridica) references basic_pessoa (id);
alter table basic_grupo_representante add index juridica (juridica), add constraint basic_grupo_representante_juridica foreign key (juridica) references basic_pessoa (id);
alter table basic_grupo_representante_comissao add index grupoRepresentante (grupoRepresentante), add constraint basic_grupo_representante_comissao_grupoRepresentante foreign key (grupoRepresentante) references basic_grupo_representante (id);
alter table basic_pessoa add index enderecoCorrespondencia (enderecoCorrespondencia), add constraint basic_pessoa_enderecoCorrespondencia foreign key (enderecoCorrespondencia) references basic_endereco (id);
alter table basic_pessoa add index cnae (cnae), add constraint basic_pessoa_cnae foreign key (cnae) references basic_cnae (id);
alter table basic_pessoa add index responsavelCpf (responsavelCpf), add constraint basic_pessoa_responsavelCpf foreign key (responsavelCpf) references basic_responsavel_cpf (id);
alter table basic_pessoa add index contato (contato), add constraint basic_pessoa_contato foreign key (contato) references basic_contato (id);
alter table basic_pessoa add index naturalidade (naturalidade), add constraint basic_pessoa_naturalidade foreign key (naturalidade) references basic_municipio (id);
alter table basic_pessoa add index escritorioContabil (escritorioContabil), add constraint basic_pessoa_escritorioContabil foreign key (escritorioContabil) references basic_escritorio_contabil (id);
alter table basic_pessoa add index profissao (profissao), add constraint basic_pessoa_profissao foreign key (profissao) references basic_profissao (id);
alter table basic_pessoa add index nacionalidade (nacionalidade), add constraint basic_pessoa_nacionalidade foreign key (nacionalidade) references basic_pais (id);
alter table basic_pessoa add index contador (contador), add constraint basic_pessoa_contador foreign key (contador) references basic_contador (id);
alter table basic_pessoa add index applicationUser (applicationUser), add constraint basic_pessoa_applicationUser foreign key (applicationUser) references security_user (id);
alter table basic_pessoa_endereco add index categoria (categoria), add constraint basic_pessoa_endereco_categoria foreign key (categoria) references basic_endereco_categoria (id);
alter table basic_pessoa_endereco add index pessoa (pessoa), add constraint basic_pessoa_endereco_pessoa foreign key (pessoa) references basic_pessoa (id);
alter table basic_pessoa_endereco add index endereco (endereco), add constraint basic_pessoa_endereco_endereco foreign key (endereco) references basic_endereco (id);
alter table basic_representante add index fisica (fisica), add constraint basic_representante_fisica foreign key (fisica) references basic_pessoa (id);
alter table basic_representante add index grupoRepresentante (grupoRepresentante), add constraint basic_representante_grupoRepresentante foreign key (grupoRepresentante) references basic_grupo_representante (id);
alter table basic_responsavel_cpf add index grauParentesco (grauParentesco), add constraint basic_responsavel_cpf_grauParentesco foreign key (grauParentesco) references basic_grau_parentesco (id);
alter table basic_socio add index fisica (fisica), add constraint basic_socio_fisica foreign key (fisica) references basic_pessoa (id);
alter table basic_socio add index cargo (cargo), add constraint basic_socio_cargo foreign key (cargo) references basic_cargo (id);
alter table basic_socio add index juridica (juridica), add constraint basic_socio_juridica foreign key (juridica) references basic_pessoa (id);
alter table basic_telefone add index tipoTelefone (tipoTelefone), add constraint basic_telefone_tipoTelefone foreign key (tipoTelefone) references basic_tipo_telefone (id);
alter table basic_telefone add index pessoa_id (pessoa_id), add constraint basic_telefone_pessoa_id foreign key (pessoa_id) references basic_pessoa (id);
alter table framework_document_model_entity add index applicationEntity (applicationEntity), add constraint framework_document_model_entity_applicationEntity foreign key (applicationEntity) references security_entity (id);
alter table framework_document_model_entity add index applicationUser (applicationUser), add constraint framework_document_model_entity_applicationUser foreign key (applicationUser) references security_user (id);
alter table framework_label_address add index addressLabelGroup (addressLabelGroup), add constraint framework_label_address_addressLabelGroup foreign key (addressLabelGroup) references framework_label_address_group (id);
alter table framework_label_address add index applicationEntity (applicationEntity), add constraint framework_label_address_applicationEntity foreign key (applicationEntity) references security_entity (id);
alter table framework_label_address add index applicationUser (applicationUser), add constraint framework_label_address_applicationUser foreign key (applicationUser) references security_user (id);
alter table framework_label_address_group add index applicationUser (applicationUser), add constraint framework_label_address_group_applicationUser foreign key (applicationUser) references security_user (id);
alter table framework_label_model_entity add index applicationEntity (applicationEntity), add constraint framework_label_model_entity_applicationEntity foreign key (applicationEntity) references security_entity (id);
alter table framework_order_condiction add index userReport (userReport), add constraint framework_order_condiction_userReport foreign key (userReport) references framework_user_report (id);
alter table framework_parent_condiction add index applicationEntity (applicationEntity), add constraint framework_parent_condiction_applicationEntity foreign key (applicationEntity) references security_entity (id);
alter table framework_query_condiction add index userReport (userReport), add constraint framework_query_condiction_userReport foreign key (userReport) references framework_user_report (id);
alter table framework_result_condiction add index userReport (userReport), add constraint framework_result_condiction_userReport foreign key (userReport) references framework_user_report (id);
alter table framework_user_report add index applicationEntity (applicationEntity), add constraint framework_user_report_applicationEntity foreign key (applicationEntity) references security_entity (id);
alter table framework_user_report add index applicationUser (applicationUser), add constraint framework_user_report_applicationUser foreign key (applicationUser) references security_user (id);
alter table security_entity add index applicationModule (applicationModule), add constraint security_entity_applicationModule foreign key (applicationModule) references security_module (id);
alter table security_entity_property_group add index applicationEntity (applicationEntity), add constraint security_entity_property_group_applicationEntity foreign key (applicationEntity) references security_entity (id);
alter table security_process add index applicationModule (applicationModule), add constraint security_process_applicationModule foreign key (applicationModule) references security_module (id);
alter table security_process_runnable_entities add index applicationProcess (applicationProcess), add constraint security_process_runnable_entities_applicationProcess foreign key (applicationProcess) references security_process (id);
alter table security_process_runnable_entities add index FKCBA38094C7B60C2D (applicationEntity), add constraint security_process_runnable_entities_FKCBA38094C7B60C2D foreign key (applicationEntity) references security_entity (id);
alter table security_right_crud add index securityGroup (securityGroup), add constraint security_right_crud_securityGroup foreign key (securityGroup) references security_group (id);
alter table security_right_crud add index applicationEntity (applicationEntity), add constraint security_right_crud_applicationEntity foreign key (applicationEntity) references security_entity (id);
alter table security_right_process add index applicationProcess (applicationProcess), add constraint security_right_process_applicationProcess foreign key (applicationProcess) references security_process (id);
alter table security_right_process add index securityGroup (securityGroup), add constraint security_right_process_securityGroup foreign key (securityGroup) references security_group (id);
alter table security_user_group add index applicationGroup (securityGroup), add constraint security_user_group_applicationGroup foreign key (securityGroup) references security_group (id);
alter table security_user_group add index applicationUser (applicationUser), add constraint security_user_group_applicationUser foreign key (applicationUser) references security_user (id);