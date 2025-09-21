package com.elad.kce.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.elad.kce.demo.ui.theme.KceTheme
import com.elad.kce.demo.ui.theme.PurplePrimary
import com.kosherjava.zmanim.hebrewcalendar.HebrewDateFormatter
import com.kosherjava.zmanim.hebrewcalendar.JewishCalendar
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
  private val vm: MainViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        KceTheme { AppScreen(vm) }
      }
    }
  }
}

@Composable
fun AppScreen(vm: MainViewModel) {
  val state by remember { derivedStateOf { vm.state } }
  var cityMenuOpen by remember { mutableStateOf(false) }
  var profileMenuOpen by remember { mutableStateOf(false) }

  val gregFmt = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy") }
  val hebDateText = remember(state.date) { formatHebrewDate(state.date) }

  Scaffold(topBar = { /* no toolbar */ }) { padding ->
    LazyColumn(
      modifier = Modifier
        .padding(padding)
        .fillMaxSize(),
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // ───────── Title ─────────
      item {
        Text(
          text = "זמני היום בהלכה",
          style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
          textAlign = TextAlign.Center,
          modifier = Modifier.fillMaxWidth(),
          color = MaterialTheme.colorScheme.onSurface
        )
      }

      // ───────── Hebrew date (day-of-week + Hebrew date) ─────────
      item {
        Text(
          text = hebDateText,
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
          textAlign = TextAlign.Center,
          modifier = Modifier.fillMaxWidth(),
          color = MaterialTheme.colorScheme.onSurface
        )
      }

      // ───────── Gregorian date ─────────
      item {
        Text(
          text = state.date.format(gregFmt),
          style = MaterialTheme.typography.bodyMedium,
          textAlign = TextAlign.Center,
          modifier = Modifier.fillMaxWidth(),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      // ───────── City (clickable text, no box) ─────────
      item {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
          Text(
            text = state.cities.getOrNull(state.selectedCityIdx)?.name ?: "בחר עיר",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable { cityMenuOpen = true }
          )

          DropdownMenu(
            expanded = cityMenuOpen,
            onDismissRequest = { cityMenuOpen = false }
          ) {
            state.cities.forEachIndexed { idx, city ->
              DropdownMenuItem(
                text = { Text(city.name) },
                onClick = {
                  cityMenuOpen = false
                  vm.selectCity(idx)
                }
              )
            }
          }
        }
      }

      // ───────── Day controls (small boxes): Prev | Today | Next ─────────
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Prev (right in RTL)
          OutlinedButton(
            onClick = vm::prevDay,
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
          ) { Text("אתמול", style = MaterialTheme.typography.labelSmall) }

          Spacer(Modifier.width(8.dp))

          // Today (center)
          FilledTonalButton(
            onClick = vm::today,
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
          ) { Text("היום", style = MaterialTheme.typography.labelSmall) }

          Spacer(Modifier.width(8.dp))

          // Next (left in RTL)
          OutlinedButton(
            onClick = vm::nextDay,
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
          ) { Text("מחר", style = MaterialTheme.typography.labelSmall) }
        }
      }

      // ───────── Board line: "שיטה: <name>" (clickable, no box) ─────────
      item {
        val boardName = state.profiles
          .getOrNull(state.selectedProfileIdx)
          ?.display
          ?: state.profiles.getOrNull(state.selectedProfileIdx)?.displayName
          ?: "בחר לוח"

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.Center
        ) {
          Text(
            text = "שיטה: $boardName",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Medium),
            color = PurplePrimary,
            modifier = Modifier.clickable { profileMenuOpen = true }
          )
        }

        DropdownMenu(
          expanded = profileMenuOpen,
          onDismissRequest = { profileMenuOpen = false }
        ) {
          state.profiles.forEachIndexed { idx, p ->
            val name = p.display ?: p.displayName
            DropdownMenuItem(
              text = { Text(name) },
              onClick = {
                profileMenuOpen = false
                vm.selectProfile(idx)
              }
            )
          }
        }
      }

      // ───────── (Optional) Section title ─────────
      item {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
          Text(
            "רשימת זמנים",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
            color = PurplePrimary
          )
        }
      }

      // ───────── Results / Loading / Error ─────────
      when {
        state.loading -> {
          item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
              CircularProgressIndicator()
            }
          }
        }
        state.error != null -> {
          item { ErrorCard(state.error!!) }
        }
        state.result.isNotEmpty() -> {
          items(state.result) { item ->
            ZmanRow(
              // label RIGHT
              label = item.labelHe,
              // time LEFT
              time = "%02d:%02d".format(item.time.hour, item.time.minute)
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

/** LABEL right, TIME left */
@Composable
private fun ZmanRow(label: String, time: String) {
  Surface(
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
      Text(
        text = label,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurface
      )
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

/* ----------------- Helpers ----------------- */

private fun formatHebrewDate(date: LocalDate): String {
  // Convert LocalDate -> JewishCalendar (uses system TZ; fine for just day resolution)
  val zdt = date.atStartOfDay(ZoneId.systemDefault())
  val jc = JewishCalendar()
  jc.date = java.util.GregorianCalendar.from(zdt)

  val hdf = HebrewDateFormatter().apply {
    isHebrewFormat = true
    // Optional: use geresh/gereshayim punctuation for numbers.
  }

  // Example output: "ראשון, כ״ח אלול התשפ״ה"
  val dow = hdf.formatDayOfWeek(jc)              // יום בשבוע (e.g., ראשון)
  val hebDate = hdf.format(jc)                   // כ״ח אלול התשפ״ה
  return "$dow, $hebDate"
}