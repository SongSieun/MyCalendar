package com.example.dsm2016.weather_api;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.crypto.SecretKey;

public class MainActivity extends AppCompatActivity {
    private TextView today_weather;
    private String totalWeather;
    private String SecretKey = "";

    long now = System.currentTimeMillis();
    Date date = new Date(now);
    SimpleDateFormat dayNow = new SimpleDateFormat("yyyyMMdd");
    SimpleDateFormat timeNow = new SimpleDateFormat("H");
    String formatDate = dayNow.format(date);
    String base_time = timeNow.format(date);
    int int_base_time = Integer.parseInt(base_time);
    int int_formatDate = Integer.parseInt(formatDate);

    private int selectedNum;

    ArrayList<WeatherItem> weatherItemArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        today_weather = (TextView) findViewById(R.id.today_weather);
        Log.d("base_time = ", base_time);
        Log.d("formatDate = ", formatDate);
        Log.d("int_formatDate = ", String.valueOf(int_formatDate));

        getWeatherData();
    }

    private void getWeatherData() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                BufferedInputStream inputStream = null;
                switch (int_base_time) {
                    case 0:
                    case 1:
                    case 2:
                        base_time = "0000";
                        break;
                    case 3:
                    case 4:
                    case 5:
                        base_time = "0200";
                        break;
                    case 6:
                    case 7:
                    case 8:
                        base_time = "0500";
                        break;
                    case 9:
                    case 10:
                    case 11:
                        base_time = "0800";
                        break;
                    case 12:
                    case 13:
                    case 14:
                        base_time = "1100";
                        break;
                    case 15:
                    case 16:
                    case 17:
                        base_time = "1400";
                        break;
                    case 18:
                    case 19:
                    case 20:
                        base_time = "1700";
                        break;
                    case 21:
                    case 22:
                    case 23:
                        base_time = "2000";
                        break;
                }
                try {
                    // 해당 URL 데이터 읽어와서 String에 저장
                    URL url = new URL(
                            "http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService2/ForecastSpaceData?serviceKey=" + SecretKey + "&base_date=" + formatDate + "&base_time=" + base_time + "&nx=68&ny=101&numOfRows=250&pageSize=10&pageNo=1&startPage=1&_type=json"
                    );
                    Log.d("base_time = ", base_time);
                    Log.d("formatDate = ", formatDate);

                    inputStream = new BufferedInputStream(url.openStream());
                    StringBuffer stringBuffer = new StringBuffer();

                    int i;
                    byte[] b = new byte[4096];
                    while ((i = inputStream.read(b)) != -1) {
                        stringBuffer.append(new String(b, 0, i));
                    }

                    String jsonString = stringBuffer.toString();
                    Log.d("JSON STRING", jsonString);


                    JSONObject allJSONObject = new JSONObject(jsonString);     // String을 JSONObject로 변환
                    JSONArray itemJSONArray = allJSONObject.getJSONObject("response").getJSONObject("body").getJSONObject("items").getJSONArray("item");   // 필요한 데이터 가져오기 (item 이 key인 Array)
                    Log.d("JSON ARRAY", itemJSONArray.toString());

                    // 날짜, 시간은 최초 한번만 가져오기
                    JSONObject firstObjectInArray = (JSONObject) itemJSONArray.get(0);
                    String baseDate = firstObjectInArray.getString("baseDate");
                    String baseTime = firstObjectInArray.getString("baseTime");
                    String fcstTime = firstObjectInArray.getString("fcstTime");

                    System.out.println("발표일자==" + baseDate + "/ 발표시각==" + baseTime + "/ 예보시각==" + fcstTime);

                    // 가져온 Array 안에 있는 Object들 안에있는 category, fcstValue 가져오기
                    for (int k = 0; k < itemJSONArray.length(); k++) {
                        JSONObject itemObject = (JSONObject) itemJSONArray.get(k);
                        String fcstDate = itemObject.getString("fcstDate");
                        String category = itemObject.getString("category");
                        int fcstValue = itemObject.getInt("fcstValue");

                        weatherItemArrayList.add(new WeatherItem(fcstDate, category, fcstValue));    // ArrayList에 저장
                    }

                    // ArrayList 출력
                    for (int j = 0; j < weatherItemArrayList.size(); j++) {
                        Log.d("Weather Item" + j, weatherItemArrayList.get(j).toString());
                    }

                    setUpTodayTv();

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

    private void setUpTodayTv() {
        for (int a = 0; a < weatherItemArrayList.size(); a++) {
            String currentDate = weatherItemArrayList.get(a).getFcstDate();
            String currentCategory = weatherItemArrayList.get(a).getCategory();
            int currentValue = weatherItemArrayList.get(a).getFcstValue();

            Log.d("currentDate", currentDate);
            Log.d("CurrentCategory", currentCategory);

            if (currentDate.equals(formatDate)) {
                if (currentCategory.equals("POP")) {
                    totalWeather = "강수확률 : " + Integer.toString(currentValue) + "%\n";
                    Log.d("강수확률", String.valueOf(currentValue));
                } else if (currentCategory.equals("REH")) {
                    totalWeather += "습도 : " + Integer.toString(currentValue) + "%\n";
                    Log.d("습도", String.valueOf(currentValue));
                } else if (currentCategory.equals("SKY")) {
                    totalWeather += "하늘상태 : ";
                    if (currentValue == 1) {
                        totalWeather += "맑음\n";
                    } else if (currentValue == 2) {
                        totalWeather += "구름조금\n";
                    } else if (currentValue == 3) {
                        totalWeather += "구름많음\n";
                    } else {
                        totalWeather += "흐림\n";
                    }
                } else if (currentCategory.equals("T3H")) {
                    totalWeather += "예상기온 : " + Integer.toString(currentValue) + "ºC\n";
                    Log.d("예상기온", String.valueOf(currentValue));
                } else if (currentCategory.equals("TMN")) {
                    totalWeather += "최저기온 : " + Integer.toString(currentValue) + "ºC\n";
                    Log.d("최저기온", String.valueOf(currentValue));
                } else if (currentCategory.equals("TMX")) {
                    totalWeather += "최고기온 : " + Integer.toString(currentValue) + "ºC\n";
                    Log.d("최고기온", String.valueOf(currentValue));
                }
            }
        }
        Log.d("TotalWeather", totalWeather);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                today_weather.setText(totalWeather);
            }
        });
    }
}