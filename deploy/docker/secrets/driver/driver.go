package main

import "github.com/docker/go-plugins-helpers/secrets"

type Driver struct {
}

func (d Driver) Get(request secrets.Request) secrets.Response {
	return secrets.Response{Value: []byte("hello")}
}

func main() {
	panic(secrets.NewHandler(Driver{}).ServeUnix("docker_secrets", 0))
}
