package compose

import (
	"log"
	"os"
)

func ensurePrerequisites() {
	failed := false

	if !testFile(".env") {
		log.Println("missing .env file")
		failed = true
	}

	// if !testFile("docker-compose.ssh.yml") {
	// 	log.Println("missing docker-compose.ssh.yml")
	// 	failed = true
	// }
	//
	// if os.Getenv("SSH_AUTH_SOCK") == "" {
	// 	log.Println("SSH_AUTH_SOCK env var not found (is ssh-agent running?)")
	// 	failed = true
	// }

	if failed {
		os.Exit(1)
	}
}

func testFile(path string) bool {
	if _, err := os.Stat(path); err != nil {
		if os.IsNotExist(err) {
			return false
		} else {
			log.Panicln(err)
		}
	}

	return true
}
