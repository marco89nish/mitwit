package rs.mitwit

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import rs.mitwit.models.*
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

object Data {
    private val usersByIdMap: MutableMap<UserId, UserInfo> = HashMap()
    private val usersByUsernameMap: MutableMap<String, UserInfo> = HashMap()
    private val tokens: MutableMap<Token, UserToken> = HashMap()
    private val userIdCounter = AtomicLong(1)

    init {
        val testUser = UserInfo("test", "a", "a@b.com", getNextFreeUserId())
        registerUser(testUser)
    }

    fun debugPrint() = println(usersByUsernameMap)
    fun checkCredentials(username: String, password: String) =
        usersByUsernameMap[username].let { it != null && it.password == password }

    fun isUsernameTaken(username: String) = usersByUsernameMap.containsKey(username)
    fun isTokenValid(token: Token) = tokens[token]?.let { it.expiry.epochTime > System.currentTimeMillis() } ?: false
    fun getNextFreeUserId() = UserId(userIdCounter.getAndIncrement().toString())
    fun getUserInfo(username: String) = usersByUsernameMap[username]
    fun invalidateToken(token: String) = tokens.remove(Token(token))
    fun loginUser(userId: UserId): UserToken {
        val token = UserToken(
            userId,
            Token(UUID.randomUUID().toString()),
            Time(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7))
        )
        tokens[token.token] = token
        return token
    }

    fun registerUser(userInfo: UserInfo): UserToken {
        usersByIdMap[userInfo.userId] = userInfo
        usersByUsernameMap[userInfo.username] = userInfo
        return loginUser(userInfo.userId)
    }

}

fun Application.main() {
    install(ContentNegotiation) {
        gson {
            // Configure Gson here
        }
    }
    install(CallLogging)
    install(DefaultHeaders)

    routing {
        loginRoute()
        signupRoute()
        logoutRoute()
    }
}

private fun Routing.logoutRoute() {
    get("/logout") {
        val token = call.request.headers["token"]
        val responseCode = if (token != null) {
            Data.invalidateToken(token)
            HttpStatusCode.OK
        } else {
            HttpStatusCode.Unauthorized
        }
        call.respond(responseCode)
    }
}

private fun Routing.signupRoute() {
    post("/signup") {
        val (username, email, password) = try {
            call.receive<UserSignupRequest>()
        } catch (t: Throwable) {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        try {
            val response = when {
                Data.isUsernameTaken(username) -> SignupResultWrapper(resultUsernameTaken = SignupResultUsernameTaken())
                password.length < 8 -> SignupResultWrapper(resultPasswordInvalid = SignupResultPasswordInvalid())
                else -> {
                    val userInfo = UserInfo(username, password, email, Data.getNextFreeUserId())
                    val token = Data.registerUser(userInfo)
                    SignupResultWrapper(resultSuccess = SignupResultSuccess(token))
                }
            }

            call.respond(response)
        } catch (t: Throwable) {
            call.respond(HttpStatusCode.InternalServerError)
        }

    }
}

private fun Routing.loginRoute() {
    post("/login") {
        val (username, password) = call.receive<UserLoginRequest>()
        val userInfo = Data.getUserInfo(username)
        val userId = userInfo?.userId

        val response = if (userId != null && Data.checkCredentials(username, password))
            LoginRequestResult(true, Data.loginUser(userId))
        else
            LoginRequestResult(false, null)

        call.respond(response)
    }
}

fun main(args: Array<String>) {
    embeddedServer(Netty, port = 8080) { main() }.start(wait = true)
}



