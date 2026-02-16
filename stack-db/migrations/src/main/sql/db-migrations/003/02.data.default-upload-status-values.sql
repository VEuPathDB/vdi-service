INSERT INTO
  vdi.upload_status (
    dataset_id
  , status
  )
SELECT
  dataset_id
, 'success' as status
FROM vdi.datasets
;