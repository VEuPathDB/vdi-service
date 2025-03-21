.PHONY: default
default:
	@echo "nope"

.PHONY: start-pod
start-pod:
	@CONTAINERS_CONF=shell-driver.ini \
	  podman kube play pod.yml

.PHONY: stop-pod
stop-pod:
	@CONTAINERS_CONF=shell-driver.ini \
	  podman kube down pod.yml

.PHONY: remove-secret
remove-secret:
	@podman secret rm test-secret

.PHONY: create-secret
create-secret:
	@CONTAINERS_CONF=shell-driver.ini \
	  podman secret create test-secret - <<<nothing

.PHONY: print-logs
print-logs:
	@podman logs test-pod-test-container

.PHONY: print-env
print-env:
	@podman inspect test-pod-test-container --format='{{ .Config.Env | json }}' \
	  | jq '[ .[] | split("=") | { key: .[0], value: .[1] } ] | from_entries'
