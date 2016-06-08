package com.owlab.callblocker.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.owlab.callblocker.Constant;
import com.owlab.callblocker.MainActivity;
import com.owlab.callblocker.R;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class CallBlockerIntentService extends IntentService {
    public static final String TAG  = CallBlockerIntentService.class.getSimpleName();

    private static final String ACTION_STARTUP = "com.owlab.callblocker.service.action.STARTUP";
    private static final String ACTION_STATUSBAR_NOTIFICATION_ON = "com.owlab.callblocker.service.action.STATUSBAR_NOTIFICATION_ON";
    private static final String ACTION_STATUSBAR_NOTIFICATION_OFF = "com.owlab.callblocker.service.action.STATUSBAR_NOTIFICATION_OFF";

    private static boolean isStarted = false;
    private static boolean statusbarNotificationOn = false;

    public CallBlockerIntentService() {
        super(TAG);
    }

    public static void startService(Context context) {
        Intent intent = new Intent(context, CallBlockerIntentService.class);
        intent.setAction(ACTION_STARTUP);
        context.startService(intent);
    }

    public static void startActionStatusbarNotificationOn(Context context) {
        Intent intent = new Intent(context, CallBlockerIntentService.class);
        intent.setAction(ACTION_STATUSBAR_NOTIFICATION_ON);
        context.startService(intent);
    }

    public static void startActionStatusbarNotificationOff(Context context) {
        Intent intent = new Intent(context, CallBlockerIntentService.class);
        intent.setAction(ACTION_STATUSBAR_NOTIFICATION_OFF);
        context.startService(intent);
    }

    public static void stopService(Context context) {
        Intent intent = new Intent(context, CallBlockerIntentService.class);
        context.stopService(intent);
    }

    //@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!isStarted) {
            //TODO Register receivers, depending on the permissions
            ////pref_key_blocking_notification_on
            //SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            //if(preferences.getBoolean("pref_key_blocking_notification_on", false)) {
            //    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
            //            .setSmallIcon(R.drawable.ic_call_blocker_48)
            //            .setContentTitle("CallBlocker")
            //            .setOngoing(true)
            //            .setContentIntent(PendingIntent.getActivity(getApplication(), 0, new Intent(getApplication(), MainActivity.class), 0));
            //    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            //    notificationManager.notify(Constant.STATUSBAR_NOTIFICATION_ID, notificationBuilder.build());
            //}
            isStarted = true;
            Toast.makeText(this, "CallBlocker service started", Toast.LENGTH_SHORT).show();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if(statusbarNotificationOn) {
            if(PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getBoolean("pref_key_blocking_notification_on", false)) {
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(Constant.STATUSBAR_NOTIFICATION_ID);
            }
            statusbarNotificationOn = false;
            Toast.makeText(this, "CallBlocker service terminated", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(intent == null) return;

        if(intent.getAction().equals(ACTION_STATUSBAR_NOTIFICATION_ON)) {
            handleActionStatusbarNotificationOn();
        } else if(intent.getAction().equals(ACTION_STATUSBAR_NOTIFICATION_OFF)) {
            handleActionStatusbarNotificationOff();
        }
    }

    private void handleActionStatusbarNotificationOn() {
        //If the notification is already on then return
        //If the notification is disabled then return
        if(statusbarNotificationOn || !PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getBoolean("pref_key_blocking_notification_on", false)) return;

        //Otherwise enable notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_call_blocker_48)
                .setContentTitle("CallBlocker")
                .setOngoing(true)
                .setContentIntent(PendingIntent.getActivity(getApplication(), 0, new Intent(getApplication(), MainActivity.class), 0));
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Constant.STATUSBAR_NOTIFICATION_ID, notificationBuilder.build());
        statusbarNotificationOn = true;
    }

    private void handleActionStatusbarNotificationOff() {
        //If the notification is not turned on, return
        if(!statusbarNotificationOn) return;

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(Constant.STATUSBAR_NOTIFICATION_ID);
        statusbarNotificationOn = false;
    }
    //// IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    //private static final String ACTION_FOO = "com.owlab.callblocker.service.action.FOO";
    //private static final String ACTION_BAZ = "com.owlab.callblocker.service.action.BAZ";

    //private static final String EXTRA_PARAM1 = "com.owlab.callblocker.service.extra.PARAM1";
    //private static final String EXTRA_PARAM2 = "com.owlab.callblocker.service.extra.PARAM2";

    //public CallBlockerIntentService() {
    //    super("CallBlockerIntentService");
    //}

    ///**
    // * Starts this service to perform action Foo with the given parameters. If
    // * the service is already performing a task this action will be queued.
    // *
    // * @see IntentService
    // */
    //public static void startActionFoo(Context context, String param1, String param2) {
    //    Intent intent = new Intent(context, CallBlockerIntentService.class);
    //    intent.setAction(ACTION_FOO);
    //    intent.putExtra(EXTRA_PARAM1, param1);
    //    intent.putExtra(EXTRA_PARAM2, param2);
    //    context.startService(intent);
    //}

    ///**
    // * Starts this service to perform action Baz with the given parameters. If
    // * the service is already performing a task this action will be queued.
    // *
    // * @see IntentService
    // */
    //public static void startActionBaz(Context context, String param1, String param2) {
    //    Intent intent = new Intent(context, CallBlockerIntentService.class);
    //    intent.setAction(ACTION_BAZ);
    //    intent.putExtra(EXTRA_PARAM1, param1);
    //    intent.putExtra(EXTRA_PARAM2, param2);
    //    context.startService(intent);
    //}

    //@Override
    //protected void onHandleIntent(Intent intent) {
    //    if (intent != null) {
    //        final String action = intent.getAction();
    //        if (ACTION_FOO.equals(action)) {
    //            final String param1 = intent.getStringExtra(EXTRA_PARAM1);
    //            final String param2 = intent.getStringExtra(EXTRA_PARAM2);
    //            handleActionFoo(param1, param2);
    //        } else if (ACTION_BAZ.equals(action)) {
    //            final String param1 = intent.getStringExtra(EXTRA_PARAM1);
    //            final String param2 = intent.getStringExtra(EXTRA_PARAM2);
    //            handleActionBaz(param1, param2);
    //        }
    //    }
    //}

    ///**
    // * Handle action Foo in the provided background thread with the provided
    // * parameters.
    // */
    //private void handleActionFoo(String param1, String param2) {
    //    throw new UnsupportedOperationException("Not yet implemented");
    //}

    ///**
    // * Handle action Baz in the provided background thread with the provided
    // * parameters.
    // */
    //private void handleActionBaz(String param1, String param2) {
    //    throw new UnsupportedOperationException("Not yet implemented");
    //}
}
