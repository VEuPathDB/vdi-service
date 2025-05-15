#!groovy

@Library('pipelib')
import org.veupathdb.lib.Builder

node('centos8') {
  def builder = new Builder(this)

  builder.gitClone()

//   sh "git pull -f --tags"
//   sh "git checkout ${builder.gitCommit()}"

  def tag = sh(
    script: "git describe --tags --always",
    returnStdout: true
  )

  if (tag.isEmpty()) {
    tag = "snapshot"
  }

  builder.buildContainers([
    [ name: 'vdi-service', buildArgs: [ GIT_TAG: tag ] ],
  ])
}
