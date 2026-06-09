package com.example.extensions

interface MediaProvider {
    val name: String
    suspend fun search(query: String): List<SearchResult>
    suspend fun getMainPage(): List<MainPageItem>
    suspend fun extractVideoLinks(url: String): List<VideoLink>
}

data class SearchResult(val name: String, val url: String, val posterUrl: String?)
data class MainPageItem(val title: String, val items: List<SearchResult>)
data class VideoLink(val quality: String, val url: String)
