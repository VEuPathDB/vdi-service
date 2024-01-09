#!/usr/bin/env bash

SERVICE_URL="$1"
AUTH_TOKEN="$2"
INPUT_FILE="$3"

function printUsage() {
  echo "Usage:"
  echo "  delete-datasets <SERVICE_URL> <ADMIN_TOKEN> <INPUT_FILE>"
  echo ""
}

if [ -z "$SERVICE_URL" ] || [ -z "$AUTH_TOKEN" ] || [ -z "$INPUT_FILE" ]; then
  printUsage
  exit 1
fi

if ! [ -f "$INPUT_FILE" ]; then
  echo "File $INPUT_FILE not found."
  exit 1
fi

while read -r line; do
  echo curl -XDELETE -H"Admin-Token: $AUTH_TOKEN" "$SERVICE_URL/vdi-datasets/$line"
done < "$INPUT_FILE"