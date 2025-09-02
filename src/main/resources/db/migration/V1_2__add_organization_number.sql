ALTER TABLE messaging_settings
    ADD COLUMN organization_number VARCHAR(12);

CREATE INDEX idx_messaging_settings_municipality_id_namespace_department_name
    ON messaging_settings (municipality_id, namespace, department_name);
