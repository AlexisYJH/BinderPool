package com.example.binderpool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "BinderPoolActivity";
    private HandlerThread mHandlerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");
        new Thread(new Runnable() {
            @Override
            public void run() {
                doWork();
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandlerThread.quit();
    }

    private void doWork(){
        BinderPool binderPool = BinderPool.getInstance(this);
        IBinder securityBinder = binderPool.queryBinder(BinderPool.BINDER_SECURITY_CENTER);
        ISecurityCenter mSecurityCenter = (ISecurityCenter)SecurityCenterImpl.asInterface(securityBinder);
        Log.d(TAG, "visit ISecurityCenter");
        String msg = "helloworld-安卓";
        try {
            String password = mSecurityCenter.encrypt(msg);
            Log.d(TAG, "encrypt:" + password);
            Log.d(TAG, "decrypt:" + mSecurityCenter.decrypt(password));
        } catch (RemoteException e) {
            Log.e(TAG, "ISecurityCenter:" + e);
        }

        IBinder computeBinder = binderPool.queryBinder(BinderPool.BINDER_COMPUTER);
        ICompute mCompute = (ICompute)ComputeImpl.asInterface(computeBinder);
        Log.d(TAG, "visit ICompute");
        try {
            Log.d(TAG, "1+1=" + mCompute.add(1, 1));
        } catch (RemoteException e) {
            Log.e(TAG, "ICompute:" + e);
        }
    }
}