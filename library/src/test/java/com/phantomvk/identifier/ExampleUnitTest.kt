package com.phantomvk.identifier

import android.app.Application
import com.phantomvk.identifier.model.ProviderConfig
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun providerConfigCacheKeyTest() {
        val config = ProviderConfig(Application())
        assert(config.getCacheKey() == "0")

        config.queryAaid = true
        assert(config.getCacheKey() == "1")

        config.queryVaid = true
        assert(config.getCacheKey() == "3")

        config.queryGoogleAdsId = true
        assert(config.getCacheKey() == "7")
    }
}