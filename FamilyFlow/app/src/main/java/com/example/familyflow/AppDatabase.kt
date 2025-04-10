package com.example.familyflow.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.familyflow.data.converter.StringSetConverter
import com.example.familyflow.data.dao.TaskDao
import com.example.familyflow.data.entity.TaskEntity
import com.example.familyflow.logins.UserDao
import com.example.familyflow.logins.UserEntity
import timber.log.Timber

@Database(
    entities = [TaskEntity::class, UserEntity::class],
    version = 4,
    exportSchema = true
)
@TypeConverters(StringSetConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Migration from version 2 to 3
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Additional migration steps if needed (example: adding new columns)
                Timber.d("DatabaseMigration: Migrating from version 2 to 3")
            }
        }

        // Migration from version 3 to 4
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Ensure users table exists with the proper schema
                // If the table exists already, skip the creation
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS users (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        username TEXT NOT NULL UNIQUE,
                        email TEXT NOT NULL UNIQUE,
                        password_hash TEXT NOT NULL
                    )
                """)

                Timber.d("DatabaseMigration: Ensured users table exists in version 4")
            }
        }

        // Database initialization with migration handling
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "family_flow_database"
                )
                    .addMigrations(MIGRATION_2_3, MIGRATION_3_4)
                    .fallbackToDestructiveMigration() // Fallback if migration fails
                    .enableMultiInstanceInvalidation() // Support for multiple database instances
                    .build()

                INSTANCE = instance
                instance
            }
        }

        // Method to reset database (use cautiously)
        fun resetDatabase(context: Context) {
            synchronized(this) {
                INSTANCE?.let {
                    it.clearAllTables()
                    INSTANCE = null
                }
                // Recreate the database
                getDatabase(context)
            }
        }
    }
}
