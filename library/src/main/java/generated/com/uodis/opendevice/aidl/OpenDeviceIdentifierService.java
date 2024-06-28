/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package generated.com.uodis.opendevice.aidl;
public interface OpenDeviceIdentifierService extends android.os.IInterface
{
  /** Default implementation for OpenDeviceIdentifierService. */
  public static class Default implements generated.com.uodis.opendevice.aidl.OpenDeviceIdentifierService
  {
    @Override public java.lang.String getOaid() throws android.os.RemoteException
    {
      return null;
    }
    @Override public boolean isOaidTrackLimited() throws android.os.RemoteException
    {
      return false;
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements generated.com.uodis.opendevice.aidl.OpenDeviceIdentifierService
  {
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an generated.com.uodis.opendevice.aidl.OpenDeviceIdentifierService interface,
     * generating a proxy if needed.
     */
    public static generated.com.uodis.opendevice.aidl.OpenDeviceIdentifierService asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof generated.com.uodis.opendevice.aidl.OpenDeviceIdentifierService))) {
        return ((generated.com.uodis.opendevice.aidl.OpenDeviceIdentifierService)iin);
      }
      return new generated.com.uodis.opendevice.aidl.OpenDeviceIdentifierService.Stub.Proxy(obj);
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
        case TRANSACTION_getOaid:
        {
          java.lang.String _result = this.getOaid();
          reply.writeNoException();
          reply.writeString(_result);
          break;
        }
        case TRANSACTION_isOaidTrackLimited:
        {
          boolean _result = this.isOaidTrackLimited();
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
    private static class Proxy implements generated.com.uodis.opendevice.aidl.OpenDeviceIdentifierService
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
      @Override public java.lang.String getOaid() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.lang.String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getOaid, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readString();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public boolean isOaidTrackLimited() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_isOaidTrackLimited, _data, _reply, 0);
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
    static final int TRANSACTION_getOaid = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    static final int TRANSACTION_isOaidTrackLimited = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
  }
  public static final java.lang.String DESCRIPTOR = "com.uodis.opendevice.aidl.OpenDeviceIdentifierService";
  public java.lang.String getOaid() throws android.os.RemoteException;
  public boolean isOaidTrackLimited() throws android.os.RemoteException;
}
