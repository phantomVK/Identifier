package com.phantomvk.identifier.impl

import android.os.Build
import com.phantomvk.identifier.util.sysProperty

object Devices {
  fun isAsus(): Boolean {
    return Build.MANUFACTURER.equals("ASUS", true)
        && Build.BRAND.equals("ASUS", true)
  }

  fun isBlackShark(): Boolean {
    return Build.MANUFACTURER.equals("BLACKSHARK", true)
        && Build.BRAND.equals("BLACKSHARK", true)
  }

  fun isCoosea(): Boolean {
    return sysProperty("ro.odm.manufacturer", "").equals("PRIZE", true)
  }

  fun isFreeme(): Boolean {
    return !sysProperty("ro.build.freeme.label", "").isNullOrBlank()
  }

  fun isEmui(): Boolean {
    return !sysProperty("ro.build.version.emui", "").isNullOrBlank()
  }

  fun isFlyme(): Boolean {
    return Build.DISPLAY.contains("FLYME", true)
  }

  fun isHonor(): Boolean {
    return Build.MANUFACTURER.equals("HONOR", true)
        && Build.BRAND.equals("HONOR", true)
  }

  fun isHuawei(): Boolean {
    return Build.MANUFACTURER.equals("HUAWEI", true)
        && (Build.BRAND.equals("HUAWEI", true)
        || Build.BRAND.equals("HONOR", true))
  }

  fun isMeizu(): Boolean {
    return Build.MANUFACTURER.equals("MEIZU", true)
        && Build.BRAND.equals("MEIZU", true)
  }

  fun isMiui(): Boolean {
    return !sysProperty("ro.miui.ui.version.name", "").isNullOrBlank()
  }

  fun isLenovo(): Boolean {
    return Build.MANUFACTURER.equals("LENOVO", true)
        && (Build.BRAND.equals("LENOVO", true)
        || Build.BRAND.equals("ZUK", true))
  }

  fun isMotorola(): Boolean {
    return Build.MANUFACTURER.equals("MOTOROLA", true)
        && Build.BRAND.equals("MOTOROLA", true)
  }

  fun isNubia(): Boolean {
    return Build.MANUFACTURER.equals("NUBIA", true)
        && Build.BRAND.equals("NUBIA", true)
  }

  fun isOppo(): Boolean {
    return Build.MANUFACTURER.equals("OPPO", true)
        && Build.BRAND.equals("OPPO", true)
  }

  fun isRealme(): Boolean {
    return Build.MANUFACTURER.equals("realme", true)
        && Build.BRAND.equals("realme", true)
  }

  fun isOppoRom(): Boolean {
    return !sysProperty("ro.build.version.opporom", "").isNullOrBlank()
  }

  fun isOnePlus(): Boolean {
    return Build.MANUFACTURER.equals("ONEPLUS", true)
        && Build.BRAND.equals("ONEPLUS", true)
  }

  fun isQiku(): Boolean {
    return sysProperty("ro.build.uiversion", "")?.contains("360UI", true) == true
  }

  fun isSamsung(): Boolean {
    return Build.MANUFACTURER.equals("SAMSUNG", true)
        && Build.BRAND.equals("SAMSUNG", true)
  }

  fun isXiaomi(): Boolean {
    return Build.MANUFACTURER.equals("XIAOMI", true)
        || (Build.BRAND.equals("XIAOMI", true)
        && Build.BRAND.equals("REDMI", true))
  }

  fun isVivo(): Boolean {
    return (Build.MANUFACTURER.equals("VIVO", true)
        && Build.BRAND.equals("VIVO", true))
        || !sysProperty("ro.vivo.os.version", "").isNullOrBlank()
  }

  fun isSsui(): Boolean {
    return !sysProperty("ro.ssui.product", "").isNullOrBlank()
  }

  fun isZTE(): Boolean {
    return Build.MANUFACTURER.equals("ZTE", true)
        && Build.BRAND.equals("ZTE", true)
  }
}