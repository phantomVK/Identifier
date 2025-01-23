package com.phantomvk.identifier.model

import android.app.Application
import com.phantomvk.identifier.listener.OnResultListener
import org.junit.Assert.assertEquals
import org.junit.Test
import java.lang.ref.WeakReference
import java.util.concurrent.Executor

class ProviderConfigTest {

  @Test
  fun testClone() {
    val mockOnResultListener = object : OnResultListener {
      override fun onSuccess(result: IdentifierResult) {}
      override fun onError(msg: String, throwable: Throwable?) {}
    }

    val providerConfig = ProviderConfig(Application())
    providerConfig.executor = Executor { it.run() }
    providerConfig.isDebug = true
    providerConfig.isExperimental = true
    providerConfig.isMemCacheEnabled = true
    providerConfig.queryAaid = true
    providerConfig.queryVaid = true
    providerConfig.queryGoogleAdsId = true
    providerConfig.verifyLimitAdTracking = true
    providerConfig.callback = WeakReference(mockOnResultListener)

    val clonedConfig = providerConfig.clone()

    assertEquals(providerConfig.executor, clonedConfig.executor)
    assertEquals(providerConfig.isDebug, clonedConfig.isDebug)
    assertEquals(providerConfig.isExperimental, clonedConfig.isExperimental)
    assertEquals(providerConfig.verifyLimitAdTracking, clonedConfig.verifyLimitAdTracking)
    assertEquals(providerConfig.isMemCacheEnabled, clonedConfig.isMemCacheEnabled)
    assertEquals(providerConfig.queryAaid, clonedConfig.queryAaid)
    assertEquals(providerConfig.queryVaid, clonedConfig.queryVaid)
    assertEquals(providerConfig.queryGoogleAdsId, clonedConfig.queryGoogleAdsId)
  }

  @Test
  fun testGetCacheKey() {
    val providerConfig = ProviderConfig(Application())

    providerConfig.queryAaid = false
    providerConfig.queryVaid = false
    providerConfig.queryGoogleAdsId = false
    assertEquals("0", providerConfig.getCacheKey())

    providerConfig.queryAaid = true
    providerConfig.queryVaid = false
    providerConfig.queryGoogleAdsId = false
    assertEquals("1", providerConfig.getCacheKey())

    providerConfig.queryAaid = false
    providerConfig.queryVaid = true
    providerConfig.queryGoogleAdsId = false
    assertEquals("2", providerConfig.getCacheKey())

    providerConfig.queryAaid = false
    providerConfig.queryVaid = false
    providerConfig.queryGoogleAdsId = true
    assertEquals("4", providerConfig.getCacheKey())

    providerConfig.queryAaid = true
    providerConfig.queryVaid = true
    providerConfig.queryGoogleAdsId = false
    assertEquals("3", providerConfig.getCacheKey())

    providerConfig.queryAaid = true
    providerConfig.queryVaid = false
    providerConfig.queryGoogleAdsId = true
    assertEquals("5", providerConfig.getCacheKey())

    providerConfig.queryAaid = false
    providerConfig.queryVaid = true
    providerConfig.queryGoogleAdsId = true
    assertEquals("6", providerConfig.getCacheKey())

    providerConfig.queryAaid = true
    providerConfig.queryVaid = true
    providerConfig.queryGoogleAdsId = true
    assertEquals("7", providerConfig.getCacheKey())
  }
}