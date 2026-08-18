# RepoScout 🚀

RepoScout is a high-performance, native Android application designed to discover, search, inspect, and bookmark trending Android repositories from GitHub. Built strictly with **Clean Architecture** and **MVI / UDF (Model-View-Intent / Unidirectional Data Flow)**, it emphasizes offline persistence, resilience against API rate-limiting, edge-to-edge layout design, and a polished Material 3 experience.

---

## ✨ Features

* **Explore Trending**: Curated default feed of trending Android repositories with smooth infinite scroll pagination, pull-to-refresh, and comprehensive loading, empty, and error states.
* **Debounced Search**

  * **Keystroke Debouncing**: 400ms reactive Coroutine debounce pipeline preventing excessive GitHub API calls.
  * **Category Filters**: Filter dynamically by language (`Kotlin`, `Java`) or topic (`Compose`).
  * **Preserved State**: Existing results remain visible with a linear progress indicator while new queries load instead of blanking the screen.
* **Deep Repository Details**: 2×2 metrics grid showing Stars ⭐, Forks 🍴, Watchers 👁️, and Open Issues ❗, along with language pills, license badges, formatted relative dates, and native Android Share Sheet integration.
* **In-App Browser**: "Open on GitHub" launches GitHub using Chrome Custom Tabs with a fallback to the external browser.
* **Offline-First Storage**: Bookmark repositories to a local **Room** database with real-time UI synchronization and an **Offline ✓** indicator.
* **Offline Details Fallback**: View full details, statistics, and metadata of bookmarked repositories even when disconnected from the network.
* **Dynamic Material 3 Theming**: Switch between **System Default**, **Light**, and **Dark** modes through an interactive TopAppBar dropdown menu.
* **Edge-to-Edge UI**: Proper status-bar padding and window-inset handling to prevent layout collisions with system navigation bars.

---

## 🛠️ Technical Stack

* **UI**: Jetpack Compose + Material 3
* **Architecture**: Clean Architecture + MVI / UDF
* **Networking**: **Ktor Client 3.x** with OkHttp Engine, Content Negotiation, and typed JSON serialization
* **Serialization**: `kotlinx.serialization`
* **Dependency Injection**: **Koin 4.x**
* **Persistence**: **Room Database 2.x** with reactive Coroutine `Flow` queries
* **Image Loading**: **Coil Compose** with memory and disk caching
* **Concurrency**: Kotlin Coroutines, `StateFlow`, and reactive Flow operators
* **Navigation**: Navigation Compose 2.8+ with Type-Safe Routing using `@Serializable Route`
* **In-App Browser**: AndroidX Browser / Chrome Custom Tabs
* **Testing**: JUnit 4, MockK, Turbine, and Kotlinx Coroutines Test

---

## 🏗️ Architecture & Data Flow

RepoScout follows **Clean Architecture** combined with **MVI (Model-View-Intent)** to enforce a strict separation of concerns, testability, and deterministic state handling.

