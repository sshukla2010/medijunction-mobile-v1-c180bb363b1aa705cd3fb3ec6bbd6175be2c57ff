package com.smileparser.medijunctions;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import com.smileparser.medijunctions.bean.Allergy;
import com.smileparser.medijunctions.bean.HealthCondition;
import com.smileparser.medijunctions.bean.User;
import com.smileparser.medijunctions.services.AllergyDataService;
import com.smileparser.medijunctions.services.HealthConditionDataService;
import com.smileparser.medijunctions.utils.BaseActivity;
import com.smileparser.medijunctions.utils.Global;
import com.smileparser.medijunctions.utils.ServiceBase;
import java.util.ArrayList;
import java.util.List;

public class Dashboard extends BaseActivity implements View.OnClickListener {

    TextView tv_welcome;
    ServiceBase<Allergy> allergyServiceBase;
    ServiceBase<HealthCondition> healthConditionServiceBase;
    CardView dsh_reg,dsh_registerd,dsh_coupons, dsh_video, dsh_casehistory;
    CardView dsh_ofc_reg, dsh_ofc_reged, dsh_ofc_casehistory;
    CardView doctor_video;
    LinearLayout layout_patitent, layout_doctor, layout_officer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_dashboard);
        ViewGroup content = (ViewGroup) findViewById(R.id.sidenav_frame_container);
        getLayoutInflater().inflate(R.layout.activity_dashboard, content, true);

        tv_welcome = (TextView) findViewById(R.id.tv_wellcome);

        layout_patitent = findViewById(R.id.patientDashbord);
        layout_doctor = findViewById(R.id.doctorDashbord);
        layout_officer = findViewById(R.id.officerDashbord);

        dsh_reg = findViewById(R.id.dsh_registration);
        dsh_registerd = findViewById(R.id.dsh_registerd);
        dsh_coupons = findViewById(R.id.dsh_coupons);
        dsh_video = findViewById(R.id.dsh_videocons);
        dsh_casehistory =findViewById(R.id.dsh_casehistory);

        //doctor
        doctor_video = findViewById(R.id.dsh_doct_videocons);

        //officer
        dsh_ofc_reg = findViewById(R.id.dsh_ofc_registration);
        dsh_ofc_reged = findViewById(R.id.dsh_ofc_registerd);
        dsh_ofc_casehistory = findViewById(R.id.dsh_ofc_casehistory);

        dsh_reg.setOnClickListener(this);
        dsh_registerd.setOnClickListener(this);
        dsh_coupons.setOnClickListener(this);
        dsh_video.setOnClickListener(this);
        dsh_casehistory.setOnClickListener(this);

        doctor_video.setOnClickListener(this);

        dsh_ofc_reg.setOnClickListener(this);
        dsh_ofc_reged.setOnClickListener(this);
        dsh_ofc_casehistory.setOnClickListener(this);

        healthConditionServiceBase = new ServiceBase<HealthCondition>(getApplicationContext());
        allergyServiceBase = new ServiceBase<Allergy>(getApplicationContext());




        try {

            List<Allergy> allergyList = allergyServiceBase.GetAll(Allergy.class);
            List<HealthCondition> healthConditionList = healthConditionServiceBase.GetAll(HealthCondition.class);


            if (Global.IsNotNull(allergyList)) {
                if (allergyList.size() == 0) {
                    try {

                        AllergyDataService allergyDataService = new AllergyDataService(Dashboard.this);
                        allergyDataService.getAll();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                allergyList = new ArrayList<>();

                if (allergyList.size() == 0) {
                    try {

                        AllergyDataService allergyDataService = new AllergyDataService(Dashboard.this);
                        allergyDataService.getAll();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }

            if (Global.IsNotNull(healthConditionList)) {
                if (healthConditionList.size() == 0) {
                    try {

                        HealthConditionDataService healthConditionDataService = new HealthConditionDataService(Dashboard.this);
                        healthConditionDataService.getAll();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                healthConditionList = new ArrayList<>();

                if (healthConditionList.size() == 0) {
                    try {

                        HealthConditionDataService healthConditionDataService = new HealthConditionDataService(Dashboard.this);
                        healthConditionDataService.getAll();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void initData()
    {
        try {
            User user = Global.getUserDetails(Dashboard.this);
            if (Global.IsNotNull(user)) {
                tv_welcome.setText("Welcome " + user.getUserName());

                if (user.getUsertype().equals(getString(R.string.hint_doctor))) {
                    layout_doctor.setVisibility(View.VISIBLE);
                } else if (user.getUsertype().equals(getString(R.string.hint_officer))) {
                    layout_officer.setVisibility(View.VISIBLE);
                } else {
                    layout_patitent.setVisibility(View.VISIBLE);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void onClick(View view) {

        Global.globalregisterPatientList = new ArrayList<>();
        switch (view.getId()) {

            case R.id.dsh_registration:
                Intent registerPatient = new Intent(Dashboard.this, RegisterPatientActivity.class);
                //registerPatient.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(registerPatient);
                overridePendingTransition(0, 0);
                break;
            case R.id.dsh_registerd:
                Intent registeredPatient = new Intent(Dashboard.this, RegisteredPatientSearchActivity.class);
                startActivity(registeredPatient);
                overridePendingTransition(0, 0);
                break;
            case R.id.dsh_coupons:
                Intent bookConsultation = new Intent(Dashboard.this, BookConsultationActivity.class);
                startActivity(bookConsultation);
                //registerPatient.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                overridePendingTransition(0, 0);
                break;
            case R.id.dsh_videocons:
                Intent consultation = new Intent(Dashboard.this, ConsultationActivity.class);
                startActivity(consultation);
                overridePendingTransition(0, 0);
                break;

            case R.id.dsh_doct_videocons:
                Intent consultationDoctor = new Intent(Dashboard.this, ConsultationActivity.class);
                startActivity(consultationDoctor);
                overridePendingTransition(0, 0);
                break;
            case R.id.dsh_casehistory:
                Intent caseHistory = new Intent(Dashboard.this, CaseHistoryActivity.class);
                startActivity(caseHistory);
                overridePendingTransition(0, 0);
                break;
            case R.id.dsh_ofc_casehistory:
                Intent officerCaseHistory = new Intent(Dashboard.this,
                    ClinicHistoryNativeActivity.class);
                startActivity(officerCaseHistory);
                overridePendingTransition(0, 0);
                break;
            case R.id.dsh_ofc_registration:
                Intent registerOfficer = new Intent(Dashboard.this, RegisterPatientActivity.class);
                startActivity(registerOfficer);
                overridePendingTransition(0, 0);
                break;
            case R.id.dsh_ofc_registerd:
                Intent ofregisteredPatient = new Intent(Dashboard.this,
                    RegisteredPatientSearchActivity.class);
                startActivity(ofregisteredPatient);
                overridePendingTransition(0, 0);
                break;


        }
    }
}
