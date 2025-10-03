package com.elad.kce.demo.banner

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun Banner(vm: BannerViewModel, modifier: Modifier = Modifier) {
    val ui by vm.state.collectAsState()

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF0F2F5) // subtle gray to match your scheme
        )
    ) {
        Column(Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            if (ui.occasionHe != "—") {
                Text(
                    text = "מועד: ${ui.occasionHe}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = Color(0xFF1E88E5)
                )
                Spacer(Modifier.height(4.dp))
            }
            Text("שעה זמנית: ${ui.shaahHms}", style = MaterialTheme.typography.bodySmall)
            Text("דקה זמנית: ${ui.zmanitMinute}", style = MaterialTheme.typography.bodySmall)
            ui.candleHHmm?.let {
                Text("הדלקת נרות: $it", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}