package com.elad.kce.demo.data

import android.content.Context
import android.util.Log
import com.elad.kce.demo.City
import org.json.JSONArray
import java.nio.charset.Charset

object AssetCities {
    private const val FILE = "cities.json"

    fun load(context: Context): List<City> {
        return try {
            val json = context.assets.open(FILE).use { it.readBytes().toString(Charset.forName("UTF-8")) }
            val arr = JSONArray(json)
            val out = ArrayList<City>(arr.length())
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                out += City(
                    name = o.getString("name"),
                    lat = o.getDouble("lat"),
                    lon = o.getDouble("lon"),
                    elev = o.optDouble("elev", 0.0)
                )
            }
            Log.d("KCE", "Loaded ${out.size} cities from assets/$FILE")
            out
        } catch (t: Throwable) {
            Log.e("KCE", "Failed to load cities from assets/$FILE", t)
            emptyList()
        }
    }
}