#!/usr/bin/env sh

#
# Constants*
#
EXIT_CODE_VALIDATION_ERROR=1
EXIT_CODE_TRANSFORMATION_ERROR=2
EXIT_CODE_UNEXPECTED_ERROR=3


#
# Utilities
#

function log() {
  >&2 echo $1
}

function print() {
  echo $1
}

function verifyDir() {
  if [ -z "$1" ]; then
    log "required directory parameter was blank or absent"
    return 1
  fi

  if [ ! -d "$1" ]; then
    log "directory $1 does not exist or is not a directory"
    return 1
  fi

  return 0
}

#
# Parameter Handling
#

INPUT_DIR=$1
OUTPUT_DIR=$2

verifyDir "${INPUT_DIR}"  || exit $EXIT_CODE_UNEXPECTED_ERROR
verifyDir "${OUTPUT_DIR}" || exit $EXIT_CODE_UNEXPECTED_ERROR

#
# Script Body
#

for file in `ls $INPUT_DIR`; do
  log "Copying $file to $OUTPUT_DIR"
  cp $file $OUTPUT_DIR
done
