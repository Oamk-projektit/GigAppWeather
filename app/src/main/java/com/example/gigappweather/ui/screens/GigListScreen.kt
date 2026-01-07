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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import com.example.gigappweather.R
import com.example.gigappweather.core.UiState
import com.example.gigappweather.domain.model.FinnishCities
import com.example.gigappweather.ui.components.ErrorView
import com.example.gigappweather.ui.components.GigCard
import com.example.gigappweather.ui.components.LoadingView
import com.example.gigappweather.ui.components.OfflineBanner
import com.example.gigappweather.ui.viewmodel.AddGigViewModel
import com.example.gigappweather.ui.viewmodel.AppViewModel
import com.example.gigappweather.ui.viewmodel.GigListViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GigListScreen(
    viewModel: GigListViewModel,
    addGigViewModel: AddGigViewModel,
    appViewModel: AppViewModel,
    onOpenDetail: (Long) -> Unit,
    onOpenInfo: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()
    val addState by addGigViewModel.state.collectAsState()
    val isOnline by appViewModel.isOnline.collectAsState()
    val simulatedOffline by appViewModel.simulatedOffline.collectAsState()
    val scope = rememberCoroutineScope()

    val snackbarHostState = remember { SnackbarHostState() }
    val networkErrorMessage = stringResource(id = R.string.error_network)

    var showAddDialog by remember { mutableStateOf(false) }
    var deleteId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(addState) {
        if (addState is UiState.Success) {
            showAddDialog = false
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text(text = stringResource(id = R.string.gig_list_title)) },
                    actions = {
                        TextButton(onClick = onOpenInfo) {
                            Text(text = stringResource(id = R.string.info))
                        }
                    },
                )
                if (!isOnline) {
                    OfflineBanner(isSimulated = simulatedOffline)
                }
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = modifier,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            val demoTitle = stringResource(id = R.string.demo_gig_title)
            val demoCityId = "oulu"

            RowActions(
                onAdd = { showAddDialog = true },
                onAddDemo = {
                    val demoDate = tomorrowIsoDate()
                    addGigViewModel.addGig(
                        title = demoTitle,
                        dateIso = demoDate,
                        cityId = demoCityId,
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
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(s.data, key = { it.id }) { item ->
                            GigCard(
                                item = item,
                                onClick = { onOpenDetail(item.id) },
                                onDelete = { deleteId = item.id },
                                isOnline = isOnline,
                                onRetryWeather = {
                                    if (isOnline) {
                                        viewModel.retryWeatherForCity(item.cityId)
                                    } else {
                                        scope.launch {
                                            snackbarHostState.showSnackbar(
                                                message = networkErrorMessage,
                                            )
                                        }
                                    }
                                },
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
                onSave = { title, dateIso, cityId, isOutdoor ->
                    addGigViewModel.addGig(
                        title = title,
                        dateIso = dateIso,
                        cityId = cityId,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddGigDialog(
    addState: UiState<Long>,
    onDismiss: () -> Unit,
    onSave: (title: String, dateIso: String, cityId: String, isOutdoor: Boolean) -> Unit,
) {
    var title by remember { mutableStateOf("") }
    var dateIso by remember { mutableStateOf("") }
    var cityId by remember { mutableStateOf("") }
    var isOutdoor by remember { mutableStateOf(true) }

    val canSave = title.isNotBlank() && dateIso.isNotBlank() && cityId.isNotBlank()

    val cities = FinnishCities.all
    var cityExpanded by remember { mutableStateOf(false) }
    val selectedCityName = cities
        .firstOrNull { it.id == cityId }
        ?.let { stringResource(id = it.displayNameRes) }
        .orEmpty()

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
                DatePickerField(
                    label = stringResource(id = R.string.field_date_iso),
                    selectedDateIso = dateIso,
                    onDateSelectedIso = { dateIso = it },
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(8.dp))

                ExposedDropdownMenuBox(
                    expanded = cityExpanded,
                    onExpandedChange = { cityExpanded = !cityExpanded },
                ) {
                    OutlinedTextField(
                        value = selectedCityName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(text = stringResource(id = R.string.field_city_select)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cityExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                    )
                    DropdownMenu(
                        expanded = cityExpanded,
                        onDismissRequest = { cityExpanded = false },
                    ) {
                        cities.forEach { city ->
                            DropdownMenuItem(
                                text = { Text(text = stringResource(id = city.displayNameRes)) },
                                onClick = {
                                    cityId = city.id
                                    cityExpanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            )
                        }
                    }
                }
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
                onClick = { onSave(title, dateIso, cityId, isOutdoor) },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerField(
    label: String,
    selectedDateIso: String,
    onDateSelectedIso: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var open by remember { mutableStateOf(false) }

    val datePickerState = androidx.compose.material3.rememberDatePickerState()

    OutlinedTextField(
        value = selectedDateIso,
        onValueChange = { /* read-only */ },
        modifier = modifier,
        readOnly = true,
        label = { Text(label) },
        trailingIcon = {
            IconButton(onClick = { open = true }) {
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = stringResource(id = R.string.pick_date),
                )
            }
        },
    )

    if (open) {
        DatePickerDialog(
            onDismissRequest = { open = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val millis = datePickerState.selectedDateMillis
                        if (millis != null) {
                            onDateSelectedIso(millisToIsoDate(millis))
                        }
                        open = false
                    },
                ) {
                    Text(text = stringResource(id = R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { open = false }) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

private fun millisToIsoDate(millis: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    return sdf.format(Date(millis))
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
