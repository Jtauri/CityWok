package com.example.citywok.ui.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class SearchViewModel : ViewModel() {

    var searchRadiusInput by mutableStateOf("")
    var currentSearchRadius by mutableStateOf(1000)
        private set
    var currentLocation = mutableStateOf<Location?>(null)
        private set


    @SuppressLint("MissingPermission")
    fun fetchCurrentLocation(context: Context) {
        val fusedLocationClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                currentLocation.value = location
            }
            .addOnFailureListener {
                    //asetetaan paikaksi helsinki jos ei löydy
                    currentLocation.value = Location("").apply {
                    latitude = 60.192059
                    longitude = 24.945831
                }
            }
    }

    fun changeSearchRadiusInput(newValue: String) {
        searchRadiusInput = newValue
    }

    fun getSearchRadius(): Int {
        return searchRadiusInput.toIntOrNull() ?: 0
    }

    fun changeCurrentSearchRadius(newValue: Int) {
        currentSearchRadius = newValue
    }
}

