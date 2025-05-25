package com.amonga.snifflicks.module.network.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class GenreResponse(
    @Expose @SerializedName("genres") val genres: List<Genre>
)

data class Genre(
    @Expose @SerializedName("id") val id: Int,
    @Expose @SerializedName("name") val name: String
)