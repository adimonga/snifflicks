package com.amonga.snifflicks.module.network.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class MovieVideosResponse(
    @Expose @SerializedName("id") val id: Int,
    @Expose @SerializedName("results") val results: List<MovieVideo>
)

data class MovieVideo(
    @Expose @SerializedName("id") val id: String,
    @Expose @SerializedName("key") val key: String,
    @Expose @SerializedName("name") val name: String,
    @Expose @SerializedName("site") val site: String,
    @Expose @SerializedName("size") val size: Int,
    @Expose @SerializedName("type") val type: String,
    @Expose @SerializedName("official") val official: Boolean,
    @Expose @SerializedName("published_at") val publishedAt: String
) {
    fun getYoutubeUrl() = "https://www.youtube.com/watch?v=$key"
    fun getYoutubeThumbnailUrl() = "https://img.youtube.com/vi/$key/hqdefault.jpg"
} 