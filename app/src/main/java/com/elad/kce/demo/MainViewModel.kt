package com.elad.kce.demo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

data class UiState(
  val cities: List<City> = emptyList(),
  val profiles: List<EngineBridge.UiProfile> = emptyList(),
  val selectedCityIdx: Int = 0,
  val selectedProfileIdx: Int = 0,
  val date: LocalDate = LocalDate.now(),
  val loading: Boolean = false,
  val result: ComputeResultDemo? = null,
  val error: String? = null
)

class MainViewModel : ViewModel() {

  private val _state = androidx.compose.runtime.mutableStateOf(UiState())
  val state: androidx.compose.runtime.State<UiState> get() = _state

  init {
    // Initial cities (can be replaced later with geolocation)
    val initialCities = listOf(
      City("הוד השרון", 32.1556, 34.8892, 40.0, "Asia/Jerusalem"),
      City("ירושלים", 31.7683, 35.2137, 754.0, "Asia/Jerusalem"),
      City("חיפה", 32.7940, 34.9896, 10.0, "Asia/Jerusalem"),
      City("אילת", 29.5577, 34.9519, 10.0, "Asia/Jerusalem")
    )
    _state.value = _state.value.copy(cities = initialCities)
    refreshProfiles()
  }

  fun prevDay() {
    _state.value = _state.value.copy(date = _state.value.date.minusDays(1))
    compute()
  }
  fun nextDay() {
    _state.value = _state.value.copy(date = _state.value.date.plusDays(1))
    compute()
  }
  fun today() {
    _state.value = _state.value.copy(date = LocalDate.now())
    compute()
  }

  fun selectCity(index: Int) {
    _state.value = _state.value.copy(selectedCityIdx = index)
    compute()
  }

  fun selectProfile(index: Int) {
    _state.value = _state.value.copy(selectedProfileIdx = index)
    compute()
  }

  fun refreshProfiles() {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        _state.value = _state.value.copy(loading = true, error = null)
        val profiles = EngineBridge.listProfiles()
        val sel = profiles.indexOfFirst { it.key.contains("or-hachaim") }.takeIf { it >= 0 } ?: 0
        _state.value = _state.value.copy(
          profiles = profiles,
          selectedProfileIdx = if (profiles.isNotEmpty()) sel else 0,
          loading = false
        )
        compute()
      } catch (e: Exception) {
        _state.value = _state.value.copy(loading = false, error = e.message ?: "Error loading profiles")
      }
    }
  }

  fun compute() {
    viewModelScope.launch(Dispatchers.IO) {
      val s = _state.value
      if (s.cities.isEmpty() || s.profiles.isEmpty()) return@launch
      _state.value = s.copy(loading = true, error = null)
      try {
        val city = s.cities[s.selectedCityIdx]
        val profile = s.profiles[s.selectedProfileIdx]

        val res = EngineBridge.computeProfile(
          profileKey = profile.key,
          date = s.date,
          lat = city.lat, lon = city.lon, elev = city.elev, tz = city.tz
        ).copy(locationName = city.name, date = s.date)

        _state.value = _state.value.copy(loading = false, result = res)
      } catch (e: Exception) {
        _state.value = _state.value.copy(loading = false, error = e.message ?: "Error computing")
      }
    }
  }
}