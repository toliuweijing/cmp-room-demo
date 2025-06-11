Of course. Here is a comprehensive, step-by-step implementation guide formatted as a prompt for an AI agent. It contains all the necessary code, file paths, and explanations to build the requested demo project from scratch.

---

### **Prompt for AI Agent: Build a Compose Multiplatform Paging Demo**

**Your Role:** You are an expert Kotlin Multiplatform developer.

**Your Mission:** Implement a fully functional Compose Multiplatform project that demonstrates a clean architecture for displaying a paged list of items fetched from a network and cached in a local Room database. You will follow the "Paging from network and database" guide, using a `RemoteMediator` and item-based keys.

**Core Requirements:**
1.  **Architecture:** Strictly follow a Clean Architecture (Data, Domain, UI layers).
2.  **Platform:** Compose Multiplatform (targeting Android and iOS).
3.  **Data Stack:**
    *   **Room:** For local database caching (Single Source of Truth for the UI).
    *   **Ktor:** For a mocked network layer.
    *   **Paging 3:** For implementing pagination, including a `RemoteMediator`.
4.  **Completeness:** This guide contains all necessary code. Implement it exactly as specified. Do not omit any files or steps. Create each file in the specified directory path with the provided content.

---

### **Phase 0: Project Setup & Dependencies**

**Objective:** Configure your Gradle build files to include all necessary libraries.

**File: `composeApp/build.gradle.kts`**

```kotlin
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.kotlinxSerialization)
    id("com.google.devtools.ksp")
}

kotlin {
    // Define targets
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        // Define versions
        val coroutinesVersion = "1.8.0"
        val ktorVersion = "2.3.10"
        val roomVersion = "2.6.1"
        val pagingVersion = "3.3.0"
        val lifecycleVersion = "2.8.0"
        val navVersion = "2.7.7"
        val serializationVersion = "1.6.3"

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)

            // Paging
            implementation("androidx.paging:paging-common:$pagingVersion")

            // Room
            implementation("androidx.room:room-runtime:$roomVersion")
            implementation("androidx.room:room-ktx:$roomVersion")

            // Ktor
            implementation("io.ktor:ktor-client-core:$ktorVersion")
            implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
            implementation("io.ktor:ktor-serialization-kotlinx-json:$serializationVersion")

            // Coroutines & Lifecycle
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
            implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
            implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")
            implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycleVersion")

            // Navigation
            implementation("androidx.navigation:navigation-compose:$navVersion")
        }

        androidMain.dependencies {
            implementation(libs.androidx.appcompat)
            implementation(libs.androidx.activity.compose)
            implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
        }

        iosMain.dependencies {
            implementation("io.ktor:ktor-client-darwin:$ktorVersion")
        }
    }
}

android {
    namespace = "org.example.project"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "org.example.project"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

// Room Schema Location
room {
    schemaDirectory("$projectDir/schemas")
}

// KSP Dependencies
dependencies {
    add("kspCommonMainMetadata", "androidx.room:room-compiler:2.6.1")
    add("kspAndroid", "androidx.room:room-compiler:2.6.1")
    add("kspIosX64", "androidx.room:room-compiler:2.6.1")
    add("kspIosArm64", "androidx.room:room-compiler:2.6.1")
    add("kspIosSimulatorArm64", "androidx.room:room-compiler:2.6.1")
}
```

---

### **Phase 1: The Data Layer**

**Objective:** Implement the local database, remote API, and repository.

#### **Step 1.1: Remote Networking (Ktor)**

**File: `composeApp/src/commonMain/kotlin/org/example/project/data/remote/dto/NoteDto.kt`**
```kotlin
package org.example.project.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class NoteDto(val id: Long, val title: String, val content: String)
```

