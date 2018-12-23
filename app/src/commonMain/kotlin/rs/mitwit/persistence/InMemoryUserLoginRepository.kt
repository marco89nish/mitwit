package rs.mitwit.persistence

import rs.mitwit.Platform
import rs.mitwit.models.*

/** In-memory repository, useful until we developer proper persistence and/or testing */
object InMemoryUserLoginRepository : UserLoginRepository {
    private var loggedInUser: UserId? = null
    private var token: Token? = null
    private var validUntil: Long = 0

    override fun loginUser(userToken: UserToken) {
        loggedInUser = userToken.userId
        token = userToken.token
        validUntil = userToken.expiry.epochTime
    }

    override fun getUserToken(): Token? = if (isTokenValid()) token else null

    override fun getLoginState(): UserLoginState =
        with(loggedInUser) {
            when (this) {
                null -> UserNeverLoggedIn
                else ->
                    if (isTokenValid())
                        UserLoggedIn(this)
                    else
                        UserLoggedOut(this)
            }
        }

    private fun isTokenValid() = validUntil > Platform.getCurrentTimeMillis()

    override fun logoutUser() {
        validUntil = 0
    }

}