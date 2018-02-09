package com.alfanthariq.tekteksil;

import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.alfanthariq.tekteksil.helper.GameSettingHelper;
import com.alfanthariq.tekteksil.model.GlobalResponse;
import com.alfanthariq.tekteksil.model.Kabkota;
import com.alfanthariq.tekteksil.model.KabkotaResponse;
import com.alfanthariq.tekteksil.model.Provinsi;
import com.alfanthariq.tekteksil.model.ProvinsiResponse;
import com.alfanthariq.tekteksil.rest.ApiInterface;
import com.alfanthariq.tekteksil.services.NotifService;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class PengaturanActivity extends AppCompatActivity {

    private String TAG = "PengaturanActivity";
    private Toolbar toolbar;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private TextView tvLoc, tvEmail;
    private EditText etNama;
    private CircleImageView ivFoto;
    private ImageView ivEditLoc;
    private LinearLayout linLayLoc;
    private Switch swAutoNotif, swDarkMode;
    public static final int PICK_IMAGE = 1;
    private GameSettingHelper mDbHelper;
    private Cursor kabkota, provCursor;
    private int id_kota;
    private AlertDialog alertDialog;
    private Menu menuTool;
    private String base64Image = null;

    // API
    private ApiInterface api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengaturan);

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

        tvEmail = (TextView) findViewById(R.id.tvEmail);
        tvLoc = (TextView) findViewById(R.id.tvLocation);
        linLayLoc = (LinearLayout) findViewById(R.id.linLayoutLoc);
        etNama = (EditText) findViewById(R.id.etNama);
        ivFoto = (CircleImageView) findViewById(R.id.profile_image);
        swAutoNotif = (Switch) findViewById(R.id.swAutoNotif);
        swDarkMode = (Switch) findViewById(R.id.swNightMode);
        ivEditLoc = (ImageView) findViewById(R.id.ivEditLoc);

        etNama.addTextChangedListener(new MyTextWatcher(etNama));

        api = ApiInterface.retrofit.create(ApiInterface.class);
        getPrefs();

        swAutoNotif.setChecked(isMyServiceRunning(NotifService.class));
        swAutoNotif.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    startService(new Intent(PengaturanActivity.this, NotifService.class));
                } else {
                    stopService(new Intent(PengaturanActivity.this, NotifService.class));
                }
            }
        });

        if (pref.contains("email")) {
            id_kota = pref.getInt("id_kabkota", 99);
            tvEmail.setText(pref.getString("email", ""));
            tvLoc.setText(pref.getString("lokasi", "Indonesia"));
            tvEmail.setVisibility(View.VISIBLE);
            linLayLoc.setVisibility(View.VISIBLE);
            etNama.setEnabled(true);
            etNama.setText(pref.getString("full_name", ""));
            String img64 = pref.getString("img64", "");
            Bitmap decodedImage;
            if (img64!=null && !img64.equals("")) {
                byte[] imageBytes = Base64.decode(img64, Base64.DEFAULT);
                decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                ivFoto.setImageBitmap(decodedImage);
            }
            ivFoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                }
            });

            ivEditLoc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //final Dialog login = new Dialog(PengaturanActivity.this, R.style.DialogSlideAnim);
                    //login.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    // Set GUI of login screen
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(PengaturanActivity.this);
                    LayoutInflater inflater = PengaturanActivity.this.getLayoutInflater();
                    View login = inflater.inflate(R.layout.login_dialog, null);
                    dialogBuilder.setView(login);

                    Button btnSet = (Button) login.findViewById(R.id.btnSet);
                    Button btnCancel = (Button) login.findViewById(R.id.btnCancel);
                    Spinner spProv = (Spinner) login.findViewById(R.id.spin_provinsi);
                    Spinner spKabkota = (Spinner) login.findViewById(R.id.spin_kabkota);

                    alertDialog = dialogBuilder.create();
                    alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogSlideAnim;
                    alertDialog.show();

                    mDbHelper = new GameSettingHelper(PengaturanActivity.this);
                    final int provCount = mDbHelper.getProvCount();

                    if (provCount==0){
                        getProvinsi(spProv, spKabkota);
                    }
                    initSpProv(spProv, spKabkota);
                    initSpKabkota(spKabkota);

                    btnSet.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int id_kabkota = kabkota.getInt(kabkota.getColumnIndexOrThrow("_id"));
                            String kota = kabkota.getString(kabkota.getColumnIndexOrThrow("nama_kabkota"));
                            String prov = provCursor.getString(provCursor.getColumnIndexOrThrow("nama"));
                            if(id_kabkota>=0) {
                                id_kota = id_kabkota;
                                tvLoc.setText(kota+", "+prov);
                                showMenu(true);
                                alertDialog.dismiss();
                            } else {
                                Toast.makeText(PengaturanActivity.this, "Silahkan memilih kabupaten / kota", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });
                }
            });
        } else {
            etNama.setEnabled(false);
            etNama.setText(getResources().getString(R.string.unlogin_name));
            tvEmail.setVisibility(View.GONE);
            linLayLoc.setVisibility(View.GONE);
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void showMenu(Boolean status){
        if (menuTool!=null) {
            menuTool.findItem(0)
                    .setVisible(status);
        }
    }

    private void initSpProv(Spinner spProv, final Spinner spKabkota){
        provCursor =  mDbHelper.getProvinsi();
        android.widget.SimpleCursorAdapter adapter = new android.widget.SimpleCursorAdapter(this,
                android.R.layout.simple_spinner_item,
                provCursor,
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
                initSpKabkota(spKabkota);
                getKabkota(id, spKabkota);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initSpKabkota(Spinner spKabkota){
        kabkota =  mDbHelper.getKabkota();
        android.widget.SimpleCursorAdapter adapter = new android.widget.SimpleCursorAdapter(this,
                android.R.layout.simple_spinner_item,
                kabkota,
                new String[] {"nama_kabkota"},
                new int[] {android.R.id.text1}, 0);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spKabkota.setAdapter(adapter);
    }

    private void getProvinsi(final Spinner spProv, final Spinner spKabkota){
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
                    initSpProv(spProv, spKabkota);
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

    private void getKabkota(int id_prov, final Spinner spKabkota){
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
                    initSpKabkota(spKabkota);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE) {
            try {
                // get uri from Intent
                if (data!=null) {
                    Uri uri = data.getData();
                    // get bitmap from uri
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    ivFoto.setImageBitmap(bitmap);
                    // store bitmap to file
                    File filename = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
                    FileOutputStream out = new FileOutputStream(filename);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 60, out);
                    out.flush();
                    out.close();
                    // get base64 string from file
                    base64Image = getStringImage(filename);
                    showMenu(true);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getStringImage(File file){
        try {
            FileInputStream fin = new FileInputStream(file);
            byte[] imageBytes = new byte[(int)file.length()];
            fin.read(imageBytes, 0, imageBytes.length);
            fin.close();
            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
        } catch (Exception ex) {
            Log.e(TAG, Log.getStackTraceString(ex));
        }
        return null;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        menu.add(0, 0, 0, "Save").
                setIcon(new IconicsDrawable(this).icon(FontAwesome.Icon.faw_floppy_o).color(Color.WHITE).sizeDp(20));
        MenuItem menuItem;
        menuItem = menu.findItem(0);
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menuItem.setVisible(false);
        menuTool = menu;

        return true;
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
        } else if (id == 0) {
            updateProfile();
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateProfile(){
        String email = pref.getString("email", "");
        String full_name = etNama.getText().toString();
        Call<GlobalResponse> call = api.update_profile(email, full_name, base64Image, id_kota);
        Log.d(TAG, email+","+full_name+","+Integer.toString(id_kota));
        // Set up progress before call
        final ProgressDialog pDialog = new ProgressDialog(this);

        pDialog.setMessage("Updating data ...");
        pDialog.setIndeterminate(false);
        pDialog.setMax(100);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCancelable(false);
        pDialog.show();

        call.enqueue(new Callback<GlobalResponse>() {
            @Override
            public void onResponse(Call<GlobalResponse>call, Response<GlobalResponse> response) {
                String message = response.body().getMessage();
                Boolean status = response.body().isStatus();
                pDialog.dismiss();
                Toast.makeText(PengaturanActivity.this, message, Toast.LENGTH_SHORT).show();
                if (status) {
                    editor = pref.edit();
                    editor.putString("full_name", etNama.getText().toString());
                    if (base64Image != null) {
                        editor.putString("img64", base64Image);
                    }
                    editor.putString("lokasi", tvLoc.getText().toString());
                    editor.apply();
                    finish();
                    overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                }
            }

            @Override
            public void onFailure(Call<GlobalResponse>call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
                pDialog.dismiss();
                Toast.makeText(PengaturanActivity.this, "Update profile gagal, mohon coba kembali", Toast.LENGTH_SHORT).show();
            }
        });
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
                case R.id.etNama:
                    showMenu(true);
                    break;
            }
        }
    }
}
