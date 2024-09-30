package com.bun.miitmdid.core;

import android.content.Context;

import com.bun.miitmdid.InfoCode;
import com.bun.miitmdid.interfaces.IIdentifierListener;
import com.bun.miitmdid.interfaces.IPermissionCallbackListener;

import java.util.Collections;

public final class MdidSdkHelper {
  
  public static String SDK_VERSION = "1";
  public static int SDK_VERSION_CODE = 1;
  public static MdidSdkInterface sSdkInterface = null;

  public static boolean setGlobalTimeout(long timeoutMillis) {
    MdidSdkInterface i = sSdkInterface;
    if (i == null) {
      return false;
    } else {
      return i.setGlobalTimeout(timeoutMillis);
    }
  }

  public static boolean InitCert(Context context, String cert) {
    MdidSdkInterface i = sSdkInterface;
    if (i == null) {
      return false;
    } else {
      return i.InitCert(context, cert);
    }
  }
  
  public static int InitSdk(Context context, boolean isGetOAID, IIdentifierListener listener) {
    return InitSdk(context, isGetOAID, false, false, false, listener);
  }

  public static int InitSdk(Context context, boolean enableLog, boolean isGetOAID, boolean isGetVAID, boolean isGetAAID, IIdentifierListener listener) {
    MdidSdkInterface i = sSdkInterface;
    if (i == null) {
      return InfoCode.INIT_ERROR_SDK_CALL_ERROR;
    } else {
      return i.InitSdk(context, enableLog, isGetOAID, isGetVAID, isGetAAID, listener);
    }
  }

  public static void requestOAIDPermission(Context context, IPermissionCallbackListener listener) {
    MdidSdkInterface i = sSdkInterface;
    if (i == null) {
      listener.onAskAgain(Collections.emptyList());
    } else {
      i.requestOAIDPermission(context, listener);
    }
  }
}
