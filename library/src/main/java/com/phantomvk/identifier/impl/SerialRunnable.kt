package com.phantomvk.identifier.impl

import com.phantomvk.identifier.IdentifierManager
import com.phantomvk.identifier.impl.Constants.NO_IMPLEMENTATION_FOUND
import com.phantomvk.identifier.interfaces.Disposable
import com.phantomvk.identifier.interfaces.OnResultListener
import com.phantomvk.identifier.log.Log
import com.phantomvk.identifier.manufacturer.AbstractProvider
import com.phantomvk.identifier.model.ProviderConfig
import java.util.concurrent.CountDownLatch

class SerialRunnable(config: ProviderConfig) : AbstractProvider(config), Disposable {

  private companion object {
    @Volatile
    private var cachedId: String? = null
  }

  private val proxy = ResultListenerProxy(config.callback)

  init {
    setCallback(proxy)
  }

  override fun getTag(): String {
    return "SerialRunnable"
  }

  override fun ifSupported(): Boolean {
    return true
  }

  override fun execute() {
    // return cached id if existed.
    val curCachedId = cachedId
    if (curCachedId != null) {
      getCallback().onSuccess(curCachedId)
      return
    }

    // fetch latest id.
    var isSuccess = false
    val providers = ManufacturerFactory.getProviders(config)
    for (provider in providers) {
      if (proxy.isDisposed()) {
        return
      }

      val latch = CountDownLatch(1)
      val resultCallback = object : OnResultListener {
        override fun onSuccess(id: String) {
          Log.i(getTag(), "${provider.getTag()} Success $id")

          if (IdentifierManager.getInstance().isMemCacheEnabled) {
            cachedId = id
          }

          getCallback().onSuccess(id)
          isSuccess = true
          latch.countDown()
        }

        override fun onError(msg: String, t: Throwable?) {
          Log.e("SerialRunnable", "${provider.getTag()} Error", t)
          latch.countDown()
        }
      }

      provider.setCallback(resultCallback)
      provider.run()

      latch.await()
      if (isSuccess) {
        return
      }
    }

    getCallback().onError(NO_IMPLEMENTATION_FOUND, null)
  }

  override fun dispose() {
    proxy.dispose()
  }
}