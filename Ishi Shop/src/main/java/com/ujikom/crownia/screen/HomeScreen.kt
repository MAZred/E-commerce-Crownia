package com.ujikom.crownia.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ujikom.crownia.components.SearchTopBar
import com.ujikom.crownia.components.TitleTopBar
import com.ujikom.crownia.pages.CartPage
import com.ujikom.crownia.pages.FavoritePage
import com.ujikom.crownia.pages.HomePage
import com.ujikom.crownia.pages.ProfilePage
import com.ujikom.crownia.viewmodel.AuthViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    val navItemList = listOf(
        NavItem(label = "Home", Icons.Default.Home),
        NavItem(label = "Favorite", Icons.Default.Favorite),
        NavItem(label = "Cart", Icons.Default.ShoppingCart),
        NavItem(label = "Profile", Icons.Default.Person),
    )

    var selectedIndex by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            when (selectedIndex) {
                0 -> SearchTopBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearchClicked = { /* opsional */ }
                )
                1 -> TitleTopBar(title = "Favorite")
                2 -> TitleTopBar(title = "Keranjang")
                3 -> TitleTopBar(title = "Profil")
            }
        },
    bottomBar = {
            NavigationBar {
                navItemList.forEachIndexed { index, navItem ->
                    NavigationBarItem(
                        selected = index == selectedIndex,
                        onClick = { selectedIndex = index },
                        icon = {
                            Icon(
                                imageVector = navItem.icon,
                                contentDescription = navItem.label
                            )
                        },
                        label = { Text(text = navItem.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            unselectedIconColor = Color.Gray,
                            selectedTextColor = Color.Black,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        ContentScreen(
            modifier = modifier.padding(innerPadding),
            navController = navController,
            selectedIndex = selectedIndex
        )
    }

}

@Composable
fun ContentScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    selectedIndex: Int
) {
    when (selectedIndex) {
        0 -> HomePage(modifier = modifier, navController = navController)
        1 -> FavoritePage(modifier = modifier, navController = navController)
        2 -> CartPage(modifier = modifier, navController = navController)
        3 -> ProfilePage(modifier = modifier, navController = navController)
    }
}

data class NavItem(
    val label: String,
    val icon: ImageVector
)