**File: `composeApp/src/commonMain/kotlin/org/example/project/data/remote/api/NoteApiService.kt`**
```kotlin
package org.example.project.data.remote.api

import kotlinx.coroutines.delay
import org.example.project.data.remote.dto.NoteDto

// This class fakes a remote API service.
class NoteApiService {
    suspend fun getNotes(page: Int, pageSize: Int): List<NoteDto> {
        println("Fetching page: $page")
        delay(1500) // Simulate network latency

        // Simulate having only 5 pages of data
        if (page > 5) {
            println("End of data reached.")
            return emptyList()
        }

        // Generate fake data for the current page
        return (1..pageSize).map { i ->
            val noteId = ((page - 1) * pageSize) + i.toLong()
            NoteDto(id = noteId, title = "Note #$noteId", content = "This is the content for note $noteId fetched from the remote API.")
        }
    }
}
```

#### **Step 1.2: Local Caching (Room)**

**File: `composeApp/src/commonMain/kotlin/org/example/project/data/local/NoteEntity.kt`**
```kotlin
package org.example.project.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val content: String
)
```

**File: `composeApp/src/commonMain/kotlin/org/example/project/data/local/RemoteKey.kt`**
```kotlin
package org.example.project.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// This entity stores the next page key for a given note item.
// It is the cornerstone of the network + database pagination strategy.
@Entity(tableName = "remote_keys")
data class RemoteKey(
    @PrimaryKey val noteId: Long,
    val prevKey: Int?,
    val nextKey: Int?
)
```

**File: `composeApp/src/commonMain/kotlin/org/example/project/data/local/NoteDao.kt`**
```kotlin
package org.example.project.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllNotes(notes: List<NoteEntity>)

    @Query("SELECT * FROM notes ORDER BY id ASC")
    fun pagingSource(): PagingSource<Int, NoteEntity>

    @Query("DELETE FROM notes")
    suspend fun clearAllNotes()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllRemoteKeys(remoteKeys: List<RemoteKey>)

    @Query("SELECT * FROM remote_keys WHERE noteId = :noteId")
    suspend fun getRemoteKeyByNoteId(noteId: Long): RemoteKey?
    
    @Query("SELECT * FROM remote_keys ORDER BY noteId DESC LIMIT 1")
    suspend fun getLastRemoteKey(): RemoteKey?

    @Query("DELETE FROM remote_keys")
    suspend fun clearAllRemoteKeys()
}
```

**File: `composeApp/src/commonMain/kotlin/org/example/project/data/local/NotesDatabase.kt`**
```kotlin
package org.example.project.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [NoteEntity::class, RemoteKey::class], version = 1)
abstract class NotesDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}
```

#### **Step 1.3: The RemoteMediator**

**File: `composeApp/src/commonMain/kotlin/org/example/project/data/paging/NoteRemoteMediator.kt`**
```kotlin
package org.example.project.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import org.example.project.data.local.NoteEntity
import org.example.project.data.local.NotesDatabase
import org.example.project.data.local.RemoteKey
import org.example.project.data.remote.api.NoteApiService
import org.example.project.data.remote.dto.NoteDto

@OptIn(ExperimentalPagingApi::class)
class NoteRemoteMediator(
    private val database: NotesDatabase,
    private val apiService: NoteApiService
) : RemoteMediator<Int, NoteEntity>() {

    private val noteDao = database.noteDao()

    override suspend fun load(loadType: LoadType, state: PagingState<Int, NoteEntity>): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastRemoteKey = noteDao.getLastRemoteKey()
                    lastRemoteKey?.nextKey ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
            }

            val response = apiService.getNotes(page = page, pageSize = state.config.pageSize)
            val endOfPaginationReached = response.isEmpty()

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    noteDao.clearAllNotes()
                    noteDao.clearAllRemoteKeys()
                }

                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1

                val remoteKeys = response.map { RemoteKey(noteId = it.id, prevKey = prevKey, nextKey = nextKey) }
                noteDao.insertAllRemoteKeys(remoteKeys)
                noteDao.insertAllNotes(response.map { it.toEntity() })
            }

            MediatorResult.Success(endOfPaginationReached)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
    
    private fun NoteDto.toEntity() = NoteEntity(id = this.id, title = this.title, content = this.content)
}
```

#### **Step 1.4: Mappers and Repository Implementation**

