package com.example.bai.detector20;

import android.app.PendingIntent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

public class MainActivity extends AppCompatActivity {

    private String ssid ="testssid" ;
    private int level=-51;
    WifiManager wifiManager;
    List<ScanResult> results;
    List<String> droneList=Arrays.asList("BebopDrone-029505","BebopDrone-029506","BebopDrone-029507","test-1");
    List<String> targetList=Arrays.asList("BebopDrone-029505");
    ScanResult targetResult=null;
    int enterTime=0;
    TextView txt1,txt2,txt3,txt4,txt5,txt6,txt7;
    Timer timer1;
    boolean turnCode=false;
   // private String targetSsid = "BebopDrone-029505";
   private String targetSsid ="testssid" ;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    detect1();
                    txt6.setText("Detected");
                    txt1.setText("Drone name :  "+ssid);
                    txt3.setText("Transaction status :  "+"shipment transaction processing");
                    break;
                case 2:
                    detect2();
                    txt6.setText("Detected");
                    txt1.setText("Drone name :  "+ssid);
                    txt3.setText("Transaction status :  "+"middler transaction processing");
                    break;
                case 3:
                    Toast.makeText(MainActivity.this,
                            "signal strength：" + level, Toast.LENGTH_SHORT)
                            .show();
                    txt1.setText("Drone name :  "+ssid);
                    txt2.setText("Drone status :  "+"near the detector");

                    break;
                case 4:
                    Toast.makeText(MainActivity.this,
                            "signal strength：" + level, Toast.LENGTH_SHORT)
                            .show();
                    txt1.setText("Drone name :  "+ssid);
                    txt2.setText("Drone status :  "+"near the detector");

                    break;
                case 5:
                    Toast.makeText(MainActivity.this,
                            "signal strength：" + level, Toast.LENGTH_SHORT)
                            .show();
                    txt2.setText("Drone status :  "+"approaching the detector");
                    break;
                case 6:
                    Toast.makeText(MainActivity.this,
                            "signal strength：" + level, Toast.LENGTH_SHORT)
                            .show();
                    txt2.setText("Drone status :  "+"leaving the detector");
                    //交易结束 shipment
                    String jsonDataStr="{\n" +
                            "  \"$class\": \"org.drone.mynetwork.ShipmentArrivedTransaction\",\n" +
                            "  \"shipmentRecord\": \"resource:org.drone.mynetwork.ShipmentRecord#SHIP_001\"\n" +
                            "}";
                    new TriggerShipmentTransaction(MainActivity.this,txt3).execute(jsonDataStr);
                    txt4.setText( "Transaction end time :  "+showTime());

                    timer1.cancel();
                    break;
                case 7:
                    Toast.makeText(MainActivity.this,
                            "signal strength：" + level, Toast.LENGTH_SHORT)
                            .show();
                    txt2.setText("Drone status :  "+"leaving the detector");
                    //交易结束 middler
                    //ending time
                    String jsonDataStr2="{\n" +
                            "  \"$class\": \"org.drone.mynetwork.PassingMiddlerTransaction\",\n" +
                            "  \"middlerPassingRecord\": \"resource:org.drone.mynetwork.MiddlerPassingRecord#MIDPASS_001\"\n" +
                            "}";
                    new TriggerMiddlerPassingTransaction(MainActivity.this,txt3).execute(jsonDataStr2);
                    txt4.setText( "Transaction end time :  "+showTime());
                     timer1.cancel();
                    break;
                case 8:
                    //shipment transcation
                    Toast.makeText(MainActivity.this,
                            "Scaning ....." + level, Toast.LENGTH_SHORT)
                            .show();
                    break;
                case 9:
                    //show middler dealing
                    break;
                case 10:
                    //show shipment dealing
                    break;
                case 11:
                    //not found
                    txt1.setText("9090909090");
                    break;
                case 12:

                    break;
                case 13:
                    break;
                case 14:
                    break;
            }

        }
    };


    private String showTime() {
        Date dt;
        dt = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm ");
        String str_time = sdf.format(dt);

        return str_time;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        txt1 = (TextView) findViewById(R.id.text1);
        txt2 = (TextView) findViewById(R.id.text2);
        txt3 = (TextView) findViewById(R.id.text3);
        txt4 = (TextView) findViewById(R.id.text4);
        txt5 = (TextView) findViewById(R.id.text5);
        txt6=(TextView) findViewById(R.id.text99);
        setLongitudeAndLatitude();
        timer1 = new Timer();
        timer1.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                wifiManager.startScan();
                List<ScanResult> results= wifiManager.getScanResults();
               for(ScanResult result:results)
                {

                   if (droneList.contains(result.SSID))
                    {
                        if(targetList.contains(result.SSID)) {
                            ssid = result.SSID;
                            level = result.level;
                            Message msg = new Message();
                            msg.what = 1;
                            mHandler.sendMessage(msg);
                            break;
                        }
                        else
                        {
                            ssid = result.SSID;
                            level = result.level;
                            Message msg = new Message();
                            msg.what = 2;
                            mHandler.sendMessage(msg);
                            break;
                        }
                    }
                }



            }
        }, 2000, 5000);
    }

    private  void  detect1(){
        if (level <= 0 && level >= -40) {
            Message msg = new Message();
            msg.what = 3;
            mHandler.sendMessage(msg);
            turnCode=true;
        }
         else if (level < -40 && level >= -100) {



            if(turnCode){
                Message msg = new Message();
                msg.what = 6;
                mHandler.sendMessage(msg);
            }else{
            Message msg = new Message();
            msg.what = 5;
            mHandler.sendMessage(msg);}
        }
    }
    private  void  detect2(){
        if (level <= 0 && level >= -40) {
            Message msg = new Message();
            msg.what = 4;
            mHandler.sendMessage(msg);
            turnCode=true;
        }
         else if (level < -40 && level >= -100) {

            enterTime+=1;

            if(turnCode){
                Message msg = new Message();
                msg.what = 7;
                mHandler.sendMessage(msg);
            }else {
            Message msg = new Message();
            msg.what = 5;
            mHandler.sendMessage(msg);}
        }
    }


    public void setGPS(boolean on_off) {
        boolean gpsEnabled = android.provider.Settings.Secure.isLocationProviderEnabled(getContentResolver(), LocationManager.GPS_PROVIDER);
        Intent gpsIntent = new Intent();
        gpsIntent.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
        gpsIntent.addCategory("android.intent.category.ALTERNATIVE");

        if (on_off == true) {
            if (!gpsEnabled) {
                gpsIntent.setData(Uri.parse("custom:3"));
                try {
                    PendingIntent.getBroadcast(this, 0, gpsIntent, 0).send();
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (gpsEnabled) {
                gpsIntent.setData(Uri.parse("custom:3"));
                try {
                    PendingIntent.getBroadcast(this, 0, gpsIntent, 0).send();
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setLongitudeAndLatitude() {
        setGPS(true);
        String  longitude =null;
        String   latitude = null;
        LocationManager loctionManager;
        String contextService = Context.LOCATION_SERVICE;
        //通过系统服务，取得LocationManager对象
        loctionManager = (LocationManager) getSystemService(contextService);
        String provider = LocationManager.GPS_PROVIDER;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = loctionManager.getLastKnownLocation(provider);
        if(location != null) {
            longitude = String.valueOf(location.getLongitude());
            latitude = String.valueOf(location.getLatitude());
        }

        txt5.setText( "Detector location :  "+"Lng :"+longitude+ " "+"Lat :"+ latitude);

    }



}

