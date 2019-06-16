package com.lateph.godeals

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import com.lateph.godeals.retrofit.INodeJS
import com.lateph.godeals.retrofit.RertrofitClient
import com.lateph.godeals.helper.PreferencesHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_auth.*
import retrofit2.Retrofit
import android.support.v7.app.AlertDialog
import java.util.Locale
import android.widget.Spinner
import com.lateph.godeals.retrofit.model.LoginModel
import com.google.gson.GsonBuilder
import retrofit2.HttpException
import android.app.ProgressDialog




class AuthActivity : BaseActivity() {
    lateinit var myAPI:INodeJS
    var compositeDisposable = CompositeDisposable()
    var spinner: Spinner? = null
    var myLocale: Locale? = null
    var currentLanguage = "en"
    var currentLang:String? = null

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

        btnAR.setOnClickListener {
            updateLocale(Locale("ar"))
        }

        btnEN.setOnClickListener {
            updateLocale(Locale("en"))
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

    fun setLocale(localeName: String) {
        if (localeName != currentLanguage) {
            myLocale = Locale(localeName)
            val res = resources
            val dm = res.displayMetrics
            val conf = res.configuration
            conf.locale = myLocale
            res.updateConfiguration(conf, dm)
            val refresh = Intent(this, AuthActivity::class.java)
            refresh.putExtra(currentLang, localeName)
            startActivity(refresh)
            finish()
        } else {
            Toast.makeText(this, "Language already selected!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun login(email: String, password: String){
        val preferencesHelper = PreferencesHelper(this)
//        progressBar.visibility = View.VISIBLE
        val asyncDialog = ProgressDialog(this)
        asyncDialog.setMessage("Login ...")
        asyncDialog.show()
        compositeDisposable
            .add(
                myAPI.loginUser(LoginModel.Body(email, password)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe( {
                    message ->
                        preferencesHelper.accessToken = message.data.device.accessToken
                        asyncDialog.hide()
                        val intent = Intent(applicationContext, AgentDashboardActivity::class.java)
                        startActivity(intent)
                        finish()
                }, {
                    throwable ->
                        if (throwable is HttpException) {
                            val errorJsonString = throwable.response()
                                .errorBody()?.string()

                            val e = GsonBuilder().create().fromJson(errorJsonString, LoginModel.Error::class.java)
                            var s = e.message

                            if(!e.data.password.isNullOrEmpty()){
                                s = e.data.password[0]
                            }
                            if(!e.data.login.isNullOrEmpty()){
                                s = e.data.login[0]
                            }

                            AlertDialog.Builder(this)
                                .setTitle("Login Error")
                                .setMessage(s).show()
                        } else {
                            Toast.makeText(this@AuthActivity, throwable.message.toString(), Toast.LENGTH_LONG).show()
                        }
                        asyncDialog.hide()
//                        progressBar.visibility = View.INVISIBLE
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
