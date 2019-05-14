package com.example.primapay

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.widget.*
import com.auth0.android.jwt.JWT
import com.example.primapay.Retrofit.INodeJS
import com.example.primapay.Retrofit.RertrofitClient
import com.example.primapay.adapter.MainMenu
import com.example.primapay.adapter.MainMenuAdapter
import com.example.primapay.helper.PreferencesHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import retrofit2.Retrofit
import android.nfc.cardemulation.CardEmulation
import android.content.ComponentName
import android.nfc.NfcAdapter
import android.support.v4.app.ActivityCompat.startActivityForResult
import android.app.Activity
import android.app.ActivityOptions
import android.media.Image
import android.os.Build
import android.transition.Explode
import android.util.Log
import kotlinx.android.synthetic.main.col_main.*
import kotlinx.android.synthetic.main.splash__screen.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var myAPI: INodeJS
    lateinit var preferencesHelper: PreferencesHelper

    private val data: ArrayList<MainMenu>
        get() {
            val cs = ArrayList<MainMenu>()
            cs.add(MainMenu("Pesan", R.drawable.home_iconmessage))
            cs.add(MainMenu("Tagihan Listrik", R.drawable.home_pln))
            cs.add(MainMenu("Tiket Kereta", R.drawable.train_ticket))
            cs.add(MainMenu("Telkomsel", R.drawable.telkomsel))
            cs.add(MainMenu("PDAM", R.drawable.pdam))
            cs.add(MainMenu("XL", R.drawable.xl))
            cs.add(MainMenu("Tiket Pesawat", R.drawable.penerbangan))
            cs.add(MainMenu("More", R.drawable.more))
            return cs
        }
    var compositeDisposable = CompositeDisposable()
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val retrofit: Retrofit = RertrofitClient.create(this@MainActivity)
        myAPI = retrofit.create(INodeJS::class.java)

        preferencesHelper = PreferencesHelper(this)
        val jwt = JWT(preferencesHelper.accessToken)
//        helloText.text = jwt.claims.toString()
        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        toolbar_title.text = toolbar.title
        supportActionBar?.setDisplayShowTitleEnabled(false)

        my_recycler_view.layoutManager = GridLayoutManager(this, 3)
        val adapter  = MainMenuAdapter(data, this)
        adapter.setOnItemClickListener(object : MainMenuAdapter.ClickListener {
            override fun onClick(it: MainMenu, aView: ImageView) {
                if (!isFinishing) {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                        val intent = Intent(applicationContext, TransaksiPulsaActivity::class.java)
                        intent.putExtra("id", it.img)
                    val options = ActivityOptions
                        .makeSceneTransitionAnimation(this@MainActivity, aView, "logoTransition")
                        // start the new activity
                    startActivity(intent, options.toBundle())
//                        startActivity(intent)
//                finishAfterTransition()
                    }
                    else{
                        val intent = Intent(applicationContext, TransaksiPulsaActivity::class.java)
                        intent.putExtra("id", it.img)
                        startActivity(intent)
//                finish()
                    }
                }
//            val intent = Intent(applicationContext, TransaksiPulsaActivity::class.java)
//            startActivity(intent)
            }
        })

        my_recycler_view.adapter = adapter
        my_recycler_view.setFocusable(false)
//        my_recycler_view.setOn
        parent_layout.requestFocus()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                val intent = Intent(applicationContext, CardEnrollmentActivity::class.java)
                startActivity(intent)
            }
//            R.id.nav_send -> {
//                val jwt = JWT(preferencesHelper.accessToken)
//
//                compositeDisposable
//                    .add(
//                        myAPI.getUser(jwt.getClaim("userId").asString().toString()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe( {
//                                message ->
////                            helloText.text = message.toString()
//                        }, {
//                                throwable ->
//                            Toast.makeText(this@MainActivity, throwable.message.toString(), Toast.LENGTH_LONG).show()
//                        })
//                    )
//            }
            R.id.logout -> {
                PreferencesHelper(this).logout()
                val intent = Intent(applicationContext, AuthActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
