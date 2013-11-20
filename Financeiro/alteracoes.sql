-- Lucio -08/10/2013 
ALTER TABLE `financeiro_conta` 
  ADD `compensacaoAutomatica` bit;
UPDATE `financeiro_conta` 
  SET `compensacaoAutomatica` = 0;

-- Lucio - 13/11/2012
ALTER TABLE `financeiro_conta` 
  CHANGE codigoContaContabil contaContabilPrevista varchar(20),
  ADD contaContabilMovimento varchar(20),
  ADD contaContabilCompensacao varchar(20);
  
-- Lucio - 12/11/2012
UPDATE `financeiro_convenio_cobranca`
SET contratado = null;

ALTER TABLE `financeiro_convenio_cobranca`
 DROP FOREIGN KEY `financeiro_convenio_cobranca_contratado`;

ALTER TABLE `financeiro_convenio_cobranca` ADD CONSTRAINT `financeiro_convenio_cobranca_contratado` FOREIGN KEY `financeiro_convenio_cobranca_contratado` (`contratado`)
    REFERENCES `basic_contrato` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;


-- Lucio - 05/11/2012
alter table financeiro_titulo_ocorrencia_controle 
  add  motivo varchar(100);
insert into financeiro_titulo_ocorrencia (codigo, descricao) values(1080, 'Retorno - Tarifa bancária');
alter table financeiro_convenio_cobranca 
  add bancoGeraOcorrenciaValorTarifa tinyint(1),
  add valorTarifa numeric(19,2);
update financeiro_convenio_cobranca
set bancoGeraOcorrenciaValorTarifa = false;
  
 
-- Lucio - 22/08/2012
create table basic_grupo_representante_comissao (discriminator varchar(3) not null, id bigint not null auto_increment, percentual double precision, valor numeric(19,2), itemCusto bigint, grupoRepresentante bigint, primary key (id)) ENGINE=InnoDB;
alter table basic_grupo_representante_comissao add index itemCusto (itemCusto), add constraint basic_grupo_representante_comissao_itemCusto foreign key (itemCusto) references financeiro_item_custo (id);
alter table basic_grupo_representante_comissao add index grupoRepresentante (grupoRepresentante), add constraint basic_grupo_representante_comissao_grupoRepresentante foreign key (grupoRepresentante) references basic_grupo_representante (id);

-- Lucio - 05/03/2012
alter table financeiro_item_custo 
  ADD codigoContaContabil varchar(20);
alter table financeiro_centro_custo 
  ADD codigoContaAgrupadoraContabil varchar(20);
alter table financeiro_conta 
  ADD codigoContaContabil varchar(20);

  -- Lucio - 02/02/2012
alter table financeiro_lancamento_item 
  ADD contrato bigint,
  ADD dataLancamento DATE,
  ADD dataCompetencia DATE;
  
update financeiro_lancamento_item as fli
inner join financeiro_lancamento as fl on fl.id = fli.lancamento
set fli.contrato = fl.contrato,
    fli.dataLancamento = fl.data,
    fli.dataCompetencia = fl.data
where fli.contrato is null;
  
alter table financeiro_lancamento_item add index contrato (contrato), add constraint financeiro_lancamento_item_contrato foreign key (contrato) references basic_contrato (id);

create table financeiro_lancamento_parcela (id bigint not null auto_increment, dataLancamento date, dataVencimento date, numero integer, valor numeric(19,2), lancamentoMovimento bigint, lancamento bigint, documentoCobranca bigint, primary key (id)) ENGINE=InnoDB;
alter table financeiro_lancamento_parcela add index lancamento (lancamento), add constraint financeiro_lancamento_parcela_lancamento foreign key (lancamento) references financeiro_lancamento (id);
alter table financeiro_lancamento_parcela add index lancamentoMovimento (lancamentoMovimento), add constraint financeiro_lancamento_parcela_lancamentoMovimento foreign key (lancamentoMovimento) references financeiro_lancamento_movimento (id);
alter table financeiro_lancamento_parcela add index documentoCobranca (documentoCobranca), add constraint financeiro_lancamento_parcela_documentoCobranca foreign key (documentoCobranca) references financeiro_documento_cobranca (id);

