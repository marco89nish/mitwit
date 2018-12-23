package rs.mitwit.models

inline class UserId(val id: String)
inline class Token(val token: String)
inline class Time(val epochTime: Long)

sealed class UserLoginState
class UserLoggedIn(val user: UserId) : UserLoginState()
class UserLoggedOut(val user: UserId) : UserLoginState()
object UserNeverLoggedIn : UserLoginState()

data class UserToken(val userId: UserId, val token: Token, val expiry: Time)

data class UserLoginRequest(val username: String, val password: String)
data class LoginRequestResult(val success: Boolean, val token: UserToken?)

data class UserInfo(val username: String, val password: String, val email: String, val userId: UserId)
data class UserSignupRequest(val username: String, val email: String, val password: String)

sealed class SignupResult
class SignupResultSuccess(val token: UserToken) : SignupResult()
class SignupResultUsernameTaken : SignupResult()
class SignupResultPasswordInvalid : SignupResult()

/** Helper class for passing sealed classes though JSON serializers */
data class SignupResultWrapper(
    val resultSuccess: SignupResultSuccess? = null,
    val resultUsernameTaken: SignupResultUsernameTaken? = null,
    val resultPasswordInvalid: SignupResultPasswordInvalid? = null
) {
    fun getResult(): SignupResult = when {
        resultSuccess != null -> resultSuccess
        resultUsernameTaken != null -> resultUsernameTaken
        resultPasswordInvalid != null -> resultPasswordInvalid
        else -> throw IllegalStateException("Result wrapper is empty")
    }
}