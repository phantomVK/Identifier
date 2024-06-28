package com.phantomvk.identifier.interfaces

import android.os.IBinder
import com.phantomvk.identifier.model.CallBinderResult

interface BinderCallback {
  fun call(binder: IBinder): CallBinderResult
}