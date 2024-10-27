package com.example.citywok.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.citywok.ui.theme.CityWokTheme
import com.example.citywok.ui.viewmodel.Restaurants
import com.example.citywok.ui.viewmodel.RestaurantsUIState
import com.example.citywok.ui.viewmodel.RestaurantsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CityWokTheme {
                CityWokApp(
                    restaurantsViewModel = RestaurantsViewModel()
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityWokApp(restaurantsViewModel: RestaurantsViewModel) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Restaurants") }) }
    ) { innerPadding ->
        RestaurantScreen(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            uiState = restaurantsViewModel.restaurantsUIState
        )
    }
}

@Composable
fun RestaurantScreen(modifier: Modifier = Modifier, uiState: RestaurantsUIState) {
    when (uiState) {
        is RestaurantsUIState.Loading -> LoadingScreen(modifier = modifier)
        is RestaurantsUIState.Success -> RestaurantList(modifier = modifier, restaurants = uiState.restaurants)
        is RestaurantsUIState.Error -> ErrorScreen(modifier = modifier)
    }
}

@Composable
fun RestaurantList(modifier: Modifier = Modifier, restaurants: List<Restaurants>) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(8.dp),
    ) {
        items(restaurants) { restaurant ->
            Text(
                text = restaurant.name,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(8.dp)
            )
            Text(
                text = restaurant.rating.toString(),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(8.dp)
            )
            Text(
                text = restaurant.location.address,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(8.dp)
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
        }
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = "Loading..."
    )
}

@Composable
fun ErrorScreen(modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = "Error"
    )
}
