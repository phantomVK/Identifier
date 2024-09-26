package com.phantomvk.identifier.impl;

import com.phantomvk.identifier.interfaces.Disposable;

public class DisposedDisposable implements Disposable {
  @Override
  public void dispose() {
  }
  
  @Override
  public boolean isDisposed() {
    return true;
  }
}