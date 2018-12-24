package rs.mitwit

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import rs.mitwit.di.Injector

class LoadingActivity : AppCompatActivity(), LoadingScreenView {
    private val presenter by lazy { Injector.provideLoadingScreenPresenter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.onCreate()
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

    override fun goToLoginScreen() {
        startActivity(Intent(this, LoginActivity::class.java))
        ActivityCompat.finishAffinity(this)
    }

    override fun goToTimelineScreen() {
        startActivity(Intent(this, TimelineActivity::class.java))
        ActivityCompat.finishAffinity(this)
    }
}
