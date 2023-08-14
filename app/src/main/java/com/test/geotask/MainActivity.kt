package com.test.geotask

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
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
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.Builder.IMPLICIT_MIN_UPDATE_INTERVAL
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.test.geotask.Settings.Companion.CUSTOM_PERMISSIONS_REQUEST_BACKGROUND_LOCATION
import com.test.geotask.Settings.Companion.CUSTOM_PERMISSIONS_REQUEST_LOCATION
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
            val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
//            me.latitude = location?.latitude?.toFloat()!!
//            me.longitude = location.longitude.toFloat()
            mainLatitude = 30f
            mainLongitude = 50f
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
                        val locationManager =
                            this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                        val location =
                            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                        mainLatitude = location?.latitude?.toFloat()!!
                        mainLongitude = location.longitude.toFloat()
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
                        val locationManager =
                            this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                        val location =
                            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                        mainLatitude = location?.latitude?.toFloat()!!
                        mainLongitude = location.longitude.toFloat()
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

    override fun onItemClickListener(person: Person) {
        peopleViewModel.selectPerson(person.id)
    }

}