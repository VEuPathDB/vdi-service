INSERT INTO
  vdi.service_metadata (key, value)
VALUES
  ('db.version', '3')
ON CONFLICT (key) DO UPDATE
SET
  value = '3'
