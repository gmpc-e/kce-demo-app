package com.elad.kce.demo.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.elad.kce.demo.City
import com.elad.kce.demo.UiProfile
import java.time.LocalDate

/**
 * Simple, clean, RTL:
 *  - Centered title
 *  - עיר: <name>  (right aligned, clickable)
 *  - שיטה: <name> (right aligned, clickable)
 *  - Card with Hebrew header centered and compact day chips underneath
 */
@Composable
fun TopSection(
    date: LocalDate,
    profiles: List<UiProfile>,
    selectedProfileIdx: Int,
    onSelectProfile: (Int) -> Unit,
    cities: List<City>,
    selectedCityIdx: Int,
    onSelectCity: (Int) -> Unit,
    onPrev: () -> Unit,
    onToday: () -> Unit,
    onNext: () -> Unit,
    hebrewHeader: String
) {
    var cityMenuOpen by remember { mutableStateOf(false) }
    var profileMenuOpen by remember { mutableStateOf(false) }

    val Blue = Color(0xFF1E88E5)
    val TextPrimary = Color(0xFF212121)
    val CardBg = Color(0xFFF0F2F5)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        // Title centered (25% bigger feel via titleLarge + bold)
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text(
                text = "זמני היום בהלכה",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = Blue,
                textAlign = TextAlign.Right
            )
        }

        Spacer(Modifier.height(10.dp))

        // עיר: <name>  — fully right aligned (text + click anchor)
        val cityName = cities.getOrNull(selectedCityIdx)?.name ?: "בחר עיר"
        Row(Modifier.fillMaxWidth()) {
            Text(
                text = "עיר: $cityName",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = Blue,
                textAlign = TextAlign.Right,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { cityMenuOpen = true }
                    .padding(vertical = 2.dp)
            )
        }
        DropdownMenu(
            expanded = cityMenuOpen,
            onDismissRequest = { cityMenuOpen = false }
        ) {
            cities.forEachIndexed { idx, c ->
                DropdownMenuItem(
                    text = { Text(c.name) },
                    onClick = {
                        cityMenuOpen = false
                        onSelectCity(idx)
                    }
                )
            }
        }

        Spacer(Modifier.height(6.dp))

        // שיטה: <name> — fully right aligned
        val boardName = profiles.getOrNull(selectedProfileIdx)?.displayName ?: "בחר לוח"
        Row(Modifier.fillMaxWidth()) {
            Text(
                text = "שיטה: $boardName",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = TextPrimary,
                textAlign = TextAlign.Right,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { profileMenuOpen = true }
                    .padding(vertical = 2.dp)
            )
        }
        DropdownMenu(
            expanded = profileMenuOpen,
            onDismissRequest = { profileMenuOpen = false }
        ) {
            profiles.forEachIndexed { idx, p ->
                DropdownMenuItem(
                    text = { Text(p.displayName) },
                    onClick = {
                        profileMenuOpen = false
                        onSelectProfile(idx)
                    }
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        // Header card: Hebrew header centered + compact day chips (Next on the RIGHT in RTL)
        Surface(
            color = CardBg,
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 0.dp,
            shadowElevation = 0.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Hebrew header
                Text(
                    text = hebrewHeader,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Blue,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                // Chips order is [Next, Today, Prev]; with RTL layout, Next appears on the RIGHT.
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AssistChip(
                        onClick = onPrev,
                        label = { Text("︎▶︎") },
                        colors = AssistChipDefaults.assistChipColors(
                            labelColor = Blue,
                            containerColor = Color.Transparent

                        )
                    )
                    AssistChip(
                        onClick = onToday,
                        label = { Text("היום") },
                        colors = AssistChipDefaults.assistChipColors(
                            labelColor = Blue,
                            containerColor = Blue.copy(alpha = 0.12f)
                        )
                    )
                    AssistChip(
                        onClick = onNext,
                        label = { Text("◀︎") },
                        colors = AssistChipDefaults.assistChipColors(
                            labelColor = Blue,
                            containerColor = Color.Transparent
                        )
                    )
                }
            }
        }
    }
}