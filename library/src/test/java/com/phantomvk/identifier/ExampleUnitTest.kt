package com.phantomvk.identifier

import android.app.Application
import com.phantomvk.identifier.model.IdConfig
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
        config.idConfig = IdConfig()
        assert(config.getCacheKey() == "0")

        config.idConfig = IdConfig(true)
        assert(config.getCacheKey() == "1")

        config.idConfig = IdConfig(true, true)
        assert(config.getCacheKey() == "3")

        config.idConfig = IdConfig(true, true, true)
        assert(config.getCacheKey() == "7")
    }
}