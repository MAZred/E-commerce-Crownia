package com.ujikom.Ishi_Shop

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.ujikom.Ishi_Shop.pages.CategoryProductsPage
import com.ujikom.Ishi_Shop.screen.AuthScreen
import com.ujikom.Ishi_Shop.screen.HomeScreen
import com.ujikom.Ishi_Shop.screen.LoginScreen
import com.ujikom.Ishi_Shop.screen.SignupScreen


/// Function untuk navigasi setiap halaman
@Composable
fun Navigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    GlobalNavigation.navController = navController

    val isLoggedIn = Firebase.auth.currentUser!=null
    val firstPage = "home"

    NavHost(navController = navController, startDestination = firstPage) {


        composable(route = "auth") {/// rute login
            AuthScreen(modifier,navController)
        }
        composable(route = "login"){/// rute login
                LoginScreen(modifier,navController)
        }
        composable(route = "signup"){/// rute signup
            SignupScreen(modifier,navController)
        }
        composable(route = "home"){/// rute home/beranda
            HomeScreen(modifier,navController)
        }
        composable(route = "category-products/{categoryId}"){
            var categoryId = it.arguments?.getString("categoryId")
            CategoryProductsPage(modifier,categoryId?:"",navController)
        }
    }
}

object GlobalNavigation{
    lateinit var navController: NavHostController
}