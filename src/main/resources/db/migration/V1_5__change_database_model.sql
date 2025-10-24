-- Create tables for updated db model
create table messaging_setting (
    municipality_id varchar(5) not null,
    created datetime(6),
    updated datetime(6),
    id varchar(36) not null,
    primary key (id)
) engine=InnoDB;

create table messaging_setting_value (
    messaging_setting_id varchar(36) not null,
    `key` varchar(255) not null,
    `value` text not null,
    `type` enum ('BOOLEAN','NUMERIC','STRING') not null
) engine=InnoDB;

create index idx_messaging_setting_municipality_id 
   on messaging_setting (municipality_id);

create index idx_messaging_setting_value_messaging_setting_id_key 
   on messaging_setting_value (messaging_setting_id, `key`);

alter table if exists messaging_setting_value 
   add constraint uk_messaging_setting_id_key_value unique (messaging_setting_id, `key`, `value`);

alter table if exists messaging_setting_value 
   add constraint fk_messaging_setting_value_messaging_setting 
   foreign key (messaging_setting_id) 
   references messaging_setting (id);
       
-- Create a setting holder entity for all settings in the new model
   insert into messaging_setting (id, municipality_id, created, updated)
select id, municipality_id, created, updated from messaging_settings;

-- Move namespace setting values from messaging_settings table to namespace_settings table
insert into messaging_setting_value (messaging_setting_id, `key`, `value`, `type`)
select id, 'callback_email', callback_email, 'STRING' from messaging_settings where not isnull(namespace);

insert into messaging_setting_value (messaging_setting_id, `key`, `value`, `type`)
select id, 'contact_information_email', contact_information_email, 'STRING' from messaging_settings where not isnull(namespace);

insert into messaging_setting_value (messaging_setting_id, `key`, `value`, `type`)
select id, 'contact_information_email_name', contact_information_email_name, 'STRING' from messaging_settings where not isnull(namespace);

insert into messaging_setting_value (messaging_setting_id, `key`, `value`, `type`)
select id, 'contact_information_url', contact_information_url, 'STRING' from messaging_settings where not isnull(namespace);

insert into messaging_setting_value (messaging_setting_id, `key`, `value`, `type`)
select id, 'department_name', department_name, 'STRING' from messaging_settings where not isnull(namespace);

insert into messaging_setting_value (messaging_setting_id, `key`, `value`, `type`)
select id, 'namespace', namespace, 'STRING' from messaging_settings where not isnull(namespace);

insert into messaging_setting_value (messaging_setting_id, `key`, `value`, `type`)
select id, 'sms_sender', sms_sender, 'STRING' from messaging_settings where not isnull(namespace);

insert into messaging_setting_value (messaging_setting_id, `key`, `value`, `type`)
select id, 'support_text', support_text, 'STRING' from messaging_settings where not isnull(namespace);

-- Move department setting values from messaging_settings table to namespace_settings table
insert into messaging_setting_value (messaging_setting_id, `key`, `value`, `type`)
select id, 'contact_information_email', contact_information_email, 'STRING' from messaging_settings where isnull(namespace);

insert into messaging_setting_value (messaging_setting_id, `key`, `value`, `type`)
select id, 'contact_information_email_name', contact_information_email_name, 'STRING' from messaging_settings where isnull(namespace);

insert into messaging_setting_value (messaging_setting_id, `key`, `value`, `type`)
select id, 'contact_information_phone_number', contact_information_phone_number, 'STRING' from messaging_settings where isnull(namespace);

insert into messaging_setting_value (messaging_setting_id, `key`, `value`, `type`)
select id, 'contact_information_url', contact_information_url, 'STRING' from messaging_settings where isnull(namespace);

insert into messaging_setting_value (messaging_setting_id, `key`, `value`, `type`)
select id, 'department_id', department_id, 'NUMERIC' from messaging_settings where isnull(namespace);

insert into messaging_setting_value (messaging_setting_id, `key`, `value`, `type`)
select id, 'department_name', department_name, 'STRING' from messaging_settings where isnull(namespace);

insert into messaging_setting_value (messaging_setting_id, `key`, `value`, `type`)
select id, 'sms_sender', sms_sender, 'STRING' from messaging_settings where isnull(namespace);

insert into messaging_setting_value (messaging_setting_id, `key`, `value`, `type`)
select id, 'support_text', support_text, 'STRING' from messaging_settings where isnull(namespace);

insert into messaging_setting_value (messaging_setting_id, `key`, `value`, `type`)
select id, 'snail_mail_method', snail_mail_method, 'STRING' from messaging_settings where isnull(namespace);

insert into messaging_setting_value (messaging_setting_id, `key`, `value`, `type`)
select id, 'organization_number', organization_number, 'NUMERIC' from messaging_settings where isnull(namespace);

insert into messaging_setting_value (messaging_setting_id, `key`, `value`, `type`)
select id, 'folder_name', folder_name, 'STRING' from messaging_settings where isnull(namespace);

insert into messaging_setting_value (messaging_setting_id, `key`, `value`, `type`)
select id, 'sms_enabled', case when sms_enabled = 0 then 'false' else 'true' end, 'BOOLEAN' from messaging_settings where isnull(namespace);

insert into messaging_setting_value (messaging_setting_id, `key`, `value`, `type`)
select id, 'rek_enabled', case when rek_enabled = 0 then 'false' else 'true' end, 'BOOLEAN' from messaging_settings where isnull(namespace);

-- Remove old table
drop table messaging_settings;
