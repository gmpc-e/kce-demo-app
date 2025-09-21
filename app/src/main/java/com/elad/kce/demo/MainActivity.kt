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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.elad.kce.demo.ui.components.TopSection
import com.elad.kce.demo.ui.theme.KceTheme
import com.kosherjava.zmanim.hebrewcalendar.HebrewDateFormatter
import com.kosherjava.zmanim.hebrewcalendar.JewishCalendar
import java.time.LocalDate
import java.util.GregorianCalendar

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
  val state = remember { derivedStateOf { vm.state } }

  Scaffold(containerColor = Color(0xFFF5F5F5)) { padding ->
    LazyColumn(
      modifier = Modifier
        .padding(padding)
        .fillMaxSize(),
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // Top: title, city & board (right-aligned), header + compact day chips
      item {
        TopSection(
          date = state.value.date,
          profiles = state.value.profiles,
          selectedProfileIdx = state.value.selectedProfileIdx,
          onSelectProfile = vm::selectProfile,
          cities = state.value.cities,
          selectedCityIdx = state.value.selectedCityIdx,
          onSelectCity = vm::selectCity,
          onPrev = vm::prevDay,
          onToday = vm::today,
          onNext = vm::nextDay,
          hebrewHeader = hebrewHeaderLine(state.value.date) // <-- computed here
        )
      }

      // Results / Loading / Error — keep clean & simple
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
          items(state.value.result) { it ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = it.labelHe, // right
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF212121),
                textAlign = TextAlign.Right
              )
              Text(
                text = "%02d:%02d".format(it.time.hour, it.time.minute), // left
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF000000)
              )
            }
          }
        }
        else -> {
          item { PlaceholderCard() }
        }
      }
    }
  }
}

/** Example: "זמנים ליום ראשון כ״ח אלול תשפ״ה" using KosherJava (no ICU, no manual maps). */
/** Example: "זמנים ליום ראשון כ״ח אלול תשפ״ה" */
private fun hebrewHeaderLine(date: LocalDate): String {
  val cal = GregorianCalendar(
    date.year,
    date.monthValue - 1, // <-- 0-based month for GregorianCalendar
    date.dayOfMonth
  )
  val jc = JewishCalendar(cal).apply { setInIsrael(true) }
  val hdf = HebrewDateFormatter().apply { isHebrewFormat = true }

  val dow  = hdf.formatDayOfWeek(jc) // ראשון
  val full = hdf.format(jc)          // כ״ח אלול תשפ״ה
  return "זמנים ליום $dow $full"
}

@Composable
private fun ErrorCard(msg: String) {
  ElevatedCard(Modifier.fillMaxWidth()) {
    Column(Modifier.padding(16.dp)) {
      Text("שגיאה", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.error)
      Spacer(Modifier.height(6.dp))
      Text(msg, style = MaterialTheme.typography.bodySmall)
    }
  }
}

@Composable
private fun PlaceholderCard() {
  ElevatedCard(Modifier.fillMaxWidth()) {
    Column(Modifier.padding(16.dp)) {
      Text("בחר עיר ולוח כדי להציג זמנים", style = MaterialTheme.typography.titleMedium)
      Text("השתמש בכפתורי היום/אחורה/קדימה למעבר בין ימים.", style = MaterialTheme.typography.bodySmall)
    }
  }
}