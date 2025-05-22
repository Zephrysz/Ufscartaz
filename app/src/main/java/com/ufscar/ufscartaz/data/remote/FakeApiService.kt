package com.ufscar.ufscartaz.data.remote

import kotlinx.coroutines.delay
import java.util.UUID
import kotlin.random.Random

/**
 * Fake implementation of the API service
 */
class FakeApiService : ApiService {
    
    // In-memory storage of registered users for the fake implementation
    private val registeredUsers = mutableMapOf<String, Pair<RegisterRequest, Long>>()
    
    /**
     * Simulate a persistent API between app launches
     */
    fun preloadUser(email: String, name: String, password: String, userId: Long) {
        if (!registeredUsers.containsKey(email)) {
            val request = RegisterRequest(name, email, password)
            registeredUsers[email] = Pair(request, userId)
        }
    }
    
    override suspend fun login(request: LoginRequest): LoginResponse {
        // delay
        delay(1000)

        val user = registeredUsers[request.email]
        if (user != null && user.first.password == request.password) {
            return LoginResponse(
                userId = user.second,
                name = user.first.name,
                email = user.first.email,
                token = UUID.randomUUID().toString(),
                avatarId = 0
            )
        } else {
            throw Exception("Invalid email or password")
        }
    }
    
    override suspend fun register(request: RegisterRequest): RegisterResponse {
        delay(1000)
        
        if (registeredUsers.containsKey(request.email)) {
            throw Exception("Email already registered")
        }
        
        val userId = Random.nextLong(1000000)
        
        registeredUsers[request.email] = Pair(request, userId)
        
        return RegisterResponse(
            userId = userId,
            name = request.name,
            email = request.email,
            token = UUID.randomUUID().toString()
        )
    }
} 