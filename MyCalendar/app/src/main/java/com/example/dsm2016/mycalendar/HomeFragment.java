package com.example.dsm2016.mycalendar;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ImageView;
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
import java.util.Objects;

public class HomeFragment extends Fragment {
    private TextView Selected_weather;
    private TextView POPText;
    private ImageView POPImage;
    private ImageView weatherPic;
    private String totalWeather;
    private String POP;
    private String SecretKey = "bWqZS4m%2BWvKX95DN1hbvoC2TBZn4HLxgK6O51THm8pvQXaIlg5TrHJpXi86ppuhNcCkQaTZLL0QFtWjUZWPwTw%3D%3D";
    private String SelectedDay, settingDate;
    private int subCurrentValue;

    String[] todayWeather = {"wf3Am", "wf3Pm", "wf4Am", "wf4Pm", "wf5Am", "wf5Pm", "wf6Am", "wf6Pm", "wf7Am", "wf7Pm", "wf8", "wf9", "wf10"};
    String[] weekWeather = {"taMin3", "taMax3", "taMin4", "taMax4", "taMin5", "taMax5", "taMin6", "taMax6", "taMin7", "taMax7", "taMin8", "taMax8", "taMin9", "taMax9", "taMin10", "taMax10"};
    String[] todayValue = new String[13];
    String[] weekValue = new String[16];

    long now = System.currentTimeMillis();
    Date date = new Date(now);
    SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMMdd");
    SimpleDateFormat timeNow = new SimpleDateFormat("H");
    String formatDate = sdfNow.format(date);
    String base_time = timeNow.format(date);
    int int_base_time = Integer.parseInt(base_time);
    String Today_Time;
    private int selectedNum;

