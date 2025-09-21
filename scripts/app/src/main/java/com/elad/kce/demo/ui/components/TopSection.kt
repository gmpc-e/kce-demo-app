package com.elad.kce.demo.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.ui.text.style.TextAlign
import com.elad.kce.demo.ui.theme.PurplePrimary

@Composable
fun TopSection(
    dateText: String,
    onPrevDay: () -> Unit,
    onToday: () -> Unit,
    onNextDay: () -> Unit,
    profileLabel: String = "לוח",
    profileValue: String?,
    onProfileClick: () -> Unit,
    cityLabel: String = "עיר",
    cityValue: String?,
    onCityClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars.only(WindowInsetsSides.Top))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1) Date line — centered, smaller, subtle
        Text(
            text = dateText,
            style = MaterialTheme.typography.titleSmall.copy(
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )

        // 2) Day controls — compact & evenly spaced
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SmallTonalButton(
                text = "אתמול",
                modifier = Modifier.weight(1f),
                onClick = onPrevDay
            )
            SmallFilledButton(
                text = "היום",
                modifier = Modifier.weight(1f),
                onClick = onToday
            )
            SmallTonalButton(
                text = "מחר",
                modifier = Modifier.weight(1f),
                onClick = onNextDay
            )
        }

        // 3) Selectors — two half-width, compact, show label: value
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LabeledSelector(
                label = profileLabel,
                value = profileValue ?: "—",
                modifier = Modifier.weight(1f),
                onClick = onProfileClick
            )
            LabeledSelector(
                label = cityLabel,
                value = cityValue ?: "—",
                modifier = Modifier.weight(1f),
                onClick = onCityClick
            )
        }
    }
}

@Composable
private fun LabeledSelector(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.heightIn(min = 40.dp),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
        border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
    ) {
        // "לוח: אור החיים" / "עיר: הוד השרון"
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onSurface, // white on dark
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// Small buttons used above

@Composable
private fun SmallFilledButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = 36.dp),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PurplePrimary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp)
        )
    }
}

@Composable
private fun SmallTonalButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = modifier.heightIn(min = 36.dp),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp)
        )
    }
}