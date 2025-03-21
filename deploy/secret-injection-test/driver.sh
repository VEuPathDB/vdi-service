#!/usr/bin/env sh

function lookup() {
cat <<EOF
apiVersion: v1
kind: Secret
metadata:
  name: test-secret
  namespace: test
stringData:
  SECRET: "a secret value"
EOF
}

case "$1" in
  list)
    echo "test-secret"
    ;;
  lookup)
    lookup
    ;;
  store)
    ;;
  delete)
    ;;
  *)
    exit 1
esac

