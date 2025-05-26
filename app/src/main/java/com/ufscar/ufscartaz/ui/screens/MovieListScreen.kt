package com.ufscar.ufscartaz.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.ufscar.ufscartaz.R
import com.ufscar.ufscartaz.data.model.GenreMap
import com.ufscar.ufscartaz.data.model.Movie
import com.ufscar.ufscartaz.data.model.isGenre
import com.ufscar.ufscartaz.data.model.getGenreNames
import com.ufscar.ufscartaz.ui.viewmodels.MovieViewModel
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import com.ufscar.ufscartaz.navigation.AppDestinations
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun MovieListScreen(
    navController: NavHostController,
    viewModel: MovieViewModel = viewModel(),
    userName: String = "Lucas"
) {
    val movies by viewModel.movies.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isSearchActive by viewModel.isSearchActive.collectAsState()
    val filteredMovies by viewModel.filteredMovies.collectAsState()
    val historyMovies by viewModel.currentUserHistoryMovies.collectAsState()
    
    // Estados para filtrar os filmes
    var selectedGenreId by remember { mutableStateOf<Int?>(null) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    
    // Gêneros comuns para exibir como chips
    val commonGenres = listOf(
        99 to stringResource(R.string.category_documentaries),
        35 to stringResource(R.string.category_comedy),
        18 to stringResource(R.string.category_drama),
        28 to stringResource(R.string.category_action),
        12 to stringResource(R.string.category_adventure),
        27 to stringResource(R.string.category_horror),
        878 to stringResource(R.string.category_scifi),
        10749 to stringResource(R.string.category_romance),
        16 to stringResource(R.string.category_animation)
    )
    
    // Filtra os filmes por gênero selecionado
    val genreFilteredMovies = if (selectedGenreId != null) {
        movies.filter { it.isGenre(selectedGenreId!!) }
    } else {
        movies
    }
    
    // Filme em destaque (escolhe o primeiro com backdrop_path ou qualquer filme se não houver)
    val featuredMovie = movies.firstOrNull { it.backdrop_path != null } ?: movies.firstOrNull()

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            Column(modifier = Modifier.background(Color.Black)) {
                // Logo e ícone de busca
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row {
                        Text(
                            text = stringResource(R.string.logo_part1),
                            color = Color.Red,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stringResource(R.string.logo_part2),
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    // Ícone de pesquisa que ativa o campo de busca
                    if (!isSearchActive) {
                        IconButton(onClick = { 
                            viewModel.toggleSearchActive()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Buscar",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
                
                // Campo de busca (visível apenas quando ativo)
                AnimatedVisibility(
                    visible = isSearchActive,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { viewModel.setSearchQuery(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester),
                            placeholder = { Text(stringResource(R.string.search_placeholder)) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF1D1D1D),
                                unfocusedContainerColor = Color(0xFF1D1D1D),
                                disabledContainerColor = Color(0xFF1D1D1D),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedPlaceholderColor = Color.Gray,
                                unfocusedPlaceholderColor = Color.Gray,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                cursorColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true,
                            leadingIcon = {
                                IconButton(onClick = { viewModel.toggleSearchActive() }) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                }
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.clearSearch() }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Limpar",
                                            tint = Color.White
                                        )
                                    }
                                }
                            },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(
                                onSearch = { keyboardController?.hide() }
                            )
                        )
                    }
                    
                    LaunchedEffect(isSearchActive) {
                        if (isSearchActive) {
                            // Pequeno atraso para garantir que a UI esteja visível antes de solicitar o foco
                            kotlinx.coroutines.delay(100)
                            focusRequester.requestFocus()
                        }
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(padding)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                error != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = error ?: stringResource(R.string.unknown_error),
                            color = Color.Red,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = { viewModel.retryLoading() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Red
                            )
                        ) {
                            Text(stringResource(R.string.try_again))
                        }
                    }
                }
                isSearchActive -> {
                    if (searchQuery.isEmpty()) {
                        // Se a busca está ativa mas não há query
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(48.dp)
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text(
                                text = stringResource(R.string.search_type_to_search),
                                color = Color.Gray,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else if (filteredMovies.isEmpty()) {
                        // Se há query mas não há resultados
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = stringResource(R.string.search_no_results, searchQuery),
                                color = Color.White,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        // Exibe os resultados da pesquisa
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            item {
                                Text(
                                    text = stringResource(R.string.search_results_for, searchQuery),
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                            
                            items(filteredMovies) { movie ->
                                SearchResultItem(
                                    movie = movie,
                                    onMovieClick = { clickedMovieId ->
                                        viewModel.recordMovieClick(clickedMovieId)
                                        navController.navigate("${AppDestinations.MOVIE_DETAIL}/$clickedMovieId")
                                    }
                                )
                            }
                            
                            // Espaçamento inferior
                            item {
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                }
                movies.isEmpty() -> {
                    Text(
                        text = stringResource(R.string.no_movies_found),
                        color = Color.White,
                        fontSize = 16.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 8.dp)
                    ) {
                        // Saudação
                        item {
                            Text(
                                text = stringResource(R.string.home_greeting, userName),
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }

                        // --- Recently Viewed Section ---
                        if (historyMovies.isNotEmpty()) {
                            item {
                                Text(
                                    text = stringResource(R.string.recently_viewed), // Add this string resource
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                            item {
                                LazyRow(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp), // Match MovieCard height
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    contentPadding = PaddingValues(horizontal = 16.dp)
                                ) {
                                    // Display MovieCards for history movies
                                    items(historyMovies, key = { it.id }) { movie ->
                                        MovieCard(
                                            movie = movie, // Use the Movie object from history
                                            onMovieClick = { clickedMovieId ->
                                                viewModel.recordMovieClick(clickedMovieId) // Record click again (optional, but ensures latest timestamp if already in history)
                                                navController.navigate("${AppDestinations.MOVIE_DETAIL}/$clickedMovieId")
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Categorias
                        item {
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp)
                            ) {
                                for ((genreId, genreName) in commonGenres) {
                                    item {
                                        CategoryChip(
                                            title = genreName,
                                            isSelected = selectedGenreId == genreId,
                                            onClick = { 
                                                selectedGenreId = if (selectedGenreId == genreId) null else genreId
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        
                        // Filme em destaque
                        item {
                            if (featuredMovie != null) {
                                FeaturedMovie(
                                    movie = featuredMovie,
                                    onMovieClick = { clickedMovieId ->
                                        viewModel.recordMovieClick(clickedMovieId)
                                        navController.navigate("${AppDestinations.MOVIE_DETAIL}/$clickedMovieId")
                                    }
                                )
                            }
                        }
                        
                        // Filmes filtrados por gênero
                        if (selectedGenreId != null) {
                            // Mostra apenas filmes filtrados
                            item {
                                Text(
                                    text = GenreMap.genreMap[selectedGenreId] ?: stringResource(R.string.movies),
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                            
                            item {
                                val filtered = viewModel.getMoviesByGenre(selectedGenreId!!)
                                if (filtered.isEmpty()) {
                                    Text(
                                        text = stringResource(R.string.no_movies_in_category),
                                        color = Color.Gray,
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                } else {
                                    LazyRow(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        contentPadding = PaddingValues(horizontal = 16.dp)
                                    ) {
                                        items(filtered) { movie ->
                                            MovieCard(
                                                movie = movie,
                                                onMovieClick = { clickedMovieId ->
                                                    viewModel.recordMovieClick(clickedMovieId)
                                                    navController.navigate("${AppDestinations.MOVIE_DETAIL}/$clickedMovieId")
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            // Todos os filmes
                            item {
                                Text(
                                    text = stringResource(R.string.all_movies),
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                            
                            item {
                                LazyRow(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    contentPadding = PaddingValues(horizontal = 16.dp)
                                ) {
                                    items(movies) { movie ->
                                        MovieCard(
                                            movie = movie,
                                            onMovieClick = { clickedMovieId ->
                                                viewModel.recordMovieClick(clickedMovieId)
                                                navController.navigate("${AppDestinations.MOVIE_DETAIL}/$clickedMovieId")
                                            }
                                        )
                                    }
                                }
                            }
                            
                            // Mostrar filmes por cada categoria
                            for ((genreId, genreName) in commonGenres) {
                                val genreMovies = viewModel.getMoviesByGenre(genreId)
                                if (genreMovies.isNotEmpty()) {
                                    item {
                                        Text(
                                            text = genreName,
                                            color = Color.White,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(16.dp)
                                        )
                                    }
                                    
                                    item {
                                        LazyRow(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(200.dp),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            contentPadding = PaddingValues(horizontal = 16.dp)
                                        ) {
                                            items(genreMovies) { movie ->
                                                MovieCard(
                                                    movie = movie,
                                                    onMovieClick = { clickedMovieId ->
                                                        viewModel.recordMovieClick(clickedMovieId)
                                                        navController.navigate("${AppDestinations.MOVIE_DETAIL}/$clickedMovieId")
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryChip(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = if (isSelected) Color.White else Color.Transparent,
        border = BorderStroke(1.dp, Color.White),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = title,
            color = if (isSelected) Color.Black else Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun FeaturedMovie(movie: Movie, onMovieClick: (Int) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(360.dp)
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onMovieClick(movie.id) }
    ) {
        AsyncImage(
            model = if (movie.backdrop_path != null) 
                "https://image.tmdb.org/t/p/w780${movie.backdrop_path}" 
            else 
                "https://image.tmdb.org/t/p/w500${movie.poster_path}",
            contentDescription = movie.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black),
                        startY = 300f
                    )
                )
        )
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Text(
                text = movie.title,
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Gêneros do filme
                val genres = movie.getGenreNames().take(3)
                
                genres.forEachIndexed { index, genre ->
                    if (index > 0) {
                        Text(
                            text = "•",
                            color = Color.Red,
                            fontSize = 14.sp
                        )
                    }
                    
                    Text(
                        text = genre,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
                
                if (genres.isEmpty()) {
                    Text(
                        text = stringResource(R.string.movies),
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun MovieCard(movie: Movie, onMovieClick: (Int) -> Unit) {
    Card(
        modifier = Modifier
            .width(130.dp)
            .height(200.dp)
            .clickable { onMovieClick(movie.id) },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        AsyncImage(
            model = "https://image.tmdb.org/t/p/w500${movie.poster_path}",
            contentDescription = movie.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
        )
    }
}

@Composable
fun SearchResultItem(movie: Movie, onMovieClick: (Int) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onMovieClick(movie.id) },
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1D1D1D)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // Poster do filme
            AsyncImage(
                model = "https://image.tmdb.org/t/p/w500${movie.poster_path}",
                contentDescription = movie.title,
                modifier = Modifier
                    .width(80.dp)
                    .height(120.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Informações do filme
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = movie.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Gêneros
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val genres = movie.getGenreNames().take(3)
                    genres.forEachIndexed { index, genre ->
                        if (index > 0) {
                            Text(
                                text = " • ",
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        }
                        Text(
                            text = genre,
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = movie.overview,
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MovieListScreenPreview() {
    MovieListScreen(navController = rememberNavController())
}
