package rs.mitwit.persistence

import rs.mitwit.Platform
import rs.mitwit.models.*
import rs.mitwit.network.UserPostsService

interface TimelineRepository {
    suspend fun getTimeline(userToken: UserToken, refresh: Boolean): Timeline
    suspend fun post(userToken: UserToken, newPost: NewPost) : Boolean
    suspend fun deletePost(userToken: UserToken, postId: String) : Boolean
}

fun Time.withinLastMillis(interval: Long) = Platform.getCurrentTimeMillis() < this.epochTime + interval

class TimelineRepositoryImpl(private val userPostsService: UserPostsService) : TimelineRepository {

    override suspend fun getTimeline(userToken: UserToken, refresh: Boolean): Timeline {
        val cached = fromCache(userToken.userId)
        return if (refresh || cached == null) {
            val newCache = getTimelineFromServer(userToken)
            Timeline(newCache.posts)
        } else {
            Timeline(cached.posts)
        }
    }

    private suspend fun getTimelineFromServer(userToken: UserToken): TimelineCache {
        val timeline = userPostsService.getTimeline(userToken.token)
        val newCache = TimelineCache(ArrayList(timeline.posts), Time(Platform.getCurrentTimeMillis()))
        cache[userToken.userId] = newCache
        return newCache
    }

    private fun fromCache(userId: UserId): TimelineCache? =
        cache[userId]?.let { if (it.isValid()) it else null }

    override suspend fun post(userToken: UserToken, newPost: NewPost) : Boolean {
        val post = userPostsService.post(newPost, userToken.token)
        fromCache(userToken.userId)?.posts?.add(0, post)
        return true
    }

    override suspend fun deletePost(userToken: UserToken, postId: String) : Boolean {
        if (userPostsService.deletePost(postId, userToken.token)) {
            fromCache(userToken.userId)?.let {
                it.posts.removeAll { post ->  post.id == postId }
            }
            return true
        }
        return false
    }

    data class TimelineCache(var posts: MutableList<Post>, val timeRefreshed: Time) {
        fun isValid() = timeRefreshed.withinLastMillis(CACHE_TIMEOUT)
    }

    private val cache = HashMap<UserId, TimelineCache>()

    companion object {
        const val CACHE_TIMEOUT = 60 * 1000L
    }

}