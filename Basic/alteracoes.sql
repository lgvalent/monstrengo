-- Lucio - 20/09/2018 - Novos atributos simplificados dos sócios
ALTER TABLE `basic_socio`
  ADD nome VARCHAR(255),
  ADD dataNascimento datetime;

DELETE FROM `basic_socio`
WHERE fisica is null or juridica is null;

/*
UPDATE `basic_socio` s
INNER JOIN `basic_pessoa` p
ON p.id = s.fisica
SET s.nome = null, s.dataNascimento = null;
*/
  
  -- Lucio - 16/06/2017 - Atualização dos número para 9 dígitos
ALTER TABLE `basic_telefone`
  CHANGE numero numero VARCHAR(9);

update basic_telefone
set numero = CONCAT('9',SUBSTRING(numero, 1, 8))
WHERE numero IS NOT NULL 
AND SUBSTRING(numero, 1, 1) IN (9,8,7,6) --TIPO DE numero: 12345 = FIXO; 6789 = CELULAR
AND LENGTH(numero) = 8 --TAMANHO ANTIGO COM 8 DÍGITOS

update basic_telefone
set numero = CONCAT(' ',SUBSTRING(numero, 1, 8))
WHERE numero IS NOT NULL 
AND SUBSTRING(numero, 1, 1) IN (1,2,3,4,5) --TIPO DE numero: 12345 = FIXO; 6789 = CELULAR
AND LENGTH(numero) = 8 --TAMANHO ANTIGO COM 8 DÍGITOS

-- Lucio - 03/09/2015 - Regime Tributário PJ: Simples, Lucro presumido e Lucro Real
alter table basic_pessoa 
add regimeTributario varchar(2);

update basic_pessoa 
set regimeTributario = "LR";


-- Lucio - 06/11/2013 - Escritório contábil aceita PF como responsável
ALTER TABLE `basic_escritorio_contabil` 
	DROP FOREIGN KEY `basic_escritorio_contabil_ibfk_3`;
ALTER TABLE `basic_escritorio_contabil` 
	DROP INDEX `juridica`;
ALTER TABLE `basic_escritorio_contabil` 
	CHANGE `juridica` `pessoa` BIGINT( 20 ) NOT NULL,
	ADD INDEX `juridica`(pessoa),
	ADD CONSTRAINT `basic_escritorio_contabil_ibfk_3` FOREIGN KEY (`pessoa`) REFERENCES `basic_pessoa` (`id`);

	-- Lucio - 12/12/2012 - Nova estrutura de endereço removendo PessoaEndereco da parada
ALTER TABLE `basic_endereco`
  ADD `pessoa` bigint(20) default NULL,
  ADD `enderecoCategoria` bigint(20) default NULL,
  ADD INDEX `pessoa` (`pessoa`),
  ADD INDEX `enderecoCategoria` (`enderecoCategoria`);

ALTER TABLE `basic_endereco`
  ADD CONSTRAINT `basic_endereco_categoria` FOREIGN KEY (`enderecoCategoria`) REFERENCES `basic_endereco_categoria` (`id`),
  ADD CONSTRAINT `basic_endereco_pessoa` FOREIGN KEY (`pessoa`) REFERENCES `basic_pessoa` (`id`);

UPDATE `basic_endereco` be
  INNER JOIN `basic_pessoa_endereco` bpe ON be.id = bpe.endereco
  SET be.pessoa = bpe.pessoa,
      be.enderecoCategoria = bpe.categoria
  WHERE bpe.pessoa is not null;
  
UPDATE `basic_endereco` be
  INNER JOIN `basic_pessoa` bp ON be.id = bp.enderecoCorrespondencia
  SET be.pessoa = bp.id,
      be.enderecoCategoria = 1
  WHERE be.pessoa is null;  
  
-- Exclui endereços perdidos
DELETE FROM `basic_pessoa_endereco`;  
DELETE FROM `basic_endereco`  
  WHERE pessoa is null; 
DROP TABLE `basic_pessoa_endereco`;  
  

-- Lucio - 11/03/2012
alter table basic_contrato 
  ADD codigoContaContabil varchar(20);

--- Lucio 18/10/2011
create table basic_adesao_contrato (discriminator varchar(3) not null, id bigint not null auto_increment, codigo varchar(20), dataAdesao datetime, dataRemocao datetime, inativo bit, dataEnvioSib datetime, dataValidadeCarteirinha date, vinculoTitular varchar(7), observacoes bigint, contrato_id bigint, pessoa bigint, primary key (id)) ENGINE=InnoDB;
create index codigo on basic_adesao_contrato (codigo);
alter table basic_adesao_contrato add index observacoes (observacoes), add constraint basic_adesao_contrato_observacoes foreign key (observacoes) references basic_observacoes (id);
alter table basic_adesao_contrato add index FK9070F94B80FC57FF (contrato_id), add constraint basic_adesao_contrato_FK9070F94B80FC57FF foreign key (contrato_id) references basic_contrato (id);
alter table basic_adesao_contrato add index pessoa (pessoa), add constraint basic_adesao_contrato_pessoa foreign key (pessoa) references basic_pessoa (id);


