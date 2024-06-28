/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package generated.com.bun.lib;
public interface MsaIdInterface extends android.os.IInterface
{
  /** Default implementation for MsaIdInterface. */
  public static class Default implements generated.com.bun.lib.MsaIdInterface
  {
    @Override public java.lang.String getOAID() throws android.os.RemoteException
    {
      return null;
    }
    @Override public java.lang.String getAAID() throws android.os.RemoteException
    {
      return null;
    }
    @Override public java.lang.String getVAID() throws android.os.RemoteException
    {
      return null;
    }
    @Override public boolean isDataArrived() throws android.os.RemoteException
    {
      return false;
    }
    @Override public boolean isSupported() throws android.os.RemoteException
    {
      return false;
    }
    @Override public void shutDown() throws android.os.RemoteException
    {
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements generated.com.bun.lib.MsaIdInterface
  {
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an generated.com.bun.lib.MsaIdInterface interface,
     * generating a proxy if needed.
     */
    public static generated.com.bun.lib.MsaIdInterface asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof generated.com.bun.lib.MsaIdInterface))) {
        return ((generated.com.bun.lib.MsaIdInterface)iin);
      }
      return new generated.com.bun.lib.MsaIdInterface.Stub.Proxy(obj);
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
        case TRANSACTION_getAAID:
        {
          java.lang.String _result = this.getAAID();
          reply.writeNoException();
          reply.writeString(_result);
          break;
        }
        case TRANSACTION_getVAID:
        {
          java.lang.String _result = this.getVAID();
          reply.writeNoException();
          reply.writeString(_result);
          break;
        }
        case TRANSACTION_isDataArrived:
        {
          boolean _result = this.isDataArrived();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          break;
        }
        case TRANSACTION_isSupported:
        {
          boolean _result = this.isSupported();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          break;
        }
        case TRANSACTION_shutDown:
        {
          this.shutDown();
          reply.writeNoException();
          break;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
      return true;
    }
    private static class Proxy implements generated.com.bun.lib.MsaIdInterface
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
      @Override public java.lang.String getAAID() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.lang.String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
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
      @Override public java.lang.String getVAID() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.lang.String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
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
      @Override public boolean isDataArrived() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_isDataArrived, _data, _reply, 0);
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public boolean isSupported() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_isSupported, _data, _reply, 0);
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public void shutDown() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_shutDown, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
    }
    static final int TRANSACTION_getOAID = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    static final int TRANSACTION_getAAID = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    static final int TRANSACTION_getVAID = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
    static final int TRANSACTION_isDataArrived = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
    static final int TRANSACTION_isSupported = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
    static final int TRANSACTION_shutDown = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
  }
  public static final java.lang.String DESCRIPTOR = "com.bun.lib.MsaIdInterface";
  public java.lang.String getOAID() throws android.os.RemoteException;
  public java.lang.String getAAID() throws android.os.RemoteException;
  public java.lang.String getVAID() throws android.os.RemoteException;
  public boolean isDataArrived() throws android.os.RemoteException;
  public boolean isSupported() throws android.os.RemoteException;
  public void shutDown() throws android.os.RemoteException;
}
