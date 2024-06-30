package com.phantomvk.identifier.app;

import android.text.TextUtils;
import android.widget.Toast;

import com.phantomvk.identifier.log.Logger;
import com.phantomvk.identifier.log.TraceLevel;
import com.phantomvk.identifier.util.MainThreadKt;

public class LoggerImpl implements Logger {
    @Override
    public void log(TraceLevel level, String tag, String msg, Throwable tr) {
        MainThreadKt.runOnMainThread(0, () -> Toast.makeText(Application.sApplication, msg, Toast.LENGTH_LONG).show());
        switch (level) {
            case VERBOSE:
                if (tr == null) {
                    android.util.Log.v(tag, msg);
                } else {
                    android.util.Log.v(tag, msg, tr);
                }
                break;

            case DEBUG:
                if (tr == null) {
                    android.util.Log.d(tag, msg);
                } else {
                    android.util.Log.d(tag, msg, tr);
                }
                break;

            case INFO:
                if (tr == null) {
                    android.util.Log.i(tag, msg);
                } else {
                    android.util.Log.i(tag, msg, tr);
                }
                break;

            case WARN:
                if (tr == null) {
                    android.util.Log.w(tag, msg);
                } else if (android.text.TextUtils.isEmpty(msg)) {
                    android.util.Log.w(tag, tr);
                } else {
                    android.util.Log.w(tag, msg, tr);
                }
                break;

            case ERROR:
                if (tr == null) {
                    android.util.Log.e(tag, msg);
                } else {
                    android.util.Log.e(tag, msg, tr);
                }
                break;

            case ASSERT:
                if (tr == null) {
                    android.util.Log.wtf(tag, msg);
                } else if (TextUtils.isEmpty(msg)) {
                    android.util.Log.wtf(tag, tr);
                } else {
                    android.util.Log.wtf(tag, msg, tr);
                }
                break;

            default:
                break;
        }
    }
}
