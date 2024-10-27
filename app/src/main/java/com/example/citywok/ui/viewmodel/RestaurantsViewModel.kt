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

class RestaurantsViewModel(currentLocation: String? = null) : ViewModel() {
    var restaurantsUIState: RestaurantsUIState by mutableStateOf(RestaurantsUIState.Loading)
        private set

    init {
        val defaultLocation = currentLocation ?: "60.192059,24.945831"
        getRestaurantsList(1000, defaultLocation)
    }

    private fun getRestaurantsList(radius: Int, ll: String) {
        viewModelScope.launch {
            restaurantsUIState = RestaurantsUIState.Loading
            try {
                val restaurantsApi = RestaurantsApi.getInstance()
                val response = restaurantsApi.getRestaurants(ll, radius)

                if (response.results.isEmpty()) {
                    restaurantsUIState = RestaurantsUIState.Error
                } else {
                    restaurantsUIState = RestaurantsUIState.Success(response.results)
                }
            } catch (e: Exception) {
                Log.e("VIEWMODEL", "Error fetching restaurants: ${e.message}")
                restaurantsUIState = RestaurantsUIState.Error
            }
        }
    }

    fun searchRestaurants(radius: Int, ll: String) {
        getRestaurantsList(radius, ll)
    }
}