**File: `composeApp/src/commonMain/kotlin/org/example/project/data/Mappers.kt`**
```kotlin
package org.example.project.data

import org.example.project.data.local.NoteEntity
import org.example.project.domain.model.Note

fun NoteEntity.toDomainModel(): Note {
    return Note(id = this.id, title = this.title, content = this.content)
}
```

**File: `composeApp/src/commonMain/kotlin/org/example/project/data/repository/NoteRepositoryImpl.kt`**
```kotlin
package org.example.project.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.example.project.data.local.NotesDatabase
import org.example.project.data.paging.NoteRemoteMediator
import org.example.project.data.remote.api.NoteApiService
import org.example.project.data.toDomainModel
import org.example.project.domain.model.Note
import org.example.project.domain.repository.NoteRepository

class NoteRepositoryImpl(
    private val database: NotesDatabase,
    private val apiService: NoteApiService
) : NoteRepository {
    @OptIn(ExperimentalPagingApi::class)
    override fun getPagedNotes(): Flow<PagingData<Note>> {
        val pagingSourceFactory = { database.noteDao().pagingSource() }

        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            remoteMediator = NoteRemoteMediator(database, apiService),
            pagingSourceFactory = pagingSourceFactory
        ).flow.map { pagingData ->
            pagingData.map { it.toDomainModel() }
        }
    }
}
```

---

### **Phase 2: The Domain Layer**

**Objective:** Define the business models and repository contracts.

**File: `composeApp/src/commonMain/kotlin/org/example/project/domain/model/Note.kt`**
```kotlin
package org.example.project.domain.model

data class Note(val id: Long, val title: String, val content: String)
```

**File: `composeApp/src/commonMain/kotlin/org/example/project/domain/repository/NoteRepository.kt`**
```kotlin
package org.example.project.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.example.project.domain.model.Note

interface NoteRepository {
    fun getPagedNotes(): Flow<PagingData<Note>>
}
```

---

### **Phase 3: The Presentation (UI) Layer**

**Objective:** Create the ViewModel and the Composable UI.

**File: `composeApp/src/commonMain/kotlin/org/example/project/ui/NoteListViewModel.kt`**
```kotlin
package org.example.project.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import org.example.project.domain.model.Note
import org.example.project.domain.repository.NoteRepository

class NoteListViewModel(repository: NoteRepository) : ViewModel() {
    val notes: Flow<PagingData<Note>> = repository
        .getPagedNotes()
        .cachedIn(viewModelScope)
}
```

**File: `composeApp/src/commonMain/kotlin/org/example/project/ui/NoteListScreen.kt`**
```kotlin
package org.example.project.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import org.example.project.domain.model.Note

@Composable
fun NoteListScreen(viewModel: NoteListViewModel) {
    val lazyPagingItems = viewModel.notes.collectAsLazyPagingItems()

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                count = lazyPagingItems.itemCount,
                key = lazyPagingItems.itemKey { it.id }
            ) { index ->
                val note = lazyPagingItems[index]
                if (note != null) {
                    NoteItem(note)
                }
            }

            when (val refreshState = lazyPagingItems.loadState.refresh) {
                is LoadState.Loading -> item { FullScreenLoading() }
                is LoadState.Error -> item { ErrorItem("Error refreshing: ${refreshState.error.message}") }
                else -> {}
            }

            when (val appendState = lazyPagingItems.loadState.append) {
                is LoadState.Loading -> item { CenteredLoadingIndicator() }
                is LoadState.Error -> item { ErrorItem("Error appending: ${appendState.error.message}") }
                else -> {}
            }
        }
    }
}

@Composable
fun NoteItem(note: Note) {
    Card(elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(note.title, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Text(note.content, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun FullScreenLoading() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun CenteredLoadingIndicator() {
    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorItem(message: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.Center) {
        Text(text = message, color = MaterialTheme.colorScheme.error)
    }
}
```

### **Phase 4: App Wiring and Dependency Injection**

**Objective:** Instantiate and provide dependencies to the app.

