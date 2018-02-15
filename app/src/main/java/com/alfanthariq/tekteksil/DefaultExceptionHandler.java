package com.alfanthariq.tekteksil;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import java.util.Arrays;

/**
 * Created by alfanthariq on 13/02/2018.
 */

public class DefaultExceptionHandler implements Thread.UncaughtExceptionHandler {

    Activity activity;

    public DefaultExceptionHandler(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void uncaughtException(Thread thread, final Throwable ex) {
        Intent intent = new Intent(activity, WelcomeActivity.class);
        //intent.putExtra(activity.getString(R.string.app_crashed), ex);
        //Log.d("ERROR","---------" + ex.getMessage());
        //Log.d("ERROR","--------" + ex.getCause());
        //Log.d("ERROR","--------" + Arrays.toString(ex.getStackTrace()));
        activity.startActivity(intent);
        activity.finish();
        System.exit(0);
    }
}
