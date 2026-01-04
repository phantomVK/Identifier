package com.phantomvk.identifier.app.main


object SysMarkJni {
  init { System.loadLibrary("sys_mark") }
  external fun getUpdateMark(): String
  external fun getBootMark(): String
}