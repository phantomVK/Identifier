package com.phantomvk.identifier.manufacturer

import android.annotation.SuppressLint
import android.content.Context
import com.phantomvk.identifier.model.ProviderConfig
import java.lang.reflect.Method

/**
 * https://dev.mi.com/console/doc/detail?pId=1821
 */
class XiaomiProvider(config: ProviderConfig) : AbstractProvider(config) {

  override fun getTag(): String {
    return "XiaomiProvider"
  }

  override fun ifSupported(): Boolean {
    return XiaomiClazz.sInstance != null
  }

  override fun execute() {
    val id = XiaomiClazz.getId(config.context, IdEnum.OAID)
    checkId(id, getCallback())
  }
}

@SuppressLint("PrivateApi")
object XiaomiClazz {
  @Volatile
  var sInstance: Any? = null

  private var sMethodGetUDID: Method? = null
  private var sMethodGetOAID: Method? = null
  private var sMethodGetVAID: Method? = null
  private var sMethodGetAAID: Method? = null

  init {
    try {
      val clazz = Class.forName("com.android.id.impl.IdProviderImpl")
      sInstance = clazz.getDeclaredConstructor().newInstance()
      sMethodGetUDID = clazz.getMethod("getUDID", Context::class.java)
      sMethodGetOAID = clazz.getMethod("getOAID", Context::class.java)
      sMethodGetVAID = clazz.getMethod("getVAID", Context::class.java)
      sMethodGetAAID = clazz.getMethod("getAAID", Context::class.java)
    } catch (t: Throwable) {
      sInstance = null
      sMethodGetUDID = null
      sMethodGetOAID = null
      sMethodGetVAID = null
      sMethodGetAAID = null
    }
  }

  fun getId(context: Context, type: IdEnum): String? {
    val method = when (type) {
      IdEnum.UDID -> sMethodGetUDID
      IdEnum.OAID -> sMethodGetOAID
      IdEnum.VAID -> sMethodGetVAID
      IdEnum.AAID -> sMethodGetAAID
    }

    return method?.invoke(sInstance ?: return null, context) as? String
  }
}

enum class IdEnum { UDID, OAID, VAID, AAID }