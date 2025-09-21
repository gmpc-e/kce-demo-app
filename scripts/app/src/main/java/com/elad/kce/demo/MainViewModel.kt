package com.elad.kce.demo

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import org.json.JSONArray
import java.time.LocalDate

/**
 * Keep ONLY UiState here.
 * (City / UiProfile / ZmanItem live in Models.kt)
 */
data class UiState(
  val date: LocalDate = LocalDate.now(),
  val cities: List<City> = emptyList(),
  val selectedCityIdx: Int = 0,
  val profiles: List<UiProfile> = emptyList(),
  val selectedProfileIdx: Int = 0,
  val loading: Boolean = false,
  val error: String? = null,
  val result: List<ZmanItem> = emptyList()
)

class MainViewModel(app: Application) : AndroidViewModel(app) {

  private val TAG = "MainViewModel"

  var state by mutableStateOf(UiState())
    private set

  init {
    // 1) Load cities from assets
    val loadedCities = loadCitiesFromAssets()
    state = state.copy(
      cities = loadedCities,
      selectedCityIdx = if (loadedCities.isNotEmpty()) 0 else -1
    )
    Log.i(TAG, "Loaded ${loadedCities.size} cities from assets")

    // 2) Load boards from engine on start
    refreshProfiles()
  }

  // ---------- Top controls actions ----------

  fun prevDay() {
    state = state.copy(date = state.date.minusDays(1))
    computeIfPossible()
  }

  fun today() {
    state = state.copy(date = LocalDate.now())
    computeIfPossible()
  }

  fun nextDay() {
    state = state.copy(date = state.date.plusDays(1))
    computeIfPossible()
  }

  fun selectCity(idx: Int) {
    state = state.copy(selectedCityIdx = idx)
    computeIfPossible()
  }

  fun selectProfile(idx: Int) {
    state = state.copy(selectedProfileIdx = idx)
    computeIfPossible()
  }

  // ---------- Data loading ----------

  fun refreshProfiles() {
    runCatching {
      val profiles = EngineBridge.listProfiles()
      Log.i(TAG, "EngineBridge.listProfiles() -> ${profiles.size} items")
      state = state.copy(
        profiles = profiles,
        selectedProfileIdx = if (profiles.isNotEmpty()) 0 else -1
      )
    }.onFailure { t ->
      Log.e(TAG, "Failed to load profiles from engine", t)
      state = state.copy(profiles = emptyList(), selectedProfileIdx = -1, error = t.message)
    }

    // Try to compute immediately if we have both city + profile
    computeIfPossible()
  }

  private fun loadCitiesFromAssets(): List<City> {
    return try {
      val json = getApplication<Application>()
        .assets
        .open("cities.json")
        .bufferedReader()
        .use { it.readText() }

      val arr = JSONArray(json)
      val out = ArrayList<City>(arr.length())
      for (i in 0 until arr.length()) {
        val o = arr.getJSONObject(i)
        val name = o.optString("name", "")
        val lat = o.optDouble("lat", Double.NaN)
        val lon = o.optDouble("lon", Double.NaN)
        val elev = o.optDouble("elev", 0.0)
        // tz is optional in file; we ignore it because City in Models.kt has no tz field

        if (name.isNotBlank() && !lat.isNaN() && !lon.isNaN()) {
          out.add(City(name = name, lat = lat, lon = lon, elev = elev))
        }
      }
      out
    } catch (t: Throwable) {
      Log.e(TAG, "Failed to load assets/cities.json", t)
      emptyList()
    }
  }

  // ---------- Compute ----------

  private fun computeIfPossible() {
    val profile = state.profiles.getOrNull(state.selectedProfileIdx)
    val city = state.cities.getOrNull(state.selectedCityIdx)

    if (profile == null) {
      Log.w(TAG, "computeIfPossible: no profile selected yet")
      return
    }
    if (city == null) {
      Log.w(TAG, "computeIfPossible: no city selected yet")
      return
    }

    state = state.copy(loading = true, error = null)
    Log.i(
      TAG,
      "computeProfile -> key=${profile.key}, date=${state.date}, city=${city.name} (${city.lat},${city.lon}, elev=${city.elev})"
    )

    val res = EngineBridge.computeProfile(
      key = profile.key,
      date = state.date,
      city = city
    )

    state = res.fold(
      onSuccess = { list ->
        Log.i(TAG, "computeProfile SUCCESS: received ${list.size} items")
        state.copy(loading = false, result = list, error = null)
      },
      onFailure = { t ->
        Log.e(TAG, "computeProfile ERROR", t)
        state.copy(loading = false, error = t.message ?: "שגיאה", result = emptyList())
      }
    )
  }
}