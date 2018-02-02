package com.alfanthariq.tekteksil;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alfanthariq.tekteksil.model.NewsData;
import com.alfanthariq.tekteksil.model.NewsDataResponse;
import com.alfanthariq.tekteksil.rest.ApiInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class NewsActivity extends AppCompatActivity {

    private String TAG = "NewsActivity";
    private Toolbar toolbar;
    private TextView tvJudul, tvTanggal, tvIsi;
    private ProgressBar progBar;
    private ScrollView scrollView;

    // API
    private ApiInterface api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

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
        tvIsi = (TextView) findViewById(R.id.tvIsi);
        tvTanggal = (TextView) findViewById(R.id.tvTanggal);
        scrollView = (ScrollView) findViewById(R.id.scroll);
        Intent intent = getIntent();
        int id = intent.getIntExtra("id", 0);
        tvJudul.setText(intent.getStringExtra("judul"));
        tvTanggal.setText(intent.getStringExtra("tanggal"));
        progBar = (ProgressBar) findViewById(R.id.progress_bar_news);

        getNewsDetail(id);
    }

    private void initLoading(Boolean visible){
        if(visible) {
            progBar.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
        } else {
            progBar.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
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
                    tvIsi.setText(isi);
                }
                initLoading(false);
            }

            @Override
            public void onFailure(Call<NewsDataResponse>call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
                initLoading(false);
                Toast.makeText(NewsActivity.this, "Gagal mengambil detail berita", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
