package com.ufscar.ufscartaz.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape // Use CircleShape for avatars? Or RoundedCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage // Import Coil Composable
import com.ufscar.ufscartaz.R
import com.ufscar.ufscartaz.navigation.AppDestinations
import com.ufscar.ufscartaz.ui.viewmodels.AvatarViewModel

@Composable
fun AvatarSelectionScreen(
    navController: NavHostController,
    viewModel: AvatarViewModel = viewModel() // Use the updated ViewModel
) {
    // Observe states from ViewModel
    val avatarList by viewModel.avatarList.collectAsState()
    val isLoadingAvatars by viewModel.isLoadingAvatars.collectAsState()
    val fetchAvatarsError by viewModel.fetchAvatarsError.collectAsState()
    val selectedAvatarPexelsId by viewModel.selectedAvatarPexelsId.collectAsState()
    val saveAvatarState by viewModel.saveAvatarState.collectAsState() // Observe save state

    // Handle save avatar state changes
    LaunchedEffect(saveAvatarState) {
        when (saveAvatarState) {
            is AvatarViewModel.SaveAvatarState.Success -> {
                // Navigate to movies screen on successful save
                navController.navigate(AppDestinations.MOVIES) {
                    popUpTo(AppDestinations.AVATAR_SELECTION) { inclusive = true }
                }
                // Reset state after navigation
                viewModel.resetSaveAvatarState()
            }
            is AvatarViewModel.SaveAvatarState.Error -> {
                // Error message is already in the state, will be displayed
            }
            else -> {
                // Do nothing for Idle or Loading
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black), // Use MaterialTheme colors normally
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
                color = Color.White, // Use MaterialTheme colors
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Error message (for fetching or saving)
            val errorMessage = fetchAvatarsError ?: (saveAvatarState as? AvatarViewModel.SaveAvatarState.Error)?.message
            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // --- Avatar Options ---
            if (isLoadingAvatars) {
                // Loading indicator while fetching avatars
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(48.dp)
                        .padding(vertical = 16.dp),
                    color = Color.White // Use MaterialTheme colors
                )
            } else if (avatarList.isNotEmpty()) {
                // Display the list of avatars
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp), // Space between avatars
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(avatarList, key = { it.pexelsId }) { avatar ->
                        val isSelected = avatar.pexelsId == selectedAvatarPexelsId

                        AsyncImage(
                            model = avatar.url, // Image URL from Pexels
                            contentDescription = "Avatar option ${avatar.pexelsId}",
                            modifier = Modifier
                                .size(72.dp) // Size of avatar images
                                .clip(CircleShape) // Make avatars round
                                .border( // Add border for selection
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) Color.Red else Color.Gray, // Selection color
                                    shape = CircleShape
                                )
                                .clickable {
                                    // Select this avatar when clicked
                                    viewModel.selectAvatar(avatar)
                                },
                            contentScale = ContentScale.Crop // Crop image to fit circle
                            // Optional: Add placeholder and error images
                            // placeholder = painterResource(R.drawable.placeholder),
                            // error = painterResource(R.drawable.error_image)
                        )
                    }
                }
            } else if (fetchAvatarsError == null) {
                // Case where list is empty and no explicit error occurred (maybe query returned no results)
                Text(
                    text = "No avatars found. Try again later.",
                    color = Color.Gray,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
            // --- End Avatar Options ---

            Spacer(modifier = Modifier.height(32.dp))

            // Skip button (outlined)
            OutlinedButton(
                onClick = { navController.navigate(AppDestinations.MOVIES) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White // Use MaterialTheme colors
                ),
                enabled = saveAvatarState != AvatarViewModel.SaveAvatarState.Loading // Disable while saving
            ) {
                Text(
                    text = stringResource(R.string.button_skip),
                    fontSize = 16.sp
                )
            }

            // Continue button
            val isSaving = saveAvatarState == AvatarViewModel.SaveAvatarState.Loading
            Button(
                onClick = { viewModel.saveAvatar() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red, // Use MaterialTheme colors
                    disabledContainerColor = Color.Gray // Use MaterialTheme colors
                ),
                // Enabled if not saving AND an avatar is selected
                enabled = !isSaving && selectedAvatarPexelsId != null
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White, // Use MaterialTheme colors
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = stringResource(R.string.button_continue),
                        fontSize = 16.sp,
                        color = Color.White // Use MaterialTheme colors
                    )
                }
            }

            // Optional: Deselect button
            if (selectedAvatarPexelsId != null && !isSaving) {
                TextButton(onClick = { viewModel.deselectAvatar() }) {
                    Text(
                        text = "Deselect Avatar", // Add to strings.xml
                        color = Color.Gray, // Use MaterialTheme colors
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AvatarSelectionScreenPreview() {
    // In preview, AvatarViewModel might throw errors or not show data
    // Consider creating a mock ViewModel for complex previews
    AvatarSelectionScreen(navController = rememberNavController())
}