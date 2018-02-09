package com.alfanthariq.tekteksil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.nio.BufferUnderflowException;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class WelcomeActivity extends AppCompatActivity {

    private Button btnStart, btnLogin;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        getPrefs();

        if (pref.contains("email")){
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Poppins-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        btnStart = (Button) findViewById(R.id.btnStart);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnStart.setOnClickListener(myClick);
        btnLogin.setOnClickListener(myClick);
    }

    private void getPrefs() {
        pref = getApplicationContext().getSharedPreferences("tekteksil_user", MODE_PRIVATE);
    }

    private View.OnClickListener myClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            Intent intent = null;
            switch (id){
                case R.id.btnStart:
                    intent = new Intent(WelcomeActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.btnLogin:
                    intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                    intent.putExtra("openFrom", 1);
                    startActivity(intent);
                    break;
            }
        }
    };

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
