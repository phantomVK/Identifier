package com.phantomvk.identifier.model

import android.app.Application
import com.phantomvk.identifier.functions.Consumer
import org.junit.Assert.assertEquals
import org.junit.Test
import java.lang.ref.WeakReference
import java.util.concurrent.Executor

class ProviderConfigTest {

  @Test
  fun testClone() {
    val mockConsumer = object : Consumer {
      override fun onSuccess(result: IdentifierResult) {}
      override fun onError(msg: String, throwable: Throwable?) {}
    }

    val providerConfig = ProviderConfig(Application())
    providerConfig.executor = Executor { it.run() }
    providerConfig.isDebug = true
    providerConfig.isExperimental = true
    providerConfig.memoryConfig = MemoryConfig(true)
    providerConfig.idConfig = IdConfig(true, true, true)
    providerConfig.verifyLimitAdTracking = true
    providerConfig.consumer = WeakReference(mockConsumer)

    val clonedConfig = providerConfig.clone()

    assertEquals(providerConfig.executor, clonedConfig.executor)
    assertEquals(providerConfig.isDebug, clonedConfig.isDebug)
    assertEquals(providerConfig.isExperimental, clonedConfig.isExperimental)
    assertEquals(providerConfig.verifyLimitAdTracking, clonedConfig.verifyLimitAdTracking)
    assertEquals(providerConfig.memoryConfig.isEnabled, clonedConfig.memoryConfig.isEnabled)
    assertEquals(providerConfig.idConfig.isAaidEnabled, clonedConfig.idConfig.isAaidEnabled)
    assertEquals(providerConfig.idConfig.isVaidEnabled, clonedConfig.idConfig.isVaidEnabled)
    assertEquals(providerConfig.idConfig.isGoogleAdsIdEnabled, clonedConfig.idConfig.isGoogleAdsIdEnabled)
  }

  @Test
  fun testGetCacheKey() {
    val providerConfig = ProviderConfig(Application())

    providerConfig.idConfig = IdConfig()
    assertEquals("0", providerConfig.getCacheKey())

    providerConfig.idConfig = IdConfig(isAaidEnabled = true)
    assertEquals("1", providerConfig.getCacheKey())

    providerConfig.idConfig = IdConfig(isVaidEnabled = true)
    assertEquals("2", providerConfig.getCacheKey())

    providerConfig.idConfig = IdConfig(isGoogleAdsIdEnabled = true)
    assertEquals("4", providerConfig.getCacheKey())

    providerConfig.idConfig = IdConfig(isAaidEnabled = true, isVaidEnabled = true)
    assertEquals("3", providerConfig.getCacheKey())

    providerConfig.idConfig = IdConfig(isAaidEnabled = true, isGoogleAdsIdEnabled = true)
    assertEquals("5", providerConfig.getCacheKey())

    providerConfig.idConfig = IdConfig(isVaidEnabled = true, isGoogleAdsIdEnabled = true)
    assertEquals("6", providerConfig.getCacheKey())

    providerConfig.idConfig = IdConfig(true, true, true)
    assertEquals("7", providerConfig.getCacheKey())
  }
}