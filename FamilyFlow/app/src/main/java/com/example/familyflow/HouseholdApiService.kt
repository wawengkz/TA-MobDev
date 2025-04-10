package com.example.familyflow.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * API service interface for Household-related operations
 */
interface HouseholdApiService {

    /**
     * Create a new household
     * Note: Using the endpoint that matches your backend API
     *
     * @param householdRequest The household data to create
     * @return Response containing the created household
     */
    @POST("households/")  // Removed "api/" prefix
    suspend fun createHousehold(@Body householdRequest: HouseholdRequest): Response<HouseholdResponse>

    /**
     * Verify a household code
     *
     * @param codeRequest The household code to verify
     * @return Response containing the household information if the code is valid
     */
    @POST("households/verify/")  // Removed "api/" prefix
    suspend fun verifyHouseholdCode(@Body codeRequest: HouseholdCodeRequest): Response<HouseholdResponse>
}

/**
 * Request model for creating a household
 */
data class HouseholdRequest(
    val name: String,
    val adminRole: String,
    val members: List<String> = emptyList(),
    val code: String // 4-digit code
)

/**
 * Request model for household code verification
 */
data class HouseholdCodeRequest(
    val code: String // 4-digit code
)

/**
 * Response model for household data
 */
data class HouseholdResponse(
    val id: Long,
    val name: String,
    val adminRole: String,
    val members: List<String>,
    val code: String
)