```text
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                       │
│                                                             │
│  Jetpack Compose Screens  ◄── StateFlow ──►  ViewModel     │
│                                                             │
│                         User Intent                          │
│                              │                              │
└──────────────────────────────┼──────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                       Domain Layer                          │
│                                                             │
│  UseCases: Search, Explore, Bookmark, Detail               │
│                                                             │
│  Domain Models: RepositoryItem                              │
│                                                             │
│  Repository Interfaces: GithubRepository, BookmarkRepo     │
│                                                             │
└──────────────────────────────┼──────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                        Data Layer                           │
│                                                             │
│  Ktor Remote Client (OkHttp)  ◄──►  Room SQLite Database   │
│                                                             │
│  Data Mappers: DTO ◄──► Domain ◄──► Entity                 │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### Unidirectional Data Flow (UDF / MVI)

1. **User Action / Event**
   The UI emits an immutable `Intent`, such as `SearchIntent.QueryChanged` or `ExploreIntent.ToggleBookmark`, to the ViewModel.

2. **Business Execution**
   The ViewModel triggers a focused, suspendable UseCase.

3. **Data Fetching & Mapping**
   The UseCase interacts with the Repository, which queries Ktor or Room DAO, maps DTOs/Entities to immutable Domain Models, and returns an `ApiResult<T>` or `Flow<T>`.

4. **State Emission**
   The ViewModel reduces the result into an immutable UI State using `StateFlow<UiState>`, which is observed by Jetpack Compose through `collectAsStateWithLifecycle()`.

---

# ❓ Technical Decisions & Assignment Q&A

## 1. Why did you choose this architecture?

Clean Architecture + MVI/UDF provides a strict separation of concerns.

* **Domain Layer** contains pure Kotlin models, repository interfaces, and UseCases with zero dependencies on Android frameworks, Ktor, or Room.
* This makes business logic portable and highly testable through unit tests.
* **Data Layer** coordinates remote networking using Ktor and local persistence using Room behind domain interfaces.
* **Presentation Layer** enforces Unidirectional Data Flow.
* Compose screens observe immutable `StateFlow<UiState>` and dispatch explicit Intents, reducing state inconsistencies and race conditions.

---

## 2. How does data move from the API to the UI?

The data flow is:

```text
User Action
    ↓
Compose UI
    ↓
Intent
    ↓
ViewModel
    ↓
UseCase
    ↓
Repository
    ↓
Ktor Client
    ↓
GitHub REST API
    ↓
DTO
    ↓
Domain Model
    ↓
UiState / StateFlow
    ↓
Jetpack Compose
```

For example:

1. The UI emits:

```kotlin
SearchIntent.QueryChanged("ktor")
```

2. The ViewModel receives the Intent.
3. The ViewModel calls `SearchRepositoriesUseCase`.
4. The UseCase invokes `GithubRepository`.
5. `GithubRepository` delegates the request to `GithubRemoteDataSource`.
6. `GithubRemoteDataSource` uses Ktor Client.
7. The GitHub JSON response is parsed into `SearchResponseDto`.
8. The DTO is mapped into the domain model `RepositoryItem`.
9. The result is returned as `ApiResult.Success`.
10. The ViewModel updates `SearchUiState`.
11. Compose observes the state using:

```kotlin
collectAsStateWithLifecycle()
```

---

## 3. How are API errors and network failures handled?

All network operations are wrapped using an `ApiResult<T>` sealed hierarchy.

```kotlin
sealed interface ApiResult<out T> {

    data class Success<T>(
        val data: T
    ) : ApiResult<T>

    data class Error(
        val message: String,
        val cause: Throwable? = null
    ) : ApiResult<Nothing>
}
```

### GitHub Rate Limiting

Unauthenticated GitHub API requests are limited to approximately **60 requests per hour**.

The Ktor client checks rate-limit information such as:

```text
x-ratelimit-remaining
```

When the rate limit is reached, the application displays an informative error state with retry instructions.

### Network Failures

Network failures such as:

* `IOException`
* Connection failures
* Request timeouts

are converted into UI error states.

The application displays an `ErrorStateCard` with a **Retry** button instead of clearing previously loaded content.

---

## 4. How does offline saved-repository access work?

When a user bookmarks a repository, the repository's metadata is stored locally in Room.

```text
GitHub Repository
       ↓
Domain Model
       ↓
SavedRepositoryEntity
       ↓
Room Database
```

The Saved / Bookmarks screen observes Room using a reactive `Flow`:

```kotlin
bookmarkDao.getAllSavedRepositories()
```

This means changes to bookmarks are automatically reflected in the UI.

### Offline Detail Fallback

When opening repository details:

```text
Try Network
    │
    ├── Success → Show Remote Data
    │
    └── Failure
          ↓
     Query Room
          ↓
     Show Cached Data
