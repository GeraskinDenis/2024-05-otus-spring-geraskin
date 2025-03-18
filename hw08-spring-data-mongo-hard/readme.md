# Книги
Это консольное приложение позволяет вести список Книг в разрезе Авторов и Жанров.

## Используемые технологии
* Spring Boot 3
* Spring Data
* Spring Shell
* MongoDb (миграция на Mongock)

## Команды
### Book commands
* [x] book del: delete book by ID
* [x] book find-by-title: find books by title substring
* [x] book find: find by ID (find all books)
* [x] book update: update book
* [x] book find-by-author: find books by author full name substring
* [x] book add: add a new book

### Book commentary commands
* [x] comment del-by-book: delete all book comments by book ID
* [x] comment del: delete a book comment by ID
* [x] comment update: Update a book comment
* [x] comment find: find a book comment by ID
* [x] comment count-by-book: count book comments by book ID
* [x] comment find-by-book: find all book comments by book ID
* [x] comment add: add a new book comment

### Built-In Commands
* [x] help: Display help about available commands
* [x] stacktrace: Display the full stacktrace of the last error.
* [x] clear: Clear the shell screen.
* [x] quit, exit: Exit the shell.
* [x] history: Display or save the history of previously run commands
* [x] version: Show version info
* [x] script: Read and execute commands from a file.

### Genre commands
* [x] genre add: Add a new genre
* [x] genre find: find by ID (find all)
* [x] genre del: delete a genre by ID
* [x] genre update: update a genre
