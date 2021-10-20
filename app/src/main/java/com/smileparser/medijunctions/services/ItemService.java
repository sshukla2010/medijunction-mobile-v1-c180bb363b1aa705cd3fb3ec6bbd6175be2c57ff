package com.smileparser.medijunctions.services;

/**
 * Created by HD on 13/10/2017.
 */

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.smileparser.medijunctions.R;
import com.smileparser.medijunctions.RegisterPatientActivity;
import com.smileparser.medijunctions.bean.ApiResponse;
import com.smileparser.medijunctions.bean.DeleteSync;
import com.smileparser.medijunctions.bean.RegisterPatient;
import com.smileparser.medijunctions.customviews.LoadingDialog;
import com.smileparser.medijunctions.interfaces.ApiInterface;
import com.smileparser.medijunctions.utils.ApiClient;
import com.smileparser.medijunctions.utils.Global;
import com.smileparser.medijunctions.utils.ServiceBase;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemService {

    Context context;
    ApiInterface itemInterface;
    ServiceBase<RegisterPatient> serviceBase;
    ServiceBase<DeleteSync> deleteSyncServiceBase;

    public ItemService(Context context)
    {
        this.context=context;
        itemInterface = ApiClient.getClient().create(ApiInterface.class);
        serviceBase=new ServiceBase<RegisterPatient>(context);
        deleteSyncServiceBase = new ServiceBase<DeleteSync>(context);
    }



    public void UpdateAll()
    {
        try
        {
            if(Global.isInternetOn(context))
            {
                try {

                final List<RegisterPatient> registerPatientList = serviceBase.GetAll(RegisterPatient.class);
                Map<String,String> header = Global.generateHeader(context);

                Call<ApiResponse> apiResponseCall = itemInterface.patientSync(header,registerPatientList);

                apiResponseCall.enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                        //progressBar.setVisibility(View.GONE);

                        if (Global.IsNotNull(response.body())) {
                            if (response.body().isResponse()) {

                                Gson gson = new Gson();
                                Type type = new TypeToken<List<RegisterPatient>>() {
                                }.getType();
                                List<RegisterPatient> registerPatientListData = gson.fromJson(response.body().getResult(), type);

                                if(Global.IsNotNull(registerPatientListData) && registerPatientListData.size()>0)
                                {
                                    for(RegisterPatient registerPatientId : registerPatientListData) {
                                        if(registerPatientId.isInserted()) {
                                            RegisterPatient registerPatient = serviceBase.FindById(registerPatientId.getId(), RegisterPatient.class);
                                            serviceBase.Delete(registerPatient, RegisterPatient.class);
                                        }
                                    }
                                }

                            }
                        } else if (Global.IsNotNull(response.errorBody())) {
                            JsonParser parser = new JsonParser();
                            JsonElement mJson = null;
                            try {
                                LoadingDialog.cancelLoading();
                                mJson = parser.parse(response.errorBody().string());
                                Gson gson = new Gson();
                                ApiResponse errorResponse = gson.fromJson(mJson, ApiResponse.class);


                            } catch (IOException ex) {
                                ex.printStackTrace();
                            } catch (Exception e) {
                                e.getMessage();
                            }


                        } else {

                        }

                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {

                    }
                });
            } catch (Exception e) {
            e.getMessage();
        }

            }
        }
        catch(Exception exp)
        {
            if(Global.IsNotNull(exp)&& Global.IsNotNull(exp.getMessage()))
                Log.e(ItemService.class.getName(), exp.getMessage());
        }
    }


    public void DeleteAll()
    {
        try
        {
            if(Global.isInternetOn(context))
            {
                try {

                    final List<DeleteSync> deleteSyncList = deleteSyncServiceBase.GetAll(DeleteSync.class);
                    Map<String,String> header = Global.generateHeader(context);
                    Call<ApiResponse> apiResponseCall = itemInterface.deletePatientSync(header,deleteSyncList);

                    apiResponseCall.enqueue(new Callback<ApiResponse>() {
                        @Override
                        public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                            //progressBar.setVisibility(View.GONE);

                            if (Global.IsNotNull(response.body())) {
                                if (response.body().isResponse()) {

                                    Gson gson = new Gson();
                                    Type type = new TypeToken<List<DeleteSync>>() {
                                    }.getType();
                                    List<DeleteSync> deleteSyncListData = gson.fromJson(response.body().getResult(), type);

                                    if(Global.IsNotNull(deleteSyncListData) && deleteSyncListData.size()>0)
                                    {
                                        for(DeleteSync deleteSync : deleteSyncListData) {
                                            if(deleteSync.isInserted()) {
                                                DeleteSync deleteSyncdata = deleteSyncServiceBase.FindById(deleteSync.getAppId(), DeleteSync.class);
                                                deleteSyncServiceBase.Delete(deleteSyncdata, DeleteSync.class);
                                            }
                                        }
                                    }

                                }
                            } else if (Global.IsNotNull(response.errorBody())) {
                                JsonParser parser = new JsonParser();
                                JsonElement mJson = null;
                                try {
                                    LoadingDialog.cancelLoading();
                                    mJson = parser.parse(response.errorBody().string());
                                    Gson gson = new Gson();
                                    ApiResponse errorResponse = gson.fromJson(mJson, ApiResponse.class);


                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                } catch (Exception e) {
                                    e.getMessage();
                                }


                            } else {

                            }

                        }

                        @Override
                        public void onFailure(Call<ApiResponse> call, Throwable t) {

                        }
                    });
                } catch (Exception e) {
                    e.getMessage();
                }

            }
        }
        catch(Exception exp)
        {
            if(Global.IsNotNull(exp)&& Global.IsNotNull(exp.getMessage()))
                Log.e(ItemService.class.getName(), exp.getMessage());
        }
    }
}
