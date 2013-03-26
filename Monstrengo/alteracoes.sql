-- Lucio 21/05/2012: Tabela auditoria de serviços
CREATE TABLE  `auditorship_service` (
		  `id` bigint(20) NOT NULL AUTO_INCREMENT,
		  `serviceName` varchar(50) DEFAULT NULL,
		  `ocurrencyDate` datetime DEFAULT NULL,
		  `description` varchar(255) DEFAULT NULL,
		  `applicationUser` bigint(20) DEFAULT NULL,
		  `terminal` varchar(255) DEFAULT NULL,
		  PRIMARY KEY (`id`),
		  KEY `applicationUser` (`applicationUser`),
		  CONSTRAINT `auditorship_service_ibfk_1` FOREIGN KEY (`applicationUser`) REFERENCES `security_process` (`id`)
		) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Lucio 17/01/2012: Uma conta de email pode ser marcada como Padrão
ALTER TABLE `framework_email_account` 
ADD useAsDefault bit;
UPDATE `framework_email_account` 
set useAsDefault = false;

-- Lucio 21/11/2011: Adeus Application
DROP TABLE security_process_runnable_entities;

-- Lucio 21/07/2011: Novas propriedades padrões label, hint, description, colorName nos metadados
ALTER TABLE `security_entity` 
ADD `colorName` VARCHAR( 50 );

ALTER TABLE `security_entity_property_group` 
ADD `label` VARCHAR( 100 ) ,
ADD `hint` VARCHAR( 255 ) ,
ADD `description` TEXT,
ADD `colorName` VARCHAR( 50 );

UPDATE `security_entity_property_group`
  SET `label` = `name`;

ALTER TABLE `security_entity_property` 
ADD `applicationEntity` BIGINT( 20 ) ,
ADD INDEX `applicationEntity` ( `applicationEntity` ) ,
ADD CONSTRAINT security_entity_property_group_applicationEntity FOREIGN KEY ( `applicationEntity` ) REFERENCES security_entity( id );

-- Lucio 13/11/2008: Novo grupo para as etiquetas
create table framework_label_address_group (
   id bigint not null auto_increment,
   name varchar(100),
   applicationUser bigint,
   INDEX applicationUser( applicationUser ) ,
   CONSTRAINT framework_label_address_group_applicationUser FOREIGN KEY ( applicationUser ) REFERENCES security_user( id ),
   primary key (id)
) ENGINE=InnoDB;

ALTER TABLE `framework_label_address`
  ADD ocurrencyDate datetime, 
  ADD `addressLabelGroup` BIGINT( 20 ),
  ADD INDEX ( `addressLabelGroup` ) ,
  ADD CONSTRAINT `framework_label_address_addressLabelGroup` 
  FOREIGN KEY ( `addressLabelGroup` ) 
  REFERENCES `framework_label_address_group` ( `id` ); 

-- Yuka 16/02/2007 : Metadados persistidos no banco de dados
update `security_entity` set `runQueryOnOpen`=0;

ALTER TABLE `security_entity` ADD `description` TEXT,
ADD `hint` VARCHAR( 255 ) ,
ADD `runQueryOnOpen` TINYINT;

create table security_entity_property_group (
   id bigint not null auto_increment,
   indexGroup integer,
   name varchar(50),
   applicationEntity bigint,
   primary key (id)
);

create table security_entity_property (
   id bigint not null auto_increment,
   name varchar(30),
   label varchar(50),
   colorName varchar(20),
   allowSubQuery bit,
   defaultValue varchar(50),
   description Text,
   displayFormat varchar(20),
   editMask varchar(50),
   editShowList bit,
   hint varchar(255),
   indexProperty integer,
   indexGroup integer,
   maximum double precision,
   minimum double precision,
   readOnly bit,
   required bit,
   valuesList varchar(255),
   visible bit,
   applicationEntity bigint,
   primary key (id)
);

--------------------------


-- 05/06/2007 - Lucio: novas propriedades para o ApplicationProcess
-- Deve ser executado com a atualização da classe ApplicatinProcess e ProcessMetadataAnnotation
ALTER TABLE `security_process`
ADD `label` varchar(100),
ADD `hint` varchar(255),
ADD `description` Text;

