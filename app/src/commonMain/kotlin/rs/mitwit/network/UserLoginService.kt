package rs.mitwit.network

import rs.mitwit.models.*

interface UserLoginService {
    suspend fun signupUser(request: UserSignupRequest): SignupResultWrapper
    suspend fun loginUser(request: UserLoginRequest): LoginRequestResult
    suspend fun logoutUser(token: Token)
}

