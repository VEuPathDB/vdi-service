package vdi.service.rest.server.outputs

import org.veupathdb.lib.container.jaxrs.errors.UnprocessableEntityException
import org.veupathdb.lib.request.validation.ValidationErrors
import vdi.service.rest.generated.model.*
import vdi.service.rest.generated.support.ResponseDelegate

// region 400

fun BadRequestError(message: String): BadRequestError =
  BadRequestErrorImpl().also { it.message = message }

inline fun <reified T: ResponseDelegate> BadRequestError.wrap(): T =
  T::class.java.getMethod("respond400WithApplicationJson", BadRequestError::class.java)
    .invoke(null, this) as T

// endregion 400

// region 403

fun ForbiddenError(message: String): ForbiddenError =
  ForbiddenErrorImpl().also { it.message = message }

inline fun <reified T: ResponseDelegate> ForbiddenError.wrap(): T =
  T::class.java.getMethod("respond403WithApplicationJson", ForbiddenError::class.java)
    .invoke(null, this) as T

// endregion 403

// region 404

object Static404 : NotFoundErrorImpl()

fun NotFoundError(message: String? = null): NotFoundError =
  NotFoundErrorImpl().also { it.message = message }

inline fun <reified T: ResponseDelegate> NotFoundError.wrap(): T =
  T::class.java.getMethod("respond404WithApplicationJson", NotFoundError::class.java)
    .invoke(null, this) as T

// endregion 404

// region 409

fun ConflictError(message: String): ConflictError =
  ConflictErrorImpl().also { it.message = message }

inline fun <reified T: ResponseDelegate> ConflictError.wrap(): T =
  T::class.java.getMethod("respond409WithApplicationJson", ConflictError::class.java)
    .invoke(null, this) as T

// endregion 409

// region 422

fun UnprocessableEntityError(errors: ValidationErrors): UnprocessableEntityError =
  UnprocessableEntityErrorImpl().also {
    it.errors = UnprocessableEntityErrorImpl.ErrorsTypeImpl().also { e ->
      e.general = errors.general
      e.byKey   = UnprocessableEntityErrorImpl.ErrorsTypeImpl.ByKeyTypeImpl().also { k ->
        k.additionalProperties.putAll(errors.byKey)
      }
    }
  }

fun UnprocessableEntityError(x: UnprocessableEntityException): UnprocessableEntityError =
  UnprocessableEntityErrorImpl().also {
    it.errors = UnprocessableEntityErrorImpl.ErrorsTypeImpl().also { e ->
      e.general = x.general
      e.byKey = UnprocessableEntityErrorImpl.ErrorsTypeImpl.ByKeyTypeImpl().also { k ->
        k.additionalProperties.putAll(x.byKey)
      }
    }
  }

inline fun <reified T: ResponseDelegate> UnprocessableEntityError.wrap(): T =
  T::class.java.getDeclaredMethod("respond422WithApplicationJson", UnprocessableEntityError::class.java)
    .invoke(null, this) as T

// endregion 422
