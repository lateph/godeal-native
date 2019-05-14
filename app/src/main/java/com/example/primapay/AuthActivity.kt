package com.example.primapay

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import com.example.primapay.Retrofit.INodeJS
import com.example.primapay.Retrofit.RertrofitClient
import com.example.primapay.helper.PreferencesHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_auth.*
import retrofit2.Retrofit


class AuthActivity : AppCompatActivity() {
    lateinit var myAPI:INodeJS
    var compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.allowEnterTransitionOverlap = false
            window.allowReturnTransitionOverlap = false
        }

        val retrofit:Retrofit = RertrofitClient.create(this@AuthActivity)
        myAPI = retrofit.create(INodeJS::class.java)
        btnLogin.setOnClickListener{
            login(emailText.text.toString(), passwodText.text.toString())
        }

        passwodText.setOnKeyListener { _, keyCode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                login(emailText.text.toString(), passwodText.text.toString())
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        progressBar.visibility = View.INVISIBLE
    }

    private fun login(email: String, password: String){
        val preferencesHelper = PreferencesHelper(this)
        progressBar.visibility = View.VISIBLE
        compositeDisposable
            .add(
                myAPI.loginUser(email, password, "local", true).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe( {
                    message ->
                        preferencesHelper.accessToken = message.accessToken
                        preferencesHelper.refreshToken = message.refreshToken

                        val intent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                }, {
                    throwable ->
                        Toast.makeText(this@AuthActivity, throwable.message.toString(), Toast.LENGTH_LONG).show()
                        progressBar.visibility = View.INVISIBLE
                })
            )
    }

    override fun onStop() {
        compositeDisposable.clear()
        super.onStop()
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }
}
