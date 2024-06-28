package com.phantomvk.identifier.log;

public enum TraceLevel {
    VERBOSE(2, "V"),
    DEBUG(3, "D"),
    INFO(4, "I"),
    WARN(5, "W"),
    ERROR(6, "E"),
    ASSERT(7, "A");

    public final String levelString;
    public final int level;

    TraceLevel(int level, String levelString) {
        this.level = level;
        this.levelString = levelString;
    }
}
