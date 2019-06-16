package com.lateph.godeals

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolygonOptions

class AgentSelectArea : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var selected = mutableListOf<String>()
    private var idMap = mutableMapOf<String, String>()
    val FILL_NOT_SELECTED = Color.argb(75, 255, 0, 0)
    val FILL_SELECTED = Color.argb(75, 0, 0, 255)

    val STROKE_NOT_SELECTED = Color.RED
    val STROKE_SELECTED = Color.BLUE

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.agent_select_area, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val i = Intent()
        i.putExtra("ids", ArrayList<String>(selected))
        setResult(Activity.RESULT_OK, i)
        finish()
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("mytest", intent.getStringArrayListExtra("ids").toString())
        selected = intent.getStringArrayListExtra("ids").toMutableList()
        setContentView(R.layout.agent_select_area)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(21.4153424, 39.8197534)
        var mApp = GodealsApplication()
        val markerMap: Map<String, Object>
        Log.d("MyTest", GodealsApplication.listCities.size.toString())
        GodealsApplication.listCities.filter {
            it.pointCoordinates.isNotEmpty()
        }.forEach {
            val polygon = PolygonOptions()
                .fillColor(if (selected.indexOf(it.id) == -1) FILL_NOT_SELECTED else FILL_SELECTED)
                .strokeColor(if (selected.indexOf(it.id) == -1) STROKE_NOT_SELECTED else STROKE_SELECTED)
                .strokeWidth(5.0f)
                .clickable(true)
            val b = LatLngBounds.Builder()
            it.pointCoordinates.forEach { c ->
                val ll = LatLng(c.latitude, c.longitude)
                polygon.add(ll)
                b.include(ll)
            }
            idMap.put(mMap.addPolygon(polygon).id, it.id)
//            mMap.addMarker(MarkerOptions().title(it.name).position(b.build().center))
        }
        mMap.setOnPolygonClickListener {
            Log.d("mytest", "clicked brooo")
            val id = idMap[it.id]
            val index = selected.indexOf(id)
            // set terpilih
            if(index == -1){
                it.fillColor = FILL_SELECTED
                it.strokeColor = STROKE_SELECTED
                selected.add(id!!)
            }
            else{ // set not select
                it.fillColor = FILL_NOT_SELECTED
                it.strokeColor = STROKE_NOT_SELECTED
                selected.removeAt(index)
            }
        }
//        mMap.addPolygon(PolygonOptions().add(LatLng(20.4153424, 39.8197534), LatLng(21.4153424, 40.8197534), LatLng(23.4153424, 40.8197534)).fillColor(Color.RED).strokeColor(Color.blue(2)).strokeWidth(5.0f))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 7.0f))
    }
}
