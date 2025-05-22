package com.ufscar.ufscartaz.ui.screens // Ensure this matches your project structure

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.ufscar.ufscartaz.R
import com.ufscar.ufscartaz.navigation.AppDestinations
import com.ufscar.ufscartaz.ui.viewmodels.AuthViewModel

// This is your @Composable function representing the Login screen
@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // Observe login state
    val loginState by viewModel.loginState.collectAsState()
    
    // Handle login state changes
    LaunchedEffect(loginState) {
        when (loginState) {
            is AuthViewModel.LoginState.Loading -> {
                isLoading = true
                errorMessage = null
            }
            is AuthViewModel.LoginState.Success -> {
                isLoading = false
                errorMessage = null
                // Navigate to movies screen
                navController.navigate(AppDestinations.MOVIES) {
                    popUpTo(AppDestinations.LOGIN) { inclusive = true }
                }
                // Reset state
                viewModel.resetLoginState()
            }
            is AuthViewModel.LoginState.Error -> {
                isLoading = false
                errorMessage = (loginState as AuthViewModel.LoginState.Error).message
            }
            else -> {
                isLoading = false
            }
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            // Logo text with UFSCAR and TAZ parts
            val logoText = buildAnnotatedString {
                withStyle(style = SpanStyle(
                    color = Color.Red,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )) {
                    append(stringResource(R.string.logo_part1))
                }
                withStyle(style = SpanStyle(
                    color = Color.Red,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )) {
                    append(stringResource(R.string.logo_part2))
                }
            }
            
            Text(text = logoText)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Welcome back text
            Text(
                text = stringResource(R.string.auth_welcome_back),
                color = Color.White,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            // Error message
            if (errorMessage != null) {
                Text(
                    text = errorMessage ?: "",
                    color = Color.Red,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            
            // Email field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(stringResource(R.string.label_email), color = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedTextColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedBorderColor = Color.Gray,
                    focusedBorderColor = Color.White
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                enabled = !isLoading
            )
            
            // Password field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(R.string.label_password), color = Color.Gray) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedTextColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedBorderColor = Color.Gray,
                    focusedBorderColor = Color.White
                ),
                enabled = !isLoading
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Login button
            Button(
                onClick = { viewModel.login(email, password) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    disabledContainerColor = Color.Gray
                ),
                enabled = !isLoading && email.isNotBlank() && password.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = stringResource(R.string.button_login),
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(navController = rememberNavController())
}