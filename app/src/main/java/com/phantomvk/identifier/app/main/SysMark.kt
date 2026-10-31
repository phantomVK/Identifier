package com.phantomvk.identifier.app.main

object SysMark {
  init {
    System.loadLibrary("sys_mark")
  }

  external fun getBootMark(): String
  external fun getUpdateMark(): String
}
