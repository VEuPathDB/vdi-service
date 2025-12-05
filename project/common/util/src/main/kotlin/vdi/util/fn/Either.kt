@file:Suppress("NOTHING_TO_INLINE")

package vdi.util.fn

sealed interface Either<L, R> {
  val isLeft: Boolean
  val isRight: Boolean

  fun unwrapLeft(): L
  fun unwrapRight(): R

  companion object {
    fun <L, R> left(value: L): Either<L, R> = LeftEither(value)
    fun <L, R> right(value: R): Either<L, R> = RightEither(value)

    fun <L, R> ofLeft(value: L): Either<L, R> = LeftEither(value)
    fun <L, R> ofRight(value: R): Either<L, R> = RightEither(value)
  }
}

inline fun <L, R> Either<L, R>.leftOr(fn: (right: R) -> L): L =
  if (isLeft) unwrapLeft() else fn(unwrapRight())

inline fun <L, R> Either<L, R>.rightOr(fn: (left: L) -> R): R =
  if (isRight) unwrapRight() else fn(unwrapLeft())

inline fun <OL, NL, R> Either<OL, R>.mapLeft(fn: (OL) -> NL): Either<NL, R> =
  if (isLeft) Either.ofLeft(fn(unwrapLeft())) else Either.ofRight(unwrapRight())

inline fun <L, OR, NR> Either<L, OR>.mapRight(fn: (OR) -> NR): Either<L, NR> =
  if (isRight) Either.ofRight(fn(unwrapRight())) else Either.ofLeft(unwrapLeft())

inline fun <L, R, O> Either<L, R>.fold(left: (L) -> O, right: (R) -> O): O =
  if (isLeft) left(unwrapLeft()) else right(unwrapRight())

inline fun <L: O, R: O, O> Either<L, R>.fold(): O =
  if (isLeft) unwrapLeft() else unwrapRight()

inline fun <L, R> Either<L, R>.leftOrNull(): L? =
  if (isLeft) unwrapLeft() else null

inline fun <L, R> Either<L, R>.rightOrNull(): R? =
  if (isRight) unwrapRight() else null

inline fun <L, R, NL> Either<L, R>.flatMapLeft(fn: (L) -> Either<NL, R>): Either<NL, R> =
  if (isLeft) fn(unwrapLeft()) else Either.ofRight(unwrapRight())

inline fun <L, R, NR> Either<L, R>.flatMapRight(fn: (R) -> Either<L, NR>): Either<L, NR> =
  if (isRight) fn(unwrapRight()) else Either.ofLeft(unwrapLeft())

@JvmInline
private value class LeftEither<L, R>(private val actual: L): Either<L, R> {
  override val isLeft: Boolean
    get() = true

  override val isRight: Boolean
    get() = false

  override fun unwrapLeft(): L = actual
  override fun unwrapRight(): R = throw NoSuchElementException()
}

@JvmInline
private value class RightEither<L, R>(private val actual: R): Either<L, R> {
  override val isLeft: Boolean
    get() = false

  override val isRight: Boolean
    get() = true

  override fun unwrapLeft(): L = throw NoSuchElementException()
  override fun unwrapRight(): R = actual
}
