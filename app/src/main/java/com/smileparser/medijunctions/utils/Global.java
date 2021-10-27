package com.smileparser.medijunctions.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;

import com.google.gson.Gson;
import com.smileparser.medijunctions.R;
import com.smileparser.medijunctions.bean.RegisterPatient;
import com.smileparser.medijunctions.bean.User;
import com.smileparser.medijunctions.recivers.ConnectivityReceiver;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by hardik on 18/4/18.
 */

/*    Screens details throughout the app being used for different types of logins.

        Virtual Health Officer(Patient) Login:

        Dashboard Activity            (Native)
        Book Consultaion Activity     (Native)
        Register New Patient Activity (Native)
        Patient Consultaion Activity  (Webview)
        Patient Case History Activity (Webview)
        ECG Activity                  (Native)
        Family Members List Activity  (Native)
        Asha+ Connect Activity        (Native)


        Health Officer Login:

        Register New Patient Activity (Native)
        Patient List Activity         (Native)
        Clinic History Activity       (Native)
        Case History Activity         (Webview)
        ECG Activity                  (Native)
        Asha+ Connect Activity        (Native)


        Doctor Login:

        Dashboard Activity            (Native)
        Patient Consultaion Activity  (Webview)
        Missed Call Log Activity      (Native)*/

//    Login info:
//    Health Officer: nazimahmad8587@gmail.com / nazim1234
//    Doctor: rupadivatia@yahoo.co.in / MediDoctor2019
//    Virtual Health Officer(Patient): anuju@gmail.com / anuj


public class Global {
    private static final String TAG = "Global";

    public static Context context;

    private static Global mInstance;

    public static SPreferenceManager mPrefs;

    public static DecimalFormat decimalFormat = new DecimalFormat("#.##");

    public static String siteurl; //= "https://medijunction.co.in/"; //= "https://devgv.medijunction.co.in/";

    public static String siteurlalternet = "https://medijunction.co.in/";//"https://devys.medijunction.co.in/"; //= "https://devgv.medijunction.co.in/";

    public static Boolean IsAutoSyncServiceOn=false;

    public static Boolean IsSync=false;

    public static List<RegisterPatient> globalregisterPatientList;

    /*  @Override*/
    public void onCreate() {
     /*   super.onCreate();
        context = getApplicationContext();*/
        mPrefs = SPreferenceManager.getInstance(context);
        globalregisterPatientList = new ArrayList<>();
        mInstance = this;
        siteurl = context.getString(R.string.app_url); //"https://devgv.medijunction.co.in/";
        //context.startService(new Intent(context, TempService.class));
        Log.d(TAG,"Global class OnCreate");

    }


    public static synchronized Global getInstance() {
        return mInstance;
    } public static synchronized Global getInstance(Context coxt) {
        context=coxt;
        if(mInstance == null) {
            mInstance = new Global();
        }
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

    public static boolean IsNotNull(Object object) {

        return object != null && !object.equals("null") && !object.equals("");

    }

    public static String getFormatedDate(String date)
    {
        String outputDate = "";
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        SimpleDateFormat output = new SimpleDateFormat("M/dd/yyyy");

        Date d = null;
        try
        {
            d = input.parse(date);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        if (IsNotNull(d)) {
            outputDate = output.format(d);
        }
        return  outputDate;
    }

    public static String getFormatedTime(String date)
    {
        String outputTime = "";
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        SimpleDateFormat output = new SimpleDateFormat("HH:mm a");

        Date d = null;
        try
        {
            d = input.parse(date);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        if (IsNotNull(d)) {
            outputTime = output.format(d);
        }
        return  outputTime;
    }

    public static String computeHash(String input) throws NoSuchAlgorithmException,
            UnsupportedEncodingException
    {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.reset();

        byte[] byteData = digest.digest(input.getBytes("UTF-8"));
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16)
                    .substring(1));
        }
        return sb.toString();
    }


    public static String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getJsonString(Object object)
    {
        Gson gson = new Gson();
        String json = gson.toJson(object);
        return json;
    }

    public static Object getObjectFromJson(String jsonString)
    {
        Gson gson = new Gson();
        Object obj = gson.fromJson(jsonString, Object.class);

        return obj;
    }

