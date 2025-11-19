package vdi.test

import vdi.model.meta.DatasetID
import java.time.OffsetDateTime

internal typealias DSGetter<T> = (DatasetID) -> T

internal typealias DSConsumer = (DatasetID) -> Unit

internal typealias DSMultiUpdate = (DatasetID) -> Int

internal typealias DSSingleUpdate = (DatasetID) -> Boolean

internal typealias DSSync = (DatasetID, OffsetDateTime) -> Unit

internal typealias Runnable = () -> Unit

internal typealias Con<T> = (T) -> Unit
internal typealias BiCon<A, B> = (A, B) -> Unit

internal typealias Pro<T> = () -> T

internal typealias Fn<I, O> = (I) -> O

internal typealias BiFn<I1, I2, O> = (I1, I2) -> O

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

internal fun <I> multiUpdate(i: I): Int = 0
internal fun <A, B> multiUpdate(a: A, b: B): Int = 0

internal fun <I> singleUpdate(i: I): Boolean = false
internal fun <A, B> singleUpdate(a: A, b: B): Boolean = false