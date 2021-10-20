package com.smileparser.medijunctions.utils;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.smileparser.medijunctions.AddCouponsActivity;
import com.smileparser.medijunctions.BookConsultationActivity;
import com.smileparser.medijunctions.CaseHistoryActivity;
import com.smileparser.medijunctions.ClinicHistoryNativeActivity;
import com.smileparser.medijunctions.ConsultationActivity;
import com.smileparser.medijunctions.CouponsActivity;
import com.smileparser.medijunctions.Dashboard;
import com.smileparser.medijunctions.LoginActivity;
import com.smileparser.medijunctions.MissedCallLogActivity;
import com.smileparser.medijunctions.MyCouponsActivity;
import com.smileparser.medijunctions.OurDoctorActivity;
import com.smileparser.medijunctions.R;
import com.smileparser.medijunctions.RegisterPatientActivity;
import com.smileparser.medijunctions.RegisteredPatientSearchActivity;
import com.smileparser.medijunctions.bean.Coupons;
import com.smileparser.medijunctions.bean.User;
import com.smileparser.medijunctions.customviews.LoadingDialog;
import com.smileparser.medijunctions.recivers.ConnectivityReceiver;
import com.smileparser.medijunctions.services.AllergyDataService;
import com.smileparser.medijunctions.services.HealthConditionDataService;
import com.smileparser.medijunctions.services.ItemService;
import java.util.ArrayList;
import java.util.Random;

