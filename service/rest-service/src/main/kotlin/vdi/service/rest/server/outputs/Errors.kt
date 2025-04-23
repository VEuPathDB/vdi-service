package vdi.service.rest.server.outputs

import org.veupathdb.lib.container.jaxrs.errors.UnprocessableEntityException
import org.veupathdb.lib.request.validation.ValidationErrors
import vdi.service.rest.generated.model.BadRequestError
import vdi.service.rest.generated.model.BadRequestErrorImpl
import vdi.service.rest.generated.model.ConflictError
import vdi.service.rest.generated.model.ConflictErrorImpl
import vdi.service.rest.generated.model.ForbiddenError
import vdi.service.rest.generated.model.ForbiddenErrorImpl
import vdi.service.rest.generated.model.NotFoundError
import vdi.service.rest.generated.model.NotFoundErrorImpl
import vdi.service.rest.generated.model.UnprocessableEntityError
import vdi.service.rest.generated.model.UnprocessableEntityErrorImpl
import vdi.service.rest.generated.support.ResponseDelegate

// region 400

fun BadRequestError(message: String): vdi.service.rest.generated.model.BadRequestError =
  vdi.service.rest.generated.model.BadRequestErrorImpl().also { it.message = message }

inline fun <reified T: vdi.service.rest.generated.support.ResponseDelegate> vdi.service.rest.generated.model.BadRequestError.wrap(): T =
  T::class.java.getDeclaredMethod("respond400WithApplicationJson")
    .invoke(null, this) as T

// endregion 400

// region 403

fun ForbiddenError(message: String): vdi.service.rest.generated.model.ForbiddenError =
  vdi.service.rest.generated.model.ForbiddenErrorImpl().also { it.message = message }

inline fun <reified T: vdi.service.rest.generated.support.ResponseDelegate> vdi.service.rest.generated.model.ForbiddenError.wrap(): T =
  T::class.java.getDeclaredMethod("respond403WithApplicationJson")
    .invoke(null, this) as T

// endregion 403

// region 404

object Static404 : vdi.service.rest.generated.model.NotFoundErrorImpl()

fun NotFoundError(message: String? = null): vdi.service.rest.generated.model.NotFoundError =
  vdi.service.rest.generated.model.NotFoundErrorImpl().also { it.message = message }

inline fun <reified T: vdi.service.rest.generated.support.ResponseDelegate> vdi.service.rest.generated.model.NotFoundError.wrap(): T =
  T::class.java.getDeclaredMethod("respond404WithApplicationJson")
    .invoke(null, this) as T

// endregion 404

// region 409

fun ConflictError(message: String): vdi.service.rest.generated.model.ConflictError =
  vdi.service.rest.generated.model.ConflictErrorImpl().also { it.message = message }

inline fun <reified T: vdi.service.rest.generated.support.ResponseDelegate> vdi.service.rest.generated.model.ConflictError.wrap(): T =
  T::class.java.getDeclaredMethod("respond409WithApplicationJson")
    .invoke(null, this) as T

// endregion 409

// region 422

fun UnprocessableEntityError(errors: ValidationErrors): vdi.service.rest.generated.model.UnprocessableEntityError =
  vdi.service.rest.generated.model.UnprocessableEntityErrorImpl().also {
    it.errors = vdi.service.rest.generated.model.UnprocessableEntityErrorImpl.ErrorsTypeImpl().also { e ->
      e.general = errors.general
      e.byKey   = vdi.service.rest.generated.model.UnprocessableEntityErrorImpl.ErrorsTypeImpl.ByKeyTypeImpl().also { k ->
        k.additionalProperties.putAll(errors.byKey)
      }
    }
  }

fun UnprocessableEntityError(x: UnprocessableEntityException): vdi.service.rest.generated.model.UnprocessableEntityError =
  vdi.service.rest.generated.model.UnprocessableEntityErrorImpl().also {
    it.errors = vdi.service.rest.generated.model.UnprocessableEntityErrorImpl.ErrorsTypeImpl().also { e ->
      e.general = x.general
      e.byKey = vdi.service.rest.generated.model.UnprocessableEntityErrorImpl.ErrorsTypeImpl.ByKeyTypeImpl().also { k ->
        k.additionalProperties.putAll(x.byKey)
      }
    }
  }

inline fun <reified T: vdi.service.rest.generated.support.ResponseDelegate> vdi.service.rest.generated.model.UnprocessableEntityError.wrap(): T =
  T::class.java.getDeclaredMethod("respond422WithApplicationJson")
    .invoke(null, this) as T

// endregion 422
