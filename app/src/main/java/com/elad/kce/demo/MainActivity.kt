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

class MainActivity : ComponentActivity() {
    private val vm: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                KceTheme { AppScreen(vm = vm) }
            }
        }
    }
}

@Composable
fun AppScreen(vm: MainViewModel) {
    val state = vm.state

    Scaffold(containerColor = Color(0xFFF5F5F5)) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                TopSection(
                    profiles = state.profiles,
                    selectedProfileIdx = state.selectedProfileIdx,
                    onSelectProfile = vm::selectProfile,
                    cities = state.cities,
                    selectedCityIdx = state.selectedCityIdx,
                    onSelectCity = vm::selectCity,
                    onPrev = vm::prevDay,
                    onToday = vm::today,
                    onNext = vm::nextDay,
                    hebrewHeader = state.hebrewHeader
                )
            }

            when {
                state.loading -> item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        CircularProgressIndicator()
                    }
                }
                state.error != null -> item { ErrorCard(state.error) }
                state.result.isNotEmpty() -> items(state.result) { item ->
                    val weight = if (item.bold) FontWeight.ExtraBold else FontWeight.Normal
                    val labelColor = if (item.bold) Color(0xFF1565C0) else Color(0xFF212121)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.labelHe,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = weight),
                            color = labelColor,
                            textAlign = TextAlign.Right
                        )
                        Text(
                            text = "%02d:%02d".format(item.time.hour, item.time.minute),
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = if (item.bold) Color(0xFF1565C0) else Color(0xFF000000)
                        )
                    }
                }
                else -> item { PlaceholderCard() }
            }
        }
    }
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