--- Lucio 19/09/2011
create table basic_telefone_operadora (
   id bigint not null auto_increment,
   nome varchar(100),
   primary key (id)
)ENGINE=InnoDB DEFAULT CHARSET=latin1;

ALTER TABLE `basic_telefone`
  ADD `telefoneOperadora` bigint(20) default NULL,
  ADD INDEX `telefoneOperadora` (`telefoneOperadora`);

ALTER TABLE `basic_telefone`
  ADD CONSTRAINT `basic_telefone_operadora` FOREIGN KEY (`telefoneOperadora`) REFERENCES `basic_telefone_operadora` (`id`);


--- Lucio 18/07/2011
alter table basic_pessoa 
add inss varchar (11),
add cmc varchar (10), 
add cco varchar (12); 

--- Lucio 13/06/2011
alter table basic_municipio 
add codigoIbge varchar (7); 

-- Antonio 08/05/2007 
-- Preenche a categoria de contrato do basic_contrato com informações de financeiro_contrato.
update basic_contrato bc 
set bc.categoria = (select fc.categoria 
                    from financeiro_contrato fc 
                    where fc.id = bc.id)
where bc.id in (select fc.id from financeiro_contrato fc)

update basic_contrato bc 
set bc.categoria = (select sc.categoria 
                    from sindicato_contrato sc 
                    where sc.id = bc.id)
where bc.id in (select sc.id from sindicato_contrato sc)

--Tatiana 05/02/2007 Nova estrutura de PESSOA
--cria os novos campos na tabela basic_pessoa
alter table basic_pessoa 
add documento varchar (15), 
add apelido varchar (255),
add dataInicial datetime,
add dataFinal datetime;

--copia os dados da estrutura antiga para a nova
update basic_pessoa bp 
set 
  bp.documento = 
	(select bj.cnpj from 
	basic_juridica bj 
	where bj.id=bp.id), 
  bp.apelido = 
	(select bj.nomeFantasia 
	from basic_juridica bj 
	where bj.id=bp.id), 
  bp.dataInicial = 
	(select bj.inicioAtividade 
	from basic_juridica bj 
	where bj.id=bp.id), 
  bp.dataFinal = 
	(select bj.finalAtividade 
	from basic_juridica bj 
	where bj.id=bp.id) 
where bp.id in 
  (select bj.id from basic_juridica bj); 

update basic_pessoa bp 
set 
  bp.documento = 
	(select bf.cpf 
	from basic_fisica bf 
	where bf.id=bp.id), 
  bp.dataInicial = 
	(select bf.dataNascimento from 
	basic_fisica bf 
	where bf.id=bp.id) 
where bp.id in 
  (select bf.id from basic_fisica bf);

--deleta os dados da estrutura antiga
alter table basic_juridica
drop cnpj,
drop nomeFantasia,
drop inicioAtividade,
drop finalAtividade;

alter table basic_fisica
drop cpf,
drop dataNascimento;


-- Lucio 20/11/2006 Nova ESTRUTURA DE ENDEREÇO

-- Retira a restrição, renomeia o campo cep para cep_ para usá-lo no ADAP
ALTER TABLE `basic_endereco`
  DROP FOREIGN KEY `cep`,
  CHANGE `cep` `cep_` BIGINT( 20 ) NULL DEFAULT NULL;

-- Adicionar os novos campos
ALTER TABLE `basic_endereco`
  ADD `bairro` bigint(20) default NULL,
  ADD `cep` varchar(8) default NULL,
  ADD `municipio` bigint(20) default NULL,
  ADD `logradouro` bigint(20) default NULL,
  ADD INDEX `bairro` (`bairro`),
  ADD INDEX `municipio` (`municipio`),
  ADD INDEX `logradouro` (`logradouro`);

ALTER TABLE `basic_endereco`
  ADD CONSTRAINT `basic_endereco_bairro` FOREIGN KEY (`bairro`) REFERENCES `basic_bairro` (`id`),
  ADD CONSTRAINT `basic_endereco_logradouro` FOREIGN KEY (`logradouro`) REFERENCES `basic_logradouro` (`id`),
  ADD CONSTRAINT `basic_endereco_municipio` FOREIGN KEY (`municipio`) REFERENCES `basic_municipio` (`id`);

-- Copia as definições
UPDATE `basic_endereco` be
SET 
  `bairro` = (SELECT bb.id FROM basic_bairro bb, basic_cep bc WHERE bb.id = bc.bairro AND bc.id = be.cep_),
  `cep` = (SELECT bc.codigo FROM basic_cep bc WHERE bc.id = be.cep_),
  `municipio` = (SELECT bm.id FROM basic_municipio bm, basic_cep bc WHERE bm.id = bc.municipio AND bc.id = be.cep_),
  `logradouro` = (SELECT bl.id FROM basic_logradouro bl, basic_cep bc WHERE bl.id = bc.logradouro AND bc.id = be.cep_);

