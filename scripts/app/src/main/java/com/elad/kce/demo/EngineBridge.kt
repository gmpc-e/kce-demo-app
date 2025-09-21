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

/**
 * Bridge to the engine ProfilesService with Android-safe fallback for listing profiles.
 * City / UiProfile / ZmanItem are defined in Models.kt (do NOT redeclare them).
 */
object EngineBridge {
  private const val TAG = "EngineBridge"

  private var app: Application? = null
  private val svc: ProfilesService by lazy { ProfilesServiceImpl() }

  /** Call this once from MainActivity.onCreate() */
  fun init(appContext: Application) {
    app = appContext
    Log.i(TAG, "init(): EngineBridge initialized")
  }

  /**
   * List boards from engine. On Android, directory listing inside AAR often fails and returns [].
   * Fallback: read app/src/main/assets/profiles_index.json (keys + display names).
   */
  fun listProfiles(): List<UiProfile> {
    return try {
      val engine = svc.listProfiles()
      Log.i(TAG, "listProfiles(): engine returned ${engine.size} items")
      engine.forEachIndexed { i, p ->
        Log.i(TAG, "engine[$i]: key='${p.key}', displayName='${p.displayName}'")
      }
      if (engine.isNotEmpty()) {
        engine.map { UiProfile(key = it.key, displayName = it.displayName) }
      } else {
        // Fallback to assets index
        val fromAssets = loadProfilesFromAssets()
        Log.w(TAG, "listProfiles(): using assets fallback -> ${fromAssets.size} items")
        fromAssets
      }
    } catch (t: Throwable) {
      Log.e(TAG, "listProfiles() failed, falling back to assets", t)
      loadProfilesFromAssets()
    }
  }

  /**
   * Compute a selected board for date + city. Engine loads the profile JSON by key (that part
   * usually works on Android); listing is the part that fails.
   */
  fun computeProfile(
    key: String,
    date: LocalDate,
    city: City,
    tz: String = "Asia/Jerusalem"
  ): Result<List<ZmanItem>> = runCatching {
    val input = ProfileComputeInput(
      dateIso = date.toString(),
      geo = GeoInput(lat = city.lat, lon = city.lon, elev = city.elev ?: 0.0, tz = tz)
    )

    Log.i(TAG, "computeProfile(): key=$key date=${input.dateIso} city=${city.name} tz=$tz")
    val resp: ProfileComputeResponse = svc.computeProfile(key, input)
    Log.i(TAG, "computeProfile(): results=${resp.results.size}, warnings=${resp.warnings.size}")

    resp.warnings.forEachIndexed { i, w ->
      Log.w(TAG, "warn[$i] path=${w.path} code=${w.code} msg=${w.message}")
    }

    val items = resp.results.mapNotNull { r ->
      val he = r.label?.he ?: r.label?.en ?: r.id
      val localIso = r.local ?: return@mapNotNull null
      val localTime = parseLocalTime(localIso) ?: return@mapNotNull null

      Log.i(TAG, "res id=${r.id}, label='$he', local=$localIso, kind=${r.resolution.kind}, status=${r.resolution.status}")
      ZmanItem(labelHe = he, time = localTime)
    }
    items
  }

  // ——— helpers ———

  private fun loadProfilesFromAssets(): List<UiProfile> {
    val a = app ?: return emptyList()
    return try {
      a.assets.open("profiles_index.json").use { ins ->
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