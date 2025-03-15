package com.phantomvk.identifier

import android.os.Looper
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.phantomvk.identifier.functions.Consumer
import com.phantomvk.identifier.model.IdentifierResult

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
  @Test
  fun useAppContext() {
    // Context of the app under test.
    val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    assertEquals("com.phantomvk.identifier.app", appContext.packageName)
  }

  @Test
  fun assertCallbackUiThread() {
    IdentifierManager.build()
      .enableAsyncCallback(false)
      .subscribe(object : Consumer {
        override fun onSuccess(result: IdentifierResult) {
          assertEquals(Looper.getMainLooper(), Looper.myLooper())
        }

        override fun onError(msg: String, throwable: Throwable?) {
          assertEquals(Looper.getMainLooper(), Looper.myLooper())
        }
      })
  }

  @Test
  fun assertCallbackWorkerThread() {
    IdentifierManager.build()
      .enableAsyncCallback(true)
      .subscribe(object : Consumer {
        override fun onSuccess(result: IdentifierResult) {
          assertNotEquals(Looper.getMainLooper(), Looper.myLooper())
        }

        override fun onError(msg: String, throwable: Throwable?) {
          assertNotEquals(Looper.getMainLooper(), Looper.myLooper())
        }
      })
  }
}