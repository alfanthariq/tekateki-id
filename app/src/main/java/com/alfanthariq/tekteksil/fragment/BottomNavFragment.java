package com.alfanthariq.tekteksil.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alfanthariq.tekteksil.LoginActivity;
import com.alfanthariq.tekteksil.R;
import com.alfanthariq.tekteksil.adapter.AvailableAdapter;
import com.alfanthariq.tekteksil.adapter.DownloadedAdapter;
import com.alfanthariq.tekteksil.adapter.RankingAdapter;
import com.alfanthariq.tekteksil.helper.EndlessScrollListener;
import com.alfanthariq.tekteksil.helper.GameSettingHelper;
import com.alfanthariq.tekteksil.model.AvailableTts;
import com.alfanthariq.tekteksil.model.AvailableTtsResponse;
import com.alfanthariq.tekteksil.model.RankingDetail;
import com.alfanthariq.tekteksil.model.RankingObject;
import com.alfanthariq.tekteksil.model.RankingResponse;
import com.alfanthariq.tekteksil.rest.ApiInterface;
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
    private int last_idx;
    private List<RankingObject> rankList = new ArrayList<RankingObject>();
    private RankingAdapter rankAdapter = null;
    private View footer;

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
        api = ApiInterface.retrofit.create(ApiInterface.class);
        mDbHelper = new GameSettingHelper(getContext());
        getPrefs();
        last_idx = 0;

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
            initShare(view);
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
                    if (myCursor.getCount()==0) {
                        fragmentContainer.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                    } else {
                        fragmentContainer.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    }
                } catch (Exception e){

                }
            } else if (getArguments().getInt("index", 0) == 1) {
                try {
                    Cursor myCursor = mDbHelper.getDownloaded();
                    dAdapter.swapCursor(myCursor);
                    if (myCursor.getCount()==0) {
                        fragmentContainer.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                    } else {
                        fragmentContainer.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    }
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

    private void initSetting(View view) {
        txtLogin = view.findViewById(R.id.txtSign);
        txtFullName = view.findViewById(R.id.txtFullName);
        txtEmail = view.findViewById(R.id.txtEmail);
        txtFacebook = view.findViewById(R.id.txtFBConnect);
        fragmentContainer = view.findViewById(R.id.frameBg);
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());

        updateInfo();
    }

    private void initRank(View view) {
        listView = (ListView) view.findViewById(R.id.list);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        fragmentContainer = (FrameLayout) view.findViewById(R.id.frameBg);

        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorPrimaryDark),
                getResources().getColor(R.color.colorAccent));

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (last_idx>0) {
                    rankList.clear();
                    rankAdapter.notifyDataSetChanged();
                    last_idx = 0;
                    getRankPaging(last_idx);
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
                getRankPaging(last_idx);
                // or loadNextDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });

        getRankPaging(last_idx);
    }

    private void initShare(View view) {
        fragmentContainer = view.findViewById(R.id.frameBg);
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
                    } else if (auth_type==2) {

                    } else {

                    }
                    editor = pref.edit();
                    editor.remove("email");
                    editor.remove("user_id");
                    editor.remove("id_kabkota");
                    editor.remove("full_name");
                    editor.remove("auth_type");
                    editor.apply();
                    updateInfo();
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

    private void initDownloaded(View view) {
        listView = (ListView) view.findViewById(R.id.list);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        fragmentContainer = (FrameLayout) view.findViewById(R.id.frameBg);

        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimary),
                                          getResources().getColor(R.color.colorPrimaryDark),
                                          getResources().getColor(R.color.colorAccent));
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Cursor myCursor = mDbHelper.getDownloaded();
                dAdapter.swapCursor(myCursor);
                listView.deferNotifyDataSetChanged();
                swipeRefresh.setRefreshing(false);
            }
        });

        itemData = mDbHelper.getDownloaded();
        //Log.i(TAG, "Downloaded : " + Integer.toString(itemData.getCount()));
        if (itemData.getCount()==0) {
            fragmentContainer.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        } else {
            fragmentContainer.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        }
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
        //Log.i(TAG, "Available : " + Integer.toString(itemData.getCount()));
        if (itemData.getCount()==0) {
            fragmentContainer.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        } else {
            fragmentContainer.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        }
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
    }

    private void getRankPaging(int startIdx){
        swipeRefresh.setRefreshing(true);
        Call<RankingResponse> call = api.getRank(startIdx, limit);
        call.enqueue(new Callback<RankingResponse>() {
            @Override
            public void onResponse(Call<RankingResponse>call, Response<RankingResponse> response) {
                List<RankingDetail> data = response.body().getData();
                for (int i = 0; i < data.size(); i++) {
                    int rank = data.get(i).getRank();
                    int total_skor = data.get(i).getTotal_skor();
                    String nama = data.get(i).getFull_name();
                    String user_id = data.get(i).getUser_id();
                    String kota = data.get(i).getKota();
                    String prov = data.get(i).getProv();

                    RankingObject ranking = new RankingObject();
                    ranking.setRanking(rankList.size()+1);
                    ranking.setTotal_skor(total_skor);
                    ranking.setFull_name(nama);
                    ranking.setUser_id(user_id);
                    ranking.setKota(kota);
                    ranking.setProv(prov);
                    rankList.add(ranking);
                    last_idx += 1;
                }
                rankAdapter.notifyDataSetChanged();
                swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<RankingResponse>call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
                swipeRefresh.setRefreshing(true);
                Toast.makeText(getContext(), "Gagal mengambil data ranking", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getAvailableTTS(){
        String tglTerbit = mDbHelper.getLastTglTerbit();
        Call<AvailableTtsResponse> call = api.getAvailableTTS(tglTerbit);
        call.enqueue(new Callback<AvailableTtsResponse>() {
            @Override
            public void onResponse(Call<AvailableTtsResponse>call, Response<AvailableTtsResponse> response) {
                List<AvailableTts> data = response.body().getData();
                for (int i = 0; i < data.size(); i++) {
                    String ed_string = data.get(i).getEdisiStr();
                    int ed_int = data.get(i).getEdisiInt();
                    String tgl_terbit = data.get(i).getTglTerbit();
                    String nama_db = data.get(i).getNamaFile();

                    mDbHelper.addAvailableTTS(ed_int, ed_string, tgl_terbit, nama_db);
                }
                mDbHelper.close();
                swipeRefresh.setRefreshing(false);
                Cursor itemDataNew = mDbHelper.getAvailable();
                itemData = qAdapter.swapCursor(itemDataNew);
                if (itemDataNew.getCount()==0) {
                    fragmentContainer.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                } else {
                    fragmentContainer.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                }
            }

            @Override
            public void onFailure(Call<AvailableTtsResponse>call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
                Toast.makeText(getContext(), "Gagal mendapatkan TTS", Toast.LENGTH_SHORT).show();
                swipeRefresh.setRefreshing(false);
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

}
