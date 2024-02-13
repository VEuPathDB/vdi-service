package vdi.test

import org.veupathdb.vdi.lib.common.field.DatasetID
import java.time.OffsetDateTime

typealias DSGetter<T> = (DatasetID) -> T

typealias DSConsumer = (DatasetID) -> Unit

typealias DSSync = (DatasetID, OffsetDateTime) -> Unit

typealias Runnable = () -> Unit

typealias Con<T> = (T) -> Unit

typealias Pro<T> = () -> T

typealias Fn<I, O> = (I) -> O

typealias BiFn<I1, I2, O> = (I1, I2) -> O

internal fun runnable() = Unit

internal fun <I> consumer(i: I) = Unit

internal fun <I1, I2> biConsumer(i1: I1, i2: I2) = Unit

internal fun <I1, I2, I3> triConsumer(i1: I1, i2: I2, i3: I3) = Unit

internal fun <I, O> oneParamNull(ignored: I): O? = null

internal fun <I1, I2, O> twoParamNull(i1: I1, i2: I2): O? = null

internal fun <O> noParamList(): List<O> = emptyList()

internal fun <I, O> oneParamList(i: I): List<O> = emptyList()

internal fun <I1, I2, O> twoParamList(i1: I1, i2: I2): List<O> = emptyList()

internal fun <I, K, V> oneParamMap(i: I): Map<K, V> = emptyMap()

internal fun <I1, I2, K, V> twoParamMap(i1: I1, i2: I2): Map<K, V> = emptyMap()
