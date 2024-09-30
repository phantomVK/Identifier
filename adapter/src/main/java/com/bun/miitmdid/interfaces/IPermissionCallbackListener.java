package com.bun.miitmdid.interfaces;

import java.util.List;

public interface IPermissionCallbackListener {
  void onGranted(String[] var1);
  void onDenied(List<String> var1);
  void onAskAgain(List<String> var1);
}
