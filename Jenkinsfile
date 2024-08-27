#!groovy

@Library('pipelib@fixes')
import org.veupathdb.lib.Builder

node('centos8') {
  def builder = new Builder(this)

  builder.gitClone()
  
builder.buildContainers([
    [ name: 'vdi-service' ],
  ])
}
