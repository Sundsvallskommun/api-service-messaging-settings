
    create table messaging_settings (
        created datetime(6),
        updated datetime(6),
        id varchar(36) not null,
        callback_email varchar(255),
        contact_information_email varchar(255),
        contact_information_phone_number varchar(255),
        contact_information_url varchar(255),
        department_id varchar(255),
        department_name varchar(255),
        municipality_id varchar(255),
        sms_sender varchar(255),
        support_text text,
        snail_mail_method enum ('EMAIL','SC_ADMIN'),
        primary key (id)
    ) engine=InnoDB;

    create index idx_messaging_settings_municipality_id_department_id
       on messaging_settings (municipality_id, department_id);
