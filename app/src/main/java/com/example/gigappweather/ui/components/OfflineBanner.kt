package com.example.gigappweather.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.gigappweather.R

@Composable
fun OfflineBanner(
    isSimulated: Boolean,
    modifier: Modifier = Modifier,
) {
    Surface(
        tonalElevation = 2.dp,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(id = R.string.offline_banner_message),
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = stringResource(id = if (isSimulated) R.string.offline_badge_demo else R.string.offline_badge_offline),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
