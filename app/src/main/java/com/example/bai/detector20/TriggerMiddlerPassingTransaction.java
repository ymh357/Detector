package com.example.bai.detector20;

import android.os.AsyncTask;
import android.widget.TextView;
import android.content.Context;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class TriggerMiddlerPassingTransaction extends AsyncTask<String,Void,Integer> {

    private WeakReference<Context> weakContext=null;
    private WeakReference<TextView> tx3=null;

    public TriggerMiddlerPassingTransaction(Context ctx, TextView tx3) {
        this.weakContext=new WeakReference<>(ctx);
        this.tx3=new WeakReference<>(tx3);

    }


    @Override
    protected Integer doInBackground(String... strings) {
        int responseCode=-2;
        try{

            String jsonDataStr=strings[0];
            JSONObject jsonObject=new JSONObject(jsonDataStr);
            responseCode=NetworkUtils.Post("http://139.132.17.23:3000/api/org.drone.mynetwork.PassingMiddlerTransaction",jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }finally {
            return responseCode;
        }

    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        tx3.get().setText("Transaction status :  Middler transaction finished  and status code is "+integer);

    }


}
