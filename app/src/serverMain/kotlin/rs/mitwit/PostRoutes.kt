package rs.mitwit

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.util.pipeline.PipelineContext
import rs.mitwit.models.*
import java.util.concurrent.atomic.AtomicLong

object PostData {
    data class MutableTimeline(val posts: MutableList<Post>)

    private val userIdToTimelineMap: MutableMap<UserId, MutableTimeline> = HashMap()
    private val postIdCounter: AtomicLong = AtomicLong(1)

    init {
        addPost(UserId("1"), NewPost("First post", "Welcome to mitwit"))
    }

    fun getTimeline(userId: UserId) = userIdToTimelineMap[userId]
    fun addPost(userId: UserId, newPost: NewPost): Post {
        var timeline = getTimeline(userId)
        if (timeline == null) {
            timeline = MutableTimeline(ArrayList())
            userIdToTimelineMap[userId] = timeline
        }
        val (title, content) = newPost
        val post =
            Post(getNextPostId(), title, content, Time(System.currentTimeMillis()))
        timeline.posts.add(0, post)
        return post
    }

    private fun getNextPostId() = postIdCounter.getAndIncrement().toString()

    fun deletePost(userId: UserId, postId: String) =
        getTimeline(userId)?.posts?.removeAll { it.id == postId } ?: false

}

fun Routing.deletePostRoute() {
    get("/delete_post") {
        val userToken = validateToken() ?: return@get

        val postId = try {
            call.request.headers["postId"]!!
        } catch (t: Throwable) {
            call.respond(HttpStatusCode.BadRequest)
            return@get
        }

        val deleted = PostData.deletePost(userToken.userId, postId)
        Logger.log("Deleted post = $deleted, postId=$postId, userId=${userToken.userId.id}")
        call.respond(deleted)
    }
}

fun Routing.postRoute() {
    post("/post") {
        val userToken = validateToken() ?: return@post

        val newPost = try {
            call.receive<NewPost>()
        } catch (t: Throwable) {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val post = PostData.addPost(userToken.userId, newPost)
        call.respond(post)
    }
}

fun Routing.getTimelineRoute() {
    get("/timeline") {
        val userToken = validateToken() ?: return@get
        val posts: List<Post> = PostData.getTimeline(userToken.userId)?.posts ?: listOf()

        call.respond(Timeline(posts))
    }
}

private suspend fun PipelineContext<Unit, ApplicationCall>.validateToken(): UserToken? {
    val token = call.request.headers["token"]
    if (token == null) {
        Logger.log("Missing auth token")
        call.respond(HttpStatusCode.Forbidden, "Missing auth token"); return null
    }
    val userToken = UserData.validateTokenAndGetUser(Token(token))
    if (userToken == null) {
        Logger.log("Invalid auth token")
        call.respond(HttpStatusCode.Forbidden, "Invalid auth token"); return null
    }
    return userToken
}