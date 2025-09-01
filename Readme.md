Randompedia – Architecture Decisions Record (ADR)


Overview
Randopedia is a small, pragmatic Android app built to demonstrate modern, production‑ready architecture with a focus on clarity, testability, and developer experience. The app fetches people from RandomUser API with paging, allows bookmarking users locally, and renders the UI with Jetpack Compose.

# UI
<img width="200" height="400" alt="image" src="https://github.com/user-attachments/assets/d783f9cc-38ed-4aeb-9f96-8fff8b083a59" />
<img width="200" height="400" alt="image" src="https://github.com/user-attachments/assets/d3f9c657-d0fe-4398-8079-96bfed69f49c" />
<img width="200" height="400" alt="image" src="https://github.com/user-attachments/assets/269f1ed6-37f3-493e-aa58-08ec91cc4566" />
<img width="200" height="400" alt="image" src="https://github.com/user-attachments/assets/6b04a872-d124-4d96-b1a0-857352ada6dc" />





Architectural Goals
- Maintainability: Clear boundaries, single responsibility, limited coupling.
- Testability: Deterministic domain contracts, replaceable data sources, unit tests for data and presentation.
- UX Resilience: Smooth paging, recoverable errors, and responsive UI state.
- Pragmatism: Keep the stack lean—avoid ceremony when a simpler choice works.

High‑Level Architecture
- Presentation (ui): Jetpack Compose screens and ViewModels that expose UI state as Flows/StateFlows.
- Domain (domain): Business contracts (interfaces, models, errors). No Android dependencies.
- Data (data): Repository implementation, Remote and Local data sources, mappers, and DI modules.

Dependency Direction (Clean Architecture‑inspired)
ui → domain ← data
- UI depends only on domain contracts (RandomUserRepository interface, domain models, AppError).
- Data implements domain contracts and is injected into UI via DI.
- This keeps UI free from data implementation details and simplifies testing.

Key Decisions
1) Repository pattern with domain‑first contracts
- Interface: domain/RandomUserRepository.kt
- Implementation: data/RandomUserRepositoryImpl.kt
- Rationale: The UI layer is insulated from data implementation changes (Retrofit/Room/Paging). Enables easy mocking in tests.

2) Paging with Paging 3
- data.remote.paging.PagingUsersSource implements PagingSource<Int, User> using RandomUserApi.
- data.remote.RemoteUsersDataSource exposes Pager.flow with PagingConfig(pageSize = 25).
- UI consumes Flow<PagingData<User>> and caches it in scope (cachedIn(viewModelScope)).
- Rationale: First‑class pagination with built‑in diffing, caching, and retry.

3) Retrofit + Kotlinx Serialization + OkHttp
- Retrofit configured in NetworkModule with kotlinx.serialization converter.
- OkHttp logging interceptor at BASIC level for debuggability.
- RandomUserApi narrows payload via inc and uses a seed to make pagination stable.
- Rationale: Type‑safe HTTP client, small runtime, predictable paging across sessions.

4) Room for bookmarks (local, not offline cache)
- AppDatabase + BookmarkedUserDao store only user bookmarks (id + snapshot of details as entity).
- DatabaseModule uses fallbackToDestructiveMigration(true): safe because data is a local convenience cache (bookmarks) that can be reconstructed by the user.
- BookmarksLocalDataSource exposes:
  - Flow<Set<String>> for fast lookup state in UI
  - Flow<List<User>> for a dedicated Bookmarks section
- Rationale: Reliable local persistence, reactive streams for UI.

5) Error modeling with AppError
- domain/AppError.kt defines small set of user‑facing error categories (Network, Storage, Unknown).
- Throwable.toAppError() maps platform/IO exceptions to AppError; userMessage() provides a friendly message.
- ViewModels map failures to UI events without leaking low‑level exceptions.
- Rationale: Consistent error handling/API for presentation.

6) ViewModel state and events
- UsersListViewModel combines PagingData<User> with bookmarked ids (combine + map to UserUiModel with isBookmarked flag).
- Failures from Flows are caught and surfaced via a SharedFlow of UiEvents (snackbars/toasts).
- UserDetailsViewModel accepts a serialized User from navigation args, observes bookmark status, and toggles bookmarks with error feedback.
- Rationale: Unidirectional data flow, reactive state, explicit side‑effects via events.

7) Hilt for dependency injection
- Modules: DataModule (binds repository), NetworkModule (Retrofit/OkHttp/Json), DatabaseModule (Room DB & DAO).
- @HiltViewModel for ViewModels with constructor injection of domain contracts.
- Rationale: Minimal glue code, predictable lifetimes, easier testing with module replacement.

8) Serialization across navigation
- Compose Navigation encodes a User as JSON in the route when navigating to details; decoded in UserDetailsViewModel via SavedStateHandle.
- Rationale: Keep details screen self‑sufficient without global state. For larger apps, a shared ID + repository fetch could be preferred to limit arg size.

Data Flow (Happy Path)
1. UI requests paging data from RandomUserRepository.userPagingFlow().
2. Repository delegates to RemoteUsersDataSource which provides a Pager over PagingUsersSource.
3. PagingUsersSource calls RandomUserApi.getUsers(page, results, seed, inc) and maps DTOs to domain User.
4. UsersListViewModel combines the PagingData with bookmarked ids from BookmarksLocalDataSource.
5. UI renders UserUiModel; bookmark toggles call repository add/remove which forward to BookmarksLocalDataSource.

Trade‑offs & Alternatives
- No separate use‑case layer: The app is small; the repository interface is sufficient. A use‑case layer can be introduced if business logic grows.
- Navigation payload via JSON: Simple and explicit. An alternative is route IDs + a detail repository lookup to reduce arg size.
- Destructive Room migrations: Acceptable here because data is user‑reconstructible bookmarks; for critical data, proper migrations are required.
- Logging level: BASIC for OkHttp to balance insight and privacy; adjust per build type if needed.

Testing Strategy
- Unit tests cover:
  - PagingUsersSource (data paging behavior)
  - BookmarksLocalDataSource (Room DAO contract)
  - UsersListViewModel (state combination and event emission)
- Dispatchers controlled via MainDispatcherRule to ensure deterministic coroutine tests.
- Repository is interface‑based, enabling mocking/faking in ViewModel tests.

UI/UX Notes
- Compose screens render loading lists with Paging; shimmer/placeholder utilities are included.
- Snackbars/toasts/messages are driven by UiEvent flows.
- Seeded paging ensures stable page boundaries when users refresh.

Tech Stack
- Language: Kotlin, Coroutines/Flow
- UI: Jetpack Compose, Navigation Compose
- Data: Retrofit + OkHttp, Kotlinx Serialization, Room, Paging 3
- DI: Hilt
- Testing: JUnit, coroutine test utilities

How to Run
- Open the project in Android Studio (Giraffe+ recommended).
- Build and run the app module on a device/emulator with internet access.

Module & Package Map (short)
- app/src/main/java/daniluk/randopedia
  - domain: contracts, models, AppError
  - data: repository impl, local (Room), remote (Retrofit), paging, DI modules
  - ui: compose screens, ViewModels, navigation, ui models, theme

Summary
- UI depends on domain contracts, not data details.
- Repositories are implemented in the data layer and injected via Hilt.
- Paging, Room, and Retrofit are composed behind the repository to keep presentation clean and testable.
