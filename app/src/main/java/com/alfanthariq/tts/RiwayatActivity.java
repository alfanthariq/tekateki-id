package com.alfanthariq.tts;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.alfanthariq.tts.adapter.RiwayatAdapter;
import com.alfanthariq.tts.helper.EndlessScrollListener;
import com.alfanthariq.tts.model.RiwayatDetail;
import com.alfanthariq.tts.model.RiwayatObject;
import com.alfanthariq.tts.model.RiwayatResponse;
import com.alfanthariq.tts.rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class RiwayatActivity extends AppCompatActivity {

    private String TAG = "RiwayatActivity";
    private Toolbar toolbar;
    private SwipeRefreshLayout swipeRefresh;
    private List<RiwayatObject> riwayatList = new ArrayList<RiwayatObject>();
    private RiwayatAdapter riwayatAdapter = null;
    private ListView listView;
    private int limit = 10;
    private int last_idx_riwayat;
    private SharedPreferences pref;

    // API
    ApiInterface api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat);
        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(this));

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

        api = ApiInterface.retrofit.create(ApiInterface.class);
        getPrefs();

        listView = (ListView) findViewById(R.id.list);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorPrimaryDark),
                getResources().getColor(R.color.colorAccent));
        last_idx_riwayat = 0;
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (last_idx_riwayat>0) {
                    riwayatList.clear();
                    riwayatAdapter.notifyDataSetChanged();
                    last_idx_riwayat = 0;
                    String email = pref.getString("email", "");
                    getRiwayatPaging(email, last_idx_riwayat);
                }
            }
        });

        riwayatAdapter = new RiwayatAdapter(this, riwayatList);
        listView.setAdapter(riwayatAdapter);

        View empty = findViewById(R.id.emptyView);
        listView.setEmptyView(empty);
        listView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                String email = pref.getString("email", "");
                getRiwayatPaging(email, last_idx_riwayat);
                // or loadNextDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });

        String email = pref.getString("email", "");
        getRiwayatPaging(email, last_idx_riwayat);
    }

    private void getPrefs() {
        pref = this.getSharedPreferences("tekteksil_user", MODE_PRIVATE);
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

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    private void getRiwayatPaging(String email, int startIdx){
        swipeRefresh.setRefreshing(true);
        Call<RiwayatResponse> call = api.getRiwayat(email, startIdx, limit);
        call.enqueue(new Callback<RiwayatResponse>() {
            @Override
            public void onResponse(Call<RiwayatResponse>call, Response<RiwayatResponse> response) {
                List<RiwayatDetail> data = response.body().getData();
                for (int i = 0; i < data.size(); i++) {
                    int id_tts = data.get(i).getId_tts();
                    String edisi_str = data.get(i).getEdisiStr();
                    String tgl_terbit = data.get(i).getTglTerbit();
                    String tgl_kirim = data.get(i).getTglKirim();
                    int skor = data.get(i).getSkor();

                    RiwayatObject riwayat = new RiwayatObject();
                    riwayat.setId_tts(id_tts);
                    riwayat.setEdisiStr(edisi_str);
                    riwayat.setTglTerbit(tgl_terbit);
                    riwayat.setTglKirim(tgl_kirim);
                    riwayat.setSkor(skor);
                    riwayatList.add(riwayat);
                    last_idx_riwayat += 1;
                }
                riwayatAdapter.notifyDataSetChanged();
                swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<RiwayatResponse>call, Throwable t) {
                // Log error here since request failed
                swipeRefresh.setRefreshing(false);
            }
        });
    }
}
