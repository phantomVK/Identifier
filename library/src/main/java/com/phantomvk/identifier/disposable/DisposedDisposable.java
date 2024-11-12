package com.phantomvk.identifier.disposable;

public class DisposedDisposable implements Disposable {
  @Override
  public void dispose() {
  }
  
  @Override
  public boolean isDisposed() {
    return true;
  }
}