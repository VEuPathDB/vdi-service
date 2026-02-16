SELECT
  dataset_id
, 'success' as status
INTO vdi.upload_status
FROM vdi.datasets
;