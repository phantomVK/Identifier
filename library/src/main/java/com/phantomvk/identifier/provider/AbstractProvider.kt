package com.phantomvk.identifier.provider

import android.content.ComponentName
import android.content.ContentProviderClient
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import android.os.Parcel
import androidx.annotation.IntDef
import com.phantomvk.identifier.functions.Consumer
import com.phantomvk.identifier.model.IdentifierResult
import com.phantomvk.identifier.model.ProviderConfig

internal abstract class AbstractProvider(protected val config: ProviderConfig) {

  abstract fun isSupported(): Boolean

  abstract fun run()

  protected open fun getInterfaceName(): String = ""

  private lateinit var consumer: Consumer

  // This method must be public.
  fun setConsumer(callback: Consumer) {
    consumer = callback
  }

  protected fun getConsumer(): Consumer {
    return consumer
  }

  protected fun isBrand(manufacturer: String, brand: String): Boolean {
    return Build.BRAND.equals(brand, true) && Build.MANUFACTURER.equals(manufacturer, true)
  }

  protected fun isPackageInfoExisted(packageName: String): Boolean {
    return config.context.packageManager.getPackageInfo(packageName, 0) != null
  }

  protected fun isContentProviderExisted(packageName: String): Boolean {
    return config.context.packageManager.resolveContentProvider(packageName, 0) != null
  }

  protected fun getSysProperty(key: String, defValue: String?): String? {
    return try {
      config.sysProps.invoke(null, key, defValue) as? String ?: defValue
    } catch (t: Throwable) {
      defValue
    }
  }

  protected fun readBoolean(
    remote: IBinder,
    code: Int,
    defValue: Boolean,
    writeData: ((Parcel) -> Unit)?
  ): Boolean {
    val data = Parcel.obtain()
    val reply = Parcel.obtain()
    try {
      data.writeInterfaceToken(getInterfaceName())
      writeData?.invoke(data)
      remote.transact(code, data, reply, 0)
      reply.readException()
      return 0 != reply.readInt()
    } catch (t: Throwable) {
      return defValue
    } finally {
      reply.recycle()
      data.recycle()
    }
  }

  protected fun queryId(
    binder: IBinder,
    oaidCode: Int,
    vaidCode: Int,
    aaidCode: Int
  ): BinderResult {
    return when (val o = getId(binder, oaidCode)) {
      is BinderResult.Failed -> o
      is BinderResult.Success -> {
        val vaid = invokeById(IdEnum.VAID) { getId(binder, vaidCode) }
        val aaid = invokeById(IdEnum.AAID) { getId(binder, aaidCode) }
        BinderResult.Success(o.id, vaid, aaid)
      }
    }
  }

  protected fun getId(remote: IBinder, code: Int): BinderResult {
    val data = Parcel.obtain()
    val reply = Parcel.obtain()
    try {
      data.writeInterfaceToken(getInterfaceName())
      data.writeString(config.context.packageName)
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

  protected fun invokeById(@IdEnum type: Int, callback: () -> BinderResult): String? {
    val isEnabled = when (type) {
      IdEnum.AAID -> config.idConfig.isAaidEnabled
      IdEnum.VAID -> config.idConfig.isVaidEnabled
      else -> false
    }

    return if (isEnabled) (callback.invoke() as? BinderResult.Success)?.id else null
  }

  protected fun checkId(id: String?, callback: Consumer? = null): BinderResult {
    val result = when {
      id.isNullOrBlank() -> BinderResult.Failed(ID_IS_NULL_OR_BLANK)
      id.any { it != '0' && it != '-' } -> BinderResult.Success(id, null, null)
      else -> BinderResult.Failed(ID_IS_INVALID)
    }

    if (callback != null) {
      when (result) {
        is BinderResult.Failed -> callback.onError(result.msg)
        is BinderResult.Success -> callback.onSuccess(IdentifierResult(result.id))
      }
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

  private fun unbindServiceOnError(conn: ServiceConnection, msg: String, t: Throwable?) {
    getConsumer().onError(msg, t)

    try {
      config.context.unbindService(conn)
    } catch (ignore: Exception) {
      // Caused by: java.lang.IllegalArgumentException: Service not registered
    }
  }

  protected fun bindService(intent: Intent, binderCallback: BinderCallback) {
    val conn = object : ServiceConnection {
      override fun onServiceConnected(name: ComponentName, service: IBinder) {
        config.executor.execute {
          try {
            when (val r = binderCallback.call(service)) {
              is BinderResult.Success -> getConsumer().onSuccess(IdentifierResult(r.id, r.aaid, r.vaid))
              is BinderResult.Failed -> getConsumer().onError(r.msg, r.throwable)
            }
          } catch (t: Throwable) {
            unbindServiceOnError(this, EXCEPTION_THROWN, t)
          }
        }
      }

      override fun onServiceDisconnected(name: ComponentName) {
        unbindServiceOnError(this, "Service is disconnected.", null)
      }

      override fun onBindingDied(name: ComponentName) {
        unbindServiceOnError(this, "Service is on binding died.", null)
      }

      override fun onNullBinding(name: ComponentName) {
        unbindServiceOnError(this, "Service is on null binding.", null)
      }
    }

    try {
      if (!config.context.bindService(intent, conn, Context.BIND_AUTO_CREATE)) {
        unbindServiceOnError(conn, "Bind service return false.", null)
      }
    } catch (t: Throwable) {
      unbindServiceOnError(conn, "Bind service error.", t)
    }
  }

  protected interface BinderCallback {
    fun call(binder: IBinder): BinderResult
  }

  protected sealed interface BinderResult {
    class Success(val id: String, val vaid: String?, val aaid: String?) : BinderResult
    class Failed(val msg: String, val throwable: Throwable? = null) : BinderResult
  }

  @IntDef(IdEnum.AAID, IdEnum.VAID)
  @Retention(AnnotationRetention.SOURCE)
  protected annotation class IdEnum {
    companion object {
      const val AAID = 0
      const val VAID = 1
    }
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

//    public static final String BLANK_ID_FORMAT = "00000000-0000-0000-0000-000000000000";
//    public static final String BLANK_ID_FORMAT_VIVO = "0000000000000000000000000000000000000000000000000000000000000000";
//    public static final String BLANK_ID_FORMAT_MEIZU = "00000000000000000000000000000000";
internal const val AIDL_INTERFACE_IS_NULL: String = "Aidl interface is null."
internal const val CONTENT_PROVIDER_CLIENT_IS_NULL: String = "ContentProvider client is null."
internal const val BUNDLE_IS_NULL: String = "Bundle is null."
internal const val EXCEPTION_THROWN: String = "Exception thrown when querying id."
internal const val ID_INFO_IS_NULL: String = "Advertising identifier info is null."
internal const val ID_IS_NULL_OR_BLANK: String = "ID is null or blank."
internal const val ID_IS_INVALID: String = "ID is invalid."
internal const val LIMIT_AD_TRACKING_IS_ENABLED: String = "Limit ad tracking is enabled."
internal const val NO_AVAILABLE_COLUMN_INDEX: String = "No available column index."
internal const val NO_IMPLEMENTATION_FOUND: String = "No implementation found."
internal const val QUERY_CURSOR_IS_NULL: String = "Query cursor is null."
internal const val SIGNATURE_IS_NULL: String = "Signature is null."
internal const val SIGNATURE_HASH_IS_NULL: String = "Signature hash is null."
internal const val SYSTEM_PROPS_METHOD_NOT_FOUND: String = "SystemProperties not found using reflection."
internal const val PRIVACY_IS_NOT_ACCEPTED: String = "Privacy is not accepted."