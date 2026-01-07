package com.example.gigappweather.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import com.example.gigappweather.data.local.AppDatabase
import com.example.gigappweather.data.repository.GigRepositoryImpl
import com.example.gigappweather.data.repository.WeatherRepositoryImpl
import com.example.gigappweather.ui.screens.GigDetailScreen
import com.example.gigappweather.ui.screens.GigListScreen
import com.example.gigappweather.ui.screens.InfoScreen
import com.example.gigappweather.ui.viewmodel.AddGigViewModel
import com.example.gigappweather.ui.viewmodel.AppViewModel
import com.example.gigappweather.ui.viewmodel.GigDetailViewModel
import com.example.gigappweather.ui.viewmodel.GigListViewModel

@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val context = LocalContext.current.applicationContext

    val appViewModel: AppViewModel = viewModel(
        factory = AppViewModelFactory(),
    )

    var listVmNonce by remember { mutableIntStateOf(0) }

    val database = remember {
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DB_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    val gigRepository = remember { GigRepositoryImpl(database.gigDao()) }
    val weatherRepository = remember { WeatherRepositoryImpl() }

    NavHost(
        navController = navController,
        startDestination = NavRoutes.LIST,
        modifier = modifier,
    ) {
        composable(NavRoutes.LIST) {
            val listViewModel: GigListViewModel = viewModel(
                key = "GigListViewModel-$listVmNonce",
                factory = GigListViewModelFactory(gigRepository, weatherRepository),
            )
            val addViewModel: AddGigViewModel = viewModel(
                factory = AddGigViewModelFactory(gigRepository),
            )

            GigListScreen(
                viewModel = listViewModel,
                addGigViewModel = addViewModel,
                appViewModel = appViewModel,
                onOpenDetail = { gigId -> navController.navigate(NavRoutes.detail(gigId)) },
                onOpenInfo = { navController.navigate(NavRoutes.INFO) },
                onRetry = { listVmNonce += 1 },
            )
        }

        composable(
            route = NavRoutes.DETAIL,
            arguments = listOf(
                navArgument(NavRoutes.Args.GIG_ID) { type = NavType.LongType },
            ),
        ) { backStackEntry ->
            val gigId = backStackEntry.arguments?.getLong(NavRoutes.Args.GIG_ID) ?: 0L

            var detailVmNonce by remember(gigId) { mutableIntStateOf(0) }

            val detailViewModel: GigDetailViewModel = viewModel(
                key = "GigDetailViewModel-$gigId-$detailVmNonce",
                factory = GigDetailViewModelFactory(
                    gigId = gigId,
                    gigRepository = gigRepository,
                    weatherRepository = weatherRepository,
                ),
            )

            GigDetailScreen(
                viewModel = detailViewModel,
                appViewModel = appViewModel,
                onBack = { navController.popBackStack() },
                onRetry = { detailVmNonce += 1 },
            )
        }

        composable(NavRoutes.INFO) {
            InfoScreen(
                appViewModel = appViewModel,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
