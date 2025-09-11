package compose

import (
	"errors"
	"os"
	"os/exec"
)

const CmdUp = "up"
const DescUp = "Starts up the compose stack, creating or replacing any missing or outdated containers."

func DefaultUp() {
	CustomizedUp(DefaultPath, DefaultFiles(), []string{"service"})
}

func CustomizedUp(path string, files, services []string) {
	ensurePrerequisites()

	if err := prepCommand(path, makeFileArgs(files), CmdUp, services).Run(); err != nil {
		var tmp *exec.ExitError
		if errors.As(err, &tmp) {
			os.Exit(tmp.ExitCode())
		} else {
			panic(err)
		}
	}
}
