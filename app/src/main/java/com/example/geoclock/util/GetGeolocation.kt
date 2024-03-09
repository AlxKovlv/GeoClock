package com.example.geoclock.util

import android.content.Context
import android.location.Address
import android.location.Geocoder
import java.io.IOException
import java.util.*

fun getGeolocation(context: Context, latitude: Double, longitude: Double, maxResults: Int = 1): String {
    val geocoder = Geocoder(context, Locale.getDefault())
    var geolocation = "Location not found"

    try {
        val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, maxResults)?.filterNotNull() ?: emptyList()

        if (addresses.isNotEmpty()) {
            val address = addresses[0]
            val addressParts = mutableListOf<String>()

            // Add address lines if available
            for (i in 0..address.maxAddressLineIndex) {
                addressParts.add(address.getAddressLine(i))
            }

            // Concatenate address parts into a single string
            geolocation = addressParts.joinToString(separator = ", ")
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }

    return geolocation
}
