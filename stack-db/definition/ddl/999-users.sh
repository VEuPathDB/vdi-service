#!/usr/bin/env bash

if [ ! -z "$POSTGRES_DB" ]; then
  DB_FLAG="--dbname=$POSTGRES_DB"
fi

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" $DB_FLAG <<-EOF
  GRANT ALL PRIVILEGES ON SCHEMA vdi TO "$POSTGRES_USER";
EOF
