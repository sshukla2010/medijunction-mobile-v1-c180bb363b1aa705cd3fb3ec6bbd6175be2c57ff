package com.smileparser.medijunctions.utils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hardik on 25/4/18.
 */

public class ApiClient {

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS).build();

        //https://medijunction.co.in/api/User/RegisterPatient
        String url = Global.siteurl;
        retrofit = new Retrofit.Builder()
                .baseUrl(Global.siteurl + "api/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();



        return retrofit;
    }



}
