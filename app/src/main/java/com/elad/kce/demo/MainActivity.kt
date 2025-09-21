package com.elad.kce.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
  private val vm: MainViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      MaterialTheme {
        AppScreen(vm)
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScreen(vm: MainViewModel) {
  val state by vm.state

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = state.result?.profileName ?: "Kosher Time Engine Demo",
            fontWeight = FontWeight.SemiBold
          )
        }
      )
    }
  ) { padding ->
    Column(
      modifier = Modifier
        .padding(padding)
        .padding(16.dp)
        .fillMaxSize(),
      verticalArrangement = Arrangement.Top
    ) {
      // Row: city dropdown + profile dropdown
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        CityDropdown(
          cities = state.cities,
          selected = state.selectedCityIdx,
          onSelect = vm::selectCity,
          modifier = Modifier.weight(1f)
        )
        ProfileDropdown(
          profiles = state.profiles,
          selected = state.selectedProfileIdx,
          onSelect = vm::selectProfile,
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(Modifier.height(12.dp))

      // Date controls
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Button(onClick = vm::prevDay) { Text("<< אתמול") }
        Button(onClick = vm::today) { Text("היום") }
        Button(onClick = vm::nextDay) { Text("מחר >>") }
      }

      Spacer(Modifier.height(16.dp))

      when {
        state.loading -> {
          Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            CircularProgressIndicator()
          }
        }
        state.error != null -> Text("שגיאה: ${state.error}", color = MaterialTheme.colorScheme.error)
        state.result != null -> ZmanimList(state.result!!)
        else -> Text("בחר עיר ופרופיל כדי לחשב זמנים.")
      }
    }
  }
}

@Composable
fun CityDropdown(
  cities: List<City>,
  selected: Int,
  onSelect: (Int) -> Unit,
  modifier: Modifier = Modifier
) {
  var expanded by remember { mutableStateOf(false) }
  val label = cities.getOrNull(selected)?.name ?: "—"

  Box(modifier) {
    OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
      Text(label)
    }
    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
      cities.forEachIndexed { idx, city ->
        DropdownMenuItem(
          text = { Text(city.name) },
          onClick = { expanded = false; onSelect(idx) }
        )
      }
    }
  }
}

@Composable
fun ProfileDropdown(
  profiles: List<EngineBridge.UiProfile>,
  selected: Int,
  onSelect: (Int) -> Unit,
  modifier: Modifier = Modifier
) {
  var expanded by remember { mutableStateOf(false) }
  val label = profiles.getOrNull(selected)?.displayName ?: "—"

  Box(modifier) {
    OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
      Text(label)
    }
    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
      profiles.forEachIndexed { idx, p ->
        DropdownMenuItem(
          text = { Text(p.displayName) },
          onClick = { expanded = false; onSelect(idx) }
        )
      }
    }
  }
}

@Composable
fun ZmanimList(result: ComputeResultDemo) {
  Column(Modifier.fillMaxWidth()) {
    Text(
      text = "${result.locationName} — ${result.date}",
      style = MaterialTheme.typography.titleMedium
    )
    Spacer(Modifier.height(8.dp))
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      items(result.times) { item ->
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
          Text(item.labelHe, fontWeight = FontWeight.Medium)
          Text("%02d:%02d".format(item.time.hour, item.time.minute))
        }
      }
    }
  }
}