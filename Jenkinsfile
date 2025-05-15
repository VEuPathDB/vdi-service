#!groovy

@Library('pipelib')
import org.veupathdb.lib.Builder

node('centos8') {
  def builder = new Builder(this)

  builder.gitClone()

  sh "git checkout ${env.GIT_COMMIT}"
  sh "git tags"
  def tag = sh(
    script: "git describe --tags",
    returnStdout: true
  )

  print(env)

  if (tag.isEmpty()) {
    tag = "snapshot"
  }

  builder.buildContainers([
    [ name: 'vdi-service', buildArgs: [ GIT_TAG: tag ] ],
  ])
}
