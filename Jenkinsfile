#!groovy

@Library('pipelib')
import org.veupathdb.lib.Builder

node('centos8') {
  def builder = new Builder(this)

  builder.gitClone()

  sh "git fetch --tags --depth=1"
  def tag = sh "git describe --tags || echo 'snapshot'"

  builder.buildContainers([
    [ name: 'vdi-service', dockerfile: 'service/Dockerfile', buildArgs: [ GIT_TAG: tag ] ],
  ])
}
