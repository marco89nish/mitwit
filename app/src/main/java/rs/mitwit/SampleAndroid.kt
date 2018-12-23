package rs.mitwit

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import kotlinx.android.synthetic.main.activity_main2.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //hello()
        setContentView(R.layout.activity_main2)
        gotoLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        signup.setOnClickListener {
            //todo: start signup activity
        }
    }
}