package com.alfanthariq.tekteksil;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.alfanthariq.tekteksil.helper.GameSettingHelper;
import com.alfanthariq.tekteksil.model.Kabkota;
import com.alfanthariq.tekteksil.model.KabkotaResponse;
import com.alfanthariq.tekteksil.model.Provinsi;
import com.alfanthariq.tekteksil.model.ProvinsiResponse;
import com.alfanthariq.tekteksil.model.GlobalResponse;
import com.alfanthariq.tekteksil.rest.ApiInterface;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.alfanthariq.tekteksil.rest.RestTools.MD5;

public class RegistrasiActivity extends AppCompatActivity {

    private String TAG = "RegistrasiActivity";
    private Toolbar toolbar;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private TextInputLayout inputLayoutEmail, inputLayoutPassword, inputLayoutKonfirmPwd,
                            inputLayoutName, inputLayoutProv, inputLayoutKabkota;
    private Button btnDaftar;
    private EditText inputEmail, inputPassword, inputKonfirmPwd, inputName;
    private Spinner spProv, spKabkota;
    private GameSettingHelper mDbHelper;
    private Cursor kabkota;

    // API
    private ApiInterface api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrasi);

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

        getPrefs();

        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        inputLayoutKonfirmPwd = (TextInputLayout) findViewById(R.id.input_layout_k_password);
        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_nama);
        inputLayoutProv = (TextInputLayout) findViewById(R.id.input_layout_prov);
        inputLayoutKabkota = (TextInputLayout) findViewById(R.id.input_layout_kabkota);
        inputEmail = (EditText) findViewById(R.id.input_email);
        inputPassword = (EditText) findViewById(R.id.input_password);
        inputKonfirmPwd = (EditText) findViewById(R.id.input_k_password);
        inputName = (EditText) findViewById(R.id.input_nama);
        btnDaftar = (Button) findViewById(R.id.btnDaftar);
        spProv = (Spinner) findViewById(R.id.spin_provinsi);
        spKabkota = (Spinner) findViewById(R.id.spin_kabkota);

        inputEmail.addTextChangedListener(new RegistrasiActivity.MyTextWatcher(inputEmail));
        inputPassword.addTextChangedListener(new RegistrasiActivity.MyTextWatcher(inputPassword));
        inputKonfirmPwd.addTextChangedListener(new RegistrasiActivity.MyTextWatcher(inputKonfirmPwd));
        inputName.addTextChangedListener(new RegistrasiActivity.MyTextWatcher(inputName));

        btnDaftar.setOnClickListener(startReg);

        api = ApiInterface.retrofit.create(ApiInterface.class);
        mDbHelper = new GameSettingHelper(this);
        int provCount = mDbHelper.getProvCount();

        if (provCount==0){
            getProvinsi();
        }
        initSpProv();
        initSpKabkota();
    }

    private View.OnClickListener startReg = new View.OnClickListener() {
        public void onClick(View v) {
            if (!validateEmail()) {
                return;
            }
            if (!validatePassword()) {
                return;
            }
            if (!validatePasswordKonfirm()) {
                return;
            }
            if (!validateNama()) {
                return;
            }
            if (!validateKabkota()) {
                return;
            }
            Register();
        }
    };

    private void Register(){
        String pwd_hash = MD5(inputPassword.getText().toString());
        int id_kabkota = kabkota.getInt(kabkota.getColumnIndexOrThrow("_id"));
        Call<GlobalResponse> call = api.reg(0, inputEmail.getText().toString(), pwd_hash,
                "", inputName.getText().toString(), id_kabkota);
        Log.d(TAG, "Password : "+pwd_hash);
        // Set up progress before call
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Sending registration data ...");
        pDialog.setIndeterminate(false);
        pDialog.setMax(100);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCancelable(false);
        pDialog.show();
        ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(inputEmail.getWindowToken(), 0);

        call.enqueue(new Callback<GlobalResponse>() {
            @Override
            public void onResponse(Call<GlobalResponse>call, Response<GlobalResponse> response) {
                if (response.body()!=null) {
                    String message = response.body().getMessage();
                    Boolean status = response.body().isStatus();
                    pDialog.dismiss();
                    RegistrasiActivity.this.finish();
                    Toast.makeText(RegistrasiActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GlobalResponse>call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
                pDialog.dismiss();
                Toast.makeText(RegistrasiActivity.this, "Registrasi gagal", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initSpProv(){
        Cursor prov =  mDbHelper.getProvinsi();
        android.widget.SimpleCursorAdapter adapter = new android.widget.SimpleCursorAdapter(this,
                android.R.layout.simple_spinner_item,
                prov,
                new String[] {"nama"},
                new int[] {android.R.id.text1}, 0);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spProv.setAdapter(adapter);
        spProv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor c = (Cursor)adapterView.getItemAtPosition(i);
                int id = c.getInt(c.getColumnIndexOrThrow("_id"));
                mDbHelper.clearKabkota();
                initSpKabkota();
                getKabkota(id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initSpKabkota(){
        kabkota =  mDbHelper.getKabkota();
        android.widget.SimpleCursorAdapter adapter = new android.widget.SimpleCursorAdapter(this,
                android.R.layout.simple_spinner_item,
                kabkota,
                new String[] {"nama_kabkota"},
                new int[] {android.R.id.text1}, 0);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spKabkota.setAdapter(adapter);
    }

    private void getProvinsi(){
        Call<ProvinsiResponse> call = api.getProvinsi();
        call.enqueue(new Callback<ProvinsiResponse>() {
            @Override
            public void onResponse(Call<ProvinsiResponse>call, Response<ProvinsiResponse> response) {
                if (response.body()!=null) {
                    List<Provinsi> data = response.body().getData();
                    for (int i = 0; i < data.size(); i++) {
                        String nama = data.get(i).getNama();
                        int id = data.get(i).getId();

                        mDbHelper.addProvinsi(id, nama);
                    }
                    initSpProv();
                }
                //mDbHelper.close();
            }

            @Override
            public void onFailure(Call<ProvinsiResponse>call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
            }
        });
    }

    private void getKabkota(int id_prov){
        Call<KabkotaResponse> call = api.getKabkota(id_prov);
        call.enqueue(new Callback<KabkotaResponse>() {
            @Override
            public void onResponse(Call<KabkotaResponse>call, Response<KabkotaResponse> response) {
                if (response.body()!=null) {
                    List<Kabkota> data = response.body().getData();
                    for (int i = 0; i < data.size(); i++) {
                        String nama = data.get(i).getNama();
                        int id = data.get(i).getId();
                        int id_p = data.get(i).getId_prov();

                        mDbHelper.addKabkota(id, id_p, nama);
                    }
                    initSpKabkota();
                }
                //mDbHelper.close();
            }

            @Override
            public void onFailure(Call<KabkotaResponse>call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
            }
        });
    }

    private void getPrefs() {
        pref = this.getSharedPreferences("tekteksil_user", MODE_PRIVATE);
    }

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

    private boolean validateEmail() {
        String email = inputEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus(inputEmail);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
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

    private boolean validatePasswordKonfirm() {
        if (!inputKonfirmPwd.getText().toString().equals(inputPassword.getText().toString())) {
            inputLayoutKonfirmPwd.setError(getString(R.string.err_msg_password_k));
            requestFocus(inputKonfirmPwd);
            return false;
        } else {
            inputLayoutKonfirmPwd.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateNama() {
        if (inputName.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError(getString(R.string.err_msg_nama));
            requestFocus(inputName);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateKabkota() {
        if (kabkota.getCount()==0) {
            inputLayoutKabkota.setError(getString(R.string.err_msg_kabkota));
            requestFocus(spKabkota);
            return false;
        } else {
            inputLayoutKabkota.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
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
                case R.id.input_email:
                    validateEmail();
                    break;
                case R.id.input_password:
                    validatePassword();
                    break;
                case R.id.input_k_password:
                    validatePasswordKonfirm();
                    break;
                case R.id.input_nama:
                    validateNama();
                    break;
            }
        }
    }
}
