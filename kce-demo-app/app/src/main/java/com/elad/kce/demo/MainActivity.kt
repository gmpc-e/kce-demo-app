package com.elad.kce.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent { AppUI() }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppUI(vm: MainViewModel = viewModel()) {
  val state by vm.state.collectAsState()
  val timeFmt = remember { DateTimeFormatter.ofPattern("HH:mm") }
  MaterialTheme {
    Scaffold(
      topBar = { SmallTopAppBar(title = { Text("זמני היום — דמו") }) }
    ) { pad ->
      Column(Modifier.padding(pad).padding(12.dp)) {

        // City picker
        if (state.cities.isNotEmpty()) {
          var expanded by remember { mutableStateOf(false) }
          ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
            OutlinedTextField(
              modifier = Modifier.menuAnchor().fillMaxWidth(),
              readOnly = true,
              value = state.cities[state.selectedCityIdx].name,
              onValueChange = {},
              label = { Text(stringResource(id = R.string.choose_city)) }
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
              state.cities.forEachIndexed { idx, c ->
                DropdownMenuItem(
                  text = { Text(c.name) },
                  onClick = { vm.selectCity(idx); expanded = false }
                )
              }
            }
          }
        }

        Spacer(Modifier.height(8.dp))

        // Profile picker
        if (state.profiles.isNotEmpty()) {
          var expanded by remember { mutableStateOf(false) }
          ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
            OutlinedTextField(
              modifier = Modifier.menuAnchor().fillMaxWidth(),
              readOnly = true,
              value = state.profiles[state.selectedProfileIdx].labelHe,
              onValueChange = {},
              label = { Text(stringResource(id = R.string.choose_profile)) }
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
              state.profiles.forEachIndexed { idx, p ->
                DropdownMenuItem(
                  text = { Text(p.labelHe) },
                  onClick = { vm.selectProfile(idx); expanded = false }
                )
              }
            }
          }
        }

        Spacer(Modifier.height(8.dp))

        // Date controls
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
          OutlinedButton(onClick = { vm.prevDay() }) { Text(stringResource(id = R.string.prev)) }
          OutlinedButton(onClick = { vm.today() }) { Text(stringResource(id = R.string.today)) }
          OutlinedButton(onClick = { vm.nextDay() }) { Text(stringResource(id = R.string.next)) }
        }

        Spacer(Modifier.height(12.dp))

        if (state.loading) {
          Box(Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
          }
        }

        state.error?.let { Text("שגיאה: $it", color = MaterialTheme.colorScheme.error) }

        state.result?.let { res ->
          Text("${res.locationName} — ${res.profileName} — ${res.date}", fontWeight = FontWeight.Bold)
          Spacer(Modifier.height(8.dp))
          LazyColumn {
            items(res.times) { z ->
              Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(z.labelHe, fontWeight = FontWeight.Medium)
                Text(z.time.format(timeFmt))
              }
              Divider()
            }
          }
        }
      }
    }
  }
}
