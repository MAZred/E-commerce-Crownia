package com.ujikom.Ishi_Shop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.ujikom.Ishi_Shop.ui.theme.CrowniaTheme
import com.ujikom.Ishi_Shop.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val authViewModel : AuthViewModel by viewModels()
        setContent {
            CrowniaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        Navigation(modifier = Modifier.padding(innerPadding),)
                }
            }
        }
    }
}
