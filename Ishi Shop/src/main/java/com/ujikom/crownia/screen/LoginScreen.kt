package com.ujikom.crownia.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ujikom.crownia.AppUtil
import com.ujikom.crownia.viewmodel.AuthViewModel
import com.ujikom.crownia.R

@Composable
fun LoginScreen(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    /// input email dan password
    var email by remember {
        mutableStateOf(value = "")
    }

    var password by remember {
        mutableStateOf(value = "")
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    var context = LocalContext.current


    /// Column layout
    Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment =  Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = R.drawable.login),
            contentDescription = "banner"
        )

        Text(text = "Welcome Back!", fontSize = 32.sp)
        Spacer(modifier = Modifier.height(16.dp))

        /// Input email
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
            },
            label = {
                Text(text = "Email")
            },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(8.dp))

        /// Input password
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
            },
            label = {
                Text(text = "password")
            },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        /// Proses authenticate login
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick =  {
            isLoading = true
            authViewModel.login(email,password){success,errorMessage->
                if (success){
                    isLoading = false
                    navController.navigate("home"){
                        popUpTo("auth") {inclusive = true}
                    }
                }else{
                    isLoading = false
                    AppUtil.showToast(context,errorMessage?:"something went wrong")
                }
            }

        },
            enabled = !isLoading,
            modifier= Modifier.fillMaxWidth()
                .height(60.dp)
        ) {
            Text(text = if (isLoading)"Logging in" else "Login", fontSize = 16.sp)
        }
    }
}