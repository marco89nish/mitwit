package rs.mitwit

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.TextView

import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.*
import rs.mitwit.di.Injector
import rs.mitwit.user.UserLoginView

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity(), UserLoginView {

    private val presenter by lazy {
        Injector.provideUserLoginPresenter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        presenter.onCreate()
        // Set up the login form.
        password.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                loginClicked()
                return@OnEditorActionListener true
            }
            false
        })

        email_sign_in_button.setOnClickListener { loginClicked() }
        sign_up_button.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

    override fun startProgress() {
        showProgress(true)
    }

    override fun stopProgress() {
        showProgress(false)
    }

    override fun setErrorBadCredentials() {
        password.error = getString(R.string.error_incorrect_password)
        password.requestFocus()
    }

    override fun setErrorNetworkFailed() {
        AlertDialog.Builder(this)
            .setMessage(R.string.error_network)
            .setCancelable(true)
            .setNeutralButton(R.string.ok) { _: DialogInterface, _: Int -> }
            .create()
            .show()
    }
    override fun setErrorUsernameNotSet() {
        username.error= getString(R.string.error_username_blank)
        username.requestFocus()
    }
    override fun setErrorPasswordNotSet() {
        password.error= getString(R.string.error_password_blank)
        password.requestFocus()     }

    override fun clearErrors() {
        // Reset errors.
        username.error = null
        password.error = null
    }

    override fun setUsername(usernameStr: String) {
        username.setText(usernameStr)
    }

    override fun gotoNextScreen() {
        startActivity(Intent(this, TimelineActivity::class.java))
        ActivityCompat.finishAffinity(this)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_configure, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.configure -> {
                showConfigDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showConfigDialog() {
        fun onConfigSet(host: String, port: Int) {
            //hack :)
            Injector.ktorNetworkService.apply {
                hostUrl = host
                hostPort = port
            }
        }

        lateinit var dialog: DialogInterface
        dialog = alert {
            title = "Configure server"
            isCancelable = true
            customView {
                linearLayout {
                    orientation = LinearLayout.VERTICAL
                    textView("Server URL:")
                    val host = editText("192.168.0.11")
                    textView("Server port:")
                    val port = editText("8080")
                    button("OK") {
                        setOnClickListener {
                            onConfigSet(host.text.toString(), Integer.parseInt(port.text.toString()))
                            dialog.dismiss()
                        }
                    }
                    padding = dip(16)
                }
            }
        }.show()
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private fun loginClicked() {
        val usernameStr = username.text.toString()
        val passwordStr = password.text.toString()

        presenter.onSignInClicked(usernameStr, passwordStr)
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

            login_form.visibility = if (show) View.GONE else View.VISIBLE
            login_form.animate()
                .setDuration(shortAnimTime)
                .alpha((if (show) 0 else 1).toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        login_form.visibility = if (show) View.GONE else View.VISIBLE
                    }
                })

            login_progress.visibility = if (show) View.VISIBLE else View.GONE
            login_progress.animate()
                .setDuration(shortAnimTime)
                .alpha((if (show) 1 else 0).toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        login_progress.visibility = if (show) View.VISIBLE else View.GONE
                    }
                })
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            login_progress.visibility = if (show) View.VISIBLE else View.GONE
            login_form.visibility = if (show) View.GONE else View.VISIBLE
        }
    }

}