CREATE TABLE security_process_runnable_entities(
  applicationProcess bigint,
  INDEX applicationProcess( applicationProcess ) ,
  CONSTRAINT runnable_entities_applicationProcess FOREIGN KEY ( applicationProcess ) REFERENCES security_process( id ),
  applicationEntity bigint,
  INDEX applicationEntity( applicationEntity ) ,
  CONSTRAINT runnable_entities_applicationEntity FOREIGN KEY ( applicationEntity ) REFERENCES security_entity( id )
  );

-- Lucio 29/12/2006 
-- Nova propriedade da entidade. label: um nome mais amigável da entidade
ALTER TABLE security_entity
  ADD label varchar(50);
  
UPDATE security_entity
  SET label=name;

-- Lucio 11/12/2006 
-- Alteração no modelo de etiquetas
ALTER TABLE framework_label_model
  ADD envelope tinyint(1);
  
UPDATE framework_label_model
  SET envelope = 0;

-- Lucio 11/12/2006 
-- Alteração no modelo de etiquetas
ALTER TABLE framework_label_model
  DROP labelType,
  DROP linesLabel,
  DROP columnsLabel,
  CHANGE marginUpper marginTop FLOAT NULL DEFAULT NULL,
  ADD fontName VARCHAR(50),
  ADD fontSize int;
  
UPDATE framework_label_model
  SET fontName = 'SansSerif', fontSize = 10;
-- FIM

create table framework_document_model_entity (
   id bigint not null auto_increment,
   applicationUser bigint,
   applicationEntity bigint,
   description varchar(100),
   name varchar(50),
   source Text,
   date datetime,
   primary key (id)
);

ALTER TABLE framework_document_model_entity 
  ADD INDEX applicationEntity( applicationEntity ) ,
  ADD CONSTRAINT framework_label_entity_entity FOREIGN KEY ( applicationEntity ) REFERENCES security_entity( id ),
  ADD INDEX applicationUser( applicationUser ) ,
  ADD CONSTRAINT framework_label_entity_user FOREIGN KEY ( applicationUser ) REFERENCES security_user( id );


CREATE TABLE framework_label_model_entity(
  id bigint NOT NULL AUTO_INCREMENT ,
  applicationEntity bigint,
  description varchar( 100 ) ,
  name varchar( 50 ) ,
  line1 varchar( 255 ) ,
  line2 varchar( 255 ) ,
  line3 varchar( 255 ) ,
  line4 varchar( 255 ) ,
  line5 varchar( 255 ) ,
  PRIMARY KEY ( id )
);


ALTER TABLE framework_label_model_entity 
  ADD INDEX applicationEntity( applicationEntity ) ,
  ADD CONSTRAINT framework_label_entity_entity FOREIGN KEY ( applicationEntity ) REFERENCES security_entity( id );

ALTER TABLE `framework_label_address`  
  ADD `applicationEntity` BIGINT( 20 ) NULL DEFAULT NULL ,
  ADD INDEX ( `applicationEntity` ) ,
  ADD CONSTRAINT `framework_label_address_entity` 
  FOREIGN KEY ( `applicationEntity` ) 
  REFERENCES `security_entity` ( `id` ); 

-- Tatiana 29/08/2006 Novo campo com o nome completo do operador 
--                    para exibição em impressoes
alter table security_user
add name varchar(50);

-- Lucio 08/08/2006 Etiquetas foram passadas para o framework
drop table basic_etiqueta_endereco;
drop table basic_etiqueta_modelo;

create table framework_label_address (
   id bigint not null auto_increment,
   print bit,
   applicationUser bigint,
   line1 varchar(100),
   line2 varchar(100),
   line3 varchar(100),
   line4 varchar(100),
   line5 varchar(100),
   primary key (id)
);
create table framework_label_model (
   id bigint not null auto_increment,
   name varchar(50),
   labelType integer,
   linesLabel integer,
   columnsLabel integer,
   marginUpper float,
   marginLeft float,
   horizontalDistance float,
   verticalDistance float,
   labelWidth float,
   labelHeight float,
   pageWidth float,
   pageHeight float,
   primary key (id)
);
alter table framework_label_address add index applicationUser (applicationUser), add constraint framework_label_address_applicationUser foreign key (applicationUser) references security_user (id);

-- Lucio 27/11/2007 Tabela de conta de email
create table framework_email_account (id bigint not null auto_increment, host varchar(50), password varchar(50), senderMail varchar(100), senderName varchar(100), user varchar(50), primary key (id)) ENGINE=InnoDB;


