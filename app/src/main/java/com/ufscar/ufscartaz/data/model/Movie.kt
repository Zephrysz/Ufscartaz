package com.ufscar.ufscartaz.data.model

data class MovieResponse(
    val results: List<Movie>
)

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val poster_path: String?,
    val genre_ids: List<Int> = listOf(),
    val backdrop_path: String? = null,
    val vote_average: Double = 0.0
)

// Mapeamento dos IDs de gêneros para nomes
object GenreMap {
    val genreMap = mapOf(
        28 to "Ação",
        12 to "Aventura",
        16 to "Animação",
        35 to "Comédia",
        80 to "Crime",
        99 to "Documentário",
        18 to "Drama",
        10751 to "Família",
        14 to "Fantasia",
        36 to "História",
        27 to "Terror",
        10402 to "Música",
        9648 to "Mistério",
        10749 to "Romance",
        878 to "Ficção Científica",
        10770 to "Filme de TV",
        53 to "Thriller",
        10752 to "Guerra",
        37 to "Faroeste"
    )
}

// Extensão para verificar se um filme pertence a um gênero
fun Movie.isGenre(genreId: Int): Boolean {
    return genre_ids.contains(genreId)
}

// Extensão para obter os nomes dos gêneros de um filme
fun Movie.getGenreNames(): List<String> {
    return genre_ids.mapNotNull { GenreMap.genreMap[it] }
}
