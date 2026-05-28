package com.elad.kce.demo

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kosherjava.zmanim.hebrewcalendar.HebrewDateFormatter
import com.kosherjava.zmanim.hebrewcalendar.JewishCalendar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.time.LocalDate
import java.util.GregorianCalendar

data class UiState(
    val date: LocalDate = LocalDate.now(),
    val hebrewHeader: String = hebrewHeaderFor(LocalDate.now()),
    val cities: List<City> = emptyList(),
    val selectedCityIdx: Int = 0,
    val profiles: List<UiProfile> = emptyList(),
    val selectedProfileIdx: Int = 0,
    val loading: Boolean = false,
    val error: String? = null,
    val result: List<ZmanItem> = emptyList()
)

internal fun hebrewHeaderFor(date: LocalDate): String {
    val cal = GregorianCalendar(date.year, date.monthValue - 1, date.dayOfMonth)
    val jc = JewishCalendar(cal).apply { setInIsrael(true) }
    val hdf = HebrewDateFormatter().apply { isHebrewFormat = true }
    return "זמנים ליום ${hdf.formatDayOfWeek(jc)} ${hdf.format(jc)}"
}

class MainViewModel(app: Application) : AndroidViewModel(app) {

    private val TAG = "MainViewModel"

    var state by mutableStateOf(UiState())
        private set

    private var computeJob: Job? = null

    init {
        val loadedCities = loadCitiesFromAssets()
        state = state.copy(
            cities = loadedCities,
            selectedCityIdx = if (loadedCities.isNotEmpty()) 0 else -1
        )
        Log.i(TAG, "Loaded ${loadedCities.size} cities from assets")
        refreshProfiles()
    }

    fun prevDay() {
        val newDate = state.date.minusDays(1)
        state = state.copy(date = newDate, hebrewHeader = hebrewHeaderFor(newDate))
        computeIfPossible()
    }

    fun today() {
        val newDate = LocalDate.now()
        state = state.copy(date = newDate, hebrewHeader = hebrewHeaderFor(newDate))
        computeIfPossible()
    }

    fun nextDay() {
        val newDate = state.date.plusDays(1)
        state = state.copy(date = newDate, hebrewHeader = hebrewHeaderFor(newDate))
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

    fun refreshProfiles() {
        viewModelScope.launch {
            val profiles = withContext(Dispatchers.Default) {
                runCatching { EngineBridge.listProfiles(getApplication()) }
                    .onFailure { Log.e(TAG, "Failed to load profiles from engine", it) }
                    .getOrElse { emptyList() }
            }
            state = state.copy(
                profiles = profiles,
                selectedProfileIdx = if (profiles.isNotEmpty()) 0 else -1
            )
            computeIfPossible()
        }
    }

    private fun loadCitiesFromAssets(): List<City> {
        return try {
            val json = getApplication<Application>()
                .assets.open("cities.json")
                .bufferedReader().use { it.readText() }
            val arr = JSONArray(json)
            val out = ArrayList<City>(arr.length())
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                val name = o.optString("name", "")
                val lat = o.optDouble("lat", Double.NaN)
                val lon = o.optDouble("lon", Double.NaN)
                val elev = o.optDouble("elev", 0.0)
                val tz = o.optString("tz", "Asia/Jerusalem")
                if (name.isNotBlank() && !lat.isNaN() && !lon.isNaN()) {
                    val candleMin = o.optInt("candleMinutes", 30)
                    out.add(City(name = name, lat = lat, lon = lon, elev = elev, candleMinutes = candleMin, tz = tz))
                }
            }
            out
        } catch (t: Throwable) {
            Log.e(TAG, "Failed to load assets/cities.json", t)
            emptyList()
        }
    }

    private fun computeIfPossible() {
        val profile = state.profiles.getOrNull(state.selectedProfileIdx) ?: run {
            Log.w(TAG, "computeIfPossible: no profile selected yet")
            return
        }
        val city = state.cities.getOrNull(state.selectedCityIdx) ?: run {
            Log.w(TAG, "computeIfPossible: no city selected yet")
            return
        }

        computeJob?.cancel()
        computeJob = viewModelScope.launch {
            state = state.copy(loading = true, error = null)
            Log.i(TAG, "computeProfile -> key=${profile.key}, date=${state.date}, city=${city.name} tz=${city.tz}")

            val res = withContext(Dispatchers.Default) {
                EngineBridge.computeProfile(key = profile.key, date = state.date, city = city)
            }

            state = res.fold(
                onSuccess = { list ->
                    Log.i(TAG, "computeProfile SUCCESS: ${list.size} items")
                    state.copy(loading = false, result = list, error = null)
                },
                onFailure = { t ->
                    Log.e(TAG, "computeProfile ERROR", t)
                    state.copy(loading = false, error = t.message ?: "שגיאה", result = emptyList())
                }
            )
        }
    }
}
