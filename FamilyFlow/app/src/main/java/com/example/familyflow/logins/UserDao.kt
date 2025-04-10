package com.example.familyflow.logins

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE username = :username)")
    suspend fun usernameExists(username: String): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE email = :email)")
    suspend fun emailExists(email: String): Boolean

    @Query("SELECT * FROM users WHERE (username = :usernameOrEmail OR email = :usernameOrEmail) AND password_hash = :passwordHash LIMIT 1")
    suspend fun authenticateUser(usernameOrEmail: String, passwordHash: String): UserEntity?

    @Transaction
    suspend fun registerNewUser(user: UserEntity): Long {
        // Check for existing username or email and throw exception if exists
        if (usernameExists(user.username)) {
            throw Exception("Username already exists")
        }
        if (emailExists(user.email)) {
            throw Exception("Email already exists")
        }

        // Insert the new user and return the inserted user's ID
        return insertUser(user)
    }

    @Query("DELETE FROM users")
    suspend fun clearAllUsers()
}
