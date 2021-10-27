package com.smileparser.medijunctions;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.smileparser.medijunctions.bean.ApiResponse;
import com.smileparser.medijunctions.bean.User;
import com.smileparser.medijunctions.bean.VitalResponse;
import com.smileparser.medijunctions.customviews.LoadingDialog;
import com.smileparser.medijunctions.interfaces.ApiInterface;
import com.smileparser.medijunctions.utils.ApiClient;
import com.smileparser.medijunctions.utils.Global;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AshaConnectActivity extends AppCompatActivity {

    // Application Login Credentials

//    Login info:
//    Health Officer: nazimahmad8587@gmail.com / nazim1234
//    Doctor: rupadivatia@yahoo.co.in / MediDoctor2019
//    Virtual Health Officer(Patient): anuju@gmail.com / anuj


    private static final int REQUEST_ENABLE_BT = 1;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private BluetoothSocket connectedBluetoothSocket;
    private InputStream connectedInputStream;
    private OutputStream connectedOutputStream;
    BluetoothAdapter bluetoothAdapter;
    int bytes;
    FileOutputStream fos = null;

    int i = 0;
    int endofFileDetect = 0;
    byte[] firstChar = new byte[1];
    int writetoFile = 0;
    int k = 0;
    long finalbytes = 0;
    boolean startdetect = false;
    int byteCnt = 0;
    boolean j = true;
    boolean timeOut = false;
    byte[] buf = null; //new byte[75000];


    private static final int REQUEST_FOR_PERMISSIONS = 20;
    File stethPath = Environment.getExternalStorageDirectory();
    File steth = new File(stethPath, "/medijunction/steth.wav");


    File steth_filter = new File(stethPath, "/medijunction/steth_filter.wav");

    // Pradeep code /****************************************/
    MainFilter audio_filter = new MainFilter();
    /********************************************************/
//


    private ArrayAdapter<String> pairedListAdapter;
    private ArrayList<BluetoothSocket> m_bluetoothSockets = new ArrayList<BluetoothSocket>();
    File ecgfiles = new File("/storage/emulated/0/medijunction/ecg.txt");
    File stetfiles = new File("/storage/emulated/0/medijunction/steth_filter.wav");

    ArrayList<BluetoothDevice> pairedDeviceArrayList;

    TextView textInfo, textStatus;
    ListView listViewPairedDevice;
    LinearLayout inputPane;
    ScrollView inputpaneScrollView;
    EditText inputField;
    Button stop, btnSend, all_results;
    ImageView stetescope, temprature, blood_pressure, torch, ecg_results, glucose_results, spo;

    // shud code to store all responses in strings for sending data to server.
    String tempratureValue = "";
    String stetescopeValue = "";
    String bloodPressureValue = "";
    String torchValue = "";
    String ecgValue = "";
    String spoValue = "";
    String glucoseValue = "";
    String valuesArray[];

    TextView stetescope_response, temprature_response, blood_pressure_response, torch_response, ecg_results_response, spo_response, glucose_results_response, stop_response, all_results_response;
    ArrayAdapter<BluetoothDevice> pairedDeviceAdapter;
    private UUID myUUID;
    private final String UUID_STRING_WELL_KNOWN_SPP =
            "00001101-0000-1000-8000-00805F9B34FB";
    String stest = "s";
    Button submit;
    String clicked_value;
    String patientid;
    ThreadConnectBTdevice myThreadConnectBTdevice;
    ThreadConnected myThreadConnected;

    User user;
    String vitalid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asha_connect);

        if (checkRunTimePermission()) {
            String dir_path = Environment.getExternalStorageDirectory() + "/medijunction";

            if (!dir_exists(dir_path)) {
                File directory = new File(dir_path);
                directory.mkdirs();
            }
        }


        Intent intent = getIntent();
        patientid = intent.getStringExtra("patientid");
        textInfo = (TextView) findViewById(R.id.info);
        textStatus = (TextView) findViewById(R.id.status);
        stetescope_response = findViewById(R.id.stetescope_response);
        spo = findViewById(R.id.spo);
        submit = findViewById(R.id.submit);
        spo_response = findViewById(R.id.spo_response);
        temprature_response = findViewById(R.id.temprature_response);
        blood_pressure_response = findViewById(R.id.blood_pressure_response);
        torch_response = findViewById(R.id.torch_response);
        ecg_results_response = findViewById(R.id.ecg_results_response);
        glucose_results_response = findViewById(R.id.glucose_results_response);
        stop_response = findViewById(R.id.stop_response);
        all_results_response = findViewById(R.id.all_results_response);

        listViewPairedDevice = (ListView) findViewById(R.id.pairedlist);
        pairedListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        listViewPairedDevice.setAdapter(pairedListAdapter);
        inputPane = (LinearLayout) findViewById(R.id.inputpane);
        inputpaneScrollView = (ScrollView) findViewById(R.id.inputpaneScrollView);
        inputField = (EditText) findViewById(R.id.input);
        btnSend = (Button) findViewById(R.id.send);
        stetescope = (ImageView) findViewById(R.id.stetescope);
        temprature = (ImageView) findViewById(R.id.temprature);
        torch = (ImageView) findViewById(R.id.torch);
        blood_pressure = (ImageView) findViewById(R.id.blood_pressure);
        all_results = (Button) findViewById(R.id.all_results);
        ecg_results = (ImageView) findViewById(R.id.ecg_results);
        glucose_results = (ImageView) findViewById(R.id.glucose_results);
        stop = (Button) findViewById(R.id.stop);
        all_results.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = "r";
                clicked_value = s;
                byte[] bytesToSend = s.getBytes();
                myThreadConnected.write(bytesToSend);
            }
        });
        ecg_results.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ecg_results.setBackgroundResource(R.color.menucolor);
                ecg_results_response.setText("Fetching...");

                String s = "e";
                clicked_value = s;
                byte[] bytesToSend = s.getBytes();

                //char[] buffer = new char[15000];
                buf = new byte[75000];
                //byte[] buffer2 = new byte[128];
                String recvdString = "";
                i = 0;
                k = 0;
                byteCnt = 0;
                j = true;
                //SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                //Log.d("BluetoothPlugin", "StartTime: " + format.format(startTime));
                timeOut = false;
                myThreadConnected.write(bytesToSend);
                // bluetoothecg(s);
            }
        });
        spo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spo.setBackgroundResource(R.color.menucolor);
                spo_response.setText("Fetching...");

                String s = "p";
                clicked_value = s;
                byte[] bytesToSend = s.getBytes();
                myThreadConnected.write(bytesToSend);
            }
        });
        glucose_results.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                glucose_results.setBackgroundResource(R.color.menucolor);
                glucose_results_response.setText("Fetching...");

                String s = "g";
                clicked_value = s;
                byte[] bytesToSend = s.getBytes();
                myThreadConnected.write(bytesToSend);
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = "x";
                clicked_value = s;

                byte[] bytesToSend = s.getBytes();
                bluetooth(s);

                myThreadConnected.write(bytesToSend);

            }
        });
        stetescope.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (stest.equalsIgnoreCase("s")) {
                    stetescope.setBackgroundResource(R.color.menucolor);
                    stetescope_response.setText("Please press again to stop");
                    String s = "s";
                    clicked_value = s;
//                    bluetooth(s);
                    stest = "x";
                    byte[] bytesToSend = s.getBytes();
                    myThreadConnected.write(bytesToSend);
                } else if (stest.equalsIgnoreCase("x")) {
                    stetescope_response.setText("" + stetfiles);

                    if (stetfiles.exists()) {
                        stetescopeValue = "exist";
                    } else {
                        stetescopeValue = "";
                    }

                    String s = "x";
                    clicked_value = s;
                    stest = "s";
                    byte[] bytesToSend = s.getBytes();
                    //bluetooth(s);
                    i = 0;
                    endofFileDetect = 0;

                    writetoFile = 0;
                    k = 0;
                    finalbytes = 0;
                    startdetect = false;
                    byteCnt = 0;
                    firstChar[0] = 0x52;
                    j = true;
                    try {
                        steth.createNewFile();
                        fos = new FileOutputStream(steth);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    myThreadConnected.write(bytesToSend);
                }

            }
        });
        temprature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temprature.setBackgroundResource(R.color.menucolor);
                temprature_response.setText("Fetching...");

                String s = "m";
                clicked_value = s;
                byte[] bytesToSend = s.getBytes();
                myThreadConnected.write(bytesToSend);
            }
        });
        torch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                torch.setBackgroundResource(R.color.menucolor);

                String s = "t";
                clicked_value = s;
                byte[] bytesToSend = s.getBytes();
                myThreadConnected.write(bytesToSend);
            }
        });
        blood_pressure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blood_pressure.setBackgroundResource(R.color.menucolor);
                blood_pressure_response.setText("Fetching...");
                String s = "b";
                clicked_value = s;
                byte[] bytesToSend = s.getBytes();
                myThreadConnected.write(bytesToSend);
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (myThreadConnected != null) {
                    byte[] bytesToSend = inputField.getText().toString().getBytes();
                    myThreadConnected.write(bytesToSend);
                }
            }
        });

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
            Toast.makeText(this,
                    "FEATURE_BLUETOOTH NOT support",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        //using the well-known SPP UUID
        myUUID = UUID.fromString(UUID_STRING_WELL_KNOWN_SPP);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this,
                    "Bluetooth is not supported on this hardware platform",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        String stInfo = bluetoothAdapter.getName() + "\n" +
                bluetoothAdapter.getAddress();
        textInfo.setText(stInfo);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                valuesArray= new String[] {tempratureValue,spoValue,bloodPressureValue,glucoseValue,stetescopeValue,ecgValue};
                final String  staticvaluesArray[] = new String[] {"Temperature","SPO2 and Pulse","Blood Pressure","Blood Glucose","Stethoscope","ECG"};

                String msgString = "";
                String titleString = "Please check the records before uploading";

                for (int i=0;i<valuesArray.length;i++){

                    if (valuesArray[i].length()>1) {
                        if (i == 0) {
                            msgString = msgString + staticvaluesArray[i];
                        } else {
                            msgString = msgString+", " + staticvaluesArray[i];
                        }
                    }
                }

                if (msgString.length() < 1) {
                    titleString = "Sorry no records available";

                    new AlertDialog.Builder(AshaConnectActivity.this)
                            .setTitle(titleString)
                            .setCancelable(false)


                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Continue with delete operation

                                }
                            })


                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                } else {
                    new AlertDialog.Builder(AshaConnectActivity.this)
                            .setTitle(titleString)
                            .setMessage(msgString)
                            .setCancelable(false)


                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Continue with delete operation

                                    user = Global.getUserDetails();

                                    new getUser().execute();
                                }
                            })

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }

            }
        });

    }


    // Shud code for applying run time permissions
    public boolean checkRunTimePermission() {
        Context context;
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.MODIFY_AUDIO_SETTINGS) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN}, REQUEST_FOR_PERMISSIONS);
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FOR_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED
                        && grantResults[3] == PackageManager.PERMISSION_GRANTED && grantResults[4] == PackageManager.PERMISSION_GRANTED && grantResults[5] == PackageManager.PERMISSION_GRANTED && grantResults[6] == PackageManager.PERMISSION_GRANTED) {
                    //  shud code to create the application folder once permission is granted.
                    String dir_path = Environment.getExternalStorageDirectory() + "/medijunction";

                    if (!dir_exists(dir_path)) {
                        File directory = new File(dir_path);
                        directory.mkdirs();
                    }

                } else {
                    new AlertDialog.Builder(this)
                            .setTitle("Warning")
                            .setMessage("Without permission we can't consulting you. Please allow the permissions you want to allow ?")
                            .setCancelable(false)
                            .setPositiveButton("[YES]", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    checkRunTimePermission();
                                }

                            })
                            .setNegativeButton("[NO]", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    onBackPressed();
                                }
                            })
                            .show();
                }
                break;
        }
    }


    public boolean dir_exists(String dir_path) {
        boolean ret = false;
        File dir = new File(dir_path);
        if (dir.exists() && dir.isDirectory())
            ret = true;
        return ret;
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Turn ON BlueTooth if it is OFF
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }

        setup();
    }

    private void setup() {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            pairedDeviceArrayList = new ArrayList<BluetoothDevice>();
            for (BluetoothDevice device : pairedDevices) {
                pairedListAdapter.add(device.getName());
                pairedDeviceArrayList.add(device);
                Log.e("pairedDeviceArrayList", "" + device);
            }

            /*pairedDeviceAdapter = new ArrayAdapter<BluetoothDevice>(this,
                    android.R.layout.simple_list_item_1, pairedDeviceArrayList);
            listViewPairedDevice.setAdapter(pairedDeviceAdapter);*/

            listViewPairedDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    if (pairedDeviceArrayList.get(position).toString().equals("34:81:F4:5C:2A:34")) {
                        BluetoothDevice device =
                                (BluetoothDevice) pairedDeviceArrayList.get(position);
                        Toast.makeText(AshaConnectActivity.this,
                                "Please wait connecting with " + device.getName() ,
                                Toast.LENGTH_SHORT).show();

                        //   textStatus.setText("start ThreadConnectBTdevice");
                        myThreadConnectBTdevice = new ThreadConnectBTdevice(device);
                        myThreadConnectBTdevice.start();
                    } else {
                        Toast.makeText(AshaConnectActivity.this, "Something went wrong! Please Connect to ASHA + bluetooth device", Toast.LENGTH_SHORT).show();

                    }

                }
            });
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (myThreadConnectBTdevice != null) {
            myThreadConnectBTdevice.cancel();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                setup();
            } else {
                Toast.makeText(this,
                        "BlueTooth NOT enabled",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    //Called in ThreadConnectBTdevice once connect successed
    //to start ThreadConnected
    private void startThreadConnected(BluetoothSocket socket) {

        myThreadConnected = new ThreadConnected(socket);
        myThreadConnected.start();
    }

    /*
    ThreadConnectBTdevice:
    Background Thread to handle BlueTooth connecting
    */
    private class ThreadConnectBTdevice extends Thread {

        private BluetoothSocket bluetoothSocket = null;
        private final BluetoothDevice bluetoothDevice;

        String device_name;


        private ThreadConnectBTdevice(BluetoothDevice device) {
            bluetoothDevice = device;
            device_name = device.getName();

            try {
                bluetoothSocket = device.createRfcommSocketToServiceRecord(myUUID);
                // textStatus.setText("bluetoothSocket: \n" + bluetoothSocket);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            boolean success = false;
            try {
                bluetoothSocket.connect();
                m_bluetoothSockets.add(bluetoothSocket);
                success = true;
            } catch (IOException e) {
                e.printStackTrace();

                final String eMessage = e.getMessage();
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        textStatus.setText("Please check Asha + device is On or not.");
                        Toast.makeText(AshaConnectActivity.this,
                                "Please check Asha + device is On or not.",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                try {
                    bluetoothSocket.close();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }

            if (success) {
                //connect successful
                final String msgconnected = "Connected successfully to ASHA+:\n"
                        + "Connected to: " + device_name;

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        textStatus.setText(msgconnected);
                        listViewPairedDevice.setVisibility(View.GONE);
                        inputPane.setVisibility(View.VISIBLE);
                        inputpaneScrollView.setVisibility(View.VISIBLE);
                    }
                });

                startThreadConnected(bluetoothSocket);
            } else {
                //fail
            }
        }

        public void cancel() {

            Toast.makeText(getApplicationContext(),
                    "close bluetoothSocket",
                    Toast.LENGTH_LONG).show();

            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

    }

    /*
    ThreadConnected:
    Background Thread to handle Bluetooth data communication
    after connected
     */
    private class ThreadConnected extends Thread {


        public ThreadConnected(BluetoothSocket socket) {
            connectedBluetoothSocket = socket;
            InputStream in = null;
            OutputStream out = null;

            try {
                in = socket.getInputStream();
                out = socket.getOutputStream();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            connectedInputStream = in;
            connectedOutputStream = out;
        }

        @Override
        public void run() {
            byte[] buffer = new byte[2048];

            while (true) {
                if (connectedBluetoothSocket != null) {
                    try {
                        bytes = connectedInputStream.read(buffer);
                        Log.d("connectedInputStream", "" + bytes);

                        String strReceived = new String(buffer, 0, bytes);
                        final String msgReceived = /*String.valueOf(bytes) +
                            " bytes received:\n"*/
                                strReceived;

                        int finalBytes = bytes;
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                //Log.e("", "" + msgReceived);
                                if (clicked_value.equalsIgnoreCase("b")) {
                                    //blood_pressure_response.setText(msgReceived);
                                    String myString = msgReceived;
                                    String[] splitString = myString.split("_");
                                    blood_pressure_response.setText("" + splitString[1] + " / " + splitString[2]);

                                    if (myString.contains("_")) {
                                        bloodPressureValue = "" + splitString[1] + " / " + splitString[2];
                                    } else {
                                        bloodPressureValue = "";
                                    }

                                } else if (clicked_value.equalsIgnoreCase("s")) {
                                    String msg = msgReceived;
                                    stetescope_response.setText(msgReceived);

                                    // callbackContext.sendPluginResult(pluginResult);

                                } else if (clicked_value.equalsIgnoreCase("m")) {
                                    String myString = msgReceived;
                                    String[] splitString = myString.split("_");
                                    temprature_response.setText("" + splitString[1] + "\u2103 , " + splitString[2]+"\u2109");
                                    if (myString.contains("_")) {
                                        tempratureValue = "" + splitString[1] + " / " + splitString[2];
                                    } else {
                                        tempratureValue = "";
                                    }
                                } else if (clicked_value.equalsIgnoreCase("t")) {
                                    //   torch_response.setText(msgReceived);
                                    String myString = msgReceived;
                                    String[] splitString = myString.split("_");
                                    torch_response.setText("" + splitString[1]);
                                    if (myString.contains("_")) {
                                        torchValue = "" + splitString[1];
                                    } else {
                                        torchValue = "";
                                    }

                                } else if (clicked_value.equalsIgnoreCase("e")) {

                                    try {

                                        if (msgReceived.equalsIgnoreCase("k")) {
                                            ecg_results_response.setText("Please Check Cable");
                                        }

                                        //int socketId = args.getInt(0);
                                        Calendar cal = Calendar.getInstance();

                                        Date startTime = cal.getTime();

                                        String recvdString = "";

                                        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                        //Log.d("BluetoothPlugin", "StartTime: " + format.format(startTime));

                                        if (j) {
                                            Calendar newCal = Calendar.getInstance();
                                            Date endTime = newCal.getTime();

                                            if ((endTime.getTime() - startTime.getTime()) < 2000) {

                                                if (bytes > 0) {
                                                    startTime = endTime;
                                                    //	Log.d( "BluetoothPlugin", "Time Increment: " + format.format(endTime));
                                                    for (k = 0; k < bytes; k++) {
                                                        buf[k + i] = buffer[k];
                                                    }
                                                    i += bytes; //connectedInputStream.read(buf, k, connectedInputStream.available());
                                                    //k = i;
                                                    Log.d("BluetoothPlugin", "i=" + i);
                                                    Log.d("BluetoothPlugin", "buf[0]=" + buf[0]);
                                                }
                                                //Log.d( "BluetoothPlugin", "i="+dataInputStream);
                                                //inputStream.close(); 63975
                                                if (((i > 51180) && (buf[0] == 'K')) || ((i > 63975) && (buf[0] == 'W'))) {
                                                    //Log.d( "BluetoothPlugin", "i="+i);
                                                    j = false;

                                                    File ecgPath = Environment.getExternalStorageDirectory();
                                                    File ecg = new File(ecgPath, "/medijunction/ecg.txt");
                                                    ecg.createNewFile();

                                                    FileWriter fos = new FileWriter(ecg, false);
                                                    Log.d("ecg.txt", "File: " + ecg + "ecgPath");

                                                    String stringBuf = new String("");
                                                    //long byteCnt
                                                    byteCnt = (i - 1) / 3;
                                                    long[] buf2 = new long[byteCnt];

                                                    for (k = 0; k < byteCnt; k++) {

                                                        int firstByte = 0;
                                                        int secondByte = 0;
                                                        int thirdByte = 0;
                                                        int fourthByte = 0;
                                                        int index = k * 3;
                                                        firstByte = (0x000000FF & ((int) buf[index + 1]));
                                                        secondByte = (0x000000FF & ((int) buf[index + 2]));
                                                        thirdByte = (0x000000FF & ((int) buf[index + 3]));
                                                        buf2[k] = ((long) (firstByte << 16
                                                                | secondByte << 8
                                                                | thirdByte
                                                        ))
                                                                & 0xFFFFFFFFL;

                                                        stringBuf = buf2[k] + ",";
                                                        fos.write(stringBuf);
                                                    }

                                                    fos.flush();
                                                    fos.close();
                                                    byteCnt = i;
                                                    if (buf[0] == 'K') recvdString = "3LEAD";
                                                    else recvdString = "12LEAD";
                                                    ecg_results_response.setText("" + ecg);
                                                    if (ecg.exists()) {
                                                        ecgValue = "exist";
                                                    } else {
                                                        ecgValue = "";
                                                    }

                                                    //i++;
                                                }
                                            } else {
                                                j = false;
                                                timeOut = true;
                                                Log.d("BluetoothPlugin", "ECG Read TimeOut");
                                            }


                                        }
                                        if (timeOut) {
                                            recvdString = "Aborted";

                                        } else {

                                            // recvdString = ecg.getPath();
                                        }

                                    } catch (Exception e) {
                                        Log.e("BluetoothPlugin", e.toString() + " / " + e.getMessage());

                                    }


                                } else if (clicked_value.equalsIgnoreCase("g")) {
                                    // glucose_results_response.setText(msgReceived);
                                    String myString = msgReceived;
                                    String[] splitString = myString.split("_");
                                    glucose_results_response.setText("" + splitString[1] + " / " + splitString[2]);

                                    if (myString.contains("_")) {
                                        glucoseValue = "" + splitString[1] + " / " + splitString[2];
                                    } else {
                                        glucoseValue = "";
                                    }


                                } else if (clicked_value.equalsIgnoreCase("r")) {
                                    all_results_response.setText(msgReceived);

                                } else if (clicked_value.equalsIgnoreCase("x")) {

                                    try {
                                        int a = 1;
                                        if (a == 1) {
                                            Log.e("connectedsocket name", "" + connectedBluetoothSocket);

                                            Calendar cal = Calendar.getInstance();
                                            //byte [] buf = new byte[245000];
                                            Date startTime = cal.getTime();
                                            String recvdString = "";

                                            if (j) {
                                                Calendar newCal = Calendar.getInstance();
                                                Date endTime = newCal.getTime();
                                                // Log.d("endTime", "" + (endTime.getTime() - startTime.getTime()));

                                                /*if ((endTime.getTime() - startTime.getTime()) < 2000) {*/
                                                if (bytes > 0) {
                                                    startTime = endTime;
                                                    //Log.d( "BluetoothPlugin", "inputStream.available="+inputStream.available());
                                                    //byte [] buf = new byte[inputStream.available()];
                                                    k = bytes;
                                                    //Log.d("BluetoothPlugink", "" + k);
                                                    if ((writetoFile == 0)) {
                                                        Log.d("BluetoothPluginbuf", "" + buffer[0]);

                                                        if ((buffer[0] & 0xFF) == 0x52) {

                                                            if (k > 1) {
                                                                if ((buffer[1] & 0xFF) == 0x49) {
                                                                    writetoFile = 1;
                                                                    Log.d("BluetoothPluginwri", "" + writetoFile);
                                                                    i = 0;
                                                                }

                                                            } else {

                                                                startdetect = true;
                                                                Log.d("startdetect", "" + startdetect);

                                                            }

                                                        } else if (((buffer[0] & 0xFF) == 0x49) && startdetect == true) {
                                                            Log.d("BluetoothPluginwri", "" + buffer[0]);

                                                            fos.write(firstChar, 0, 1);
                                                            writetoFile = 1;
                                                            i = 0;
                                                        } else {
                                                            startdetect = false;
                                                            Log.d("startdetect", "" + startdetect);
                                                        }
                                                    }
                                                    if (writetoFile == 1) {

                                                        i += k;
                                                        Log.d("BluetoothPlugin", "i=" + i);
                                                        //Log.d( "BluetoothPlugin", "k="+k);
                                                        fos.write(buffer, 0, k);
                                                        //if (k>1)Log.d( "BluetoothPlugin", "buf[k-2]="+Integer.toHexString(buf[k-2]&0xFF));
                                                        //Log.d( "BluetoothPlugin", "buf[k-1]="+Integer.toHexString(buf[k-1]&0xFF));
                                                        if ((k > 1) && ((buffer[k - 2] & 0xFF) == 0xAA) && ((buffer[k - 1] & 0xFF) == 0xBB)) {
                                                            endofFileDetect = 2;
                                                            //	Log.d( "BluetoothPlugin", "EoF Detected Multibyte");

                                                        } else if ((k == 1) && ((buffer[0] & 0xFF) == 0xAA)) {
                                                            endofFileDetect = 1;
                                                            //	Log.d( "BluetoothPlugin", "EoF Detected Firstbyte");
                                                        } else if (((buffer[0] & 0xFF) == 0xBB) && (endofFileDetect == 1)) {
                                                            endofFileDetect += 1;
                                                            //		Log.d( "BluetoothPlugin", "EoF Detected Sectbyte");
                                                        } else {
                                                            endofFileDetect = 0;
                                                        }

                                                        //Log.d("endofFileDetect", ""+endofFileDetect);

                                                        if (endofFileDetect == 2) {
                                                            Log.d("BluetoothPlugin", "File Write Complete");
                                                            //Log.d( "BluetoothPlugin", "i="+i);
                                                            fos.flush();
                                                            fos.close();
                                                            stetescope_response.setText("" + steth);
                                                            j = false;
                                                            //i++;
                                                            // Pradeep code /****************************************/

                                                            audio_filter.filter(stethPath + "/medijunction/steth", 2); // second paramenter ***option*** = 1 for 900 hz filtering ***option*** =2 for 1000 hz filtering
                                                            /********************************************************/
                                                            Log.d("BluetoothPlugin", "File: " + stethPath + "/medijunction/steth");
                                                            recvdString = steth_filter.getPath();
                                                            stetescope_response.setText("" + stetfiles);
                                                            if (stetfiles.exists()) {
                                                                stetescopeValue = "exist";
                                                            } else {
                                                                stetescopeValue = "";
                                                            }

                                                        }


                                                    } else {

                                                    }
                                                    //	DatagramPacket p = new DatagramPacket(buf, k,local,server_port);
                                                    //	s.send(p);//					DataInputStream dataInputStream = new DataInputStream(inputStream);
                                                } else {

                                                }
                                                //Log.d( "BluetoothPlugin", "i="+dataInputStream);
                                                //inputStream.close();


                                            }
                                        } else {

                                        }
                                    } catch (Exception e) {
                                        String eMessage = e.getMessage();
                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                } else if (clicked_value.equalsIgnoreCase("p")) {
                                    //  spo_response.setText(msgReceived);
                                    String myString = msgReceived;
                                    String[] splitString = myString.split("_");
                                    spo_response.setText("SPO2:" + splitString[1] + "% , Pulse:" + splitString[2]+"bpm");

                                    if (myString.contains("_")) {
                                        spoValue = "" + splitString[1] + " / " + splitString[2];
                                    } else {
                                        spoValue = "";
                                    }

                                }
                                //   textStatus.setText(msgReceived);
                            }
                        });

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();

                        final String msgConnectionLost = "Connection lost:\n"
                                + e.getMessage();
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                textStatus.setText(msgConnectionLost);
                            }
                        });
                    }
                }
            }
        }

        public void write(byte[] buffer) {
            try {
                connectedOutputStream.write(buffer);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        public void cancel() {
            try {
                connectedBluetoothSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void bluetooth(String q) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (connectedBluetoothSocket != null) {
                        Log.e("connectedsocket name", "" + connectedBluetoothSocket);
                        connectedBluetoothSocket.getOutputStream().write(q.toString().getBytes());

                        // break;
                        InputStream inputStream = connectedBluetoothSocket.getInputStream();
                        Log.e("connectedsocketinput", "" + inputStream);

                        Calendar cal = Calendar.getInstance();
                        //byte [] buf = new byte[245000];
                        Date startTime = cal.getTime();
                        String recvdString = "";
                        int i = 0;
                        int endofFileDetect = 0;
                        byte[] firstChar = new byte[1];
                        int writetoFile = 0;
                        int k = 0;
                        long finalbytes = 0;
                        boolean startdetect = false;
                        int byteCnt = 0;
                        boolean j = true;
                        boolean ecgRec = false;
                        byte[] buf = new byte[20000];
                        firstChar[0] = 0x52;
                        File stethPath = Environment.getExternalStorageDirectory();
                        File steth = new File(stethPath, "/medijunction/steth.wav");

                        File steth_filter = new File(stethPath, "/medijunction/steth_filter.wav");
                        FileOutputStream fos = new FileOutputStream(steth);

                        // Pradeep code /****************************************/
                        MainFilter audio_filter = new MainFilter();
                        /********************************************************/
                        while (j) {
                            Calendar newCal = Calendar.getInstance();
                            Date endTime = newCal.getTime();
                            // Log.d("endTime", "" + (endTime.getTime() - startTime.getTime()));

                            /*if ((endTime.getTime() - startTime.getTime()) < 2000) {*/
                            if (inputStream.available() > 0) {
                                startTime = endTime;
                                //Log.d( "BluetoothPlugin", "inputStream.available="+inputStream.available());
                                //byte [] buf = new byte[inputStream.available()];
                                k = inputStream.read(buf, 0, inputStream.available());
                                Log.d("BluetoothPlugink", "" + k);
                                if ((writetoFile == 0)) {
                                    Log.d("BluetoothPluginbuf", "" + buf[0]);

                                    if ((buf[0] & 0xFF) == 0x52) {

                                        if (k > 1) {
                                            if ((buf[1] & 0xFF) == 0x49) {
                                                writetoFile = 1;
                                                Log.d("BluetoothPluginwri", "" + writetoFile);

                                                i = 0;
                                            }

                                        } else {

                                            startdetect = true;
                                            Log.d("startdetect", "" + startdetect);

                                        }

                                    } else if (((buf[0] & 0xFF) == 0x49) && startdetect == true) {
                                        Log.d("BluetoothPluginwri", "" + buf[0]);

                                        fos.write(firstChar, 0, 1);
                                        writetoFile = 1;
                                        i = 0;
                                    } else {
                                        startdetect = false;
                                        Log.d("startdetect", "" + startdetect);

                                    }
                                }
                                if (writetoFile == 1) {

                                    i += k;
                                    //Log.d( "BluetoothPlugin", "i="+i);
                                    //Log.d( "BluetoothPlugin", "k="+k);
                                    fos.write(buf, 0, k);
                                    //if (k>1)Log.d( "BluetoothPlugin", "buf[k-2]="+Integer.toHexString(buf[k-2]&0xFF));
                                    //Log.d( "BluetoothPlugin", "buf[k-1]="+Integer.toHexString(buf[k-1]&0xFF));
                                    if ((k > 1) && ((buf[k - 2] & 0xFF) == 0xAA) && ((buf[k - 1] & 0xFF) == 0xBB)) {
                                        endofFileDetect = 2;
                                        //	Log.d( "BluetoothPlugin", "EoF Detected Multibyte");

                                    } else if ((k == 1) && ((buf[0] & 0xFF) == 0xAA)) {
                                        endofFileDetect = 1;
                                        //	Log.d( "BluetoothPlugin", "EoF Detected Firstbyte");
                                    } else if (((buf[0] & 0xFF) == 0xBB) && (endofFileDetect == 1)) {
                                        endofFileDetect += 1;
                                        //		Log.d( "BluetoothPlugin", "EoF Detected Sectbyte");
                                    } else {
                                        endofFileDetect = 0;
                                    }

                                    Log.d("endofFileDetect", "" + endofFileDetect);

                                    if (endofFileDetect == 2) {
                                        Log.d("BluetoothPlugin", "File Write Complete");
                                        //Log.d( "BluetoothPlugin", "i="+i);
                                        fos.flush();
                                        fos.close();
                                        stetescope_response.setText("" + steth);
                                        j = false;
                                        //i++;
                                        // Pradeep code /****************************************/

                                        audio_filter.filter(stethPath + "/medijunction/steth", 2);
                                        // second paramenter ***option*** = 1 for 900 hz filtering ***option*** =2 for 1000 hz filtering
                                        /********************************************************/
                                        Log.d("BluetoothPlugin", "File: " + stethPath + "/medijunction/steth");
                                        recvdString = steth_filter.getPath();

                                    }


                                } else {

                                }
                                //	DatagramPacket p = new DatagramPacket(buf, k,local,server_port);
                                //	s.send(p);//					DataInputStream dataInputStream = new DataInputStream(inputStream);
                            } else {

                            }
                            //Log.d( "BluetoothPlugin", "i="+dataInputStream);
                            //inputStream.close();


                        }
                    } else {

                    }
                } catch (Exception e) {
                    // Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
    }


    private class getUser extends AsyncTask<String, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            LoadingDialog.showLoadingDialog(AshaConnectActivity.this,"Please wait uploading records.");
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

                HashMap<String, String> map = new HashMap<String, String>();
                user = Global.getUserDetails();

                map.put("UserId", user.getId());
                map.put("PatientId", patientid);
                map.put("Temp", tempratureValue);
                map.put("BloodPressure", bloodPressureValue);
                map.put("BloodSugar", glucoseValue);
                map.put("SPO2", spoValue);
                //map.put("IMEINo",params[2]);
                Call<VitalResponse> apiResponseCall = apiInterface.getpatientVitals(map);

                apiResponseCall.enqueue(new Callback<VitalResponse>() {
                    @Override
                    public void onResponse(Call<VitalResponse> call, Response<VitalResponse> response) {


                        if (Global.IsNotNull(response.body())) {
                            if (response.body().getResponse()) {
                                // finish();
                                Gson gson = new Gson();

                                // VitalResponse users = gson.fromJson(response.body().getResult(), VitalResponse.class);
                                vitalid = "" + response.body().getResult().getPatientVitalsID();
                                Log.e("vitalid", "" + response.body().getResult().getPatientVitalsID());
                                if (ecg_results_response.getText().toString().isEmpty() && stetescope_response.getText().toString().isEmpty()) {

                                    try {
                                        LoadingDialog.cancelLoading();
                                        finish();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }


                                } else if (!ecg_results_response.getText().toString().isEmpty() && !stetescope_response.getText().toString().isEmpty()) {
                                    ArrayList<File> files = new ArrayList<>();
                                    files.add(ecgfiles);
                                    uploadImage(patientid, user.getId(), "" + vitalid, files);


                                  /*  try {
                                     //   myThreadConnectBTdevice.cancel();
                                        connectedBluetoothSocket.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    finishAffinity();
 */
                                } else if (ecg_results_response.getText().toString().isEmpty() && !stetescope_response.getText().toString().isEmpty()) {
                                    ArrayList<File> files = new ArrayList<>();
                                    files.add(stetfiles);
                                    // Log.e("patientvitalid",""+users.getResult().getPatientVitalsID());
                                    uploadstetImage(patientid, user.getId(), "" + vitalid, files);

/*
                                    try {
                                        //myThreadConnectBTdevice.cancel();
                                        connectedBluetoothSocket.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    finishAffinity();*/
                                } else if (!ecg_results_response.getText().toString().isEmpty() && stetescope_response.getText().toString().isEmpty()) {
                                    ArrayList<File> files = new ArrayList<>();
                                    files.add(ecgfiles);
                                    uploadImage(patientid, user.getId(), "" + vitalid, files);

                                  /*  try {
                                    //    myThreadConnectBTdevice.cancel();
                                        connectedBluetoothSocket.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    finish();*/
                                } else {

                                   /* try {
                                    //    myThreadConnectBTdevice.cancel();
                                        connectedBluetoothSocket.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    finish();*/
                                }

                            } else {
                                if (Global.IsNotNull(response.body().getMessage())) {
                                    Toast.makeText(AshaConnectActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else if (Global.IsNotNull(response.errorBody())) {
                            JsonParser parser = new JsonParser();
                            JsonElement mJson = null;
                            try {
                                mJson = parser.parse(response.errorBody().string());
                                Gson gson = new Gson();
                                ApiResponse errorResponse = gson.fromJson(mJson, ApiResponse.class);

                                Toast.makeText(AshaConnectActivity.this, "" + errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            } catch (Exception e) {
                                e.getMessage();
                            }

                        } else {
                            Toast.makeText(AshaConnectActivity.this, "" + getString(R.string.somethingwrong), Toast.LENGTH_SHORT).show();
                        }

                   /*     try {
                            LoadingDialog.cancelLoading();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }*/
                    }


                    @Override
                    public void onFailure(Call<VitalResponse> call, Throwable t) {

                        try {
                            LoadingDialog.cancelLoading();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Toast.makeText(AshaConnectActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            } catch (Exception e) {
                e.getMessage();
            }

            return null;
        }

    }

    private void uploadImage(String loggedInUserId, String appointmentId, String patientVitalId, ArrayList<File> files) {
        int count = 1;
       /* RequestBody requestFile = RequestBody
                .create(MediaType.parse("multipart/form-data"), files.get(count));*/
        // MultipartBody.Part is used to send also the actual filename
        RequestBody requestBodyUploadImages = RequestBody.create(MediaType.parse("multipart/form-data"), ecgfiles);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", ecgfiles.getName(), requestBodyUploadImages);


       /* MultipartBody.Part body = MultipartBody.Part
                .createFormData("file", files.get(count).getName(), requestFile);*/
        RequestBody appointmentIdBody = RequestBody
                .create(MediaType.parse("text/plain"), appointmentId);
        RequestBody loggedInUserIdBody = RequestBody
                .create(MediaType.parse("text/plain"), loggedInUserId);
        RequestBody patientVitalIdBody = RequestBody
                .create(MediaType.parse("text/plain"), patientVitalId);
        ApiClient.getClient().create(ApiInterface.class)
                .uploadECGVitals(body, appointmentIdBody, loggedInUserIdBody, patientVitalIdBody)
                .enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call,
                                           Response<ApiResponse> response) {
                        if (response.code() / 100 == 2) {
                            Log.d("ECG", "Ecg file uploaded");

                            if (!ecg_results_response.getText().toString().isEmpty() && !stetescope_response.getText().toString().isEmpty()) {
                                ArrayList<File> files2 = new ArrayList<>();
                                files2.add(stetfiles);
                                uploadstetImage(patientid, user.getId(), "" + vitalid, files2);
                            } else {
                                LoadingDialog.cancelLoading();
                                finish();
                            }

                         /*   Log.d(TAG, response.body().toString());
                            updateProgress(100);
                            count++;*/
//                        addFileToMemory();
                        } else {
                            Log.d("TAG", response.toString());
                            Toast.makeText(AshaConnectActivity.this,
                                    "Unable to add image to memory memory at this time",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        finish();
                       /* Toast.makeText(AshaConnectActivity.this,
                                "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();*/
                    }
                });
    }

    private void uploadstetImage(String loggedInUserId, String appointmentId, String patientVitalId, ArrayList<File> files) {
        int count = 1;

        RequestBody requestBodyUploadImages = RequestBody.create(MediaType.parse("multipart/form-data"), stetfiles);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", stetfiles.getName(), requestBodyUploadImages);


        RequestBody appointmentIdBody = RequestBody
                .create(MediaType.parse("text/plain"), appointmentId);
        RequestBody loggedInUserIdBody = RequestBody
                .create(MediaType.parse("text/plain"), loggedInUserId);
        RequestBody patientVitalIdBody = RequestBody
                .create(MediaType.parse("text/plain"), patientVitalId);
        ApiClient.getClient().create(ApiInterface.class)
                .uploadStethoscopeVitals(body, appointmentIdBody, loggedInUserIdBody, patientVitalIdBody)
                .enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call,
                                           Response<ApiResponse> response) {
                        if (response.code() / 100 == 2) {
                            Log.d("STATE", "State file uploaded");
                          /*  Log.d(TAG, response.body().toString());
                            updateProgress(100);
                            count++;*/
//                        addFileToMemory();
                            finish();
                        } else {
                            Log.d("TAG", response.toString());
                            Toast.makeText(AshaConnectActivity.this,
                                    "Unable to add image to memory memory at this time",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        Toast.makeText(AshaConnectActivity.this,
                                "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
