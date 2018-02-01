package com.alfanthariq.tekteksil;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alfanthariq.tekteksil.model.GlobalResponse;
import com.alfanthariq.tekteksil.rest.ApiInterface;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.alfanthariq.tekteksil.rest.RestTools.MD5;

public class ForgotActivity extends AppCompatActivity {
    private String TAG = "ForgotActivity";
    private Toolbar toolbar;
    private EditText inputPassword;
    private TextInputLayout inputLayoutPassword;
    private Button btnKirim;
    private SharedPreferences pref;

    // API
    private ApiInterface api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Poppins-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        inputPassword = (EditText) findViewById(R.id.input_password);
        btnKirim = (Button) findViewById(R.id.btn_forgot);
        btnKirim.setOnClickListener(kirimReq);

        inputPassword.addTextChangedListener(new ForgotActivity.MyTextWatcher(inputPassword));

        api = ApiInterface.retrofit.create(ApiInterface.class);
        getPrefs();
    }

    private void getPrefs() {
        pref = this.getSharedPreferences("tekteksil_user", MODE_PRIVATE);
    }

    private View.OnClickListener kirimReq = new View.OnClickListener() {
        public void onClick(View v) {
            submitForm();
        }
    };

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void submitForm() {
        if (!validatePassword()) {
            return;
        }
        SendRequest();
    }

    private void SendRequest(){
        String pwd_hash = MD5(inputPassword.getText().toString());
        Call<GlobalResponse> call = api.send_forgot(pref.getString("email", ""), pwd_hash);
        Log.d(TAG, "Password : "+pwd_hash);
        // Set up progress before call
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Mengirim request ...");
        pDialog.setIndeterminate(false);
        pDialog.setMax(100);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCancelable(false);
        pDialog.show();
        ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(inputPassword.getWindowToken(), 0);

        call.enqueue(new Callback<GlobalResponse>() {
            @Override
            public void onResponse(Call<GlobalResponse>call, Response<GlobalResponse> response) {
                if (response.body()!=null) {
                    Log.e(TAG, new Gson().toJson(response.body()));
                    String message = response.body().getMessage();
                    Boolean status = response.body().isStatus();
                    pDialog.dismiss();
                    Toast.makeText(ForgotActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GlobalResponse>call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
                pDialog.dismiss();
                Toast.makeText(ForgotActivity.this, "Registrasi gagal", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validatePassword() {
        if (inputPassword.getText().toString().trim().isEmpty()) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            requestFocus(inputPassword);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_password:
                    validatePassword();
                    break;
            }
        }
    }
}
