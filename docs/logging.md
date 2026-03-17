# VDI Logging

## Log Format

VDI's logging includes structured key/value pairs with log-line-specific data to
assist with filtering logs by specific context.

The keys are constants [defined here](/project/common/logging/src/main/kotlin/vdi/logging/extensions.kt).

[!NOTE]
The key/value pairs will always appear in ascending order by key.

| Key | Meaning                       |
|-----|-------------------------------|
| `D` | VDI **D**ataset ID            |
| `E` | Unique **E**vent ID           |
| `I` | Plugin name, or **I**nstaller |
| `O` | Dataset **O**wner user ID     |
| `P` | **P**roject ID                |
| `R` | **R**equest ID                |
| `S` | Plugin **S**cript name        |
| `T` | Data **T**ype name            |
| `U` | Request **U**RI               |
| `W` | **W**orker identifier         |


*Example Usage*
```sh
# Trace an event through the system
grep -R "E=<EVENT_ID>"

# Trace a specific dataset ID
grep -R "D=<DATASET_ID>"

# Trace a specific request
grep -R "R=<REQUEST_ID>"

# Filter by user & project
grep -R "O=<USER_ID> P=<PROJECT_ID>"
```

## Event IDs

VDI is an event driven system, and a single event can result in actions across
multiple services.  To help trace the path and results of an event, a numeric ID
is assigned to each event that will follow the processing of that event through
each part of the core VDI service and plugin services.

[!WARNING]
Event IDs may not be issued for requests.  In those cases, the request ID is the
intended filter field.
