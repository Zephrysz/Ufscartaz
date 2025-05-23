# UFSCartaz

UFSCartaz é um aplicativo Mobile de listagem e informações de filmes. O programa foi desenvolvido na plataforma Android Stuido utilizando Kotlin com Jetapack Compose, e foi construído durante a disciplina de Desenvolvimento Móvel do Departamento de Computação da UFSCar em 2025.1.

## Características Técnicas

O aplicativo conta com a navegação entre mais de 6 telas diferentes, sistema de login e cadastro de usuários, aramazenamento local, implementação do framework Retrofit para requisições HTTP e integração com as APIs de filmes para fazer a listagem e de imagens para os avatares. \
O sistema está implementado no padrão MVVM (Model-View-ViewModel) e possui suporte a temas escuros e traduções para os idiomas Inglês e Português.

## Integrantes

Grupo 4) \
Arthur Braga da Fonseca - 811461 \
William Tsuyoshi Matsuda - 812305 \
Pedro Vinícius Guandalini Vicente - 812124 

## Execução do Aplicativo

Para que o aplicativo funcione corretamente é necessário incluir uma key do Pexels no arquivo local.properties para que as imagens dos avatares de registro funcionem corretamente. \
Para isso, basta gerar a sua chave no site oficial do Pexels (https://www.pexels.com/api/) e incluí-la conforme exemplo abaixo:
````
pexelsApiKey=SUA_KEY_DO_PEXELS
````
E em seguida basta dar o build e o run no projeto com o Android Studio normalmente.
