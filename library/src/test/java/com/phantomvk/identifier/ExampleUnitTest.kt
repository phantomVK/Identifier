package com.phantomvk.identifier

import com.phantomvk.identifier.util.HashCalculator
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val result = HashCalculator.hash("MD5", "MD5".toByteArray())
        assertEquals(result, "7f138a09169b250e9dcb378140907378")
    }
}