package com.ufscar.ufscartaz.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.ufscar.ufscartaz.R
import com.ufscar.ufscartaz.navigation.AppDestinations
import com.ufscar.ufscartaz.ui.viewmodels.AvatarViewModel

@Composable
fun AvatarSelectionScreen(
    navController: NavHostController,
    viewModel: AvatarViewModel = viewModel()
) {
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // Observe avatar state
    val avatarState by viewModel.avatarState.collectAsState()
    val selectedAvatarId by viewModel.selectedAvatarId.collectAsState()
    
    // Handle avatar state changes
    LaunchedEffect(avatarState) {
        when (avatarState) {
            is AvatarViewModel.AvatarState.Loading -> {
                isLoading = true
                errorMessage = null
            }
            is AvatarViewModel.AvatarState.Success -> {
                isLoading = false
                errorMessage = null
                // Navigate to home screen
                navController.navigate(AppDestinations.HOME) {
                    popUpTo(AppDestinations.AVATAR_SELECTION) { inclusive = true }
                }
                // Reset state
                viewModel.resetAvatarState()
            }
            is AvatarViewModel.AvatarState.Error -> {
                isLoading = false
                errorMessage = (avatarState as AvatarViewModel.AvatarState.Error).message
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
            // Title
            Text(
                text = stringResource(R.string.title_choose_avatar),
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
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
            
            // Avatar options (simplified for now)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Avatar 1
                Button(
                    onClick = { viewModel.selectAvatar(1) },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedAvatarId == 1) Color.Red else Color.DarkGray
                    ),
                    modifier = Modifier.size(60.dp)
                ) {
                    Text("1")
                }
                
                // Avatar 2
                Button(
                    onClick = { viewModel.selectAvatar(2) },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedAvatarId == 2) Color.Red else Color.DarkGray
                    ),
                    modifier = Modifier.size(60.dp)
                ) {
                    Text("2")
                }
                
                // Avatar 3
                Button(
                    onClick = { viewModel.selectAvatar(3) },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedAvatarId == 3) Color.Red else Color.DarkGray
                    ),
                    modifier = Modifier.size(60.dp)
                ) {
                    Text("3")
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Skip button (outlined)
            OutlinedButton(
                onClick = { navController.navigate(AppDestinations.HOME) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White
                ),
                enabled = !isLoading
            ) {
                Text(
                    text = stringResource(R.string.button_skip),
                    fontSize = 16.sp
                )
            }
            
            // Continue button
            Button(
                onClick = { viewModel.saveAvatar() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    disabledContainerColor = Color.Gray
                ),
                enabled = !isLoading && selectedAvatarId > 0
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = stringResource(R.string.button_continue),
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
fun AvatarSelectionScreenPreview() {
    AvatarSelectionScreen(navController = rememberNavController())
}