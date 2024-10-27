package com.example.citywok.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


sealed interface RestaurantsUIState {
    data class Success(val restaurants: List<Restaurants>) : RestaurantsUIState
    object Error : RestaurantsUIState
    object Loading : RestaurantsUIState
}

class RestaurantsViewModel : ViewModel() {
    var restaurantsUIState: RestaurantsUIState by mutableStateOf<RestaurantsUIState>(RestaurantsUIState.Loading)
        private set

    init {
        getRestaurantsList()
    }

    private fun getRestaurantsList() {
        viewModelScope.launch {
            var restaurantsApi: RestaurantsApi? = null
            try {
                restaurantsApi = RestaurantsApi.getInstance()
                val response = restaurantsApi.getRestaurants("60.192059,24.945831", 1000)
                restaurantsUIState = RestaurantsUIState.Success(response.results)
            } catch (e: Exception) {
                Log.d("VIEWMODEL", e.message.toString())
                restaurantsUIState = RestaurantsUIState.Error
            }
        }
    }
}