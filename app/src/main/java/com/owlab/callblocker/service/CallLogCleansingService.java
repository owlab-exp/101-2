package com.owlab.callblocker.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.owlab.callblocker.contentobserver.CallLogDeleter;

/**
 * Created by ernest on 6/13/16.
 */
public class CallLogCleansingService extends Service {
    private static final String TAG = CallLogCleansingService.class.getSimpleName();

    private CallLogDeleter callLogDeleter;

    @Override
    public void onCreate() {
        Log.d(TAG, ">>>>> creating...");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, ">>>>> starting...");

        //callLogDeleter = new CallLogDeleter(new Handler(), getBaseContext());
        callLogDeleter = new CallLogDeleter(new Handler(), getBaseContext(), this);
        Log.d(TAG, ">>>>> Uri to be registered: " + CallLog.Calls.CONTENT_URI);
        getBaseContext().getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI, true, callLogDeleter);
        Toast.makeText(getBaseContext(), "Call log cleansing service started", Toast.LENGTH_SHORT).show();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, ">>>>> destroying...");

        getBaseContext().getContentResolver().unregisterContentObserver(callLogDeleter);
        callLogDeleter = null;
        Toast.makeText(getBaseContext(), "Call log cleansing service terminated", Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}