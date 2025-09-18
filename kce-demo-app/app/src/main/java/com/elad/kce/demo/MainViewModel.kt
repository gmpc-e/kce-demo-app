package com.elad.kce.demo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import java.time.LocalDate

data class UiState(
  val cities: List<City> = emptyList(),
  val profiles: List<EngineBridge.EngineProfile> = emptyList(),
  val selectedCityIdx: Int = 0,
  val selectedProfileIdx: Int = 0,
  val date: LocalDate = LocalDate.now(),
  val loading: Boolean = false,
  val result: ComputeResultDemo? = null,
  val error: String? = null
)

class MainViewModel(app: Application) : AndroidViewModel(app) {
  private val json = Json { ignoreUnknownKeys = true }

  private val _state = MutableStateFlow(UiState())
  val state: StateFlow<UiState> = _state

  init {
    viewModelScope.launch {
      val cities = loadCities()
      _state.value = _state.value.copy(cities = cities)
      refreshProfiles()
    }
  }

  private fun ctx() = getApplication<Application>()

  private fun loadCities(): List<City> {
    val a = ctx().assets.open("cities.json").bufferedReader().use { it.readText() }
    return json.decodeFromString(a)
  }

  fun refreshProfiles() {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        _state.value = _state.value.copy(loading = true, error = null)
        val profiles = EngineBridge.getProfiles(ctx())
        _state.value = _state.value.copy(
          profiles = profiles,
          selectedProfileIdx = 0,
          loading = false
        )
        compute()
      } catch (e: Exception) {
        _state.value = _state.value.copy(loading = false, error = e.message ?: "Error loading profiles")
      }
    }
  }

  fun selectCity(idx: Int) {
    _state.value = _state.value.copy(selectedCityIdx = idx)
    compute()
  }

  fun selectProfile(idx: Int) {
    _state.value = _state.value.copy(selectedProfileIdx = idx)
    compute()
  }

  fun setDate(newDate: LocalDate) {
    _state.value = _state.value.copy(date = newDate)
    compute()
  }

  fun prevDay() = setDate(state.value.date.minusDays(1))
  fun nextDay() = setDate(state.value.date.plusDays(1))
  fun today() = setDate(LocalDate.now())

  fun compute() {
    viewModelScope.launch(Dispatchers.IO) {
      val s = _state.value
      if (s.cities.isEmpty() || s.profiles.isEmpty()) return@launch
      _state.value = s.copy(loading = true, error = null)
      try {
        val city = s.cities[s.selectedCityIdx]
        val profile = s.profiles[s.selectedProfileIdx]
        val req = ComputeRequestDemo(
          date = s.date,
          city = city,
          profileExternalName = profile.externalName
        )
        val res = EngineBridge.compute(ctx(), req)
        _state.value = _state.value.copy(loading = false, result = res)
      } catch (e: Exception) {
        _state.value = _state.value.copy(loading = false, error = e.message ?: "Error computing")
      }
    }
  }
}
