package com.sesong.mycalendar_kotlin

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    val secretKey =
        "%2Fci7Zc3Sb4%2ByZV9TNfQv3HfvWhiyu5ysfWzRMSDEOSIaec3gy8S%2BRBElcLe5PyHmFkTAI%2BjwwclokDJqCrV5XA%3D%3D"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(bar)

        getWeather()

        fab.setOnClickListener {
            /*val addIntent = Intent(this, TodoActivity::class.java)
            startActivity(addIntent)*/
        }
    }

    private fun getWeather() {
        Thread(Runnable {
            val url =
                URL("http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService2/ForecastSpaceData?serviceKey=%2Fci7Zc3Sb4%2ByZV9TNfQv3HfvWhiyu5ysfWzRMSDEOSIaec3gy8S%2BRBElcLe5PyHmFkTAI%2BjwwclokDJqCrV5XA%3D%3D&base_date=20181107&base_time=0500&nx=60&ny=127&numOfRows=10&pageSize=10&pageNo=1&startPage=1&_type=json")
            val inputStream = url.openStream()
            var i: Int
            val byteArray = ByteArray(8192)
            val stringBuffer: StringBuffer

            try {
                while (true){
                    val length = inputStream.read(byteArray)

                    if (length <= 0) break

                    stringBuffer.append(byteArray, 0, length)
                }
            }catch (t: Throwable){

            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.app_bar_search -> toast("Fav menu item is clicked!")
        }
        return true
    }
}