package rs.mitwit.persistence

import rs.mitwit.Platform
import rs.mitwit.models.*

/** In-memory repository, useful until we developer proper persistence and/or testing */
object InMemoryUserLoginRepository : UserLoginRepository {
    private var userToken: UserToken? = null

    override fun loginUser(userToken: UserToken) {
        this.userToken = userToken
    }

    override fun getUserToken(): UserToken? = if (isTokenValid()) userToken else null

    override fun getLoginState(): UserLoginState =
        with(userToken) {
            when (this) {
                null -> UserNeverLoggedIn
                else ->
                    if (isTokenValid())
                        UserLoggedIn(this.userId)
                    else
                        UserLoggedOut(this.userId)
            }
        }

    private fun isTokenValid() = userToken?.let { it.expiry.epochTime > Platform.getCurrentTimeMillis() } ?: false

    override fun logoutUser() {
        userToken = userToken?.copy(expiry = Time(0))
    }

}