package rs.mitwit

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import rs.mitwit.models.*

internal fun Routing.logoutRoute() {
    get("/logout") {
        val token = call.request.headers["token"]
        val responseCode = if (token != null) {
            UserData.invalidateToken(token)
            HttpStatusCode.OK
        } else {
            HttpStatusCode.Unauthorized
        }
        call.respond(responseCode)
    }
}

internal fun Routing.signupRoute() {
    post("/signup") {
        val (username, email, password) = try {
            call.receive<UserSignupRequest>()
        } catch (t: Throwable) {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        try {
            val response = when {
                UserData.isUsernameTaken(username) -> SignupResultWrapper(resultUsernameTaken = SignupResultUsernameTaken())
                password.length < 8 -> SignupResultWrapper(resultPasswordInvalid = SignupResultPasswordInvalid())
                else -> {
                    val userInfo =
                        UserInfo(username, password, email, UserData.getNextFreeUserId())
                    val token = UserData.registerUser(userInfo)
                    SignupResultWrapper(resultSuccess = SignupResultSuccess(token))
                }
            }

            call.respond(response)
        } catch (t: Throwable) {
            call.respond(HttpStatusCode.InternalServerError)
        }

    }
}

internal fun Routing.loginRoute() {
    post("/login") {
        val (username, password) = call.receive<UserLoginRequest>()
        val userInfo = UserData.getUserInfo(username)
        val userId = userInfo?.userId

        val response = if (userId != null && UserData.checkCredentials(username, password))
            LoginRequestResult(true, UserData.loginUser(userId))
        else
            LoginRequestResult(false, null)

        call.respond(response)
    }
}