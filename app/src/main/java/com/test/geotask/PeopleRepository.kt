package com.test.geotask

import android.location.Location
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface AppLocationManager {
    suspend fun onLocationReceived(longitude: Float, latitude: Float)
}


class PeopleRepository : AppLocationManager {

    var shouldRequestData = true

    private var targetLongitude: Float? = null
    private var targetLatitude: Float? = null

    private var targetId: Int = 0

    val listOfPeopleFlow: Flow<List<Person>> =
        flow {
            while (true) {
                if (targetLatitude != null && targetLongitude != null && shouldRequestData) {
                    var targetPerson = personList.find { it.id == targetId }!!
                    println("target = $targetPerson")
                    if (targetPerson.id == 0){
                        targetPerson.latitude = targetLatitude!!
                        targetPerson.longitude = targetLongitude!!
                    }
                    emit(
                        personList.onEach { person ->
                            val result = floatArrayOf(0f)
                            if (person != targetPerson){
                                person.latitude = generateRandomLatitude()
                                person.longitude = generateRandomLongitude()
                                Location.distanceBetween(
                                    targetPerson.latitude.toDouble(),
                                    targetPerson.longitude.toDouble(),
                                    person.latitude.toDouble(),
                                    person.longitude.toDouble(),
                                    result
                                )
                                person.distance = "%.${0}f".format(result[0]) + " m"
                            } else {
                                targetPerson.distance = "0 m"
                            }
//                            if (person.id != 0){
//                                person.latitude = generateRandomLatitude()
//                                person.longitude = generateRandomLongitude()
//                                Location.distanceBetween(
//                                    targetLatitude!!.toDouble(),
//                                    targetLongitude!!.toDouble(),
//                                    person.latitude.toDouble(),
//                                    person.longitude.toDouble(),
//                                    result
//                                )
//                            } else {
//                                Location.distanceBetween(
//                                    targetLatitude!!.toDouble(),
//                                    targetLongitude!!.toDouble(),
//                                    person.latitude.toDouble(),
//                                    person.longitude.toDouble(),
//                                    result
//                                )
//                            }
//                            person.distance = "%.${0}f".format(result[0]) + " m"
                        }
                    )
                    delay(3000)
                } else delay(100)
            }
        }

    override suspend fun onLocationReceived(longitude: Float, latitude: Float) {
        targetLatitude = latitude
        targetLongitude = longitude
    }

    fun setTargetId(id: Int = 0){
        targetId = id
    }

    val targetFlow = fun(target: Person, persons: List<Person>) = flow<List<Person>>{
        emit(
            personList.onEach { person ->
                val result = floatArrayOf(0f)
                person.latitude = generateRandomLatitude()
                person.longitude = generateRandomLongitude()
                Location.distanceBetween(
                    target.latitude.toDouble(),
                    target.longitude.toDouble(),
                    person.latitude.toDouble(),
                    person.longitude.toDouble(),
                    result
                )
                person.distance = "%.${0}f".format(result[0]) + " m"
            }
        )
    }
}