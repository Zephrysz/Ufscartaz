package com.ufscar.ufscartaz.data.remote

import com.google.gson.annotations.SerializedName

data class PexelsSearchResponse(
    val photos: List<Photo>,
    @SerializedName("total_results") val totalResults: Int,
    @SerializedName("per_page") val perPage: Int,
    val page: Int,
    @SerializedName("next_page") val nextPage: String?
)

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

data class Avatar(
    val pexelsId: Int,
    val url: String
)

data class AvatarCategoryConfig(
    val label: String,
    val query: String
)

data class AvatarCategory(
    val label: String,
    val avatars: List<Avatar>
)