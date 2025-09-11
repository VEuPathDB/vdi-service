package compose

import (
	"errors"
	"os"
	"os/exec"
)

const CmdBuild = "build"
const DescBuild = "Builds the docker compose local dev stack images whose dockerfiles reside in this repository."

func DefaultBuild() {
	CustomizedBuild(DefaultPath, DefaultFiles())
}

func CustomizedBuild(path string, files []string) {
	envArgs := appendEnvArgs(make([]string, 0, 4))

	if err := prepCommand(path, makeFileArgs(files), CmdBuild, envArgs).Run(); err != nil {
		var tmp *exec.ExitError
		if errors.As(err, &tmp) {
			os.Exit(tmp.ExitCode())
		} else {
			panic(err)
		}
	}
}
