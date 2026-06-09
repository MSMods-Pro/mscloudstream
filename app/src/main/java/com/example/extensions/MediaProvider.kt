package com.example.extensions

interface MediaProvider {
    val name: String
    suspend fun search(query: String): List<SearchResult>
    suspend fun getMainPage(): List<MainPageItem>
    suspend fun load(url: String): LoadResponse? 
    suspend fun extractVideoLinks(url: String): List<VideoLink>
}

data class LoadResponse(
    val title: String,
    val posterUrl: String?,
    val backgroundUrl: String?,
    val plot: String?,
    val tags: List<String>,
    val rating: String?,
    val year: String? = null,
    val duration: String? = null,
    val status: String? = null,
    val cast: List<String> = emptyList(),
    val recommendations: List<SearchResult> = emptyList(),
    val episodes: List<Episode>
)

data class Episode(
    val name: String,
    val url: String,
    val episodeNumber: Int,
    val seasonNumber: Int? = null,
    val description: String? = null,
    val posterUrl: String? = null
)

data class SearchResult(val name: String, val url: String, val posterUrl: String?)
data class MainPageItem(val title: String, val items: List<SearchResult>)
data class VideoLink(val quality: String, val url: String)
