package com.example.gigappweather.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.gigappweather.R
import com.example.gigappweather.ui.components.OfflineBanner
import com.example.gigappweather.ui.viewmodel.AppViewModel
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Alignment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoScreen(
    appViewModel: AppViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isOnline by appViewModel.isOnline.collectAsState()
    val simulatedOffline by appViewModel.simulatedOffline.collectAsState()

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text(text = stringResource(id = R.string.info_title)) },
                    navigationIcon = {
                        TextButton(onClick = onBack) { Text(text = stringResource(id = R.string.back)) }
                    },
                )
                if (!isOnline) {
                    OfflineBanner(isSimulated = simulatedOffline)
                }
            }
        },
        modifier = modifier,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(text = stringResource(id = R.string.info_description))
            Text(text = stringResource(id = R.string.info_architecture))
            Text(text = stringResource(id = R.string.info_attribution))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(text = stringResource(id = R.string.simulate_offline))
                    Switch(
                        checked = simulatedOffline,
                        onCheckedChange = { appViewModel.setSimulatedOffline(it) },
                    )
                }
                Text(
                    text = stringResource(id = R.string.simulate_offline_hint),
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}
