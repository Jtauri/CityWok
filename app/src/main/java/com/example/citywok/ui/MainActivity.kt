package com.example.citywok.ui

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.citywok.ui.theme.CityWokTheme
import com.example.citywok.ui.viewmodel.Restaurants
import com.example.citywok.ui.viewmodel.RestaurantsUIState
import com.example.citywok.ui.viewmodel.RestaurantsViewModel
import com.example.citywok.ui.viewmodel.SearchViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CityWokTheme {
                CityWokApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityWokApp() {
    val restaurantsViewModel = RestaurantsViewModel()
    val searchViewModel = SearchViewModel()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Restaurants") }) }
    ) { innerPadding ->
        RestaurantScreen(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            uiState = restaurantsViewModel.restaurantsUIState,
            searchViewModel = searchViewModel
        )
    }
}

@Composable
fun RestaurantScreen(modifier: Modifier = Modifier,
                     uiState: RestaurantsUIState,
                     searchViewModel: SearchViewModel)
{
    when (uiState) {
        is RestaurantsUIState.Loading -> LoadingScreen(modifier = modifier)
        is RestaurantsUIState.Success -> RestaurantList(modifier = modifier, restaurants = uiState.restaurants, searchViewModel = searchViewModel)
        is RestaurantsUIState.Error -> ErrorScreen(modifier = modifier)
    }
}

@Composable
fun RestaurantList(
    modifier: Modifier = Modifier,
    restaurants: List<Restaurants>,
    searchViewModel: SearchViewModel
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(8.dp),
    ) {
        item {
            SearchBar(modifier = Modifier.padding(8.dp), searchViewModel = searchViewModel)
        }
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

@Composable
fun SearchBar(modifier: Modifier = Modifier, searchViewModel: SearchViewModel) {

    val searchRadiusInput = searchViewModel.searchRadiusInput
    val currentLocation = searchViewModel.currentLocation.value

    Column(modifier = modifier) {
        Text(
            text = "Search radius:",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(8.dp)
        )
        OutlinedTextField(
            value = searchRadiusInput,
            onValueChange = { searchViewModel.changeSearchRadiusInput(it.replace(",", ".")) },
            label = { Text("Search radius") },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.padding(8.dp)
        )
        Text(
            text = "Current search radius: ${searchViewModel.getSearchRadius()} meters",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(8.dp)
        )
        LocationPermissionHandler(searchViewModel = searchViewModel)
    }
}

@Composable
fun LocationPermissionHandler(searchViewModel: SearchViewModel = viewModel()) {
    val context = LocalContext.current


    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->

        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            searchViewModel.fetchCurrentLocation(context)
        }
    }

    LaunchedEffect(Unit) {
        permissionsLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    val currentLocation = searchViewModel.currentLocation.value
    Text(
        text = "Current location: ${currentLocation?.latitude ?: "Unknown"}, ${currentLocation?.longitude ?: "Unknown"}"
    )
}
