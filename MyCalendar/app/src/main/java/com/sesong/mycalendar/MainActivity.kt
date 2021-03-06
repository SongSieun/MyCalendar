package com.sesong.mycalendar

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sesong.mycalendar.databinding.ActivityMainBinding
import com.sesong.mycalendar.todo.*
import com.sesong.mycalendar.weather.WeatherItem
import io.realm.Realm
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy { DataBindingUtil.setContentView(this, R.layout.activity_main) }
    private val realm: Realm by lazy { Realm.getDefaultInstance() }
    private val secretKey = "%2Fci7Zc3Sb4%2ByZV9TNfQv3HfvWhiyu5ysfWzRMSDEOSIaec3gy8S%2BRBElcLe5PyHmFkTAI%2BjwwclokDJqCrV5XA%3D%3D"
    private var baseDate: String? = null
    private var baseTime: String? = null
    private var timeHour: String? = null
    private var dataUrl: String? = null
    private var todoTitle: String? = null
    private var todoContent: String? = null
    private var todoDate: String? = null
    var weatherItemArrayList = ArrayList<WeatherItem>()
    private var dataList: MutableList<TodoItem> = ArrayList()
    private val time: Unit
        get() {
            val now = System.currentTimeMillis()
            val date = Date(now)
            val sdfD = SimpleDateFormat("yyyyMMdd")
            val sdfT = SimpleDateFormat("HH")
            baseDate = sdfD.format(date)
            timeHour = sdfT.format(date)
            Log.d("!@#base_date ", baseDate)
            Log.d("!@#time_hour ", timeHour)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        time
        setTime()
        setUrl()
        weather
        setTodo()
        //setTodo();
        binding.fab.setOnClickListener {
            val intent = Intent(this@MainActivity, TodoActivity::class.java)
            startActivityForResult(intent, 1000)
        }
        binding.layoutContent.calendar.setOnDateChangeListener { view, year, month, dayOfMonth ->
            var selectedDate: String? = null
            selectedDate = if (dayOfMonth / 10 == 0) year.toString() + (month + 1).toString() + "0" + dayOfMonth.toString() else year.toString() + (month + 1).toString() + dayOfMonth.toString()
            selectTodo(selectedDate)
        }
        binding.bar.setNavigationOnClickListener { }
        binding.layoutContent.recyclerview.addOnItemTouchListener(RecyclerViewClickListener(applicationContext, binding.layoutContent.recyclerview, object : RecyclerViewClickListener.OnItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                removeTodo(position)
            }

            override fun onLongItemClick(view: View?, position: Int) {}
        }))
    }

    private fun init() {
        Realm.init(this)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        binding.layoutContent.recyclerview.layoutManager = layoutManager
    }// ArrayList에 저장// 필요한 데이터 가져오기 (item 이 key인 Array)

    // String을 JSONObject로 변환
    private val weather: Unit
        get() {
            object : Thread() {
                override fun run() {
                    super.run()
                    var inputStream: BufferedInputStream? = null
                    try {
                        val url = URL(dataUrl)
                        inputStream = BufferedInputStream(url.openStream())
                        val stringBuffer = StringBuffer()
                        var i: Int
                        val b = ByteArray(4096)
                        while (inputStream.read(b).also { i = it } != -1) {
                            stringBuffer.append(String(b, 0, i))
                        }
                        val jsonString = stringBuffer.toString()
                        Log.d("!@#JSON_STRING", jsonString)
                        val allJSONObject = JSONObject(jsonString) // String을 JSONObject로 변환
                        val itemJSONArray = allJSONObject.getJSONObject("response").getJSONObject("body").getJSONObject("items").getJSONArray("item") // 필요한 데이터 가져오기 (item 이 key인 Array)
                        Log.d("!@#JSON_ARRAY", itemJSONArray.toString())
                        for (k in 0 until itemJSONArray.length()) {
                            val itemObject = itemJSONArray[k] as JSONObject
                            val category = itemObject.getString("category")
                            val fcstValue = itemObject.getInt("fcstValue")
                            weatherItemArrayList.add(WeatherItem(category, fcstValue)) // ArrayList에 저장
                        }
                        setWeather()
                    } catch (e: MalformedURLException) {
                        e.printStackTrace()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }.start()
        }

    private fun setWeather() {
        runOnUiThread(object : Runnable {
            // POP: 강수확률(%), PTY: 강수형태(없음(0), 비(1), 비/눈(2), 눈(3))
            // SKT: 하늘상태(맑음(1), 구름조금(2), 구름많음(3), 흐림(4)), T3H: 3시간 기온(℃)
            var resultPOP: String? = null
            var resultSKY: String? = null
            var resultT3H: String? = null
            var resultPTY = 0
            override fun run() {
                for (a in weatherItemArrayList.indices) {
                    val currentCategory = weatherItemArrayList[a].category
                    val currentValue = weatherItemArrayList[a].fcstValue
                    when (currentCategory) {
                        "POP" -> resultPOP = "$currentValue%"
                        "PTY" -> if (currentValue == 0) resultPTY = 0 else if (currentValue == 1) binding!!.layoutContent.weatherImage.setImageResource(R.drawable.rain) else if (currentValue == 2) binding!!.layoutContent.weatherImage.setImageResource(R.drawable.rain_snow) else if (currentValue == 3) binding!!.layoutContent.weatherImage.setImageResource(R.drawable.snow)
                        "SKY" -> if (resultPTY == 0) {
                            if (currentValue == 1) binding!!.layoutContent.weatherImage.setImageResource(R.drawable.sun) else if (currentValue == 2) binding!!.layoutContent.weatherImage.setImageResource(R.drawable.small_cloudy) else if (currentValue == 3) binding!!.layoutContent.weatherImage.setImageResource(R.drawable.many_cloudy) else if (currentValue == 4) binding!!.layoutContent.weatherImage.setImageResource(R.drawable.cloud)
                        }
                        "T3H" -> resultT3H = "$currentValue℃"
                    }
                }
                val totalWeather = "현재 $resultT3H  강수확률 $resultPOP"
                Log.d("!@#totalWeather ", totalWeather)
                binding!!.layoutContent.weatherText.text = totalWeather
            }
        })
    }

    private fun setUrl() {
        dataUrl = "http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService2/ForecastSpaceData?serviceKey=" +
                secretKey +
                "&base_date=" + baseDate +
                "&base_time=" + baseTime +
                "&nx=60&ny=127&numOfRows=20&pageSize=10&pageNo=1&startPage=1&_type=json"
        Log.d("!@#data_url ", dataUrl)
    }

    private fun setTime() {
        val intTimeHour = timeHour!!.toInt()
        baseTime = if (intTimeHour in 1..2) "2300" else if (intTimeHour in 3..5) "0200" else if (intTimeHour in 6..8) "0500" else if (intTimeHour in 9..11) "0800" else if (intTimeHour in 12..14) "1100" else if (intTimeHour in 15..17) "1400" else if (intTimeHour in 18..20) "1700" else "2000"
        Log.d("!@#base_time ", baseTime)
    }

    private fun saveTodo() {
        realm.beginTransaction()
        val realmObject: TodoRealmObject = realm.createObject(TodoRealmObject::class.java)
        realmObject.title = todoTitle.toString()
        realmObject.content = todoContent.toString()
        realmObject.date = todoDate.toString()
        realm.commitTransaction()
    }

    private fun setTodo() {
        val realmResults = realm.where(TodoRealmObject::class.java).findAll()
        for (i in 0 until realmResults.size) {
            dataList.add(TodoItem(realmResults[i].title, realmResults[i].content, realmResults[i].date))
        }
        val adapter = TodoRecyclerAdapter(dataList)
        binding!!.layoutContent.recyclerview.adapter = adapter
    }

    private fun selectTodo(calendarDate: String) {
        val realmResults = realm.where(TodoRealmObject::class.java).equalTo("date", calendarDate).findAll()
        for (i in 0 until realmResults.size) {
            dataList.add(TodoItem(realmResults[i].title, realmResults[i].content, realmResults[i].date))
        }
        val adapter = TodoRecyclerAdapter(dataList)
        binding.layoutContent.recyclerview.adapter = adapter
    }

    private fun removeTodo(position: Int) {
        val realmResults = realm.where(TodoRealmObject::class.java)
        val qwer = realmResults.equalTo("content", realmResults(position).getContent()).findAll()
        realm.executeTransaction { realmResults.deleteFromRealm(0) }
        setTodo()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                1000 -> {
                    todoTitle = data!!.getStringExtra("todoTitle")
                    todoContent = data.getStringExtra("todoContent")
                    todoDate = data.getStringExtra("todoDate")
                    saveTodo()
                    setTodo()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    /*private void getLocation() {
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
    }*/
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return true
    }
}