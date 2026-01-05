package com.example.gigappweather.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.gigappweather.R
import com.example.gigappweather.core.UiState
import com.example.gigappweather.ui.components.ErrorView
import com.example.gigappweather.ui.components.GigCard
import com.example.gigappweather.ui.components.LoadingView
import com.example.gigappweather.ui.model.GigWeatherStatus
import com.example.gigappweather.ui.viewmodel.AddGigViewModel
import com.example.gigappweather.ui.viewmodel.GigListViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GigListScreen(
    viewModel: GigListViewModel,
    addGigViewModel: AddGigViewModel,
    onOpenDetail: (Long) -> Unit,
    onOpenInfo: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()
    val addState by addGigViewModel.state.collectAsState()
    val scope = rememberCoroutineScope()

    var showAddDialog by remember { mutableStateOf(false) }
    var deleteId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(addState) {
        if (addState is UiState.Success) {
            showAddDialog = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.gig_list_title)) },
                actions = {
                    TextButton(onClick = onOpenInfo) {
                        Text(text = stringResource(id = R.string.info))
                    }
                },
            )
        },
        modifier = modifier,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            val demoTitle = stringResource(id = R.string.demo_gig_title)
            val demoCity = stringResource(id = R.string.demo_city_oulu)

            RowActions(
                onAdd = { showAddDialog = true },
                onAddDemo = {
                    val demoDate = tomorrowIsoDate()
                    addGigViewModel.addGig(
                        title = demoTitle,
                        dateIso = demoDate,
                        city = demoCity,
                        isOutdoor = true,
                        createdAt = System.currentTimeMillis(),
                    )
                },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )

            when (val s = state) {
                UiState.Loading -> LoadingView()
                UiState.Empty -> EmptyState()
                is UiState.Error -> ErrorView(
                    messageRes = s.messageRes,
                    onRetry = onRetry,
                )
                is UiState.Success -> {
                    val anyWeatherLoading = s.data.any { it.weatherStatus == GigWeatherStatus.Loading }
                    if (anyWeatherLoading) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(s.data, key = { it.id }) { item ->
                            GigCard(
                                item = item,
                                onClick = { onOpenDetail(item.id) },
                                onDelete = { deleteId = item.id },
                            )
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            AddGigDialog(
                addState = addState,
                onDismiss = { showAddDialog = false },
                onSave = { title, dateIso, city, isOutdoor ->
                    addGigViewModel.addGig(
                        title = title,
                        dateIso = dateIso,
                        city = city,
                        isOutdoor = isOutdoor,
                        createdAt = System.currentTimeMillis(),
                    )
                },
            )
        }

        deleteId?.let { id ->
            ConfirmDeleteDialog(
                onConfirm = {
                    scope.launch { viewModel.deleteGig(id) }
                    deleteId = null
                },
                onDismiss = { deleteId = null },
            )
        }
    }
}

private fun tomorrowIsoDate(): String {
    val cal = Calendar.getInstance()
    cal.add(Calendar.DAY_OF_YEAR, 1)
    val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    return fmt.format(cal.time)
}

@Composable
private fun RowActions(
    onAdd: () -> Unit,
    onAddDemo: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = onAdd, modifier = Modifier.fillMaxWidth()) {
            Text(text = stringResource(id = R.string.add_gig))
        }
        OutlinedButton(onClick = onAddDemo, modifier = Modifier.fillMaxWidth()) {
            Text(text = stringResource(id = R.string.add_demo_gig))
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(id = R.string.empty_gigs),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun AddGigDialog(
    addState: UiState<Long>,
    onDismiss: () -> Unit,
    onSave: (title: String, dateIso: String, city: String, isOutdoor: Boolean) -> Unit,
) {
    var title by remember { mutableStateOf("") }
    var dateIso by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var isOutdoor by remember { mutableStateOf(true) }

    val canSave = title.isNotBlank() && dateIso.isNotBlank() && city.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.add_gig)) },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(text = stringResource(id = R.string.field_title)) },
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = dateIso,
                    onValueChange = { dateIso = it },
                    label = { Text(text = stringResource(id = R.string.field_date_iso)) },
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text(text = stringResource(id = R.string.field_city)) },
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(12.dp))

                Column {
                    Text(text = stringResource(id = R.string.field_is_outdoor))
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Checkbox(checked = isOutdoor, onCheckedChange = { isOutdoor = it })
                        Text(text = stringResource(id = R.string.outdoor_yes))
                    }
                }

                if (addState is UiState.Error) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = stringResource(id = addState.messageRes))
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(title, dateIso, city, isOutdoor) },
                enabled = canSave && addState !is UiState.Loading,
            ) {
                Text(text = stringResource(id = R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = addState !is UiState.Loading) {
                Text(text = stringResource(id = R.string.cancel))
            }
        },
    )
}

@Composable
private fun ConfirmDeleteDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.delete_confirm_title)) },
        text = { Text(text = stringResource(id = R.string.delete_confirm_body)) },
        confirmButton = {
            Button(onClick = onConfirm) { Text(text = stringResource(id = R.string.delete)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(text = stringResource(id = R.string.cancel)) }
        },
    )
}
