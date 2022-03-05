package com.sourav1.secura

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.sourav1.secura.PermissionUtils.PermissionDeniedDialog.Companion.newInstance
import com.sourav1.secura.PermissionUtils.isPermissionGranted
import com.sourav1.secura.PermissionUtils.requestPermission
import com.sourav1.secura.databinding.ActivityMapsBinding
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener,
    ActivityCompat.OnRequestPermissionsResultCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding


    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * [.onRequestPermissionsResult].
     */
    private var permissionDenied = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    companion object {
        /**
         * Request code for location permission request.
         *
         * @see .onRequestPermissionsResult
         */
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        var userLat:Double? = null
        var userLng:Double? = null
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
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
        mMap.isMyLocationEnabled = true

        mMap.setOnMyLocationButtonClickListener(this)
        mMap.setOnMyLocationClickListener(this)
    }


    @SuppressLint("MissingPermission")
    fun getMyLocation(){
        val locationManager:LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val locationProvider:String = LocationManager.NETWORK_PROVIDER

        val lastKnownLocation: Location? = locationManager.getLastKnownLocation(locationProvider)
        userLat = lastKnownLocation?.latitude
        userLng = lastKnownLocation?.longitude
//        Toast.makeText(this, "Latitude: $userLat", Toast.LENGTH_SHORT).show()
//        Toast.makeText(this, "Longitude: $userLng", Toast.LENGTH_SHORT).show()


        val geocoder: Geocoder = Geocoder(this, Locale.getDefault())

        val addresses:List<Address> = geocoder.getFromLocation(userLat!!, userLng!!, 1)
        val address = addresses[0].getAddressLine(0)
        val city = addresses[0].locality
        val state = addresses[0].adminArea
        val country = addresses[0].countryName
        val postalCode = addresses[0].postalCode
        val knownName = addresses[0].featureName

//        Toast.makeText(this, "Address: $address", Toast.LENGTH_SHORT).show()
//        Toast.makeText(this, "city: $city", Toast.LENGTH_SHORT).show()
//        Toast.makeText(this, "state: $state", Toast.LENGTH_SHORT).show()
//        Toast.makeText(this, "country: $country", Toast.LENGTH_SHORT).show()
//        Toast.makeText(this, "postalCode: $postalCode", Toast.LENGTH_SHORT).show()
//        Toast.makeText(this, "knownName: $knownName", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, SendSms::class.java)
        intent.putExtra("Address", address)
        intent.putExtra("City", city)
        intent.putExtra("State", state)
        intent.putExtra("Country", country)
        intent.putExtra("PostalCode", postalCode)
        intent.putExtra("KnownName", knownName)
        startActivity(intent)
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */

    @SuppressLint("MissingPermission")
    private fun enableMyLocation(){
        if(!::mMap.isInitialized)
            return

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
         == PackageManager.PERMISSION_GRANTED){
            mMap.isMyLocationEnabled = true
        }

        else{
            //Permission to access is missing. Show rationale and request permission
            requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                Manifest.permission.ACCESS_FINE_LOCATION, true)
        }
    }


    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(this, "My location Button Clicked..", Toast.LENGTH_SHORT).show()
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        getMyLocation()
        return false
    }

    override fun onMyLocationClick(location: Location) {
        Toast.makeText(this, "Current location is $location", Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int,permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode != LOCATION_PERMISSION_REQUEST_CODE){
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }

        if(isPermissionGranted(permissions as Array<String>, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)){
            //Enable myLocation layer if permission is granted.
            enableMyLocation()
        }

        else{
            //Permission was denied. Display an error message
            //Display the missing permission dialog when the fragment resumes.
            permissionDenied = true
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        if(permissionDenied){
            //Permission was not granted, Display error dialog.
            showMissingPermissionError()
            permissionDenied = false
        }
    }

    /**
     * Display a dialog with error message explaining that the location permission is missing.
     */
    private fun showMissingPermissionError(){
        newInstance(true).show(supportFragmentManager, "dialog")
    }

}