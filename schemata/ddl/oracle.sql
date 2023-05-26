-- IMPORTANT:
--   This is just a reference for the design of the tables in the VDI schema.
--   The "live" schema creation script lives in the ApiCommonData/Load sql
--   scripts directory.

CREATE USER vdi IDENTIFIED BY asdfasdfasdfasdfasdf;
ALTER USER vdi QUOTA UNLIMITED ON users;

CREATE TABLE vdi.dataset (
  dataset_id CHAR(32)
    PRIMARY KEY
    NOT NULL
, owner NUMBER
    NOT NULL
, type_name VARCHAR2(64)
    NOT NULL
, type_version VARCHAR2(64)
    NOT NULL
, is_deleted NUMBER
    DEFAULT 0
    NOT NULL
);

CREATE TABLE vdi.sync_control (
  dataset_id CHAR(32)
    NOT NULL
, shares_update_time TIMESTAMP WITH TIME ZONE
    NOT NULL
, data_update_time TIMESTAMP WITH TIME ZONE
    NOT NULL
, meta_update_time TIMESTAMP WITH TIME ZONE
    NOT NULL
, FOREIGN KEY (dataset_id) REFERENCES vdi.dataset (dataset_id)
);

CREATE TABLE vdi.dataset_install_message (
  dataset_id CHAR(32)
    NOT NULL
, install_type VARCHAR2(64)
    NOT NULL
, status VARCHAR2(64)
    NOT NULL
, message CLOB
, FOREIGN KEY (dataset_id) REFERENCES vdi.dataset (dataset_id)
);

CREATE TABLE vdi.dataset_visibility (
  dataset_id CHAR(32)
    NOT NULL
, user_id NUMBER
    NOT NULL
, FOREIGN KEY (dataset_id) REFERENCES vdi.dataset (dataset_id)
);

CREATE TABLE vdi.dataset_project (
  dataset_id CHAR(32)
    NOT NULL
, project_id VARCHAR2(64)
    NOT NULL
, FOREIGN KEY (dataset_id) REFERENCES vdi.dataset (dataset_id)
);
