package com.smileparser.medijunctions.services;

/**
 * Created by HD on 13/10/2017.
 */

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.smileparser.medijunctions.bean.ApiResponse;
import com.smileparser.medijunctions.bean.HealthCondition;
import com.smileparser.medijunctions.bean.RegisterPatient;
import com.smileparser.medijunctions.customviews.LoadingDialog;
import com.smileparser.medijunctions.interfaces.ApiInterface;
import com.smileparser.medijunctions.utils.ApiClient;
import com.smileparser.medijunctions.utils.Global;
import com.smileparser.medijunctions.utils.ServiceBase;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HealthConditionDataService {

    Context context;
    ApiInterface itemInterface;
    ServiceBase<HealthCondition> serviceBase;

    public HealthConditionDataService(Context context)
    {
        this.context=context;
        itemInterface= ApiClient.getClient().create(ApiInterface.class);
        serviceBase=new ServiceBase<HealthCondition>(context);
    }



    public void getAll()
    {
        try
        {
            if(Global.isInternetOn(context))
            {
                try {
                Map<String,String> header = Global.generateHeader(context);

                Call<ApiResponse> apiResponseCall = itemInterface.getHealthConditionList(header);

                apiResponseCall.enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                        //progressBar.setVisibility(View.GONE);

                        if (Global.IsNotNull(response.body())) {
                            if (response.body().isResponse()) {

                                Gson gson = new Gson();
                                Type type = new TypeToken<List<HealthCondition>>() {
                                }.getType();
                                List<HealthCondition> healthConditionList = gson.fromJson(response.body().getResult(), type);

                                if(Global.IsNotNull(healthConditionList) )
                                {
                                    if (healthConditionList.size()>0) {
                                       /* List<HealthCondition> healthConditions = serviceBase.GetAll(HealthCondition.class);

                                        if (Global.IsNotNull(healthConditions)) {
                                            if (healthConditions.size() > 0) {
                                                if (healthConditions.size() != healthConditionList.size()) {
                                                    serviceBase.ResetTableData(HealthCondition.class);
                                                    serviceBase.InsertMany(healthConditionList, HealthCondition.class);
                                                }
                                            } else {
                                                serviceBase.InsertMany(healthConditionList, HealthCondition.class);
                                            }
                                        } else {
                                            serviceBase.InsertMany(healthConditionList, HealthCondition.class);
                                        }*/
                                        serviceBase.ResetTableData(HealthCondition.class);
                                        serviceBase.InsertMany(healthConditionList, HealthCondition.class);
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
                Log.e(HealthConditionDataService.class.getName(), exp.getMessage());
        }
    }
}
