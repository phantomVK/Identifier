package com.phantomvk.identifier.provider

import android.content.ComponentName
import android.content.ContentProviderClient
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.os.Build
import android.os.IBinder
import com.phantomvk.identifier.interfaces.OnResultListener
import com.phantomvk.identifier.model.ProviderConfig
import java.security.MessageDigest

abstract class AbstractProvider(protected val config: ProviderConfig) : Runnable {

  private lateinit var resultCallback: OnResultListener

  abstract fun isSupported(): Boolean

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

  private fun getSignatures(pm: PackageManager, packageName: String): Array<Signature>? {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
      val flag = PackageManager.GET_SIGNING_CERTIFICATES
      val info = pm.getPackageInfo(packageName, flag)?.signingInfo ?: return null

      return if (info.hasMultipleSigners()) {
        info.apkContentsSigners
      } else {
        info.signingCertificateHistory
      }
    }

    val flag = PackageManager.GET_SIGNATURES
    val signatures = pm.getPackageInfo(packageName, flag)?.signatures
    if (signatures.isNullOrEmpty() || signatures[0] == null) {
      return null
    }

    return signatures
  }

  /**
   * Calculate hash value using specified algorithm.
   */
  private fun hash(algorithm: String, bytes: ByteArray): String? {
    if (bytes.isEmpty()) return null

    return try {
      val sb = StringBuilder()
      val byteArray = MessageDigest.getInstance(algorithm).digest(bytes)

      for (byte in byteArray) {
        sb.append(String.format("%02x", byte))
      }

      sb.toString()
    } catch (t: Throwable) {
      null
    }
  }

  protected fun getSignatureHash(): CallBinderResult {
    val pkgManager = config.context.packageManager
    val pkgName = config.context.packageName
    val signature = getSignatures(pkgManager, pkgName)?.firstOrNull()
    if (signature == null) {
      return CallBinderResult.Failed(SIGNATURE_IS_NULL)
    }

    val byteArray = signature.toByteArray()
    val sign = hash("SHA1", byteArray)
    if (sign.isNullOrBlank()) {
      return CallBinderResult.Failed(SIGNATURE_HASH_IS_NULL)
    }

    return CallBinderResult.Success(sign)
  }

  protected fun releaseContentProviderClient(client: ContentProviderClient) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      client.close()
    } else {
      client.release()
    }
  }

  protected fun bindService(
    intent: Intent,
    binderCallback: BinderCallback
  ) {
    val conn = object : ServiceConnection {
      override fun onServiceConnected(name: ComponentName, service: IBinder) {
        try {
          when (val result = binderCallback.call(service)) {
            is CallBinderResult.Success -> getCallback().onSuccess(result.id)
            is CallBinderResult.Failed -> getCallback().onError(result.msg)
          }
        } catch (t: Throwable) {
          getCallback().onError(EXCEPTION_THROWN, t)
        } finally {
          unbindService(config.context, this)
        }
      }

      override fun onServiceDisconnected(name: ComponentName) {
        getCallback().onError("Service is disconnected.")
      }

      override fun onBindingDied(name: ComponentName) {
        getCallback().onError("Service is on binding died.")
        unbindService(config.context, this)
      }

      override fun onNullBinding(name: ComponentName) {
        getCallback().onError("Service is on null binding.")
        unbindService(config.context, this)
      }
    }

    try {
      val success = config.context.bindService(intent, conn, Context.BIND_AUTO_CREATE)
      if (!success) {
        getCallback().onError("Bind service return false.", null)
      }
    } catch (t: Throwable) {
      getCallback().onError("Bind service error.", t)
    }
  }

  private fun unbindService(context: Context, conn: ServiceConnection) {
    try {
      context.unbindService(conn)
    } catch (ignore: Throwable) {
    }
  }

  protected interface BinderCallback {
    fun call(binder: IBinder): CallBinderResult
  }

  protected sealed class CallBinderResult {
    class Success(val id: String) : CallBinderResult()
    class Failed(val msg: String) : CallBinderResult()
  }

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
}