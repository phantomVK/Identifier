package com.phantomvk.identifier.log;

public interface Logger {
    void log(TraceLevel level, String tag, String message, Throwable tr);
}
