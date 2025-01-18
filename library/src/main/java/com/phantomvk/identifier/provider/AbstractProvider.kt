package com.phantomvk.identifier.provider

import android.content.ComponentName
import android.content.ContentProviderClient
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import android.os.Parcel
import com.phantomvk.identifier.listener.OnResultListener
import com.phantomvk.identifier.model.IdentifierResult
import com.phantomvk.identifier.model.ProviderConfig

internal abstract class AbstractProvider(protected val config: ProviderConfig) : Runnable {

  abstract fun isSupported(): Boolean

  protected open fun getInterfaceName(): String = ""

  private lateinit var resultCallback: OnResultListener

  // This method must be public.
  fun setCallback(callback: OnResultListener) {
    resultCallback = callback
  }

  protected fun getCallback(): OnResultListener {
    return resultCallback
  }

  protected fun isBrand(brand: String): Boolean {
    return Build.MANUFACTURER.equals(brand, true) && Build.BRAND.equals(brand, true)
  }

  protected fun isBrand(manufacturer: String, brand: String): Boolean {
    return Build.MANUFACTURER.equals(manufacturer, true) && Build.BRAND.equals(brand, true)
  }

  protected fun isPackageInfoExisted(packageName: String): Boolean {
    return try {
      config.context.packageManager.getPackageInfo(packageName, 0) != null
    } catch (t: Throwable) {
      false
    }
  }

  protected fun isContentProviderExisted(packageName: String): Boolean {
    return try {
      config.context.packageManager.resolveContentProvider(packageName, 0) != null
    } catch (t: Throwable) {
      false
    }
  }

  protected fun getSysProperty(key: String, defValue: String?): String? {
    return try {
      config.getSysProps?.invoke(null, key, defValue) as? String ?: defValue
    } catch (t: Throwable) {
      defValue
    }
  }

  protected fun isSysPropertyContainsKey(key: String): Boolean {
    return getSysProperty(key, null)?.isNotBlank() == true
  }

  protected fun getId(remote: IBinder, code: Int, writePackageName: Boolean = false): BinderResult {
    val data = Parcel.obtain()
    val reply = Parcel.obtain()
    try {
      data.writeInterfaceToken(getInterfaceName())
      if (writePackageName) data.writeString(config.context.packageName)
      remote.transact(code, data, reply, 0)
      reply.readException()
      return checkId(reply.readString())
    } catch (t: Throwable) {
      return BinderResult.Failed(EXCEPTION_THROWN, t)
    } finally {
      reply.recycle()
      data.recycle()
    }
  }

  protected fun queryId(type: IdEnum, callback: () -> BinderResult): String? {
    val isEnabled = when (type) {
      IdEnum.AAID -> config.queryAaid
      IdEnum.VAID -> config.queryVaid
    }

    return if (isEnabled) (callback.invoke() as? BinderResult.Success)?.id else null
  }

  protected fun checkId(id: String?, callback: OnResultListener? = null): BinderResult {
    val result = if (id.isNullOrBlank()) {
      BinderResult.Failed(ID_IS_NULL_OR_BLANK)
    } else if (id.all { it == '0' || it == '-' }) {
      BinderResult.Failed(ID_IS_INVALID)
    } else {
      BinderResult.Success(id)
    }

    if (callback == null) {
      return result
    }

    when (result) {
      is BinderResult.Failed -> callback.onError(result.msg)
      is BinderResult.Success -> callback.onSuccess(IdentifierResult(result.id))
    }

    return result
  }

  protected fun releaseContentProviderClient(client: ContentProviderClient) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      client.close()
    } else {
      client.release()
    }
  }

  protected fun bindService(intent: Intent, binderCallback: BinderCallback) {
    val conn = object : ServiceConnection {
      override fun onServiceConnected(name: ComponentName, service: IBinder) {
        config.executor.execute {
          try {
            when (val r = binderCallback.call(service)) {
              is BinderResult.Success -> getCallback().onSuccess(IdentifierResult(r.id, r.aaid, r.vaid))
              is BinderResult.Failed -> getCallback().onError(r.msg, r.throwable)
            }
          } catch (t: Throwable) {
            getCallback().onError(EXCEPTION_THROWN, t)
          } finally {
            config.context.unbindService(this)
          }
        }
      }

      override fun onServiceDisconnected(name: ComponentName) {
        getCallback().onError("Service is disconnected.")
      }

      override fun onBindingDied(name: ComponentName) {
        getCallback().onError("Service is on binding died.")
        config.context.unbindService(this)
      }

      override fun onNullBinding(name: ComponentName) {
        getCallback().onError("Service is on null binding.")
        config.context.unbindService(this)
      }
    }

    try {
      if (!config.context.bindService(intent, conn, Context.BIND_AUTO_CREATE)) {
        getCallback().onError("Bind service return false.")
        config.context.unbindService(conn)
      }
    } catch (t: Throwable) {
      getCallback().onError("Bind service error.", t)
      config.context.unbindService(conn)
    }
  }

  protected interface BinderCallback {
    fun call(binder: IBinder): BinderResult
  }

  protected sealed class BinderResult {
    class Success(val id: String, val vaid: String? = null, val aaid: String? = null) : BinderResult()
    class Failed(val msg: String, val throwable: Throwable? = null) : BinderResult()
  }

  protected enum class IdEnum { AAID, VAID }

  protected companion object {
    //    public static final String BLANK_ID_FORMAT = "00000000-0000-0000-0000-000000000000";
    //    public static final String BLANK_ID_FORMAT_VIVO = "0000000000000000000000000000000000000000000000000000000000000000";
    //    public static final String BLANK_ID_FORMAT_MEIZU = "00000000000000000000000000000000";
    const val AIDL_INTERFACE_IS_NULL: String = "Aidl interface is null."
    const val CONTENT_PROVIDER_CLIENT_IS_NULL: String = "ContentProvider client is null."
    const val BUNDLE_IS_NULL: String = "Bundle is null."
    const val EXCEPTION_THROWN: String = "Exception thrown when querying id."
    const val ID_INFO_IS_NULL: String = "Advertising identifier info is null."
    const val ID_IS_NULL_OR_BLANK: String = "ID is null or blank."
    const val ID_IS_INVALID: String = "ID is invalid."
    const val LIMIT_AD_TRACKING_IS_ENABLED: String = "Limit ad tracking is enabled."
    const val NO_AVAILABLE_COLUMN_INDEX: String = "No available column index."
    const val NO_IMPLEMENTATION_FOUND: String = "No implementation found."
    const val QUERY_CURSOR_IS_NULL: String = "Query cursor is null."
    const val SIGNATURE_IS_NULL: String = "Signature is null."
    const val SIGNATURE_HASH_IS_NULL: String = "Signature hash is null."
  }

//  protected inline fun <reified T> getResult(clazz: String, method: String, context: Context): T? {
//    return try {
//      Class.forName(clazz).getMethod(method, Context::class.java).invoke(null, context) as? T
//    } catch (t: Throwable) {
//      null
//    }
//  }
//
//  protected inline fun <reified T> getMethodResult(obj: Any, name: String): T? {
//    return try {
//      obj::class.java.getMethod(name).invoke(obj) as? T
//    } catch (t: Throwable) {
//      null
//    }
//  }
//
//  protected inline fun <reified T> getFieldResult(obj: Any, name: String): T? {
//    return try {
//      obj::class.java.getField(name).get(obj) as? T
//    } catch (t: Throwable) {
//      null
//    }
//  }
}