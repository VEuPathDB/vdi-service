package compose

import (
	"errors"
	"os"
	"os/exec"
)

const CmdDown = "down"
const DescDown = "Shuts down the compose stack, removing all volumes and container state."

func DefaultDown() {
	CustomizedDown(DefaultPath, DefaultFiles(), []string{})
}

func CustomizedDown(path string, files, services []string) {
	ensurePrerequisites()

	taskArgs := append(make([]string, 0, len(services)+2), "--remove-orphans", "--volumes")
	taskArgs = append(taskArgs, services...)

	if err := prepCommand(path, makeFileArgs(files), CmdDown, taskArgs).Run(); err != nil {
		var tmp *exec.ExitError
		if errors.As(err, &tmp) {
			os.Exit(tmp.ExitCode())
		} else {
			panic(err)
		}
	}
}
