package rs.mitwit

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import rs.mitwit.di.Injector
import rs.mitwit.models.Timeline
import rs.mitwit.posts.TimelineView

class TimelineActivity : AppCompatActivity(), TimelineView{

    private val presenter by lazy {
        Injector.provideTimelinePresenter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline)
        presenter.onCreate()
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }


    override fun setData(timeline: Timeline) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setLoading() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun clearLoading() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun notifyDeleteFailed() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun openCreatePostUi() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun closeCreatePostUi() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun notifyPostingFailed() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
