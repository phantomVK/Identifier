package com.phantomvk.identifier.provider

import com.android.id.impl.IdProviderImpl
import com.phantomvk.identifier.model.IdentifierResult
import com.phantomvk.identifier.model.ProviderConfig

/**
 * https://dev.mi.com/console/doc/detail?pId=1821
 */
internal class XiaomiProvider(config: ProviderConfig) : AbstractProvider(config) {

  private lateinit var impl: IdProviderImpl

  override fun isSupported(): Boolean {
    impl = IdProviderImpl()
    return true
  }

  override fun run() {
    when (val r = checkId(impl.getOAID(config.context))) {
      is Failed -> getConsumer().onError(r.msg, r.throwable)
      is Success -> {
        val aaid = if (config.idConfig.isAaidEnabled) (checkId(impl.getAAID(config.context)) as? Success)?.id else null
        val vaid = if (config.idConfig.isVaidEnabled) (checkId(impl.getVAID(config.context)) as? Success)?.id else null
        getConsumer().onSuccess(IdentifierResult(r.id, aaid, vaid))
      }
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
