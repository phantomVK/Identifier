/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package generated.com.samsung.android.deviceidservice;
public interface IDeviceIdService extends android.os.IInterface
{
  /** Default implementation for IDeviceIdService. */
  public static class Default implements generated.com.samsung.android.deviceidservice.IDeviceIdService
  {
    @Override public java.lang.String getOAID() throws android.os.RemoteException
    {
      return null;
    }
    @Override public java.lang.String getVAID(java.lang.String str) throws android.os.RemoteException
    {
      return null;
    }
    @Override public java.lang.String getAAID(java.lang.String str) throws android.os.RemoteException
    {
      return null;
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements generated.com.samsung.android.deviceidservice.IDeviceIdService
  {
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an generated.com.samsung.android.deviceidservice.IDeviceIdService interface,
     * generating a proxy if needed.
     */
    public static generated.com.samsung.android.deviceidservice.IDeviceIdService asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof generated.com.samsung.android.deviceidservice.IDeviceIdService))) {
        return ((generated.com.samsung.android.deviceidservice.IDeviceIdService)iin);
      }
      return new generated.com.samsung.android.deviceidservice.IDeviceIdService.Stub.Proxy(obj);
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
        case TRANSACTION_getOAID:
        {
          java.lang.String _result = this.getOAID();
          reply.writeNoException();
          reply.writeString(_result);
          break;
        }
        case TRANSACTION_getVAID:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.lang.String _result = this.getVAID(_arg0);
          reply.writeNoException();
          reply.writeString(_result);
          break;
        }
        case TRANSACTION_getAAID:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.lang.String _result = this.getAAID(_arg0);
          reply.writeNoException();
          reply.writeString(_result);
          break;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
      return true;
    }
    private static class Proxy implements generated.com.samsung.android.deviceidservice.IDeviceIdService
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
      @Override public java.lang.String getOAID() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.lang.String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getOAID, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readString();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public java.lang.String getVAID(java.lang.String str) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.lang.String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(str);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getVAID, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readString();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public java.lang.String getAAID(java.lang.String str) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.lang.String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(str);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getAAID, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readString();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
    }
    static final int TRANSACTION_getOAID = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    static final int TRANSACTION_getVAID = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    static final int TRANSACTION_getAAID = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
  }
  public static final java.lang.String DESCRIPTOR = "com.samsung.android.deviceidservice.IDeviceIdService";
  public java.lang.String getOAID() throws android.os.RemoteException;
  public java.lang.String getVAID(java.lang.String str) throws android.os.RemoteException;
  public java.lang.String getAAID(java.lang.String str) throws android.os.RemoteException;
}
