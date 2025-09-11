package compose

import (
	"os"
	"os/exec"
)

const DefaultPath = "compose"

func DefaultFiles() []string {
	return []string{
		"docker-compose.yml",
		"docker-compose.dev.yml",
	}
}

func makeFileArgs(files []string) []string {
	fileArgs := make([]string, len(files)*2)

	for i, file := range files {
		j := i * 2
		fileArgs[j] = "-f"
		fileArgs[j+1] = file
	}

	return fileArgs
}

func appendEnvArgs(args []string) []string {
	return append(args,
		"--build-arg", "GH_USERNAME="+os.Getenv("GITHUB_USERNAME"),
		"--build-arg", "GH_TOKEN="+os.Getenv("GITHUB_TOKEN"),
	)
}

func prepCommand(path string, composeArgs []string, task string, taskArgs []string) *exec.Cmd {
	realArgs := make([]string, 2, len(composeArgs)+len(taskArgs)+1)
	realArgs[0] = "compose"
	realArgs[1] = "--env-file=../.env"
	realArgs = append(realArgs, composeArgs...)
	realArgs = append(realArgs, task)
	realArgs = append(realArgs, taskArgs...)

	cmd := exec.Command("docker", realArgs...)
	cmd.Env = os.Environ()
	cmd.Dir = path
	cmd.Stderr = os.Stderr
	cmd.Stdout = os.Stdout

	return cmd
}
