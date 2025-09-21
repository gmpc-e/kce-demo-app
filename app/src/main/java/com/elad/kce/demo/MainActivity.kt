package com.elad.kce.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.elad.kce.demo.ui.components.ControlsBar
import com.elad.kce.demo.ui.theme.KceTheme
import com.elad.kce.demo.ui.theme.PurplePrimary

class MainActivity : ComponentActivity() {
  private val vm: MainViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      androidx.compose.runtime.CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        KceTheme {
          AppScreen(vm)
        }
      }
    }
  }
}

@Composable
fun AppScreen(vm: MainViewModel) {
  val state = remember { derivedStateOf { vm.state } }

  androidx.compose.material3.Scaffold(
    containerColor = MaterialTheme.colorScheme.surface,
    topBar = {
      TopAppBar(
        title = { /* empty (no "KCE Demo") */ },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    }
  ) { padding ->
    LazyColumn(
      modifier = Modifier
        .padding(padding)
        .fillMaxSize(),
      contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // (1) DATE on top
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
          horizontalArrangement = Arrangement.Center
        ) {
          Text(
            text = state.value.date.toString(),
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface
          )
        }
      }

      // (2) Day navigation row: Prev (right), Today (center), Next (left) — RTL layout
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Right (RTL): Prev Day
          TextButton(onClick = vm::prevDay) {
            Text("אתמול", style = MaterialTheme.typography.labelSmall)
          }
          // Center: Today
          FilledTonalButton(onClick = vm::today) {
            Text("היום", style = MaterialTheme.typography.labelSmall)
          }
          // Left (RTL): Next Day
          TextButton(onClick = vm::nextDay) {
            Text("מחר", style = MaterialTheme.typography.labelSmall)
          }
        }
      }

      // (3) Controls bar: לוח + עיר
      item {
        ControlsBar(
          profiles = state.value.profiles,
          selectedProfileIdx = state.value.selectedProfileIdx,
          onSelectProfile = vm::selectProfile,
          cities = state.value.cities,
          selectedCityIdx = state.value.selectedCityIdx,
          onSelectCity = vm::selectCity
        )
      }

      // (4) Optional section title
      item {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
          Text(
            "זמנים יהודיים",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            color = PurplePrimary
          )
        }
      }

      // (5) Results / Loading / Error
      when {
        state.value.loading -> {
          item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
              CircularProgressIndicator()
            }
          }
        }
        state.value.error != null -> {
          item { ErrorCard(state.value.error!!) }
        }
        state.value.result.isNotEmpty() -> {
          items(state.value.result) { item ->
            ZmanRow(
              label = item.labelHe, // RIGHT
              time = "%02d:%02d".format(item.time.hour, item.time.minute) // LEFT
            )
          }
        }
        else -> {
          item { PlaceholderCard() }
        }
      }
    }
  }
}

@Composable
private fun ZmanRow(label: String, time: String) {
  androidx.compose.material3.Surface(
    tonalElevation = 3.dp,
    shape = MaterialTheme.shapes.large,
    color = MaterialTheme.colorScheme.surfaceVariant,
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      Modifier
        .padding(horizontal = 14.dp, vertical = 10.dp)
        .fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // LABEL on the RIGHT — right aligned & smaller font
      Text(
        text = label,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurface
      )
      // TIME on the LEFT — bold & purple (smaller)
      Text(
        text = time,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
        color = PurplePrimary
      )
    }
  }
}

@Composable
private fun ErrorCard(msg: String) {
  ElevatedCard(Modifier.fillMaxWidth()) {
    Column(Modifier.padding(16.dp)) {
      Text("שגיאה", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.error)
      Spacer(Modifier.height(6.dp))
      Text(msg)
    }
  }
}

@Composable
private fun PlaceholderCard() {
  ElevatedCard(Modifier.fillMaxWidth()) {
    Column(Modifier.padding(16.dp)) {
      Text("בחר עיר ולוח כדי להציג זמנים", style = MaterialTheme.typography.titleMedium)
      Text("השתמש בבחירת היום/אתמול/מחר.")
    }
  }
}