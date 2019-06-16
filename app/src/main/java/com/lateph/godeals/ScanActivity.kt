package com.lateph.godeals

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView
import android.app.Activity



class ScanActivity : AppCompatActivity() , ZXingScannerView.ResultHandler {

    private lateinit var mScannerView: ZXingScannerView
    private var isCaptured = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mScannerView = ZXingScannerView(this@ScanActivity)
        setContentView(mScannerView)
    }

    override fun handleResult(p0: Result) {
        val returnIntent = Intent()
        returnIntent.putExtra("result", p0.text)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    override fun onResume() {
        super.onResume()
        mScannerView.setResultHandler(this@ScanActivity) // Register ourselves as a handler for scan results.
        mScannerView.startCamera()
    }
}
