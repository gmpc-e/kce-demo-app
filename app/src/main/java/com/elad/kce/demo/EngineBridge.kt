package com.elad.kce.demo

import android.app.Application
import android.util.Log
import com.elad.halacha.engine.profiles.ProfilesServiceImpl
import com.elad.halacha.profiles.api.GeoInput
import com.elad.halacha.profiles.api.ProfileComputeInput
import com.elad.halacha.profiles.api.ProfileComputeResponse
import com.elad.halacha.profiles.api.ProfilesService
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZonedDateTime

object EngineBridge {
    private const val TAG = "EngineBridge"
    private val svc: ProfilesService by lazy { ProfilesServiceImpl() }

    fun listProfiles(app: Application): List<UiProfile> {
        return try {
            val engine = svc.listProfiles()
            Log.i(TAG, "listProfiles(): engine returned ${engine.size} items")
            if (engine.isNotEmpty()) {
                engine.map { UiProfile(key = it.key, displayName = it.displayName) }
            } else {
                val fromAssets = loadProfilesFromAssets(app)
                Log.w(TAG, "listProfiles(): using assets fallback -> ${fromAssets.size} items")
                fromAssets
            }
        } catch (t: Throwable) {
            Log.e(TAG, "listProfiles() failed, falling back to assets", t)
            loadProfilesFromAssets(app)
        }
    }

    fun computeProfile(key: String, date: LocalDate, city: City): Result<List<ZmanItem>> =
        runCatching {
            val input = ProfileComputeInput(
                dateIso = date.toString(),
                geo = GeoInput(lat = city.lat, lon = city.lon, elev = city.elev ?: 0.0, tz = city.tz),
                candleOffsetMinutes = city.candleMinutes.toDouble()
            )
            Log.i(TAG, "computeProfile(): key=$key date=${input.dateIso} city=${city.name} tz=${city.tz} candleMin=${city.candleMinutes}")

            val resp: ProfileComputeResponse = svc.computeProfile(key, input)
            Log.i(TAG, "computeProfile(): results=${resp.results.size}, warnings=${resp.warnings.size}")
            resp.warnings.forEachIndexed { i, w ->
                Log.w(TAG, "warn[$i] path=${w.path} code=${w.code} msg=${w.message}")
            }

            resp.results.mapNotNull { r ->
                val he = r.label?.he ?: r.label?.en ?: r.id
                val localIso = r.local ?: return@mapNotNull null
                val localTime = parseLocalTime(localIso) ?: return@mapNotNull null
                val isShabbat = r.resolution.kind == "SHABBAT"
                ZmanItem(labelHe = he, time = localTime, bold = isShabbat)
            }
        }

    private fun loadProfilesFromAssets(app: Application): List<UiProfile> {
        return try {
            app.assets.open("profiles_index.json").use { ins ->
                val text = BufferedReader(InputStreamReader(ins)).readText()
                val root = JSONObject(text)
                val arr = root.optJSONArray("profiles") ?: return emptyList()
                buildList {
                    for (i in 0 until arr.length()) {
                        val obj = arr.getJSONObject(i)
                        val key = obj.getString("key")
                        val display = obj.optString("displayName", key)
                        add(UiProfile(key = key, displayName = display))
                    }
                }
            }
        } catch (t: Throwable) {
            Log.e(TAG, "loadProfilesFromAssets() failed", t)
            emptyList()
        }
    }

    private fun parseLocalTime(iso: String) = try {
        OffsetDateTime.parse(iso).toLocalTime()
    } catch (_: Throwable) {
        try { ZonedDateTime.parse(iso).toLocalTime() } catch (_: Throwable) { null }
    }
}
