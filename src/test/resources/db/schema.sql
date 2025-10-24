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
