package rs.mitwit.persistence

import rs.mitwit.models.*

interface UserLoginRepository {
    fun getLoginState(): UserLoginState
    fun getUserToken(): Token?
    fun logoutUser()
    fun loginUser(userToken: UserToken)
}

