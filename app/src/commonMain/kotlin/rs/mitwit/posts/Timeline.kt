package rs.mitwit.posts

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rs.mitwit.arch.BasePresenter
import rs.mitwit.arch.Presenter
import rs.mitwit.models.NewPost
import rs.mitwit.models.Post
import rs.mitwit.models.Timeline

interface TimelineView {
    fun setData(timeline: Timeline)
    fun setLoading()
    fun clearLoading()
    fun notifyDeleteFailed()
    fun openCreatePostUi()
    fun closeCreatePostUi()
    fun notifyPostingFailed()
}

interface TimelinePresenter : Presenter {
    fun onPostDeleteClicked(post: Post)
    fun onAddPostClicked()
    fun onRefreshClicked()
    fun onCreatePostClicked(title: String, content: String)
}

class TimelinePresenterImpl(
    private val view: TimelineView,
    private val getTimeline: GetTimelineUsecase,
    private val deletePost: DeletePostFromTimelineUsecase,
    private val postToTimeline: PostToTimelineUsecase
) : BasePresenter(),
    TimelinePresenter {

    override fun onRefreshClicked() {
        loadData(true)
    }

    private fun loadData(forceRefresh: Boolean) {
        view.setLoading()
        launch {
            try {
                val timeline = withContext(Dispatchers.Default) {
                    getTimeline(GetTimelineUsecase.Params(forceRefresh))
                }

                view.setData(timeline)
            } catch (e: Exception) {

            }
            view.clearLoading()
        }
    }

    override fun onCreate() {
        super.onCreate()
        loadData(false)
    }

    override fun onPostDeleteClicked(post: Post) {

        view.setLoading()
        launch {
            try {
                val deleted = withContext(Dispatchers.Default) {
                    deletePost(DeletePostFromTimelineUsecase.Params(post.id))
                }

                if (deleted) {
                    val timeline = withContext(Dispatchers.Default) {
                        getTimeline(GetTimelineUsecase.Params(false))
                    }
                    view.setData(timeline)
                } else {
                    view.notifyDeleteFailed()
                }
                view.clearLoading()
            } catch (e: Exception) {
                view.notifyDeleteFailed()
                view.clearLoading()
            }
        }

    }

    override fun onAddPostClicked() {
        view.openCreatePostUi()
    }

    override fun onCreatePostClicked(title: String, content: String) {
        view.setLoading()
        launch {
            try {
                val posted = withContext(Dispatchers.Default) {
                    postToTimeline(NewPost(title, content))
                }

                if (posted) {
                    val timeline = withContext(Dispatchers.Default) {
                        getTimeline(GetTimelineUsecase.Params(false))
                    }
                    view.setData(timeline)
                    view.closeCreatePostUi()
                } else {
                    view.notifyPostingFailed()
                }
                view.clearLoading()
            } catch (e: Exception) {
                view.notifyPostingFailed()
                view.clearLoading()
            }
        }
    }


}