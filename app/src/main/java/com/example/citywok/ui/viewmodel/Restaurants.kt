package com.example.citywok.ui.viewmodel

import android.util.Log
import com.example.citywok.ApiKey
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/*
"location": {
        "address": "string",
        "address_extended": "string",
        "admin_region": "string",
        "census_block": "string",
        "country": "string",
        "cross_street": "string",
        "dma": "string",
        "formatted_address": "string",
        "locality": "string",
        "neighborhood": [
          "string"
        ],
        "po_box": "string",
        "post_town": "string",
        "postcode": "string",
        "region": "string"
      },
*/

data class LocationData(
    val address: String,
    val postcode: String,
)
/*
"results": [
    { ja täällä alkavat vasta tulokset niin tarvihen samanlaisen dataluokan:
 */
data class RestaurantsResponse(
    val results: List<Restaurants> // Only the list of restaurants
)

// Updated data class for restaurant information
data class Restaurants(
    val fsq_id: String,
    val name: String,
    val rating: Double,
    val location: LocationData
)

const val BASE_URL = "https://api.foursquare.com/v3/"
const val API_KEY = ApiKey.API_KEY

interface RestaurantsApi {
    @GET("places/search")
    suspend fun getRestaurants(
        @Query("ll") ll: String,                  // Hakupiste
        @Query("radius") radius: Int,             // Hakuetäisyys
        @Query("categories") categories: String = "13065",  // Ravintolat
        @Query("limit") limit: Int = 10,          // Max 10 ravintolaa
        @Query("fields") fields: String = "rating,location,name,fsq_id",  // Haettavat tiedot
        @Query("sort") sort: String = "rating"    // Paras ensin
    ): RestaurantsResponse

    companion object {
        private var RestaurantService: RestaurantsApi? = null

        fun getInstance(): RestaurantsApi {
            if (RestaurantService == null) {
                //Api interceptori
                val apiKeyInterceptor = Interceptor { chain ->
                    val originalRequest = chain.request()
                    val requestWithApiKey: Request = originalRequest.newBuilder()
                        .header("Authorization", API_KEY)
                        .build()
                    Log.d("API_DEBUG", "API Key: $API_KEY") // Oli vähän ongelmia apiavaimen kanssa
                    chain.proceed(requestWithApiKey)
                }

                val loggingInterceptor = HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY // Apiavaimen ongelmien debuggaamista
                }

                // OkHttpClient with the API key interceptor
                val client = OkHttpClient.Builder()
                    .addInterceptor(apiKeyInterceptor)
                    .addInterceptor(loggingInterceptor)
                    .build()

                // Retrofit instance with the custom OkHttpClient
                RestaurantService = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(RestaurantsApi::class.java)
            }
            return RestaurantService!!
        }
    }
}
