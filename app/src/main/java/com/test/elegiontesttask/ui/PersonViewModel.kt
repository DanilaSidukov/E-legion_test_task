package com.test.elegiontesttask.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.elegiontesttask.data.PeopleRepository
import com.test.elegiontesttask.model.Person
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UiState(
    var selectedPersonId: Int = 0,
    var peopleList: List<Person> = emptyList()
) {
    override fun equals(other: Any?): Boolean {
        return this === other
    }
}

class PeopleViewModel(
    private val peopleRepository: PeopleRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    var uiState = _uiState.asStateFlow()

    private val _newDistance = MutableStateFlow<List<Person>>(emptyList())
    var newDistance = _newDistance.asStateFlow()

    init {
        viewModelScope.launch {
            peopleRepository.listOfPeopleFlow.collect {
                _uiState.update { s ->
                    s.copy(peopleList = it)
                }
            }
        }
    }

    fun onLocationReceived(longitude: Float, latitude: Float) {
        viewModelScope.launch {
            peopleRepository.onLocationReceived(longitude, latitude)
        }
    }

    fun emitDistanceForTarget(target: Person, list: List<Person>){
        viewModelScope.launch {
            peopleRepository.targetFlow(target, list).collect{
                _newDistance.emit(
                    it
                )
            }
        }
    }

    fun selectPerson(personId: Int = _uiState.value.selectedPersonId) {
        if (personId != 0 && _uiState.value.selectedPersonId != 0 && personId == _uiState.value.selectedPersonId) {
            viewModelScope.launch {
                _uiState.update { s ->
                    s.copy(selectedPersonId = 0)
                }
            }
            peopleRepository.shouldRequestData = true
        } else if (personId != 0 && _uiState.value.selectedPersonId == 0) {
            var target = _uiState.value.peopleList.find { it.id == personId }
            target?.distance = "0 m"
            emitDistanceForTarget(target!!, _uiState.value.peopleList.filter { it.id != target.id })
            viewModelScope.launch {
                _uiState.update { s ->
                    s.copy(selectedPersonId = personId)
                }
            }
            peopleRepository.shouldRequestData = false
        }
    }

}