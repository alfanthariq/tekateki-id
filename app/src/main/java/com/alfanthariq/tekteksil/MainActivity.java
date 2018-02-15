package com.alfanthariq.tekteksil;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.widget.Toast;

import com.alfanthariq.tekteksil.adapter.BottomNavAdapter;
import com.alfanthariq.tekteksil.fragment.BottomNavFragment;
import com.alfanthariq.tekteksil.helper.GameSettingHelper;
import com.alfanthariq.tekteksil.services.NotifService;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationViewPager;
import com.aurelhubert.ahbottomnavigation.notification.AHNotification;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import java.io.File;
import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";
    private BottomNavFragment currentFragment;
    private BottomNavAdapter adapter;
    private AHBottomNavigationAdapter navigationAdapter;
    private ArrayList<AHBottomNavigationItem> bottomNavigationItems = new ArrayList<>();
    private Handler handler = new Handler();
    private GameSettingHelper mDbHelper;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    // UI
    private AHBottomNavigationViewPager viewPager;
    private AHBottomNavigation bottomNavigation;
    private TapTargetSequence guide;

    private static final int REQUEST = 112;

    // Progress Dialog
    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;

    //Admob
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(this));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Poppins-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        if (Build.VERSION.SDK_INT >= 23) {
            String[] PERMISSIONS = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (!hasPermissions(getApplicationContext(), PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST );
            } else {
                //do here
            }
        } else {
            //do here
        }

        String dirPath = getFilesDir().getAbsolutePath() + File.separator + "tts";
        File projDir = new File(dirPath);
        if (!projDir.exists())
            projDir.mkdirs();

        getPrefs();
        initUI();
        if (!pref.getBoolean("main_guide", false)) {
            initGuide();
            guide.start();
        }

        Intent intent = getIntent();
        Boolean ref = intent.getBooleanExtra("auto_refresh", false);
        if (ref) {
            currentFragment.getAvailableTTS();
        }
    }

    private void getPrefs() {
        pref = getApplicationContext().getSharedPreferences("tekteksil_user", MODE_PRIVATE);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //do here
                } else {
                    Toast.makeText(getApplicationContext(), "The app was not allowed to write in your storage", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    /**
     * Init UI
     */
    private void initUI() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        }

        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
        viewPager = (AHBottomNavigationViewPager) findViewById(R.id.view_pager);

        AHBottomNavigationItem item1 = new AHBottomNavigationItem("New", new IconicsDrawable(MainActivity.this).icon(FontAwesome.Icon.faw_cloud_download).color(Color.WHITE).sizeDp(20),
                getResources().getColor(R.color.color_tab_1));
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("Downloaded", new IconicsDrawable(MainActivity.this).icon(FontAwesome.Icon.faw_download).color(Color.WHITE).sizeDp(20),
                getResources().getColor(R.color.color_tab_2));
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("Rankings", new IconicsDrawable(MainActivity.this).icon(FontAwesome.Icon.faw_trophy).color(Color.WHITE).sizeDp(20),
                getResources().getColor(R.color.color_tab_3));
        AHBottomNavigationItem item4 = new AHBottomNavigationItem("News", new IconicsDrawable(MainActivity.this).icon(FontAwesome.Icon.faw_newspaper_o).color(Color.WHITE).sizeDp(20),
                getResources().getColor(R.color.color_tab_4));
        AHBottomNavigationItem item5 = new AHBottomNavigationItem("Setting", new IconicsDrawable(MainActivity.this).icon(FontAwesome.Icon.faw_ellipsis_v).color(Color.WHITE).sizeDp(20),
                getResources().getColor(R.color.color_tab_5));

        bottomNavigationItems.add(item1);
        bottomNavigationItems.add(item2);
        bottomNavigationItems.add(item3);
        bottomNavigationItems.add(item4);
        bottomNavigationItems.add(item5);

        bottomNavigation.addItems(bottomNavigationItems);

        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_HIDE);
        bottomNavigation.setColored(false);
        bottomNavigation.setDefaultBackgroundColor(getResources().getColor(R.color.colorPrimary));
        bottomNavigation.setInactiveColor(getResources().getColor(R.color.colorWhite));
        bottomNavigation.setAccentColor(getResources().getColor(R.color.colorAccent));

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {

                if (currentFragment == null) {
                    currentFragment = adapter.getCurrentFragment();
                }

                if (wasSelected) {
                    currentFragment.refresh();
                    return true;
                }

                if (currentFragment != null) {
                    currentFragment.willBeHidden();
                }

                viewPager.setCurrentItem(position, false);

                if (currentFragment == null) {
                    return true;
                }

                currentFragment = adapter.getCurrentFragment();
                currentFragment.willBeDisplayed();

                if (position == 1) {
                    bottomNavigation.setNotification("", 1);

                } else {

                }

                return true;
            }
        });

        viewPager.setOffscreenPageLimit(4);
        adapter = new BottomNavAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        currentFragment = adapter.getCurrentFragment();
    }

    private void initGuide(){
        final Display display = getWindowManager().getDefaultDisplay();
        int menuWidth = display.getWidth()/5;
        int targetLeft = menuWidth - (menuWidth/2) - 30;
        int targetRight = menuWidth - 30;
        final Rect targetRect1 = new Rect( targetLeft * 1, display.getHeight()-60, targetRight * 1, display.getHeight());
        final Rect targetRect2 = new Rect( targetLeft + (menuWidth * 1), display.getHeight()-60, targetRight + (menuWidth * 1), display.getHeight());
        final Rect targetRect3 = new Rect( targetLeft + (menuWidth * 2), display.getHeight()-60, targetRight + (menuWidth * 2), display.getHeight());
        final Rect targetRect4 = new Rect( targetLeft + (menuWidth * 3), display.getHeight()-60, targetRight + (menuWidth * 3), display.getHeight());
        final Rect targetRect5 = new Rect( targetLeft + (menuWidth * 4), display.getHeight()-60, targetRight + (menuWidth * 4), display.getHeight());

        guide = new TapTargetSequence(this)
                .targets(
                        TapTarget.forBounds(targetRect1, "TTS tersedia", getResources().getString(R.string.desc_available))
                                .outerCircleColor(R.color.colorAccent)
                                .targetCircleColor(R.color.colorWhite)
                                .textColor(R.color.colorTextPrimary)
                                .dimColor(R.color.colorBlack)
                                .drawShadow(true)
                                .cancelable(false)
                                .transparentTarget(true)
                                .id(1),
                        TapTarget.forBounds(targetRect2, "TTS terunduh", getResources().getString(R.string.desc_unduh))
                                .outerCircleColor(R.color.colorAccent)
                                .targetCircleColor(R.color.colorWhite)
                                .textColor(R.color.colorTextPrimary)
                                .dimColor(R.color.colorBlack)
                                .drawShadow(true)
                                .cancelable(false)
                                .transparentTarget(true)
                                .id(2),
                        TapTarget.forBounds(targetRect3, "Ranking", getResources().getString(R.string.desc_rank))
                                .outerCircleColor(R.color.colorAccent)
                                .targetCircleColor(R.color.colorWhite)
                                .textColor(R.color.colorTextPrimary)
                                .dimColor(R.color.colorBlack)
                                .drawShadow(true)
                                .cancelable(false)
                                .transparentTarget(true)
                                .id(3),
                        TapTarget.forBounds(targetRect4, "Berita", getResources().getString(R.string.desc_news))
                                .outerCircleColor(R.color.colorAccent)
                                .targetCircleColor(R.color.colorWhite)
                                .textColor(R.color.colorTextPrimary)
                                .dimColor(R.color.colorBlack)
                                .drawShadow(true)
                                .cancelable(false)
                                .transparentTarget(true)
                                .id(4),
                        TapTarget.forBounds(targetRect5, "Lainnya", getResources().getString(R.string.desc_other))
                                .outerCircleColor(R.color.colorAccent)
                                .targetCircleColor(R.color.colorWhite)
                                .textColor(R.color.colorTextPrimary)
                                .dimColor(R.color.colorBlack)
                                .drawShadow(true)
                                .cancelable(false)
                                .transparentTarget(true)
                                .id(5)
                )
                .listener(new TapTargetSequence.Listener() {
                    // This listener will tell us when interesting(tm) events happen in regards
                    // to the sequence
                    @Override
                    public void onSequenceFinish() {
                        editor = pref.edit();
                        editor.putBoolean("main_guide", true);
                        editor.apply();
                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {

                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {

                    }
                });
    }

    /**
     * Update the bottom navigation colored param
     */
    public void updateBottomNavigationColor(boolean isColored) {
        bottomNavigation.setColored(isColored);
    }

    /**
     * Return if the bottom navigation is colored
     */
    public boolean isBottomNavigationColored() {
        return bottomNavigation.isColored();
    }

    /**
     * Show or hide the bottom navigation with animation
     */
    public void showOrHideBottomNavigation(boolean show) {
        if (show) {
            bottomNavigation.restoreBottomNavigation(true);
        } else {
            bottomNavigation.hideBottomNavigation(true);
        }
    }

    /**
     * Show or hide selected item background
     */
    public void updateSelectedBackgroundVisibility(boolean isVisible) {
        bottomNavigation.setSelectedBackgroundVisible(isVisible);
    }

    /**
     * Show or hide selected item background
     */
    public void setForceTitleHide(boolean forceTitleHide) {
        AHBottomNavigation.TitleState state = forceTitleHide ? AHBottomNavigation.TitleState.ALWAYS_HIDE : AHBottomNavigation.TitleState.ALWAYS_SHOW;
        bottomNavigation.setTitleState(state);
    }

    /**
     * Reload activity
     */
    public void reload() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    /**
     * Return the number of items in the bottom navigation
     */
    public int getBottomNavigationNbItems() {
        return bottomNavigation.getItemsCount();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type: // we set this to 0
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Downloading file. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pDialog.setCancelable(false);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }

}