#### **Step 4.1: The Database Driver Factory (`expect`/`actual`)**

**File: `composeApp/src/commonMain/kotlin/org/example/project/data/local/DatabaseDriverFactory.kt`**
```kotlin
package org.example.project.data.local

import androidx.room.RoomDatabase

expect class DatabaseDriverFactory {
    fun createDriver(): RoomDatabase.Builder<NotesDatabase>
}
```

**File: `composeApp/src/androidMain/kotlin/org/example/project/data/local/DatabaseDriverFactory.kt`**
```kotlin
package org.example.project.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): RoomDatabase.Builder<NotesDatabase> {
        val dbFile = context.getDatabasePath("notes.db")
        return Room.databaseBuilder<NotesDatabase>(
            context = context.applicationContext,
            name = dbFile.absolutePath
        )
    }
}
```

**File: `composeApp/src/iosMain/kotlin/org/example/project/data/local/DatabaseDriverFactory.kt`**
```kotlin
package org.example.project.data.local

import androidx.room.Room
import androidx.room.RoomDatabase
import org.example.project.data.local.instantiateImpl
import platform.Foundation.NSHomeDirectory

actual class DatabaseDriverFactory {
    actual fun createDriver(): RoomDatabase.Builder<NotesDatabase> {
        val dbFile = NSHomeDirectory() + "/notes.db"
        return Room.databaseBuilder<NotesDatabase>(
            name = dbFile,
            factory = { NotesDatabase::class.instantiateImpl() }
        )
    }
}
```

#### **Step 4.2: Main App Entry Points**

**File: `composeApp/src/commonMain/kotlin/org/example/project/App.kt`**
```kotlin
package org.example.project

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import org.example.project.data.local.DatabaseDriverFactory
import org.example.project.data.local.NotesDatabase
import org.example.project.data.remote.api.NoteApiService
import org.example.project.data.repository.NoteRepositoryImpl
import org.example.project.domain.repository.NoteRepository
import org.example.project.ui.NoteListScreen
import org.example.project.ui.NoteListViewModel

@Composable
fun App(driverFactory: DatabaseDriverFactory) {
    // Simple manual DI
    val database = remember { NotesDatabase.getDatabase(driverFactory) }
    val apiService = remember { NoteApiService() }
    val repository: NoteRepository = remember { NoteRepositoryImpl(database, apiService) }
    val viewModel = remember { NoteListViewModel(repository) }

    MaterialTheme {
        NoteListScreen(viewModel)
    }
}

// Helper to build the database once
fun NotesDatabase.Companion.getDatabase(driverFactory: DatabaseDriverFactory): NotesDatabase {
    return driverFactory.createDriver().build()
}
```

**File: `composeApp/src/androidMain/kotlin/org/example/project/MainActivity.kt`**
```kotlin
package org.example.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import org.example.project.data.local.DatabaseDriverFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App(driverFactory = DatabaseDriverFactory(applicationContext))
        }
    }
}
```

**File: `composeApp/src/iosMain/kotlin/main.kt`**
```kotlin
import androidx.compose.ui.window.ComposeUIViewController
import org.example.project.App
import org.example.project.data.local.DatabaseDriverFactory

fun MainViewController() = ComposeUIViewController { App(driverFactory = DatabaseDriverFactory()) }
```

---

### **Final Instructions for AI Agent**

1.  **Implement All Files:** Create every file exactly as specified in its designated path.
2.  **Sync Gradle:** After setting up the `build.gradle.kts` file, perform a Gradle sync to download all dependencies.
3.  **Build and Run:** Compile and run the project on both an Android emulator and an iOS simulator.
4.  **Verification:**
    *   The app should launch and display a full-screen loading indicator.
    *   After a delay, a list of 20 notes should appear.
    *   Scrolling to the bottom of the list should show a centered loading indicator, followed by the next page of notes.
    *   This should continue for 5 pages, after which scrolling to the bottom will do nothing.
    *   Closing and reopening the app should instantly show the cached notes, followed by a network fetch to refresh.

This concludes the implementation guide. Execute these steps precisely.