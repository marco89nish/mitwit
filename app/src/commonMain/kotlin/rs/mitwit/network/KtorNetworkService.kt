package rs.mitwit.network

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType
import rs.mitwit.models.*

class KtorNetworkService(var hostUrl: String = "192.168.0.11", var hostPort: Int = 80, private val client: HttpClient) :
    UserLoginService, UserPostsService {

    override suspend fun signupUser(request: UserSignupRequest) = client.post<SignupResultWrapper> {
        url { host = hostUrl; port = hostPort; encodedPath = "/signup" }
        contentType(ContentType.Application.Json)
        body = request
    }

    override suspend fun logoutUser(token: Token) = client.get<Unit> {
        url { host = hostUrl; port = hostPort; encodedPath = "/logout" }
        header("token", token.token)
    }

    override suspend fun loginUser(request: UserLoginRequest) = client.post<LoginRequestResult> {
        url { host = hostUrl; port = hostPort; encodedPath = "/login" }
        contentType(ContentType.Application.Json)
        body = request
    }


    override suspend fun getTimeline(token: Token) = client.get<Timeline> {
        url { host = hostUrl; port = hostPort; encodedPath = "/timeline" }
        header("token", token.token)
        //Logger.log("token = ${token.token}")
    }

    override suspend fun post(post: NewPost, token: Token) = client.post<Post> {
        url { host = hostUrl; port = hostPort; encodedPath = "/post" }
        header("token", token.token)
        contentType(ContentType.Application.Json)
        body = post
    }

    override suspend fun deletePost(postId: String, token: Token) = client.get<Boolean> {
        url { host = hostUrl; port = hostPort; encodedPath = "/delete_post" }
        header("token", token.token)
        header("postId", postId)
    }

}