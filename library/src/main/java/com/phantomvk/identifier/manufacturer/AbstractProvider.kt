package com.phantomvk.identifier.manufacturer

import com.phantomvk.identifier.impl.Constants.BLANK_ID_FORMAT
import com.phantomvk.identifier.impl.Constants.BLANK_ID_FORMAT_VIVO
import com.phantomvk.identifier.impl.Constants.EXCEPTION_THROWN
import com.phantomvk.identifier.impl.Constants.ID_IS_INVALID
import com.phantomvk.identifier.impl.Constants.ID_IS_NULL_OR_BLANK
import com.phantomvk.identifier.interfaces.OnResultListener
import com.phantomvk.identifier.model.CallBinderResult
import com.phantomvk.identifier.model.ProviderConfig

abstract class AbstractProvider(protected val config: ProviderConfig) : Runnable {

  private lateinit var resultCallback: OnResultListener

  abstract fun getTag(): String

  protected abstract fun ifSupported(): Boolean

  protected abstract fun execute()

  fun isSupported(): Boolean {
    return try {
      ifSupported()
    } catch (t: Throwable) {
      false
    }
  }

  override fun run() {
    try {
      execute()
    } catch (t: Throwable) {
      getCallback().onError(EXCEPTION_THROWN, t)
    }
  }

  fun setCallback(callback: OnResultListener) {
    resultCallback = callback
  }

  protected fun getCallback(): OnResultListener {
    return resultCallback
  }

  protected fun isPackageInfoExisted(packageName: String): Boolean {
    return try {
      val manager = config.context.packageManager
      manager.getPackageInfo(packageName, 0) != null
    } catch (t: Throwable) {
      false
    }
  }

  protected fun isContentProviderExisted(packageName: String): Boolean {
    return try {
      val manager = config.context.packageManager
      manager.resolveContentProvider(packageName, 0) != null
    } catch (t: Throwable) {
      false
    }
  }

  fun checkId(id: String?): CallBinderResult {
    if (id.isNullOrBlank()) {
      return CallBinderResult.Failed(ID_IS_NULL_OR_BLANK)
    }

    if (id == BLANK_ID_FORMAT) {
      return CallBinderResult.Failed(ID_IS_INVALID)
    }

    return CallBinderResult.Success(id)
  }

  fun checkId(id: String?, callback: OnResultListener) {
    if (id.isNullOrBlank()) {
      callback.onError(ID_IS_NULL_OR_BLANK)
      return
    }

    if (id == BLANK_ID_FORMAT || id == BLANK_ID_FORMAT_VIVO) {
      callback.onError(ID_IS_INVALID)
      return
    }

    callback.onSuccess(id)
  }
}