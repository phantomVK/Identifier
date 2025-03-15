package com.phantomvk.identifier.impl

import android.app.Application
import com.phantomvk.identifier.internal.CacheCenter
import com.phantomvk.identifier.model.IdentifierResult
import com.phantomvk.identifier.model.MemoryConfig
import com.phantomvk.identifier.model.ProviderConfig
import org.junit.Assert.assertEquals
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
    providerConfig.memoryConfig = MemoryConfig(true)
    cacheCenter.put(providerConfig, identifierResult)

    val result = cacheCenter.get(providerConfig)
    assertEquals(identifierResult, result)
  }

  @Test
  fun testGetWithMemCacheDisabled() {
    providerConfig.memoryConfig = MemoryConfig(false)
    cacheCenter.put(providerConfig, identifierResult)

    val result = cacheCenter.get(providerConfig)
    assertNull(result)
  }

  @Test
  fun testPutIfAbsentWithMemCacheEnabled() {
    providerConfig.memoryConfig = MemoryConfig(true)
    cacheCenter.put(providerConfig, identifierResult)

    val result = cacheCenter.get(providerConfig)
    assertEquals(identifierResult, result)
  }

  @Test
  fun testPutIfAbsentWithMemCacheDisabled() {
    providerConfig.memoryConfig = MemoryConfig(false)
    cacheCenter.put(providerConfig, identifierResult)

    val result = cacheCenter.get(providerConfig)
    assertNull(result)
  }

  @Test
  fun testPutIfAbsentWithExistingKey() {
    providerConfig.memoryConfig = MemoryConfig(true)
    cacheCenter.put(providerConfig, identifierResult)

    val newIdentifierResult = IdentifierResult(oaid = "oaid")
    cacheCenter.put(providerConfig, newIdentifierResult)

    val result = cacheCenter.get(providerConfig)
    assertEquals(identifierResult, result)
  }
}