package rs.mitwit.arch

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

interface Presenter {
    fun onCreate()
    fun onStart()
    fun onStop()
    fun onDestroy()
}

abstract class BasePresenter : Presenter, CoroutineScope {
    protected lateinit var job: Job
        private set
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate() {
        job = Job()
    }

    override fun onStart() {
    }

    override fun onStop() {
    }

    override fun onDestroy() {
        job.cancel()
    }
}