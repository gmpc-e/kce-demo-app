package com.elad.kce.demo.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.elad.kce.demo.ZmanItem
import com.elad.kce.demo.ui.theme.PurplePrimary

@Composable
fun TimesList(items: List<ZmanItem>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(items) { it ->
            ZmanRow(label = it.labelHe, time = "%02d:%02d".format(it.time.hour, it.time.minute))
        }
        item {
            Spacer(Modifier.height(12.dp))
            HorizontalDivider()
        }
    }
}

/** Label on the RIGHT (aligned to the right), time on the LEFT, RTL-friendly. */
@Composable
private fun ZmanRow(label: String, time: String) {
    Surface(
        tonalElevation = 1.dp,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // LABEL — right aligned
            Text(
                label,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f),
                color = PurplePrimary
            )
            // TIME — left, bold purple
            Text(
                time,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = PurplePrimary
            )

        }
    }
}