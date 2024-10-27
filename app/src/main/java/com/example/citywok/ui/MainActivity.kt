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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.citywok.R
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

    val searchViewModel: SearchViewModel = viewModel()
    val currentLocation = searchViewModel.currentLocation.value

    val restaurantsViewModel = remember(currentLocation) {
        RestaurantsViewModel(currentLocation = "${currentLocation?.latitude},${currentLocation?.longitude}")
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.restaurants)) }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            SearchBar(
                modifier = Modifier.padding(8.dp),
                searchViewModel = searchViewModel,
                restaurantsViewModel = restaurantsViewModel
            )

            RestaurantScreen(
                modifier = Modifier.fillMaxSize(),
                uiState = restaurantsViewModel.restaurantsUIState,
            )
        }
    }
}



@Composable
fun RestaurantScreen(
    modifier: Modifier = Modifier,
    uiState: RestaurantsUIState,
) {
    when (uiState) {
        is RestaurantsUIState.Loading -> LoadingScreen(modifier = modifier)
        is RestaurantsUIState.Success -> RestaurantList(
            modifier = modifier,
            restaurants = uiState.restaurants
        )
        is RestaurantsUIState.Error -> ErrorScreen(modifier = modifier)
    }
}


@Composable
fun RestaurantList(
    modifier: Modifier = Modifier,
    restaurants: List<Restaurants>
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(8.dp),
    ) {
        items(restaurants) { restaurant ->
            Text(
                text = restaurant.name ?: stringResource(R.string.no_name),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(8.dp)
            )
            Text(
                text = restaurant.rating.toString() ?: stringResource(R.string.no_rating),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(8.dp)
            )
            Text(
                text = restaurant.location.address ?: stringResource(R.string.no_address),
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
        text = stringResource(R.string.loading)
    )
}

@Composable
fun ErrorScreen(modifier: Modifier = Modifier, message: String = stringResource(R.string.error)) {
    Text(
        modifier = modifier,
        text = message
    )
}


@Composable
fun SearchBar(modifier: Modifier = Modifier, searchViewModel: SearchViewModel, restaurantsViewModel: RestaurantsViewModel) {

    val searchRadiusInput = searchViewModel.searchRadiusInput
    val currentLocation = searchViewModel.currentLocation.value

    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.search_radius),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(8.dp)
        )
        OutlinedTextField(
            value = searchRadiusInput,
            onValueChange = { searchViewModel.changeSearchRadiusInput(it.replace(",", ".")) },
            label = { Text(stringResource(R.string.search_radius2)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.padding(8.dp)
        )
        Text(
            text = stringResource(
                R.string.current_search_radius_meters,
                searchViewModel.currentSearchRadius
            ),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(8.dp)
        )
        Button(
            onClick = {
                val radius = searchViewModel.getSearchRadius() ?: 0
                searchViewModel.changeCurrentSearchRadius(radius)
                restaurantsViewModel.searchRestaurants(radius, "${currentLocation?.latitude},${currentLocation?.longitude}")
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Text(text = stringResource(R.string.search_restaurants))
        }
        GetLocation(searchViewModel = searchViewModel)
    }
}

@Composable
fun GetLocation(searchViewModel: SearchViewModel = viewModel()) {
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
        text = stringResource(
            R.string.current_location,
            currentLocation?.latitude ?: "Unknown",
            currentLocation?.longitude ?: "Unknown"
        )
    )
}
