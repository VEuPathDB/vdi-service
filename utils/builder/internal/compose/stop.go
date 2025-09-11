package compose

import (
	"errors"
	"os"
	"os/exec"
)

const CmdStop = "stop"
const DescStop = "Stops a running compose stack, retaining volumes and container state."

func DefaultStop() {
	CustomizedBuild(DefaultPath, DefaultFiles())
}

func CustomizedStop(path string, files []string) {
	ensurePrerequisites()

	if err := prepCommand(path, makeFileArgs(files), CmdStop, []string{}).Run(); err != nil {
		var tmp *exec.ExitError
		if errors.As(err, &tmp) {
			os.Exit(tmp.ExitCode())
		} else {
			panic(err)
		}
	}
}
