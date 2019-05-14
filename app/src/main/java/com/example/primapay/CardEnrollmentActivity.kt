package com.example.primapay

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.ActionBar
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.findNavController
import com.idemia.wa.api.WalletAgentApi
import kotlinx.android.synthetic.main.activity_card_enrollment.toolbar

class CardEnrollmentActivity : AppCompatActivity() {
    lateinit var a:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_enrollment)
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar as ActionBar
        actionBar?.setDisplayShowTitleEnabled(false)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        super.onBackPressed()
//        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.menu_add, menu)
        return super.onCreateOptionsMenu(menu)
    }

//    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
//        R.id.action_settings -> {
////            this.getV
////            findNavController().navigate(R.id.toSelectCardType)
//            true
//        }
//        else -> {
//            // If we got here, the user's action was not recognized.
//            // Invoke the superclass to handle it.
//            super.onOptionsItemSelected(item)
//        }
//    }
}