```

This allows bookmarked repositories to remain accessible even without an internet connection.

---

## 5. How does search debouncing work?

Search uses Kotlin Coroutines Flow operators.

```kotlin
searchTrigger
    .debounce(400L)
    .distinctUntilChanged()
    .flatMapLatest { (query, filter) ->
        searchRepositoriesUseCase(
            query = query,
            page = 1
        )
    }
```

### `debounce(400L)`

Waits 400 milliseconds after the user stops typing before triggering the search request.

For example:

```text
k
ko
kot
kotlin
     ↓
Wait 400ms
     ↓
API Request
```

This prevents unnecessary API calls for every keystroke.

### `distinctUntilChanged()`

Prevents duplicate API requests when the search query has not actually changed.

### `flatMapLatest`

Cancels the previous active search request when a new query arrives.

For example:

```text
Search: "kot"
     ↓
Request A

User types:
"kotlin"
     ↓
Request A cancelled
     ↓
Request B starts
```

This ensures the UI receives the latest search result.

---

## 6. What trade-offs did you make because of the time limit?

### Unauthenticated GitHub API

The application uses public unauthenticated GitHub API endpoints.

This avoids requiring users to:

* Generate a Personal Access Token
* Complete an OAuth login flow
* Grant additional permissions

The trade-off is the lower API rate limit.

### In-Memory Cursor Pagination

List pagination is handled through ViewModel state rather than implementing Paging 3 with `RemoteMediator`.

This approach was selected because it provides:

* Simpler implementation
* Predictable state management
* Easier offline Room synchronization
* Lower implementation complexity within the assignment timeframe

---

## 7. What would you improve with another 1–2 days?

### 1. GitHub OAuth 2.0 PKCE Login

Add secure GitHub authentication.

Benefits:

* Higher API rate limits
* Authenticated API requests
* Ability to star/unstar repositories directly
* Better personalized GitHub experience

### 2. Room FTS5

Add SQLite Full-Text Search for saved repositories.

This would provide fast offline searching across locally bookmarked repositories.

### 3. Paging 3 + RemoteMediator

Introduce Paging 3 with `RemoteMediator` to provide a more robust cache-backed pagination system.

The architecture would become:

```text
GitHub API
    ↓
RemoteMediator
    ↓
Room
    ↓
Paging 3
    ↓
Compose LazyPagingItems
```

This would make large datasets and pagination more scalable.

---

# 🧪 Automated Testing

RepoScout includes deterministic unit tests covering ViewModels, UseCases, and offline fallback mechanisms.

### Run Tests

```bash
./gradlew testDebugUnitTest
```

### Test Coverage

#### `ExploreViewModelTest`

Tests:

* Loading → Success state transitions
* Success → Error state transitions
* Bookmark stream observation
* Rate-limit error states

#### `SearchViewModelTest`

Tests:

* 400ms debounce behavior
* Search query changes
* Filter chip mutations
* Search query reset behavior

#### `GetRepositoryDetailUseCaseTest`

Tests the offline fallback:

```text
Remote API
    ↓
Failure
    ↓
Room Database
    ↓
Cached Repository
```

---

# 🚀 How to Build and Run

## Prerequisites

* Android Studio Ladybug (2024.2+) or Meerkat
* Android SDK 36
* Compile SDK 36
* Minimum SDK 26
* JDK 17+

---

## Running the App

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/RepoScoutApp.git
cd RepoScoutApp
```

### 2. Build and Install Debug Version

```bash
./gradlew installDebug
```

---

# 📦 Generate APK

Run:

```bash
./gradlew assembleDebug
```

The generated APK will be available at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

---

# 🏗️ Project Structure