public class BaseActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {
    //, NavigationView.OnNavigationItemSelectedListener
    public boolean isInternetConnected;
    private DrawerLayout mDrawerLayout;
    protected FrameLayout frameLayout;
    TextView headerName, headerMobile;
    ImageView headerImage;
    Toolbar toolbar;
    TextView pointFirstName,pointLastName,pointPoints,pointTierType,pointTier;
    NavigationView navigationView;
    FrameLayout pointLayout;
    boolean poinLayoutOpen = false;
    User user;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.side_navigation);


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

        try {
             user = Global.getUserDetails(BaseActivity.this);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        frameLayout = (FrameLayout) findViewById(R.id.sidenav_frame_container);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);




        View headerView = navigationView.getHeaderView(0);
        headerName = (TextView) headerView.findViewById(R.id.sideheader_textview_name);
        headerImage = (ImageView) headerView.findViewById(R.id.sideheader_imagebutton_profile);

        setUserDetails();



        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.sidenavigation_home:
                                Global.globalregisterPatientList = new ArrayList<>();
                                toolbar.setTitle(R.string.app_name);
                                Intent dashbord = new Intent(BaseActivity.this, Dashboard.class);
                                dashbord.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(dashbord);
                                overridePendingTransition(0,0);
                                menuItem.setChecked(false);
                                mDrawerLayout.closeDrawers();
                                break;
                            case R.id.sidenavigation_registerpatient:
                                Global.globalregisterPatientList = new ArrayList<>();
                                Intent registerPatient = new Intent(BaseActivity.this, RegisterPatientActivity.class);
                                //registerPatient.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(registerPatient);
                                overridePendingTransition(0,0);
                                menuItem.setChecked(false);
                                mDrawerLayout.closeDrawers();
                                break;
                            case R.id.sidenavigation_registeredpatient:
                                Global.globalregisterPatientList = new ArrayList<>();
                                Intent registeredPatient = new Intent(BaseActivity.this, RegisteredPatientSearchActivity.class);
                                startActivity(registeredPatient);
                                overridePendingTransition(0,0);
                                menuItem.setChecked(false);
                                mDrawerLayout.closeDrawers();

                                break;

                            case R.id.sidenavigation_coupons:
                                Global.globalregisterPatientList = new ArrayList<>();
                                Intent bookConsultation = new Intent(BaseActivity.this, BookConsultationActivity.class);
                                startActivity(bookConsultation);
                                overridePendingTransition(0,0);
                                menuItem.setChecked(false);
                                mDrawerLayout.closeDrawers();

                                break;

                            case R.id.sidenavigation_addcoupns:
                                Global.globalregisterPatientList = new ArrayList<>();
                                Intent addcoupons = new Intent(BaseActivity.this, AddCouponsActivity.class);
                                startActivity(addcoupons);
                                overridePendingTransition(0,0);
                                menuItem.setChecked(false);
                                mDrawerLayout.closeDrawers();

                                break;
                            case R.id.sidenavigation_mycoupon:
                                Global.globalregisterPatientList = new ArrayList<>();
                                Intent mycoupons = new Intent(BaseActivity.this, MyCouponsActivity.class);
                                startActivity(mycoupons);
                                overridePendingTransition(0,0);
                                menuItem.setChecked(false);
                                mDrawerLayout.closeDrawers();

                                break;

                            case R.id.sidenavigation_ourdoctors:
                                Global.globalregisterPatientList = new ArrayList<>();
                                Intent ourdoctors = new Intent(BaseActivity.this, OurDoctorActivity.class);
                                startActivity(ourdoctors);
                                overridePendingTransition(0,0);
                                menuItem.setChecked(false);
                                mDrawerLayout.closeDrawers();

                                break;

                            case R.id.sidenavigation_consultation:
                                Global.globalregisterPatientList = new ArrayList<>();
                                Intent consultation = new Intent(BaseActivity.this, ConsultationActivity.class);
                                startActivity(consultation);
                                overridePendingTransition(0,0);
                                menuItem.setChecked(false);
                                mDrawerLayout.closeDrawers();

                                break;

                            case R.id.sidenavigation_casehistory:
                                Global.globalregisterPatientList = new ArrayList<>();
                                Intent casehistory = new Intent(BaseActivity.this,
                                    CaseHistoryActivity.class);
                                startActivity(casehistory);
                                overridePendingTransition(0, 0);
                                menuItem.setChecked(false);
                                mDrawerLayout.closeDrawers();

                                break;

                            case R.id.sidenavigation_clinichistory:
                                Global.globalregisterPatientList = new ArrayList<>();
                                Intent clinicHistory = new Intent(BaseActivity.this,
                                    ClinicHistoryNativeActivity.class);
                                startActivity(clinicHistory);
                                overridePendingTransition(0, 0);
                                menuItem.setChecked(false);
                                mDrawerLayout.closeDrawers();
                                break;

                            case R.id.sidenavigation_missedcallLogs:
                                Global.globalregisterPatientList = new ArrayList<>();
                                Intent missedCallLog = new Intent(BaseActivity.this,
                                        MissedCallLogActivity.class);
                                startActivity(missedCallLog);
                                overridePendingTransition(0, 0);
                                menuItem.setChecked(false);
                                mDrawerLayout.closeDrawers();
                                break;

                            case R.id.sidenavigation_logout:
                                Global.globalregisterPatientList = new ArrayList<>();
                                menuItem.setChecked(false);
                                //mDrawerLayout.closeDrawers();
                                if(Global.logoutUser())
                                {
                                    Intent intent = new Intent(BaseActivity.this,LoginActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                                else
                                {
                                    Toast.makeText(BaseActivity.this, "Try again", Toast.LENGTH_SHORT).show();
                                }
                                break;
                        }
                        return false;
                    }
                });
    }


    public String generateRandomNumber() {
        Random rnd = new Random();
        int number = rnd.nextInt(999999);
        return String.format("%06d", number);
    }

    @Override
    protected void onStart() {
        super.onStart();
      /*  try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    startForegroundService(new Intent(this, NotificationIntentService.class));
                }
                catch (Exception e)
                {
                    e.getMessage();
                }
                //startForegroundService(intent);
            } else {

                try {
                    startService(new Intent(this, NotificationIntentService.class));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }


            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }*/
    }


    private void setUserDetails() {


       if(Global.IsNotNull(user))
       {
           headerName.setText(user.getUserName());
           Glide.with(BaseActivity.this).load(R.drawable.ic_user).into(headerImage);

           try {
               if(Global.IsNotNull(user.getUsertype()))
               {
                   Menu menu = navigationView.getMenu();
                   if(user.getUsertype().equals(getString(R.string.hint_doctor)))
                   {

                       menu.findItem(R.id.sidenavigation_registerpatient).setVisible(false);
                       menu.findItem(R.id.sidenavigation_registeredpatient).setVisible(false);
                       menu.findItem(R.id.sidenavigation_coupons).setVisible(false);
                     /*  menu.findItem(R.id.sidenavigation_addcoupns).setVisible(false);
                       menu.findItem(R.id.sidenavigation_mycoupon).setVisible(false);
                       menu.findItem(R.id.sidenavigation_ourdoctors).setVisible(false);*/
                       menu.findItem(R.id.sidenavigation_clinichistory).setVisible(false);
                       menu.findItem(R.id.sidenavigation_casehistory).setVisible(false);
                   }
                   else if(user.getUsertype().equals(getString(R.string.hint_officer)))
                   {
                       menu.findItem(R.id.sidenavigation_missedcallLogs).setVisible(false);
                       menu.findItem(R.id.sidenavigation_coupons).setVisible(false);
                       /*menu.findItem(R.id.sidenavigation_addcoupns).setVisible(false);
                       menu.findItem(R.id.sidenavigation_mycoupon).setVisible(false);
                       menu.findItem(R.id.sidenavigation_ourdoctors).setVisible(false);*/
                       menu.findItem(R.id.sidenavigation_consultation).setVisible(false);
                       menu.findItem(R.id.sidenavigation_casehistory).setVisible(false);
                   }
                   else {
                       menu.findItem(R.id.sidenavigation_missedcallLogs).setVisible(false);
                       menu.findItem(R.id.sidenavigation_registerpatient).setTitle(getString(R.string.nav_RegisterAddFamilyMember));
                       menu.findItem(R.id.sidenavigation_registeredpatient).setTitle(getString(R.string.nav_RegisteredFamilymembers));
                       menu.findItem(R.id.sidenavigation_clinichistory).setVisible(false);
                   }

               }

           }
           catch (Exception e)
           {
               e.printStackTrace();
           }
       }
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(Global.IsNotNull(user) && Global.IsNotNull(user.getUsertype())) {
            if (user.getUsertype().equals(getString(R.string.hint_doctor)))
                menu.findItem(R.id.btn_syncmenu).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.btn_syncmenu:
                LoadingDialog.showLoadingDialog(BaseActivity.this,"Please wait data is syncing...");
                syncAllData();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void syncAllData()
    {
        try
        {
            new Thread(new Runnable()
            {
                @SuppressLint("InvalidWakeLockTag")
                @Override
                public void run()
                {
                    PowerManager pm;
                    PowerManager.WakeLock wl = null;
                    try
                    {
                        //pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
                        //wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AutoSyncService");
                        if(Global.isInternetOn(getApplicationContext()))
                        {
                          /*  if(Global.IsNotNull(wl))
                            {
                                if(wl.isHeld())
                                {
                                    wl.release();
                                }
                                wl.acquire();
                            }*/
                            if (android.os.Build.VERSION.SDK_INT > 9)
                            {
                                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                                        .permitAll().build();
                                StrictMode.setThreadPolicy(policy);
                            }
                            if(Global.IsAutoSyncServiceOn==null || Global.IsAutoSyncServiceOn==false)
                            {
                                ItemService ServerUtil=new ItemService(BaseActivity.this);
                                ServerUtil.UpdateAll();
                                ServerUtil.DeleteAll();

                                AllergyDataService allergyDataService =new AllergyDataService(BaseActivity.this);
                                allergyDataService.getAll();

                                HealthConditionDataService healthConditionDataService = new HealthConditionDataService(BaseActivity.this);
                                healthConditionDataService.getAll();
                            }
                        }
                    }
                    catch (Exception exp)
                    {
                       /* if(wl!=null)
                        {
                            if(wl.isHeld())
                                wl.release();
                            wl=null;
                        }*/
                        Global.IsAutoSyncServiceOn=false;
                        if(Global.IsNotNull(exp)&& Global.IsNotNull(exp.getMessage()))
                            Log.e(this.getClass().getName(), exp.getMessage());
                    }
                    finally
                    {
                        /*if(wl!=null)
                        {
                            if(wl.isHeld())
                                wl.release();
                            wl=null;
                        }*/
                        Global.IsAutoSyncServiceOn=false;
                        try {
                            LoadingDialog.cancelLoading();
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }
        catch(Exception exp)
        {
            if(Global.IsNotNull(exp)&& Global.IsNotNull(exp.getMessage()))
                Log.e(this.getClass().getName(), exp.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Global.getInstance(this).onCreate();
        Global.getInstance(this).setConnectivityListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected){
            isInternetConnected = false;
            Snackbar snackbar = Snackbar.make(frameLayout, "Internet is not Connected!!", Snackbar.LENGTH_INDEFINITE);
            View view = snackbar.getView();
            TextView textView = (TextView) view.findViewById(com.google.android.material.R.id.snackbar_text);
            textView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            snackbar.show();
        }
        /*  if (isConnected) {
            isInternetConnected = true;
            Snackbar snackbar = Snackbar.make(frameLayout, "Internet is Connected!!", Snackbar.LENGTH_SHORT);
            View view = snackbar.getView();
            TextView textView = (TextView) view.findViewById(com.google.android.material.R.id.snackbar_text);
            textView.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            snackbar.show();
        } else {
            isInternetConnected = false;
            Snackbar snackbar = Snackbar.make(frameLayout, "Internet is not Connected!!", Snackbar.LENGTH_INDEFINITE);
            View view = snackbar.getView();
            TextView textView = (TextView) view.findViewById(com.google.android.material.R.id.snackbar_text);
            textView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            snackbar.show();
        }*/

    }

     public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sync_menu, menu);
        return true;
    }

}
