package com.ufscar.ufscartaz.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn // Import LazyColumn
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
import com.ufscar.ufscartaz.data.remote.AvatarCategory // Import AvatarCategory
import com.ufscar.ufscartaz.navigation.AppDestinations
import com.ufscar.ufscartaz.ui.viewmodels.AvatarViewModel

@Composable
fun AvatarSelectionScreen(
    navController: NavHostController,
    viewModel: AvatarViewModel = viewModel()
) {
    // Observe states from ViewModel
    // Changed to observe the categorized list
    val categorizedAvatarList by viewModel.categorizedAvatarList.collectAsState()
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
                // Do nothing
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
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Row: Title and Skip button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(48.dp))

                Text(
                    text = stringResource(R.string.title_choose_avatar),
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )

                // Skip Button (Text Button in top right)
                TextButton(
                    onClick = { navController.navigate(AppDestinations.MOVIES) },
                    enabled = saveAvatarState != AvatarViewModel.SaveAvatarState.Loading
                ) {
                    Text(
                        text = stringResource(R.string.button_skip), // Assuming "Pular"
                        color = Color.White,
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

            // --- Avatar Categories Area ---
            // Use LazyColumn to stack the category rows vertically and efficiently
            if (isLoadingAvatars) {
                // Loading indicator while fetching avatars
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(), // Fill remaining space
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = Color.White
                    )
                }
            } else if (fetchAvatarsError != null && categorizedAvatarList.isEmpty()) {
                // Display fetch error if avatars couldn't be loaded for any category
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(), // Fill remaining space
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
            else if (categorizedAvatarList.isNotEmpty()) {
                // Display the list of avatar categories using LazyColumn
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(), // LazyColumn takes remaining space
                    verticalArrangement = Arrangement.spacedBy(16.dp) // Space between categories
                ) {
                    items(categorizedAvatarList, key = { it.label }) { category ->
                        // Display the category label
                        Text(
                            text = category.label, // Use the label from the category
                            color = Color.LightGray,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold, // Maybe bold the label
                            modifier = Modifier.padding(start = 8.dp) // Align Label
                        )
                        // Display the avatars for this category in a LazyRow
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp), // Space between avatars
                            verticalAlignment = Alignment.CenterVertically,
                            contentPadding = PaddingValues(horizontal = 8.dp) // Add horizontal padding
                        ) {
                            items(category.avatars, key = { it.pexelsId }) { avatar ->
                                val isSelected = avatar.pexelsId == selectedAvatarPexelsId

                                AsyncImage(
                                    model = avatar.url,
                                    contentDescription = "Avatar option ${avatar.pexelsId}",
                                    modifier = Modifier
                                        .size(72.dp)
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
                // Case where list is empty and no explicit error occurred (maybe queries returned no results)
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(), // Fill remaining space
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No avatars found across all categories.", // More specific empty message
                        color = Color.Gray,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            // --- End Avatar Categories Area ---


            // Optional: Deselect button
            if (selectedAvatarPexelsId != null && saveAvatarState != AvatarViewModel.SaveAvatarState.Loading) {
                TextButton(
                    onClick = { viewModel.deselectAvatar() },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                        .padding(top = 16.dp)
                ) {
                    Text(
                        text = "Deselect Avatar", // Add to strings.xml if not already
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
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
                    containerColor = Color.Red,
                    disabledContainerColor = Color.Gray
                ),
                enabled = !isSaving && selectedAvatarPexelsId != null
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
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}