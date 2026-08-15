package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.KashifBottomBar
import com.example.ui.components.KashifTopBar
import com.example.ui.components.ShopScreen
import com.example.ui.screens.*
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.KashifMobileTheme
import com.example.ui.viewmodel.ShopViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KashifMobileTheme {
                MainShopApp()
            }
        }
    }
}

@Composable
fun MainShopApp(viewModel: ShopViewModel = viewModel()) {
    var currentScreen by remember { mutableStateOf(ShopScreen.DASHBOARD) }

    Scaffold(
        topBar = {
            KashifTopBar(
                title = "KASHIF MOBILE AND REPAIR",
                subtitle = "OFFLINE MOBILE SHOP MANAGEMENT",
                onSearchClick = { currentScreen = ShopScreen.MOBILES }
            )
        },
        bottomBar = {
            KashifBottomBar(
                currentScreen = currentScreen,
                onScreenSelected = { currentScreen = it }
            )
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground)
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                ShopScreen.DASHBOARD -> DashboardScreen(
                    viewModel = viewModel,
                    onNavigate = { currentScreen = it }
                )
                ShopScreen.MOBILES -> MobilesScreen(viewModel = viewModel)
                ShopScreen.ACCESSORIES -> AccessoriesScreen(viewModel = viewModel)
                ShopScreen.REPAIRS -> RepairsScreen(viewModel = viewModel)
                ShopScreen.SALES -> SalesScreen(viewModel = viewModel)
                ShopScreen.CUSTOMERS -> CustomersScreen(viewModel = viewModel)
                ShopScreen.EXPENSES -> ExpensesScreen(viewModel = viewModel)
                ShopScreen.REPORTS -> ReportsScreen(viewModel = viewModel)
                ShopScreen.BACKUP -> DailyRepairsScreen(
                    viewModel = viewModel,
                    onBack = { currentScreen = ShopScreen.DASHBOARD }
                )
            }
        }
    }
}
