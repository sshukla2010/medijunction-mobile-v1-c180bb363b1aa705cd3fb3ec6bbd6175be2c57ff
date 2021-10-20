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
import com.smileparser.medijunctions.bean.Allergy;
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

public class AllergyDataService {

    Context context;
    ApiInterface itemInterface;
    ServiceBase<Allergy> serviceBase;

    public AllergyDataService(Context context)
    {
        this.context=context;
        itemInterface= ApiClient.getClient().create(ApiInterface.class);
        serviceBase=new ServiceBase<Allergy>(context);
    }



    public void getAll()
    {
        try
        {
            if(Global.isInternetOn(context))
            {
                try {
                    Map<String,String> header = Global.generateHeader(context);

                    Call<ApiResponse> apiResponseCall = itemInterface.getAllergyList(header);

                    apiResponseCall.enqueue(new Callback<ApiResponse>() {
                        @Override
                        public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                            //progressBar.setVisibility(View.GONE);

                            if (Global.IsNotNull(response.body())) {
                                if (response.body().isResponse()) {

                                    Gson gson = new Gson();
                                    Type type = new TypeToken<List<Allergy>>() {
                                    }.getType();
                                    List<Allergy> allergyList = gson.fromJson(response.body().getResult(), type);

                                    if(Global.IsNotNull(allergyList) )
                                    {
                                        if (allergyList.size()>0) {
                                           /* List<Allergy> allergies = serviceBase.GetAll(Allergy.class);

                                            if (Global.IsNotNull(allergies)) {
                                                if (allergies.size() > 0) {
                                                    //if (allergies.size() != allergyList.size()) {
                                                        //serviceBase.ResetTableData(Allergy.class);
                                                        serviceBase.InsertMany(allergyList, Allergy.class);
                                                    //}
                                                } else {
                                                    serviceBase.InsertMany(allergyList, Allergy.class);
                                                }
                                            } else {
                                                serviceBase.InsertMany(allergyList, Allergy.class);
                                            }*/
                                            serviceBase.ResetTableData(Allergy.class);
                                            serviceBase.InsertMany(allergyList, Allergy.class);
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

                                Log.d("log","log");
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
                Log.e(AllergyDataService.class.getName(), exp.getMessage());
        }
    }
}
