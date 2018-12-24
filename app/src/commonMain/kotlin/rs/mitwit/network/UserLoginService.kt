package rs.mitwit.network

import rs.mitwit.models.*

interface UserLoginService {
    suspend fun signupUser(request: UserSignupRequest): SignupResultWrapper
    suspend fun loginUser(request: UserLoginRequest): LoginRequestResult
    suspend fun logoutUser(token: Token)
}

interface UserPostsService {
    suspend fun getTimeline(token: Token): Timeline
    suspend fun post(post: NewPost, token: Token) : Post
    suspend fun deletePost(postId: String, token: Token) : Boolean
}