-- Lucio - 29/01/2012
ALTER TABLE `financeiro_item_custo` 
  ADD `discriminator` VARCHAR(3);

-- Lucio - 18/07/2011
ALTER TABLE `financeiro_conta_bancaria` 
  ADD `tipoConta` varchar(14);

create table financeiro_irrf_aliquota (id bigint not null auto_increment, aliquota numeric(19,2), valorDeducao numeric(19,2), valorFinal numeric(19,2), valorInicial numeric(19,2), tabela_id bigint, primary key (id)) ENGINE=InnoDB;
create table financeiro_irrf_tabela (id bigint not null auto_increment, deducaoAposentado numeric(19,2), deducaoDependente numeric(19,2), finalVigencia date, inicioVigencia date, valorMinimoRecolhimento numeric(19,2), primary key (id)) ENGINE=InnoDB;
alter table financeiro_irrf_aliquota add index FKA8D823C9BC5BEEC7 (tabela_id), add constraint financeiro_irrf_aliquota_FKA8D823C9BC5BEEC7 foreign key (tabela_id) references financeiro_irrf_tabela (id);

alter table financeiro_lancamento_item 
  ADD adesaoContrato bigint;
alter table financeiro_lancamento_item add index adesaoContrato (adesaoContrato), add constraint financeiro_lancamento_item_adesaoContrato foreign key (adesaoContrato) references basic_adesao_contrato (id);

-- Antonio - 19/10/2007

ALTER TABLE `financeiro_convenio_cobranca` ADD `descontoAntecipacao` decimal(19,2);
ALTER TABLE `financeiro_convenio_cobranca` ADD `diasAntecipacao` int;
ALTER TABLE `financeiro_convenio_cobranca` ADD `itemCustoDescontoAntecipacao` bigint(20);
ALTER TABLE `financeiro_documento_pagamento` ADD `multa` decimal(19,2);
ALTER TABLE `financeiro_documento_pagamento` ADD `juros` decimal(19,2);

-- Antonio - 23/10/2007
ALTER TABLE `financeiro_convenio_cobranca` CHANGE `carteiraCodigo` `carteiraCodigo` VARCHAR( 2 ) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL;
ALTER TABLE `financeiro_convenio_cobranca` ADD `carteiraVariacao` VARCHAR( 3 ) NULL AFTER `carteiraCodigo`;

-- Antonio - 24/10/2007
ALTER TABLE `financeiro_documento_pagamento`
  DROP `juros`,
  DROP `multa`;

ALTER TABLE `financeiro_lancamento_movimento` 
  ADD `juros` DECIMAL( 19, 2 ) NULL ,
  ADD `multa` DECIMAL( 19, 2 ) NULL ,
  ADD `desconto` DECIMAL( 19, 2 ) NULL ;
  
-- Lucio - 24/10/2007 DTYPE para discriminator
ALTER TABLE `financeiro_documento_cobranca` CHANGE `DTYPE` `discriminator` VARCHAR(3) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL;
ALTER TABLE `financeiro_documento_pagamento` CHANGE `DTYPE` `discriminator` VARCHAR( 3 ) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL;
ALTER TABLE `financeiro_convenio_cobranca` CHANGE `DTYPE` `discriminator` VARCHAR( 3 ) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL;
ALTER TABLE `financeiro_convenio_pagamento` CHANGE `DTYPE` `discriminator` VARCHAR( 3 ) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL;

-- Lucio - 20/11/2007 Alterações nos campos de documentos de cobrança
ALTER TABLE `financeiro_convenio_cobranca` CHANGE COLUMN `nossoNumeroSequencial` `sequenciaNumeroDocumento` INTEGER  DEFAULT NULL;
ALTER TABLE `financeiro_documento_cobranca` CHANGE COLUMN `nossoNumero` `numeroDocumento` VARCHAR(20)  CHARACTER SET latin1 COLLATE latin1_swedish_ci DEFAULT NULL,
 ADD INDEX `numeroDocumento`(`numeroDocumento`);

ALTER TABLE `financeiro_convenio_pagamento` CHANGE `DTYPE` `discriminator` VARCHAR( 3 ) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL;

-- Juliana - 26/11/2007
ALTER TABLE `basic_contrato` ADD `diaVencimentoFatura` INT NULL ;
ALTER TABLE `basic_contrato_categoria` ADD `diaVencimentoFatura` INT NULL ;

