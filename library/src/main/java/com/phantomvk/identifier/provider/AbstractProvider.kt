package com.phantomvk.identifier.provider

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

  protected fun checkId(id: String?, callback: OnResultListener? = null): CallBinderResult {
    if (id.isNullOrBlank()) {
      callback?.onError(ID_IS_NULL_OR_BLANK)
      return CallBinderResult.Failed(ID_IS_NULL_OR_BLANK)
    }

    if (id.all { it == '0' || it == '-' }) {
      callback?.onError(ID_IS_INVALID)
      return CallBinderResult.Failed(ID_IS_INVALID)
    }

    callback?.onSuccess(id)
    return CallBinderResult.Success(id)
  }
}