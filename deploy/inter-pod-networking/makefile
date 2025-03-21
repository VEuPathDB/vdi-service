.PHONY: default
default:
	@echo "no"

.PHONY: test-single-config
test-single-config:
	@podman kube play --network=test pod.yml
	@sleep 5
	@$(MAKE) print-logs || :
	@echo
	@podman kube down pod.yml

.PHONY: test-split-config
test-split-config:
	@podman kube play --network=test pod1.yml
	@podman kube play --network=test pod2.yml
	@sleep 3
	@$(MAKE) print-logs || :
	@echo
	@podman kube down pod1.yml
	@podman kube down pod2.yml

.PHONY: stop-pods
stop-pods:
	@podman kube down pod.yml

.PHONY: print-logs
print-logs:
	@tail -f \
	  <(podman logs -f inter-pod-network-test-pod-2-test) \
	  <(podman logs -f inter-pod-network-test-pod-1-test)
