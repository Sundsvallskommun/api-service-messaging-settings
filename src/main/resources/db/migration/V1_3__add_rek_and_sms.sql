ALTER TABLE messaging_settings
    ADD COLUMN sms_enabled BIT(1) NOT NULL DEFAULT 0;

ALTER TABLE messaging_settings
    ADD COLUMN rek_enabled BIT(1) NOT NULL DEFAULT 0;
