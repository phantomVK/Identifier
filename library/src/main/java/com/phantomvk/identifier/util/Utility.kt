package com.phantomvk.identifier.util

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.os.Build

fun getSignatures(pm: PackageManager, packageName: String): Array<Signature>? {
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
    val flag = PackageManager.GET_SIGNING_CERTIFICATES
    val info = pm.getPackageInfo(packageName, flag)?.signingInfo ?: return null

    return if (info.hasMultipleSigners()) {
      info.apkContentsSigners
    } else {
      info.signingCertificateHistory
    }
  }

  val flag = PackageManager.GET_SIGNATURES
  val signatures = pm.getPackageInfo(packageName, flag)?.signatures
  if (signatures.isNullOrEmpty() || signatures[0] == null) {
    return null
  }

  return signatures
}

@SuppressLint("PrivateApi")
fun sysProperty(key: String, defValue: String): String? {
  return try {
    val clazz = Class.forName("android.os.SystemProperties")
    val method = clazz.getMethod("get", String::class.java, String::class.java)
    method.invoke(clazz, key, defValue) as String
  } catch (t: Throwable) {
    null
  }
}