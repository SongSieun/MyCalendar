package com.sesong.mycalendar;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;

import com.sesong.mycalendar.Todo.RecyclerViewClickListener;
import com.sesong.mycalendar.Todo.TodoActivity;
import com.sesong.mycalendar.Todo.TodoItem;
import com.sesong.mycalendar.Todo.TodoRealmObject;
import com.sesong.mycalendar.Todo.TodoRecyclerAdapter;
import com.sesong.mycalendar.Weather.WeatherItem;
import com.sesong.mycalendar.databinding.ActivityMainBinding;

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
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private Realm realm;
    RealmResults<TodoRealmObject> realmResults;
    private String secretKey = "%2Fci7Zc3Sb4%2ByZV9TNfQv3HfvWhiyu5ysfWzRMSDEOSIaec3gy8S%2BRBElcLe5PyHmFkTAI%2BjwwclokDJqCrV5XA%3D%3D";
    private String base_date, base_time, time_hour, data_url;
    private String todoTitle, todoContent, todoDate;

    ArrayList<WeatherItem> weatherItemArrayList = new ArrayList<>();
    List<TodoItem> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        init();

        getTime();
        setTime();

        setUrl();
        getWeather();

        setTodo();
        //setTodo();

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TodoActivity.class);
                startActivityForResult(intent, 1000);
            }
        });

        binding.layoutContent.calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                String selectedDate = null;
                if ((dayOfMonth / 10) == 0)
                    selectedDate = String.valueOf(year) + String.valueOf(month + 1) + "0" + String.valueOf(dayOfMonth);
                else
                    selectedDate = String.valueOf(year) + String.valueOf(month + 1) + String.valueOf(dayOfMonth);
                selectTodo(selectedDate);
            }
        });

        binding.bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        binding.layoutContent.recyclerview.addOnItemTouchListener(new RecyclerViewClickListener(getApplicationContext(), binding.layoutContent.recyclerview, new RecyclerViewClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                removeTodo(position);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }

    private void init() {
        realm = Realm.getDefaultInstance();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.layoutContent.recyclerview.setLayoutManager(layoutManager);
    }

    private void getWeather() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                BufferedInputStream inputStream = null;
                try {
                    URL url = new URL(data_url);
                    inputStream = new BufferedInputStream(url.openStream());
                    StringBuffer stringBuffer = new StringBuffer();

                    int i;
                    byte[] b = new byte[4096];
                    while ((i = inputStream.read(b)) != -1) {
                        stringBuffer.append(new String(b, 0, i));
                    }

                    String jsonString = stringBuffer.toString();
                    Log.d("!@#JSON_STRING", jsonString);

                    JSONObject allJSONObject = new JSONObject(jsonString);     // String을 JSONObject로 변환
                    JSONArray itemJSONArray = allJSONObject.getJSONObject("response").getJSONObject("body").getJSONObject("items").getJSONArray("item");   // 필요한 데이터 가져오기 (item 이 key인 Array)
                    Log.d("!@#JSON_ARRAY", itemJSONArray.toString());

                    for (int k = 0; k < itemJSONArray.length(); k++) {
                        JSONObject itemObject = (JSONObject) itemJSONArray.get(k);
                        String category = itemObject.getString("category");
                        int fcstValue = itemObject.getInt("fcstValue");

                        weatherItemArrayList.add(new WeatherItem(category, fcstValue));    // ArrayList에 저장
                    }

                    setWeather();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void setWeather() {
        runOnUiThread(new Runnable() {
            // POP: 강수확률(%), PTY: 강수형태(없음(0), 비(1), 비/눈(2), 눈(3))
            // SKT: 하늘상태(맑음(1), 구름조금(2), 구름많음(3), 흐림(4)), T3H: 3시간 기온(℃)
            String resultPOP = null, resultSKY = null, resultT3H = null;
            int resultPTY = 0;

            @Override
            public void run() {
                for (int a = 0; a < weatherItemArrayList.size(); a++) {
                    String currentCategory = weatherItemArrayList.get(a).getCategory();
                    int currentValue = weatherItemArrayList.get(a).getFcstValue();

                    switch (currentCategory) {
                        case "POP":
                            resultPOP = String.valueOf(currentValue) + "%";
                            break;
                        case "PTY":
                            if (currentValue == 0) resultPTY = 0;
                            else if (currentValue == 1)
                                binding.layoutContent.weatherImage.setImageResource(R.drawable.rain);
                            else if (currentValue == 2)
                                binding.layoutContent.weatherImage.setImageResource(R.drawable.rain_snow);
                            else if (currentValue == 3)
                                binding.layoutContent.weatherImage.setImageResource(R.drawable.snow);
                            break;
                        case "SKY":
                            if (resultPTY == 0) {
                                if (currentValue == 1)
                                    binding.layoutContent.weatherImage.setImageResource(R.drawable.sun);
                                else if (currentValue == 2)
                                    binding.layoutContent.weatherImage.setImageResource(R.drawable.small_cloudy);
                                else if (currentValue == 3)
                                    binding.layoutContent.weatherImage.setImageResource(R.drawable.many_cloudy);
                                else if (currentValue == 4)
                                    binding.layoutContent.weatherImage.setImageResource(R.drawable.cloud);
                            }
                            break;
                        case "T3H":
                            resultT3H = String.valueOf(currentValue) + "℃";
                            break;
                    }
                }

                String totalWeather = "현재 " + resultT3H + "  강수확률 " + resultPOP;
                Log.d("!@#totalWeather ", totalWeather);
                binding.layoutContent.weatherText.setText(totalWeather);
            }
        });


    }

    private void setUrl() {
        data_url = "http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService2/ForecastSpaceData?serviceKey=" +
                "%2Fci7Zc3Sb4%2ByZV9TNfQv3HfvWhiyu5ysfWzRMSDEOSIaec3gy8S%2BRBElcLe5PyHmFkTAI%2BjwwclokDJqCrV5XA%3D%3D" +
                "&base_date=" + base_date +
                "&base_time=" + base_time +
                "&nx=60&ny=127&numOfRows=20&pageSize=10&pageNo=1&startPage=1&_type=json";
        Log.d("!@#data_url ", data_url);
    }

    private void getTime() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfD = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sdfT = new SimpleDateFormat("HH");
        base_date = sdfD.format(date);
        time_hour = sdfT.format(date);

        Log.d("!@#base_date ", base_date);
        Log.d("!@#time_hour ", time_hour);
    }

    private void setTime() {
        int int_time_hour = Integer.parseInt(time_hour);

        if (0 < int_time_hour && int_time_hour <= 2) base_time = "2300";
        else if (2 < int_time_hour && int_time_hour <= 5) base_time = "0200";
        else if (5 < int_time_hour && int_time_hour <= 8) base_time = "0500";
        else if (8 < int_time_hour && int_time_hour <= 11) base_time = "0800";
        else if (11 < int_time_hour && int_time_hour <= 14) base_time = "1100";
        else if (14 < int_time_hour && int_time_hour <= 17) base_time = "1400";
        else if (17 < int_time_hour && int_time_hour <= 20) base_time = "1700";
        else base_time = "2000";

        Log.d("!@#base_time ", base_time);
    }

    private void saveTodo() {
        realm.beginTransaction();
        TodoRealmObject realmObject = realm.createObject(TodoRealmObject.class);
        realmObject.setTitle(todoTitle);
        realmObject.setContent(todoContent);
        realmObject.setDate(todoDate);
        realm.commitTransaction();
    }

    private void setTodo() {
        realmResults = realm.where(TodoRealmObject.class).findAll();
        for (int i = 0; i < realmResults.size(); i++) {
            dataList.add(new TodoItem(realmResults.get(i).getTitle(), realmResults.get(i).getContent(), realmResults.get(i).getDate()));
        }
        TodoRecyclerAdapter adapter = new TodoRecyclerAdapter(dataList);
        binding.layoutContent.recyclerview.setAdapter(adapter);
    }

    private void selectTodo(String calendarDate) {
        realmResults = realm.where(TodoRealmObject.class).equalTo("date", calendarDate).findAll();
        for (int i = 0; i < realmResults.size(); i++) {
            dataList.add(new TodoItem(realmResults.get(i).getTitle(), realmResults.get(i).getContent(), realmResults.get(i).getDate()));
        }
        TodoRecyclerAdapter adapter = new TodoRecyclerAdapter(dataList);
        binding.layoutContent.recyclerview.setAdapter(adapter);
    }

    private void removeTodo(int position) {
        realmResults = realm.where(TodoRealmObject.class).equalTo("content", realmResults.get(position).getContent()).findAll();

        // 1번의 값 삭제
        realm.beginTransaction();
        TodoRealmObject realmObject = realmResults.get(position);
        realmObject.deleteFromRealm();
        //realmResults.deleteAllFromRealm();
        realm.commitTransaction();
        setTodo();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1000:
                    todoTitle = data.getStringExtra("todoTitle");
                    todoContent = data.getStringExtra("todoContent");
                    todoDate = data.getStringExtra("todoDate");

                    saveTodo();
                    setTodo();
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    private void getLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                int longitude = (int) location.getLongitude(); // 경도
                int latitude = (int) location.getLatitude();   // 위도
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }
}