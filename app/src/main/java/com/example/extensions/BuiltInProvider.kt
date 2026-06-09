package com.example.extensions

class BuiltInProvider : MediaProvider {
    override val name: String = "Built-in Mock Provider"
    
    override suspend fun search(query: String): List<SearchResult> {
        return listOf(
            SearchResult("Mock Movie $query", "mock://movie/$query", "https://image.tmdb.org/t/p/w500/8cdWjvZQUExUUTzyp4t6EDMubfO.jpg")
        )
    }

    override suspend fun getMainPage(): List<MainPageItem> {
        val posters = listOf(
            "https://image.tmdb.org/t/p/w500/8cdWjvZQUExUUTzyp4t6EDMubfO.jpg",
            "https://image.tmdb.org/t/p/w500/vpnVM9B6NMmQpWeZvzRx8D6284f.jpg",
            "https://image.tmdb.org/t/p/w500/1pdfLvkbY9ohJlCjQH2TbRxOqRw.jpg",
            "https://image.tmdb.org/t/p/w500/1X4h40fcB4WWUmIBK0auT4zRBAV.jpg",
            "https://image.tmdb.org/t/p/w500/mY7SeH4HFFxW1hiI6cWuwCRKptN.jpg",
            "https://image.tmdb.org/t/p/w500/wsUbnE1xG9m1M0UoO5lKzN28Y8b.jpg",
            "https://image.tmdb.org/t/p/w500/sh7Rg8Er3tFcN9BpKIPOMvALgZd.jpg",
            "https://image.tmdb.org/t/p/w500/qJ2tW6WMUDux911r6m7haRef0WH.jpg"
        )
        
        return listOf(
            MainPageItem(
                title = "Trending Movies",
                items = (1..20).map { i -> 
                    SearchResult("Trending Movie $i", "mock://movie/$i", posters[i % posters.size])
                }
            ),
            MainPageItem(
                title = "Popular TV Shows",
                items = (1..20).map { i ->
                    SearchResult("Popular Show $i", "mock://show/$i", posters[(i + 1) % posters.size])
                }
            ),
            MainPageItem(
                title = "Action",
                items = (1..20).map { i ->
                    SearchResult("Action Movie $i", "mock://action/$i", posters[(i + 2) % posters.size])
                }
            ),
            MainPageItem(
                title = "Comedy",
                items = (1..20).map { i ->
                    SearchResult("Comedy Movie $i", "mock://comedy/$i", posters[(i + 3) % posters.size])
                }
            ),
             MainPageItem(
                title = "Drama",
                items = (1..20).map { i ->
                    SearchResult("Drama Movie $i", "mock://drama/$i", posters[(i + 4) % posters.size])
                }
            )
        )
    }

    override suspend fun load(url: String): LoadResponse {
        return LoadResponse(
            title = "", // Let DetailScreen use the original title
            posterUrl = null, // Let DetailScreen use the original poster
            backgroundUrl = "https://image.tmdb.org/t/p/original/9l1eZiJHmhr5jIlthMdJN5X0NNn.jpg",
            plot = "This is a detailed description of the selected movie or TV show. It contains in-depth plot details, an engaging summary, and deep lore that gives the user an immersive context about the cinematic universe they are about to experience.",
            tags = listOf("Action", "Adventure", "Sci-Fi", "Fantasy", "Drama"),
            rating = "8.5/10",
            year = "2024",
            duration = "142 min",
            status = "Completed",
            cast = listOf("Ryan Reynolds", "Hugh Jackman", "Emma Corrin", "Matthew Macfadyen"),
            recommendations = listOf(
                SearchResult("Similar Movie 1", "mock://rec/1", "https://image.tmdb.org/t/p/w500/vpnVM9B6NMmQpWeZvzRx8D6284f.jpg"),
                SearchResult("Similar Movie 2", "mock://rec/2", "https://image.tmdb.org/t/p/w500/1pdfLvkbY9ohJlCjQH2TbRxOqRw.jpg"),
                SearchResult("Similar Movie 3", "mock://rec/3", "https://image.tmdb.org/t/p/w500/1X4h40fcB4WWUmIBK0auT4zRBAV.jpg")
            ),
            episodes = listOf(
                Episode("Episode 1", "$url/eps/1", 1, 1, "The first episode where everything begins.", null),
                Episode("Episode 2", "$url/eps/2", 2, 1, "The story continues as our heroes face new challenges.", null),
                Episode("Episode 3", "$url/eps/3", 3, 1, "A shocking revelation changes everything.", null),
                Episode("Episode 4", "$url/eps/4", 4, 1, "The season finale.", null),
            )
        )
    }

    override suspend fun extractVideoLinks(url: String): List<VideoLink> {
        // Return a mock sample video
        return listOf(
            VideoLink("1080p", "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4")
        )
    }
}
