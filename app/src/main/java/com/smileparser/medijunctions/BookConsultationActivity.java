package com.smileparser.medijunctions;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.smileparser.medijunctions.bean.User;
import com.smileparser.medijunctions.databinding.ActivityBookConsultationBinding;
import com.smileparser.medijunctions.fragments.CouponsFragment;
import com.smileparser.medijunctions.fragments.OurDoctorFragment;
import com.smileparser.medijunctions.utils.Global;

import java.util.ArrayList;


public class BookConsultationActivity extends AppCompatActivity {

    ActivityBookConsultationBinding binding;
    public static User user;
    CouponsFragment couponsFragment;
    OurDoctorFragment ourDoctorFragment;
    CouponsFragment myCouponsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookConsultationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        try {

            ActionBar actionBar = getSupportActionBar();
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.nav_bookConsultation));

        } catch (Exception e) {
            e.printStackTrace();
        }

        initUI();
    }



    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = item -> {

        switch (item.getItemId()) {

            case R.id.navigation_coupons:

              if (binding.bottomNavigation.getSelectedItemId() != R.id.navigation_coupons) {
                    openFragment(couponsFragment, CouponsFragment.TAG);
               }
                break;

            case R.id.navigation_ourdoctors:
                if (binding.bottomNavigation.getSelectedItemId() != R.id.navigation_ourdoctors) {
                    openFragment(ourDoctorFragment, CouponsFragment.TAG);
                }
                break;
            case R.id.navigation_mycoupons:
                if (binding.bottomNavigation.getSelectedItemId() != R.id.navigation_mycoupons) {
                    openFragment(myCouponsFragment, CouponsFragment.TAG);
                }
                break;
            default:
                break;
        }


        return true;
    };

    public void openFragment(Fragment fragment, String tag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.flBookingContainer, fragment, tag);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }

    private void initUI() {

        binding.bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        try {
            user = Global.getUserDetails(BookConsultationActivity.this);

            binding.bottomNavigation.post(() -> {
                couponsFragment = new CouponsFragment(BookConsultationActivity.this);
                ourDoctorFragment = new OurDoctorFragment(BookConsultationActivity.this);
                myCouponsFragment = new CouponsFragment(BookConsultationActivity.this);
                binding.bottomNavigation.setSelectedItemId(R.id.navigation_coupons);
                openFragment(couponsFragment, CouponsFragment.TAG);
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                // ProjectsActivity is my 'home' activity
                onBackPressed();
                return true;
        }
        return (super.onOptionsItemSelected(menuItem));
    }


    @Override
    public void onBackPressed() {
        Global.globalregisterPatientList = new ArrayList<>();
        Intent dashbord = new Intent(BookConsultationActivity.this, Dashboard.class);
        dashbord.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(dashbord);
        overridePendingTransition(0,0);
    }
}