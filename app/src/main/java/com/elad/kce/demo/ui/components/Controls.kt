package com.elad.kce.demo.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.elad.kce.demo.City
import com.elad.kce.demo.UiProfile

/**
 * Two compact selector boxes: "לוח: <value>" and "עיר: <value>"
 * - Right-to-left aligned
 * - Clickable -> shows dropdown menu
 * - Text is visible on dark theme (uses onSurface)
 */
@Composable
fun ControlsBar(
    modifier: Modifier = Modifier,
    profiles: List<UiProfile>,
    selectedProfileIdx: Int,
    onSelectProfile: (Int) -> Unit,
    cities: List<City>,
    selectedCityIdx: Int,
    onSelectCity: (Int) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SelectorField(
            label = "לוח",
            value = profiles.getOrNull(selectedProfileIdx)?.displayName ?: "—",
            items = profiles.map { it.displayName },
        ) { idx -> if (idx in profiles.indices) onSelectProfile(idx) }

        SelectorField(
            label = "עיר",
            value = cities.getOrNull(selectedCityIdx)?.name ?: "—",
            items = cities.map { it.name },
        ) { idx -> if (idx in cities.indices) onSelectCity(idx) }
    }
}

/**
 * A compact, pill-like selector that shows "label: value" and opens a menu.
 * Keeps fonts ~15% smaller than default for better fit.
 */
@Composable
private fun SelectorField(
    label: String,
    value: String,
    items: List<String>,
    onSelectIndex: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        onClick = { expanded = true },
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 2.dp,
        modifier = Modifier.clip(MaterialTheme.shapes.large)
    ) {
        Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
            // "label: value" — keep it short and readable
            Text(
                text = "$label: ",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }

    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        items.forEachIndexed { index, title ->
            DropdownMenuItem(
                text = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                onClick = {
                    expanded = false
                    onSelectIndex(index)
                }
            )
        }
    }
}