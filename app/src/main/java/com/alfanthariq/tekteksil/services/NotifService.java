package com.alfanthariq.tekteksil.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.alfanthariq.tekteksil.MainActivity;
import com.alfanthariq.tekteksil.R;
import com.alfanthariq.tekteksil.model.GlobalResponse;
import com.alfanthariq.tekteksil.rest.ApiInterface;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotifService extends Service {
    private String TAG = "NotfifService";
    public static final String ACTION_CLIENT_LIST_BROADCAST = "com.alfanthariq.hotspotid.MainActivity";
    NotificationCompat.Builder notifBuilder;
    Notification notification;
    NotificationManager notifManager;
    int notifyID = 1203;
    private String android_id;
    Handler handler = new Handler();
    private BroadcastReceiver receiver;
    // API
    private ApiInterface api;

    @Override
    public void onCreate() {
        api = ApiInterface.retrofit.create(ApiInterface.class);
        android_id = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        // To avoid cpu-blocking, we create a background handler to run our service
        HandlerThread thread = new HandlerThread("NotifService",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        //mServiceLooper = thread.getLooper();
        //mServiceHandler = new ServiceHandler(mServiceLooper);
        notifManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(isOnline()==true)
                {
                    checkNotif(android_id);
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        this.registerReceiver(this.receiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // call a new service handler. The service ID can be used to identify the service
        /*Message message = mServiceHandler.obtainMessage();
        message.arg1 = startId;
        mServiceHandler.sendMessage(message);
        loop = true;*/
        handler.postDelayed(new Runnable() {
            public void run() {
                checkNotif(android_id);
                handler.postDelayed(this, 6 * 60 * 60 * 1000); //run every 6 hours
            }
        }, 0);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy(){
        notifManager.cancel(notifyID);
        this.unregisterReceiver(this.receiver);
        super.onDestroy();
    }

    protected void showToast(final String msg){
        //gets the main thread
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                // run this code in the main thread
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendBroadcastMessage() {
        Intent intent = new Intent(ACTION_CLIENT_LIST_BROADCAST);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void checkNotif(String android_id){
        Call<GlobalResponse> call = api.checkNotif(android_id);
        // Set up progress before call
        call.enqueue(new Callback<GlobalResponse>() {
            @Override
            public void onResponse(Call<GlobalResponse>call, Response<GlobalResponse> response) {
                if (response.body()!=null) {
                    Boolean status = response.body().isStatus();
                    String message = response.body().getMessage();
                    if (status) {
                        showNotif(message);
                    }
                }
            }

            @Override
            public void onFailure(Call<GlobalResponse>call, Throwable t) {
                // Log error here since request failed

            }
        });
    }

    private void showNotif(String jsonStr){
        int tipe=1;
        String judul = "";
        String isi = "";
        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                tipe = jsonObj.getInt("tipe");
                judul = jsonObj.getString("title");
                isi = jsonObj.getString("content");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        PendingIntent pendingIntent = null;
        Intent notificationIntent = null;
        switch (tipe){
            case 1:
                notificationIntent = new Intent(NotifService.this, MainActivity.class);
                notificationIntent.putExtra("auto_refresh", true);
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                pendingIntent =
                        TaskStackBuilder.create(NotifService.this)
                                .addNextIntent(notificationIntent)
                                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                break;
        }
        notifBuilder = new NotificationCompat.Builder(NotifService.this)
                .setSmallIcon(getNotificationIcon())
                .setContentTitle("Tekateki-ID")
                .setColor(getResources().getColor(R.color.colorPrimaryDark))
                .setContentText(isi)
                .setContentIntent(pendingIntent);
        notification = notifBuilder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notifManager.notify(0, notification);
    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.mipmap.ic_launcher_round : R.mipmap.ic_launcher_round;
    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) NotifService.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }
}
