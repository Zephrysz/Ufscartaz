package com.ufscar.ufscartaz.data.remote

import com.google.gson.annotations.SerializedName

// Represents the overall response from the Pexels search API
data class PexelsSearchResponse(
    val photos: List<Photo>,
    @SerializedName("total_results") val totalResults: Int,
    @SerializedName("per_page") val perPage: Int,
    val page: Int,
    @SerializedName("next_page") val nextPage: String?
)

// Represents a single photo returned by the API
data class Photo(
    val id: Int,
    val width: Int,
    val height: Int,
    val url: String,
    val photographer: String,
    @SerializedName("photographer_url") val photographerUrl: String,
    @SerializedName("photographer_id") val photographerId: Long,
    val avgColor: String,
    val src: PhotoSource, // Contains various image sizes
    val liked: Boolean,
    val alt: String?
)

// Represents the different source URLs for a photo
data class PhotoSource(
    val original: String,
    val large2x: String,
    val large: String,
    val medium: String, // Good size for avatars
    val small: String,
    val portrait: String,
    val landscape: String,
    val tiny: String
)

// Simple data class for the UI
data class Avatar(
    val pexelsId: Int,
    val url: String // We'll likely use the 'medium' or 'large' url
)