-- Antonio - 29/11/2007 (Cria o campo "lancamentoSituacao"; Junta a tabela "agendamento" com "lancamento".
ALTER TABLE `financeiro_lancamento` ADD `lancamentoSituacao` VARCHAR( 10 ) NULL ;

ALTER TABLE `financeiro_lancamento_item` ADD `discriminator` VARCHAR( 3 ) NOT NULL FIRST ;

ALTER TABLE `financeiro_lancamento` 
  ADD `discriminator` VARCHAR( 3 ) NOT NULL FIRST ,
  ADD `frequencia` varchar(20) default NULL,
  ADD `quantidadeRestante` int(11) default NULL,
  ADD `ultimoLancamento` bigint(20) default NULL,
  ADD `agendamentoItem` bigint(20) default NULL;

ALTER TABLE `financeiro_lancamento`
  ADD KEY `agendamentoItem` (`agendamentoItem`),
  ADD KEY `contaPrevista` (`contaPrevista`),
  ADD KEY `ultimoLancamento` (`ultimoLancamento`),
  ADD KEY `documentoPagamento` (`documentoPagamento`),
  ADD KEY `documentoCobranca` (`documentoCobranca`),
  ADD KEY `operacao` (`operacao`),
  ADD CONSTRAINT `financeiro_lancamento_agendamentoItem` FOREIGN KEY (`agendamentoItem`) REFERENCES `financeiro_agendamento_item` (`id`),
  ADD CONSTRAINT `financeiro_lancamento_contaPrevista` FOREIGN KEY (`contaPrevista`) REFERENCES `financeiro_conta` (`id`),
  ADD CONSTRAINT `financeiro_lancamento_documentoCobranca` FOREIGN KEY (`documentoCobranca`) REFERENCES `financeiro_documento_cobranca` (`id`),
  ADD CONSTRAINT `financeiro_lancamento_documentoPagamento` FOREIGN KEY (`documentoPagamento`) REFERENCES `financeiro_documento_pagamento` (`id`),
  ADD CONSTRAINT `financeiro_lancamento_operacao` FOREIGN KEY (`operacao`) REFERENCES `financeiro_operacao` (`id`),
  ADD CONSTRAINT `financeiro_lancamento_ultimoLancamento` FOREIGN KEY (`ultimoLancamento`) REFERENCES `financeiro_lancamento` (`id`);

ALTER TABLE `financeiro_lancamento_item`
  ADD KEY `lancamento` (`lancamento`),
  ADD KEY `itemCusto` (`itemCusto`),
  ADD KEY `centroCusto` (`centroCusto`),
  ADD KEY `classificacaoContabil` (`classificacaoContabil`),
  ADD CONSTRAINT `financeiro_lancamento_item_centroCusto` FOREIGN KEY (`centroCusto`) REFERENCES `financeiro_centro_custo` (`id`),
  ADD CONSTRAINT `financeiro_lancamento_item_classificacaoContabil` FOREIGN KEY (`classificacaoContabil`) REFERENCES `financeiro_classificacao_contabil` (`id`),
  ADD CONSTRAINT `financeiro_lancamento_item_itemCusto` FOREIGN KEY (`itemCusto`) REFERENCES `financeiro_item_custo` (`id`),
  ADD CONSTRAINT `financeiro_lancamento_item_lancamento` FOREIGN KEY (`lancamento`) REFERENCES `financeiro_lancamento` (`id`);

update financeiro_lancamento set discriminator = 'LAN';
update financeiro_lancamento_item set discriminator = 'LAN';
update financeiro_lancamento set lancamentoSituacao = 'PENDENTE' where not saldo = 0;
update financeiro_lancamento set lancamentoSituacao = 'QUITADO' where saldo = 0;

-- Lucio - 02/12/2007
ALTER TABLE `financeiro_conta` ADD `inativo` INT NULL ;
UPDATE `financeiro_conta` SET `inativo`= false;

ALTER TABLE `financeiro_item_custo` ADD `inativo` INT NULL ;
UPDATE `financeiro_item_custo` SET `inativo`= false;

ALTER TABLE `financeiro_centro_custo` ADD `inativo` INT NULL ;
UPDATE `financeiro_centro_custo` SET `inativo`= false;

ALTER TABLE `financeiro_classificacao_contabil` ADD `inativo` INT NULL ;
UPDATE `financeiro_classificacao_contabil` SET `inativo`= false;

-- Lucio - 18/12/2007: documento de pagamento com número
ALTER TABLE `financeiro_documento_pagamento` 
 ADD `numeroDocumento` VARCHAR(20)  CHARACTER SET latin1 COLLATE latin1_swedish_ci DEFAULT NULL,
 ADD INDEX `numeroDocumento`(`numeroDocumento`);

-- Lucio - 21/01/2008: campos nome maiores para suportar dados da importação da Uningá
ALTER TABLE `financeiro_centro_custo` CHANGE `nome` `nome` VARCHAR(100);
ALTER TABLE `financeiro_item_custo` CHANGE `nome` `nome` VARCHAR(100);
ALTER TABLE `financeiro_documento_pagamento` CHANGE `contaCorrente` `contaCorrente` VARCHAR(100);
ALTER TABLE `financeiro_operacao` CHANGE `nome` `nome` VARCHAR(100);
ALTER TABLE `financeiro_banco` CHANGE `nome` `nome` VARCHAR(100);
ALTER TABLE `basic_tipo_telefone` CHANGE `nome` `nome` VARCHAR(100);
ALTER TABLE `basic_contrato` CHANGE `contratoAcademicoSituacao` `contratoAcademicoSituacao` VARCHAR(25);

-- Lucio - 24/01/2008: novo campo para armazenar o id de uma movimentação que foi importada de outros sistemas
ALTER TABLE `financeiro_lancamento` ADD `codigoExterno` VARCHAR(20);

-- Andre 09/06/2008: removendo campos de Cedente
alter table financeiro_convenio_cobranca drop foreign key financeiro_convenio_cobranca_itemCustoAcrescimo,DROP INDEX `itemCustoAcrescimo`;
alter table financeiro_convenio_cobranca drop foreign key financeiro_convenio_cobranca_itemCustoDesconto,DROP INDEX `itemCustoAcrescimo`;
alter table financeiro_convenio_cobranca drop foreign key financeiro_convenio_cobranca_itemCustoOutrasDeducoes,DROP INDEX `itemCustoOutrasDeducoes`;
alter table financeiro_convenio_cobranca drop foreign key financeiro_convenio_cobranca_itemCustoJurosMora,DROP INDEX `itemCustoJurosMora`;
alter table financeiro_convenio_cobranca drop foreign key financeiro_convenio_cobranca_itemCustoMultaAtraso,DROP INDEX `itemCustoMultaAtraso`;
alter table financeiro_convenio_cobranca drop foreign key financeiro_convenio_cobranca_itemCustoDescontoAntecipacao,DROP INDEX `itemCustoDescontoAntecipacao`;

ALTER TABLE `financeiro_convenio_cobranca`
  DROP `itemCustoMultaAtraso`,
  DROP `itemCustoDescontoAntecipacao`,
  DROP `itemCustoOutrasDeducoes`,
  DROP `itemCustoDesconto`,
  DROP `itemCustoJurosMora`,
  DROP `itemCustoAcrescimo`;
  
-- Andre 09/06/2008: criando os campos novos em DocumentoCobrancaCategoria
ALTER TABLE `financeiro_documento_cobranca_categoria` ADD `formatoInstrucoes0` VARCHAR( 200 ) NULL;
ALTER TABLE `financeiro_documento_cobranca_categoria` ADD `instrucoes1` VARCHAR( 80 ) NULL;
ALTER TABLE `financeiro_documento_cobranca_categoria` ADD `instrucoes2` VARCHAR( 80 ) NULL;
ALTER TABLE `financeiro_documento_cobranca_categoria` ADD `jurosMora` DECIMAL( 19, 2 ) NULL ;
ALTER TABLE `financeiro_documento_cobranca_categoria` ADD `multaAtraso` DECIMAL( 19, 2 ) NULL ;
ALTER TABLE `financeiro_documento_cobranca_categoria` ADD `descontoAntecipacao` DECIMAL( 19, 2 ) NULL ;
ALTER TABLE `financeiro_documento_cobranca_categoria` ADD `diasToleranciaMultaAtraso` INT NULL ;

-- Andre 09/06/2008: movendo dados de Cedente e ConvenioCobranca para DocumentoCobrancaCategoria
UPDATE financeiro_documento_cobranca_categoria ca
INNER JOIN financeiro_convenio_cobranca co
ON ca.convenioCobranca = co.id
SET ca.formatoInstrucoes0 = co.formatoInstrucoes0, 
	ca.instrucoes1 = co.instrucoes1,
	ca.instrucoes2 = co.instrucoes2,
	ca.jurosMora = co.jurosMora,
	ca.multaAtraso = co.multaAtraso,
	ca.descontoAntecipacao = co.descontoAntecipacao,
	ca.diasToleranciaMultaAtraso = co.diasToleranciaMultaAtraso		
WHERE (ca.convenioCobranca = co.id);

-- Andre 09/06/2008: excluindo os campos que foram mudados de Cedente e ConvenioCobranca para DocumentoCobrancaCategoria; estes campos não serão mais utilizados em Cedente e ConvenioCobranca
ALTER TABLE `financeiro_convenio_cobranca` 
	DROP `formatoInstrucoes0`,
	DROP `instrucoes1`,
	DROP `instrucoes2`,
	DROP `jurosMora`,
	DROP `multaAtraso`,
	DROP `descontoAntecipacao`,
	DROP `diasToleranciaMultaAtraso`;
	
-- Andre 12/07/2008: campo 'acrescimo' adicionado à classe LancamentoMovimento
ALTER TABLE `financeiro_lancamento_movimento` ADD `acrescimo` decimal(19,2);	

-- Andre 25/07/2008: campos de item de custo foram adicionados novamente à tabela de convênio cobrança pois ocorriam erros na grcs sem os campos
-- Segundo o Lucio: "Bem, a questao eh que o ConvenioGrcs AINDA utiliza os campos de item de custo especial. Com a execucao das rotinas de adaptacao em SQL, ele removeu, mas a GRCS ainda usa. Assim, adicione os campos novamente na tabela principal para que a entidade de convenio da GRCS funcione e nao deh erro"
ALTER TABLE `financeiro_convenio_cobranca` ADD `itemCustoMultaAtraso` DECIMAL( 19, 2 ) NULL ;
ALTER TABLE `financeiro_convenio_cobranca` ADD `itemCustoDescontoAntecipacao` DECIMAL( 19, 2 ) NULL ;
ALTER TABLE `financeiro_convenio_cobranca` ADD `itemCustoOutrasDeducoes` DECIMAL( 19, 2 ) NULL ;
ALTER TABLE `financeiro_convenio_cobranca` ADD `itemCustoDesconto` DECIMAL( 19, 2 ) NULL ;
ALTER TABLE `financeiro_convenio_cobranca` ADD `itemCustoJurosMora` DECIMAL( 19, 2 ) NULL ;
ALTER TABLE `financeiro_convenio_cobranca` ADD `itemCustoAcrescimo` DECIMAL( 19, 2 ) NULL ;

-- Lucio 28/10/2008: novo campo do movimento para armazenar o valor total recebido/pago
ALTER TABLE `financeiro_lancamento_movimento` ADD `valorTotal` decimal(19,2) after valor;
UPDATE `financeiro_lancamento_movimento` SET `valorTotal` = `valor`;

-- Lucio 14/04/2009: novo campo de data de lançamento e adapta os dados
ALTER TABLE `financeiro_lancamento_movimento` ADD `dataLancamento` DATE AFTER `id`;
-- Copia a data do movimento na data de lancamento
UPDATE `financeiro_lancamento_movimento` 
SET dataLancamento = data;
-- Copia a data de compensacao para a data do movimento que foi importada errada.
-- 159252 foi o ultimo lancamento gerado pela versao antes da importacao
UPDATE `financeiro_lancamento_movimento` 
SET data = dataCompensacao,
    dataCompensacao = null
WHERE id <= 159252;

-- LUCIO 20110705
-- Cria a tabela de comissões do financeiro
create table basic_grupo_representante_comissao (discriminator varchar(3) not null, id bigint not null auto_increment, percentual double precision, valor numeric(19,2), grupoRepresentante bigint, itemCusto bigint, primary key (id)) ENGINE=InnoDB;
