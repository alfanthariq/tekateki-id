package com.alfanthariq.tts.fragment;


import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alfanthariq.tts.BuildConfig;
import com.alfanthariq.tts.DefaultExceptionHandler;
import com.alfanthariq.tts.LoginActivity;
import com.alfanthariq.tts.NewsActivity;
import com.alfanthariq.tts.PengaturanActivity;
import com.alfanthariq.tts.R;
import com.alfanthariq.tts.RiwayatActivity;
import com.alfanthariq.tts.adapter.AvailableAdapter;
import com.alfanthariq.tts.adapter.DownloadedAdapter;
import com.alfanthariq.tts.adapter.NewsAdapter;
import com.alfanthariq.tts.adapter.RankingAdapter;
import com.alfanthariq.tts.helper.EndlessScrollListener;
import com.alfanthariq.tts.helper.GameSettingHelper;
import com.alfanthariq.tts.model.AvailableTts;
import com.alfanthariq.tts.model.AvailableTtsResponse;
import com.alfanthariq.tts.model.GlobalResponse;
import com.alfanthariq.tts.model.NewsDetail;
import com.alfanthariq.tts.model.NewsObject;
import com.alfanthariq.tts.model.NewsResponse;
import com.alfanthariq.tts.model.RankingDetail;
import com.alfanthariq.tts.model.RankingObject;
import com.alfanthariq.tts.model.RankingResponse;
import com.alfanthariq.tts.rest.ApiInterface;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class BottomNavFragment extends Fragment {

    private String TAG = "BottomNavFragment";
    private FrameLayout fragmentContainer;
    private ListView listView;
    private GameSettingHelper mDbHelper;
    private Cursor itemData;
    private SwipeRefreshLayout swipeRefresh;
    private AvailableAdapter qAdapter = null;
    private DownloadedAdapter dAdapter = null;
    private TextView txtLogin, txtFullName, txtEmail, txtFacebook;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private boolean shouldRefreshOnResume = false;
    private View view;
    private int limit = 10;
    private int last_idx_rank, last_idx_news;
    private List<RankingObject> rankList = new ArrayList<RankingObject>();
    private List<NewsObject> newsList = new ArrayList<NewsObject>();
    private RankingAdapter rankAdapter = null;
    private NewsAdapter newsAdapter = null;
    private View footer;
    private Button btnPengaturan, btnRule, btnRiwayat,
                    btnVersi, btnContact, btnShare, btnRate;

    // API
    ApiInterface api;

    public static BottomNavFragment newInstance(int index) {
        BottomNavFragment fragment = new BottomNavFragment();
        Bundle b = new Bundle();
        b.putInt("index", index);
        fragment.setArguments(b);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(getActivity()));
        api = ApiInterface.retrofit.create(ApiInterface.class);
        mDbHelper = new GameSettingHelper(getContext());
        getPrefs();
        last_idx_rank = 0;
        last_idx_news = 0;

        if (getArguments().getInt("index", 0) == 0) {
            view = inflater.inflate(R.layout.fragment_available_tts, container, false);
            initAvailable(view);
            return view;
        } else if (getArguments().getInt("index", 0) == 1) {
            view = inflater.inflate(R.layout.fragment_downloaded_tts, container, false);
            initDownloaded(view);
            return view;
        } else if (getArguments().getInt("index", 0) == 2) {
            view = inflater.inflate(R.layout.fragment_rank, container, false);
            initRank(view);
            return view;
        } else if (getArguments().getInt("index", 0) == 3) {
            view = inflater.inflate(R.layout.fragment_news, container, false);
            initNews(view);
            return view;
        } else if (getArguments().getInt("index", 0) == 4) {
            view = inflater.inflate(R.layout.fragment_settings, container, false);
            initSetting(view);
            return view;
        } else {
            return null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Check should we need to refresh the fragment
        if(shouldRefreshOnResume){
            if (getArguments().getInt("index", 0) == 0) {
                initAvailable(view);
            } else if (getArguments().getInt("index", 0) == 1) {
                initDownloaded(view);
            } else if (getArguments().getInt("index", 0) == 4) {
                initSetting(view);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        shouldRefreshOnResume = true;
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {
            if (getArguments().getInt("index", 0) == 0) {
                try {
                    Cursor myCursor = mDbHelper.getAvailable();
                    qAdapter.swapCursor(myCursor);
                } catch (Exception e){

                }
            } else if (getArguments().getInt("index", 0) == 1) {
                try {
                    Cursor myCursor = mDbHelper.getDownloaded(pref.getInt("tipe_downloaded", 0));
                    dAdapter.swapCursor(myCursor);
                } catch (Exception e){

                }
            } else if (getArguments().getInt("index", 0) == 4) {
                try {
                    updateInfo();
                } catch (Exception e){

                }
            }
        }
    }

    private void getPrefs() {
        pref = getContext().getSharedPreferences("tekteksil_user", MODE_PRIVATE);
    }

    private void initRank(View view) {
        listView = (ListView) view.findViewById(R.id.list);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        fragmentContainer = (FrameLayout) view.findViewById(R.id.frameBg);

        String[] arraySpinner = new String[] {
                "Bulan ini", "Semester ini", "Tahun ini"
        };
        final Spinner s = (Spinner) view.findViewById(R.id.spin_tipe);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
        s.setSelection(0);
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                rankList.clear();
                rankAdapter.notifyDataSetChanged();
                last_idx_rank = 0;
                getRankPaging(i, last_idx_rank);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorPrimaryDark),
                getResources().getColor(R.color.colorAccent));

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (last_idx_rank>0) {
                    rankList.clear();
                    rankAdapter.notifyDataSetChanged();
                    last_idx_rank = 0;
                    int tipe = s.getSelectedItemPosition();
                    getRankPaging(tipe, last_idx_rank);
                }
            }
        });

        footer = ((LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.footer_loader, null, false);
        rankAdapter = new RankingAdapter(getActivity(), rankList);
        listView.setAdapter(rankAdapter);

        View empty = view.findViewById(R.id.emptyView);
        listView.setEmptyView(empty);
        listView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                int tipe = s.getSelectedItemPosition();
                getRankPaging(tipe, last_idx_rank);
                // or loadNextDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });
    }

    private void initNews(View view) {
        listView = (ListView) view.findViewById(R.id.list);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        fragmentContainer = (FrameLayout) view.findViewById(R.id.frameBg);

        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorPrimaryDark),
                getResources().getColor(R.color.colorAccent));

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (last_idx_news>0) {
                    newsList.clear();
                    newsAdapter.notifyDataSetChanged();
                    last_idx_news = 0;
                    getNewsPaging(last_idx_news);
                }
            }
        });

        footer = ((LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.footer_loader, null, false);
        newsAdapter = new NewsAdapter(getActivity(), newsList);
        listView.setAdapter(newsAdapter);

        View empty = view.findViewById(R.id.emptyView);
        listView.setEmptyView(empty);
        listView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                getNewsPaging(last_idx_news);
                // or loadNextDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getContext(), NewsActivity.class);
                intent.putExtra("id", newsList.get(i).getId());
                intent.putExtra("judul", newsList.get(i).getJudul());
                intent.putExtra("tanggal", newsList.get(i).getPublish_date());
                intent.putExtra("img64", newsList.get(i).getImg64());
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        getNewsPaging(last_idx_news);
    }

    private void initSetting(View view) {
        txtLogin = view.findViewById(R.id.txtSign);
        txtFullName = view.findViewById(R.id.txtFullName);
        txtEmail = view.findViewById(R.id.txtEmail);
        txtFacebook = view.findViewById(R.id.txtFBConnect);
        fragmentContainer = view.findViewById(R.id.frameBg);

        btnPengaturan = view.findViewById(R.id.btnPengaturan);
        btnRule = view.findViewById(R.id.btnRule);
        btnRiwayat = view.findViewById(R.id.btnRiwayat);
        btnVersi = view.findViewById(R.id.btnVersi);
        btnContact = view.findViewById(R.id.btnContact);
        btnShare = view.findViewById(R.id.btnShare);
        btnRate = view.findViewById(R.id.btnRate);

        btnPengaturan.setOnClickListener(new MyButtonClick());
        btnRule.setOnClickListener(new MyButtonClick());
        btnRiwayat.setOnClickListener(new MyButtonClick());
        btnVersi.setOnClickListener(new MyButtonClick());
        btnContact.setOnClickListener(new MyButtonClick());
        btnShare.setOnClickListener(new MyButtonClick());
        btnRate.setOnClickListener(new MyButtonClick());

        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());

        updateInfo();
    }

    public void updateInfo(){
        boolean isLogin;
        if (pref.contains("email")) isLogin = true;
        else isLogin = false;

        txtEmail.setText(pref.getString("email", ""));
        txtFullName.setText(pref.getString("full_name", "Masuk atau daftar untuk bisa berkompetisi dengan pemain lain"));

        if (pref.getInt("auth_type", 0)==0) {
            txtFacebook.setText("");
        } else {
            txtFacebook.setText("Terkoneksi dengan akun facebook");
        }

        final int auth_type = pref.getInt("auth_type",0);

        if (isLogin==true){
            txtLogin.setTag(1);
            txtLogin.setText(R.string.capt_keluar);
            txtLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (auth_type==1) {
                        disconnectFromFacebook();
                        logout(1);
                    } else if (auth_type==2) {
                        logout(2);
                    } else {
                        logout(0);
                    }
                }
            });
        } else {
            if (auth_type==0) {
                txtLogin.setTag(0);
                txtLogin.setText(R.string.capt_masuk);
            }
            txtLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
                }
            });
        }
    }

    public class MyButtonClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnPengaturan:
                    Intent intent = new Intent(getContext(), PengaturanActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
                    break;
                case R.id.btnRule:
                    LayoutInflater inflater= LayoutInflater.from(getContext());
                    View view=inflater.inflate(R.layout.dialog_aturan, null);

                    new AlertDialog.Builder(getContext())
                            .setTitle("Aturan permainan")
                            .setView(view)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();
                    break;
                case R.id.btnRiwayat:
                    if (!pref.contains("email")) {
                        Toast.makeText(getContext(), "Anda harus masuk ke akun untuk melihat riwayat permainan", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intentRiwayat = new Intent(getContext(), RiwayatActivity.class);
                        startActivity(intentRiwayat);
                        getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
                    }
                    break;
                case R.id.btnVersi:
                    String versionName = BuildConfig.VERSION_NAME;
                    String appName = getResources().getString(R.string.app_name);
                    new AlertDialog.Builder(getContext())
                            .setTitle("Versi Aplikasi")
                            .setMessage("Saat ini anda menggunakan "+appName+" dengan versi "+versionName)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();
                    break;
                case R.id.btnContact:
                    new AlertDialog.Builder(getContext())
                            .setTitle("Contact me")
                            .setMessage("Anda bisa menghubungi developer melalui : \nEmail : contact@alfanthariq.com \nWhatsApp : +6287787088499")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();
                    break;
                case R.id.btnShare:
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
                    String sAux = "\nAyo isi waktumu dengan hal yang bermanfaat. Asah otakmu di Tekateki-ID\n\n";
                    sAux = sAux + "market://details?id=" + getActivity().getPackageName()+" \n\n";
                    i.putExtra(Intent.EXTRA_TEXT, sAux);
                    startActivity(Intent.createChooser(i, "Berbagi dengan"));
                    break;
                case R.id.btnRate:
                    launchMarket();
                    break;
            }
        }
    }

    private void launchMarket() {
        Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), " unable to find market app", Toast.LENGTH_LONG).show();
        }
    }

    private void initDownloaded(View view) {
        listView = (ListView) view.findViewById(R.id.list);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        fragmentContainer = (FrameLayout) view.findViewById(R.id.frameBg);

        String[] arraySpinner = new String[] {
                "Semua", "Belum selesai", "Sudah selesai"
        };
        final Spinner s = (Spinner) view.findViewById(R.id.spin_tipe);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
        s.setSelection(0);
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                editor = pref.edit();
                editor.putInt("tipe_downloaded", i);
                editor.apply();
                Cursor myCursor = mDbHelper.getDownloaded(i);
                dAdapter.swapCursor(myCursor);
                listView.deferNotifyDataSetChanged();
                swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimary),
                                          getResources().getColor(R.color.colorPrimaryDark),
                                          getResources().getColor(R.color.colorAccent));
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Cursor myCursor = mDbHelper.getDownloaded(pref.getInt("tipe_downloaded", 0));
                dAdapter.swapCursor(myCursor);
                listView.deferNotifyDataSetChanged();
                swipeRefresh.setRefreshing(false);
            }
        });

        itemData = mDbHelper.getDownloaded(pref.getInt("tipe_downloaded", 0));

        try {
            dAdapter = new DownloadedAdapter(getContext(), itemData, listView, getActivity());
            listView.setAdapter(dAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                public void onItemClick(AdapterView<?> parent, View view, int position, long arg3)
                {
                    Cursor cur = (Cursor) dAdapter.getItem(position);
                    cur.moveToPosition(position);
                }
            });
            View empty = view.findViewById(R.id.emptyView);
            listView.setEmptyView(empty);
        } catch (Exception e){

        }
    }

    private void initAvailable(View view) {
        listView = (ListView) view.findViewById(R.id.list);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        fragmentContainer = (FrameLayout) view.findViewById(R.id.frameBg);

        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorPrimaryDark),
                getResources().getColor(R.color.colorAccent));
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAvailableTTS();
            }
        });

        itemData = mDbHelper.getAvailable();

        try {
            qAdapter = new AvailableAdapter(getContext(), itemData, listView, getActivity());
            listView.setAdapter(qAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                public void onItemClick(AdapterView<?> parent, View view, int position, long arg3)
                {
                    Cursor cur = (Cursor) qAdapter.getItem(position);
                    cur.moveToPosition(position);
                }
            });
            View empty = view.findViewById(R.id.emptyView);
            listView.setEmptyView(empty);
        } catch (Exception e){

        }
        getAvailableTTS();
    }

    private void getRankPaging(int tipe, int startIdx){
        swipeRefresh.setRefreshing(true);
        Call<RankingResponse> call = api.getRank(tipe, startIdx, limit);
        call.enqueue(new Callback<RankingResponse>() {
            @Override
            public void onResponse(Call<RankingResponse>call, Response<RankingResponse> response) {
                List<RankingDetail> data = response.body().getData();
                for (int i = 0; i < data.size(); i++) {
                    int total_skor = data.get(i).getTotal_skor();
                    String nama = data.get(i).getFull_name();
                    String user_id = data.get(i).getUser_id();
                    String kota = data.get(i).getKota();
                    String prov = data.get(i).getProv();
                    String img64 = data.get(i).getImg64();

                    RankingObject ranking = new RankingObject();
                    ranking.setRanking(rankList.size()+1);
                    ranking.setTotal_skor(total_skor);
                    ranking.setFull_name(nama);
                    ranking.setUser_id(user_id);
                    ranking.setKota(kota);
                    ranking.setProv(prov);
                    ranking.setImg64(img64);
                    rankList.add(ranking);
                    last_idx_rank += 1;
                }
                rankAdapter.notifyDataSetChanged();
                swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<RankingResponse>call, Throwable t) {
                // Log error here since request failed
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    private void getNewsPaging(int startIdx){
        swipeRefresh.setRefreshing(true);
        Call<NewsResponse> call = api.getNews(startIdx, limit);
        call.enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                List<NewsDetail> data = response.body().getData();
                for (int i = 0; i < data.size(); i++) {
                    int id = data.get(i).getId();
                    String judul = data.get(i).getJudul();
                    String ctn = data.get(i).getCtn();
                    String img64 = data.get(i).getImg64();
                    String p_date = data.get(i).getPublish_date();

                    NewsObject news = new NewsObject();
                    news.setId(id);
                    news.setJudul(judul);
                    news.setCtn(ctn);
                    news.setImg64(img64);
                    news.setPublish_date(p_date);
                    newsList.add(news);
                    last_idx_news += 1;
                }
                newsAdapter.notifyDataSetChanged();
                swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<NewsResponse>call, Throwable t) {
                // Log error here since request failed
                swipeRefresh.setRefreshing(true);
            }
        });
    }

    public void getAvailableTTS(){
        String tglTerbit = mDbHelper.getLastTglTerbit();
        Call<AvailableTtsResponse> call = api.getAvailableTTS(tglTerbit);
        call.enqueue(new Callback<AvailableTtsResponse>() {
            @Override
            public void onResponse(Call<AvailableTtsResponse>call, Response<AvailableTtsResponse> response) {
                List<AvailableTts> data = response.body().getData();
                //Log.d(TAG, "Data size : "+Integer.toString(data.size()));
                for (int i = 0; i < data.size(); i++) {
                    int id = data.get(i).getIdTts();
                    String ed_string = data.get(i).getEdisiStr();
                    int ed_int = data.get(i).getEdisiInt();
                    String tgl_terbit = data.get(i).getTglTerbit();
                    String nama_db = data.get(i).getNamaFile();

                    mDbHelper.addAvailableTTS(id, ed_int, ed_string, tgl_terbit, nama_db);
                }
                mDbHelper.close();
                swipeRefresh.setRefreshing(false);
                Cursor itemDataNew = mDbHelper.getAvailable();
                itemData = qAdapter.swapCursor(itemDataNew);
            }

            @Override
            public void onFailure(Call<AvailableTtsResponse>call, Throwable t) {
                // Log error here since request failed
                swipeRefresh.setRefreshing(false);
                //Log.d(TAG, "Error failure : "+t.getMessage());
            }
        });
    }

    private void logout(final int auth_type){
        String email = pref.getString("email", "");
        Call<GlobalResponse> call = api.logout(email);
        // Set up progress before call
        final ProgressDialog pDialog = new ProgressDialog(getActivity());

        if (auth_type==0) {
            pDialog.setMessage("Logging out ...");
            pDialog.setIndeterminate(false);
            pDialog.setMax(100);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        call.enqueue(new Callback<GlobalResponse>() {
            @Override
            public void onResponse(Call<GlobalResponse>call, Response<GlobalResponse> response) {
                String message = response.body().getMessage();
                Boolean status = response.body().isStatus();
                if (auth_type==0) {
                    pDialog.dismiss();
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                }
                removeSharedPref();
                updateInfo();
            }

            @Override
            public void onFailure(Call<GlobalResponse>call, Throwable t) {
                // Log error here since request failed
                if (auth_type==0) {
                    pDialog.dismiss();
                    Toast.makeText(getContext(), "Logout gagal, mohon coba kembali", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Called when a fragment will be displayed
     */
    public void willBeDisplayed() {
        // Do what you want here, for example animate the content
        if (fragmentContainer != null) {
            Animation fadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
            fragmentContainer.startAnimation(fadeIn);
        }
    }

    /**
     * Called when a fragment will be hidden
     */
    public void willBeHidden() {
        if (fragmentContainer != null) {
            Animation fadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
            fragmentContainer.startAnimation(fadeOut);
        }
    }

    /**
     * Refresh
     */
    public void refresh() {
        if (getArguments().getInt("index", 0) == 0 && listView != null) {
            listView.smoothScrollToPosition(0);
        }
    }

    public void disconnectFromFacebook() {
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Logging off ...");
        pDialog.setIndeterminate(false);
        pDialog.setMax(100);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCancelable(false);
        pDialog.show();
        if (AccessToken.getCurrentAccessToken() == null) {
            return; // already logged out
        }

        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                .Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {
                LoginManager.getInstance().logOut();
                txtLogin.setTag(0);
                txtLogin.setText(R.string.capt_masuk);
                pDialog.dismiss();
            }
        }).executeAsync();
    }

    private void removeSharedPref(){
        editor = pref.edit();
        editor.remove("email");
        editor.remove("user_id");
        editor.remove("id_kabkota");
        editor.remove("full_name");
        editor.remove("auth_type");
        editor.remove("lokasi");
        editor.remove("img64");
        editor.apply();
    }
}
