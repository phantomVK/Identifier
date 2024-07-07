/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package generated.com.hihonor.cloudservice.oaid;
public interface IOAIDService extends android.os.IInterface
{
  /** Default implementation for IOAIDService. */
  public static class Default implements generated.com.hihonor.cloudservice.oaid.IOAIDService
  {
    @Override public void a(int i, long j, boolean z, float f, double d, java.lang.String str) throws android.os.RemoteException
    {
    }
    @Override public void getOaid(generated.com.hihonor.cloudservice.oaid.IOAIDCallBack callback) throws android.os.RemoteException
    {
    }
    @Override public void isLimited(generated.com.hihonor.cloudservice.oaid.IOAIDCallBack callback) throws android.os.RemoteException
    {
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements generated.com.hihonor.cloudservice.oaid.IOAIDService
  {
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an generated.com.hihonor.cloudservice.oaid.IOAIDService interface,
     * generating a proxy if needed.
     */
    public static generated.com.hihonor.cloudservice.oaid.IOAIDService asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof generated.com.hihonor.cloudservice.oaid.IOAIDService))) {
        return ((generated.com.hihonor.cloudservice.oaid.IOAIDService)iin);
      }
      return new generated.com.hihonor.cloudservice.oaid.IOAIDService.Stub.Proxy(obj);
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
        case TRANSACTION_a:
        {
          int _arg0;
          _arg0 = data.readInt();
          long _arg1;
          _arg1 = data.readLong();
          boolean _arg2;
          _arg2 = (0!=data.readInt());
          float _arg3;
          _arg3 = data.readFloat();
          double _arg4;
          _arg4 = data.readDouble();
          java.lang.String _arg5;
          _arg5 = data.readString();
          this.a(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_getOaid:
        {
          generated.com.hihonor.cloudservice.oaid.IOAIDCallBack _arg0;
          _arg0 = generated.com.hihonor.cloudservice.oaid.IOAIDCallBack.Stub.asInterface(data.readStrongBinder());
          this.getOaid(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_isLimited:
        {
          generated.com.hihonor.cloudservice.oaid.IOAIDCallBack _arg0;
          _arg0 = generated.com.hihonor.cloudservice.oaid.IOAIDCallBack.Stub.asInterface(data.readStrongBinder());
          this.isLimited(_arg0);
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
    private static class Proxy implements generated.com.hihonor.cloudservice.oaid.IOAIDService
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
      @Override public void a(int i, long j, boolean z, float f, double d, java.lang.String str) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(i);
          _data.writeLong(j);
          _data.writeInt(((z)?(1):(0)));
          _data.writeFloat(f);
          _data.writeDouble(d);
          _data.writeString(str);
          boolean _status = mRemote.transact(Stub.TRANSACTION_a, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void getOaid(generated.com.hihonor.cloudservice.oaid.IOAIDCallBack callback) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStrongInterface(callback);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getOaid, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void isLimited(generated.com.hihonor.cloudservice.oaid.IOAIDCallBack callback) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStrongInterface(callback);
          boolean _status = mRemote.transact(Stub.TRANSACTION_isLimited, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
    }
    static final int TRANSACTION_a = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    static final int TRANSACTION_getOaid = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    static final int TRANSACTION_isLimited = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
  }
  public static final java.lang.String DESCRIPTOR = "com.hihonor.cloudservice.oaid.IOAIDService";
  public void a(int i, long j, boolean z, float f, double d, java.lang.String str) throws android.os.RemoteException;
  public void getOaid(generated.com.hihonor.cloudservice.oaid.IOAIDCallBack callback) throws android.os.RemoteException;
  public void isLimited(generated.com.hihonor.cloudservice.oaid.IOAIDCallBack callback) throws android.os.RemoteException;
}
