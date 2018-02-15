package com.alfanthariq.tekteksil.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.alfanthariq.tekteksil.DefaultExceptionHandler;
import com.alfanthariq.tekteksil.R;
import com.alfanthariq.tekteksil.model.RiwayatObject;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import cyd.awesome.material.AwesomeButton;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by alfanthariq on 12/02/2018.
 */

public class RiwayatAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<RiwayatObject> riwayatItems;
    private CallbackManager callbackManager;
    private LoginManager manager;
    private List<String> permissionNeeds;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private RiwayatObject obj;

    public RiwayatAdapter(Activity activity, List<RiwayatObject> riwayatItems){
        this.activity = activity;
        this.riwayatItems = riwayatItems;
        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(activity));
        FacebookSdk.sdkInitialize(activity);
        callbackManager = CallbackManager.Factory.create();
        permissionNeeds = Arrays.asList("publish_actions");
        getPrefs();
    }

    @Override
    public int getCount() {
        return riwayatItems.size();
    }

    @Override
    public Object getItem(int location) {
        return riwayatItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.riwayat_list, null);

        final TextView edisi = (TextView) convertView.findViewById(R.id.tvEdisiTTS);
        //TextView tglTerbit = (TextView) convertView.findViewById(R.id.tvTglTerbit);
        TextView tglKirim = (TextView) convertView.findViewById(R.id.tvTglKirim);
        TextView skor = (TextView) convertView.findViewById(R.id.tvSkor);
        //AwesomeButton shareButton = (AwesomeButton) convertView.findViewById(R.id.btnShareFB);

        // getting movie data for the row
        obj = riwayatItems.get(position);

        edisi.setText(obj.getEdisiStr()+"("+dateFormat(obj.getTglTerbit(), "dd MMMM yyyy")+")");
        //tglTerbit.setText("Tgl. terbit TTS : "+);
        tglKirim.setText("Tgl. kirim jawaban : "+dateFormat(obj.getTglKirim(), "dd MMMM yyyy"));
        skor.setText(Integer.toString(obj.getSkor()));
        /*shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean isPermission = pref.getBoolean("sharePermission", false);
                if (isPermission) {
                    manager = LoginManager.getInstance();
                    manager.logInWithPublishPermissions(activity, permissionNeeds);
                    manager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            ShareDialog shareDialog;
                            shareDialog = new ShareDialog(activity);
                            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                    .setQuote("Saya mendapat skor "+Integer.toString(obj.getSkor())+
                                              " poin dari TTS "+obj.getEdisiStr()+"\n"+
                                              "Kamu bisa lebih baik? \nMainkan gamenya hanya di Tekateki-ID")
                                    .setContentUrl(Uri.parse("market://details?id=" + activity.getPackageName())).build();
                            shareDialog.show(linkContent);
                            editor = pref.edit();
                            editor.putBoolean("sharePermission", true);
                            editor.apply();
                            Toast.makeText(activity, "Sukses share", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancel() {
                            editor = pref.edit();
                            editor.putBoolean("sharePermission", false);
                            editor.apply();
                            Toast.makeText(activity, "Canceled", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(FacebookException exception) {
                            editor = pref.edit();
                            editor.putBoolean("sharePermission", false);
                            editor.apply();
                        }
                    });
                } else {
                    ShareDialog shareDialog;
                    shareDialog = new ShareDialog(activity);
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setQuote("Saya mendapat skor "+Integer.toString(obj.getSkor())+
                                    " poin dari TTS "+obj.getEdisiStr()+"\n"+
                                    "Kamu bisa lebih baik? \nMainkan gamenya hanya di Tekateki-ID")
                            .setContentUrl(Uri.parse("market://details?id=" + activity.getPackageName())).build();
                    shareDialog.show(linkContent);
                }
            }
        });*/

        return convertView;
    }

    private void getPrefs() {
        pref = activity.getSharedPreferences("tekteksil_user", MODE_PRIVATE);
    }

    private String dateFormat(String tgl, String format){
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd");
        Date myDate = null;
        try {
            myDate = dateFormat.parse(tgl);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat timeFormat = new SimpleDateFormat(format);
        String finalDate = timeFormat.format(myDate);
        return finalDate;
    }
}
