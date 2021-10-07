package com.darktornado.weatherinfo

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.os.StrictMode
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import org.jsoup.Jsoup
import java.io.IOException

class WeatherActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StrictMode.enableDefaults()
        val zoneId = intent.getStringExtra("zoneId")
        val data = getWeather(zoneId!!)
        if (data == null) {
            Toast.makeText(this, "날씨 정보 불러오기 실패", Toast.LENGTH_SHORT).show()
            return
        }
        val location = data.first

        actionBar!!.setTitle("날씨 정보 : $location")

        val layout = LinearLayout(this)
        layout.orientation = 1
        val txt = TextView(this)
        for (info in data.second) {
            txt.append("=== ${info!!.time} 날씨 ===\n" +
                    "상태 : ${info.status}\n" +
                    "온도 : ${info.temp}\n" +
                    "습도 : ${info.hum}\n" +
                    "바람 : ${info.windDir}, ${info.windSpeed}\n" +
                    "강수확률 : ${info.rain}\n\n")
        }
        txt.textSize = 18f;
        txt.gravity = Gravity.CENTER
        txt.setTextColor(Color.BLACK)
        layout.addView(txt)
        val pad = dip2px(16)
        layout.setPadding(pad, pad, pad, pad)
        val scroll = ScrollView(this)
        scroll.addView(layout)
        setContentView(scroll)
    }

    fun getWeather(zoneId: String): Pair<String, Array<Info?>>? {
        try {
            val data0 = Jsoup.connect("https://www.kma.go.kr/wid/queryDFSRSS.jsp?zone=$zoneId").get()
            val location = data0.select("category").text()

            val data = data0.select("body").select("data");
            val days = arrayOf("오늘", "내일", "모래")
            val result = arrayOfNulls<Info>(data.size / 2 + 1)
            var n = 0;
            while (n < data.size) {
                val datum = data.get(n);
                val time = days[datum.select("day").text().toInt()] + " " +
                        datum.select("hour").text() + "시"
                val status = datum.select("wfKor").text()
                val temp = datum.select("temp").text() + "℃"
                val hum = datum.select("reh").text() + "%"
                val rain = datum.select("pop").text() + "%"
                val windDir = datum.select("wdKor").text() + "풍"
                val windSpeed = (Math.round(datum.select("ws").text().toDouble() * 10) / 10).toString() + "m/s"
                result[n / 2] = Info(time, status, temp, hum, rain, windDir, windSpeed);
                n += 2;
            }
            return Pair(location, result)
        } catch (e: Exception) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
        }
        return null
    }

    fun dip2px(dips: Int) = Math.ceil((dips * getResources().getDisplayMetrics().density).toDouble()).toInt()

}