/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package generated.com.qiku.id;
// Qiku: bindService
public interface IOAIDInterface extends android.os.IInterface
{
  /** Default implementation for IOAIDInterface. */
  public static class Default implements IOAIDInterface
  {
    @Override public int isSupported() throws android.os.RemoteException
    {
      return 0;
    }
    @Override public String getUDID() throws android.os.RemoteException
    {
      return null;
    }
    @Override public String getOAID() throws android.os.RemoteException
    {
      return null;
    }
    @Override public String getVAID() throws android.os.RemoteException
    {
      return null;
    }
    @Override public String getAAID() throws android.os.RemoteException
    {
      return null;
    }
    @Override public void shutdown() throws android.os.RemoteException
    {
    }
    @Override public void resetOAID() throws android.os.RemoteException
    {
    }
    @Override public boolean isLimited() throws android.os.RemoteException
    {
      return false;
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements IOAIDInterface
  {
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an generated.com.qiku.id.IOAIDInterface interface,
     * generating a proxy if needed.
     */
    public static IOAIDInterface asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof IOAIDInterface))) {
        return ((IOAIDInterface)iin);
      }
      return new Proxy(obj);
    }
    @Override public android.os.IBinder asBinder()
    {
      return this;
    }
    @Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
    {
      String descriptor = DESCRIPTOR;
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
        case TRANSACTION_isSupported:
        {
          int _result = this.isSupported();
          reply.writeNoException();
          reply.writeInt(_result);
          break;
        }
        case TRANSACTION_getUDID:
        {
          String _result = this.getUDID();
          reply.writeNoException();
          reply.writeString(_result);
          break;
        }
        case TRANSACTION_getOAID:
        {
          String _result = this.getOAID();
          reply.writeNoException();
          reply.writeString(_result);
          break;
        }
        case TRANSACTION_getVAID:
        {
          String _result = this.getVAID();
          reply.writeNoException();
          reply.writeString(_result);
          break;
        }
        case TRANSACTION_getAAID:
        {
          String _result = this.getAAID();
          reply.writeNoException();
          reply.writeString(_result);
          break;
        }
        case TRANSACTION_shutdown:
        {
          this.shutdown();
          reply.writeNoException();
          break;
        }
        case TRANSACTION_resetOAID:
        {
          this.resetOAID();
          reply.writeNoException();
          break;
        }
        case TRANSACTION_isLimited:
        {
          boolean _result = this.isLimited();
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
    private static class Proxy implements IOAIDInterface
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
      public String getInterfaceDescriptor()
      {
        return DESCRIPTOR;
      }
      @Override public int isSupported() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        int _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_isSupported, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readInt();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public String getUDID() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getUDID, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readString();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public String getOAID() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        String _result;
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
      @Override public String getVAID() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        String _result;
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
      @Override public String getAAID() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        String _result;
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
      @Override public void shutdown() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_shutdown, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void resetOAID() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_resetOAID, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public boolean isLimited() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_isLimited, _data, _reply, 0);
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
    static final int TRANSACTION_isSupported = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    static final int TRANSACTION_getUDID = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    static final int TRANSACTION_getOAID = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
    static final int TRANSACTION_getVAID = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
    static final int TRANSACTION_getAAID = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
    static final int TRANSACTION_shutdown = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
    static final int TRANSACTION_resetOAID = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
    static final int TRANSACTION_isLimited = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
  }
  public static final String DESCRIPTOR = "com.qiku.id.IOAIDInterface";
  public int isSupported() throws android.os.RemoteException;
  public String getUDID() throws android.os.RemoteException;
  public String getOAID() throws android.os.RemoteException;
  public String getVAID() throws android.os.RemoteException;
  public String getAAID() throws android.os.RemoteException;
  public void shutdown() throws android.os.RemoteException;
  public void resetOAID() throws android.os.RemoteException;
  public boolean isLimited() throws android.os.RemoteException;
}
