package com.test.geotask

import android.location.LocationManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PeopleViewModel(
   private val peopleRepository: PeopleRepository,
): ViewModel() {

    private val _personListFlow: MutableSharedFlow<List<PeopleItem>> = MutableSharedFlow()
    var personListFlow = _personListFlow.asSharedFlow()

    private var listOfPersons: List<PeopleItem> = emptyList()

    init {
        viewModelScope.launch {
            peopleRepository.listOfPeopleFlow.collect{
                listOfPersons = it
                if (it[0].id == 0) {
                    _personListFlow.emit(
                        it
                    )
                }
            }
        }
    }

    fun onLocationReceived(longitude: Float, latitude: Float) {
        viewModelScope.launch {
            peopleRepository.onLocationReceived(longitude, latitude)
        }
    }

}