    ArrayList<WeatherItem> weatherItemArrayList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, null);

        CalendarView calendarView = (CalendarView) view.findViewById(R.id.main_calendar);
        Selected_weather = (TextView) view.findViewById(R.id.today_weather);
        weatherPic = (ImageView) view.findViewById(R.id.weather_image);
        POPImage = (ImageView) view.findViewById(R.id.POPImage);
        POPText = (TextView) view.findViewById(R.id.POPText);
        final TextView select_Text = (TextView) view.findViewById(R.id.selectedDay);

        setBaseTime(int_base_time);

        Today_Time = formatDate + "0600";

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                setSelectedDay(year, month, dayOfMonth);
                Log.d("SelectedDay = ", SelectedDay);
                select_Text.setText(year + "년 " + (month + 1) + "월 " + dayOfMonth + "일 날씨 ");

                if (formatDate.equals(SelectedDay)) {
                    selectedNum = 0;
                    getWeatherData();
                } else if (Integer.parseInt(SelectedDay) - Integer.parseInt(formatDate) == 1) {
                    selectedNum = 1;
                    getWeatherData();
                } else if (Integer.parseInt(SelectedDay) - Integer.parseInt(formatDate) == 2) {
                    selectedNum = 2;
                    getWeatherData();
                } else {
                    getTemperatureData();
                    getCloudWeatherData();
                }
            }
        });
        return view;
    }

    private void setBaseTime(int int_base_time) {
        switch (int_base_time) {
            case 0:
            case 1:
            case 2:
                base_time = "2300";
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
    }

    private void setSelectedDay(int year, int month, int dayOfMonth) {
        if ((String.valueOf(month).length() == 1) && (String.valueOf(dayOfMonth).length() == 2))
            SelectedDay = String.valueOf(year) + ('0' + String.valueOf(month + 1)) + String.valueOf(dayOfMonth);
        else if ((String.valueOf(dayOfMonth).length() == 2) && (String.valueOf(dayOfMonth).length() == 1))
            SelectedDay = String.valueOf(year) + String.valueOf(month + 1) + ('0' + String.valueOf(dayOfMonth));
        else if ((String.valueOf(dayOfMonth).length() == 1) && (String.valueOf(dayOfMonth).length() == 1))
            SelectedDay = String.valueOf(year) + ('0' + String.valueOf(month + 1)) + ('0' + String.valueOf(dayOfMonth));
        else
            SelectedDay = String.valueOf(year) + String.valueOf(month + 1) + String.valueOf(dayOfMonth);
    }

    private void getWeatherData() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                BufferedInputStream inputStream;
                try {
                    // 해당 URL 데이터 읽어와서 String에 저장
                    URL url = new URL(
                            "http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService2/ForecastSpaceData?serviceKey=" + SecretKey +
                                    "&base_date=" + formatDate + "&base_time=" + base_time + "&nx=68&ny=101&numOfRows=200&pageSize=10&pageNo=1&startPage=1&_type=json"
                    );
                    Log.d("base_time = ", base_time);

                    inputStream = new BufferedInputStream(url.openStream());
                    StringBuffer stringBuffer = new StringBuffer();

                    int i;
                    byte[] b = new byte[48452];
                    while ((i = inputStream.read(b)) != -1) {
                        stringBuffer.append(new String(b, 0, i));
                    }

                    String jsonString = stringBuffer.toString();
                    Log.d("JSON STRING", jsonString);


                    JSONObject allJSONObject = new JSONObject(jsonString);     // String을 JSONObject로 변환
                    JSONArray itemJSONArray = allJSONObject.getJSONObject("response").getJSONObject("body").getJSONObject("items").getJSONArray("item");   // 필요한 데이터 가져오기 (item 이 key인 Array)
                    //Log.d("JSON ARRAY", itemJSONArray.toString());

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
                    /*
                    for (int j = 0; j < weatherItemArrayList.size(); j++) {
                        Log.d("Weather Item" + j, weatherItemArrayList.get(j).toString());
                    }
                    */
                    setUpWeatherTv();

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

    private void setUpWeatherTv() {
        if (selectedNum == 0) {
            settingDate = formatDate;
        } else if (selectedNum == 1) {
            int formatNumber = Integer.parseInt(formatDate);
            formatNumber += 1;
            settingDate = String.valueOf(formatNumber);
        } else if (selectedNum == 2) {
            int formatNumber = Integer.parseInt(formatDate);
            formatNumber += 2;
            settingDate = String.valueOf(formatNumber);
        }

        Log.d("settingDate = ", settingDate);
        for (int a = 0; a < weatherItemArrayList.size(); a++) {
            String currentFcstdate = weatherItemArrayList.get(a).getFcstDate();
            String currentCategory = weatherItemArrayList.get(a).getCategory();
            int currentValue = weatherItemArrayList.get(a).getFcstValue();

            if (currentFcstdate.equals(settingDate)) {
                if (currentCategory.equals("POP")) {
                    POP = "강수확률 : " + Integer.toString(currentValue);
                } else if (currentCategory.equals("REH")) {
                    totalWeather = Integer.toString(currentValue) + "% / ";
                } else if (currentCategory.equals("PTY")) {
                    if (currentValue == 1) {
                        //totalWeather += "강수형태 : 비\n";
                        subCurrentValue = 4;
                    } else if (currentValue == 2) {
                        //totalWeather += "강수형태 : 비 / 눈\n";
                    } else {
                        //totalWeather += "강수형태 : 눈\n";
                    }
                }else if (currentCategory.equals("SKY")) {
                    if (currentValue == 1) {
                        //totalWeather += "맑음\n";
                        subCurrentValue = 1;
                    } else if (currentValue == 2) {
                        //totalWeather += "구름조금\n";
                        subCurrentValue = 2;
                    } else if (currentValue == 3) {
                        //totalWeather += "구름많음\n";
                        subCurrentValue = 3;
                    } else {
                        //totalWeather += "흐림\n";
                        subCurrentValue = 4;
                    }
                } else if (currentCategory.equals("T3H")) {
                    totalWeather += Integer.toString(currentValue) + "ºC";
                }
            }
        }
        Log.d("TotalWeather", totalWeather);

        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("subCurrentValue = ", String.valueOf(subCurrentValue));
                Selected_weather.setText(totalWeather);
                if (subCurrentValue == 1) {
                    weatherPic.setImageResource(R.drawable.sun);
                } else if (subCurrentValue == 2) {
                    weatherPic.setImageResource(R.drawable.smallcloudy);
                } else if (subCurrentValue == 3) {
                    weatherPic.setImageResource(R.drawable.cloudy);
                } else if (subCurrentValue == 4) {
                    weatherPic.setImageResource(R.drawable.rainy);
                }
                POPImage.setVisibility(View.VISIBLE);
                POPText.setVisibility(View.VISIBLE);
                POPImage.setImageResource(R.drawable.drop);
                POPText.setText(POP);
            }
        });
    }

    private void getTemperatureData() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                BufferedInputStream inputStream;
                try {
                    URL url = new URL("http://newsky2.kma.go.kr/service/MiddleFrcstInfoService/getMiddleTemperature?serviceKey=" + SecretKey + "&regId=11C20401&tmFc=" + Today_Time + "&pageNo=1&startPage=1&numOfRows=10&pageSize=10&type=json"
                    );

                    Log.d("Today_Time = ", Today_Time);

                    inputStream = new BufferedInputStream(url.openStream());
                    StringBuffer stringBuffer = new StringBuffer();

                    int i;
                    byte[] b = new byte[4096];
                    while ((i = inputStream.read(b)) != -1) {
                        stringBuffer.append(new String(b, 0, i));
                    }

                    String jsonString = stringBuffer.toString();
                    Log.d("Temp JSON STRING", jsonString);

                    JSONObject allJSONObject = new JSONObject(jsonString);
                    JSONObject itemJSONObject = allJSONObject.getJSONObject("response").getJSONObject("body").getJSONObject("items").getJSONObject("item");
                    //Log.d("Temp JSON Object", itemJSONObject.toString());

                    for (int j = 0; j < 16; j++) {
                        weekValue[j] = itemJSONObject.getString(weekWeather[j]);
                        Log.d("weekValue = ", weekValue[j]);
                    }
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
        new Thread() {
            @Override
            public void run() {
                super.run();
                BufferedInputStream inputStream;
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
                    Log.d("Cloud JSON STRING ", jsonString);

                    JSONObject allJSONObject = new JSONObject(jsonString);
                    JSONObject itemJSONObject = allJSONObject.getJSONObject("response").getJSONObject("body").getJSONObject("items").getJSONObject("item");
                    //Log.d("Cloud JSON ARRAY ", itemJSONObject.toString());

                    for (int j = 0; j < 13; j++) {
                        todayValue[j] = itemJSONObject.getString(todayWeather[j]);
                        Log.d("todayValue = ", todayValue[j]);
                    }

                    setUpWeekWeatherTV();
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

    private void setUpWeekWeatherTV() {
        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (Integer.parseInt(SelectedDay) - Integer.parseInt(formatDate) == 3) {
                    boolean d = Integer.parseInt(SelectedDay) - Integer.parseInt(formatDate) == 3;
                    Log.d("3일 후", String.valueOf(d));
                    Selected_weather.setText(todayValue[0] + " / " + todayValue[1] + " " + weekValue[0] + "ºC / " + weekValue[1] + "ºC");
                } else if (Integer.parseInt(SelectedDay) - Integer.parseInt(formatDate) == 4) {
                    boolean d = Integer.parseInt(SelectedDay) - Integer.parseInt(formatDate) == 4;
                    Log.d("4일 후", String.valueOf(d));
                    Selected_weather.setText(todayValue[2] + " / " + todayValue[3] + " " + weekValue[2] + "ºC / " + weekValue[3] + "ºC");
                } else if (Integer.parseInt(SelectedDay) - Integer.parseInt(formatDate) == 5) {
                    boolean d = Integer.parseInt(SelectedDay) - Integer.parseInt(formatDate) == 5;
                    Log.d("5일 후", String.valueOf(d));
                    Selected_weather.setText(todayValue[4] + " / " + todayValue[5] + " " + weekValue[4] + "ºC / " + weekValue[5] + "ºC");
                } else if (Integer.parseInt(SelectedDay) - Integer.parseInt(formatDate) == 6) {
                    boolean d = Integer.parseInt(SelectedDay) - Integer.parseInt(formatDate) == 6;
                    Log.d("6일 후", String.valueOf(d));
                    Selected_weather.setText(todayValue[6] + " / " + todayValue[7] + " " + weekValue[6] + "ºC / " + weekValue[7] + "ºC");
                } else if (Integer.parseInt(SelectedDay) - Integer.parseInt(formatDate) == 7) {
                    boolean d = Integer.parseInt(SelectedDay) - Integer.parseInt(formatDate) == 7;
                    Log.d("7일 후", String.valueOf(d));
                    Selected_weather.setText(todayValue[8] + " / " + todayValue[9] + " " + weekValue[8] + "ºC / " + weekValue[9] + "ºC");
                } else if (Integer.parseInt(SelectedDay) - Integer.parseInt(formatDate) == 8) {
                    boolean d = Integer.parseInt(SelectedDay) - Integer.parseInt(formatDate) == 8;
                    Log.d("8일 후", String.valueOf(d));
                    Selected_weather.setText(todayValue[10] + " " + weekValue[10] + "ºC / " + weekValue[11] + "ºC");
                } else if (Integer.parseInt(SelectedDay) - Integer.parseInt(formatDate) == 9) {
                    boolean d = Integer.parseInt(SelectedDay) - Integer.parseInt(formatDate) == 9;
                    Log.d("9일 후", String.valueOf(d));
                    Selected_weather.setText(todayValue[11] + " " + weekValue[12] + "ºC / " + weekValue[13] + "ºC");
                } else if (Integer.parseInt(SelectedDay) - Integer.parseInt(formatDate) == 10) {
                    boolean d = Integer.parseInt(SelectedDay) - Integer.parseInt(formatDate) == 10;
                    Log.d("10일 후", String.valueOf(d));
                    Selected_weather.setText(todayValue[12] + " " + weekValue[14] + "ºC / " + weekValue[15] + "ºC");
                } else {
                    weatherPic.setImageResource(R.drawable.face);
                    Selected_weather.setText("NULL");
                }
                POPImage.setVisibility(View.INVISIBLE);
                POPText.setVisibility(View.INVISIBLE);
            }
        });
    }
}
