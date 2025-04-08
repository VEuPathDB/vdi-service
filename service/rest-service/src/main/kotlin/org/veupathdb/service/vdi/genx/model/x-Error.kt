package org.veupathdb.service.vdi.genx.model

import org.veupathdb.lib.container.jaxrs.errors.UnprocessableEntityException
import org.veupathdb.service.vdi.generated.model.BadRequestError
import org.veupathdb.service.vdi.generated.model.BadRequestErrorImpl
import org.veupathdb.service.vdi.generated.model.ForbiddenError
import org.veupathdb.service.vdi.generated.model.ForbiddenErrorImpl
import org.veupathdb.service.vdi.generated.model.NotFoundError
import org.veupathdb.service.vdi.generated.model.NotFoundErrorImpl
import org.veupathdb.service.vdi.generated.model.UnprocessableEntityError
import org.veupathdb.service.vdi.generated.model.UnprocessableEntityErrorImpl
import org.veupathdb.service.vdi.util.ValidationErrors

internal fun BadRequestError(message: String): BadRequestError =
  BadRequestErrorImpl().also { it.message = message }

internal fun ForbiddenError(message: String): ForbiddenError =
  ForbiddenErrorImpl().also { it.message = message }

internal fun NotFoundError(message: String? = null): NotFoundError =
  NotFoundErrorImpl().also { it.message = message }

internal fun UnprocessableEntityError(errors: ValidationErrors): UnprocessableEntityError =
  UnprocessableEntityErrorImpl().also {
    it.errors = UnprocessableEntityErrorImpl.ErrorsTypeImpl().also { e ->
      e.general = errors.general
      e.byKey   = UnprocessableEntityErrorImpl.ErrorsTypeImpl.ByKeyTypeImpl().also { k ->
        k.additionalProperties.putAll(errors.byKey)
      }
    }
  }

internal fun UnprocessableEntityError(x: UnprocessableEntityException): UnprocessableEntityError =
  UnprocessableEntityErrorImpl().also {
    it.errors = UnprocessableEntityErrorImpl.ErrorsTypeImpl().also { e ->
      e.general = x.general
      e.byKey = UnprocessableEntityErrorImpl.ErrorsTypeImpl.ByKeyTypeImpl().also { k ->
        k.additionalProperties.putAll(x.byKey)
      }
    }
  }