-- Retira o campo cep e seu índice
ALTER TABLE `basic_endereco`
  DROP INDEX `cep`,
  DROP `cep_`;

DROP TABLE `basic_cep`;

-- Lucio 08/09/2006 Nova entidade Cargo e Socio e os relacionamentos
        
create table basic_cargo (
   id bigint not null auto_increment,
   nome varchar(100),
   primary key (id)
)ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Copia as profissões como funções
INSERT INTO basic_cargo( id, nome )
SELECT id, nome
FROM basic_profissao

CREATE TABLE basic_socio(
  id bigint NOT NULL AUTO_INCREMENT ,
  fisica bigint,
  juridica bigint,
  cargo bigint,
  dataEntrada datetime,
  dataSaida datetime,
  PRIMARY KEY ( id ) ,
  INDEX `cargo` ( `cargo` ) ,
  CONSTRAINT `basic_socio_cargo` FOREIGN KEY ( `cargo` ) REFERENCES `basic_cargo` ( `id` ),
  INDEX `fisica` ( `fisica` ) ,
  CONSTRAINT `basic_socio_fisica` FOREIGN KEY ( `fisica` ) REFERENCES `basic_fisica` ( `id` ),
  INDEX `juridica` ( `juridica` ) ,
  CONSTRAINT `basic_socio_juridica` FOREIGN KEY ( `juridica` ) REFERENCES `basic_juridica` ( `id` )
)ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Copia todos os atuais socios da velha estrutura para a nova estrutura de relação
INSERT INTO `basic_socio` ( fisica, juridica, cargo )
SELECT js.fisica, js.juridica, f.profissao
FROM basic_juridica_socio js
INNER JOIN basic_fisica f ON ( js.fisica = f.id );

DROP TABLE basic_juridica_socio;

ALTER TABLE `basic_contato`  
  DROP FOREIGN KEY profissao,
  DROP profissao,        
  ADD `cargo` bigint( 20 ) default NULL,
  ADD INDEX `cargo` (`cargo`),
  ADD CONSTRAINT `basic_contato_cargo` FOREIGN KEY (`cargo`) REFERENCES `basic_cargo` (`id`);

ALTER TABLE `basic_funcionario`  
  ADD `cargo` bigint( 20 ) default NULL,
  ADD INDEX `cargo` (`cargo`),
  ADD CONSTRAINT `basic_funcionario_cargo` FOREIGN KEY (`cargo`) REFERENCES `basic_cargo` (`id`);

-- Lucio 25/07/2006 Adaptação do campo observacoes dos contatos para VARCHAR()

ALTER TABLE `basic_contrato_contato`
  DROP FOREIGN KEY `basic_contrato_contato_observacoes`,
  DROP INDEX observacoes,
  DROP observacoes,
  ADD observacoes VARCHAR(255) NULL,
  ADD retornoCliente TINYINT(1) NULL;

--Andre 12/10/2007 - tabela para necessidades especiais (algumas pessoas ainda não tinham esta tabela)
-- Não esquecer de tirar a relação do indice antes de executar os comandos. E volte o indice depois. Não esqueça!
create table basic_necessidade_especial (id bigint not null auto_increment, descricao varchar(255), primary key (id)) ENGINE=InnoDB

ALTER TABLE `basic_fisica_necessidade_especial` DROP INDEX `necessidadeEspecial` ,
ADD INDEX `necessidadesEspeciais` ( `necessidadeEspecial` )

ALTER TABLE `basic_fisica_necessidade_especial` CHANGE `necessidadeEspecial` `necessidadesEspeciais` BIGINT( 20 ) NOT NULL

-- Lucio 23/10/2007 - Novos campos de seguranca
ALTER TABLE `basic_pessoa` ADD senha varchar(5), ADD codigoSeguranca varchar(3);

-- Lucio 06/11/2007 - Novo campo de contrato.codigo
ALTER TABLE `basic_contrato` ADD codigo varchar(20);

-- Lucio 29/11/2007 - Nome da Pãe e do Pai para a importação
ALTER TABLE `basic_pessoa`
 ADD `nomeMae` VARCHAR( 100 ) NULL ,
 ADD `nomePai` VARCHAR( 100 ) NULL ,
 ADD `documentoMilitarNumero` VARCHAR( 20 ) NULL ,
 ADD `documentoMilitarOrgao` VARCHAR( 3 ) NULL ,
 ADD `documentoMilitarTipo` VARCHAR( 3 ) NULL ,
 ADD `documentoMilitarDataExpedicao` DATE NULL;

-- Lucio 29/11/2007 - Nova classe País
create table basic_pais(id bigint not null auto_increment, nome varchar(50), sigla2 varchar(2), sigla3 varchar(3), primary key (id)) ENGINE=InnoDB;

ALTER TABLE `basic_pessoa`
  ADD `nacionalidade` bigint( 20 ) default NULL,
  ADD INDEX `nacionalidade` (`nacionalidade`),
  ADD CONSTRAINT `basic_pessoa_pais` FOREIGN KEY (`nacionalidade`) REFERENCES `basic_pais` (`id`);

