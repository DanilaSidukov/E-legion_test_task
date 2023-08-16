package com.test.elegiontesttask.ui

import android.Manifest
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.Builder.IMPLICIT_MIN_UPDATE_INTERVAL
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.test.elegiontesttask.R
import com.test.elegiontesttask.data.PeopleRepository
import com.test.elegiontesttask.data.Settings.Companion.CUSTOM_PERMISSIONS_REQUEST_BACKGROUND_LOCATION
import com.test.elegiontesttask.data.Settings.Companion.CUSTOM_PERMISSIONS_REQUEST_LOCATION
import com.test.elegiontesttask.model.Person
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(), PersonClickListener {

    private lateinit var recyclerViewPeople: RecyclerView
    private lateinit var peopleAdapter: PeopleAdapter
    private lateinit var peopleViewModel: PeopleViewModel

    private lateinit var mainPersonView: CardView
    private lateinit var mainPersonName: TextView
    private lateinit var mainPersonDistance: TextView
    private lateinit var mainPersonAvatar: ImageView
    private var mainLatitude = 0f
    private var mainLongitude = 0f

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

        mainPersonView = findViewById(R.id.main_card_view)
        mainPersonAvatar = findViewById(R.id.main_person_avatar)
        mainPersonDistance = findViewById(R.id.main_person_distance_data)
        mainPersonName = findViewById(R.id.main_person_name)

        recyclerViewPeople = findViewById(R.id.rv_list_of_people)
        recyclerViewPeople.layoutManager = LinearLayoutManager(this)

        peopleViewModel = PeopleViewModel(
            PeopleRepository()
        )

        peopleAdapter = PeopleAdapter(emptyList(), this)
        recyclerViewPeople.adapter = peopleAdapter

        if (
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            getLocation()

            val criteria = Criteria()
            criteria.accuracy = Criteria.ACCURACY_COARSE
            criteria.powerRequirement = Criteria.POWER_LOW
            criteria.isAltitudeRequired = false
            criteria.isBearingRequired = false
            criteria.isSpeedRequired = false
            criteria.isCostAllowed = true
            criteria.horizontalAccuracy = Criteria.ACCURACY_HIGH
            criteria.verticalAccuracy = Criteria.ACCURACY_HIGH

            peopleViewModel.onLocationReceived(mainLongitude, mainLatitude)
        }

        lifecycleScope.launch {
            peopleViewModel.uiState.collect {
                peopleAdapter.updateList(it.peopleList.filter { person -> person.id != it.selectedPersonId })

                it.peopleList.find { person -> person.id == it.selectedPersonId }?.let {
                    mainPersonName.text = it.name
                    mainPersonDistance.text = it.distance
                    mainPersonAvatar.setImageResource(it.avatar)
                }
            }
        }

        lifecycleScope.launch {
            peopleViewModel.newDistance.collect{
                peopleAdapter.updateList(it)
            }
        }

        mainPersonView.setOnClickListener {
            peopleViewModel.selectPerson()
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
                        getLocation()
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
                        getLocation()
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

    fun getLocation() {
        LocationListener {
            fun onLocationChanged(location: Location) {
                val location = location
                Log.d("Location Changes", location.toString())
                mainLatitude = location.latitude.toFloat()
                mainLongitude = location.longitude.toFloat()
            }

            fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                Log.d("Status Changed", status.toString())
            }

            fun onProviderEnabled(provider: String?) {
                Log.d("Provider Enabled", provider!!)
            }

            fun onProviderDisabled(provider: String?) {
                Log.d("Provider Disabled", provider!!)
            }
        }
    }

    override fun onItemClickListener(person: Person) {
        peopleViewModel.selectPerson(person.id)
    }

}