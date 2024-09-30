package com.bun.miitmdid.core;

import android.content.Context;

import com.bun.miitmdid.interfaces.IIdentifierListener;
import com.bun.miitmdid.interfaces.IPermissionCallbackListener;

public interface MdidSdkInterface {
  
  boolean setGlobalTimeout(long timeoutMillis);
  
  boolean InitCert(Context context, String cert);
  
  int InitSdk(Context context, boolean isGetOAID, IIdentifierListener listener);
  
  int InitSdk(Context context, boolean enableLog, boolean isGetOAID, boolean isGetVAID, boolean isGetAAID, IIdentifierListener listener);
  
  void requestOAIDPermission(Context context, IPermissionCallbackListener listener);
}
