FROM postgres:17.6-alpine3.22

# Install the VDI schema
RUN apk add --no-cache git \
 && git clone https://github.com/VEuPathDB/VdiSchema \
 && cd VdiSchema/Main/lib/sql/Postgres \
 && cp createVdiControlTables.sql      /docker-entrypoint-initdb.d/01-createVdiControlTables.sql \
 && cp createUserDatasetTypeTables.sql /docker-entrypoint-initdb.d/02-createUserDatasetTypeTables.sql \
 && cp createEntityGraphTables.sql     /docker-entrypoint-initdb.d/03-createEntityGraphTables.sql \
 && rm -rf /VdiSchema
