package com.ufscar.ufscartaz.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import androidx.compose.foundation.BorderStroke

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieListScreen(
    navController: NavHostController,
    viewModel: MovieViewModel = viewModel(),
    userName: String = "Lucas"
) {
    val movies by viewModel.movies.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    // Estados para filtrar os filmes
    var selectedGenreId by remember { mutableStateOf<Int?>(null) }
    
    // Filtra os filmes por gênero selecionado
    val filteredMovies = if (selectedGenreId != null) {
        movies.filter { it.isGenre(selectedGenreId!!) }
    } else {
        movies
    }
    
    // Listas por categoria
    val documentaries = movies.filter { it.isGenre(99) }
    val comedies = movies.filter { it.isGenre(35) }
    val dramas = movies.filter { it.isGenre(18) }
    
    // Filme em destaque (escolhe o primeiro com backdrop_path ou qualquer filme se não houver)
    val featuredMovie = movies.firstOrNull { it.backdrop_path != null } ?: movies.firstOrNull()

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    Text(
                        text = "UFSCAR",
                        color = Color.Red,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "TAZ",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_search),
                    contentDescription = "Buscar",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
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
                            text = error ?: "Erro desconhecido",
                            color = Color.Red,
                            fontSize = 16.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = { viewModel.retryLoading() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Red
                            )
                        ) {
                            Text("Tentar novamente")
                        }
                    }
                }
                movies.isEmpty() -> {
                    Text(
                        text = "Nenhum filme encontrado",
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
                        
                        // Categorias
                        item {
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp)
                            ) {
                                item {
                                    CategoryChip(
                                        title = stringResource(R.string.category_documentaries),
                                        isSelected = selectedGenreId == 99,
                                        onClick = { 
                                            selectedGenreId = if (selectedGenreId == 99) null else 99
                                        }
                                    )
                                }
                                item {
                                    CategoryChip(
                                        title = stringResource(R.string.category_comedy),
                                        isSelected = selectedGenreId == 35,
                                        onClick = { 
                                            selectedGenreId = if (selectedGenreId == 35) null else 35
                                        }
                                    )
                                }
                                item {
                                    CategoryChip(
                                        title = stringResource(R.string.category_drama),
                                        isSelected = selectedGenreId == 18,
                                        onClick = { 
                                            selectedGenreId = if (selectedGenreId == 18) null else 18
                                        }
                                    )
                                }
                            }
                        }
                        
                        // Filme em destaque
                        item {
                            if (featuredMovie != null) {
                                FeaturedMovie(movie = featuredMovie)
                            }
                        }
                        
                        // Documentários (ou filmes filtrados)
                        if (selectedGenreId != null) {
                            // Mostra apenas filmes filtrados
                            item {
                                Text(
                                    text = GenreMap.genreMap[selectedGenreId] ?: "Filmes",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                            
                            item {
                                if (filteredMovies.isEmpty()) {
                                    Text(
                                        text = "Nenhum filme encontrado nesta categoria",
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
                                        items(filteredMovies) { movie ->
                                            MovieCard(movie = movie)
                                        }
                                    }
                                }
                            }
                        } else {
                            // Mostrar todas as categorias
                            if (documentaries.isNotEmpty()) {
                                item {
                                    Text(
                                        text = stringResource(R.string.category_documentaries),
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
                                        items(documentaries) { movie ->
                                            MovieCard(movie = movie)
                                        }
                                    }
                                }
                            }
                            
                            if (comedies.isNotEmpty()) {
                                item {
                                    Text(
                                        text = stringResource(R.string.category_comedy),
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
                                        items(comedies) { movie ->
                                            MovieCard(movie = movie)
                                        }
                                    }
                                }
                            }
                            
                            if (dramas.isNotEmpty()) {
                                item {
                                    Text(
                                        text = stringResource(R.string.category_drama),
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
                                        items(dramas) { movie ->
                                            MovieCard(movie = movie)
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
fun FeaturedMovie(movie: Movie) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(360.dp)
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
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
                        text = "Filme",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun MovieCard(movie: Movie) {
    Card(
        modifier = Modifier
            .width(130.dp)
            .height(200.dp),
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

@Preview(showBackground = true)
@Composable
fun MovieListScreenPreview() {
    MovieListScreen(navController = rememberNavController())
}
