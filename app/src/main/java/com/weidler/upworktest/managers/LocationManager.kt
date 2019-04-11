package com.weidlersoftware.upworktest.managers
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.location.*

class LocationManager (context: AppCompatActivity, permissionManager: PermissionManager) {

    private var fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private var locationResult: ((List<Location>) -> Unit)? = null

    private val locationRequest = LocationRequest.create()?.apply {
        interval = 10000
        fastestInterval = 5000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult?.locations?.let {
                this@LocationManager.locationResult?.invoke(it)
            }
        }
    }

    init {
        permissionManager.checkPermission(PermissionManager.LOCATION_PERMISSION_REQUEST, context) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                this@LocationManager.locationResult?.invoke(listOf(location))
            }
            val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest!!)
            val client = LocationServices.getSettingsClient(context)
            val task = client.checkLocationSettings(builder.build())
            task.addOnSuccessListener {
                startLocationUpdates()
            }
        }
    }





    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }


    /*-- open funcs --*/

    fun onLocationUpdated(listener: (List<Location>) -> Unit) {
        locationResult = listener
    }

    fun stopUpdates(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }


}