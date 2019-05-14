package com.example.primapay

import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.transition.Explode
import android.util.Log
import android.view.Window
import com.idemia.wa.api.lifecycle.LifecycleService
import com.idemia.wa.api.wallet.WalletService
import com.idemia.wa.api.wms.ReplenishmentService

class TransaksiPulsaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaksi_pulsa)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            window.exitTransition = Explode()
            window.enterTransition = Explode()
        }
//        Log.d("Parameters", intent.extras.getInt("id").toString())
    }
}
