create table auditorship_crud (id bigint not null auto_increment, description varchar(1000), ocurrencyDate datetime, terminal varchar(255), created bit, deleted bit, entityId bigint, updated bit, applicationUser bigint, applicationEntity bigint, primary key (id)) ENGINE=InnoDB
create table auditorship_process (id bigint not null auto_increment, description varchar(1000), ocurrencyDate datetime, terminal varchar(255), applicationProcess bigint, applicationUser bigint, primary key (id)) ENGINE=InnoDB
create table auditorship_service (id bigint not null auto_increment, description varchar(1000), ocurrencyDate datetime, terminal varchar(255), serviceName varchar(50), applicationUser bigint, primary key (id)) ENGINE=InnoDB
create table framework_document_model_entity (id bigint not null auto_increment, date datetime, description varchar(255), name varchar(50), source Text, applicationUser bigint, applicationEntity bigint, primary key (id)) ENGINE=InnoDB
create table framework_email_account (id bigint not null auto_increment, host varchar(50), password varchar(50), senderMail varchar(100), senderName varchar(100), useAsDefault bit, user varchar(50), primary key (id)) ENGINE=InnoDB
create table framework_label_address (id bigint not null auto_increment, line1 varchar(150), line2 varchar(150), line3 varchar(150), line4 varchar(150), line5 varchar(150), ocurrencyDate datetime, print bit, applicationUser bigint, addressLabelGroup bigint, applicationEntity bigint, primary key (id)) ENGINE=InnoDB
create table framework_label_address_group (id bigint not null auto_increment, name varchar(100), applicationUser bigint, primary key (id)) ENGINE=InnoDB
create table framework_label_model (id bigint not null auto_increment, envelope bit, fontName varchar(50), fontSize integer, horizontalDistance float, labelHeight float, labelWidth float, marginLeft float, marginTop float, name varchar(50), pageHeight float, pageWidth float, verticalDistance float, primary key (id)) ENGINE=InnoDB
create table framework_label_model_entity (id bigint not null auto_increment, description varchar(255), line1 varchar(255), line2 varchar(255), line3 varchar(255), line4 varchar(255), line5 varchar(255), name varchar(50), applicationEntity bigint, primary key (id)) ENGINE=InnoDB
create table framework_order_condiction (id bigint not null auto_increment, active bit, orderDirection integer, propertyPath varchar(200), userReport bigint, orderIndex integer, primary key (id)) ENGINE=InnoDB
create table framework_page_condiction (id bigint not null, itemsCount integer, page integer, pageSize integer, primary key (id)) ENGINE=InnoDB
create table framework_parent_condiction (id bigint not null, property varchar(50), applicationEntity bigint, primary key (id)) ENGINE=InnoDB
create table framework_query_condiction (id bigint not null auto_increment, active bit, closePar bit, initOperator integer, openPar bit, operatorId integer, propertyPath varchar(200), value1 varchar(50), value2 varchar(50), userReport bigint, orderIndex integer, primary key (id)) ENGINE=InnoDB
create table framework_result_condiction (id bigint not null auto_increment, propertyPath varchar(200), resultIndex integer, userReport bigint, primary key (id)) ENGINE=InnoDB
create table framework_user_report (id bigint not null auto_increment, date datetime, description Text, filterCondiction varchar(50), hqlWhereCondiction Text, name varchar(100), applicationEntity bigint, applicationUser bigint, primary key (id)) ENGINE=InnoDB
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