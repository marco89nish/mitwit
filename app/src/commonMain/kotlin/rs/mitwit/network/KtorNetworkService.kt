package rs.mitwit.network

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType
import rs.mitwit.models.*

class KtorNetworkService(val hostUrl: String = "192.168.0.11", val hostPort: Int = 80, private val client: HttpClient) :
    UserLoginService {

    override suspend fun signupUser(request: UserSignupRequest) = client.post<SignupResultWrapper> {
        url { host = hostUrl; port = hostPort; encodedPath = "/signup" }
        contentType(ContentType.Application.Json)
        body = request
    }

    override suspend fun logoutUser(token: Token) = client.get<Unit> {
        url { host = hostUrl; port = hostPort; encodedPath = "/logout" }
        headers {
            "token" to token
        }
    }

    override suspend fun loginUser(request: UserLoginRequest) = client.post<LoginRequestResult> {
        url { /*protocol = URLProtocol.HTTPS;*/ host = hostUrl; port = hostPort; encodedPath = "/login" }
        contentType(ContentType.Application.Json)
        body = request

    }

}