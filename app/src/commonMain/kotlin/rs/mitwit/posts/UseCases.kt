package rs.mitwit.posts

import rs.mitwit.arch.UseCase
import rs.mitwit.models.NewPost
import rs.mitwit.models.Timeline
import rs.mitwit.persistence.TimelineRepository
import rs.mitwit.persistence.UserLoginRepository

class UserNotLoggedInException : Exception()

class GetTimelineUsecase(
    private val userLoginRepository: UserLoginRepository,
    private val timelineRepository: TimelineRepository
) : UseCase<Timeline, GetTimelineUsecase.Params>() {

    override suspend fun invoke(params: Params): Timeline {
        val token = userLoginRepository.getUserToken() ?: throw UserNotLoggedInException()

        return timelineRepository.getTimeline(token, params.forceRefresh)
    }

    class Params(val forceRefresh: Boolean)
}

class PostToTimelineUsecase(
    private val userLoginRepository: UserLoginRepository,
    private val timelineRepository: TimelineRepository
) : UseCase<Boolean, NewPost>() {

    override suspend fun invoke(params: NewPost): Boolean {
        val token = userLoginRepository.getUserToken() ?: throw UserNotLoggedInException()
        return timelineRepository.post(token, newPost = params)
    }
}

class DeletePostFromTimelineUsecase(
    private val userLoginRepository: UserLoginRepository,
    private val timelineRepository: TimelineRepository
) : UseCase<Boolean, DeletePostFromTimelineUsecase.Params>() {

    override suspend fun invoke(params: Params): Boolean {
        val token = userLoginRepository.getUserToken() ?: throw UserNotLoggedInException()
        return timelineRepository.deletePost(token, params.postId)
    }

    class Params(val postId: String)
}