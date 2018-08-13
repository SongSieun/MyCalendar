package com.example.dsm2016.weeklyweather_api;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private String SecretKey = "";

    private TextView day3 = null;
    private TextView day4 = null;
    private TextView day5 = null;
    private TextView day6 = null;
    private TextView day7 = null;
    private TextView day8 = null;
    private TextView day9 = null;
    private TextView day10 = null;

    long now = System.currentTimeMillis();
    Date date = new Date(now);
    SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMMdd");
    String formatDate = sdfNow.format(date);
    String Today_Time = formatDate + "0600";
    private String wf3Am;
    private String wf3Pm;
    private String wf4Am;
    private String wf4Pm;
    private String wf5Am;
    private String wf5Pm;
    private String wf6Am;
    private String wf6Pm;
    private String wf7Am;
    private String wf7Pm;
    private String wf8;
    private String wf9;
    private String wf10;

    private String taMin3;
    private String taMax3;
    private String taMin4;
    private String taMax4;
    private String taMin5;
    private String taMax5;
    private String taMin6;
    private String taMax6;
    private String taMin7;
    private String taMax7;
    private String taMin8;
    private String taMax8;
    private String taMin9;
    private String taMax9;
    private String taMin10;
    private String taMax10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getCloudWeatherData();
        getTemperatureData();
        Log.d("Today : ", Today_Time);
    }

    private void getTemperatureData() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                BufferedInputStream inputStream = null;
                try {
                    URL url = new URL("http://newsky2.kma.go.kr/service/MiddleFrcstInfoService/getMiddleTemperature?serviceKey=" + SecretKey + "&regId=11C20401&tmFc=" + Today_Time + "&pageNo=1&startPage=1&numOfRows=10&pageSize=10&type=json"
                    );

                    inputStream = new BufferedInputStream(url.openStream());
                    StringBuffer stringBuffer = new StringBuffer();

                    int i;
                    byte[] b = new byte[4096];
                    while ((i = inputStream.read(b)) != -1) {
                        stringBuffer.append(new String(b, 0, i));
                    }

                    String jsonString = stringBuffer.toString();
                    Log.d("JSON STRING", jsonString);

                    JSONObject allJSONObject = new JSONObject(jsonString);
                    JSONObject itemJSONObject = allJSONObject.getJSONObject("response").getJSONObject("body").getJSONObject("items").getJSONObject("item");
                    Log.d("JSON Object", itemJSONObject.toString());

                    taMin3 = itemJSONObject.getString("taMin3");
                    taMax3 = itemJSONObject.getString("taMax3");
                    taMin4 = itemJSONObject.getString("taMin4");
                    taMax4 = itemJSONObject.getString("taMax4");
                    taMin5 = itemJSONObject.getString("taMin5");
                    taMax5 = itemJSONObject.getString("taMax5");
                    taMin6 = itemJSONObject.getString("taMin6");
                    taMax6 = itemJSONObject.getString("taMax6");
                    taMin7 = itemJSONObject.getString("taMin7");
                    taMax7 = itemJSONObject.getString("taMax7");
                    taMin8 = itemJSONObject.getString("taMin8");
                    taMax8 = itemJSONObject.getString("taMax8");
                    taMin9 = itemJSONObject.getString("taMin9");
                    taMax9 = itemJSONObject.getString("taMax9");
                    taMin10 = itemJSONObject.getString("taMin10");
                    taMax10 = itemJSONObject.getString("taMax10");

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void getCloudWeatherData() {
        day3 = (TextView) findViewById(R.id.day3);
        day4 = (TextView) findViewById(R.id.day4);
        day5 = (TextView) findViewById(R.id.day5);
        day6 = (TextView) findViewById(R.id.day6);
        day7 = (TextView) findViewById(R.id.day7);
        day8 = (TextView) findViewById(R.id.day8);
        day9 = (TextView) findViewById(R.id.day9);
        day10 = (TextView) findViewById(R.id.day10);
        new Thread() {
            @Override
            public void run() {
                super.run();
                BufferedInputStream inputStream = null;
                try {
                    URL url = new URL("http://newsky2.kma.go.kr/service/MiddleFrcstInfoService/getMiddleLandWeather?serviceKey=" + SecretKey + "&regId=11C20000&tmFc=" + Today_Time + "&numOfRows=10&pageSize=10&pageNo=1&startPage=1&type=json"
                    );

                    inputStream = new BufferedInputStream(url.openStream());
                    StringBuffer stringBuffer = new StringBuffer();

                    int i;
                    byte[] b = new byte[4096];
                    while ((i = inputStream.read(b)) != -1) {
                        stringBuffer.append(new String(b, 0, i));
                    }

                    String jsonString = stringBuffer.toString();
                    Log.d("JSON STRING ", jsonString);

                    JSONObject allJSONObject = new JSONObject(jsonString);
                    JSONObject itemJSONObject = allJSONObject.getJSONObject("response").getJSONObject("body").getJSONObject("items").getJSONObject("item");
                    Log.d("JSON ARRAY ", itemJSONObject.toString());

                    wf3Am = itemJSONObject.getString("wf3Am");
                    wf3Pm = itemJSONObject.getString("wf3Pm");
                    wf4Am = itemJSONObject.getString("wf4Am");
                    wf4Pm = itemJSONObject.getString("wf4Pm");
                    wf5Am = itemJSONObject.getString("wf5Am");
                    wf5Pm = itemJSONObject.getString("wf5Pm");
                    wf6Am = itemJSONObject.getString("wf6Am");
                    wf6Pm = itemJSONObject.getString("wf6Pm");
                    wf7Am = itemJSONObject.getString("wf7Am");
                    wf7Pm = itemJSONObject.getString("wf7Pm");
                    wf8 = itemJSONObject.getString("wf8");
                    wf9 = itemJSONObject.getString("wf9");
                    wf10 = itemJSONObject.getString("wf10");

                    //setUpWeatherTV();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            day3.setText("3 : " + wf3Am + " / " + wf3Pm + "\n 최저/최고기온 : " + taMin3 + " / " + taMax3);
                            day4.setText("4 : " + wf4Am + " / " + wf4Pm + " \n 최저/최고기온 : " + taMin4 + " / " + taMax4);
                            day5.setText("5 : " + wf5Am + " / " + wf5Pm + " \n 최저/최고기온 : " + taMin5 + " / " + taMax5);
                            day6.setText("6 : " + wf6Am + " / " + wf6Pm + " \n 최저/최고기온 : " + taMin6 + " / " + taMax6);
                            day7.setText("7 : " + wf7Am + " / " + wf7Pm + " \n 최저/최고기온 : " + taMin7 + " / " + taMax7);
                            day8.setText("8 : " + wf8 + " \n 최저/최고기온 : " + taMin8 + " / " + taMax8);
                            day9.setText("9 : " + wf9 + " \n 최저/최고기온 : " + taMin9 + " / " + taMax9);
                            day10.setText("10 : " + wf10 + " \n 최저/최고기온 : " + taMin10 + " / " + taMax10);
                        }
                    });


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}