package com.phantomvk.identifier.log;

import androidx.annotation.Nullable;

public class Log {

    @Nullable
    private static Logger sLogger;

    public static void setLogger(Logger logger) {
        sLogger = logger;
    }

    public static void v(String tag, String text) {
        log(TraceLevel.VERBOSE, tag, text, null);
    }

    public static void v(String tag, String text, Throwable tr) {
        log(TraceLevel.VERBOSE, tag, text, tr);
    }

    public static void d(String tag, String text) {
        log(TraceLevel.DEBUG, tag, text, null);
    }

    public static void d(String tag, String text, Throwable tr) {
        log(TraceLevel.DEBUG, tag, text, tr);
    }

    public static void i(String tag, String text) {
        log(TraceLevel.INFO, tag, text, null);
    }

    public static void i(String tag, String text, Throwable tr) {
        log(TraceLevel.INFO, tag, text, tr);
    }

    public static void w(String tag, String text) {
        log(TraceLevel.WARN, tag, text, null);
    }

    public static void w(String tag, String text, Throwable tr) {
        log(TraceLevel.WARN, tag, text, tr);
    }

    public static void e(String tag, String text) {
        log(TraceLevel.ERROR, tag, text, null);
    }

    public static void e(String tag, String text, Throwable tr) {
        log(TraceLevel.ERROR, tag, text, tr);
    }

    private static void log(TraceLevel level, String tag, String msg, Throwable tr) {
        Logger curLogger = sLogger;
        if (curLogger == null) return;
        try {
            curLogger.log(level, tag, msg, tr);
        } catch (Throwable ignore) {
        }
    }
}
