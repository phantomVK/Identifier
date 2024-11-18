/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package generated.com.hihonor.cloudservice.oaid;

import android.os.Parcel;

public interface IOAIDCallBack extends android.os.IInterface
{
  /** Local-side IPC implementation stub class. */
  abstract class Stub extends android.os.Binder implements generated.com.hihonor.cloudservice.oaid.IOAIDCallBack
  {
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
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
      if (code == INTERFACE_TRANSACTION) {
        reply.writeString(descriptor);
        return true;
      }
      switch (code)
      {
        case TRANSACTION_a:
        {
          int _arg0 = data.readInt();
          long _arg1 = data.readLong();
          boolean _arg2 = (0 != data.readInt());
          float _arg3 = data.readFloat();
          double _arg4 = data.readDouble();
          java.lang.String _arg5 = data.readString();
          this.a(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_onResult:
        {
          int _arg0 = data.readInt();
          android.os.Bundle _arg1 = _Parcel.readTypedObject(data);
          this.onResult(_arg0, _arg1);
          reply.writeNoException();
          _Parcel.writeTypedObject(reply, _arg1);
          break;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
      return true;
    }
    static final int TRANSACTION_a = android.os.IBinder.FIRST_CALL_TRANSACTION;
    static final int TRANSACTION_onResult = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
  }
  java.lang.String DESCRIPTOR = "com.hihonor.cloudservice.oaid.IOAIDCallBack";
  void a(int i, long j, boolean z, float f, double d, java.lang.String str) throws android.os.RemoteException;
  void onResult(int i, android.os.Bundle bundle) throws android.os.RemoteException;
  /** @hide */
  class _Parcel {
    static private <T> T readTypedObject(Parcel parcel) {
      if (parcel.readInt() != 0) {
          return ((android.os.Parcelable.Creator<T>) android.os.Bundle.CREATOR).createFromParcel(parcel);
      } else {
          return null;
      }
    }
    static private <T extends android.os.Parcelable> void writeTypedObject(Parcel parcel, T value) {
      if (value != null) {
        parcel.writeInt(1);
        value.writeToParcel(parcel, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
      } else {
        parcel.writeInt(0);
      }
    }
  }
}
