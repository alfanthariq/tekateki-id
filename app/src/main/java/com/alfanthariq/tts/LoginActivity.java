package com.alfanthariq.tts;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alfanthariq.tts.model.LoginDetail;
import com.alfanthariq.tts.model.LoginResponse;
import com.alfanthariq.tts.rest.ApiInterface;
import com.alfanthariq.tts.rest.ServiceGenerator;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.alfanthariq.tts.rest.RestTools.MD5;

public class LoginActivity extends AppCompatActivity {
    private String TAG = "LoginActivity";
    private Toolbar toolbar;
    private EditText inputEmail, inputPassword;
    private TextInputLayout inputLayoutEmail, inputLayoutPassword;
    private Button btnSignUp;
    private FancyButton btnSignUpFb;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private TextView txtReg, txtForgot;
    private int openFrom;

    // API
    private ApiInterface api;

    // Instance of Facebook Class
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(this));

        FacebookSdk.sdkInitialize(this.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        //Toast.makeText(LoginActivity.this, "Login sukses", Toast.LENGTH_SHORT).show();
                        getFbInfo(loginResult);
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(LoginActivity.this, "Login Cancel", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(LoginActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        setContentView(R.layout.activity_login);

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

        Intent intent = getIntent();
        openFrom = intent.getIntExtra("openFrom", 0);

        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        inputEmail = (EditText) findViewById(R.id.input_email);
        inputPassword = (EditText) findViewById(R.id.input_password);
        btnSignUp = (Button) findViewById(R.id.btn_signup);
        btnSignUpFb = (FancyButton) findViewById(R.id.btn_signup_fb);

        inputEmail.addTextChangedListener(new MyTextWatcher(inputEmail));
        inputPassword.addTextChangedListener(new MyTextWatcher(inputPassword));

        txtReg = (TextView) findViewById(R.id.txtRegistrasi);
        txtReg.setOnClickListener(openReg);
        txtForgot = (TextView) findViewById(R.id.txtForgot);
        txtForgot.setOnClickListener(openForgot);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });
        btnSignUpFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "email"));
            }
        });

        api = ServiceGenerator.createService(ApiInterface.class);
        getPrefs();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private View.OnClickListener openReg = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(LoginActivity.this, RegistrasiActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        }
    };

    private View.OnClickListener openForgot = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(LoginActivity.this, ForgotActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        }
    };

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
        if (openFrom!=1) {
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            if (openFrom!=1) {
                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void submitForm() {
        if (!validateEmail()) {
            return;
        }
        if (!validatePassword()) {
            return;
        }
        Login(0, inputEmail.getText().toString(), "", inputPassword.getText().toString(), "");
    }

    private void Login(final int auth_type, String email, String user_id, String password, String fullname){
        String pwd_hash = MD5(password);
        Call<LoginResponse> call = api.login(auth_type, email, user_id,
                                             pwd_hash, fullname);
        // Set up progress before call
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Authenticating ...");
        pDialog.setIndeterminate(false);
        pDialog.setMax(100);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCancelable(false);
        pDialog.show();
        ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(inputEmail.getWindowToken(), 0);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse>call, Response<LoginResponse> response) {
                try {
                    if (response.body()!=null) {
                        if (auth_type==0) {
                            JSONObject jsonObj = new JSONObject(new Gson().toJson(response.body()));
                            if (jsonObj.has("data")) {
                                List<LoginDetail> data = response.body().getData();
                                for (int i = 0; i < data.size(); i++) {
                                    String email = data.get(i).getEmail();
                                    int id_kabkota = data.get(i).getId_kabkota();
                                    String user_id = data.get(i).getUser_id();
                                    String full_name = data.get(i).getFull_name();
                                    String kota = data.get(i).getKota();
                                    String prov = data.get(i).getProv();
                                    String img64 = data.get(i).getImg64();

                                    editor = pref.edit();
                                    editor.putString("email", email);
                                    editor.putString("user_id", user_id);
                                    editor.putInt("id_kabkota", id_kabkota);
                                    editor.putString("full_name", full_name);
                                    editor.putInt("auth_type", 0);
                                    editor.putString("lokasi", kota+", "+prov);
                                    editor.putString("img64", img64);
                                    editor.apply();
                                    pDialog.dismiss();
                                    Toast.makeText(LoginActivity.this, "Login berhasil", Toast.LENGTH_SHORT).show();
                                    finish();
                                    overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                                }
                            } else {
                                String message = response.body().getMessage();
                                pDialog.dismiss();
                                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Boolean stat = response.body().isStatus();
                            if (stat) {
                                pDialog.dismiss();
                                LoginActivity.this.finish();
                                Toast.makeText(LoginActivity.this, "Login berhasil", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse>call, Throwable t) {
                // Log error here since request failed
                pDialog.dismiss();
                Toast.makeText(LoginActivity.this, "Login gagal", Toast.LENGTH_SHORT).show();
            }
        });
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
            }
        }
    }

    private void getFbInfo(LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            String id = object.getString("id");
                            String first_name = object.getString("first_name");
                            String last_name = object.getString("last_name");
                            String full_name = first_name +" "+ last_name;
                            String gender = object.getString("gender");
                            String image_url = "http://graph.facebook.com/" + id + "/picture?type=large";
                            String email;
                            if (object.has("email")) {
                                email = object.getString("email");
                            } else {
                                email = "";
                            }

                            editor = pref.edit();
                            editor.putString("email", email);
                            editor.putString("user_id", id);
                            editor.putInt("id_kabkota", 99);
                            editor.putString("full_name", full_name);
                            editor.putInt("auth_type", 1);
                            editor.putString("lokasi", "Indonesia");
                            editor.putString("img64", "");
                            editor.apply();
                            Login(1, email, id, "", full_name);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,first_name,last_name,email,gender"); // id,first_name,last_name,email,gender,birthday,cover,picture.type(large)
        request.setParameters(parameters);
        request.executeAsync();
    }
}
