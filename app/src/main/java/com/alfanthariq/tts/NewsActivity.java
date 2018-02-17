package com.alfanthariq.tts;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alfanthariq.tts.model.NewsData;
import com.alfanthariq.tts.model.NewsDataResponse;
import com.alfanthariq.tts.rest.ApiInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class NewsActivity extends AppCompatActivity {

    private String TAG = "NewsActivity";
    private Toolbar toolbar;
    private TextView tvJudul, tvTanggal;
    private ProgressBar progBar;
    //private ScrollView scrollView;
    private ImageView ivPic;
    private WebView wvContent;

    // API
    private ApiInterface api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(this));

        api = ApiInterface.retrofit.create(ApiInterface.class);
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

        tvJudul = (TextView) findViewById(R.id.tvJudul);
        tvTanggal = (TextView) findViewById(R.id.tvTanggal);
        wvContent = (WebView) findViewById(R.id.wvContent);
        ivPic = (ImageView) findViewById(R.id.ivPic);
        Intent intent = getIntent();
        int id = intent.getIntExtra("id", 0);
        String img64 = intent.getStringExtra("img64");
        tvJudul.setText(intent.getStringExtra("judul"));
        tvTanggal.setText(intent.getStringExtra("tanggal"));
        progBar = (ProgressBar) findViewById(R.id.progress_bar_news);
        Bitmap decodedImage;
        if (img64!=null) {
            byte[] imageBytes = Base64.decode(img64, Base64.DEFAULT);
            decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            ivPic.setImageBitmap(decodedImage);
        }

        getNewsDetail(id);
    }

    private void initLoading(Boolean visible){
        if(visible) {
            progBar.setVisibility(View.VISIBLE);
            wvContent.setVisibility(View.GONE);
        } else {
            progBar.setVisibility(View.GONE);
            wvContent.setVisibility(View.VISIBLE);
        }
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

    private void getNewsDetail(int id){
        initLoading(true);
        Call<NewsDataResponse> call = api.getNewsByID(id);
        call.enqueue(new Callback<NewsDataResponse>() {
            @Override
            public void onResponse(Call<NewsDataResponse> call, Response<NewsDataResponse> response) {
                List<NewsData> data = response.body().getData();
                for (int i = 0; i < data.size(); i++) {
                    String isi = data.get(i).getIsi();
                    //tvIsi.setText(isi);
                    wvContent.getSettings().setJavaScriptEnabled(false);
                    wvContent.getSettings().setBuiltInZoomControls(false);
                    wvContent.setInitialScale(1);
                    wvContent.getSettings().setUseWideViewPort(true);
                    wvContent.loadData(isi, "text/html", "UTF-8");
                }
                initLoading(false);
            }

            @Override
            public void onFailure(Call<NewsDataResponse>call, Throwable t) {
                // Log error here since request failed
                initLoading(false);
                Toast.makeText(NewsActivity.this, "Gagal mengambil detail berita", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
