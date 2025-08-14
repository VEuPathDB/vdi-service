package vdi.service.rest.server.inputs

import vdi.service.rest.generated.model.JSONPatchAction
import vdi.service.rest.generated.model.JSONPatchAction.OpType

class PatchRuleSet {
  private val rules =

  fun validate(actions: List<JSONPatchAction>) {

  }

  class Builder {
    private val allowedOps = ArrayList<OpType>(2)
    private val children = ArrayList<Pair<Matcher, Builder>>()

    fun allowOps(vararg ops: OpType) {
      allowedOps.addAll(ops)
    }

    fun addObjectField(field: String, builder: Builder.() -> Unit)

    fun addArrayValue(index: Int, builder: Builder.() -> Unit)

    fun addArrayWildcard(builder: Builder.() -> Unit)

    fun build(): Map<String, Rules> {

    }
  }

  private interface Matcher {
    fun matches(path: String): Boolean
  }

  @JvmInline
  private value class StringMatcher(val value: String): Matcher {
    override fun matches(path: String) = path == value
  }

  @JvmInline
  private value class PatternMatcher(val pattern: Regex): Matcher {
    override fun matches(path: String) = pattern.matches(path)
  }

  class Rules(val allowedOperations: Array<OpType>)
}

fun List<JSONPatchAction>.validate() {

}