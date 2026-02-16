INSERT INTO
  vdi.service_metadata (key, value)
VALUES
  ('db.version', '2')
ON CONFLICT (key) DO UPDATE
SET
  value = '3'
