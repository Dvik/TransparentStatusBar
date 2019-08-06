package com.dvik.transparentstatusbar

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        makeStatusBarTransparent()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.content_container)) { _, insets ->
            findViewById<FloatingActionButton>(R.id.fab1).setMarginTop(insets.systemWindowInsetTop)
            findViewById<FloatingActionButton>(R.id.fab2).setMarginTop(insets.systemWindowInsetTop)
            insets.consumeSystemWindowInsets()
        }
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val markerPosition = LatLng(-6.2485422, 106.7972844)
        mMap.addMarker(MarkerOptions().position(markerPosition))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPosition, 17f))
    }
}
