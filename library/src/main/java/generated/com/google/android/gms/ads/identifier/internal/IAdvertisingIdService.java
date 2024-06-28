/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package generated.com.google.android.gms.ads.identifier.internal;
public interface IAdvertisingIdService extends android.os.IInterface
{
  /** Default implementation for IAdvertisingIdService. */
  public static class Default implements generated.com.google.android.gms.ads.identifier.internal.IAdvertisingIdService
  {
    @Override public java.lang.String getId() throws android.os.RemoteException
    {
      return null;
    }
    @Override public boolean isLimitAdTrackingEnabled(boolean boo) throws android.os.RemoteException
    {
      return false;
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements generated.com.google.android.gms.ads.identifier.internal.IAdvertisingIdService
  {
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an generated.com.google.android.gms.ads.identifier.internal.IAdvertisingIdService interface,
     * generating a proxy if needed.
     */
    public static generated.com.google.android.gms.ads.identifier.internal.IAdvertisingIdService asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof generated.com.google.android.gms.ads.identifier.internal.IAdvertisingIdService))) {
        return ((generated.com.google.android.gms.ads.identifier.internal.IAdvertisingIdService)iin);
      }
      return new generated.com.google.android.gms.ads.identifier.internal.IAdvertisingIdService.Stub.Proxy(obj);
    }
    @Override public android.os.IBinder asBinder()
    {
      return this;
    }
    @Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
    {
      java.lang.String descriptor = DESCRIPTOR;
      if (code >= android.os.IBinder.FIRST_CALL_TRANSACTION && code <= android.os.IBinder.LAST_CALL_TRANSACTION) {
        data.enforceInterface(descriptor);
      }
      switch (code)
      {
        case INTERFACE_TRANSACTION:
        {
          reply.writeString(descriptor);
          return true;
        }
      }
      switch (code)
      {
        case TRANSACTION_getId:
        {
          java.lang.String _result = this.getId();
          reply.writeNoException();
          reply.writeString(_result);
          break;
        }
        case TRANSACTION_isLimitAdTrackingEnabled:
        {
          boolean _arg0;
          _arg0 = (0!=data.readInt());
          boolean _result = this.isLimitAdTrackingEnabled(_arg0);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          break;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
      return true;
    }
    private static class Proxy implements generated.com.google.android.gms.ads.identifier.internal.IAdvertisingIdService
    {
      private android.os.IBinder mRemote;
      Proxy(android.os.IBinder remote)
      {
        mRemote = remote;
      }
      @Override public android.os.IBinder asBinder()
      {
        return mRemote;
      }
      public java.lang.String getInterfaceDescriptor()
      {
        return DESCRIPTOR;
      }
      @Override public java.lang.String getId() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.lang.String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getId, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readString();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public boolean isLimitAdTrackingEnabled(boolean boo) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(((boo)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_isLimitAdTrackingEnabled, _data, _reply, 0);
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
    }
    static final int TRANSACTION_getId = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    static final int TRANSACTION_isLimitAdTrackingEnabled = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
  }
  public static final java.lang.String DESCRIPTOR = "com.google.android.gms.ads.identifier.internal.IAdvertisingIdService";
  public java.lang.String getId() throws android.os.RemoteException;
  public boolean isLimitAdTrackingEnabled(boolean boo) throws android.os.RemoteException;
}
