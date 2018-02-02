package com.alfanthariq.tekteksil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alfanthariq.tekteksil.adapter.GridItemView;
import com.alfanthariq.tekteksil.adapter.TextDrawable;
import com.alfanthariq.tekteksil.fragment.MendatarFragment;
import com.alfanthariq.tekteksil.fragment.MenurunFragment;
import com.alfanthariq.tekteksil.helper.GameDataHelper;
import com.alfanthariq.tekteksil.helper.GameSettingHelper;
import com.alfanthariq.tekteksil.model.GlobalResponse;
import com.alfanthariq.tekteksil.rest.ApiInterface;
import com.google.gson.Gson;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.w3c.dom.Text;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class GameActivity extends AppCompatActivity
        implements GridItemView.OnClickListener {

    String TAG = "GameActivity";
    public String DBNAME;
    GridItemView[] gridView;
    GridLayout mGridLayout;
    ArrayList<Integer> listX, listY;
    Boolean success = true;
    GameDataHelper mDbHelper;
    GameSettingHelper SettingHelper;
    int numOfCol, numOfRow, lastX, lastY,
            orientation, questionIdx, actionBarHeight,
            hours, mins, secs, skor, id_tts, isSent;
    private TextView txtQuest, title_app;
    private Button btnOrient;
    private LinearLayout gameCont, questionLayout;
    private DisplayMetrics metrics;
    public SlidingUpPanelLayout mLayout;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ScrollView scroll;
    private HorizontalScrollView horScroll;
    private Date startTime;
    private Menu menu;
    private long millis, currMillis;
    private boolean isCreate, isLogin;
    private SharedPreferences pref;

    // API
    private ApiInterface api;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        api = ApiInterface.retrofit.create(ApiInterface.class);

        title_app = (TextView) findViewById(R.id.toolbar_title);
        Intent intent = getIntent();
        DBNAME = intent.getStringExtra("dbname");
        title_app.setText(intent.getStringExtra("ed_str"));
        id_tts = Integer.valueOf(intent.getStringExtra("id_tts"));
                //intent.getIntExtra("id_tts", 0);
        Toast.makeText(this, "ID TTS : "+Integer.toString(id_tts), Toast.LENGTH_SHORT).show();
        isSent = intent.getIntExtra("is_sent", 0);
        isCreate = true;
        skor = 0;

        if (pref.contains("email")) isLogin = true;
        else isLogin = false;

        orientation = 0; //0: horizontal, 1: vertical
        questionIdx = 0;
        lastY = 0;
        lastX = 0;

        scroll = (ScrollView) findViewById(R.id.scroll);
        horScroll = (HorizontalScrollView) findViewById(R.id.horScroll);

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        startTime = calendar.getTime();

        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }

        txtQuest = (TextView) findViewById(R.id.txtQuestion);
        btnOrient = (Button) findViewById(R.id.btnOrientation);
        btnOrient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                for (int i = 0; i < listX.size(); i++) {
                    gridView[listY.get(i)*numOfCol + listX.get(i)].setBackColor(R.color.colorWhite);
                }
                listX.clear();
                listY.clear();

                if (orientation==0) {
                    orientation=1;
                } else {
                    orientation=0;
                }
                blockBox(lastY, lastX, R.color.colorBlock);
            }
        });

        initBoard();
        initSlidePanel();
        initTabLayout();
        //startTimer();
    }

    @Override
    public void onStart(){
        super.onStart();
        isCreate = false;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void getPrefs() {
        pref = getApplicationContext().getSharedPreferences("tekteksil_user", MODE_PRIVATE);
    }

    private void SubmitScore(){
        TimeZone tz = TimeZone.getDefault();
        Call<GlobalResponse> call = api.submit_score(id_tts, pref.getString("email", ""), skor, tz.getID());
        // Set up progress before call
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Mengirim skor ...");
        pDialog.setIndeterminate(false);
        pDialog.setMax(100);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCancelable(false);
        pDialog.show();

        call.enqueue(new Callback<GlobalResponse>() {
            @Override
            public void onResponse(Call<GlobalResponse>call, Response<GlobalResponse> response) {
                if (response.body()!=null) {
                    Log.e(TAG, new Gson().toJson(response.body()));
                    String message = response.body().getMessage();
                    Boolean status = response.body().isStatus();
                    pDialog.dismiss();
                    if (status) {
                        SettingHelper = new GameSettingHelper(GameActivity.this);
                        SettingHelper.setKirim(id_tts);
                    }
                    Toast.makeText(GameActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GlobalResponse>call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
                pDialog.dismiss();
                Toast.makeText(GameActivity.this, "Gagal mengirim skor. Silahkan coba beberapa saat lagi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initTabLayout(){
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MendatarFragment(), "Mendatar");
        adapter.addFragment(new MenurunFragment(), "Menurun");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void initSlidePanel(){
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );

        params.setMargins(0, actionBarHeight, 0, actionBarHeight-15);
        gameCont = (LinearLayout) findViewById(R.id.gameContainer);
        gameCont.setLayoutParams(params);

        questionLayout = (LinearLayout) findViewById(R.id.question);
        questionLayout.getLayoutParams().height = metrics.heightPixels - 250;

        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    Utils.hideKeyboard(GameActivity.this);
                }
            }
        });
        mLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });
    }

    private void initBoard(){
        listX = new ArrayList<Integer>();
        listY = new ArrayList<Integer>();

        mGridLayout = (GridLayout)findViewById(R.id.grid);

        mDbHelper = new GameDataHelper(this, DBNAME);
        Log.i(TAG, "Jumlah tabel = "+Integer.toString(mDbHelper.getTableCount()));
        Cursor settingData = mDbHelper.getSettings();
        try{
            millis = settingData.getInt(settingData.getColumnIndex("milis"));
        } finally {
            settingData.close();
        }

        Cursor itemData = mDbHelper.getItems();

        numOfCol = (int) Math.sqrt((itemData.getCount()));
        numOfRow = (int) Math.sqrt((itemData.getCount()));
        gridView = new GridItemView[itemData.getCount()];

        mGridLayout.setRowCount(numOfRow);
        mGridLayout.setColumnCount(numOfCol);

        itemData.moveToPosition(-1);

        try {
            while (itemData.moveToNext()) {
                int row = itemData.getInt(itemData.getColumnIndex("row"));
                int col = itemData.getInt(itemData.getColumnIndex("col"));
                boolean isBlack = itemData.getInt(itemData.getColumnIndex("is_black_box")) < 0;
                String value = itemData.getString(itemData.getColumnIndex("text_value"));
                String badgeX = itemData.getString(itemData.getColumnIndex("badge"));
                String hor_rel = itemData.getString(itemData.getColumnIndex("hor_relation"));
                String ver_rel = itemData.getString(itemData.getColumnIndex("ver_relation"));
                final GridItemView tView = new GridItemView(this, col, row, 0, isBlack, hor_rel, ver_rel);
                tView.setId(row*numOfCol + col);
                InputFilter[] FilterArray = new InputFilter[3];
                FilterArray[0] = new InputFilter.LengthFilter(1);
                FilterArray[1] = new InputFilter.AllCaps();
                FilterArray[2] = new InputFilter() {
                    public CharSequence filter(CharSequence src, int start,
                                               int end, Spanned dst, int dstart, int dend) {
                        if (src.equals("")) {
                            return src;
                        }
                        if (src.toString().matches("[a-zA-Z ]+")) {
                            return src;
                        }
                        return "";
                    }
                };
                tView.setFilters(FilterArray);
                tView.setMaxLines(1);
                tView.setTextColor(Color.BLACK);
                String badge;
                if (badgeX.equals("-1")) {
                    badge = "";
                    tView.setEnabled(false);
                } else {
                    badge = badgeX;
                }
                tView.setCompoundDrawablesWithIntrinsicBounds(new TextDrawable(badge), null, null, null);
                tView.setPadding(30, 5, 5, 5);
                tView.setSelectAllOnFocus(true);
                tView.addTextChangedListener(new CustomTextWatcher(tView));
                tView.setOnKeyListener(new CustomKeyListener(tView));
                tView.setOnClickListener(this);
                tView.setText(value);
                tView.setInputType(InputType.TYPE_CLASS_TEXT);
                gridView[row*numOfCol + col] = tView;
                mGridLayout.addView(tView);
            }
        } finally {
            itemData.close();
        }

        mGridLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener(){

                    @Override
                    public void onGlobalLayout() {

                        final int MARGIN = 1;
                        int w = 80;
                        int h = 80;

                        Cursor itemData = mDbHelper.getItems();
                        itemData.moveToPosition(-1);

                        try {
                            while (itemData.moveToNext()) {
                                int row = itemData.getInt(itemData.getColumnIndex("row"));
                                int col = itemData.getInt(itemData.getColumnIndex("col"));
                                GridLayout.LayoutParams params =
                                        (GridLayout.LayoutParams) gridView[row * numOfCol + col].getLayoutParams();
                                params.width = w - 2 * MARGIN;
                                params.height = h - 2 * MARGIN;
                                params.setMargins(MARGIN, MARGIN, MARGIN, MARGIN);
                                gridView[row * numOfCol + col].setLayoutParams(params);
                                mGridLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                            }
                        } finally {
                            itemData.close();
                        }
                    }});

        mDbHelper.close();
    }

    private class CustomKeyListener implements EditText.OnKeyListener {
        private GridItemView mEditText;

        public CustomKeyListener(GridItemView e) {
            mEditText = e;
        }

        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if(keyCode == KeyEvent.KEYCODE_DEL){
                //on backspace
                String horRel = mEditText.getHorRelation();
                String verRel = mEditText.getVerRelation();
                String CurrentString = "";
                boolean valid = true;
                //int count = s.length();

                if (orientation==0) {
                    if (horRel.length()>0) { CurrentString = horRel; } else { valid = false; }
                } else {
                    if (verRel.length()>0) { CurrentString = verRel; } else { valid = false; }
                }

                if (valid) {
                    String[] tokens = CurrentString.split(";");

                    int eIdx = Arrays.asList(gridView).indexOf(mEditText);
                    int currIdx = Arrays.asList(tokens).indexOf(Integer.toString(eIdx));

                    if (mEditText.getText().toString().trim().equals("")) {
                        if (currIdx-1 >= 0) {
                            gridView[Integer.parseInt(tokens[currIdx - 1])].requestFocus();
                            lastX = gridView[Integer.parseInt(tokens[currIdx - 1])].getIdX();
                            lastY = gridView[Integer.parseInt(tokens[currIdx - 1])].getIdY();
                        }
                    }
                }
            }
            return false;
        }
    }

    private class CustomTextWatcher implements TextWatcher {
        private GridItemView mEditText;

        public CustomTextWatcher(GridItemView e) {

            mEditText = e;
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mEditText.invalidate();
            String horRel = mEditText.getHorRelation();
            String verRel = mEditText.getVerRelation();
            String CurrentString = "";
            boolean valid = true;
            //int count = s.length();

            if (orientation==0) {
                if (horRel.length()>0) { CurrentString = horRel; } else { valid = false; }
            } else {
                if (verRel.length()>0) { CurrentString = verRel; } else { valid = false; }
            }

            if (valid) {
                String[] tokens = CurrentString.split(";");
                int eIdx = Arrays.asList(gridView).indexOf(mEditText);
                int currIdx = Arrays.asList(tokens).indexOf(Integer.toString(eIdx));

                if (count == 1) {
                    if (currIdx + 1 <= tokens.length - 1) {
                        if (gridView[Integer.parseInt(tokens[currIdx + 1])] != null) {
                            gridView[Integer.parseInt(tokens[currIdx + 1])].requestFocus();
                            lastX = gridView[Integer.parseInt(tokens[currIdx + 1])].getIdX();
                            lastY = gridView[Integer.parseInt(tokens[currIdx + 1])].getIdY();
                        }
                    }
                }
                if (count == 0) {
                    if (currIdx - 1 >= 0) {
                        if (gridView[Integer.parseInt(tokens[currIdx - 1])] != null) {
                            gridView[Integer.parseInt(tokens[currIdx - 1])].requestFocus();
                            lastX = gridView[Integer.parseInt(tokens[currIdx - 1])].getIdX();
                            lastY = gridView[Integer.parseInt(tokens[currIdx - 1])].getIdY();
                        }
                    }
                }
            }
        }

        public void afterTextChanged(Editable s) {
            if (!isCreate) {
                GameDataHelper db = new GameDataHelper(GameActivity.this, DBNAME);
                db.updateValue(s.toString(), mEditText.getIdX(), mEditText.getIdY());
                db.close();
            }
        }
    }

    private void blockBox(int row, int col, int color){
        String CurrentString = "";
        boolean valid = true;
        String horRel = gridView[row*numOfCol + col].getHorRelation();
        String verRel = gridView[row*numOfCol + col].getVerRelation();

        if (horRel.length()>0 && verRel.length()<=0)  {
            orientation=0;
        } else if (horRel.length()<=0 && verRel.length()>0)  {
            orientation=1;
        }
        if (orientation==0) {
            if (horRel.length()>0) { CurrentString = horRel; } else { valid = false; }
        } else {
            if (verRel.length()>0) { CurrentString = verRel; } else { valid = false; }
        }

        if (valid) {
            String[] tokens = CurrentString.split(";");
            questionIdx = Integer.parseInt(tokens[0]);
            for (int i = 0; i < tokens.length; i++) {
                String id = tokens[i];
                int cRow = gridView[Integer.parseInt(id)].getIdY();
                int cCol = gridView[Integer.parseInt(id)].getIdX();

                gridView[Integer.parseInt(id)].setBackColor(color);
                listX.add(cCol);
                listY.add(cRow);
            }
            mDbHelper = new GameDataHelper(this, DBNAME);
            int cRow = gridView[questionIdx].getIdY();
            int cCol = gridView[questionIdx].getIdX();
            //Toast.makeText(this, "Col " +Integer.toString(cCol)+" , Row : "+Integer.toString(cRow), Toast.LENGTH_SHORT).show();
            Log.i(TAG, cCol+","+cRow);
            Cursor questData = mDbHelper.getQuestion(cCol, cRow, orientation);
            if (questData.getCount()>0) {
                txtQuest.setText(questData.getString(questData.getColumnIndex("quest")));
            }
            questData.close();
            mDbHelper.close();
        }
    }

    private final void focusOnView(final GridItemView vEdt){
        scroll.post(new Runnable() {
            @Override
            public void run() {
                scroll.scrollTo(0, vEdt.getTop());
            }
        });

        horScroll.post(new Runnable() {
            @Override
            public void run() {
                horScroll.scrollTo(vEdt.getLeft(), 0);
            }
        });
    }

    public void setOrientation(int value){
        orientation = value;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game, menu);
        this.menu = menu;
        MenuItem menuItem;
        menuItem = menu.findItem(R.id.timer);
        menuItem.setIcon(new IconicsDrawable(this).icon(FontAwesome.Icon.faw_clock_o).color(Color.WHITE).sizeDp(20));

        menuItem = menu.findItem(R.id.menu_submit);
        if (!isLogin) {
            menuItem.setIcon(new IconicsDrawable(this).icon(FontAwesome.Icon.faw_check).color(getResources().getColor(R.color.colorPrimaryDark)).sizeDp(20));
        } else {
            if (isSent==0) {
                menuItem.setIcon(new IconicsDrawable(this).icon(FontAwesome.Icon.faw_check).color(Color.WHITE).sizeDp(20));
            } else {
                menuItem.setIcon(new IconicsDrawable(this).icon(FontAwesome.Icon.faw_check).color(getResources().getColor(R.color.colorPrimaryDark)).sizeDp(20));
            }
        }
        //menuItem.setEnabled(isLogin);
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
        } else if (id == R.id.timer) {
            Calendar calendar = Calendar.getInstance(Locale.getDefault());
            currMillis = millis + (calendar.getTimeInMillis() - startTime.getTime());
            mins = (int) (currMillis/(1000*60)) % 60;
            secs = (int) (currMillis / 1000) % 60;
            int minusScore = (mins / 5) * 2;

            Toast.makeText(this, Integer.toString(mins)+" menit "+Integer.toString(secs)+" detik telah berlalu. Skor anda akan berkurang "+Integer.toString(minusScore)+" poin", Toast.LENGTH_LONG).show();
            return true;
        } else if (id == R.id.menu_reset_time) {
            mDbHelper = new GameDataHelper(this, DBNAME);
            try {
                mDbHelper.updateSetting(0,  99);
                millis = 0;
            } finally {
                mDbHelper.close();
            }
            return true;
        } else if (id == R.id.menu_cek_jawaban) {
            cekJawaban();
            return true;
        } else if (id == R.id.menu_submit) {
            if (isLogin) {
                if (isSent==1) {
                    Toast.makeText(this, "Skor anda sudah dikirim", Toast.LENGTH_SHORT).show();
                } else {
                    new AlertDialog.Builder(this)
                            .setTitle("Mengirim jawaban sekarang ?")
                            .setMessage("Jawaban yang anda kirim tidak bisa dikirim ulang. Skor akan terekam. Lanjutkan mengirim jawaban ?")
                            .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (cekJawaban()) {

                                    } else {
                                        new AlertDialog.Builder(GameActivity.this)
                                                .setTitle("Konfirmasi")
                                                .setMessage("Masih terdapat kesalahan di jawaban anda. Abaikan dan tetep mengirim ?")
                                                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        SubmitScore();
                                                    }
                                                })
                                                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    }
                                                })
                                                .show();
                                    }
                                }
                            })
                            .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();
                }
            } else {
                Toast.makeText(this, "Anda harus login untuk mengirim jawaban", Toast.LENGTH_SHORT).show();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mLayout != null &&
                (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mDbHelper = new GameDataHelper(this, DBNAME);
        try {
            Calendar calendar = Calendar.getInstance(Locale.getDefault());
            currMillis = millis + (calendar.getTimeInMillis() - startTime.getTime());
            mDbHelper.updateSetting(currMillis, 99);
        } finally {
            mDbHelper.close();
        }
    }

    @Override
    public void OnClick(GridItemView v) {

        if (v.isEnabled()) {
            for (int i = 0; i < listX.size(); i++) {
                gridView[listY.get(i) * numOfCol + listX.get(i)].setBackColor(R.color.colorWhite);
            }
            listX.clear();
            listY.clear();

            int x = v.getIdX();
            int y = v.getIdY();
            lastX = x;
            lastY = y;
            blockBox(y, x, R.color.colorBlock);
        }
    }

    public void setBlock(int x, int y){
        for (int i = 0; i < listX.size(); i++) {
            gridView[listY.get(i)*numOfCol + listX.get(i)].setBackColor(R.color.colorWhite);
        }
        listX.clear();
        listY.clear();

        focusOnView(gridView[y*numOfCol + x]);
        lastX = x;
        lastY = y;
        blockBox(y, x, R.color.colorBlock);
    }

    public static class Utils{
        public static void hideKeyboard(@NonNull Activity activity) {
            // Check if no view has focus:
            View view = activity.findViewById(R.id.sliding_layout);
            if (view != null) {
                InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }

        public static void showKeyboard(@NonNull Activity activity) {
            // Check if no view has focus:
            View view = activity.findViewById(R.id.sliding_layout);
            if (view != null) {
                InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.SHOW_FORCED);
            }
        }
    }

    private boolean cekJawaban(){
        Boolean res=true;
        mDbHelper = new GameDataHelper(this, DBNAME);
        try {
            Cursor listItem = mDbHelper.getItems();
            listItem.moveToPosition(-1);
            skor = 0;
            while (listItem.moveToNext()) {
                int row = listItem.getInt(listItem.getColumnIndex("row"));
                int col = listItem.getInt(listItem.getColumnIndex("col"));
                String jawaban = listItem.getString(listItem.getColumnIndex("text_value"));
                String jawabanBenarEncode = listItem.getString(listItem.getColumnIndex("ans_value"));
                if (!jawabanBenarEncode.equals("")) {
                    String jawabanBenar = decode64(jawabanBenarEncode);
                    if (jawaban.equals(jawabanBenar)) {
                        skor += 2;
                        gridView[row * numOfCol + col].setBackColor(R.color.colorWhite);
                    } else {
                        res = false;
                        gridView[row * numOfCol + col].setBackColor(R.color.colorFalseRed);
                    }
                }
            }
            skor -= getMinusScore();
            skor -= 1;
            return res;
        } finally {
            mDbHelper.close();
        }
    }

    private String decode64(String str){
        String str1 = new String(str.substring(0, 4));
        String str2 = new String(str.substring(str.length() - 4));
        String middleStr = new String(str.substring(4, str.length() - 4));
        String finalStr = new String(str2.concat(middleStr).concat(str1));

        byte[] data = Base64.decode(finalStr, Base64.DEFAULT);
        String text = new String(data, Charset.forName("UTF-8"));
        String[] tokens = text.split(":");
        return tokens[1];
    }

    private int getMinusScore(){
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        currMillis = millis + (calendar.getTimeInMillis() - startTime.getTime());
        mins = (int) (currMillis/(1000*60)) % 60;
        secs = (int) (currMillis / 1000) % 60;
        int minusScore = (mins / 5) * 2;
        return minusScore;
    }
}
