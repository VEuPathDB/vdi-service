package compose

import (
	"errors"
	"os"
	"os/exec"
)

const CmdStart = "start"
const DescStart = "Starts a previously stopped compose stack."

func DefaultStart() {
	CustomizedBuild(DefaultPath, DefaultFiles())
}

func CustomizedStart(path string, files []string) {
	if err := prepCommand(path, makeFileArgs(files), CmdStart, []string{}).Run(); err != nil {
		var tmp *exec.ExitError
		if errors.As(err, &tmp) {
			os.Exit(tmp.ExitCode())
		} else {
			panic(err)
		}
	}
}
