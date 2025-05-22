package com.ufscar.ufscartaz.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import coil.compose.AsyncImage
import com.ufscar.ufscartaz.R
import com.ufscar.ufscartaz.navigation.AppDestinations
import com.ufscar.ufscartaz.ui.viewmodels.AvatarViewModel

@Composable
fun AvatarSelectionScreen(
    navController: NavHostController,
    viewModel: AvatarViewModel = viewModel()
) {
    val avatarList by viewModel.avatarList.collectAsState()
    val isLoadingAvatars by viewModel.isLoadingAvatars.collectAsState()
    val fetchAvatarsError by viewModel.fetchAvatarsError.collectAsState()
    val selectedAvatarPexelsId by viewModel.selectedAvatarPexelsId.collectAsState()
    val saveAvatarState by viewModel.saveAvatarState.collectAsState()

    // Handle save avatar state changes
    LaunchedEffect(saveAvatarState) {
        when (saveAvatarState) {
            is AvatarViewModel.SaveAvatarState.Success -> {
                navController.navigate(AppDestinations.MOVIES) {
                    popUpTo(AppDestinations.AVATAR_SELECTION) { inclusive = true }
                }
                viewModel.resetSaveAvatarState()
            }
            is AvatarViewModel.SaveAvatarState.Error -> {
                // Error message is already in the state
            }
            else -> {
                // Do nothing for Idle or Loading
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize() // Use fillMaxSize for Column to manage space
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Row: Title and Skip button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 32.dp), // Adjusted padding
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Spacer to push Title slightly right or allow Pular space
                Spacer(modifier = Modifier.width(48.dp)) // Give some space on the left

                // Title (Adjust weight/modifier if you want it truly centered relative to screen)
                Text(
                    text = stringResource(R.string.title_choose_avatar),
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f) // Title takes available space
                )

                // Skip Button (Text Button in top right)
                TextButton(
                    onClick = { navController.navigate(AppDestinations.MOVIES) },
                    enabled = saveAvatarState != AvatarViewModel.SaveAvatarState.Loading // Disable while saving
                ) {
                    Text(
                        text = stringResource(R.string.button_skip), // Assuming R.string.button_skip is "Pular"
                        color = Color.White, // Or a subtle gray
                        fontSize = 16.sp
                    )
                }
            }

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

            // --- Avatar Options Area ---
            // Use a Column to stack the LazyRows and other potential elements
            Column(
                modifier = Modifier.weight(1f) // This Column takes up remaining space
            ) {
                if (isLoadingAvatars) {
                    // Loading indicator while fetching avatars
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            color = Color.White
                        )
                    }
                } else if (fetchAvatarsError != null && avatarList.isEmpty()) {
                    // Display fetch error if avatars couldn't be loaded
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = fetchAvatarsError ?: "Failed to load avatars.",
                            color = Color.Red,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                else if (avatarList.isNotEmpty()) {
                    // Display the list of avatars in multiple rows
                    // Chunk the list, e.g., 4 items per row
                    val avatarsPerRow = 4
                    val chunkedAvatarList = avatarList.chunked(avatarsPerRow)

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp) // Space between rows
                    ) {
                        chunkedAvatarList.forEachIndexed { index, rowAvatars ->
                            // Add the "Label" text above each row as seen in the image
                            Text(
                                text = "Label", // Hardcoded "Label" to match image
                                color = Color.LightGray, // Adjust color
                                fontSize = 14.sp,
                                modifier = Modifier.padding(start = 8.dp) // Align Label
                            )
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp), // Space between avatars
                                verticalAlignment = Alignment.CenterVertically,
                                contentPadding = PaddingValues(horizontal = 8.dp) // Add horizontal padding for edges
                            ) {
                                items(rowAvatars, key = { it.pexelsId }) { avatar ->
                                    val isSelected = avatar.pexelsId == selectedAvatarPexelsId

                                    AsyncImage(
                                        model = avatar.url,
                                        contentDescription = "Avatar option ${avatar.pexelsId}",
                                        modifier = Modifier
                                            .size(72.dp) // Size of avatar images
                                            .clip(CircleShape)
                                            .border(
                                                width = if (isSelected) 3.dp else 1.dp,
                                                color = if (isSelected) Color.Red else Color.Gray,
                                                shape = CircleShape
                                            )
                                            .clickable {
                                                viewModel.selectAvatar(avatar)
                                            },
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }
                    }
                } else if (fetchAvatarsError == null) {
                    // Case where list is empty and no explicit error occurred (maybe query returned no results)
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No avatars found. Try again later.", // Add to strings.xml
                            color = Color.Gray,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
            // --- End Avatar Options Area ---


            // Optional: Deselect button (placed below avatar grid, before Continue)
            if (selectedAvatarPexelsId != null && saveAvatarState != AvatarViewModel.SaveAvatarState.Loading) {
                TextButton(
                    onClick = { viewModel.deselectAvatar() },
                    modifier = Modifier.align(Alignment.CenterHorizontally) // Center the button
                        .padding(top = 16.dp) // Add space above
                ) {
                    Text(
                        text = "Deselect Avatar", // Add to strings.xml
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }

            // Continue button (at the bottom)
            val isSaving = saveAvatarState == AvatarViewModel.SaveAvatarState.Loading
            Button(
                onClick = { viewModel.saveAvatar() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp), // Padding above/below button
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    disabledContainerColor = Color.Gray
                ),
                enabled = !isSaving && selectedAvatarPexelsId != null // Enabled only if an avatar is selected and not saving
            ) {
                if (isSaving) {
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
            Spacer(modifier = Modifier.height(16.dp)) // Padding at the very bottom
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AvatarSelectionScreenPreview() {
    // For a more representative preview, you might want to create a mock ViewModel
    // that provides dummy data for avatarList.
    AvatarSelectionScreen(navController = rememberNavController())
}