    public static User getUserDetails(Context ctx) {
        try {

            Gson gson = new Gson();
            mPrefs = SPreferenceManager.getInstance(ctx);
            String json = mPrefs.getStringValue(context.getString(R.string.share_userdetails), "");
            User user = gson.fromJson(json, User.class);
            return user;
        } catch (Exception e)
        {
            e.getMessage();
            return null;
        }


    }
    public static User getUserDetails() {
        try {

            Gson gson = new Gson();
            String json = mPrefs.getStringValue(context.getString(R.string.share_userdetails), "");
            User user = gson.fromJson(json, User.class);
            return user;
        } catch (Exception e)
        {
            e.getMessage();
            return null;
        }


    }
    public static boolean isInternetOn(Context context) {
        ConnectivityManager connec = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connec.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static boolean logoutUser()
    {
        try
        {
            mPrefs.logOut();
            return true;
        }
        catch (Exception e)
        {
            return false;
        }


    }

    public static String GenerateUniqueId(String Pincode)
    {
        SecureRandom random = new SecureRandom();
        int num = random.nextInt(100000000);
        String uniqueId = String.format("%08d", num);
        if (Global.IsNotNull(Pincode))
        {
            /*String centerName = Pincode + ClinicId;
            centerName = centerName.replace("-", "");
            centerName = centerName.replace("_", "");
            if (centerName.length() >= 9)
                centerName = centerName.substring(0, 9);*/
            uniqueId = Pincode + uniqueId;
        }

        return uniqueId;
    }



    public static String getPinCode(Context ctx)
    {
        User user = getUserDetails(ctx);
        if(IsNotNull(user))
        {
            return user.getPincode();
        }
        else
        {
            return "";
        }
    }

/*
    public static String getClinicId()
    {
        User user = getUserDetails();
        if(IsNotNull(user))
        {
            return user.getClinicID();
        }
        else
        {
            return "";
        }
    }
*/

    public static String getAsscii(int count)
    {
        int a = 65+count;
        char c = (char)a;

        String asciiUniqueId = ""+c;
        return asciiUniqueId;
    }

    public static Uri getCapturePictureFile(){
        File profilePictureFile = new File(Environment.getExternalStorageDirectory(), "profilecapture.jpeg");

        return Uri.fromFile(profilePictureFile);
    }

    public static Uri getProfilePictureFileUri(Context context){

        File profilePictureTmpFile = new File(context.getFilesDir(), "profilepic_tmp.jpeg");

        if (profilePictureTmpFile.exists()) profilePictureTmpFile.delete();

        return Uri.fromFile(profilePictureTmpFile);
    }


    public static String getColoredSpanned(String text, String color)
    {
        String input = "<font color=" + color +">" + text + "</font>";
        return input;
    }

    public static String getsmallColoredSpanned(String text, String color)
    {
        String input = "<small><font color=" + color +">" + text + "</font></small>";
        return input;
    }

    public static String getStrikeSpanned(String text)
    {
        String input = "<small><strike>" + text + "</strike></small>";
        return input;
    }

    public static String getBigTextSpanned(String text)
    {
        String input = "<big>" + text + "</big>";
        return input;
    }

    // Calculating sampling size to compress image
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }


    public static int getAge(int DOByear, int DOBmonth, int DOBday) {

        int age;

        final Calendar calenderToday = Calendar.getInstance();
        int currentYear = calenderToday.get(Calendar.YEAR);
        int currentMonth = 1 + calenderToday.get(Calendar.MONTH);
        int todayDay = calenderToday.get(Calendar.DAY_OF_MONTH);

        age = currentYear - DOByear;

        if(DOBmonth > currentMonth) {
            --age;
        } else if(DOBmonth == currentMonth) {
            if(DOBday > todayDay){
                --age;
            }
        }
        return age;
    }

    public static int getDOByear(int age) {
        final Calendar calenderToday = Calendar.getInstance();
        int currentYear = calenderToday.get(Calendar.YEAR);
        int DOByear = currentYear - (age+1);
        return DOByear;
    }

    @SuppressLint("HardwareIds")
    public static String getAndroidSecureId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static Map<String, String> generateHeader(Context context) {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put(Constants.HEADER_KEYS.TOKEN, SPreferenceManager.getInstance(context).getStringValue(SPreferenceManager.PREF_TOKEN));
        headerMap.put(Constants.HEADER_KEYS.DEVICEID, SPreferenceManager.getInstance(context).getStringValue(SPreferenceManager.PREF_DEVICEID));
        headerMap.put(Constants.HEADER_KEYS.USERID, SPreferenceManager.getInstance(context).getStringValue(SPreferenceManager.PREF_USERID));
        return headerMap;
    }

}
