package com.ufscar.ufscartaz.data.model

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.ufscar.ufscartaz.R

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

// Mapeamento dos IDs de gêneros para nomes (versão internacionalizada)
object GenreMap {
    // Versão antiga (deprecated) - mantida para compatibilidade
    @Deprecated("Use getLocalizedGenreMap(context) ou getLocalizedGenreMapComposable() instead")
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
    
    // Versão internacionalizada para uso com Context
    fun getLocalizedGenreMap(context: Context): Map<Int, String> {
        return mapOf(
            28 to context.getString(R.string.category_action),
            12 to context.getString(R.string.category_adventure),
            16 to context.getString(R.string.category_animation),
            35 to context.getString(R.string.category_comedy),
            80 to context.getString(R.string.category_crime),
            99 to context.getString(R.string.category_documentaries),
            18 to context.getString(R.string.category_drama),
            10751 to context.getString(R.string.category_family),
            14 to context.getString(R.string.category_fantasy),
            36 to context.getString(R.string.category_history),
            27 to context.getString(R.string.category_horror),
            10402 to context.getString(R.string.category_music),
            9648 to context.getString(R.string.category_mystery),
            10749 to context.getString(R.string.category_romance),
            878 to context.getString(R.string.category_scifi),
            10770 to context.getString(R.string.category_tv_movie),
            53 to context.getString(R.string.category_thriller),
            10752 to context.getString(R.string.category_war),
            37 to context.getString(R.string.category_western)
        )
    }
    
    // Versão internacionalizada para uso em Composables
    @Composable
    fun getLocalizedGenreMapComposable(): Map<Int, String> {
        return mapOf(
            28 to stringResource(R.string.category_action),
            12 to stringResource(R.string.category_adventure),
            16 to stringResource(R.string.category_animation),
            35 to stringResource(R.string.category_comedy),
            80 to stringResource(R.string.category_crime),
            99 to stringResource(R.string.category_documentaries),
            18 to stringResource(R.string.category_drama),
            10751 to stringResource(R.string.category_family),
            14 to stringResource(R.string.category_fantasy),
            36 to stringResource(R.string.category_history),
            27 to stringResource(R.string.category_horror),
            10402 to stringResource(R.string.category_music),
            9648 to stringResource(R.string.category_mystery),
            10749 to stringResource(R.string.category_romance),
            878 to stringResource(R.string.category_scifi),
            10770 to stringResource(R.string.category_tv_movie),
            53 to stringResource(R.string.category_thriller),
            10752 to stringResource(R.string.category_war),
            37 to stringResource(R.string.category_western)
        )
    }
    
    // Função para obter nome de gênero específico em Composable
    @Composable
    fun getGenreName(genreId: Int): String {
        return when (genreId) {
            28 -> stringResource(R.string.category_action)
            12 -> stringResource(R.string.category_adventure)
            16 -> stringResource(R.string.category_animation)
            35 -> stringResource(R.string.category_comedy)
            80 -> stringResource(R.string.category_crime)
            99 -> stringResource(R.string.category_documentaries)
            18 -> stringResource(R.string.category_drama)
            10751 -> stringResource(R.string.category_family)
            14 -> stringResource(R.string.category_fantasy)
            36 -> stringResource(R.string.category_history)
            27 -> stringResource(R.string.category_horror)
            10402 -> stringResource(R.string.category_music)
            9648 -> stringResource(R.string.category_mystery)
            10749 -> stringResource(R.string.category_romance)
            878 -> stringResource(R.string.category_scifi)
            10770 -> stringResource(R.string.category_tv_movie)
            53 -> stringResource(R.string.category_thriller)
            10752 -> stringResource(R.string.category_war)
            37 -> stringResource(R.string.category_western)
            else -> "Desconhecido" // Fallback
        }
    }
}

// Extensão para verificar se um filme pertence a um gênero
fun Movie.isGenre(genreId: Int): Boolean {
    return genre_ids.contains(genreId)
}

// Extensão para obter os nomes dos gêneros de um filme (versão antiga)
@Deprecated("Use getGenreNamesComposable() in Composables instead")
fun Movie.getGenreNames(): List<String> {
    return genre_ids.mapNotNull { GenreMap.genreMap[it] }
}

// Extensão para obter os nomes dos gêneros de um filme (versão internacionalizada para Composables)
@Composable
fun Movie.getGenreNamesComposable(): List<String> {
    val localizedMap = GenreMap.getLocalizedGenreMapComposable()
    return genre_ids.mapNotNull { localizedMap[it] }
}

// Extensão para obter os nomes dos gêneros de um filme (versão internacionalizada com Context)
fun Movie.getGenreNames(context: Context): List<String> {
    val localizedMap = GenreMap.getLocalizedGenreMap(context)
    return genre_ids.mapNotNull { localizedMap[it] }
}
