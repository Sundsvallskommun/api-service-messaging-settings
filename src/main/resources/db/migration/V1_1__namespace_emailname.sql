ALTER TABLE messaging_settings
    ADD COLUMN contact_information_email_name VARCHAR(255),
    ADD COLUMN namespace                      VARCHAR(255);

CREATE INDEX idx_messaging_settings_municipality_id_namespace
    ON messaging_settings (municipality_id, namespace);

CREATE INDEX idx_messaging_settings_municipality_id_namespace_department_id
    ON messaging_settings (municipality_id, namespace, department_id);
