package iniyan.com.smartnews

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.ViewGroup
import com.irozon.sneaker.Sneaker
import iniyan.com.smartnews.NetworkConnectivity.NetworkReceiver.Companion.getConnectivityStatusString


class SplashScreen : Activity() {


    // Splash screen timer
    private val SPLASH_TIME_OUT = 3000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)
        val status = getConnectivityStatusString(this)

        if (!status.equals("Not connected to Internet")) {


            Handler().postDelayed(


                    {


                        startActivity(Intent(this@SplashScreen, MainActivity::class.java))
                        finish()
                        overridePendingTransition(R.anim.anim_slide_out_left,
                                R.anim.leftanim)

                    }, SPLASH_TIME_OUT.toLong())


        }

    else {

            Sneaker.with(this)
                    .setTitle("Network Problem!!")
                    .setMessage("Internet Not Connected")
                    .setDuration(6000) // Time duration to show
                    .autoHide(true) // Auto hide Sneaker view
                    .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                    .sneak(R.color.colorAccent);
    }
    }
}