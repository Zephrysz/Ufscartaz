# Internacionalização do GenreMap

## Como foi implementado

### 1. Strings Resources

Adicionamos todas as strings de gêneros nos arquivos de recursos:

**`app/src/main/res/values/strings.xml` (Português):**

```xml
<string name="category_action">Ação</string>
<string name="category_adventure">Aventura</string>
<string name="category_animation">Animação</string>
<string name="category_comedy">Comédia</string>
<string name="category_crime">Crime</string>
<string name="category_documentaries">Documentários</string>
<string name="category_drama">Drama</string>
<string name="category_family">Família</string>
<string name="category_fantasy">Fantasia</string>
<string name="category_history">História</string>
<string name="category_horror">Terror</string>
<string name="category_music">Música</string>
<string name="category_mystery">Mistério</string>
<string name="category_romance">Romance</string>
<string name="category_scifi">Ficção Científica</string>
<string name="category_tv_movie">Filme de TV</string>
<string name="category_thriller">Thriller</string>
<string name="category_war">Guerra</string>
<string name="category_western">Faroeste</string>
```

**`app/src/main/res/values-en/strings.xml` (Inglês):**

```xml
<string name="category_action">Action</string>
<string name="category_adventure">Adventure</string>
<string name="category_animation">Animation</string>
<string name="category_comedy">Comedy</string>
<string name="category_crime">Crime</string>
<string name="category_documentaries">Documentaries</string>
<string name="category_drama">Drama</string>
<string name="category_family">Family</string>
<string name="category_fantasy">Fantasy</string>
<string name="category_history">History</string>
<string name="category_horror">Horror</string>
<string name="category_music">Music</string>
<string name="category_mystery">Mystery</string>
<string name="category_romance">Romance</string>
<string name="category_scifi">Science Fiction</string>
<string name="category_tv_movie">TV Movie</string>
<string name="category_thriller">Thriller</string>
<string name="category_war">War</string>
<string name="category_western">Western</string>
```

### 2. GenreMap Atualizado

O `GenreMap` agora oferece três formas de uso:

#### Para Composables:

```kotlin
@Composable
fun MyScreen() {
    // Obter mapa completo
    val localizedGenres = GenreMap.getLocalizedGenreMapComposable()

    // Obter gênero específico
    val actionGenre = GenreMap.getGenreName(28) // "Ação" ou "Action"

    // Obter gêneros de um filme
    val movieGenres = movie.getGenreNamesComposable()
}
```

#### Para código não-Composable (com Context):

```kotlin
fun someFunction(context: Context) {
    // Obter mapa completo
    val localizedGenres = GenreMap.getLocalizedGenreMap(context)

    // Obter gêneros de um filme
    val movieGenres = movie.getGenreNames(context)
}
```

#### Versão antiga (deprecated):

```kotlin
// ❌ Não usar mais - apenas português
val oldGenres = GenreMap.genreMap
val oldMovieGenres = movie.getGenreNames()
```

### 3. Como funciona

1. **Detecção automática**: O Android detecta o idioma do sistema
2. **Seleção de arquivo**: Escolhe automaticamente `values/` (português) ou `values-en/` (inglês)
3. **stringResource()**: Retorna a string no idioma correto
4. **Fallback**: Se não encontrar tradução, usa o padrão (português)

### 4. Exemplo de uso no MovieListScreen

**Antes:**

```kotlin
val commonGenres = listOf(
    99 to "Documentários",
    35 to "Comédia",
    // ...
)
```

**Depois:**

```kotlin
val commonGenres = listOf(
    99 to stringResource(R.string.category_documentaries),
    35 to stringResource(R.string.category_comedy),
    // ...
)
```

### 5. Adicionando novos idiomas

Para adicionar espanhol, por exemplo:

1. Criar pasta `app/src/main/res/values-es/`
2. Criar arquivo `strings.xml` com traduções:

```xml
<string name="category_action">Acción</string>
<string name="category_comedy">Comedia</string>
<!-- ... -->
```

### 6. Testando

Para testar a internacionalização:

1. **No emulador/dispositivo:**

   - Configurações → Sistema → Idiomas
   - Alterar para inglês
   - Abrir o app → gêneros aparecem em inglês

2. **No Android Studio:**
   - Preview → Configuration → Locale → English
   - Ver preview em inglês

### 7. Vantagens da implementação

✅ **Automático**: Muda idioma conforme sistema
✅ **Extensível**: Fácil adicionar novos idiomas  
✅ **Compatível**: Mantém código antigo funcionando
✅ **Performático**: Não impacta performance
✅ **Padrão Android**: Segue boas práticas do Google
