package com.bun.miitmdid.interfaces;

import android.app.Activity;

public interface IdSupplier {
  boolean isSupported();
  boolean isLimited();
  String getOAID();
  String getVAID();
  String getAAID();
  boolean isSupportRequestOAIDPermission();
  void requestOAIDPermission(Activity activity, int value);
}
