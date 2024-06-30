package com.phantomvk.identifier.impl

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.phantomvk.identifier.impl.Constants.EXCEPTION_THROWN
import com.phantomvk.identifier.interfaces.BinderCallback
import com.phantomvk.identifier.interfaces.OnResultListener
import com.phantomvk.identifier.model.CallBinderResult

object ServiceManager {
  fun bindService(
    context: Context,
    intent: Intent,
    callback: OnResultListener,
    binderCallback: BinderCallback
  ) {
    val conn = object : ServiceConnection {
      override fun onServiceConnected(name: ComponentName, service: IBinder) {
        try {
          when (val result = binderCallback.call(service)) {
            is CallBinderResult.Success -> callback.onSuccess(result.id)
            is CallBinderResult.Failed -> callback.onError(result.msg)
          }
        } catch (t: Throwable) {
          callback.onError(EXCEPTION_THROWN, t)
        } finally {
          unbindService(context, this)
        }
      }

      override fun onServiceDisconnected(name: ComponentName) {
        callback.onError("Service is disconnected.")
      }

      override fun onBindingDied(name: ComponentName) {
        callback.onError("Service is on binding died.")
        unbindService(context, this)
      }

      override fun onNullBinding(name: ComponentName) {
        callback.onError("Service is on null binding.")
        unbindService(context, this)
      }
    }

    try {
      val success = context.bindService(intent, conn, Context.BIND_AUTO_CREATE)
      if (!success) {
        callback.onError("Bind service return false.", null)
      }
    } catch (t: Throwable) {
      callback.onError("Bind service error.", t)
    }
  }

  private fun unbindService(context: Context, conn: ServiceConnection) {
    try {
      context.unbindService(conn)
    } catch (ignore: Throwable) {
    }
  }
}