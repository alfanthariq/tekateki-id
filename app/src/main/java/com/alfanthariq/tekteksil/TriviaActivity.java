package com.alfanthariq.tekteksil;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alfanthariq.tekteksil.helper.GameDataHelper;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class TriviaActivity extends AppCompatActivity {

    private Button btnTutup;
    private TextView tvJudul, tvIsi, tvSumber;
    private String DBNAME;
    GameDataHelper mDbHelper;

    //Admob
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trivia);
        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(this));

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Poppins-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        /*MobileAds.initialize(this, "ca-app-pub-3323952393155404~9977259115");
        AdRequest request = new AdRequest.Builder()
                .build();
        mAdView = findViewById(R.id.adView);
        mAdView.loadAd(request);*/
        MobileAds.initialize(this, "ca-app-pub-3323952393155404~9977259115");
        AdRequest request = new AdRequest.Builder()
                .addTestDevice("0C7A997C83E80A8B3BFA16B8091B05A3")  // An example device ID
                .build();
        if (request.isTestDevice(this)) {
            mAdView = findViewById(R.id.adView);
            //AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(request);
        }

        tvJudul = (TextView) findViewById(R.id.tvTitleTrivia);
        tvIsi = (TextView) findViewById(R.id.tvIsiTrivia);
        tvSumber = (TextView) findViewById(R.id.tvSumber);
        btnTutup = (Button) findViewById(R.id.btnTutup);

        Intent intent = getIntent();
        DBNAME = intent.getStringExtra("dbname");
        mDbHelper = new GameDataHelper(this, DBNAME);
        Cursor setting = mDbHelper.getSettings();
        if (setting.getString(setting.getColumnIndexOrThrow("trivia_judul")) != null) {
            tvJudul.setText(setting.getString(setting.getColumnIndexOrThrow("trivia_judul")));
        }
        if (setting.getString(setting.getColumnIndexOrThrow("trivia_isi")) != null) {
            tvIsi.setText(setting.getString(setting.getColumnIndexOrThrow("trivia_isi")));
        }
        if (setting.getString(setting.getColumnIndexOrThrow("trivia_sumber")) != null) {
            tvSumber.setText(setting.getString(setting.getColumnIndexOrThrow("trivia_sumber")));
        }

        btnTutup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
