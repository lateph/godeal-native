package com.example.primapay

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.view.View
import com.example.primapay.helper.PreferencesHelper

import kotlinx.android.synthetic.main.splash__screen.*

class Splash_Screen : AppCompatActivity() {

    private var mDelayHandler: Handler? = null
    private val SPLASH_DELAY: Long = 1000 //3 seconds
    private var mShouldFinish = false
    internal val mRunnable: Runnable = Runnable {
        if (!isFinishing) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                val intent = Intent(applicationContext, AuthActivity::class.java)
                val options = ActivityOptions
                    .makeSceneTransitionAnimation(this, logo, "logoTransition")
                // start the new activity
                startActivity(intent, options.toBundle())
//                startActivity(intent)

                mShouldFinish = true
//                finishAfterTransition()
            }
            else{
                val intent = Intent(applicationContext, AuthActivity::class.java)
                startActivity(intent)
                mShouldFinish = true
//                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash__screen)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.allowEnterTransitionOverlap = false
            window.allowReturnTransitionOverlap = false
        }

        //Initialize the Handler
        mDelayHandler = Handler()

        //Navigate with delay
        val preferencesHelper = PreferencesHelper(this)
        if(preferencesHelper.accessToken.isNullOrBlank()){
            mDelayHandler!!.postDelayed(mRunnable, SPLASH_DELAY)
        }
        else{
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            mShouldFinish = true
        }
    }

    public override fun onDestroy() {

        if (mDelayHandler != null) {
            mDelayHandler!!.removeCallbacks(mRunnable)
        }

        super.onDestroy()
    }

    public override fun onStop() {
        super.onStop()
        if (mShouldFinish)
            finish()
    }

}
