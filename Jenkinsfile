#!groovy

@Library('pipelib')
import org.veupathdb.lib.Builder

node('centos8') {
  def builder = new Builder(this)

  builder.gitClone()

  def tag = sh "git describe --tags || echo 'snapshot'"

  builder.buildContainers([
    [ name: 'vdi-service', dockerfile: 'service/Dockerfile', buildArgs: [ GIT_TAG: tag ] ],
  ])
}
