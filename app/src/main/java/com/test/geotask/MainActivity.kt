package com.test.geotask

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.Builder.IMPLICIT_MIN_UPDATE_INTERVAL
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.test.geotask.Settings.Companion.CUSTOM_PERMISSIONS_REQUEST_BACKGROUND_LOCATION
import com.test.geotask.Settings.Companion.CUSTOM_PERMISSIONS_REQUEST_LOCATION
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerViewPeople: RecyclerView
    private lateinit var peopleAdapter: PeopleAdapter
    private lateinit var peopleViewModel: PeopleViewModel

    private var fusedLocationProvider: FusedLocationProviderClient? = null
    private val locationRequest: LocationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        3000
    ).apply {
        setWaitForAccurateLocation(false)
        setMinUpdateIntervalMillis(IMPLICIT_MIN_UPDATE_INTERVAL)
        setMaxUpdateDelayMillis(6000)
    }.build()

    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val locationList = locationResult.locations
            if (locationList.isNotEmpty()) {
                val location = locationList.last()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkLocationPermission()

        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)

        recyclerViewPeople = findViewById(R.id.rv_list_of_people)
        recyclerViewPeople.layoutManager = LinearLayoutManager(this)

        peopleViewModel = PeopleViewModel(
            PeopleRepository()
        )

        if (
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ){
            val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            val me = personList.find { it.id == 0 }!!
            me.latitude = location?.latitude?.toFloat()!!
            me.longitude = location.longitude.toFloat()
            peopleViewModel.onLocationReceived(personList[0].longitude, personList[0].latitude)
        }

        lifecycleScope.launch {
            peopleViewModel.personListFlow.collect {
                peopleAdapter = PeopleAdapter(it)
                recyclerViewPeople.adapter = peopleAdapter
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProvider?.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    override fun onPause() {
        super.onPause()
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProvider?.removeLocationUpdates(locationCallback)
        }
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission()
        } else checkBackgroundLocation()
        // TODO: Consider calling
        //    ActivityCompat#requestPermissions
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            CUSTOM_PERMISSIONS_REQUEST_LOCATION
        )
    }

    private fun checkBackgroundLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestBackgroundLocationPermission()
        }
    }

    private fun requestBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                CUSTOM_PERMISSIONS_REQUEST_BACKGROUND_LOCATION
            )
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                CUSTOM_PERMISSIONS_REQUEST_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CUSTOM_PERMISSIONS_REQUEST_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        fusedLocationProvider?.requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            Looper.getMainLooper()
                        )
                        val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                        val me = personList.find { it.id == 0 }!!
                        me.latitude = location?.latitude?.toFloat()!!
                        me.longitude = location.longitude.toFloat()
                        println("GEO DATA = lat (${personList[0].latitude}), lon(${personList[0].longitude})")
                        checkBackgroundLocation()
                    }
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    ) {
                        Toast.makeText(
                            this,
                            "Permission denied, close application...",
                            Toast.LENGTH_SHORT
                        ).show()
                        this.finish()
                    }
                }
                return
            }

            CUSTOM_PERMISSIONS_REQUEST_BACKGROUND_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        fusedLocationProvider?.requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            Looper.getMainLooper()
                        )
                        val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                        val me = personList.find { it.id == 0 }!!
                        me.latitude = location?.latitude?.toFloat()!!
                        me.longitude = location.longitude.toFloat()
                        println("GEO DATA = lat (${personList[0].latitude}), lon(${personList[0].longitude})")
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Permission denied, close application...",
                        Toast.LENGTH_SHORT
                    ).show()
                    this.finish()
                }
                return
            }
        }
    }

}