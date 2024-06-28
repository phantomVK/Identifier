/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package generated.com.oplus.stdid;
public interface IStdID extends android.os.IInterface
{
  /** Default implementation for IStdID. */
  public static class Default implements generated.com.oplus.stdid.IStdID
  {
    @Override public java.lang.String getSerID(java.lang.String pkgName, java.lang.String sign, java.lang.String type) throws android.os.RemoteException
    {
      return null;
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements generated.com.oplus.stdid.IStdID
  {
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an generated.com.oplus.stdid.IStdID interface,
     * generating a proxy if needed.
     */
    public static generated.com.oplus.stdid.IStdID asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof generated.com.oplus.stdid.IStdID))) {
        return ((generated.com.oplus.stdid.IStdID)iin);
      }
      return new generated.com.oplus.stdid.IStdID.Stub.Proxy(obj);
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
        case TRANSACTION_getSerID:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.lang.String _arg1;
          _arg1 = data.readString();
          java.lang.String _arg2;
          _arg2 = data.readString();
          java.lang.String _result = this.getSerID(_arg0, _arg1, _arg2);
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
    private static class Proxy implements generated.com.oplus.stdid.IStdID
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
      @Override public java.lang.String getSerID(java.lang.String pkgName, java.lang.String sign, java.lang.String type) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.lang.String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(pkgName);
          _data.writeString(sign);
          _data.writeString(type);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getSerID, _data, _reply, 0);
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
    static final int TRANSACTION_getSerID = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
  }
  public static final java.lang.String DESCRIPTOR = "com.oplus.stdid.IStdID";
  public java.lang.String getSerID(java.lang.String pkgName, java.lang.String sign, java.lang.String type) throws android.os.RemoteException;
}
