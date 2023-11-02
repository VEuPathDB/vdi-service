#!/usr/bin/env sh

####
##  App DB Credentials
####
vault kv put secret/database/app/amoeba username='someDatabaseUser' password='someDatabasePassword'
vault kv put secret/database/app/crypto username='someDatabaseUser' password='someDatabasePassword'
vault kv put secret/database/app/fungi username='someDatabaseUser' password='someDatabasePassword'
vault kv put secret/database/app/giardia username='someDatabaseUser' password='someDatabasePassword'
vault kv put secret/database/app/host username='someDatabaseUser' password='someDatabasePassword'
vault kv put secret/database/app/microsporidia username='someDatabaseUser' password='someDatabasePassword'
vault kv put secret/database/app/piroplasma username='someDatabaseUser' password='someDatabasePassword'
vault kv put secret/database/app/plasmo username='someDatabaseUser' password='someDatabasePassword'
vault kv put secret/database/app/toxo username='someDatabaseUser' password='someDatabasePassword'
vault kv put secret/database/app/trich username='someDatabaseUser' password='someDatabasePassword'
vault kv put secret/database/app/tritryp username='someDatabaseUser' password='someDatabasePassword'
vault kv put secret/database/app/vector username='someDatabaseUser' password='someDatabasePassword'
vault kv put secret/database/app/ortho username='someDatabaseUser' password='someDatabasePassword'
vault kv put secret/database/app/clinepi username='someDatabaseUser' password='someDatabasePassword'
vault kv put secret/database/app/microbiome username='someDatabaseUser' password='someDatabasePassword'

####
##  Other Databases
####
vault kv put secret/database/account username='someDatabaseUser' password='someDatabasePassword'
vault kv put secret/database/user  username='someDatabaseUser' password='someDatabasePassword'

####
##  VDI Specific Credentials
####
vault kv put secret/vdi/cache-db username='someUser' password='somePassword'
vault kv put secret/vdi/admin-token value='someSecretKey'

####
##  Global Resources
####
vault kv put secret/global/rabbit username='someUser' password='somePassword'
vault kv put secret/global/minio accessToken='someToken' secretKey='someSecretKey'

####
##  VEuPathDB Globals
####
vault kv put secret/veupathdb/auth-secret-key value='someAuthSecretKey'
vault kv put secret/veupathdb/ldap-servers value='someLDAPServers'

# Set up the policy that will allow the VDI stack access to the vault secrets
# that it needs
vault policy write vdi - <<EOF
path "secret/data/database/*" {
  capabilities = ["read"]
}

path "secret/data/vdi/*" {
  capabilities = ["read"]
}

path "secret/data/global/rabbit" {
  capabilities = ["read"]
}

path "secret/data/global/minio" {
  capabilities = ["read"]
}

path "secret/data/veupathdb/auth-secret-key" {
  capabilities = ["read"]
}

path "secret/data/veupathdb/ldap-servers" {
  capabilities = ["read"]
}
EOF
