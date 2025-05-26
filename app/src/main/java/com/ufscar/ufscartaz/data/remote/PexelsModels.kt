package com.ufscar.ufscartaz.data.remote

import com.google.gson.annotations.SerializedName

// Represents the overall response from the Pexels search API (remains the same)
data class PexelsSearchResponse(
    val photos: List<Photo>,
    @SerializedName("total_results") val totalResults: Int,
    @SerializedName("per_page") val perPage: Int,
    val page: Int,
    @SerializedName("next_page") val nextPage: String?
)

// Represents a single photo returned by the API (remains the same)
data class Photo(
    val id: Int,
    val width: Int,
    val height: Int,
    val url: String,
    val photographer: String,
    @SerializedName("photographer_url") val photographerUrl: String,
    @SerializedName("photographer_id") val photographerId: Long,
    val avgColor: String,
    val src: PhotoSource,
    val liked: Boolean,
    val alt: String?
)

// Represents the different source URLs for a photo (remains the same)
data class PhotoSource(
    val original: String,
    val large2x: String,
    val large: String,
    val medium: String,
    val small: String,
    val portrait: String,
    val landscape: String,
    val tiny: String
)

// Simple data class for a single avatar item in the UI (remains the same)
data class Avatar(
    val pexelsId: Int,
    val url: String
)

// Data class to represent a category *configuration* (label and query)
data class AvatarCategoryConfig(
    val label: String, // The label to display
    val query: String // The Pexels search query for this category
)

// Data class to represent a category *result* (label and fetched avatars) - Simplified
data class AvatarCategory(
    val label: String, // The label to display
    val avatars: List<Avatar> // The fetched avatars for this category
)