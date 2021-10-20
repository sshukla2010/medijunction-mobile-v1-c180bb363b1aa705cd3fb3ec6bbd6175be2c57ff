package com.smileparser.medijunctions;

import android.app.Application;
import android.content.Context;

public class MediJunctionApp extends Application {


  private static MediJunctionApp mInstance;

  public MediJunctionApp() {
    mInstance = this;
  }

  public static MediJunctionApp getInstance() {
    return mInstance;
  }

  public static Context getContext() {
    return mInstance;
  }


  @Override
  public void onCreate() {
    super.onCreate();
  }

}
