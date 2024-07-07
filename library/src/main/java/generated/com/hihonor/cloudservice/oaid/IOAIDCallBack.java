/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package generated.com.hihonor.cloudservice.oaid;
public interface IOAIDCallBack extends android.os.IInterface
{
  /** Default implementation for IOAIDCallBack. */
  public static class Default implements generated.com.hihonor.cloudservice.oaid.IOAIDCallBack
  {
    @Override public void a(int i, long j, boolean z, float f, double d, java.lang.String str) throws android.os.RemoteException
    {
    }
    @Override public void onResult(int i, android.os.Bundle bundle) throws android.os.RemoteException
    {
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements generated.com.hihonor.cloudservice.oaid.IOAIDCallBack
  {
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an generated.com.hihonor.cloudservice.oaid.IOAIDCallBack interface,
     * generating a proxy if needed.
     */
    public static generated.com.hihonor.cloudservice.oaid.IOAIDCallBack asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof generated.com.hihonor.cloudservice.oaid.IOAIDCallBack))) {
        return ((generated.com.hihonor.cloudservice.oaid.IOAIDCallBack)iin);
      }
      return new generated.com.hihonor.cloudservice.oaid.IOAIDCallBack.Stub.Proxy(obj);
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
        case TRANSACTION_onResult:
        {
          int _arg0;
          _arg0 = data.readInt();
          android.os.Bundle _arg1;
          _arg1 = _Parcel.readTypedObject(data, android.os.Bundle.CREATOR);
          this.onResult(_arg0, _arg1);
          reply.writeNoException();
          _Parcel.writeTypedObject(reply, _arg1, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          break;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
      return true;
    }
    private static class Proxy implements generated.com.hihonor.cloudservice.oaid.IOAIDCallBack
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
      @Override public void onResult(int i, android.os.Bundle bundle) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(i);
          _Parcel.writeTypedObject(_data, bundle, 0);
          boolean _status = mRemote.transact(Stub.TRANSACTION_onResult, _data, _reply, 0);
          _reply.readException();
          if ((0!=_reply.readInt())) {
            bundle.readFromParcel(_reply);
          }
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
    }
    static final int TRANSACTION_a = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    static final int TRANSACTION_onResult = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
  }
  public static final java.lang.String DESCRIPTOR = "com.hihonor.cloudservice.oaid.IOAIDCallBack";
  public void a(int i, long j, boolean z, float f, double d, java.lang.String str) throws android.os.RemoteException;
  public void onResult(int i, android.os.Bundle bundle) throws android.os.RemoteException;
  /** @hide */
  static class _Parcel {
    static private <T> T readTypedObject(
        android.os.Parcel parcel,
        android.os.Parcelable.Creator<T> c) {
      if (parcel.readInt() != 0) {
          return c.createFromParcel(parcel);
      } else {
          return null;
      }
    }
    static private <T extends android.os.Parcelable> void writeTypedObject(
        android.os.Parcel parcel, T value, int parcelableFlags) {
      if (value != null) {
        parcel.writeInt(1);
        value.writeToParcel(parcel, parcelableFlags);
      } else {
        parcel.writeInt(0);
      }
    }
  }
}
