package com.phantomvk.identifier.provider

import android.content.Context
import com.phantomvk.identifier.model.IdentifierResult
import com.phantomvk.identifier.model.ProviderConfig

/**
 * https://dev.mi.com/console/doc/detail?pId=1821
 */
internal class XiaomiProvider(config: ProviderConfig) : AbstractProvider(config) {

  private lateinit var clazz: Class<*>
  private lateinit var instance: Any

  override fun isSupported(): Boolean {
    clazz = Class.forName("com.android.id.impl.IdProviderImpl")
    instance = clazz.getDeclaredConstructor().newInstance()
    return true
  }

  override fun run() {
    when (val r = getId("getOAID")) {
      is BinderResult.Failed -> getConsumer().onError(r.msg, r.throwable)
      is BinderResult.Success -> {
        val aaid = queryId(IdEnum.AAID) { getId("getAAID") }
        val vaid = queryId(IdEnum.VAID) { getId("getVAID") }
        getConsumer().onSuccess(IdentifierResult(r.id, aaid, vaid))
      }
    }
  }

  private fun getId(name: String): BinderResult {
    return try {
      val method = clazz.getMethod(name, Context::class.java)
      checkId(method.invoke(instance, config.context) as? String)
    } catch (t: Throwable) {
      BinderResult.Failed(EXCEPTION_THROWN, t)
    }
  }
}

//object XiaomiClazz {
//  @Volatile
//  var sInstance: Any? = null
//
//  private var sMethodGetUDID: Method? = null
//  private var sMethodGetOAID: Method? = null
//  private var sMethodGetVAID: Method? = null
//  private var sMethodGetAAID: Method? = null
//
//  init {
//    try {
//      val clazz = Class.forName("com.android.id.impl.IdProviderImpl")
//      sInstance = clazz.getDeclaredConstructor().newInstance()
//      sMethodGetUDID = clazz.getMethod("getUDID", Context::class.java)
//      sMethodGetOAID = clazz.getMethod("getOAID", Context::class.java)
//      sMethodGetVAID = clazz.getMethod("getVAID", Context::class.java)
//      sMethodGetAAID = clazz.getMethod("getAAID", Context::class.java)
//    } catch (t: Throwable) {
//      sInstance = null
//      sMethodGetUDID = null
//      sMethodGetOAID = null
//      sMethodGetVAID = null
//      sMethodGetAAID = null
//    }
//  }
//
//  fun getId(context: Context, type: Int): String? {
//    val method = when (type) {
//      0 -> sMethodGetUDID
//      1 -> sMethodGetOAID
//      2 -> sMethodGetVAID
//      3 -> sMethodGetAAID
//      else -> sMethodGetOAID
//    }
//
//    return method?.invoke(sInstance ?: return null, context) as? String
//  }
//}
