package com.bun.miitmdid.pojo;


import android.app.Activity;

import com.bun.miitmdid.interfaces.IdSupplier;

public class IdSupplierImpl implements IdSupplier {
  private final String OAID;
  private final String VAID;
  private final String AAID;
  private final boolean isSupported;
  private final boolean isLimited;
  private final boolean isSupportRequestOAIDPermission;
  
  public IdSupplierImpl() {
    OAID = "";
    VAID = "";
    AAID = "";
    isSupported = false;
    isLimited = false;
    isSupportRequestOAIDPermission = false;
  }
  
  public IdSupplierImpl(String oaid, String vaid, String aaid, boolean isSupported, boolean isLimited, boolean isSupportRequestOAIDPermission) {
    this.OAID = oaid;
    this.VAID = vaid;
    this.AAID = aaid;
    this.isSupported = isSupported;
    this.isLimited = isLimited;
    this.isSupportRequestOAIDPermission = isSupportRequestOAIDPermission;
  }

  public String getOAID() {
    return OAID;
  }
  
  public String getVAID() {
    return VAID;
  }
  
  public String getAAID() {
    return AAID;
  }
  
  public boolean isSupportRequestOAIDPermission() {
    return isSupportRequestOAIDPermission;
  }
  
  public void requestOAIDPermission(Activity activity, int value) {
  }
  
  public boolean isSupported() {
    return isSupported;
  }
  
  public boolean isLimited() {
    return isLimited;
  }
}
