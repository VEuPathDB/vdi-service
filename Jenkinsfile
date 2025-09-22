#!groovy

@Library('pipelib')
import org.veupathdb.lib.Builder

node('centos8') {
  def builder = new Builder(this)

  builder.gitClone()
  builder.buildContainers([
    [ name: 'vdi-service', dockerfile: 'project/core/Dockerfile' ],
    [ name: 'vdi-internal-db', path: 'stack-db/definition' ]
  ])
}
