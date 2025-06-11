package com.innosage.cmp.example.roomdemo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.withTransaction

@Database(entities = [NoteEntity::class, RemoteKey::class], version = 1)
/**
 * Abstract Room database class for the notes application.
 * This class serves as the main access point for the underlying SQLite database.
 * It defines the entities ([NoteEntity], [RemoteKey]) and the version of the database.
 *
 * The database provides access to the [NoteDao] for performing CRUD operations on notes
 * and remote keys.
 *
 * The `withTransaction2` method is a workaround for a known issue where `androidx.room.withTransaction`
 * might not be resolved directly in some multiplatform setups. It ensures that database operations
 * are performed within a transaction, guaranteeing atomicity and data integrity.
 */
abstract class NotesDatabase : RoomDatabase() {
    /**
     * Provides the Data Access Object (DAO) for notes and remote keys.
     * @return An instance of [NoteDao].
     */
    abstract fun noteDao(): NoteDao

    /**
     * Executes the given [block] within a database transaction.
     * This ensures that all operations within the block are treated as a single atomic unit.
     * If any operation within the block fails, the entire transaction is rolled back.
     *
     * @param block The suspend function containing database operations to be executed within the transaction.
     * @return The result of the [block] execution.
     */
    suspend fun <R> withTransaction2(block: suspend () -> R): R {
        return this.withTransaction(block)
    }
}
