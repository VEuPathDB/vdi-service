package vdi.core.db.app

import java.sql.Connection
import java.sql.ShardingKey
import vdi.db.app.TargetDBPlatform

class TypedConnection(
  val platform: TargetDBPlatform,
  private val delegate: Connection,
): Connection by delegate {
  override fun beginRequest() {
    delegate.beginRequest()
  }

  override fun endRequest() {
    delegate.endRequest()
  }

  override fun setShardingKeyIfValid(
    shardingKey: ShardingKey?,
    superShardingKey: ShardingKey?,
    timeout: Int,
  ): Boolean {
    return delegate.setShardingKeyIfValid(shardingKey, superShardingKey, timeout)
  }

  override fun setShardingKeyIfValid(shardingKey: ShardingKey?, timeout: Int): Boolean {
    return delegate.setShardingKeyIfValid(shardingKey, timeout)
  }

  override fun setShardingKey(shardingKey: ShardingKey?, superShardingKey: ShardingKey?) {
    delegate.setShardingKey(shardingKey, superShardingKey)
  }

  override fun setShardingKey(shardingKey: ShardingKey?) {
    delegate.setShardingKey(shardingKey)
  }
}