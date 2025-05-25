package com.amonga.snifflicks.module.network.service

import com.amonga.snifflicks.module.network.model.GenreResponse
import com.amonga.snifflicks.module.network.model.MovieDetails
import com.amonga.snifflicks.module.network.model.MovieResponse
import com.amonga.snifflicks.module.network.model.MovieVideosResponse
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap
import javax.inject.Singleton

interface MovieService {
    @GET("3/discover/movie?include_adult=false&include_video=false&sort_by=popularity.desc")
    suspend fun discoverMovies(
        @QueryMap params: Map<String, String>
    ): MovieResponse

    @GET("3/movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int
    ): MovieDetails

    @GET("3/movie/{movie_id}/videos")
    suspend fun getMovieVideos(
        @Path("movie_id") movieId: Int
    ): MovieVideosResponse

    @GET("3/genre/movie/list")
    suspend fun getMovieGenres(): GenreResponse
}

@Module
@InstallIn(SingletonComponent::class)
class NetworkMovieService {

    @Provides
    @Singleton
    fun provideMovieService(retrofit: Retrofit): MovieService {
        return retrofit.create<MovieService>(MovieService::class.java)
    }
}