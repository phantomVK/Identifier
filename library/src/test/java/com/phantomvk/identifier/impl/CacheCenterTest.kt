package com.phantomvk.identifier.impl

import android.app.Application
import com.phantomvk.identifier.model.IdentifierResult
import com.phantomvk.identifier.model.ProviderConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class CacheCenterTest {

  private lateinit var cacheCenter: CacheCenter
  private lateinit var providerConfig: ProviderConfig
  private lateinit var identifierResult: IdentifierResult

  @Before
  fun setUp() {
    cacheCenter = CacheCenter
    providerConfig = ProviderConfig(Application())
    identifierResult = IdentifierResult(oaid = "oaid")
  }

  @Test
  fun testGetWithMemCacheEnabled() {
    providerConfig.isMemCacheEnabled = true
    cacheCenter.putIfAbsent(providerConfig, identifierResult)

    val result = cacheCenter.get(providerConfig)
    assertNotEquals(identifierResult, result)
  }

  @Test
  fun testGetWithMemCacheDisabled() {
    providerConfig.isMemCacheEnabled = false
    cacheCenter.putIfAbsent(providerConfig, identifierResult)

    val result = cacheCenter.get(providerConfig)
    assertNull(result)
  }

  @Test
  fun testPutIfAbsentWithMemCacheEnabled() {
    providerConfig.isMemCacheEnabled = true
    cacheCenter.putIfAbsent(providerConfig, identifierResult)

    val result = cacheCenter.get(providerConfig)
    assertNotEquals(identifierResult, result)
  }

  @Test
  fun testPutIfAbsentWithMemCacheDisabled() {
    providerConfig.isMemCacheEnabled = false
    cacheCenter.putIfAbsent(providerConfig, identifierResult)

    val result = cacheCenter.get(providerConfig)
    assertNull(result)
  }

  @Test
  fun testPutIfAbsentWithExistingKey() {
    providerConfig.isMemCacheEnabled = true
    cacheCenter.putIfAbsent(providerConfig, identifierResult)

    val newIdentifierResult = IdentifierResult(oaid = "oaid")
    cacheCenter.putIfAbsent(providerConfig, newIdentifierResult)

    val result = cacheCenter.get(providerConfig)
    assertEquals(identifierResult, result)
  }
}