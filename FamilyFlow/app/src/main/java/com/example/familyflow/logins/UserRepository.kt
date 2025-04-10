package com.example.familyflow.logins

import com.example.familyflow.api.LoginRequest
import com.example.familyflow.api.SessionManager
import com.example.familyflow.api.UserApiService
import com.example.familyflow.api.UserRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.security.MessageDigest

class UserRepository(
    private val userDao: UserDao,
    private val apiService: UserApiService
) {

    // Existing password hashing utility
    private fun hashPassword(password: String): String {
        return try {
            val hashedPassword = MessageDigest.getInstance("SHA-256")
                .digest(password.toByteArray())
                .fold("") { str, it -> str + "%02x".format(it) }

            Timber.d("Password hashed successfully")
            hashedPassword
        } catch (e: Exception) {
            Timber.e(e, "Password hashing failed")
            throw Exception("Password hashing error: ${e.message}")
        }
    }

    // Existing email validation
    private fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$")
        val isValid = email.matches(emailRegex)
        Timber.d("Email validation result: $isValid")
        return isValid
    }

    suspend fun registerUser(username: String, email: String, password: String): Result<Long> {
        return try {
            Timber.d("Starting user registration")

            // Input validation (same as before)
            if (username.length < 3) {
                Timber.w("Username too short")
                return Result.failure(Exception("Username must be at least 3 characters"))
            }

            if (!isValidEmail(email)) {
                Timber.w("Invalid email format")
                return Result.failure(Exception("Invalid email format"))
            }

            if (password.length < 6) {
                Timber.w("Password too short")
                return Result.failure(Exception("Password must be at least 6 characters"))
            }

            // Try to register with API first
            val response = withContext(Dispatchers.IO) {
                try {
                    apiService.registerUser(
                        UserRequest(
                            username = username,
                            email = email,
                            password = password
                        )
                    )
                } catch (e: Exception) {
                    Timber.e(e, "API registration failed")
                    null
                }
            }

            // If API registration successful, save to local database
            if (response != null && response.isSuccessful) {
                val userResponse = response.body()
                if (userResponse != null) {
                    // Save token
                    userResponse.token?.let { SessionManager.saveAuthToken(it) }
                    SessionManager.saveUserId(userResponse.id)

                    // Save to local database
                    val hashedPassword = hashPassword(password)
                    val newUser = UserEntity(
                        id = userResponse.id,
                        username = username,
                        email = email,
                        passwordHash = hashedPassword
                    )
                    val localId = userDao.insertUser(newUser)
                    Timber.d("User registration successful. User ID: ${userResponse.id}")
                    return Result.success(userResponse.id)
                }
            }

            // If API fails, try local registration as fallback
            Timber.d("Falling back to local registration")
            // Local checks and registration (similar to original code)
            val usernameExists = userDao.usernameExists(username)
            if (usernameExists) {
                return Result.failure(Exception("Username already exists"))
            }

            val emailExists = userDao.emailExists(email)
            if (emailExists) {
                return Result.failure(Exception("Email already exists"))
            }

            val hashedPassword = hashPassword(password)
            val newUser = UserEntity(
                username = username,
                email = email,
                passwordHash = hashedPassword
            )

            val userId = userDao.insertUser(newUser)
            Timber.d("Local user registration successful. User ID: $userId")
            Result.success(userId)

        } catch (e: Exception) {
            Timber.e(e, "Registration process failed")
            Result.failure(Exception("Registration failed: ${e.message}"))
        }
    }

    suspend fun loginUser(usernameOrEmail: String, password: String): Result<UserEntity> {
        return try {
            Timber.d("Starting user login")

            // Try API login first
            val response = withContext(Dispatchers.IO) {
                try {
                    apiService.loginUser(
                        LoginRequest(
                            usernameOrEmail = usernameOrEmail,
                            password = password
                        )
                    )
                } catch (e: Exception) {
                    Timber.e(e, "API login failed")
                    null
                }
            }

            // If API login successful
            if (response != null && response.isSuccessful) {
                val userResponse = response.body()
                if (userResponse != null) {
                    // Save token
                    userResponse.token?.let { SessionManager.saveAuthToken(it) }
                    SessionManager.saveUserId(userResponse.id)

                    // Update or insert in local database
                    val hashedPassword = hashPassword(password)
                    val user = UserEntity(
                        id = userResponse.id,
                        username = userResponse.username,
                        email = userResponse.email,
                        passwordHash = hashedPassword
                    )
                    userDao.insertUser(user)
                    return Result.success(user)
                }
            }

            // Fallback to local login
            Timber.d("Falling back to local login")
            val hashedPassword = hashPassword(password)
            val user = userDao.authenticateUser(usernameOrEmail, hashedPassword)

            if (user != null) {
                Timber.d("Local login successful")
                return Result.success(user)
            } else {
                Timber.w("Authentication failed")
                return Result.failure(Exception("Invalid username/email or password"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Login process failed")
            Result.failure(Exception("Login failed: ${e.message}"))
        }
    }
}