package com.example.bai.detector1;

import android.app.PendingIntent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

public class MainActivity extends AppCompatActivity {
    private Handler handler;
    private String ssid;
    private int level;
    private WifiInfo wifiInfo = null;
    WifiManager wifiManager;
    TextView txt1;
    TextView txt2;
    TextView txt3, txt4, txt5, txt6;
    Button button;
    List<ScanResult> results;
    private String targetSsid = "BebopDrone-029505";
    int enterTime=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        txt1 = (TextView) findViewById(R.id.text1);
        txt2 = (TextView) findViewById(R.id.text2);
        txt3 = (TextView) findViewById(R.id.text3);
        txt4 = (TextView) findViewById(R.id.text4);

        txt6 = (TextView) findViewById(R.id.text6);
        button = (Button) findViewById(R.id.button);
        results = wifiManager.getScanResults();

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt6.setText(showTime()); //showTime会返回一个时间字符串  location经纬度在setLongitudeAndLatitude函数里，最下面，如果需要可以改变这个函数的返回值得到经纬度

            }
        });
        setLongitudeAndLatitude();//可得到经纬度

        detect();
    }

    @SuppressLint("HandlerLeak")
    private void detect() {

        final Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                // wifiManager.addNetwork(setWifiParamsNoPassword("BebopDrone-029505"));
                // wifiManager.enableNetwork(wifiManager.addNetwork(setWifiParamsNoPassword("BebopDrone-029505")),true);
                wifiInfo = wifiManager.getConnectionInfo();
                //获得信号强度值
                //  List<ScanResult> results= wifiManager.getScanResults();
                //   results= wifiManager.getScanResults();

               // ssid = wifiInfo.getSSID();
                wifiManager.startScan();
                List<ScanResult> results= wifiManager.getScanResults();
                for(ScanResult result:results)
                {
                    if (result.SSID.equals(targetSsid))
                    {
                        //target= results.indexOf(result);
                        ssid=result.SSID;
                        level=result.level;
                        Message msg = new Message();
                        msg.what = 5;
                        handler.sendMessage(msg);
                        break;
                    }
                }
                //根据获得的信号强度发送信息
                // Date devicetime = new Date(System.currentTimeMillis());
                if (ssid.equals(targetSsid)) {
                  //  level = wifiInfo.getRssi();
                    if (level <= 0 && level >= -50) {
                        Message msg = new Message();
                        msg.what = 1;
                        handler.sendMessage(msg);
                    } else if (level < -50 && level >= -70) {
                        Message msg = new Message();
                        msg.what = 2;
                        handler.sendMessage(msg);
                    } else if (level < -70 && level >= -80) {
                        Message msg = new Message();
                        msg.what = 3;
                        handler.sendMessage(msg);
                    } else if (level < -80 && level >= -100) {
                        Message msg = new Message();
                        msg.what = 4;
                        handler.sendMessage(msg);
                    } /*else {
                        Message msg = new Message();
                        msg.what = 5;
                        handler.sendMessage(msg);

                    }*/

                } else {
                    Message msg = new Message();
                    msg.what = 9;
                    handler.sendMessage(msg);
                }
            }

        }, 2000, 8000);

        handler = new Handler() {

            @SuppressLint("SetTextI18n")
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    // 如果收到正确的消息就获取WifiInfo，改变图片并显示信号强度
                    case 1:
                        Toast.makeText(MainActivity.this,
                                "signal strength：" + level, Toast.LENGTH_SHORT)
                                .show();

                        txt2.setText(level + " dbm");
                        txt4.setText("Transaction processing");
                        break;
                    case 2:

                        Toast.makeText(MainActivity.this,
                                "signal strength：" + level, Toast.LENGTH_SHORT)
                                .show();
                        txt2.setText(level + " dbm");

                        txt4.setText("Transaction processing");
                        break;
                    case 3:

                        Toast.makeText(MainActivity.this,
                                "signal strength：" + level, Toast.LENGTH_SHORT)
                                .show();
                        txt2.setText(level + " dbm");
                       // txt6.setText(showTime());
                        txt4.setText("Transaction finished");
                        ///////////////////////
                        break;
                    case 4:

                        Toast.makeText(MainActivity.this,
                                "signal strength：" + level, Toast.LENGTH_SHORT)
                                .show();
                        txt2.setText(level + " dbm");

                        txt4.setText("Transaction finished ");
                        break;
                    case 5:
                        txt1.setText(ssid);


                        break;

                    case 9:

                        Toast.makeText(MainActivity.this,
                                "connecting   " + "signal strength: " + level, Toast.LENGTH_SHORT)
                                .show();
                        txt1.setText("not detect the drone");
                        txt2.setText("no drone signal level");


                        // wifiManager.addNetwork(setWifiParamsNoPassword("BebopDrone-029505"));

                        //  wifiManager.startScan();
                        //  wifiManager.disconnect();
                        //   wifiManager.enableNetwork(wifiManager.addNetwork(createWifiConfig("BebopDrone-029505","",0)),true);
                        WifiConfiguration configuration = createWifiConfig("BebopDrone-029505", "", 0);
                        // wifiManager.reconnect();
                        int netId = configuration.networkId;
                        if (netId == -1) {
                            netId = wifiManager.addNetwork(configuration);
                        }
                        wifiManager.enableNetwork(netId, true);

                        break;

                    default:
                        //以防万一

                        Toast.makeText(MainActivity.this, "无信号",
                                Toast.LENGTH_SHORT).show();
                }
            }

        };


    }


    private WifiConfiguration setWifiParamsNoPassword(String SSid) {
        WifiConfiguration configuration = new WifiConfiguration();
        configuration.SSID = "\"" + SSid + "\"";
        configuration.status = WifiConfiguration.Status.ENABLED;
        configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        configuration.allowedPairwiseCiphers
                .set(WifiConfiguration.PairwiseCipher.TKIP);
        configuration.allowedPairwiseCiphers
                .set(WifiConfiguration.PairwiseCipher.CCMP);
        configuration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        return configuration;
    }

    private static final int WIFICIPHER_NOPASS = 0;
    private static final int WIFICIPHER_WEP = 1;
    private static final int WIFICIPHER_WPA = 2;


    private WifiConfiguration createWifiConfig(String ssid, String password, int type) {
        //初始化WifiConfiguration
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        //指定对应的SSID
        config.SSID = "\"" + ssid + "\"";
        //如果之前有类似的配置
        WifiConfiguration tempConfig = isExist(ssid);
        if (tempConfig != null) {
            //则清除旧有配置
            wifiManager.removeNetwork(tempConfig.networkId);
        }
        //不需要密码的场景
        if (type == WIFICIPHER_NOPASS) {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            //以WEP加密的场景
        } else if (type == WIFICIPHER_WEP) {
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
            //以WPA加密的场景，自己测试时，发现热点以WPA2建立时，同样可以用这种配置连接
        } else if (type == WIFICIPHER_WPA) {
            config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    private WifiConfiguration isExist(String ssid) {
        List<WifiConfiguration> configs = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration config : configs) {
            if (config.SSID.equals("\"" + ssid + "\"")) {
                return config;
            }
        }
        return null;
    }

    private String showTime() {
        Date dt;
        dt = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm ");
        String str_time = sdf.format(dt);

        return str_time;
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

    @SuppressLint("SetTextI18n")
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

        txt3.setText("Lng :"+longitude+ " "+"Lat :"+ latitude);

    }



}
