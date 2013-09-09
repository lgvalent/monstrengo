create table auditorship_crud (id bigint not null auto_increment, description varchar(1000), ocurrencyDate datetime, terminal varchar(255), created bit, deleted bit, entityId bigint, updated bit, applicationUser bigint, applicationEntity bigint, primary key (id)) ENGINE=InnoDB
create table auditorship_process (id bigint not null auto_increment, description varchar(1000), ocurrencyDate datetime, terminal varchar(255), applicationUser bigint, applicationProcess bigint, primary key (id)) ENGINE=InnoDB
create table auditorship_service (id bigint not null auto_increment, description varchar(1000), ocurrencyDate datetime, terminal varchar(255), serviceName varchar(50), applicationUser bigint, primary key (id)) ENGINE=InnoDB
create table basic_adesao_contrato (discriminator varchar(3) not null, id bigint not null auto_increment, codigo varchar(20), dataAdesao datetime, dataRemocao datetime, inativo bit, pessoa bigint, contrato_id bigint, observacoes bigint, primary key (id)) ENGINE=InnoDB
create table basic_bairro (id bigint not null auto_increment, nome varchar(50), primary key (id)) ENGINE=InnoDB
create table basic_cargo (id bigint not null auto_increment, nome varchar(100), primary key (id)) ENGINE=InnoDB
create table basic_cnae (id bigint not null auto_increment, codigo varchar(7), nome varchar(255), primary key (id)) ENGINE=InnoDB
create table basic_contador (id bigint not null auto_increment, crc varchar(10), dataCadastro datetime, escritorioContabil bigint, observacoes bigint, fisica bigint, primary key (id)) ENGINE=InnoDB
create table basic_contato (id bigint not null auto_increment, nomeContato varchar(50), cargo bigint, primary key (id)) ENGINE=InnoDB
create table basic_contrato (discriminator varchar(3) not null, id bigint not null auto_increment, codigo varchar(20), codigoContaContabil varchar(20), dataInicio datetime, dataRescisao datetime, dataVencimento datetime, inativo bit, diaVencimentoFatura integer, observacoes bigint, pessoa bigint, categoria bigint, contratoFinanceiroCategoria bigint, representante bigint, primary key (id)) ENGINE=InnoDB
create table basic_contrato_categoria (discriminator varchar(4) not null, id bigint not null auto_increment, nome varchar(50), diaVencimentoFatura integer, observacoes bigint, primary key (id)) ENGINE=InnoDB
create table basic_contrato_contato (id bigint not null auto_increment, dataHora datetime, dataRetorno datetime, observacoes varchar(255), retornado bit, retornoCliente bit, representante bigint, contrato bigint, motivo bigint, contatoRetorno bigint, primary key (id)) ENGINE=InnoDB
create table basic_contrato_contato_motivo (id bigint not null auto_increment, descricao varchar(50), primary key (id)) ENGINE=InnoDB
create table basic_endereco (id bigint not null auto_increment, caixaPostal varchar(20), cep varchar(8), complemento varchar(50), numero integer, bairro bigint, enderecoCategoria bigint, municipio bigint, logradouro bigint, pessoa bigint, primary key (id)) ENGINE=InnoDB
create table basic_endereco_categoria (id bigint not null auto_increment, nome varchar(20), primary key (id)) ENGINE=InnoDB
create table basic_escritorio_contabil (id bigint not null auto_increment, dataCadastro datetime, juridica bigint, observacoes bigint, primary key (id)) ENGINE=InnoDB
create table basic_feriado (discriminator varchar(1) not null, id bigint not null auto_increment, ano integer, descricao varchar(100), dia integer, fixo bit, mes varchar(10), anoFinal integer, diaFinal integer, mesFinal varchar(10), primary key (id)) ENGINE=InnoDB
create table basic_fisica_necessidade_especial (fisica bigint not null, necessidadesEspeciais bigint not null) ENGINE=InnoDB
create table basic_funcionario (id bigint not null auto_increment, dataAdmissao datetime, dataDemissao datetime, fisica bigint, cargo bigint, juridica bigint, primary key (id)) ENGINE=InnoDB
create table basic_grau_parentesco (id bigint not null auto_increment, nome varchar(255), primary key (id)) ENGINE=InnoDB
create table basic_grupo_representante (id bigint not null auto_increment, nome varchar(50), juridica bigint, primary key (id)) ENGINE=InnoDB
create table basic_grupo_representante_comissao (discriminator varchar(3) not null, id bigint not null auto_increment, percentual double precision, valor numeric(19,2), grupoRepresentante bigint, itemCusto bigint, primary key (id)) ENGINE=InnoDB
create table basic_logradouro (id bigint not null auto_increment, nome varchar(50), tipoLogradouro varchar(11), primary key (id)) ENGINE=InnoDB
create table basic_municipio (id bigint not null auto_increment, codigoIbge varchar(7), nome varchar(50), uf varchar(2), primary key (id)) ENGINE=InnoDB
create table basic_necessidade_especial (id bigint not null auto_increment, descricao varchar(255), primary key (id)) ENGINE=InnoDB
create table basic_observacoes (id bigint not null auto_increment, exibir bit, observacoes text, primary key (id)) ENGINE=InnoDB
create table basic_pais (id bigint not null auto_increment, nome varchar(50), sigla2 varchar(2), sigla3 varchar(3), primary key (id)) ENGINE=InnoDB
create table basic_pessoa (discriminator varchar(1) not null, id bigint not null auto_increment, apelido varchar(255), cmc varchar(7), codigoSeguranca varchar(3), dataCadastro datetime, dataFinal datetime, dataInicial datetime, documento varchar(15), email varchar(70), nome varchar(255), senha varchar(50), www varchar(70), capitalSocial Decimal(15,4), ie varchar(14), numeroFuncionarios integer, tipoEstabelecimento varchar(10), canhota bit, cco varchar(12), documentoMilitarDataExpedicao date, documentoMilitarNumero varchar(20), documentoMilitarOrgao varchar(3), documentoMilitarTipo varchar(3), estadoCivil varchar(10), inss varchar(11), nomeMae varchar(100), nomePai varchar(100), pisPasep varchar(11), rgDataExpedicao datetime, rgNumero varchar(10), rgOrgaoExpedidor varchar(5), rgUfExpedidor varchar(2), sexo varchar(1), escritorioContabil bigint, responsavelCpf bigint, applicationUser bigint, profissao bigint, naturalidade bigint, enderecoCorrespondencia bigint, cnae bigint, contato bigint, nacionalidade bigint, contador bigint, primary key (id)) ENGINE=InnoDB
create table basic_profissao (id bigint not null auto_increment, codigo varchar(4), nome varchar(100), primary key (id)) ENGINE=InnoDB
create table basic_representante (id bigint not null auto_increment, grupoRepresentante bigint, fisica bigint, primary key (id)) ENGINE=InnoDB
create table basic_responsavel_cpf (id bigint not null auto_increment, nome varchar(255), grauParentesco bigint, primary key (id)) ENGINE=InnoDB
create table basic_socio (id bigint not null auto_increment, dataEntrada datetime, dataSaida datetime, juridica bigint, fisica bigint, cargo bigint, primary key (id)) ENGINE=InnoDB
create table basic_telefone (id bigint not null auto_increment, ddd varchar(4), numero varchar(8), ramal varchar(5), telefoneOperadora bigint, pessoa_id bigint, tipoTelefone bigint, primary key (id)) ENGINE=InnoDB
create table basic_telefone_operadora (id bigint not null auto_increment, nome varchar(100), primary key (id)) ENGINE=InnoDB
create table basic_tipo_telefone (id bigint not null auto_increment, nome varchar(20), primary key (id)) ENGINE=InnoDB
create table financeiro_banco (id bigint not null auto_increment, codigo varchar(3), digito varchar(1), nome varchar(50), primary key (id)) ENGINE=InnoDB
create table financeiro_centro_custo (id bigint not null auto_increment, codigoContaAgrupadoraContabil varchar(20), inativo bit, nome varchar(50), primary key (id)) ENGINE=InnoDB
create table financeiro_cheque_modelo (id bigint not null auto_increment, anoTamanho float, anoX float, anoY float, chequeAltura float, cidadeEstadoTamanho float, cidadeEstadoX float, cidadeEstadoY float, codigoBancoTamanho float, codigoBancoX float, codigoBancoY float, diaTamanho float, diaX float, diaY float, folhaAltura float, folhaLargura float, fonteNome varchar(50), fonteTamanho integer, mesTamanho float, mesX float, mesY float, nome varchar(50), nomeFavorecidoTamanho float, nomeFavorecidoX float, nomeFavorecidoY float, numeroChequeTamanho float, numeroChequeX float, numeroChequeY float, valorDecimalTamanho float, valorDecimalX float, valorDecimalY float, valorExtensoTamanho float, valorExtensoX float, valorExtensoY float, primary key (id)) ENGINE=InnoDB
create table financeiro_classificacao_contabil (id bigint not null auto_increment, inativo bit, nome varchar(100), classificacaoContabilCategoria bigint, primary key (id)) ENGINE=InnoDB
create table financeiro_classificacao_contabil_categoria (id bigint not null auto_increment, nome varchar(50), primary key (id)) ENGINE=InnoDB
create table financeiro_conta (id bigint not null auto_increment, contaContabilCompensacao varchar(20), contaContabilMovimento varchar(20), contaContabilPrevista varchar(20), dataAbertura date, dataEncerramento date, dataFechamento date, inativo bit, nome varchar(50), saldoAbertura numeric(19,2), contaCategoria bigint, primary key (id)) ENGINE=InnoDB
create table financeiro_conta_bancaria (id bigint not null auto_increment, agenciaCodigo varchar(4), agenciaDigito varchar(1), contaCodigo varchar(7), contaDigito varchar(1), tipoConta varchar(14), banco bigint, primary key (id)) ENGINE=InnoDB
create table financeiro_conta_categoria (id bigint not null auto_increment, nome varchar(50), primary key (id)) ENGINE=InnoDB
create table financeiro_conta_user (conta bigint not null, applicationUser bigint not null, primary key (conta, applicationUser)) ENGINE=InnoDB
create table financeiro_contrato_descontos_acrescimos (id bigint not null auto_increment, dataFinal date, dataInicial date, ordem integer, percentual numeric(19,2), valor numeric(19,2), itemCusto bigint, classificacaoContabil bigint, contratoFinanceiro bigint, primary key (id)) ENGINE=InnoDB
create table financeiro_convenio_cobranca (discriminator varchar(3) not null, id bigint not null auto_increment, diasAntecipacao integer, nome varchar(50), nomeGerenciadorDocumento varchar(50), observacoes varchar(256), remessaNumeroSequencial integer, remessaUltimaData datetime, sequenciaNumeroDocumento bigint, aceite bit, bancoGeraOcorrenciaValorTarifa bit, cancelado bit, canceladoData datetime, carteiraCodigo varchar(2), carteiraVariacao varchar(3), cedenteCodigo varchar(7), cedenteDigito varchar(1), layoutCnab varchar(10), localPagamento varchar(80), nossoNumeroAno integer, valorTarifa numeric(19,2), contaBancaria bigint, contratante bigint, itemCustoIof bigint, centroCustoGeral bigint, itemCustoTarifa bigint, documentoPagamentoCategoria bigint, contratado bigint, primary key (id)) ENGINE=InnoDB
create table financeiro_convenio_pagamento (discriminator varchar(3) not null, id bigint not null auto_increment, nome varchar(50), nomeGerenciadorDocumento varchar(50), observacoes varchar(256), proximoNumeroCheque bigint, contratado bigint, contratante bigint, chequeModelo bigint, contaBancaria bigint, primary key (id)) ENGINE=InnoDB
create table financeiro_documento_cobranca (discriminator varchar(3) not null, id bigint not null auto_increment, data date, dataCancelamento date, dataImpressao datetime, dataVencimento date, instrucoes3 varchar(80), layoutId integer, numeroDocumento varchar(20), transacao integer, valor numeric(19,2), valorAcrescimo numeric(19,2), valorDesconto numeric(19,2), valorJuros numeric(19,2), valorMulta numeric(19,2), valorPago numeric(19,2), dataCredito datetime, dataUltimaOcorrencia datetime, outrasDeducoes numeric(19,2), valorIOF numeric(19,2), valorTarifa numeric(19,2), contrato bigint, ultimaOcorrencia bigint, convenioCobranca bigint, documentoCobrancaCategoria bigint, primary key (id)) ENGINE=InnoDB
create table financeiro_documento_cobranca_categoria (id bigint not null auto_increment, descontoAntecipacao numeric(19,2), diasToleranciaMultaAtraso integer, formatoInstrucoes0 varchar(200), instrucoes1 varchar(80), instrucoes2 varchar(80), jurosMora numeric(19,2), layoutId integer, multaAtraso numeric(19,2), nome varchar(50), contaPadrao bigint, convenioCobranca bigint, primary key (id)) ENGINE=InnoDB
create table financeiro_documento_pagamento (discriminator varchar(3) not null, id bigint not null auto_increment, data datetime, dataCancelamento datetime, dataImpressao datetime, dataVencimento datetime, layoutId integer, numeroDocumento varchar(20), transacao integer, valor numeric(19,2), agencia varchar(4), contaCorrente varchar(8), contaCorrenteDigito varchar(2), convenioPagamento bigint, contrato bigint, documentoPagamentoCategoria bigint, banco bigint, primary key (id)) ENGINE=InnoDB
create table financeiro_documento_pagamento_categoria (id bigint not null auto_increment, layoutId integer, nome varchar(50), convenioPagamento bigint, contaPadrao bigint, primary key (id)) ENGINE=InnoDB
create table financeiro_irrf_aliquota (id bigint not null auto_increment, aliquota numeric(19,2), valorDeducao numeric(19,2), valorFinal numeric(19,2), valorInicial numeric(19,2), tabela_id bigint, primary key (id)) ENGINE=InnoDB
create table financeiro_irrf_tabela (id bigint not null auto_increment, deducaoAposentado numeric(19,2), deducaoDependente numeric(19,2), finalVigencia date, inicioVigencia date, valorMinimoRecolhimento numeric(19,2), primary key (id)) ENGINE=InnoDB
create table financeiro_item_custo (discriminator varchar(3) not null, id bigint not null auto_increment, codigoContaContabil varchar(20), inativo bit, nome varchar(150), primary key (id)) ENGINE=InnoDB
create table financeiro_lancamento (discriminator varchar(3) not null, id bigint not null auto_increment, codigoExterno varchar(20), data date, dataVencimento date, descricao varchar(256), lancamentoSituacao varchar(10), naoReceberAposVencimento bit, saldo numeric(19,2), valor numeric(19,2), frequencia varchar(20), quantidadeRestante integer, ultimoLancamento bigint, contrato bigint, documentoCobranca bigint, operacao bigint, contaPrevista bigint, documentoPagamento bigint, agendamentoItem bigint, primary key (id)) ENGINE=InnoDB
create table financeiro_lancamento_item (discriminator varchar(3) not null, id bigint not null auto_increment, dataCompetencia date, dataLancamento date, descricao varchar(256), peso numeric(19,2), valor numeric(19,2), lancamento bigint, itemCusto bigint, adesaoContrato bigint, contrato bigint, classificacaoContabil bigint, centroCusto bigint, primary key (id)) ENGINE=InnoDB
create table financeiro_lancamento_movimento (id bigint not null auto_increment, acrescimo numeric(19,2), data date, dataCompensacao date, dataLancamento date, desconto numeric(19,2), descricao varchar(256), estornado bit, juros numeric(19,2), lancamentoMovimentoCategoria varchar(20), multa numeric(19,2), valor numeric(19,2), valorTotal numeric(19,2), transferencia bigint, conta bigint, documentoPagamento bigint, lancamento bigint, primary key (id)) ENGINE=InnoDB
create table financeiro_lancamento_parcela (id bigint not null auto_increment, dataLancamento date, dataVencimento date, numero integer, valor numeric(19,2), documentoCobranca bigint, lancamento bigint, lancamentoMovimento bigint, primary key (id)) ENGINE=InnoDB
create table financeiro_operacao (id bigint not null auto_increment, nome varchar(256), primary key (id)) ENGINE=InnoDB
create table financeiro_titulo_ocorrencia (id bigint not null auto_increment, codigo integer, descricao varchar(100), primary key (id)) ENGINE=InnoDB
create table financeiro_titulo_ocorrencia_controle (id bigint not null auto_increment, dataOcorrencia datetime, motivo varchar(100), titulo bigint, ocorrencia bigint, primary key (id)) ENGINE=InnoDB
create table framework_document_model_entity (id bigint not null auto_increment, date datetime, description varchar(255), name varchar(50), source Text, applicationEntity bigint, applicationUser bigint, primary key (id)) ENGINE=InnoDB
create table framework_email_account (id bigint not null auto_increment, host varchar(50), password varchar(50), senderMail varchar(100), senderName varchar(100), useAsDefault bit, user varchar(50), primary key (id)) ENGINE=InnoDB
create table framework_label_address (id bigint not null auto_increment, line1 varchar(150), line2 varchar(150), line3 varchar(150), line4 varchar(150), line5 varchar(150), ocurrencyDate datetime, print bit, applicationEntity bigint, addressLabelGroup bigint, applicationUser bigint, primary key (id)) ENGINE=InnoDB
create table framework_label_address_group (id bigint not null auto_increment, name varchar(100), applicationUser bigint, primary key (id)) ENGINE=InnoDB
create table framework_label_model (id bigint not null auto_increment, envelope bit, fontName varchar(50), fontSize integer, horizontalDistance float, labelHeight float, labelWidth float, marginLeft float, marginTop float, name varchar(50), pageHeight float, pageWidth float, verticalDistance float, primary key (id)) ENGINE=InnoDB
create table framework_label_model_entity (id bigint not null auto_increment, description varchar(255), line1 varchar(255), line2 varchar(255), line3 varchar(255), line4 varchar(255), line5 varchar(255), name varchar(50), applicationEntity bigint, primary key (id)) ENGINE=InnoDB
create table framework_order_condiction (id bigint not null auto_increment, active bit, orderDirection integer, propertyPath varchar(200), userReport bigint, orderIndex integer, primary key (id)) ENGINE=InnoDB
create table framework_page_condiction (id bigint not null, itemsCount integer, page integer, pageSize integer, primary key (id)) ENGINE=InnoDB
create table framework_parent_condiction (id bigint not null, property varchar(50), applicationEntity bigint, primary key (id)) ENGINE=InnoDB
create table framework_query_condiction (id bigint not null auto_increment, active bit, closePar bit, initOperator integer, openPar bit, operatorId integer, propertyPath varchar(200), value1 varchar(50), value2 varchar(50), userReport bigint, orderIndex integer, primary key (id)) ENGINE=InnoDB
create table framework_result_condiction (id bigint not null auto_increment, propertyPath varchar(200), resultIndex integer, userReport bigint, primary key (id)) ENGINE=InnoDB
create table framework_user_report (id bigint not null auto_increment, date datetime, description Text, filterCondiction varchar(50), hqlWhereCondiction Text, name varchar(100), applicationUser bigint, applicationEntity bigint, primary key (id)) ENGINE=InnoDB
create table security_entity (id bigint not null auto_increment, className varchar(200), colorName varchar(50), description text, hint varchar(255), label varchar(100), name varchar(100), runQueryOnOpen bit, applicationModule bigint, primary key (id)) ENGINE=InnoDB
create table security_entity_property (id bigint not null auto_increment, allowSubQuery bit, colorName varchar(50), defaultValue varchar(50), description text, displayFormat varchar(50), editMask varchar(50), editShowList bit, hint varchar(255), indexGroup integer, indexProperty integer, label varchar(100), maximum double precision, minimum double precision, name varchar(50), readOnly bit, required bit, valuesList varchar(255), visible bit, applicationEntity bigint, primary key (id)) ENGINE=InnoDB
create table security_entity_property_group (id bigint not null auto_increment, colorName varchar(50), description text, hint varchar(255), indexGroup integer, label varchar(100), name varchar(100), applicationEntity bigint, primary key (id)) ENGINE=InnoDB
create table security_group (id bigint not null auto_increment, name varchar(50), primary key (id)) ENGINE=InnoDB
create table security_module (id bigint not null auto_increment, name varchar(50), primary key (id)) ENGINE=InnoDB
create table security_process (id bigint not null auto_increment, description Text, hint varchar(255), label varchar(100), name varchar(50), applicationModule bigint, primary key (id)) ENGINE=InnoDB
create table security_right_crud (id bigint not null auto_increment, createAllowed bit, deleteAllowed bit, queryAllowed bit, retrieveAllowed bit, updateAllowed bit, applicationEntity bigint, securityGroup bigint, primary key (id)) ENGINE=InnoDB
create table security_right_process (id bigint not null auto_increment, executeAllowed bit, applicationProcess bigint, securityGroup bigint, primary key (id)) ENGINE=InnoDB
create table security_user (id bigint not null auto_increment, inactive bit, login varchar(20) unique, name varchar(50), password varchar(50), primary key (id)) ENGINE=InnoDB
create table security_user_group (applicationUser bigint not null, securityGroup bigint not null, primary key (securityGroup, applicationUser)) ENGINE=InnoDB
alter table auditorship_crud add index applicationEntity (applicationEntity), add constraint applicationEntity foreign key (applicationEntity) references security_entity (id)
alter table auditorship_crud add index applicationUser (applicationUser), add constraint applicationUser foreign key (applicationUser) references security_user (id)
alter table auditorship_process add index applicationProcess (applicationProcess), add constraint applicationProcess foreign key (applicationProcess) references security_process (id)
alter table auditorship_process add index applicationUser (applicationUser), add constraint applicationUser foreign key (applicationUser) references security_user (id)
alter table auditorship_service add index applicationUser (applicationUser), add constraint applicationUser foreign key (applicationUser) references security_user (id)
create index codigo on basic_adesao_contrato (codigo)
alter table basic_adesao_contrato add index observacoes (observacoes), add constraint observacoes foreign key (observacoes) references basic_observacoes (id)
alter table basic_adesao_contrato add index FK9070F94B80FC57FF (contrato_id), add constraint FK9070F94B80FC57FF foreign key (contrato_id) references basic_contrato (id)
alter table basic_adesao_contrato add index pessoa (pessoa), add constraint pessoa foreign key (pessoa) references basic_pessoa (id)
alter table basic_contador add index escritorioContabil (escritorioContabil), add constraint escritorioContabil foreign key (escritorioContabil) references basic_escritorio_contabil (id)
alter table basic_contador add index fisica (fisica), add constraint fisica foreign key (fisica) references basic_pessoa (id)
alter table basic_contador add index observacoes (observacoes), add constraint observacoes foreign key (observacoes) references basic_observacoes (id)
alter table basic_contato add index cargo (cargo), add constraint cargo foreign key (cargo) references basic_cargo (id)
create index codigo on basic_contrato (codigo)
alter table basic_contrato add index representante (representante), add constraint representante foreign key (representante) references basic_representante (id)
alter table basic_contrato add index categoria (categoria), add constraint categoria foreign key (categoria) references basic_contrato_categoria (id)
alter table basic_contrato add index observacoes (observacoes), add constraint observacoes foreign key (observacoes) references basic_observacoes (id)
alter table basic_contrato add index contratoFinanceiroCategoria (contratoFinanceiroCategoria), add constraint contratoFinanceiroCategoria foreign key (contratoFinanceiroCategoria) references basic_contrato_categoria (id)
alter table basic_contrato add index pessoa (pessoa), add constraint pessoa foreign key (pessoa) references basic_pessoa (id)
alter table basic_contrato_categoria add index observacoes (observacoes), add constraint observacoes foreign key (observacoes) references basic_observacoes (id)
alter table basic_contrato_contato add index motivo (motivo), add constraint motivo foreign key (motivo) references basic_contrato_contato_motivo (id)
alter table basic_contrato_contato add index representante (representante), add constraint representante foreign key (representante) references basic_representante (id)
alter table basic_contrato_contato add index contatoRetorno (contatoRetorno), add constraint contatoRetorno foreign key (contatoRetorno) references basic_contrato_contato (id)
alter table basic_contrato_contato add index contrato (contrato), add constraint contrato foreign key (contrato) references basic_contrato (id)
alter table basic_endereco add index enderecoCategoria (enderecoCategoria), add constraint enderecoCategoria foreign key (enderecoCategoria) references basic_endereco_categoria (id)
alter table basic_endereco add index municipio (municipio), add constraint municipio foreign key (municipio) references basic_municipio (id)
alter table basic_endereco add index logradouro (logradouro), add constraint logradouro foreign key (logradouro) references basic_logradouro (id)
alter table basic_endereco add index bairro (bairro), add constraint bairro foreign key (bairro) references basic_bairro (id)
alter table basic_endereco add index pessoa (pessoa), add constraint pessoa foreign key (pessoa) references basic_pessoa (id)
alter table basic_escritorio_contabil add index observacoes (observacoes), add constraint observacoes foreign key (observacoes) references basic_observacoes (id)
alter table basic_escritorio_contabil add index juridica (juridica), add constraint juridica foreign key (juridica) references basic_pessoa (id)
alter table basic_fisica_necessidade_especial add index FK79B0473C67533F9B (necessidadesEspeciais), add constraint FK79B0473C67533F9B foreign key (necessidadesEspeciais) references basic_necessidade_especial (id)
alter table basic_fisica_necessidade_especial add index fisica (fisica), add constraint fisica foreign key (fisica) references basic_pessoa (id)
alter table basic_funcionario add index fisica (fisica), add constraint fisica foreign key (fisica) references basic_pessoa (id)
alter table basic_funcionario add index cargo (cargo), add constraint cargo foreign key (cargo) references basic_cargo (id)
alter table basic_funcionario add index juridica (juridica), add constraint juridica foreign key (juridica) references basic_pessoa (id)
alter table basic_grupo_representante add index juridica (juridica), add constraint juridica foreign key (juridica) references basic_pessoa (id)
alter table basic_grupo_representante_comissao add index itemCusto (itemCusto), add constraint itemCusto foreign key (itemCusto) references financeiro_item_custo (id)
alter table basic_grupo_representante_comissao add index grupoRepresentante (grupoRepresentante), add constraint grupoRepresentante foreign key (grupoRepresentante) references basic_grupo_representante (id)
create index documento on basic_pessoa (documento)
alter table basic_pessoa add index cnae (cnae), add constraint cnae foreign key (cnae) references basic_cnae (id)
alter table basic_pessoa add index enderecoCorrespondencia (enderecoCorrespondencia), add constraint enderecoCorrespondencia foreign key (enderecoCorrespondencia) references basic_endereco (id)
alter table basic_pessoa add index responsavelCpf (responsavelCpf), add constraint responsavelCpf foreign key (responsavelCpf) references basic_responsavel_cpf (id)
alter table basic_pessoa add index contato (contato), add constraint contato foreign key (contato) references basic_contato (id)
alter table basic_pessoa add index naturalidade (naturalidade), add constraint naturalidade foreign key (naturalidade) references basic_municipio (id)
alter table basic_pessoa add index escritorioContabil (escritorioContabil), add constraint escritorioContabil foreign key (escritorioContabil) references basic_escritorio_contabil (id)
alter table basic_pessoa add index nacionalidade (nacionalidade), add constraint nacionalidade foreign key (nacionalidade) references basic_pais (id)
alter table basic_pessoa add index profissao (profissao), add constraint profissao foreign key (profissao) references basic_profissao (id)
alter table basic_pessoa add index contador (contador), add constraint contador foreign key (contador) references basic_contador (id)
alter table basic_pessoa add index applicationUser (applicationUser), add constraint applicationUser foreign key (applicationUser) references security_user (id)
alter table basic_representante add index fisica (fisica), add constraint fisica foreign key (fisica) references basic_pessoa (id)
alter table basic_representante add index grupoRepresentante (grupoRepresentante), add constraint grupoRepresentante foreign key (grupoRepresentante) references basic_grupo_representante (id)
alter table basic_responsavel_cpf add index grauParentesco (grauParentesco), add constraint grauParentesco foreign key (grauParentesco) references basic_grau_parentesco (id)
alter table basic_socio add index fisica (fisica), add constraint fisica foreign key (fisica) references basic_pessoa (id)
alter table basic_socio add index cargo (cargo), add constraint cargo foreign key (cargo) references basic_cargo (id)
alter table basic_socio add index juridica (juridica), add constraint juridica foreign key (juridica) references basic_pessoa (id)
alter table basic_telefone add index tipoTelefone (tipoTelefone), add constraint tipoTelefone foreign key (tipoTelefone) references basic_tipo_telefone (id)
alter table basic_telefone add index pessoa_id (pessoa_id), add constraint pessoa_id foreign key (pessoa_id) references basic_pessoa (id)
alter table basic_telefone add index telefoneOperadora (telefoneOperadora), add constraint telefoneOperadora foreign key (telefoneOperadora) references basic_telefone_operadora (id)
alter table financeiro_classificacao_contabil add index classificacaoContabilCategoria (classificacaoContabilCategoria), add constraint classificacaoContabilCategoria foreign key (classificacaoContabilCategoria) references financeiro_classificacao_contabil_categoria (id)
alter table financeiro_conta add index contaCategoria (contaCategoria), add constraint contaCategoria foreign key (contaCategoria) references financeiro_conta_categoria (id)
alter table financeiro_conta_bancaria add index banco (banco), add constraint banco foreign key (banco) references financeiro_banco (id)
alter table financeiro_conta_user add index conta (conta), add constraint conta foreign key (conta) references financeiro_conta (id)
alter table financeiro_conta_user add index FKB21AE66E32FD389 (applicationUser), add constraint FKB21AE66E32FD389 foreign key (applicationUser) references security_user (id)
alter table financeiro_contrato_descontos_acrescimos add index itemCusto (itemCusto), add constraint itemCusto foreign key (itemCusto) references financeiro_item_custo (id)
alter table financeiro_contrato_descontos_acrescimos add index classificacaoContabil (classificacaoContabil), add constraint classificacaoContabil foreign key (classificacaoContabil) references financeiro_classificacao_contabil (id)
alter table financeiro_contrato_descontos_acrescimos add index contratoFinanceiro (contratoFinanceiro), add constraint contratoFinanceiro foreign key (contratoFinanceiro) references basic_contrato (id)
alter table financeiro_convenio_cobranca add index contratado (contratado), add constraint contratado foreign key (contratado) references basic_contrato (id)
alter table financeiro_convenio_cobranca add index contaBancaria (contaBancaria), add constraint contaBancaria foreign key (contaBancaria) references financeiro_conta_bancaria (id)
alter table financeiro_convenio_cobranca add index contratante (contratante), add constraint contratante foreign key (contratante) references basic_pessoa (id)
alter table financeiro_convenio_cobranca add index documentoPagamentoCategoria (documentoPagamentoCategoria), add constraint documentoPagamentoCategoria foreign key (documentoPagamentoCategoria) references financeiro_documento_pagamento_categoria (id)
alter table financeiro_convenio_cobranca add index centroCustoGeral (centroCustoGeral), add constraint centroCustoGeral foreign key (centroCustoGeral) references financeiro_centro_custo (id)
alter table financeiro_convenio_cobranca add index itemCustoTarifa (itemCustoTarifa), add constraint itemCustoTarifa foreign key (itemCustoTarifa) references financeiro_item_custo (id)
alter table financeiro_convenio_cobranca add index itemCustoIof (itemCustoIof), add constraint itemCustoIof foreign key (itemCustoIof) references financeiro_item_custo (id)
alter table financeiro_convenio_pagamento add index contratado (contratado), add constraint contratado foreign key (contratado) references basic_pessoa (id)
alter table financeiro_convenio_pagamento add index contaBancaria (contaBancaria), add constraint contaBancaria foreign key (contaBancaria) references financeiro_conta_bancaria (id)
alter table financeiro_convenio_pagamento add index contratante (contratante), add constraint contratante foreign key (contratante) references basic_pessoa (id)
alter table financeiro_convenio_pagamento add index chequeModelo (chequeModelo), add constraint chequeModelo foreign key (chequeModelo) references financeiro_cheque_modelo (id)
create index numeroDocumento on financeiro_documento_cobranca (numeroDocumento)
alter table financeiro_documento_cobranca add index documentoCobrancaCategoria (documentoCobrancaCategoria), add constraint documentoCobrancaCategoria foreign key (documentoCobrancaCategoria) references financeiro_documento_cobranca_categoria (id)
alter table financeiro_documento_cobranca add index contrato (contrato), add constraint contrato foreign key (contrato) references basic_contrato (id)
alter table financeiro_documento_cobranca add index convenioCobranca (convenioCobranca), add constraint convenioCobranca foreign key (convenioCobranca) references financeiro_convenio_cobranca (id)
alter table financeiro_documento_cobranca add index ultimaOcorrencia (ultimaOcorrencia), add constraint ultimaOcorrencia foreign key (ultimaOcorrencia) references financeiro_titulo_ocorrencia (id)
alter table financeiro_documento_cobranca_categoria add index contaPadrao (contaPadrao), add constraint contaPadrao foreign key (contaPadrao) references financeiro_conta (id)
alter table financeiro_documento_cobranca_categoria add index convenioCobranca (convenioCobranca), add constraint convenioCobranca foreign key (convenioCobranca) references financeiro_convenio_cobranca (id)
create index numeroDocumento on financeiro_documento_pagamento (numeroDocumento)
alter table financeiro_documento_pagamento add index banco (banco), add constraint banco foreign key (banco) references financeiro_banco (id)
alter table financeiro_documento_pagamento add index documentoPagamentoCategoria (documentoPagamentoCategoria), add constraint documentoPagamentoCategoria foreign key (documentoPagamentoCategoria) references financeiro_documento_pagamento_categoria (id)
alter table financeiro_documento_pagamento add index convenioPagamento (convenioPagamento), add constraint convenioPagamento foreign key (convenioPagamento) references financeiro_convenio_pagamento (id)
alter table financeiro_documento_pagamento add index contrato (contrato), add constraint contrato foreign key (contrato) references basic_contrato (id)
alter table financeiro_documento_pagamento_categoria add index contaPadrao (contaPadrao), add constraint contaPadrao foreign key (contaPadrao) references financeiro_conta (id)
alter table financeiro_documento_pagamento_categoria add index convenioPagamento (convenioPagamento), add constraint convenioPagamento foreign key (convenioPagamento) references financeiro_convenio_pagamento (id)
alter table financeiro_irrf_aliquota add index FKA8D823C9BC5BEEC7 (tabela_id), add constraint FKA8D823C9BC5BEEC7 foreign key (tabela_id) references financeiro_irrf_tabela (id)
alter table financeiro_lancamento add index agendamentoItem (agendamentoItem), add constraint agendamentoItem foreign key (agendamentoItem) references financeiro_lancamento_item (id)
alter table financeiro_lancamento add index contaPrevista (contaPrevista), add constraint contaPrevista foreign key (contaPrevista) references financeiro_conta (id)
alter table financeiro_lancamento add index ultimoLancamento (ultimoLancamento), add constraint ultimoLancamento foreign key (ultimoLancamento) references financeiro_lancamento (id)
alter table financeiro_lancamento add index documentoPagamento (documentoPagamento), add constraint documentoPagamento foreign key (documentoPagamento) references financeiro_documento_pagamento (id)
alter table financeiro_lancamento add index operacao (operacao), add constraint operacao foreign key (operacao) references financeiro_operacao (id)
alter table financeiro_lancamento add index documentoCobranca (documentoCobranca), add constraint documentoCobranca foreign key (documentoCobranca) references financeiro_documento_cobranca (id)
alter table financeiro_lancamento add index contrato (contrato), add constraint contrato foreign key (contrato) references basic_contrato (id)
alter table financeiro_lancamento_item add index lancamento (lancamento), add constraint lancamento foreign key (lancamento) references financeiro_lancamento (id)
alter table financeiro_lancamento_item add index itemCusto (itemCusto), add constraint itemCusto foreign key (itemCusto) references financeiro_item_custo (id)
alter table financeiro_lancamento_item add index centroCusto (centroCusto), add constraint centroCusto foreign key (centroCusto) references financeiro_centro_custo (id)
alter table financeiro_lancamento_item add index classificacaoContabil (classificacaoContabil), add constraint classificacaoContabil foreign key (classificacaoContabil) references financeiro_classificacao_contabil (id)
alter table financeiro_lancamento_item add index adesaoContrato (adesaoContrato), add constraint adesaoContrato foreign key (adesaoContrato) references basic_adesao_contrato (id)
alter table financeiro_lancamento_item add index contrato (contrato), add constraint contrato foreign key (contrato) references basic_contrato (id)
alter table financeiro_lancamento_movimento add index lancamento (lancamento), add constraint lancamento foreign key (lancamento) references financeiro_lancamento (id)
alter table financeiro_lancamento_movimento add index documentoPagamento (documentoPagamento), add constraint documentoPagamento foreign key (documentoPagamento) references financeiro_documento_pagamento (id)
alter table financeiro_lancamento_movimento add index conta (conta), add constraint conta foreign key (conta) references financeiro_conta (id)
alter table financeiro_lancamento_movimento add index transferencia (transferencia), add constraint transferencia foreign key (transferencia) references financeiro_lancamento_movimento (id)
alter table financeiro_lancamento_parcela add index lancamento (lancamento), add constraint lancamento foreign key (lancamento) references financeiro_lancamento (id)
alter table financeiro_lancamento_parcela add index lancamentoMovimento (lancamentoMovimento), add constraint lancamentoMovimento foreign key (lancamentoMovimento) references financeiro_lancamento_movimento (id)
alter table financeiro_lancamento_parcela add index documentoCobranca (documentoCobranca), add constraint documentoCobranca foreign key (documentoCobranca) references financeiro_documento_cobranca (id)
alter table financeiro_titulo_ocorrencia_controle add index titulo (titulo), add constraint titulo foreign key (titulo) references financeiro_documento_cobranca (id)
alter table financeiro_titulo_ocorrencia_controle add index ocorrencia (ocorrencia), add constraint ocorrencia foreign key (ocorrencia) references financeiro_titulo_ocorrencia (id)
alter table framework_document_model_entity add index applicationEntity (applicationEntity), add constraint applicationEntity foreign key (applicationEntity) references security_entity (id)
alter table framework_document_model_entity add index applicationUser (applicationUser), add constraint applicationUser foreign key (applicationUser) references security_user (id)
alter table framework_label_address add index addressLabelGroup (addressLabelGroup), add constraint addressLabelGroup foreign key (addressLabelGroup) references framework_label_address_group (id)
alter table framework_label_address add index applicationEntity (applicationEntity), add constraint applicationEntity foreign key (applicationEntity) references security_entity (id)
alter table framework_label_address add index applicationUser (applicationUser), add constraint applicationUser foreign key (applicationUser) references security_user (id)
alter table framework_label_address_group add index applicationUser (applicationUser), add constraint applicationUser foreign key (applicationUser) references security_user (id)
alter table framework_label_model_entity add index applicationEntity (applicationEntity), add constraint applicationEntity foreign key (applicationEntity) references security_entity (id)
alter table framework_order_condiction add index userReport (userReport), add constraint userReport foreign key (userReport) references framework_user_report (id)
alter table framework_parent_condiction add index applicationEntity (applicationEntity), add constraint applicationEntity foreign key (applicationEntity) references security_entity (id)
alter table framework_query_condiction add index userReport (userReport), add constraint userReport foreign key (userReport) references framework_user_report (id)
alter table framework_result_condiction add index userReport (userReport), add constraint userReport foreign key (userReport) references framework_user_report (id)
alter table framework_user_report add index applicationEntity (applicationEntity), add constraint applicationEntity foreign key (applicationEntity) references security_entity (id)
alter table framework_user_report add index applicationUser (applicationUser), add constraint applicationUser foreign key (applicationUser) references security_user (id)
alter table security_entity add index applicationModule (applicationModule), add constraint applicationModule foreign key (applicationModule) references security_module (id)
alter table security_entity_property add index applicationEntity (applicationEntity), add constraint applicationEntity foreign key (applicationEntity) references security_entity (id)
alter table security_entity_property_group add index applicationEntity (applicationEntity), add constraint applicationEntity foreign key (applicationEntity) references security_entity (id)
alter table security_process add index applicationModule (applicationModule), add constraint applicationModule foreign key (applicationModule) references security_module (id)
alter table security_right_crud add index securityGroup (securityGroup), add constraint securityGroup foreign key (securityGroup) references security_group (id)
alter table security_right_crud add index applicationEntity (applicationEntity), add constraint applicationEntity foreign key (applicationEntity) references security_entity (id)
alter table security_right_process add index applicationProcess (applicationProcess), add constraint applicationProcess foreign key (applicationProcess) references security_process (id)
alter table security_right_process add index securityGroup (securityGroup), add constraint securityGroup foreign key (securityGroup) references security_group (id)
alter table security_user_group add index applicationGroup (securityGroup), add constraint applicationGroup foreign key (securityGroup) references security_group (id)
alter table security_user_group add index applicationUser (applicationUser), add constraint applicationUser foreign key (applicationUser) references security_user (id)
