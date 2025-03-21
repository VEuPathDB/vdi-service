# K8S-Style Secret Injection

## Usage

### 1. Create the Secret

```shell
make create-secret
```

Output:
```
2fe0573927bd171f938e1c68e
```


### 2. Create the Pod

```shell
make start-pod
```

Output:
```
Pod:
cb31e0cb6034669c211fc88f7be0aadc3124806586e8daca8757daaaf8e293cb
Container:
50aeb43cc5daf2e61b0932cc1253a7edf7f08f699c35e6297dd5326c19ee0d7
```


### 3. Verify the Secret Injection

```shell
make print-logs
```

Output:
```
a secret value
```


### 4. View Secrets in Container Inspect

```shell
make print-env
```

Output:
```json
{
  "container": "podman",
  "SECRET": "a secret value",
  "PATH": "/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin",
  "HOME": "/root",
  "HOSTNAME": "test-pod"
}
```

### 5. Cleanup

```shell
make stop-pod remove-secret
```