```text
com.justunfold.reposcoutapp/
│
├── core/
│   ├── Network client factory
│   ├── Database setup
│   ├── Theme components
│   └── Formatters
│
├── data/
│   ├── Repository implementations
│   ├── Mappers
│   ├── Remote Data Sources
│   └── DTOs
│
├── domain/
│   ├── UseCases
│   ├── Repository interfaces
│   └── Domain models
│
├── features/
│   ├── explore/
│   │   ├── ExploreContract.kt
│   │   ├── ExploreViewModel.kt
│   │   └── ExploreScreen.kt
│   │
│   ├── search/
│   ├── detail/
│   └── bookmarks/
│
├── navigation/
│   ├── Type-safe routes
│   └── App Navigation Graph
│
└── di/
    └── Koin dependency injection modules
```

---

# 🔄 Application Flow

```text
                    ┌───────────────────┐
                    │    GitHub API     │
                    └─────────┬─────────┘
                              │
                              ▼
                    ┌───────────────────┐
                    │   Ktor Client     │
                    └─────────┬─────────┘
                              │
                              ▼
                    ┌───────────────────┐
                    │   Data Layer      │
                    │ DTO → Domain      │
                    └─────────┬─────────┘
                              │
                              ▼
                    ┌───────────────────┐
                    │   Domain Layer    │
                    │     UseCases      │
                    └─────────┬─────────┘
                              │
                              ▼
                    ┌───────────────────┐
                    │   ViewModel       │
                    │   MVI / StateFlow │
                    └─────────┬─────────┘
                              │
                              ▼
                    ┌───────────────────┐
                    │ Jetpack Compose   │
                    │       UI          │
                    └───────────────────┘
```

For bookmarked repositories:

```text
Compose UI
    ↓
Bookmark Intent
    ↓
ViewModel
    ↓
Bookmark UseCase
    ↓
Repository
    ↓
Room Database
    ↓
Flow
    ↓
ViewModel
    ↓
StateFlow
    ↓
Compose UI
```

---

# 🎨 UI & UX

RepoScout uses modern Android UI practices:

* Jetpack Compose
* Material 3
* Edge-to-edge layout
* Dynamic color support
* Light / Dark / System theme
* Responsive layouts
* Loading states
* Empty states
* Error states
* Pull-to-refresh
* Infinite scrolling
* Material 3 cards and components
* Native Android Share Sheet
* Chrome Custom Tabs

The goal is to keep the UI clean, responsive, accessible, and consistent with modern Android design principles.

---

# 🔐 Offline-First Strategy

RepoScout follows an offline-first approach for bookmarked repositories.

```text
                 ┌───────────────┐
                 │   Repository   │
                 └───────┬───────┘
                         │
               ┌─────────┴─────────┐
               │                   │
               ▼                   ▼
        ┌─────────────┐     ┌─────────────┐
        │ GitHub API  │     │    Room     │
        │   Remote    │     │    Local    │
        └─────────────┘     └─────────────┘
               │                   │
               └─────────┬─────────┘
                         ▼
                  Domain Model
                         │
                         ▼
                     UI State
                         │
                         ▼
                  Compose Screen
```

The application prioritizes fresh network data when available and falls back to locally saved repository data when the network is unavailable.

---

# 📌 Key Engineering Highlights

* Clean Architecture
* MVI / UDF
* Jetpack Compose
* Kotlin Coroutines
* StateFlow
* Flow
* Ktor Client
* Kotlin Serialization
* Room
* Koin
* Coil
* Type-Safe Navigation
* Offline-first repository details
* API rate-limit handling
* Debounced search
* Reactive database updates
* Unit testing
* Edge-to-edge UI
* Material 3

---

# 📄 License & Attribution

Built as an Android Developer Take-Home Assignment demonstrating modern Android engineering practices.

This project uses the public GitHub REST API.

GitHub API documentation:

https://docs.github.com/en/rest

---

## 👨‍💻 Author

**Deepak Yadav**

Android Developer
Kotlin • Jetpack Compose • Clean Architecture • MVI • Coroutines • Ktor • Room • Koin
