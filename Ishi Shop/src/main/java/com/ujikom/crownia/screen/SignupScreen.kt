package com.ujikom.crownia.screen

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
import androidx.navigation.NavController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ujikom.crownia.AppUtil
import com.ujikom.crownia.R
import com.ujikom.crownia.viewmodel.AuthViewModel

@Composable
fun SignupScreen(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel = viewModel()) {

        /// State input
        var email by remember {
            mutableStateOf(value = "")
        }

        var name by remember {
            mutableStateOf(value = "")
        }

        var password by remember {
            mutableStateOf(value = "")
        }

        var isLoading by remember {
            mutableStateOf(false)
        }

        var context = LocalContext.current



        /// Column Layout
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment =  Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.signup),
                contentDescription = "banner"
            )

                Text(
                    text = "Sign Up Now!",
                    modifier = Modifier.fillMaxWidth(),
                    style = TextStyle(
                        fontSize = 30.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                )
                /// sign up email
                Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Journey to Crownia is only one more step",
                modifier = Modifier.fillMaxWidth(),
                style = TextStyle(
                    fontSize = 22.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            )

                Spacer(modifier = Modifier.height(16.dp))
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

                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                    },
                    label = {
                        Text(text = "Full Name")
                    },
                    modifier = Modifier.fillMaxWidth(),
                )

                /// sign up password
                Spacer(modifier = Modifier.height(8.dp))
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

                /// make account
                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    isLoading = true
                    authViewModel.signup(email,name,password){success,errorMessage->
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
                    modifier= Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                ) {
                    Text(text = if(isLoading)"Creating Account" else "Signup", fontSize = 22.sp)
                }
        }
    }