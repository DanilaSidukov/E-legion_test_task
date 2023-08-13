package com.test.geotask

import android.location.Location
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlin.random.Random

interface AppLocationManager {
    suspend fun onLocationReceived(longitude: Float, latitude: Float)
}


class PeopleRepository(): AppLocationManager {

    private val random = Random

    private val _listOfPeopleFlow: MutableSharedFlow<List<PeopleItem>> = MutableSharedFlow()
    var listOfPeopleFlow = _listOfPeopleFlow.asSharedFlow()

    var isDistanceChangeable = true

    override suspend fun onLocationReceived(longitude: Float, latitude: Float) {
        while (true) {
            val chosenPerson = personList.find { it.isPersonChosen } ?: personList[0]
            println("chosen = $chosenPerson")
            for (person in personList) {
                if (person.id == 0 && !person.isPersonChosen) {
                    val result = FloatArray(1)
                    Location.distanceBetween(
                        chosenPerson.latitude.toDouble(),
                        chosenPerson.longitude.toDouble(),
                        person.latitude.toDouble(),
                        person.longitude.toDouble(),
                        result
                    )
                    person.distance = "%.${0}f".format(result[0]) + " m"
                } else if (person.id != 0) {
                    val result = FloatArray(1)
                    person.latitude = generateRandomLatitude(random)
                    person.longitude = generateRandomLongitude(random)
                    Location.distanceBetween(
                        chosenPerson.latitude.toDouble(),
                        chosenPerson.longitude.toDouble(),
                        person.latitude.toDouble(),
                        person.longitude.toDouble(),
                        result
                    )
                    person.distance = "%.${0}f".format(result[0]) + " m"
                }
            }
            _listOfPeopleFlow.emit(personList)
//            if (chosenPerson.id != 0) isDistanceChangeable = false
//            else isDistanceChangeable = true
            delay(3000)
        }
    }

//    fun getListOfPeople(): Flow<List<PeopleItem>> = flow {
//        while (true){
//            for (person in personList){
//                if (person.id != 0){
//                    person.latitude = generateRandomLatitude(random)
//                    person.longitude = generateRandomLongitude(random)
//                }
//            }
//            emit(personList)
//            delay(3000)
//        }
//    }

}